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
package acromusashi.stream.ml.loganalyze;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;
import acromusashi.stream.ml.anomaly.cf.ChangeFinder;
import backtype.storm.tuple.Values;

import com.google.common.collect.Maps;

/**
 * ChangeFinderを適用するTridentFunction
 * 
 * @author kimura
 */
public class ChangeFindFunction implements Function
{
    /** logger */
    private static final Logger                   logger                   = LoggerFactory.getLogger(ChangeFindFunction.class);

    /** serialVersionUID */
    private static final long                     serialVersionUID         = 6071543793313605088L;

    /** 自己回帰モデルの次数「k」デフォルト値 */
    protected static final int                    DEFAULT_AR_DIMENSION     = 4;

    /** オンライン忘却パラメータ「r」デフォルト値 */
    protected static final double                 DEFAULT_FORGETABILITY    = 0.05d;

    /** 平滑化ウィンドウサイズ「T」デフォルト値 */
    protected static final int                    DEFAULT_SMOOTHING_WINDOW = 5;

    /** changeFindを出す閾値デフォルト値 */
    protected static final double                 DEFAULT_SCORE_THRESHOLD  = 15.0d;

    /** 変化点検出を行うコンポーネントを保持するマップ */
    protected transient Map<String, ChangeFinder> changeFinderMap;

    /** 自己回帰モデルの次数「k」 */
    protected int                                 arDimensionNum           = DEFAULT_AR_DIMENSION;

    /** オンライン忘却パラメータ「r」(0~1 小さいほど過去の値に依存) */
    protected double                              forgetability            = DEFAULT_SCORE_THRESHOLD;

    /** 平滑化ウィンドウサイズ「T」 */
    protected int                                 smoothingWindow          = DEFAULT_SMOOTHING_WINDOW;

    /** changeFindを出す閾値 */
    protected double                              scoreThreshold           = DEFAULT_SCORE_THRESHOLD;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public ChangeFindFunction()
    {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map conf, TridentOperationContext context)
    {
        this.changeFinderMap = Maps.newHashMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector)
    {
        String ipAddress = tuple.getStringByField("IPaddress");
        ApacheLog response = (ApacheLog) tuple.getValueByField("responseTime");

        ChangeFinder targetChangeFinder = this.changeFinderMap.get(ipAddress);

        if (targetChangeFinder == null)
        {
            targetChangeFinder = new ChangeFinder(this.arDimensionNum, this.smoothingWindow,
                    this.forgetability, true);
            this.changeFinderMap.put(ipAddress, targetChangeFinder);
        }

        Long longResponseTime = response.getTimeSum();
        Double responseTime = longResponseTime.doubleValue();
        double changeScore = targetChangeFinder.calculateScore(responseTime);

        if (logger.isDebugEnabled() == true)
        {
            logger.debug("Change Find Result: Host=" + ipAddress + ", response time="
                    + responseTime + ", score:" + changeScore);
        }

        if (changeScore > this.scoreThreshold)
        {
            logger.info("Over scoreThreshold: Host=" + ipAddress + ", response time="
                    + responseTime + ", score:" + changeScore);
        }

        response.setAnomalyScore(changeScore);

        collector.emit(new Values(response));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup()
    {
        // Do nothing.
    }

    /**
     * @param arDimensionNum the arDimensionNum to set
     */
    public void setArDimensionNum(int arDimensionNum)
    {
        this.arDimensionNum = arDimensionNum;
    }

    /**
     * @param forgetability the forgetability to set
     */
    public void setForgetability(double forgetability)
    {
        this.forgetability = forgetability;
    }

    /**
     * @param smoothingWindow the smoothingWindow to set
     */
    public void setSmoothingWindow(int smoothingWindow)
    {
        this.smoothingWindow = smoothingWindow;
    }

    /**
     * @param scoreThreshold the scoreThreshold to set
     */
    public void setScoreThreshold(double scoreThreshold)
    {
        this.scoreThreshold = scoreThreshold;
    }
}
