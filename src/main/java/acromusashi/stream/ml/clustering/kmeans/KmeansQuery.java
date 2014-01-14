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
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansDataSet;
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansPoint;
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansResult;
import backtype.storm.tuple.Values;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * LOFに外部からデータを投入する口を提供するクエリ
 * 
 * @author kimura
 */
public class KmeansQuery extends BaseQueryFunction<MapState<KmeansDataSet>, String>
{
    /** serialVersionUID */
    private static final long        serialVersionUID = -9162800953757233992L;

    /** logger */
    private static final Logger      logger           = LoggerFactory.getLogger(KmeansQuery.class);

    /** State中のベース名称 */
    private String                   baseStateName;

    /** Jacksonを用いた変換マッパーオブジェクト */
    protected transient ObjectMapper objectMapper;

    /**
     * ベース名称を指定してインスタンスを生成する。
     * 
     * @param baseStateName State中のベース名称
     */
    public KmeansQuery(String baseStateName)
    {
        this.baseStateName = baseStateName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> batchRetrieve(MapState<KmeansDataSet> state, List<TridentTuple> args)
    {
        if (this.objectMapper == null)
        {
            this.objectMapper = new ObjectMapper();
        }

        // 学習データモデルを取得
        List<KmeansDataSet> dataSets = state.multiGet(Arrays.asList(Arrays.asList((Object) this.baseStateName)));
        KmeansDataSet dataSet = null;
        if (dataSets != null && !dataSets.isEmpty())
        {
            dataSet = dataSets.get(0);
        }

        List<String> resultStrList = new ArrayList<>();
        // 受信したデータ毎にクラスタリングを行い、結果を返す。
        for (TridentTuple targetTuple : args)
        {
            KmeansPoint recievedPoint = (KmeansPoint) targetTuple.get(0);

            // 状態が取得できなかった場合は空文字を結果として設定する。
            if (dataSet == null)
            {
                resultStrList.add("");
                continue;
            }

            KmeansResult result = KmeansCalculator.classify(recievedPoint, dataSet);
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
