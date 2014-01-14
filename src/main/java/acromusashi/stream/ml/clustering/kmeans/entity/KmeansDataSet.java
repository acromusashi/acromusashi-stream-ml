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
 * KMeansクラスタリングの学習モデルを保持するエンティティクラス
 * 
 * @author kimura
 */
public class KmeansDataSet implements Serializable
{
    /** シリアル */
    private static final long serialVersionUID = 338231277453149972L;

    /** 各クラスタの中心座標の行列を格納した配列 */
    private double[][]        centroids;

    /** 各クラスタに分類された要素数 */
    private long[]            clusteredNum;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public KmeansDataSet()
    {}

    /**
     * @return the centroids
     */
    public double[][] getCentroids()
    {
        return this.centroids;
    }

    /**
     * @param centroids the centroids to set
     */
    public void setCentroids(double[][] centroids)
    {
        this.centroids = centroids;
    }

    /**
     * @return the clusteredNum
     */
    public long[] getClusteredNum()
    {
        return this.clusteredNum;
    }

    /**
     * @param clusteredNum the clusteredNum to set
     */
    public void setClusteredNum(long[] clusteredNum)
    {
        this.clusteredNum = clusteredNum;
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
