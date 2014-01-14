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
import java.util.Comparator;

/**
 * DistanceResultを比較するコンパレータクラス。<br>
 * 以下の優先度で大小を比較し、結果を返す。
 * <ol>
 * <li>距離</li>
 * <li>データID</li>
 * </ol>
 * 
 * @author kimura
 */
public class DistanceResultComparator implements Comparator<DistanceResult>, Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = -3680794710780488725L;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public DistanceResultComparator()
    {}

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(DistanceResult o1, DistanceResult o2)
    {
        Double o1Distance = o1.getDistance();
        Double o2Distance = o2.getDistance();

        int distanceResult = o1Distance.compareTo(o2Distance);
        if (distanceResult != 0)
        {
            return distanceResult;
        }

        return o1.getDataId().compareTo(o2.getDataId());
    }
}
