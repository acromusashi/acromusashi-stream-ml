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
package acromusashi.stream.ml.anomaly.lof;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import acromusashi.stream.ml.anomaly.lof.entity.LofDataSet;
import acromusashi.stream.ml.anomaly.lof.entity.LofPoint;
import acromusashi.stream.ml.anomaly.lof.entity.LofResult;
import acromusashi.stream.ml.common.notify.ResultNotifier;

/**
 * LOFアルゴリズムの計算を行い、保存状態のアップデートを行うUpdaterクラス
 * 
 * @author kimura
 */
public class LofUpdater extends BaseStateUpdater<MapState<LofDataSet>>
{
    /** serialVersionUID */
    private static final long            serialVersionUID = -8751225616008741403L;

    /** データ処理時に呼び出される通知オブジェクト */
    protected ResultNotifier<LofResult>  dataNotifier;

    /** バッチ終了時に呼び出される通知オブジェクト */
    protected ResultNotifier<LofDataSet> batchNotifier;

    /** 状態名称 */
    private String                       stateName;

    /** 初期状態 */
    private LofDataSet                   initialState     = new LofDataSet();

    /** 中間データを学習データに保持するか */
    private boolean                      hasIntermediate;

    /** データ受信時、常時学習データモデルを更新するか */
    private boolean                      alwaysUpdateModel;

    /** 常時学習データモデル更新でない場合にデータいくつ毎に学習モデルの更新を行うか */
    private int                          updateInterval;

    /** LOFパラメータ「K」値 */
    private int                          kn;

    /** 学習データモデルが実際にLOF算出を行う際に必要な最小データ数 */
    private int                          minDataCount;

    /** 学習データモデルが最大で保持するデータ数 */
    private int                          maxDataCount;

