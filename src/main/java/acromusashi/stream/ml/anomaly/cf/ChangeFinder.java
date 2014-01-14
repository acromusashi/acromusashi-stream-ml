/**
* Copyright (c) Acroquest Technology Co, Ltd. All Rights Reserved.
* Please read the associated COPYRIGHTS file for more details.
*
* THE SOFTWARE IS PROVIDED BY Acroquest Technolog Co., Ltd.,
* WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
* BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDER BE LIABLE FOR ANY
* CLAIM, DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
* OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
*/
package acromusashi.stream.ml.anomaly.cf;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * 変化点検出アルゴリズム「ChangeFinder」を実装したクラス
 * 
 * @author kimura
 */
public class ChangeFinder
{
    /** logger */
    private static final Logger logger        = LoggerFactory.getLogger(ChangeFinder.class);

    /** 確率密度関数において乗算する定数値 */
    private static final double PDF_EXP_VALUE = -0.5;

    /** ログメッセージのヘッダ */
    private String              logHeader;

    /** 自己回帰モデルの次数「k」 */
    private int                 arDimensionNum;

    /** 平滑化ウィンドウサイズ「T」 */
    private int                 smoothingWindow;

    /** オンライン忘却パラメータ「r」(0~1 小さいほど過去の値に依存) */
    private double              forgetability;

    /** 過去データを蓄積するためのリスト。「新しい方が先頭」にしてデータが保存されている。 */
    private LinkedList<Double>  pastData;

    /** μ最尤推定値 */
    private double              estimatedMyu;

    /** Σ最尤推定値 */
    private double              estimatedSigma;

    /** 確率密度関数を生成するためのベース配列「C」 */
    private double[]            pdfBase;

    /** パラメータ推定に用いる係数配列「ω」 */
    private double[]            yuleWalkerAns;

    /** 結果のスコア値リスト */
    private List<Double>        scores;

    /** 2段階目の学習を行うChangeFinder */
    private ChangeFinder        secondChangeFinder;

    /**
     * 初期化パラメータ群を指定してインスタンスを生成する。
     * 
     * @param arDimensionNum 自己回帰モデルの次数
     * @param smoothingWindow 平滑化ウィンドウサイズ
     * @param forgetability 忘却パラメータ
     * @param hasSecond 2段階目の学習を行うChangeFinderを保持するか
     */
    public ChangeFinder(int arDimensionNum, int smoothingWindow, double forgetability,
            boolean hasSecond)
    {
        this.arDimensionNum = arDimensionNum;
        this.smoothingWindow = smoothingWindow;
        this.forgetability = forgetability;
        this.pastData = new LinkedList<>();
        this.pdfBase = new double[this.arDimensionNum + 1];
        this.yuleWalkerAns = new double[this.arDimensionNum + 1];
        this.scores = Lists.newArrayList();

        if (hasSecond == true)
        {
            this.secondChangeFinder = new ChangeFinder(arDimensionNum, smoothingWindow,
                    forgetability, false);
            this.logHeader = "FirstLearn:";
        }
        else
        {
            this.logHeader = "SecondLearn:";
        }
    }

