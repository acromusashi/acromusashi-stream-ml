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
package acromusashi.stream.ml.common.state;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.state.ValueUpdater;
import storm.trident.state.map.MapState;

/**
 * 機械学習の結果を外部のデータストアに保持するベースクラス抽象クラス<br>
 * 設定された「parallelismHint」の数だけ存在する。<br>
 * <br>
 * beginCommit/multiGet/multiPut/commitメソッドは以下の順で実行される。<br>
 * 尚、下記のメソッドは「TridentTransation実行時、SpoutがTupleをemitした場合」のみ呼び出される。<br>
 * Spoutからデータがemitされなかった場合はbeginCommitメソッドも含めてメソッドの呼び出しは行われない。
 * <ol>
 * <li>beginCommit:状態更新処理開始時に実行される。</li>
 * <li>multiGet:状態更新処理中の初期状態取得時にUpdatorから実行される。</li>
 * <li>multiPut:状態更新処理中の結果反映時にUpdatorから実行される。</li>
 * <li>commit:状態更新処理終了時に実行される。</li>
 * </ol>
 * 
 * @author kimura
 * 
 * @param <T> Stateとして保持するクラス型
 */
public abstract class MlBaseState<T> implements MapState<T>
{
    /** 状態マージインターバルのデフォルト値(秒) */
    public static final int       DEFAULT_MERGE_INTERVAL = 30;

    /** logger */
    private static final Logger   logger                 = LoggerFactory.getLogger(MlBaseState.class);

    /** パーティションID */
    protected int                 partitionIndex;

    /** 全体のパーティション数 */
    protected int                 numPartitions;

    /** 状態マージのインターバル(単位：秒) */
    protected int                 mergeInterval          = DEFAULT_MERGE_INTERVAL;

    /** マージ設定 */
    protected Map<String, Object> mergeConfig;

    /** 実行中のトランザクションID */
    protected Long                txId;

    /** 前回状態を保存したトランザクションID */
    protected Long                previousSaveTxId;

    /** 前回のマージ実行時刻 */
    protected long                previousMergeTime      = 0;

    /**
     * 構成情報をパラメータとしてインスタンスを生成する。
     * 
     * @param partitionIndex 何番目かのStateかを示すインデックス
     * @param numPartitions Stateの数
     */
    public MlBaseState(int partitionIndex, int numPartitions)
    {
        this.partitionIndex = partitionIndex;
        this.numPartitions = numPartitions;
    }

    /**
     * 共通初期化処理を行う
     */
    public void initialize()
    {
        // 前回マージ時刻初期化
        this.previousMergeTime = getCurrentTime();

        // マージ設定が存在しない場合は空マップを生成
        if (this.mergeConfig == null)
        {
            this.mergeConfig = new HashMap<>();
        }

        // 個別初期化処理を行う。
        onInitialize();
    }

    /**
     * 個別初期化処理を行う。
     */
    protected abstract void onInitialize();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> multiGet(List<List<Object>> keys)
    {
        // keysにはStateBaseNameの名称のみが指定される。そのため、1要素目を用いればいい。
        String baseKey = (String) keys.get(0).get(0);

        // 前回のクラスタ実行結果を取得する。存在しない場合は空値として扱う。
        List<T> dataModels = new ArrayList<>();
        T dataModel = null;

        Long previousTxId = null;
        if (this.previousSaveTxId == null)
        {
            // クエリによってtxIdが存在しない状態で本メソッドが呼ばれる可能性があるため、nullの場合は補正を行う。
            if (this.txId == null)
            {
                previousTxId = 0L;
            }
            else
            {
                previousTxId = this.txId - 1;
            }
        }
        else
        {
            previousTxId = this.previousSaveTxId;
        }

        try
        {
            dataModel = getState(baseKey, previousTxId, this.partitionIndex, true);
        }
        catch (IOException ex)
        {
            logger.warn("State get failed. BaseKey=" + baseKey + " ,txId=" + previousTxId
                    + " ,partitionIndex=" + this.partitionIndex, ex);
        }

        // 取得できなかった場合は空リストを返す
        if (dataModel == null)
        {
            return dataModels;
        }

        // 前回実行結果が存在し、前回の状態マージから一定時刻が経過していた場合、他のパーティションとのマージ処理を実行
        if (isExecuteMerge() == true)
        {
            for (int nowIndex = 0; nowIndex < this.numPartitions; nowIndex++)
            {
                // 自分と同じインデックスの場合は省略
                if (nowIndex == this.partitionIndex)
                {
                    continue;
                }

                T otherDataSet = null;
                try
                {
                    otherDataSet = getState(baseKey, this.previousSaveTxId, nowIndex, false);
                }
                catch (IOException ex)
                {
                    logger.warn("MergeTargetState get failed. BaseKey=" + baseKey + " ,txId="
                            + previousTxId + " ,partitionIndex=" + nowIndex, ex);
                }

                // 前回の他パーティションの結果が存在した場合、順次マージを行う
                if (otherDataSet != null)
                {
                    dataModel = mergeState(dataModel, otherDataSet, this.mergeConfig);

                    // マージ実行メッセージを出力
                    if (logger.isDebugEnabled() == true)
                    {
                        logger.debug("Merge Executed. PartitionIndex=" + this.partitionIndex
                                + ", MergeTransactionId=" + previousTxId);
                    }
                }
            }

            this.previousMergeTime = getCurrentTime();
        }

        dataModels.add(dataModel);
        return dataModels;
    }

