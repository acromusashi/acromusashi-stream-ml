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

import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import storm.trident.operation.TridentOperationContext;
import storm.trident.state.map.MapState;
import acromusashi.stream.ml.anomaly.lof.entity.LofDataSet;
import acromusashi.stream.ml.anomaly.lof.entity.LofResult;
import acromusashi.stream.ml.common.notify.ResultNotifier;

/**
 * LofUpdaterのテストクラス
 * 
 * @author kimura
 */
@RunWith(MockitoJUnitRunner.class)
public class LofUpdaterTest
{
    /** テスト対象 */
    private LofUpdater                 target = new LofUpdater();

    /** テスト用State */
    @Mock
    private MapState<LofDataSet>       mockState;

    /** テスト用のStormConfigMap */
    @SuppressWarnings("rawtypes")
    @Mock
    private Map                        mockConfMap;

    /** テスト用のTridentOperationContext */
    @Mock
    private TridentOperationContext    mockContext;

    /** テスト用Notifier(データ) */
    @Mock
    private ResultNotifier<LofResult>  mockDataNotifier;

    /** テスト用Notifier(バッチ) */
    @Mock
    private ResultNotifier<LofDataSet> mockBatchNotifier;

    /**
     * 初期化メソッド
     */
    @Before
    public void setUp()
    {

    }

    /**
     * 拡張ポイントにオブジェクトが設定されていない状態で初期化処理を実行する。
     * 
     * @target {@link LofUpdater#prepare(java.util.Map, storm.trident.operation.TridentOperationContext)}
     * @test 拡張ポイントが存在しないままであることを確認
     *    condition::拡張ポイントにオブジェクトが設定されていない状態で初期化処理を実行
     *    result::拡張ポイントが存在しないままであることを確認
     */
    @Test
    public void testPrepare_拡張ポイント未設定()
    {
        // 実施
        this.target.prepare(this.mockConfMap, this.mockContext);

        // 検証
        assertNull(this.target.dataNotifier);
        assertNull(this.target.batchNotifier);
    }

    /**
     * 拡張ポイントにオブジェクトが設定された状態で初期化処理を実行する。
     * 
     * @target {@link LofUpdater#prepare(java.util.Map, storm.trident.operation.TridentOperationContext)}
     * @test 拡張ポイントの初期化が実行されることを確認
     *    condition::拡張ポイントにオブジェクトが設定された状態で初期化処理を実行
     *    result::拡張ポイントの初期化が実行されることを確認
     */
    @Test
    public void testPrepare_拡張ポイント設定()
    {
        // 準備
        this.target.setDataNotifier(this.mockDataNotifier);
        this.target.setBatchNotifier(this.mockBatchNotifier);

        // 実施
        this.target.prepare(this.mockConfMap, this.mockContext);

        // 検証
        Mockito.verify(this.mockDataNotifier).initialize(this.mockConfMap, this.mockContext);
        Mockito.verify(this.mockBatchNotifier).initialize(this.mockConfMap, this.mockContext);;
    }

    /**
     * データ数が
     * 
     * @target {@link LofUpdater#receivePoint(acromusashi.stream.ml.anomaly.lof.entity.LofPoint, LofDataSet)}
     * @test 拡張ポイントの初期化が実行されることを確認
     *    condition::拡張ポイントにオブジェクトが設定された状態で初期化処理を実行
     *    result::拡張ポイントの初期化が実行されることを確認
     */
    @Test
    public void testReceivePoint_データ数最低閾値未満()
    {
        // 準備

        // 実施
        this.target.setDataNotifier(this.mockDataNotifier);
        this.target.setBatchNotifier(this.mockBatchNotifier);
        this.target.prepare(this.mockConfMap, this.mockContext);

        // 検証
        Mockito.verify(this.mockDataNotifier).initialize(this.mockConfMap, this.mockContext);
        Mockito.verify(this.mockBatchNotifier).initialize(this.mockConfMap, this.mockContext);;
    }
}