    /** 本コンポーネントが受信したデータ数 */
    private int                          receiveCount;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public LofUpdater()
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
    public void updateState(MapState<LofDataSet> state, List<TridentTuple> tuples,
            TridentCollector collector)
    {
        // Get model
        List<LofDataSet> dataSets = state.multiGet(Arrays.asList(Arrays.asList((Object) this.stateName)));
        LofDataSet dataSet = null;
        if (dataSets != null && !dataSets.isEmpty())
        {
            dataSet = dataSets.get(0);
        }
        else
        {
            dataSet = this.initialState;
        }

        for (TridentTuple targetTuple : tuples)
        {
            this.receiveCount++;
            LofPoint recievedPoint = (LofPoint) targetTuple.get(0);
            double lofScore = receivePoint(recievedPoint, dataSet);

            if (this.dataNotifier != null)
            {
                this.dataNotifier.notifyResult(new LofResult(lofScore, recievedPoint));
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
     * データを受信した時の処理を行う。<br>
     * LOFスコアの算出を行う。
     * 
     * @param recievedPoint 受信データ
     * @param dataSet 学習モデルデータ
     * @return 算出されたLOF値。データが最小数に満たない場合は0.0が返る。
     */
    protected double receivePoint(LofPoint recievedPoint, LofDataSet dataSet)
    {
        // 現在保持している学習データ数
        int dataCount = dataSet.getDataIdList().size();

        // データが最小数に満たない場合はデータの追加のみを行い、LOF値は0.0として扱う
        if (dataCount < this.minDataCount)
        {
            addDataWithoutCalculate(recievedPoint, dataSet);
            return 0.0d;
        }

        double lofScore = 0.0d;

        if (this.alwaysUpdateModel || dataSet.getDataIdList().size() < this.maxDataCount
                || (this.receiveCount % this.updateInterval) == 0)
        {
            // 以下の条件を満たす場合、学習データモデルの更新、及びLOF値の算出を行う。
            // 1. 「データ受信時、常時学習データモデルを更新」がtrue
            // 2. 学習データモデルの保持するデータ数が最大保持数未満の場合
            // 3. 受信データ数 % 学習データモデル更新間隔 = 0
            lofScore = calculateLofWithUpdate(recievedPoint, dataSet);
        }
        else
        {
            // 上記の条件をいずれも満たさない場合、スコアの算出のみを行う。
            lofScore = calculateLofWithoutUpdate(recievedPoint, dataSet);
        }

        return lofScore;
    }

    /**
     * LOF算出は行わず、学習データモデルへのデータ追加のみを行う。
     * 
     * @param recievedPoint 受信データ
     * @param dataSet 学習モデルデータ
     */
    protected void addDataWithoutCalculate(LofPoint recievedPoint, LofDataSet dataSet)
    {
        dataSet.addData(recievedPoint);

        // 中間データを保持する場合はデータ追加時に中間データの生成を行う。
        if (this.hasIntermediate)
        {
            LofCalculator.initDataSet(this.kn, dataSet);
        }
    }

    /**
     * 学習データモデルの更新を行い、LOF値を算出する。
     *  
     * @param recievedPoint 受信データ
     * @param dataSet 学習モデルデータ
     * @return LOF値算出結果
     */
    protected double calculateLofWithUpdate(LofPoint recievedPoint, LofDataSet dataSet)
    {
        double result = 0.0d;

        if (this.hasIntermediate)
        {
            result = LofCalculator.calculateLofWithUpdate(this.kn, this.maxDataCount,
                    recievedPoint, dataSet);
        }
        else
        {
            LofCalculator.addPointToDataSet(this.maxDataCount, recievedPoint, dataSet);
            result = LofCalculator.calculateLofNoIntermediate(this.kn, recievedPoint, dataSet);
        }

        return result;
    }

    /**
     * 学習データモデルの更新を行なわず、LOF値の算出のみを行う。
     *  
     * @param recievedPoint 受信データ
     * @param dataSet 学習モデルデータ
     * @return LOF値算出結果
     */
    protected double calculateLofWithoutUpdate(LofPoint recievedPoint, LofDataSet dataSet)
    {
        double result = 0.0d;

        if (this.hasIntermediate)
        {
            result = LofCalculator.calculateLofWithoutUpdate(this.kn, recievedPoint, dataSet);
        }
        else
        {
            result = LofCalculator.calculateLofNoIntermediate(this.kn, recievedPoint, dataSet);
        }

        return result;
    }

    /**
     * @param dataNotifier the dataNotifier to set
     */
    public void setDataNotifier(ResultNotifier<LofResult> dataNotifier)
    {
        this.dataNotifier = dataNotifier;
    }

    /**
     * @param batchNotifier the batchNotifier to set
     */
    public void setBatchNotifier(ResultNotifier<LofDataSet> batchNotifier)
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
     * @param initialState the initialState to set
     */
    public void setInitialState(LofDataSet initialState)
    {
        this.initialState = initialState;
    }

    /**
     * @param hasIntermediate the hasIntermediate to set
     */
    public void setHasIntermediate(boolean hasIntermediate)
    {
        this.hasIntermediate = hasIntermediate;
    }

    /**
     * @param alwaysUpdateModel the alwaysUpdateModel to set
     */
    public void setAlwaysUpdateModel(boolean alwaysUpdateModel)
    {
        this.alwaysUpdateModel = alwaysUpdateModel;
    }

    /**
     * @param updateInterval the updateInterval to set
     */
    public void setUpdateInterval(int updateInterval)
    {
        this.updateInterval = updateInterval;
    }

    /**
     * @param kn the kn to set
     */
    public void setKn(int kn)
    {
        this.kn = kn;
    }

    /**
     * @param minDataCount the minDataCount to set
     */
    public void setMinDataCount(int minDataCount)
    {
        this.minDataCount = minDataCount;
    }

    /**
     * @param maxDataCount the maxDataCount to set
     */
    public void setMaxDataCount(int maxDataCount)
    {
        this.maxDataCount = maxDataCount;
    }
}
