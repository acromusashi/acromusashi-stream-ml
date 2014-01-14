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
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
public class WatchTextBatchSpout implements IBatchSpout
{
    /** serialVersionUID */
    private static final long      serialVersionUID = -5097002059382826053L;

    /** logger */
    private static final Logger    logger           = LoggerFactory.getLogger(WatchTextBatchSpout.class);

    /** データファイル配置ディレクトリ */
    private String                 dataFileDir;

    /** ベースファイル名 */
    private String                 baseFileName;

    /** TaskIndex */
    private int                    taskIndex;

    /** ファイル名称 */
    private String                 dataFileName;

    /** 対象ファイル */
    private File                   targetFile       = null;

    /** 対象ファイルの更新を監視するWatchService */
    private transient WatchService watcherService   = null;

    /** 対象ファイルの更新を監視するWatcherオブジェクト */
    private transient WatchKey     watchKey         = null;

    /** 初回読み込みを行ったか */
    boolean                        isInitialReaded  = false;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public WatchTextBatchSpout()
    {}

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"rawtypes"})
    @Override
    public void open(Map conf, TopologyContext context)
    {
        this.taskIndex = context.getThisTaskIndex();
        this.dataFileName = this.baseFileName + "_" + this.taskIndex;
        this.targetFile = new File(this.dataFileDir, this.dataFileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void emitBatch(long batchId, TridentCollector collector)
    {
        try
        {
            checkDataFile(collector);
        }
        catch (Exception ex)
        {
            String logFormat = "Check file failed, skip batch. FilePath={0}";
            logger.warn(MessageFormat.format(logFormat, this.targetFile.getAbsolutePath()), ex);
        }
    }

    /**
     * ファイルのチェックを行い、初回は読み込みを行い、以後は更新された場合にのみデータを取得する。
     * 
     * @param collector Collector
     * @throws IOException ファイル入出力エラー発生時
     * @throws InterruptedException 割り込み例外発生時
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void checkDataFile(TridentCollector collector) throws IOException,
            InterruptedException
    {
        // 初回は更新がない場合でも読みこみを行い、その上でファイル更新監視を開始する。
        if (this.isInitialReaded == false)
        {
            List<String> fileContents = FileUtils.readLines(this.targetFile);
            emitTuples(fileContents, collector);
            this.isInitialReaded = true;

            // ファイル更新監視を開始
            Path dirPath = new File(this.dataFileDir).toPath();
            FileSystem fileSystem = dirPath.getFileSystem();
            this.watcherService = fileSystem.newWatchService();
            this.watchKey = dirPath.register(this.watcherService, new Kind[]{
                    StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY});

            return;
        }

        // ファイル更新が行なわれているかを確認する。
        WatchKey detectedKey = this.watcherService.poll(1, TimeUnit.SECONDS);

        // ファイルの更新イベントが取得できなかった場合、または監視対象のイベントではない場合、終了する。
        if (detectedKey == null || detectedKey.equals(this.watchKey) == false)
        {
            return;
        }

        try
        {
            // ファイル更新イベントが存在した場合、中身のファイルを読み込んで送信し、イベントの受付を再開する。
            for (WatchEvent event : detectedKey.pollEvents())
            {

                Path filePath = (Path) event.context();

                // 同一ディレクトリ配下の別名ファイルの場合は読み込みを行わない。
                if (filePath == null
                        || this.targetFile.toPath().getFileName().equals(filePath.getFileName()) == false)
                {
                    continue;
                }

                List<String> fileContents = FileUtils.readLines(this.targetFile);
                emitTuples(fileContents, collector);
            }
        }
        finally
        {
            detectedKey.reset();
        }
    }

    /**
     * 指定した内容のTupleを送信する。
     * 
     * @param fileLines ファイルの内容
     * @param collector Collector
     */
    protected void emitTuples(List<String> fileLines, TridentCollector collector)
    {
        for (String targetLine : fileLines)
        {
            collector.emit(new Values(targetLine));
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
        return this.dataFileDir;
    }

    /**
     * @param dataFilePath the dataFilePath to set
     */
    public void setDataFilePath(String dataFilePath)
    {
        this.dataFileDir = dataFilePath;
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
}
