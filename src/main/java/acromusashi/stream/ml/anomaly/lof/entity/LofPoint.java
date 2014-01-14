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
package acromusashi.stream.ml.anomaly.lof.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Local Outlier Factorのデータ座標を保持するエンティティクラス
 * 
 * @author kimura
 */
public class LofPoint implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = -7963512394458733906L;

    /** データID */
    private String            dataId;

    /** データ座標 */
    private double[]          dataPoint;

    /** K距離値 */
    private double            kDistance;

    /** K距離近傍データのIDリスト */
    private List<String>      kDistanceNeighbor;

    /** 局所到達可能密度 */
    private double            lrd;

    /** LOF判定を行った時刻 */
    private Date              judgeDate;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public LofPoint()
    {
        // Do nothing.
    }

    /**
     * 対象エンティティのDeepCopyを作成する。
     * 
     * @return 対象エンティティのDeepCopy
     */
    public LofPoint deepCopy()
    {
        LofPoint result = new LofPoint();
        result.setDataId(this.dataId);
        result.setkDistance(this.kDistance);

        double[] copiedArray = Arrays.copyOf(this.dataPoint, this.dataPoint.length);
        result.setDataPoint(copiedArray);

        List<String> copiedList = new ArrayList<>();
        if (this.kDistanceNeighbor != null)
        {
            copiedList.addAll(this.kDistanceNeighbor);
        }

        result.setkDistanceNeighbor(copiedList);
        result.setLrd(this.lrd);
        return result;
    }

    /**
     * @return the dataId
     */
    public String getDataId()
    {
        return this.dataId;
    }

    /**
     * @param dataId the dataId to set
     */
    public void setDataId(String dataId)
    {
        this.dataId = dataId;
    }

    /**
     * @return the dataPoint
     */
    public double[] getDataPoint()
    {
        return this.dataPoint;
    }

    /**
     * @param dataPoint the dataPoint to set
     */
    public void setDataPoint(double[] dataPoint)
    {
        this.dataPoint = dataPoint;
    }

    /**
     * @return the kDistance
     */
    public double getkDistance()
    {
        return this.kDistance;
    }

    /**
     * @param kDistance the kDistance to set
     */
    public void setkDistance(double kDistance)
    {
        this.kDistance = kDistance;
    }

    /**
     * @return the kDistanceNeighbor
     */
    public List<String> getkDistanceNeighbor()
    {
        return this.kDistanceNeighbor;
    }

    /**
     * @param kDistanceNeighbor the kDistanceNeighbor to set
     */
    public void setkDistanceNeighbor(List<String> kDistanceNeighbor)
    {
        this.kDistanceNeighbor = kDistanceNeighbor;
    }

    /**
     * @return the lrd
     */
    public double getLrd()
    {
        return this.lrd;
    }

    /**
     * @param lrd the lrd to set
     */
    public void setLrd(double lrd)
    {
        this.lrd = lrd;
    }

    /**
     * @return the judgeDate
     */
    public Date getJudgeDate()
    {
        return this.judgeDate;
    }

    /**
     * @param judgeDate the judgeDate to set
     */
    public void setJudgeDate(Date judgeDate)
    {
        this.judgeDate = judgeDate;
    }

    /**
     * 文字列表現を返す。<br>
     * StormからDRPC応答を生成するコンポーネント<br>
     * (ReturnResultsReducer#complete(ReturnResultsState, TridentCollector))<br>
     * に問題があり、JSON文字列とならない状態でレスポンスが生成されるため、結果をダブルクォートで囲っている。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString()
    {
        // TODO Stormの応答生成部の問題が対応された際にダブルクォートを削除
        String result = "\""
                + ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE) + "\"";
        return result;
    }
}
