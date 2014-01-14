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
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * K距離算出結果を保持するEntityクラス
 * 
 * @author kimura
 */
public class KDistanceResult implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = 3609458543729663201L;

    /** K距離 */
    private double            kDistance;

    /** K距離近傍データのIDリスト */
    private List<String>      kDistanceNeighbor;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public KDistanceResult()
    {}

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
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String result = ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        return result;
    }
}
