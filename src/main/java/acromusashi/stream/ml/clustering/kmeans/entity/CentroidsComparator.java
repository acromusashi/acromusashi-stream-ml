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
import java.util.Comparator;

/**
 * 中心点間の距離を比較するコンパレータ
 * 
 * @author kimura
 */
public class CentroidsComparator implements Comparator<CentroidMapping>, Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = -8818218082040570141L;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public CentroidsComparator()
    {}

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(CentroidMapping o1, CentroidMapping o2)
    {
        return Double.compare(o1.getEuclideanDistance(), o2.getEuclideanDistance());
    }
}