    /**
     * マージを実行するかの判定を行う。<br>
     * 前回のマージ時刻から「状態マージ間隔」以上の時間が経過していた場合、マージ実行と判定する。
     * 
     * @return 状態マージを実行する場合true、行わない場合false
     */
    protected boolean isExecuteMerge()
    {
        long elapsedTimeMs = getCurrentTime() - this.previousMergeTime;
        if (elapsedTimeMs >= TimeUnit.SECONDS.toMillis(this.mergeInterval))
        {
            return true;
        }

        return false;
    }

    /**
     * 指定された条件のデータモデルを取得する。取得結果がnull、または不正だった場合はnullとして扱う。
     * 
     * @param baseKey ベースキー値
     * @param txId TransationId
     * @param partitionIndex StateIndex
     * @param isSelfPartition 取得対象のIndexが自分自身のパーティションと同一か
     * @return 指定された条件の学習モデル
     * @throws IOException 取得に失敗した場合
     */
    protected abstract T getState(String baseKey, Long txId, int partitionIndex,
            boolean isSelfPartition) throws IOException;

    /**
     * データモデルのマージを行う。<br>
     * ベースにしたデータモデルに指定したデータモデルをマージする。
     * 
     * @param baseDataSet マージ元データモデル
     * @param targetDataSet マージ対象データモデル
     * @param mergeConfig マージ設定
     * @return マージ元データモデルにマージ対象データモデルをマージした結果データモデル
     */
    protected abstract T mergeState(T baseDataSet, T targetDataSet, Map<String, Object> mergeConfig);

    /**
     * 指定された条件でデータモデルを投入する。
     * 
     * @param baseKey ベースキー値
     * @param txId TransationId
     * @param partitionIndex StateIndex
     * @param putState 投入学習モデル
     * @throws IOException 投入に失敗した場合
     */
    protected abstract void putState(String baseKey, Long txId, int partitionIndex, T putState)
            throws IOException;

    /**
     * {@inheritDoc}
     */
    @Override
    public void beginCommit(Long txid)
    {
        // Stateに対するTransactionCommit開始時に実行
        this.txId = txid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit(Long txid)
    {
        // Do nothing.
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<T> multiUpdate(List<List<Object>> keys, List<ValueUpdater> updaters)
    {
        // 使用されないため、実装は行わない。
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void multiPut(List<List<Object>> keys, List<T> results)
    {
        // keysにはStateBaseNameの名称のみが指定される。そのため、1要素目を用いればいい。
        String baseKey = (String) keys.get(0).get(0);
        T putDataSet = results.get(0);

        try
        {
            putState(baseKey, this.txId, this.partitionIndex, putDataSet);
            this.previousSaveTxId = this.txId;
        }
        catch (IOException ex)
        {
            logger.warn("State put failed. BaseKey=" + baseKey + " ,txId=" + this.txId
                    + " ,partitionIndex=" + this.partitionIndex, ex);
        }
    }

    /**
     * 現在の時刻値を取得する。
     * 
     * @return 現在の時刻値
     */
    protected long getCurrentTime()
    {
        return System.currentTimeMillis();
    }

    /**
     * @param mergeInterval the mergeInterval to set
     */
    public void setMergeInterval(int mergeInterval)
    {
        this.mergeInterval = mergeInterval;
    }

    /**
     * @param mergeConfig the mergeConfig to set
     */
    public void setMergeConfig(Map<String, Object> mergeConfig)
    {
        this.mergeConfig = mergeConfig;
    }
}
