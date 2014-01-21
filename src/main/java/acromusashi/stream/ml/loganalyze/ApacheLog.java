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

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Apacheのレスポンス情報を保持し、統計を算出するためのエンティティクラス
 * 
 * @author hiroki
 */
public class ApacheLog implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = -5013558830583519014L;

    /** ログ情報を識別するためのキー値 */
    private String            key;

    /** これまでにマージされたログ数 */
    private long              count;

    /** 処理サイズの合計値 */
    private long              sizeSum;

    /** 処理時間の合計値 */
    private long              timeSum;

    /** 記録された時刻 */
    private Date              recordedTime;

    /** 変化点検出用のスコア値。マージした際には最大のものを使用する。 */
    private double            anomalyScore;

    /**
     * 各種初期値を指定してインスタンスを生成する。
     * 
     * @param key ログ情報を識別するためのキー値
     * @param count ログ数
     * @param size 処理サイズ
     * @param time 処理時間
     * @param recordedTime 記録された時刻
     * @param anomalyScore 変化点検出用のスコア値
     */
    public ApacheLog(String key, long count, long size, long time, Date recordedTime,
            double anomalyScore)
    {
        this.key = key;
        this.count = count;
        this.sizeSum = size;
        this.timeSum = time;
        this.recordedTime = recordedTime;
        this.anomalyScore = anomalyScore;
    }

    /**
     * ApacheLogオブジェクトを加算し、加算した結果をもとに新たなApacheResponseオブジェクトを生成する。
     * 
     * @param logA 加算対象ApacheLog
     * @param logB 加算対象ApacheLog
     * @return 加算結果加算対象ApacheLog
     */
    public static ApacheLog add(ApacheLog logA, ApacheLog logB)
    {
        String key = logA.getKey();
        if (StringUtils.isEmpty(key) == true)
        {
            key = logB.getKey();
        }
        long countSum = logA.getCount() + logB.getCount();
        long sizeSum = logA.getSizeSum() + logB.getSizeSum();
        long timeSum = logA.getTimeSum() + logB.getTimeSum();
        Date recordedTime = logA.getRecordedTime();
        double anomalyScore = Math.max(logA.getAnomalyScore(), logB.getAnomalyScore());

        ApacheLog result = new ApacheLog(key, countSum, sizeSum, timeSum, recordedTime,
                anomalyScore);
        return result;
    }

    /**
     * 処理時間の平均値を算出する。
     * 
     * @return 処理時間平均値
     */
    public long getAverageTime()
    {
        long result = this.timeSum / this.count;
        return result;
    }

    /**
     * @return the key
     */
    public String getKey()
    {
        return this.key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * @return the count
     */
    public long getCount()
    {
        return this.count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(long count)
    {
        this.count = count;
    }

    /**
     * @return the sizeSum
     */
    public long getSizeSum()
    {
        return this.sizeSum;
    }

    /**
     * @param sizeSum the sizeSum to set
     */
    public void setSizeSum(long sizeSum)
    {
        this.sizeSum = sizeSum;
    }

    /**
     * @return the timeSum
     */
    public long getTimeSum()
    {
        return this.timeSum;
    }

    /**
     * @param timeSum the timeSum to set
     */
    public void setTimeSum(long timeSum)
    {
        this.timeSum = timeSum;
    }

    /**
     * @return the recordedTime
     */
    public Date getRecordedTime()
    {
        return this.recordedTime;
    }

    /**
     * @param recordedTime the recordedTime to set
     */
    public void setRecordedTime(Date recordedTime)
    {
        this.recordedTime = recordedTime;
    }

    /**
     * @return the anomalyScore
     */
    public double getAnomalyScore()
    {
        return this.anomalyScore;
    }

    /**
     * @param anomalyScore the anomalyScore to set
     */
    public void setAnomalyScore(double anomalyScore)
    {
        this.anomalyScore = anomalyScore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String result = ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        return result;
    }
}
