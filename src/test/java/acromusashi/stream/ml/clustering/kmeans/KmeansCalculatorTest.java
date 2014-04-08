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
package acromusashi.stream.ml.clustering.kmeans;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acromusashi.stream.ml.clustering.kmeans.entity.CentroidMapping;
import acromusashi.stream.ml.clustering.kmeans.entity.CentroidsComparator;
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansDataSet;
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansPoint;
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansResult;

import com.google.common.collect.Lists;

/**
 * KmeansCalculatorのテストクラス
 * 
 * @author kimura
 */
public class KmeansCalculatorTest
{
    /** 試験用データファイル配置ディレクトリ*/
    private static final String DATA_DIR = "src/test/resources/"
                                                 + StringUtils.replaceChars(
                                                         KmeansCalculatorTest.class.getPackage().getName(),
                                                         '.', '/') + '/';

    /** logger */
    private static Logger       logger   = LoggerFactory.getLogger(KmeansCalculatorTest.class);

    /**
     * 3000件のデータを用いて中心点の初期化を行う。
     * 
     * @target {@link KmeansCalculator#createDataModel(List, int, int, double)}
     * @test 3000件のデータを用いて中心点の初期化が行われること
     *    condition::3000件のデータを用いて初期化を実施
     *    result::3000件のデータを用いて中心点の初期化が行われること
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateDataModel_3000件データ計測() throws IOException
    {
        // 準備 
        String filePath = DATA_DIR + "KmeansCalculatorTest_3000.csv";
        File targetFile = new File(filePath);
        List<String> fileContents = FileUtils.readLines(targetFile);

        List<KmeansPoint> pointList = Lists.newArrayList();
        for (String targetLine : fileContents)
        {
            KmeansPoint entity = convertToEntity(targetLine, ",");
            pointList.add(entity);
        }

        // 実施
        logger.info("Create Model Start");
        KmeansDataSet actual = KmeansCalculator.createDataModel(pointList, 3, 100, 0.5d);
        logger.info("Create Model End");

        // 検証
        assertEquals(3, actual.getCentroids().length);
    }

    /**
     * 10000件のデータを用いて中心点の初期化を行う。 
     * 
     * @target {@link KmeansCalculator#createDataModel(List, int, int, double)}
     * @test 10000件のデータを用いて中心点の初期化が行われること
     *    condition::10000件のデータを用いて初期化を実施
     *    result::10000件のデータを用いて中心点の初期化が行われること
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateDataModel_10000件データ計測() throws IOException
    {
        // 準備 
        String filePath = DATA_DIR + "KmeansCalculatorTest_10000.csv";
        File targetFile = new File(filePath);
        List<String> fileContents = FileUtils.readLines(targetFile);

        List<KmeansPoint> pointList = Lists.newArrayList();
        for (String targetLine : fileContents)
        {
            KmeansPoint entity = convertToEntity(targetLine, ",");
            pointList.add(entity);
        }

        // 実施
        logger.info("Create Model Start");
        KmeansDataSet actual = KmeansCalculator.createDataModel(pointList, 3, 100, 0.5d);
        logger.info("Create Model End");

        // 検証
        assertEquals(3, actual.getCentroids().length);
    }

    /**
     * 20000件のデータを用いて中心点の初期化を行う。 
     * 
     * @target {@link KmeansCalculator#createDataModel(List, int, int, double)}
     * @test 20000件のデータを用いて中心点の初期化が行われること
     *    condition::20000件のデータを用いて初期化を実施
     *    result::20000件のデータを用いて中心点の初期化が行われること
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCreateDataModel_20000件データ計測() throws IOException
    {
        // 準備 
        String filePath = DATA_DIR + "KmeansCalculatorTest_20000.csv";
        File targetFile = new File(filePath);
        List<String> fileContents = FileUtils.readLines(targetFile);

        List<KmeansPoint> pointList = Lists.newArrayList();
        for (String targetLine : fileContents)
        {
            KmeansPoint entity = convertToEntity(targetLine, ",");
            pointList.add(entity);
        }

        // 実施
        logger.info("Create Model Start");
        KmeansDataSet actual = KmeansCalculator.createDataModel(pointList, 3, 100, 0.5d);
        logger.info("Create Model End");

        // 検証
        assertEquals(3, actual.getCentroids().length);
    }

    /**
     * 指定した対象点、ベースの点集合をベースに最近点が取得されること。
     * 
     * @target {@link KmeansCalculator#average(double[], double[])}
     * @test double配列の平均値が取得できること
     *    condition::2つのdouble配列を指定してメソッドを実行
     *    result::double配列の平均値が取得できること
     */
    @Test
    public void testNearestCentroid_最近点取得()
    {
        // 準備 
        double[] targetPoint = {1.05d, 2.11d, 1.07d};
        double[] initPoint1 = {30.00d, 32.11d, 21.07d};
        double[] initPoint2 = {15.525d, 17.11d, 11.07d};
        double[] initPoint3 = {25.525d, 27.11d, 21.07d};
        double[][] initPoints = {initPoint1, initPoint2, initPoint3};

        // 実施
        KmeansResult actual = KmeansCalculator.nearestCentroid(targetPoint, initPoints);

        // 検証
        assertEquals(1, actual.getCentroidIndex());
        assertEquals(23.11981d, actual.getDistance(), 0.0001d);
    }

