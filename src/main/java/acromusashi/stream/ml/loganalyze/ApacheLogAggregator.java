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
package acromusashi.stream.ml.loganalyze;

import java.util.Date;

import storm.trident.operation.CombinerAggregator;
import storm.trident.tuple.TridentTuple;

/**
 * ApacheLogの統計値を算出するためのAggregatorクラス
 * 
 * @author kimura
 */
public class ApacheLogAggregator implements CombinerAggregator<ApacheLog>
{
    /** serialVersionUID */
    private static final long serialVersionUID = -5171965740633506380L;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public ApacheLogAggregator()
    {}

    /**
     * {@inheritDoc}
     */
    @Override
    public ApacheLog init(TridentTuple tuple)
    {
        return (ApacheLog) tuple.getValue(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApacheLog combine(ApacheLog val1, ApacheLog val2)
    {
        return ApacheLog.add(val1, val2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApacheLog zero()
    {
        return new ApacheLog("", 0, 0, 0, new Date(0), 0);
    }
}
