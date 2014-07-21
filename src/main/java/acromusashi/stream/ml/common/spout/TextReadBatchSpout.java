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
package acromusashi.stream.ml.common.spout;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**
 * Textファイルからデータを読み込んで流すSpout<br>
 * 
 * @author kimura
 */
public class TextReadBatchSpout implements IBatchSpout
{
    /** serialVersionUID */
    private static final long   serialVersionUID = -5097002059382826053L;

    /** logger */
    private static final Logger logger           = LoggerFactory.getLogger(TextReadBatchSpout.class);

    /** データファイル配置パス */
    private String              dataFilePath;

    /** ベースファイル名 */
    private String              baseFileName;

    /** 末尾までファイルを読み切った場合、再度頭から読み込むか */
    private boolean             isFileReload;

    /** ファイル読み込み時のバッチサイズ */
    private int                 maxBatchSize;

    /** TaskIndex */
    private int                 taskIndex;

    /** ファイル名称 */
    private String              fileName;

    /** 現在ファイルを読んでいるインデックス */
    private int                 readIndex;

    /** ファイル内容 */
    private List<String>        fileContents;

    /** ファイル内容の行数 */
    private int                 fileContentsSize;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public TextReadBatchSpout()
    {}

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"rawtypes"})
    @Override
    public void open(Map conf, TopologyContext context)
    {
        this.taskIndex = context.getThisTaskIndex();
        this.fileName = this.baseFileName + "_" + this.taskIndex;
        File targetFile = new File(this.dataFilePath, this.fileName);
        try
        {
            this.fileContents = FileUtils.readLines(targetFile);
            this.fileContentsSize = this.fileContents.size();
        }
        catch (IOException ex)
        {
            // 読込に失敗した場合は例外を投げてフェールオーバーさせる。
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void emitBatch(long batchId, TridentCollector collector)
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("batch started. taskIndex=" + this.taskIndex + ", batchId=" + batchId);
        }

        for (int index = 0; index < this.maxBatchSize; index++)
        {
            // 末尾まで読んでしまっている場合はスキップ
            if (this.readIndex >= this.fileContentsSize)
            {
                continue;
            }

            String nowLine = this.fileContents.get(this.readIndex);
            collector.emit(new Values(nowLine));

            this.readIndex++;
            // ファイルを末尾まで読みこみ、かつ再読み込みを行う場合は読込インデックスをリセット
            if (this.readIndex >= this.fileContentsSize && this.isFileReload == true)
            {
                this.readIndex = 0;
            }
        }

        if (logger.isDebugEnabled() == true)
        {
            logger.debug("batch finished. taskIndex=" + this.taskIndex + ", batchId=" + batchId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ack(long batchId)
    {
        if (logger.isDebugEnabled() == true)
        {
            logger.debug("acked. taskIndex=" + this.taskIndex + ", batchId=" + batchId);
        }
    }

    @Override
    public void close()
    {
        // Do nothing.
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map getComponentConfiguration()
    {
        return null;
    }

    @Override
    public Fields getOutputFields()
    {
        return new Fields("text");
    }

    /**
     * @return the dataFilePath
     */
    public String getDataFilePath()
    {
        return this.dataFilePath;
    }

    /**
     * @param dataFilePath the dataFilePath to set
     */
    public void setDataFilePath(String dataFilePath)
    {
        this.dataFilePath = dataFilePath;
    }

    /**
     * @return the baseFileName
     */
    public String getBaseFileName()
    {
        return this.baseFileName;
    }

    /**
     * @param baseFileName the baseFileName to set
     */
    public void setBaseFileName(String baseFileName)
    {
        this.baseFileName = baseFileName;
    }

    /**
     * @return the isFileReload
     */
    public boolean isFileReload()
    {
        return this.isFileReload;
    }

    /**
     * @param isFileReload the isFileReload to set
     */
    public void setFileReload(boolean isFileReload)
    {
        this.isFileReload = isFileReload;
    }

    /**
     * @return the maxBatchSize
     */
    public int getMaxBatchSize()
    {
        return this.maxBatchSize;
    }

    /**
     * @param maxBatchSize the maxBatchSize to set
     */
    public void setMaxBatchSize(int maxBatchSize)
    {
        this.maxBatchSize = maxBatchSize;
    }
}
