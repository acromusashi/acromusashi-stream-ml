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

import java.io.Serializable;
import java.util.Map;

import storm.trident.operation.TridentOperationContext;

/**
 * TridentFunctionにおいて初期化を行うためのインタフェース
 * 
 * @author kimura
 */
public interface TridentInitializable extends Serializable
{
    /**
     * コンポーネントの初期化を行う。
     * 
     * @param conf Storm設定オブジェクト
     * @param context TridentContext
     */
    @SuppressWarnings("rawtypes")
    void initialize(Map conf, TridentOperationContext context);
}
