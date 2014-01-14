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
package acromusashi.stream.ml.anomaly.lof;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.TridentOperationContext;
import acromusashi.stream.ml.anomaly.lof.entity.LofResult;
import acromusashi.stream.ml.common.notify.ResultNotifier;

/**
 * LOF算出結果の通知を受け、一定以上のLOFスコアだった際にログ出力を行うPrinterクラス
 * 
 * @author kimura
 */
public class LofResultPrinter implements ResultNotifier<LofResult>
{
    /** serialVersionUID */
    private static final long   serialVersionUID = 8215393451430726107L;

    /** logger */
    private static final Logger logger           = LoggerFactory.getLogger(LofResultPrinter.class);

    /** ログ出力閾値 */
    protected double            threshold;

    /**
     * 閾値を指定してインスタンスを生成する。
     * 
     * @param threshold 閾値
     */
    public LofResultPrinter(double threshold)
    {
        this.threshold = threshold;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void initialize(Map conf, TridentOperationContext context)
    {
        // Do nothing.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyResult(LofResult result)
    {
        // LOFスコアが閾値以上の場合ログ出力を行う
        if (this.threshold < result.getLofScore())
        {
            logger.info("LOF Score thredhold over. LOF Score=" + result.getLofScore() + ", Data="
                    + Arrays.toString(result.getLofPoint().getDataPoint()));
        }
    }
}
