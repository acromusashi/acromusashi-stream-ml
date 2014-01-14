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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ApacheログをEntityに分割変換するFunctionクラス
 *
 * @author hiroki
 */
public class ApacheLogSplitFunction extends BaseFunction
{
    /** serialVersionUID */
    private static final long        serialVersionUID       = 641457679579243381L;

    /** logger */
    private static final Logger      logger                 = LoggerFactory.getLogger(ApacheLogSplitFunction.class);

    /** 「Apacheログ中のDateFormat」デフォルト値 */
    private static final String      DEFAULT_DATEFORMAT_STR = "yyyy-MM-dd'T'HH:mm:SSSZ";

    /** Apacheログ中のDateFormat */
    protected String                 dateFormatStr          = DEFAULT_DATEFORMAT_STR;

    /** Jacksonを用いた変換マッパーオブジェクト */
    protected transient ObjectMapper objectMapper;

    /** Java用日付変換フォーマッタ(TridentFunctionはマルチスレッドアクセスされないため、フィールド変数として保持) */
    protected SimpleDateFormat       javaDateFormat;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public ApacheLogSplitFunction()
    {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map conf, TridentOperationContext context)
    {
        this.objectMapper = new ObjectMapper();
        this.javaDateFormat = new SimpleDateFormat(this.dateFormatStr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TridentTuple tuple, TridentCollector collector)
    {
        Map<String, String> logMap = null;
        try
        {
            logMap = convertToMap(tuple);
        }
        catch (IOException ex)
        {
            logger.warn("Map convert failed. Trash tuple.　Tuple=" + tuple, ex);
            return;
        }

        try
        {
            ApacheLog convertedEntity = createEntity(logMap);
            collector.emit(new Values(convertedEntity.getKey(), convertedEntity));
        }
        catch (Exception ex)
        {
            logger.info("Entity convert failed. Trash tuple.　LogMap=" + logMap, ex);
        }
    }

    /**
     * Tupleからログ情報をマップに変換する。
     *
     * @param tuple 受信Tuple
     * @return ログ情報を格納したMap
     * @throws IOException マップ変換失敗時
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> convertToMap(TridentTuple tuple) throws IOException
    {
        String logStr = tuple.getStringByField("str");
        Map<String, String> logMap = this.objectMapper.readValue(logStr, Map.class);
        return logMap;
    }

    /**
     * ログ情報マップを基にApacheLogエンティティを生成する。
     * 
     * @param logInfoMap ログ情報マップ
     * @return ApacheLogエンティティ
     * @throws ParseException パース失敗時
     */
    protected ApacheLog createEntity(Map<String, String> logInfoMap) throws ParseException
    {
        String serverName = logInfoMap.get("hostname");
        String sizeStr = logInfoMap.get("size");
        String timeStr = logInfoMap.get("reqtime_microsec");
        String recordedTimeStr = logInfoMap.get("time");

        long size = 0;
        long time = 0;
        Date recordedTime = null;

        if (sizeStr != null && StringUtils.equals(sizeStr, "-") == false)
        {
            size = Long.valueOf(sizeStr);
        }

        if (timeStr != null && StringUtils.equals(timeStr, "-") == false)
        {
            time = Long.valueOf(timeStr);
        }

        if (recordedTimeStr != null)
        {
            recordedTime = this.javaDateFormat.parse(recordedTimeStr);
        }

        ApacheLog result = new ApacheLog(serverName, 1, size, time, recordedTime, 0d);
        return result;
    }

    /**
     * @param dateFormatStr the dataFormatStr to set
     */
    public void setDateFormatStr(String dateFormatStr)
    {
        this.dateFormatStr = dateFormatStr;
    }
}
