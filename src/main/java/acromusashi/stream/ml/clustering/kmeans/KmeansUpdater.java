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
package acromusashi.stream.ml.clustering.kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansDataSet;
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansPoint;
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansResult;
import acromusashi.stream.ml.common.notify.ResultNotifier;

/**
 * KMeansアルゴリズムの計算を行い、保存状態のアップデートを行うUpdaterクラス
 * 
 * @author kimura
 */
public class KmeansUpdater extends BaseStateUpdater<MapState<KmeansDataSet>>
{
    /** 「クラスタ数」デフォルト値 */
    private static final int              DEFAULT_CLUSTER_NUM       = 2;

    /** 「最大イテレーション回数」デフォルト値 */
    private static final int              DEFAULT_MAX_ITERATION     = 100;

    /** 「中心点が収束したと判断する差分閾値」デフォルト値 */
    private static final double           DEFAULT_CONVERGENCE_THRES = 0.1d;

    /** serialVersionUID */
    private static final long             serialVersionUID          = -8751225616008741403L;

    /** データ処理時に呼び出される通知オブジェクト */
    private ResultNotifier<KmeansResult>  dataNotifier;

    /** バッチ終了時に呼び出される通知オブジェクト */
    private ResultNotifier<KmeansDataSet> batchNotifier;

    /** 状態名称 */
    private String                        stateName                 = "KMeans";

    /** クラスタ数 */
    private int                           clusterNum                = DEFAULT_CLUSTER_NUM;

    /** 最大イテレーション回数 */
    private int                           maxIteration              = DEFAULT_MAX_ITERATION;

    /** 中心点が収束したと判断する差分閾値 */
    private double                        convergenceThreshold      = DEFAULT_CONVERGENCE_THRES;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public KmeansUpdater()
    {}

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map conf, TridentOperationContext context)
    {
        // データ処理時のオブジェクト、バッチ処理時のオブジェクトが設定されている場合、初期化を行う。
        if (this.dataNotifier != null)
        {
            this.dataNotifier.initialize(conf, context);
        }
        if (this.batchNotifier != null)
        {
            this.batchNotifier.initialize(conf, context);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateState(MapState<KmeansDataSet> state, List<TridentTuple> tuples,
            TridentCollector collector)
    {
        // データが流れてきていない場合はupdateStateメソッドが呼ばれないため、本メソッド内ではデータが存在するとして扱う。
        List<KmeansDataSet> dataSets = state.multiGet(Arrays.asList(Arrays.asList((Object) this.stateName)));
        KmeansDataSet dataSet = null;
        if (dataSets != null && !dataSets.isEmpty())
        {
            dataSet = dataSets.get(0);
        }

        // TupleからKMeans用データを抽出する。
        List<KmeansPoint> receiveList = new ArrayList<>();
        for (TridentTuple targetTuple : tuples)
        {
            KmeansPoint recievedPoint = (KmeansPoint) targetTuple.get(0);
            receiveList.add(recievedPoint);
        }

        // 投入されたKMeans用点から学習モデルを生成する。
        KmeansDataSet generatedDataSet = KmeansCalculator.createDataModel(receiveList,
                this.clusterNum, this.maxIteration, this.convergenceThreshold);
        if (generatedDataSet != null)
        {
            dataSet = generatedDataSet;
        }

        // 元々データモデルが存在せず、かつ投入された点が不足しており学習モデルが生成できなかった場合、以後の処理は行わない。
        if (dataSet == null)
        {
            return;
        }

        // データ通知拡張ポイントに値が設定されていた場合、クラスタリング結果を算出し、通知を行う。
        if (this.dataNotifier != null)
        {
            for (KmeansPoint targetPoint : receiveList)
            {
                KmeansResult result = KmeansCalculator.classify(targetPoint, dataSet);
                this.dataNotifier.notifyResult(result);
            }
        }

        // Save model
        state.multiPut(Arrays.asList(Arrays.asList((Object) this.stateName)),
                Arrays.asList(dataSet));

        if (this.batchNotifier != null)
        {
            this.batchNotifier.notifyResult(dataSet);
        }
    }

    /**
     * @param dataNotifier the dataNotifier to set
     */
    public void setDataNotifier(ResultNotifier<KmeansResult> dataNotifier)
    {
        this.dataNotifier = dataNotifier;
    }

    /**
     * @param batchNotifier the batchNotifier to set
     */
    public void setBatchNotifier(ResultNotifier<KmeansDataSet> batchNotifier)
    {
        this.batchNotifier = batchNotifier;
    }

    /**
     * @param stateName the stateName to set
     */
    public void setStateName(String stateName)
    {
        this.stateName = stateName;
    }

    /**
     * @param clusterNum the clusterNum to set
     */
    public void setClusterNum(int clusterNum)
    {
        this.clusterNum = clusterNum;
    }

    /**
     * @param maxIteration the maxIteration to set
     */
    public void setMaxIteration(int maxIteration)
    {
        this.maxIteration = maxIteration;
    }

    /**
     * @param convergenceThreshold the convergenceThreshold to set
     */
    public void setConvergenceThreshold(double convergenceThreshold)
    {
        this.convergenceThreshold = convergenceThreshold;
    }

}