    /**
     * 指定した値を用いて変化点スコアを算出する。<br>
     * 算出と同時に過去状態の更新も行う。
     * 
     * @param input 指定値
     * @return 変化点スコア
     */
    public double calculateScore(double input)
    {
        this.pastData.addFirst(input);
        int dataLength = this.pastData.size();

        // #############################################
        // ## オンライン忘却アルゴリズムSDARを用いて1段階学習を行う。
        // #############################################
        // 最尤推定値「μ」を更新する。
        this.estimatedMyu = (1 - this.forgetability) * this.estimatedMyu + this.forgetability
                * input;

        // 確率密度関数用の係数配列を更新する。
        // 算出される個数は「過去データの数」「自己回帰モデルの次数 + 1」のうち小さい方
        int pdfBaseNum = Math.min(dataLength, this.arDimensionNum + 1);
        for (int index = 0; index < pdfBaseNum; index++)
        {
            this.pdfBase[index] = (1 - this.forgetability) * this.pdfBase[index]
                    + this.forgetability * (input - this.estimatedMyu)
                    * Math.pow((this.pastData.get(index) - this.estimatedMyu), 1);
        }

        // パラメータ推定に用いる係数配列「ω」の算出を行う。
        // 確率密度関数用の係数配列のインデックスと異なり1オリジンのため配列インデックスも1から使用している。
        for (int yuleIndex = 1; yuleIndex < pdfBaseNum; yuleIndex++)
        {
            double nowPdfBase = this.pdfBase[yuleIndex];

            for (int index = 1; index < yuleIndex; index++)
            {
                nowPdfBase = nowPdfBase
                        - (this.pdfBase[yuleIndex - index] * this.yuleWalkerAns[index]);
            }

            double yuleResult = nowPdfBase / (this.pdfBase[0]);
            this.yuleWalkerAns[yuleIndex] = yuleResult;
        }

        // データの推測値を算出
        double estimatedValue = this.estimatedMyu;
        for (int index = 1; index < pdfBaseNum; index++)
        {
            double dataResult = this.pastData.get(index);
            double dist = this.yuleWalkerAns[index] * (dataResult - this.estimatedMyu);
            estimatedValue = estimatedValue + dist;
        }

        if (this.pastData.size() > this.arDimensionNum)
        {
            this.pastData.pollLast();
        }

        // Σの最尤推定値を算出
        this.estimatedSigma = (1 - this.forgetability) * this.estimatedSigma + this.forgetability
                * Math.pow((input - estimatedValue), 2);

        // 1段階学習結果スコアを算出
        double firstScore = calcFirstScore(estimatedValue, input, this.estimatedSigma,
                this.smoothingWindow);
        this.scores.add(firstScore);
        if (this.scores.size() > this.smoothingWindow)
        {
            this.scores.remove(0);
        }

        // #############################################
        // ## 平滑化を行う。
        // #############################################
        double movingAverage = smoothing(this.scores, this.smoothingWindow);

        if (logger.isDebugEnabled() == true)
        {
            String logFormat = "CalculateResult. Input={0}, Myu={1}, Sigma={2}, Estimated={3}, Score={4}, MovingAverage={5}";
            logger.debug(this.logHeader
                    + MessageFormat.format(logFormat, input, this.estimatedMyu,
                            this.estimatedSigma, estimatedValue, firstScore, movingAverage));
        }

        // #############################################
        // ## オンライン忘却アルゴリズムSDARを用いて2段階学習を行う。
        // ## フィールド「secondFinder」が存在している場合、2段階学習結果をスコアとして返す。
        // ## 存在していない場合は本インスタンス自身が2段階目のChangeFinderのため、平滑化の結果をそのまま返す。
        // #############################################
        if (this.secondChangeFinder == null)
        {
            return movingAverage;
        }

        double secondScore = this.secondChangeFinder.calculateScore(movingAverage);
        return secondScore;
    }

    /**
     * スコアと平滑化ウィンドウサイズを指定して平滑化を行い、移動平均スコアを算出する。
     * 
     * @param scores スコア
     * @param smoothingWindow 平滑化ウィンドウサイズ
     * @return 移動平均スコア
     */
    protected static double smoothing(List<Double> scores, int smoothingWindow)
    {
        if (scores == null || scores.size() == 0)
        {
            return 0.0d;
        }

        double scoreSum = 0.0d;
        for (double score : scores)
        {
            scoreSum = scoreSum + score;
        }

        return scoreSum / smoothingWindow;
    }

    /**
     * x推測値、x実績値、Σ最尤推定値を用いて1段階学習の結果スコアを算出する。
     * 
     * @param estimatedValue x推測値
     * @param realValue x実績値
     * @param estimatedSigma Σ最尤推定値
     * @param smoothingWindow 平滑化ウィンドウサイズ
     * @return 1段階学習の学習スコア
     */
    protected static double calcFirstScore(double estimatedValue, double realValue,
            double estimatedSigma, int smoothingWindow)
    {
        if (estimatedSigma == 0)
        {
            return 0.0d;
        }
        else
        {
            // 確率密度関数を適用した結果を算出
            double logNumerator = Math.exp(((PDF_EXP_VALUE) * Math.pow(realValue - estimatedValue,
                    2)) / estimatedSigma);
            double logDenominator = Math.sqrt(2 * Math.PI) * Math.sqrt(Math.abs(estimatedSigma));
            double logValue = logNumerator / logDenominator;

            return -Math.log(logValue);
        }
    }
}
