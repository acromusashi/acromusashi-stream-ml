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

/**
 * LOF関連設定項目キー
 * 
 * @author kimura
 */
public interface LofConfKey
{
    /** ベース状態名称 */
    String STATE_BASE_NAME     = "lof.state.base.name";

    /** 中間状態を保持するか */
    String HAS_INTERMEDIATE    = "lof.has.intermediate";

    /** データ受信時、常時学習データモデルを更新するか */
    String ALWAYS_UPDATE_MODEL = "lof.always.update.model";

    /** 学習モデルを更新する頻度 */
    String UPDATE_INTERVAL     = "lof.update.interval";

    /** K値 */
    String KN                  = "lof.kn";

    /** 学習データモデルが実際にLOF算出を行う際に必要な最小データ数 */
    String MIN_DATA_COUNT      = "lof.min.data.count";

    /** 学習データモデルが最大で保持するデータ数 */
    String MAX_DATA_COUNT      = "lof.max.data.count";

    /** 通知を行うLOF閾値 */
    String NOTITY_THRESHOLD    = "lof.notify.threshold";
}
