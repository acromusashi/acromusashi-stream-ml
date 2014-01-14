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
 * 生成時刻で比較を行うコンパレータクラス
 * 
 * @author kimura
 */
public class LofPointComparator implements Comparator<LofPoint>, Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = 3897066986176797777L;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public LofPointComparator()
    {}

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(LofPoint o1, LofPoint o2)
    {
        int result = o1.getJudgeDate().compareTo(o2.getJudgeDate());
        return result;
    }
}
