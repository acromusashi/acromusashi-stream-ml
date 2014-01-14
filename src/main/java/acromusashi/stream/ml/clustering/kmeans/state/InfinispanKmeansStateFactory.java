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
package acromusashi.stream.ml.clustering.kmeans.state;

import java.util.Map;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import backtype.storm.task.IMetricsContext;

/**
 * KmeansInfinispanStateを生成するFactoryクラス
 * 
 * @author kimura
 */
public class InfinispanKmeansStateFactory implements StateFactory
{
    /** serialVersionUID */
    private static final long serialVersionUID = 8164388287299564946L;

    /** RemoteキャッシュのURL(host1:port2;host:port2...) */
    private String            servers;

    /** Remoteキャッシュ名称 */
    private String            cacheName;

    /** 状態マージを行うインターバル(単位：秒) */
    private int               mergeInterval;

    /** キャッシュ上にデータを保持する生存期間(単位：秒) */
    private int               lifespan;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public InfinispanKmeansStateFactory()
    {}

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions)
    {
        InfinispanKmeansState resultState = new InfinispanKmeansState(partitionIndex,
                numPartitions, this.servers, this.cacheName);
        if (this.mergeInterval > 0)
        {
            resultState.setMergeInterval(this.mergeInterval);
        }

        if (this.lifespan > 0)
        {
            resultState.setLifespan(this.lifespan);
        }

        resultState.initialize();
        return resultState;
    }

    /**
     * @return the servers
     */
    public String getServers()
    {
        return this.servers;
    }

    /**
     * @param servers the servers to set
     */
    public void setServers(String servers)
    {
        this.servers = servers;
    }

    /**
     * @return the cacheName
     */
    public String getCacheName()
    {
        return this.cacheName;
    }

    /**
     * @param cacheName the cacheName to set
     */
    public void setCacheName(String cacheName)
    {
        this.cacheName = cacheName;
    }

    /**
     * @return the mergeInterval
     */
    public int getMergeInterval()
    {
        return this.mergeInterval;
    }

    /**
     * @param mergeInterval the mergeInterval to set
     */
    public void setMergeInterval(int mergeInterval)
    {
        this.mergeInterval = mergeInterval;
    }

    /**
     * @return the lifespan
     */
    public int getLifespan()
    {
        return this.lifespan;
    }

    /**
     * @param lifespan the lifespan to set
     */
    public void setLifespan(int lifespan)
    {
        this.lifespan = lifespan;
    }
}
