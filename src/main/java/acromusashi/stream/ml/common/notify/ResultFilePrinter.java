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
package acromusashi.stream.ml.common.notify;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.TridentOperationContext;

/**
 * 結果をファイル出力する通知クラス
 * 
 * @author kimura
 * @param <T> 結果出力型
 */
public class ResultFilePrinter<T> implements ResultNotifier<T>
{
    /** serialVersionUID */
    private static final long            serialVersionUID = -8586160590825985631L;

    /** logger */
    private static Logger                logger           = LoggerFactory.getLogger(ResultLogPrinter.class);

    /** 出力先ファイルパス */
    protected String                     filePath;

    /** 出力時のエンコード */
    protected String                     encode;

    /** 出力時のヘッダ */
    protected String                     header;

    /** ファイル出力用オブジェクト */
    protected transient FileOutputStream outputStream;

    /**
     * 出力時のファイルパス、出力時のヘッダを指定してインスタンスを生成する。
     * 
     * @param filePath 出力先ファイルパス
     * @param encode 出力時のエンコード
     * @param header 出力時のヘッダ
     */
    public ResultFilePrinter(String filePath, String encode, String header)
    {
        this.filePath = filePath;
        this.encode = encode;
        this.header = header;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void initialize(Map conf, TridentOperationContext context)
    {
        try
        {
            this.outputStream = new FileOutputStream(this.filePath, true);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyResult(T result)
    {
        try
        {
            IOUtils.write(this.header + result.toString(), this.outputStream, this.encode);
        }
        catch (IOException ex)
        {
            logger.warn("ResultFilePrint failed. File=" + this.filePath + ", Content="
                    + this.header + result.toString(), ex);
        }
    }
}