    /**
     * 指定した対象点、ベースの点集合をベースに最近点が取得されること。
     * 
     * @target {@link KmeansCalculator#average(double[], double[])}
     * @test double配列の平均値が取得できること
     *    condition::2つのdouble配列を指定してメソッドを実行
     *    result::double配列の平均値が取得できること
     */
    @Test
    public void testNearestCentroid_最近点取得_nullデータ混じり()
    {
        // 準備 
        double[] targetPoint = {1.05d, 2.11d, 1.07d};
        double[] initPoint1 = null;
        double[] initPoint2 = {15.525d, 17.11d, 11.07d};
        double[] initPoint3 = {25.525d, 27.11d, 21.07d};
        double[][] initPoints = {initPoint1, initPoint2, initPoint3};

        // 実施
        KmeansResult actual = KmeansCalculator.nearestCentroid(targetPoint, initPoints);

        // 検証
        assertEquals(1, actual.getCentroidIndex());
        assertEquals(23.11981d, actual.getDistance(), 0.0001d);
    }

    /**
     * double配列の平均値が取得できることを確認する。
     * 
     * @target {@link KmeansCalculator#average(double[], double[])}
     * @test double配列の平均値が取得できること
     *    condition::2つのdouble配列を指定してメソッドを実行
     *    result::double配列の平均値が取得できること
     */
    @Test
    public void testAverage_平均取得()
    {
        // 準備 
        double[] baseArray = {1.05d, 2.11d, 1.07d};
        double[] targetArray = {30.00d, 32.11d, 21.07d};
        double[] expected = {15.525d, 17.11d, 11.07d};

        // 実施
        double[] actual = KmeansCalculator.average(baseArray, targetArray);

        // 検証
        assertArrayEquals(expected, actual, 0.0d);
    }

    /**
     * 2個の中心点のユークリッド距離が算出されることを確認する。
     * 
     * @target {@link KmeansCalculator#calculateDistances(double[][], double[][], int)
     * @test double配列の平均値が取得できること
     *    condition::2つのdouble配列を指定してメソッドを実行
     *    result::double配列の平均値が取得できること
     */
    @Test
    public void testCalculateDistances_2中心点()
    {
        // 準備 
        double[][] baseCentroids = {{1.05d, 2.11d}, {30.00d, 32.11d}};
        double[][] targetCentroids = {{1.07d, 21.07d}, {15.525d, 17.11d}};

        // 実施
        List<CentroidMapping> actual = KmeansCalculator.calculateDistances(baseCentroids,
                targetCentroids, 2);

        // 検証
        assertEquals(4, actual.size());
        assertEquals(0, actual.get(0).getBaseIndex());
        assertEquals(0, actual.get(0).getTargetIndex());
        assertEquals(18.96001d, actual.get(0).getEuclideanDistance(), 0.01d);
        assertEquals(0, actual.get(1).getBaseIndex());
        assertEquals(1, actual.get(1).getTargetIndex());
        assertEquals(20.84527d, actual.get(1).getEuclideanDistance(), 0.01d);
        assertEquals(1, actual.get(2).getBaseIndex());
        assertEquals(0, actual.get(2).getTargetIndex());
        assertEquals(30.96492d, actual.get(2).getEuclideanDistance(), 0.01d);
        assertEquals(1, actual.get(3).getBaseIndex());
        assertEquals(1, actual.get(3).getTargetIndex());
        assertEquals(20.84527d, actual.get(3).getEuclideanDistance(), 0.01d);
    }

