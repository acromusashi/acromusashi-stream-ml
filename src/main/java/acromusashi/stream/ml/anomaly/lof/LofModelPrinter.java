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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.TridentOperationContext;
import acromusashi.stream.ml.anomaly.lof.entity.LofDataSet;
import acromusashi.stream.ml.anomaly.lof.entity.LofPoint;
import acromusashi.stream.ml.common.notify.ResultNotifier;

/**
 * LOF学習データモデル(点のみ)を出力する通知受付クラス
 * 
 * @author kimura
 */
public class LofModelPrinter implements ResultNotifier<LofDataSet>
{
    /** serialVersionUID */
    private static final long   serialVersionUID = 8215393451430726107L;

    /** logger */
    private static final Logger logger           = LoggerFactory.getLogger(LofModelPrinter.class);

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public LofModelPrinter()
    {}

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void initialize(Map conf, TridentOperationContext context)
    {
        // Do nothing.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyResult(LofDataSet result)
    {
        StringBuilder builder = new StringBuilder();
        for (LofPoint targetPoint : result.getDataMap().values())
        {
            builder.append(Arrays.toString(targetPoint.getDataPoint()));
        }

        logger.info("LofModel PointList=" + builder.toString());
    }
}
