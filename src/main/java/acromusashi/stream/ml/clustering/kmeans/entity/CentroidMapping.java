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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 中心点同士の距離とマッピング情報を保持するエンティティ
 * 
 * @author kimura
 */
public class CentroidMapping
{
    /** ベース中心点のインデックス */
    private int    baseIndex;

    /** マージ対象中心点のインデックス */
    private int    targetIndex;

    /** ユークリッド距離 */
    private double euclideanDistance;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public CentroidMapping()
    {}

    /**
     * @return the baseIndex
     */
    public int getBaseIndex()
    {
        return this.baseIndex;
    }

    /**
     * @param baseIndex the baseIndex to set
     */
    public void setBaseIndex(int baseIndex)
    {
        this.baseIndex = baseIndex;
    }

    /**
     * @return the targetIndex
     */
    public int getTargetIndex()
    {
        return this.targetIndex;
    }

    /**
     * @param targetIndex the targetIndex to set
     */
    public void setTargetIndex(int targetIndex)
    {
        this.targetIndex = targetIndex;
    }

    /**
     * @return the euclideanDistance
     */
    public double getEuclideanDistance()
    {
        return this.euclideanDistance;
    }

    /**
     * @param euclideanDistance the euclideanDistance to set
     */
    public void setEuclideanDistance(double euclideanDistance)
    {
        this.euclideanDistance = euclideanDistance;
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