    /**
     * ユークリッド距離が近いマッピングが生成されることを確認する。
     * 
     * @target {@link KmeansCalculator#createCentroidMappings(int, List)}
     * @test ユークリッド距離が近いマッピングが生成されること（0-0,1-1）
     *    condition::2つのdouble配列を指定してメソッドを実行
     *    result::ユークリッド距離が近いマッピングが生成されること（0-0,1-1）
     */
    @Test
    public void testCreateCentroidMappings_2中心点()
    {
        // 準備 
        double[][] baseCentroids = {{10.05d, 20.11d}, {40.00d, 32.11d}};
        double[][] targetCentroids = {{10.05d, 21.11d}, {-10.525d, -17.11d}};
        List<CentroidMapping> allDistances = KmeansCalculator.calculateDistances(baseCentroids,
                targetCentroids, 2);
        Collections.sort(allDistances, new CentroidsComparator());

        Map<Integer, Integer> expected = new TreeMap<>();
        expected.put(0, 0);
        expected.put(1, 1);

        // 実施
        Map<Integer, Integer> actual = KmeansCalculator.createCentroidMappings(2, allDistances);

        // 検証
        assertEquals(expected.toString(), actual.toString());
    }

    /**
     * 2中心点で平均値が算出されることを確認する。
     * 
     * @target {@link KmeansCalculator#mergeCentroids(double[][], double[][])}
     * @test 2中心点で平均値が算出されること
     *    condition::2中心点を指定してメソッドを実行
     *    result::2中心点で平均値が算出されること
     */
    @Test
    public void testMergeCentroids_2中心点()
    {
        // 準備 
        double[][] baseCentroids = {{1.05d, 2.11d}, {30.00d, 32.11d}};
        double[][] targetCentroids = {{1.07d, 21.07d}, {15.525d, 17.11d}};

        double[] expected0 = {1.06d, 11.59d};
        double[] expected1 = {22.7625d, 24.61d};

        // ベース中心点配列とマージ対象中心点配列の各々のユークリッド距離を算出
        List<CentroidMapping> allDistance = KmeansCalculator.calculateDistances(baseCentroids,
                targetCentroids, 2);

        // nの二乗個のユークリッド距離のうち、値が小さいものからベース中心点、マージ対象中心点のマッピングを生成する
        Collections.sort(allDistance, new CentroidsComparator());
        Map<Integer, Integer> resultMapping = KmeansCalculator.createCentroidMappings(2,
                allDistance);

        // 実施

        double[][] mergedCentroids = KmeansCalculator.mergeCentroids(baseCentroids,
                targetCentroids, resultMapping);

        // 検証
        double[] actual0 = mergedCentroids[0];
        double[] actual1 = mergedCentroids[1];

        assertArrayEquals(expected0, actual0, 0.01d);
        assertArrayEquals(expected1, actual1, 0.01d);
    }

    /**
     * 2クラスタデータがマージされることを確認する。
     * 
     * @target {@link KmeansCalculator#mergeKmeans(KmeansClusterer, KmeansClusterer)}
     * @test 2クラスタデータがマージされること
     *    condition::2クラスタデータを指定してメソッドを実行
     *    result::2クラスタデータがマージされること
     */
    @Test
    public void testMergeKmeans_2クラスタデータマージ()
    {
        // 準備 
        KmeansDataSet baseKmeans = new KmeansDataSet();
        KmeansDataSet targetKmeans = new KmeansDataSet();

        double[][] baseCentroids = {{1.05d, 2.11d}, {30.00d, 32.11d}};
        double[][] targetCentroids = {{1.07d, 21.07d}, {15.525d, 17.11d}};
        baseKmeans.setCentroids(baseCentroids);
        targetKmeans.setCentroids(targetCentroids);
        double[] expected0 = {1.06d, 11.59d};
        double[] expected1 = {22.7625d, 24.61d};

        // 実施
        KmeansDataSet actual = KmeansCalculator.mergeKmeans(baseKmeans, targetKmeans);

        // 検証
        assertArrayEquals(expected0, actual.getCentroids()[0], 0.01d);
        assertArrayEquals(expected1, actual.getCentroids()[1], 0.01d);
    }

    /**
     * 入力行を基にKMeans用のエンティティを生成する。
     * 
     * @param dataLine 入力行
     * @param delimeter デリメータ
     * @return KMeans用のエンティティ
     */
    private static KmeansPoint convertToEntity(String dataLine, String delimeter)
    {
        String[] splitedStr = StringUtils.split(dataLine, delimeter);
        int dataNum = splitedStr.length;
        double[] points = new double[splitedStr.length];

        for (int index = 0; index < dataNum; index++)
        {
            points[index] = Double.parseDouble(splitedStr[index].trim());
        }

        KmeansPoint result = new KmeansPoint();
        result.setDataPoint(points);
        return result;
    }
}
