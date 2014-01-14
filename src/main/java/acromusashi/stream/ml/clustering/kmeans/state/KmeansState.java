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
package acromusashi.stream.ml.clustering.kmeans.state;

import java.util.Map;

import acromusashi.stream.ml.clustering.kmeans.KmeansCalculator;
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansDataSet;
import acromusashi.stream.ml.common.state.MlBaseState;

/**
 * KMeans分散用MapState抽象クラス<br>
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
public abstract class KmeansState extends MlBaseState<KmeansDataSet>
{
    /**
     * 設定値、構成情報をパラメータとしてインスタンスを生成する。
     * 
     * @param partitionIndex 何番目かのStateかを示すインデックス
     * @param numPartitions Stateの数
     */
    public KmeansState(int partitionIndex, int numPartitions)
    {
        super(partitionIndex, numPartitions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected KmeansDataSet mergeState(KmeansDataSet baseDataModel, KmeansDataSet targetDataModel,
            Map<String, Object> mergeConfig)
    {
        KmeansDataSet mergedDataModel = KmeansCalculator.mergeKmeans(baseDataModel, targetDataModel);
        return mergedDataModel;
    }
}
