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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 計算結果保持中間Entity
 * 
 * @author kimura
 */
public class DistanceResult implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = -3372402390204781723L;

    /** データID */
    private String            dataId;

    /** 距離 */
    private double            distance;

    /**
     * データID、距離を指定してインスタンスを生成する。
     * 
     * @param dataId データID
     * @param distance 距離
     */
    public DistanceResult(String dataId, double distance)
    {
        this.dataId = dataId;
        this.distance = distance;
    }

    /**
     * @return the dataId
     */
    public String getDataId()
    {
        return this.dataId;
    }

    /**
     * @return the distance
     */
    public double getDistance()
    {
        return this.distance;
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
