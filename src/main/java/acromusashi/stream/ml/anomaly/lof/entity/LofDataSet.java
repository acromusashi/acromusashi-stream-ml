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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Local Outlier Factorの学習モデルを保持するエンティティクラス
 * 
 * @author kimura
 */
public class LofDataSet implements Serializable
{
    /** serialVersionUID */
    private static final long     serialVersionUID = 7589332742661081169L;

    /** 保持するデータIDのリスト */
    private List<String>          dataIdList       = new ArrayList<>();

    /** 保持するデータID>データ値のマッピング */
    private Map<String, LofPoint> dataMap          = new HashMap<>();

    /**
     * パラメータを指定せずにインスタンスを生成する。
     */
    public LofDataSet()
    {}

    /**
     * 保持するデータIDのリスト/データマッピングに値を追加する。
     * 
     * @param addedPoint 追加対象点
     */
    public void addData(LofPoint addedPoint)
    {
        this.dataIdList.add(addedPoint.getDataId());
        this.dataMap.put(addedPoint.getDataId(), addedPoint);
    }

    /**
     * 保持するデータIDのリスト/データマッピングに値を追加する。
     * 
     * @param deleteDataId 追加対象点
     */
    public void deleteData(String deleteDataId)
    {
        this.dataIdList.remove(deleteDataId);
        this.dataMap.remove(deleteDataId);
    }

    /**
     * 対象エンティティのDeepCopyを作成する。
     * 
     * @return 対象エンティティのDeepCopy
     */
    public LofDataSet deepCopy()
    {
        LofDataSet result = new LofDataSet();
        result.getDataIdList().addAll(this.dataIdList);
        Collection<LofPoint> pointList = this.dataMap.values();
        for (LofPoint targetPoint : pointList)
        {
            result.getDataMap().put(targetPoint.getDataId(), targetPoint.deepCopy());
        }

        return result;
    }

    /**
     * @return the dataIdList
     */
    public List<String> getDataIdList()
    {
        return this.dataIdList;
    }

    /**
     * @param dataIdList the dataIdList to set
     */
    public void setDataIdList(List<String> dataIdList)
    {
        this.dataIdList = dataIdList;
    }

    /**
     * @return the dataMap
     */
    public Map<String, LofPoint> getDataMap()
    {
        return this.dataMap;
    }

    /**
     * @param dataMap the dataMap to set
     */
    public void setDataMap(Map<String, LofPoint> dataMap)
    {
        this.dataMap = dataMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String result = ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        return result;
    }
}
