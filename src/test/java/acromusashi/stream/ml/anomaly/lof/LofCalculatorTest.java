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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import acromusashi.stream.ml.anomaly.lof.entity.KDistanceResult;
import acromusashi.stream.ml.anomaly.lof.entity.LofDataSet;
import acromusashi.stream.ml.anomaly.lof.entity.LofPoint;

/**
 * LofCalculatorのテストクラス
 * 
 * @author kimura
 */
public class LofCalculatorTest
{
    /** 試験用データファイル配置ディレクトリ*/
    private static final String DATA_DIR = "src/test/resources/"
                                                 + StringUtils.replaceChars(
                                                         LofCalculatorTest.class.getPackage().getName(),
                                                         '.', '/') + '/';

    /**
     * K値3、データ数5の状態で点集合の中に位置するデータを渡した場合にLOF値が小さく算出可能であることを確認する。
     * 
     * @target {@link LofCalculator#calculateLofNoIntermediate(int, LofPoint, LofDataSet)}
     * @test LOF値が小さく算出可能であること
     *    condition::K値3、データ数5でLOF値算出を実行
     *    result::LOF値が小さく算出可能であること
     */
    @Test
    public void testCalculateLofNoIntermediate_K3算出_LOF値小()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {2.0d, 2.0d, 2.0d};
        point6.setDataPoint(points6);

        double expectedLof = 1.05108d;

        // 実施
        double actualLof = LofCalculator.calculateLofNoIntermediate(3, point6, dataSet);

