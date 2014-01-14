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
package acromusashi.stream.ml.anomaly.lof.state;

import java.util.Map;

import acromusashi.stream.ml.anomaly.lof.LofCalculator;
import acromusashi.stream.ml.anomaly.lof.LofConfKey;
import acromusashi.stream.ml.anomaly.lof.entity.LofDataSet;
import acromusashi.stream.ml.common.state.MlBaseState;

/**
 * LOF分散用MapState抽象クラス<br>
 * 設定された「parallelismHint」の数だけ存在する。<br>
 * <br>
 * beginCommit/multiGet/multiPut/commitメソッドは以下の順で実行される。
 * <ol>
 * <li>beginCommit:状態更新処理開始時に実行される。</li>
 * <li>multiGet:状態更新処理中の初期状態取得時にUpdatorから実行される。</li>
 * <li>multiPut:状態更新処理中の結果反映時にUpdatorから実行される。</li>
 * <li>commit:状態更新処理終了時に実行される。</li>
 * </ol>
 * 
 * @author kimura
 */
public abstract class LofState extends MlBaseState<LofDataSet>
{
    /**
     * 設定値、構成情報をパラメータとしてインスタンスを生成する。
     * 
     * @param partitionIndex 何番目かのStateかを示すインデックス
     * @param numPartitions Stateの数
     */
    public LofState(int partitionIndex, int numPartitions)
    {
        super(partitionIndex, numPartitions);
    }

    /**
     * データモデルのマージを行う。<br>
     * ベースにしたデータモデルに指定したデータモデルをマージする。
     * 
     * @param baseDataSet マージ元データモデル
     * @param targetDataSet マージ対象データモデル
     * @param mergeConfig マージ設定
     * @return マージ元データモデルにマージ対象データモデルをマージした結果データモデル
     */
    @Override
    protected LofDataSet mergeState(LofDataSet baseDataSet, LofDataSet targetDataSet,
            Map<String, Object> mergeConfig)
    {
        int kn = Integer.parseInt(mergeConfig.get(LofConfKey.KN).toString());
        int maxDataCount = Integer.parseInt(mergeConfig.get(LofConfKey.MAX_DATA_COUNT).toString());

        // 学習データの保持する対象点を新しい方から最大保持数個だけ取得
        LofDataSet mergedDataSet = LofCalculator.mergeDataSet(baseDataSet, targetDataSet,
                maxDataCount);

        // 中間データ保持設定が存在しない場合は中間データの生成は行わない。
        if (mergeConfig.containsKey(LofConfKey.HAS_INTERMEDIATE) == false)
        {
            return mergedDataSet;
        }

        // 中間データ保持設定が存在し、かつ「true」の場合のみ中間データの生成を行う。
        if (Boolean.getBoolean(mergeConfig.get(LofConfKey.HAS_INTERMEDIATE).toString()) == true)
        {
            LofCalculator.initDataSet(kn, mergedDataSet);
        }

        return mergedDataSet;
    }
}
