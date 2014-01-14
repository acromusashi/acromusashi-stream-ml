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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * LOF算出結果格納エンティティ
 * 
 * @author kimura
 */
public class LofResult
{
    /** LOFスコア */
    private double   lofScore;

    /** LOFスコア算出に用いた点 */
    private LofPoint lofPoint;

    /**
     * LOFスコアと点を指定してインスタンスを生成する。
     * 
     * @param lofScore LOFスコア
     * @param lofPoint LOFスコア算出に用いた点
     */
    public LofResult(double lofScore, LofPoint lofPoint)
    {
        this.lofScore = lofScore;
        this.lofPoint = lofPoint;
    }

    /**
     * @return the lofScore
     */
    public double getLofScore()
    {
        return this.lofScore;
    }

    /**
     * @param lofScore the lofScore to set
     */
    public void setLofScore(double lofScore)
    {
        this.lofScore = lofScore;
    }

    /**
     * @return the lofPoint
     */
    public LofPoint getLofPoint()
    {
        return this.lofPoint;
    }

    /**
     * @param lofPoint the lofPoint to set
     */
    public void setLofPoint(LofPoint lofPoint)
    {
        this.lofPoint = lofPoint;
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
