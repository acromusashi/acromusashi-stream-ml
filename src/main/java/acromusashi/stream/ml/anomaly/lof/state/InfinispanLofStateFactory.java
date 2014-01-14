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

import java.util.Map;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import backtype.storm.task.IMetricsContext;

/**
 * InfinispanLofStateを生成するFactoryクラス
 * 
 * @author kimura
 */
public class InfinispanLofStateFactory implements StateFactory
{
    /** serialVersionUID */
    private static final long     serialVersionUID = 144884499303224056L;

    /** 投入先のアドレス情報 */
    protected String              targetUri;

    /** 投入先テーブル名称 */
    protected String              tableName;

    /** 状態マージのインターバル(単位：秒) */
    protected int                 mergeInterval;

    /** マージ設定 */
    protected Map<String, Object> mergeConfig;

    /** キャッシュ上にデータを保持する生存期間(単位：秒) */
    protected int                 lifespan;

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public InfinispanLofStateFactory()
    {}

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions)
    {
        InfinispanLofState resultState = new InfinispanLofState(this.targetUri, this.tableName,
                partitionIndex, numPartitions);
        if (this.mergeInterval > 0)
        {
            resultState.setMergeInterval(this.mergeInterval);
        }

        if (this.lifespan > 0)
        {
            resultState.setLifespan(this.lifespan);
        }

        if (this.mergeConfig != null)
        {
            resultState.setMergeConfig(this.mergeConfig);
        }

        resultState.initialize();
        return resultState;
    }

    /**
     * @param targetUri the targetUri to set
     */
    public void setTargetUri(String targetUri)
    {
        this.targetUri = targetUri;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName)
    {
        this.tableName = tableName;
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

    /**
     * @param lifespan the lifespan to set
     */
    public void setLifespan(int lifespan)
    {
        this.lifespan = lifespan;
    }
}
