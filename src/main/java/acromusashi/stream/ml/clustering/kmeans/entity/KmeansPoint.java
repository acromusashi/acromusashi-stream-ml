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
import org.apache.commons.math.util.MathUtils;

/**
 * KMeansクラスタリングで使用する1データ
 * 
 * @author kimura
 */
public class KmeansPoint implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = -5518876772129721631L;

    /** データ座標 */
    private double[]          dataPoint;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public KmeansPoint()
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
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (!(other instanceof KmeansPoint))
        {
            return false;
        }

        KmeansPoint otherPoint = (KmeansPoint) other;

        for (int i = 0; i < this.dataPoint.length; ++i)
        {
            if (this.dataPoint[i] != otherPoint.getDataPoint()[i])
            {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return MathUtils.hash(this.dataPoint);
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
