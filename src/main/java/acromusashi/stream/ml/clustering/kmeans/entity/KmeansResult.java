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
package acromusashi.stream.ml.clustering.kmeans.entity;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * KMeansクラスタリングの結果を格納するエンティティ
 * 
 * @author kimura
 */
public class KmeansResult implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = -5518876772129721631L;

    /** 判定対象となったデータ */
    private double[]          dataPoint;

    /** クラスタリングされた中心点のインデックス */
    private int               centroidIndex;

    /** クラスタリングされた中心点 */
    private double[]          centroid;

    /** 中心点からの距離 */
    private double            distance;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public KmeansResult()
    {}

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
     * @return the centroidIndex
     */
    public int getCentroidIndex()
    {
        return this.centroidIndex;
    }

    /**
     * @param centroidIndex the centroidIndex to set
     */
    public void setCentroidIndex(int centroidIndex)
    {
        this.centroidIndex = centroidIndex;
    }

    /**
     * @return the centroid
     */
    public double[] getCentroid()
    {
        return this.centroid;
    }

    /**
     * @param centroid the centroid to set
     */
    public void setCentroid(double[] centroid)
    {
        this.centroid = centroid;
    }

    /**
     * @return the distance
     */
    public double getDistance()
    {
        return this.distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(double distance)
    {
        this.distance = distance;
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
