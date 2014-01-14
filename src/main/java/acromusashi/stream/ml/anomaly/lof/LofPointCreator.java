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

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;
import acromusashi.stream.ml.anomaly.lof.entity.LofPoint;
import backtype.storm.tuple.Values;

/**
 * LOFの対象ポイントを生成するFunctionクラス
 * 
 * @author kimura
 */
public class LofPointCreator extends BaseFunction
{
    /** serialVersionUID */
    private static final long   serialVersionUID = 4620883521921594615L;

    /** logger */
    private static final Logger logger           = LoggerFactory.getLogger(LofPointCreator.class);

    /** 読みこんだデータを分割する文字列 */
    private String              delimeter        = ",";

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public LofPointCreator()
    {}

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map conf, TridentOperationContext context)
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("prepared started. taskIndex=" + context.getPartitionIndex()
                    + ", taxkNum=" + context.numPartitions());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector)
    {
        String receivedStr = tuple.getString(0);

        String[] splitedStr = StringUtils.split(receivedStr, this.delimeter);
        int dataNum = splitedStr.length;
        double[] points = new double[splitedStr.length];

        try
        {
            for (int index = 0; index < dataNum; index++)
            {
                points[index] = Double.parseDouble(splitedStr[index].trim());
            }

            LofPoint result = new LofPoint();
            result.setDataId(UUID.randomUUID().toString());
            result.setDataPoint(points);
            result.setJudgeDate(new Date(getCurrentTime()));
            collector.emit(new Values(result));
        }
        catch (Exception ex)
        {
            logger.warn("Received data is invalid. skip this data. ReceivedData=" + receivedStr, ex);
        }
    }

    /**
     * 現在の時刻値を取得する。
     * 
     * @return 現在の時刻値
     */
    protected long getCurrentTime()
    {
        return System.currentTimeMillis();
    }

    /**
     * @return the delimeter
     */
    public String getDelimeter()
    {
        return this.delimeter;
    }

    /**
     * @param delimeter the delimeter to set
     */
    public void setDelimeter(String delimeter)
    {
        this.delimeter = delimeter;
    }
}
