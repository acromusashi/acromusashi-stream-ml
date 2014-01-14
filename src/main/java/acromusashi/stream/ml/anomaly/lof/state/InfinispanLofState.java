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
package acromusashi.stream.ml.anomaly.lof.state;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.marshall.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acromusashi.stream.ml.anomaly.lof.entity.LofDataSet;

import com.google.common.base.Joiner;

/**
 * LOF分散用MapStateの保存先にInfinispanを用いたクラス<br>
 * 設定された「parallelismHint」の数だけ存在する。
 * 
 * @author kimura
 */
public class InfinispanLofState extends LofState
{
    /** キャッシュ上のデータ生存期間のデフォルト値 */
    private static final int                            DEFAULT_LIFESPAN = 600;

    /** logger */
    private static final Logger                         logger           = LoggerFactory.getLogger(InfinispanLofState.class);

    /** キャッシュ上のデータ生存期間(単位：秒) */
    protected int                                       lifespan         = DEFAULT_LIFESPAN;

    /** 投入先のサーバ情報 */
    protected String                                    targetServer;

    /** 投入先キャッシュ名称 */
    protected String                                    cacheName;

    /** 状態を保存するベースキー */
    protected String                                    baseKey;

    /** Remoteキャッシュマネージャ */
    protected transient RemoteCacheManager              clientManager;

    /** 状態保存用キャッシュ */
    protected transient RemoteCache<String, LofDataSet> stateCache;

    /** TransationId保存用キャッシュ */
    protected transient RemoteCache<String, Long>       txIdCache;

    /**
     * 設定値、構成情報をパラメータとしてインスタンスを生成する。
     * 
     * @param targetServer 投入先のサーバ情報
     * @param cacheName 投入先キャッシュ名称
     * @param partitionIndex 何番目かのStateかを示すインデックス
     * @param numPartitions Stateの数
     */
    public InfinispanLofState(String targetServer, String cacheName, int partitionIndex,
            int numPartitions)
    {
        super(partitionIndex, numPartitions);
        this.targetServer = targetServer;
        this.cacheName = cacheName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onInitialize()
    {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Configuration config = builder.classLoader(loader).addServers(this.targetServer).build();

        this.clientManager = new RemoteCacheManager(config, true);
        this.stateCache = this.clientManager.getCache(this.cacheName);
        this.txIdCache = this.clientManager.getCache(this.cacheName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LofDataSet getState(String baseKey, Long txId, int partitionIndex,
            boolean isSelfPartition) throws IOException
    {
        String targetKey = null;

        // 指定された条件のクラスタ状態を取得する。取得結果がnull、または不正だった場合はnullとして扱う。
        // 自分自身のパーティションと異なるパーティションの値を取得する場合、最新の状態値保存TransatctionIdを取得し用いる。
        if (isSelfPartition == true)
        {
            targetKey = Joiner.on("_").skipNulls().join(
                    Arrays.asList(baseKey, txId, partitionIndex));
        }
        else
        {
            String txIdKey = Joiner.on("_").skipNulls().join(Arrays.asList(baseKey, partitionIndex));
            Long anotherTxId = this.txIdCache.get(txIdKey);
            targetKey = Joiner.on("_").skipNulls().join(
                    Arrays.asList(baseKey, anotherTxId, partitionIndex));
        }

        LofDataSet targetValue = this.stateCache.get(targetKey);
        return targetValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void putState(String baseKey, Long txId, int partitionIndex, LofDataSet putState)
            throws IOException
    {
        this.baseKey = baseKey;
        String putKey = Joiner.on("_").skipNulls().join(
                Arrays.asList(baseKey, txId, partitionIndex));
        // ログレベルが「DEBUG」の場合は格納するオブジェクトの予測サイズを出力
        if (logger.isDebugEnabled() == true)
        {
            Marshaller marchaller = this.clientManager.getMarshaller();
            int marchalledSize = 0;

            try
            {
                marchalledSize = marchaller.objectToByteBuffer(putState).length;
            }
            catch (InterruptedException ex)
            {
                logger.warn("Object Marchall failed.", ex);
            }

            logger.debug("Infinispan put. Key=" + putKey + ", Size=" + marchalledSize);
        }

        this.stateCache.put(putKey, putState, this.lifespan, TimeUnit.SECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit(Long txid)
    {
        // commit時に自パーティションの最終保存TransactionIdを保存する。
        String putKey = Joiner.on("_").skipNulls().join(
                Arrays.asList(this.baseKey, this.partitionIndex));
        this.txIdCache.put(putKey, txid, this.lifespan, TimeUnit.SECONDS);
    }

    /**
     * @param lifespan the lifespan to set
     */
    public void setLifespan(int lifespan)
    {
        this.lifespan = lifespan;
    }
}
