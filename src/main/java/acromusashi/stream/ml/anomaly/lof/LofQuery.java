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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseQueryFunction;
import storm.trident.state.map.MapState;
import storm.trident.tuple.TridentTuple;
import acromusashi.stream.ml.anomaly.lof.entity.LofDataSet;
import acromusashi.stream.ml.anomaly.lof.entity.LofPoint;
import acromusashi.stream.ml.anomaly.lof.entity.LofResult;
import backtype.storm.tuple.Values;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * LOFに外部からデータを投入する口を提供するクエリ
 * 
 * @author kimura
 */
public class LofQuery extends BaseQueryFunction<MapState<LofDataSet>, String>
{
    /** serialVersionUID */
    private static final long        serialVersionUID = -9162800953757233992L;

    /** logger */
    private static final Logger      logger           = LoggerFactory.getLogger(LofQuery.class);

    /** State中のベース名称 */
    private String                   baseStateName;

    /** LOFパラメータ「K」値 */
    private int                      kn;

    /** 中間データを学習データに保持するか */
    private boolean                  hasIntermediate;

    /** Jacksonを用いた変換マッパーオブジェクト */
    protected transient ObjectMapper objectMapper;

    /**
     * ベース名称を指定してインスタンスを生成する。
     * 
     * @param baseStateName State中のベース名称
     * @param kn K値
     * @param hasIntermediate 状態が中間データを保持するか
     */
    public LofQuery(String baseStateName, int kn, boolean hasIntermediate)
    {
        this.baseStateName = baseStateName;
        this.kn = kn;
        this.hasIntermediate = hasIntermediate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> batchRetrieve(MapState<LofDataSet> state, List<TridentTuple> tuples)
    {
        if (this.objectMapper == null)
        {
            this.objectMapper = new ObjectMapper();
        }

        // Get model
        List<LofDataSet> dataSets = state.multiGet(Arrays.asList(Arrays.asList((Object) this.baseStateName)));
        LofDataSet dataSet = null;
        if (dataSets != null && !dataSets.isEmpty())
        {
            dataSet = dataSets.get(0);
        }

        List<String> resultStrList = new ArrayList<>();
        // 受信したデータ毎にLOFスコアの算出を行い、結果のJSON文字列を返す
        for (TridentTuple targetTuple : tuples)
        {
            LofPoint recievedPoint = (LofPoint) targetTuple.get(0);

            // データモデルが取得できなかった場合は空文字を結果として設定
            if (dataSet == null)
            {
                resultStrList.add("");
                continue;
            }

            double lofScore = 0.0d;
            if (this.hasIntermediate)
            {
                lofScore = LofCalculator.calculateLofWithoutUpdate(this.kn, recievedPoint, dataSet);
            }
            else
            {
                lofScore = LofCalculator.calculateLofNoIntermediate(this.kn, recievedPoint, dataSet);
            }

            LofResult result = new LofResult(lofScore, recievedPoint);

            String resultStr = null;

            try
            {
                resultStr = this.objectMapper.writeValueAsString(result);
            }
            catch (IOException ex)
            {
                logger.warn("Received data is invalid. skip this data. ReceivedData="
                        + recievedPoint, ex);
            }

            if (resultStr != null)
            {
                resultStrList.add(resultStr);
            }
            else
            {
                resultStrList.add("");
            }
        }

        return resultStrList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TridentTuple tuple, String result, TridentCollector collector)
    {
        collector.emit(new Values(result));
    }
}