        // 検証
        assertEquals(expectedLof, actualLof, 0.01d);
    }

    /**
     * K値3、データ数5の状態で点集合から外れた場所に位置するデータを渡した場合にLOF値が大きく算出可能であることを確認する。
     * 
     * @target {@link LofCalculator#calculateLofNoIntermediate(int, LofPoint, LofDataSet)}
     * @test LOF値が大きく算出可能であること
     *    condition::K値3、データ数5でLOF値算出を実行
     *    result::LOF値が大きく算出可能であること
     */
    @Test
    public void testCalculateLofNoIntermediate_K3算出_LOF値大()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {10.0d, 10.0d, 10.0d};
        point6.setDataPoint(points6);

        double expectedLof = 5.97670d;

        // 実施
        double actualLof = LofCalculator.calculateLofNoIntermediate(3, point6, dataSet);

        // 検証
        assertEquals(expectedLof, actualLof, 0.01d);
    }

    /**
     * K値3、Max6、データ数5の状態で点集合の中に位置するデータを渡した場合にLOF値が小さく算出可能であることを確認する。
     * 
     * @target {@link LofCalculator#calculateLofNoIntermediate(int, LofPoint, LofDataSet)}
     * @test LOF値が小さく算出可能であること
     *    condition::K値3、Max6、データ数5でLOF値算出を実行
     *    result::LOF値が小さく算出可能であること
     */
    @Test
    public void testCalculateLofNoIntermediate_K3算出_データ構造更新_削除無_LOF値小()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {2.0d, 2.0d, 2.0d};
        point6.setDataPoint(points6);
        LofCalculator.addPointToDataSet(6, point6, dataSet);

        double expectedLof = 1.28937d;

        // 実施
        double actualLof = LofCalculator.calculateLofNoIntermediate(3, point6, dataSet);

        // 検証
        assertEquals(expectedLof, actualLof, 0.01d);
    }

    /**
     * K値3、Max6、データ数5の状態で点集合から外れた場所に位置するデータを渡した場合にLOF値が大きく算出可能であることを確認する。
     * 
     * @target {@link LofCalculator#calculateLofNoIntermediate(int, LofPoint, LofDataSet)}
     * @test LOF値が大きく算出可能であること
     *    condition::K値3、Max6、データ数5でLOF値算出を実行
     *    result::LOF値が大きく算出可能であること
     */
    @Test
    public void testCalculateLofNoIntermediate_K3算出_データ構造更新_削除無_LOF値大()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {10.0d, 10.0d, 10.0d};
        point6.setDataPoint(points6);
        LofCalculator.addPointToDataSet(6, point6, dataSet);

        double expectedLof = 5.97670d;

        // 実施
        double actualLof = LofCalculator.calculateLofNoIntermediate(3, point6, dataSet);

        // 検証
        assertEquals(expectedLof, actualLof, 0.01d);
    }

    /**
     * K値3、Max5、データ数5の状態で点集合の中に位置するデータを渡した場合にLOF値が小さく算出可能であることを確認する。
     * 
     * @target {@link LofCalculator#calculateLofNoIntermediate(int, LofPoint, LofDataSet)}
     * @test LOF値が小さく算出可能であること
     *    condition::K値3、Max5、データ数5でLOF値算出を実行
     *    result::LOF値が小さく算出可能であること
     */
    @Test
    public void testCalculateLofNoIntermediate_K3算出_データ構造更新_削除有_LOF値小()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {2.0d, 2.0d, 2.0d};
        point6.setDataPoint(points6);
        LofCalculator.addPointToDataSet(5, point6, dataSet);

        double expectedLof = 1.16216d;

        // 実施
        double actualLof = LofCalculator.calculateLofNoIntermediate(3, point6, dataSet);

        // 検証
        assertEquals(expectedLof, actualLof, 0.01d);
    }

    /**
     * K値3、Max5、データ数5の状態で点集合から外れた場所に位置するデータを渡した場合にLOF値が大きく算出可能であることを確認する。
     * 
     * @target {@link LofCalculator#calculateLofNoIntermediate(int, LofPoint, LofDataSet)}
     * @test LOF値が大きく算出可能であること
     *    condition::K値3、Max5、データ数5でLOF値算出を実行
     *    result::LOF値が大きく算出可能であること
     */
    @Test
    public void testCalculateLofNoIntermediate_K3算出_データ構造更新_削除有_LOF値大()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {10.0d, 10.0d, 10.0d};
        point6.setDataPoint(points6);
        LofCalculator.addPointToDataSet(5, point6, dataSet);

        double expectedLof = 5.47199d;

        // 実施
        double actualLof = LofCalculator.calculateLofNoIntermediate(3, point6, dataSet);

        // 検証
        assertEquals(expectedLof, actualLof, 0.01d);
    }

    /**
     * Maxが6、データ数が5の状態でデータを追加した場合にデータが削除されないこと。
     * 
     * @target {@link LofCalculator#addPointToDataSet(int, LofPoint, LofDataSet)}
     * @test データを追加した場合にデータが削除されないことを確認する
     *    condition::Maxが6、データ数が5の状態でデータを追加した場合
     *    result::データを追加した場合にデータが削除されないことを確認する
     */
    @Test
    public void testAddPointToDataSet_Max6_データ数5()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {10.0d, 10.0d, 10.0d};
        point6.setDataPoint(points6);

        // 実施
        String actualId = LofCalculator.addPointToDataSet(6, point6, dataSet);

        // 検証
        assertNull(actualId);
    }

    /**
     * Maxが5、データ数が5の状態でデータを追加した場合にデータが削除されること。
     * 
     * @target {@link LofCalculator#addPointToDataSet(int, LofPoint, LofDataSet)}
     * @test データを追加した場合にデータが削除されることを確認する
     *    condition::Maxが5、データ数が5の状態でデータを追加した場合
     *    result::データを追加した場合にデータが削除されることを確認する
     */
    @Test
    public void testAddPointToDataSet_Max5_データ数5()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {10.0d, 10.0d, 10.0d};
        point6.setDataPoint(points6);

        // 実施
        String actualId = LofCalculator.addPointToDataSet(5, point6, dataSet);

        // 検証
        assertEquals("point1", actualId);
    }

    /**
     * K値3、データ数5の状態で点集合の中に位置するデータを渡した場合にLOF値が小さく算出可能であることを確認する。
     * 
     * @target {@link LofCalculator#calculateLofWithoutUpdate(int, LofPoint, LofDataSet)}
     * @test LOF値が小さく算出可能であること
     *    condition::K値3、データ数5でLOF値算出を実行
     *    result::LOF値が小さく算出可能であること
     */
    @Test
    public void testCalculateLofWithoutUpdate_K3算出_LOF値小()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {2.0d, 2.0d, 2.0d};
        point6.setDataPoint(points6);
        LofCalculator.initDataSet(3, dataSet);

        double expectedLof = 1.05108d;

        // 実施
        double actualLof = LofCalculator.calculateLofWithoutUpdate(3, point6, dataSet);

        // 検証
        assertEquals(expectedLof, actualLof, 0.01d);
    }

    /**
     * K値3、データ数5の状態で点集合から外れた場所に位置するデータを渡した場合にLOF値が大きく算出可能であることを確認する。
     * 
     * @target {@link LofCalculator#calculateLofWithoutUpdate(int, LofPoint, LofDataSet)}
     * @test LOF値が大きく算出可能であること
     *    condition::K値3、データ数5でLOF値算出を実行
     *    result::LOF値が大きく算出可能であること
     */
    @Test
    public void testCalculateLofWithoutUpdate_K3算出_LOF値大()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {10.0d, 10.0d, 10.0d};
        point6.setDataPoint(points6);
        LofCalculator.initDataSet(3, dataSet);

        double expectedLof = 5.97670d;

        // 実施
        double actualLof = LofCalculator.calculateLofWithoutUpdate(3, point6, dataSet);

        // 検証
        assertEquals(expectedLof, actualLof, 0.01d);
    }

    /**
     * K値3、Max6、データ数5の状態で点集合の中に位置するデータを渡した場合にLOF値が小さく算出可能であることを確認する。
     * 
     * @target {@link LofCalculator#calculateLofWithUpdate(int, int, LofPoint, LofDataSet)}
     * @test LOF値が小さく算出可能であること
     *    condition::K値3、Max6、データ数5でLOF値算出を実行
     *    result::LOF値が小さく算出可能であること
     */
    @Test
    public void testCalculateLofWithUpdate_K3算出_データ構造更新_削除無_LOF値小()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {2.0d, 2.0d, 2.0d};
        point6.setDataPoint(points6);
        LofCalculator.initDataSet(3, dataSet);

        double expectedLof = 1.28937d;

        // 実施
        double actualLof = LofCalculator.calculateLofWithUpdate(3, 6, point6, dataSet);

        // 検証
        assertEquals(expectedLof, actualLof, 0.01d);
    }

    /**
     * K値3、Max6、データ数5の状態で点集合から外れた場所に位置するデータを渡した場合にLOF値が大きく算出可能であることを確認する。
     * 
     * @target {@link LofCalculator#calculateLofWithUpdate(int, int, LofPoint, LofDataSet)}
     * @test LOF値が大きく算出可能であること
     *    condition::K値3、Max6、データ数5でLOF値算出を実行
     *    result::LOF値が大きく算出可能であること
     */
    @Test
    public void testCalculateLofWithUpdate_K3算出_データ構造更新_削除無_LOF値大()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {10.0d, 10.0d, 10.0d};
        point6.setDataPoint(points6);
        LofCalculator.initDataSet(3, dataSet);

        double expectedLof = 5.97670d;

        // 実施
        double actualLof = LofCalculator.calculateLofWithUpdate(3, 6, point6, dataSet);

        // 検証
        assertEquals(expectedLof, actualLof, 0.01d);
    }

    /**
     * K値3、Max5、データ数5の状態で点集合の中に位置するデータを渡した場合にLOF値が小さく算出可能であることを確認する。
     * 
     * @target {@link LofCalculator#calculateLofWithUpdate(int, int, LofPoint, LofDataSet)}
     * @test LOF値が小さく算出可能であること
     *    condition::K値3、Max5、データ数5でLOF値算出を実行
     *    result::LOF値が小さく算出可能であること
     */
    @Test
    public void testCalculateLofWithUpdate_K3算出_データ構造更新_削除有_LOF値小()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {2.0d, 2.0d, 2.0d};
        point6.setDataPoint(points6);
        LofCalculator.initDataSet(3, dataSet);

        double expectedLof = 1.16216d;

        // 実施
        double actualLof = LofCalculator.calculateLofWithUpdate(3, 5, point6, dataSet);

        // 検証
        assertEquals(expectedLof, actualLof, 0.01d);
    }

    /**
     * K値3、Max5、データ数5の状態で点集合から外れた場所に位置するデータを渡した場合にLOF値が大きく算出可能であることを確認する。
     * 
     * @target {@link LofCalculator#calculateLofWithUpdate(int, int, LofPoint, LofDataSet)}
     * @test LOF値が大きく算出可能であること
     *    condition::K値3、Max5、データ数5でLOF値算出を実行
     *    result::LOF値が大きく算出可能であること
     */
    @Test
    public void testCalculateLofWithUpdate_K3算出_データ構造更新_削除有_LOF値大()
    {
        // 準備 
        LofDataSet dataSet = createDefaultDataSet();
        // 判定対象データ
        LofPoint point6 = new LofPoint();
        point6.setDataId("point6");
        double[] points6 = {10.0d, 10.0d, 10.0d};
        point6.setDataPoint(points6);
        LofCalculator.initDataSet(3, dataSet);

        double expectedLof = 5.47199d;

        // 実施
        double actualLof = LofCalculator.calculateLofWithUpdate(3, 5, point6, dataSet);

        // 検証
        assertEquals(expectedLof, actualLof, 0.01d);
    }

    /**
     * 学習モデル同士のマージが行われることを確認する。
     * 
     * @target {@link LofCalculator#mergeDataSet(LofDataSet, LofDataSet, int)}
     * @test 学習モデル同士のマージが行われること
     *    condition::データ数5個の学習モデル同士（1データ重複)を最大値8個でマージ
     *    result::学習モデル同士のマージが行われ、データが古い方から1個削除され、重複が含まれないこと
     */
    @Test
    public void testMergeDataSet_データマージ確認()
    {
        // 準備 
        LofDataSet baseDataSet = createDefaultDataSet();
        LofDataSet anotherDataSet = createAnotherDataSet();

        // 実施
        LofDataSet actual = LofCalculator.mergeDataSet(baseDataSet, anotherDataSet, 8);

        // 検証
        assertEquals(8, actual.getDataIdList().size());
        assertFalse(actual.getDataIdList().contains("point1"));
    }

    /**
     * K距離が算出可能であることを確認する
     * 
     * @target {@link LofCalculator#calculateKDistance(int, LofPoint, LofDataSet)}
     * @test K距離が算出可能であること
     *    condition::K=3、データ数5でK値算出を実行
     *    result::K距離が算出可能であること
     */
    @Test
    public void testCalculateKDistance_K距離算出()
    {
        // 準備 
        LofDataSet baseDataSet = createDefaultDataSet();
        KDistanceResult expected = new KDistanceResult();
        expected.setkDistance(2.44948d);
        expected.setkDistanceNeighbor(Arrays.asList("point4", "point5", "point2"));

        // 実施
        KDistanceResult actual = LofCalculator.calculateKDistance(3,
                baseDataSet.getDataMap().get("point1"), baseDataSet);

        // 検証
        assertEquals(expected.getkDistanceNeighbor().toString(),
                actual.getkDistanceNeighbor().toString());
        assertEquals(expected.getkDistance(), actual.getkDistance(), 0.01d);
    }

    /**
     * データ数100でK値を設定した場合のLOF値算出結果を確認する。
     * 
     * @target {@link LofCalculator#calculateLofNoIntermediate(int, LofPoint, LofDataSet)}
     * @test データ数100で算出可能であること
     *    condition::データ数100でK値を設定した場合
     *    result::データ数100で算出可能であること
     */
    @Test
    public void testCalculateLofNoIntermediate_サンプルデータ算出() throws IOException
    {
        // 準備 
        String filePath = DATA_DIR + "LofCalculatorTest_TestSample.txt";
        File targetFile = new File(filePath);
        List<String> fileContents = FileUtils.readLines(targetFile);
        int kn = 10;

        LofDataSet dataSet = new LofDataSet();

        int pointIndex = 0;
        for (String targetStr : fileContents)
        {
            pointIndex++;
            String[] splitedStr = StringUtils.split(targetStr, " ");
            double[] points = new double[splitedStr.length];

            for (int index = 0; index < splitedStr.length; index++)
            {
                points[index] = Double.parseDouble(splitedStr[index]);
            }

            LofPoint lofPoint = new LofPoint();
            lofPoint.setDataId("point" + pointIndex);
            lofPoint.setDataPoint(points);
            dataSet.addData(lofPoint);
        }

        pointIndex = 0;
        for (String targetId : dataSet.getDataIdList())
        {
            pointIndex++;
            LofPoint targetPoint = dataSet.getDataMap().get(targetId);
            double lofResult = LofCalculator.calculateLofNoIntermediate(kn, targetPoint, dataSet);
            System.out.println("Index=" + pointIndex + ", Lof Score=" + lofResult + ", Point="
                    + Arrays.toString(targetPoint.getDataPoint()));
        }
    }

    /**
     * データ数100でK値を設定した場合の中間データを保持したLOF値算出結果を確認する。
     * 
     * @target {@link LofCalculator"#calculateLofWithoutUpdate(int, LofPoint, LofDataSet)}
     * @test データ数100で算出可能であること
     *    condition::データ数100でK値を設定した場合
     *    result::データ数100で算出可能であること
     */
    @Test
    public void testCalculateLofWithoutUpdate_サンプルデータ算出() throws IOException
    {
        // 準備 
        String filePath = DATA_DIR + "LofCalculatorTest_TestSample.txt";
        File targetFile = new File(filePath);
        List<String> fileContents = FileUtils.readLines(targetFile);
        int kn = 10;

        LofDataSet dataSet = new LofDataSet();

        int pointIndex = 0;
        for (String targetStr : fileContents)
        {
            pointIndex++;
            String[] splitedStr = StringUtils.split(targetStr, " ");
            double[] points = new double[splitedStr.length];

            for (int index = 0; index < splitedStr.length; index++)
            {
                points[index] = Double.parseDouble(splitedStr[index]);
            }

            LofPoint lofPoint = new LofPoint();
            lofPoint.setDataId("point" + pointIndex);
            lofPoint.setDataPoint(points);
            dataSet.addData(lofPoint);
        }

        LofCalculator.initDataSet(kn, dataSet);

        pointIndex = 0;
        for (String targetId : dataSet.getDataIdList())
        {
            pointIndex++;
            LofPoint targetPoint = dataSet.getDataMap().get(targetId);
            double lofResult = LofCalculator.calculateLofWithoutUpdate(kn, targetPoint, dataSet);
            System.out.println("Index=" + pointIndex + ", Lof Score=" + lofResult + ", Point="
                    + Arrays.toString(targetPoint.getDataPoint()));
        }
    }

    /**
     * 基本の学習データセット(データ数5)を生成する。
     * 
     * @return 学習データセット
     */
    private LofDataSet createDefaultDataSet()
    {
        // 準備 
        LofPoint point1 = new LofPoint();
        LofPoint point2 = new LofPoint();
        LofPoint point3 = new LofPoint();
        LofPoint point4 = new LofPoint();
        LofPoint point5 = new LofPoint();
        point1.setJudgeDate(new Date(1L));
        point2.setJudgeDate(new Date(3L));
        point3.setJudgeDate(new Date(5L));
        point4.setJudgeDate(new Date(7L));
        point5.setJudgeDate(new Date(9L));
        point1.setDataId("point1");
        point2.setDataId("point2");
        point3.setDataId("point3");
        point4.setDataId("point4");
        point5.setDataId("point5");
        double[] points1 = {2.0d, 1.0d, 3.0d};
        double[] points2 = {3.0d, 2.0d, 1.0d};
        double[] points3 = {1.0d, 3.0d, 2.0d};
        double[] points4 = {3.0d, 1.0d, 2.0d};
        double[] points5 = {1.0d, 1.0d, 1.0d};
        point1.setDataPoint(points1);
        point2.setDataPoint(points2);
        point3.setDataPoint(points3);
        point4.setDataPoint(points4);
        point5.setDataPoint(points5);

        LofDataSet dataSet = new LofDataSet();
        dataSet.addData(point1);
        dataSet.addData(point2);
        dataSet.addData(point3);
        dataSet.addData(point4);
        dataSet.addData(point5);

        return dataSet;
    }

    /**
     * 基本の学習データセット2個目(データ数5)を生成する。
     * 
     * @return 学習データセット
     */
    private LofDataSet createAnotherDataSet()
    {
        // 準備 
        LofPoint point1 = new LofPoint();
        LofPoint point2 = new LofPoint();
        LofPoint point3 = new LofPoint();
        LofPoint point4 = new LofPoint();
        LofPoint point5 = new LofPoint();
        point1.setJudgeDate(new Date(2L));
        point2.setJudgeDate(new Date(4L));
        point3.setJudgeDate(new Date(6L));
        point4.setJudgeDate(new Date(8L));
        point5.setJudgeDate(new Date(10L));
        point1.setDataId("another1");
        point2.setDataId("another2");
        point3.setDataId("another3");
        point4.setDataId("another4");
        point5.setDataId("point5");
        double[] points1 = {12.0d, 1.0d, 3.0d};
        double[] points2 = {13.0d, 2.0d, 1.0d};
        double[] points3 = {11.0d, 3.0d, 2.0d};
        double[] points4 = {13.0d, 1.0d, 2.0d};
        double[] points5 = {11.0d, 1.0d, 1.0d};
        point1.setDataPoint(points1);
        point2.setDataPoint(points2);
        point3.setDataPoint(points3);
        point4.setDataPoint(points4);
        point5.setDataPoint(points5);

        LofDataSet dataSet = new LofDataSet();
        dataSet.addData(point1);
        dataSet.addData(point2);
        dataSet.addData(point3);
        dataSet.addData(point4);
        dataSet.addData(point5);

        return dataSet;
    }
}
