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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.math.util.MathUtils;

import acromusashi.stream.ml.clustering.kmeans.entity.CentroidMapping;
import acromusashi.stream.ml.clustering.kmeans.entity.CentroidsComparator;
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansDataSet;
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansPoint;
import acromusashi.stream.ml.clustering.kmeans.entity.KmeansResult;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * KMeansクラスタリングの計算処理を行うユーティリティクラス
 * 
 * @author kimura
 */
public class KmeansCalculator
{
    /** BinarySearchの補正定数。値を含まない場合に「-インデックス-1」が返るため、その補正用 */
    private static final int COMPENSATE_INDEX = -2;

    /**
     * インスタンス化を防止するためのコンストラクタ
     */
    private KmeansCalculator()
    {}

    /**
     * 指定した点を用いて学習モデルを生成する。<br>
     * 
     * @param pointList 学習モデルのベース点情報
     * @param clusterNum 分類クラスタ数
     * @param maxIteration 最大イテレーション回数
     * @param convergenceThres 収束したと判断する閾値
     * @return 生成された学習モデル
     */
    public static KmeansDataSet createDataModel(List<KmeansPoint> pointList, int clusterNum,
            int maxIteration, double convergenceThres)
    {
        // クラスタ数よりもデータ数が少ない場合、結果はnullとして返す。
        if (pointList.size() < clusterNum)
        {
            return null;
        }

        // 初回の中心点リストを生成する。
        List<KmeansPoint> centroids = createInitialCentroids(pointList, clusterNum);
        long[] clusteredNum = new long[clusterNum];

        // イテレーションを最大回数実行し、収束を試みる。
        for (int exeIndex = 0; exeIndex < maxIteration; exeIndex++)
        {
            Map<Integer, List<KmeansPoint>> assignments = Maps.newHashMap();
            for (int centroidIndex = 0; centroidIndex < clusterNum; centroidIndex++)
            {
                assignments.put(centroidIndex, Lists.<KmeansPoint> newArrayList());
            }

            for (KmeansPoint targetPoint : pointList)
            {
                KmeansResult result = nearestCentroid(targetPoint, centroids);
                assignments.get(result.getCentroidIndex()).add(targetPoint);
            }

            List<KmeansPoint> newCentroids = Lists.newArrayList();
            for (Map.Entry<Integer, List<KmeansPoint>> entry : assignments.entrySet())
            {
                if (entry.getValue().isEmpty())
                {
                    newCentroids.add(centroids.get(entry.getKey()));
                }
                else
                {
                    newCentroids.add(calculateCentroid(entry.getValue()));
                }

                clusteredNum[entry.getKey()] = entry.getValue().size();
            }

            boolean isConvergenced = isConvergenced(centroids, newCentroids, convergenceThres);
            centroids = newCentroids;

            if (isConvergenced == true)
            {

                break;
            }
        }

        double[][] centroidPoints = new double[clusterNum][];

        for (int centroidIndex = 0; centroidIndex < clusterNum; centroidIndex++)
        {
            centroidPoints[centroidIndex] = centroids.get(centroidIndex).getDataPoint();
        }

        KmeansDataSet createdModel = new KmeansDataSet();
        createdModel.setCentroids(centroidPoints);
        createdModel.setClusteredNum(clusteredNum);
        return createdModel;
    }

    /**
     * 指定したリスト内での中心点を算出する。
     * 
     * @param basePoints 算出元点リスト
     * @return 中心点
     */
    public static KmeansPoint calculateCentroid(List<KmeansPoint> basePoints)
    {
        double[] firstDataPoint = basePoints.get(0).getDataPoint();
        double[] centroidSum = Arrays.copyOf(firstDataPoint, firstDataPoint.length);

        for (int pointIndex = 1; pointIndex < basePoints.size(); pointIndex++)
        {
            for (int coordinateIndex = 0; coordinateIndex < centroidSum.length; coordinateIndex++)
            {
                centroidSum[coordinateIndex] = centroidSum[coordinateIndex]
                        + basePoints.get(pointIndex).getDataPoint()[coordinateIndex];
            }
        }

        double[] centroidPoints = sub(centroidSum, basePoints.size());
        KmeansPoint centroid = new KmeansPoint();
        centroid.setDataPoint(centroidPoints);
        return centroid;
    }

    /**
     * クラスタリングが収束したかを判定する。
     * 
     * @param basePoints 前回の中心点リスト
     * @param newPoints 今回の中心点リスト
     * @param convergenceThres 収束閾値
     * @return 収束した場合true、収束していない場合false
     */
    public static boolean isConvergenced(List<KmeansPoint> basePoints, List<KmeansPoint> newPoints,
            double convergenceThres)
    {
        boolean result = true;

        for (int pointIndex = 0; pointIndex < basePoints.size(); pointIndex++)
        {
            double distance = MathUtils.distance(basePoints.get(pointIndex).getDataPoint(),
                    newPoints.get(pointIndex).getDataPoint());
            if (distance > convergenceThres)
            {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
     * 対象点と中心点配列を指定し、対象点に最も近い中心点のインデックスを取得し、結果エンティティを返す
     * 
     * @param targetPoint 判定対象ポイント
     * @param centroids 中心点配列
     * @return Kmeans算出結果
     */
    public static KmeansResult nearestCentroid(double[] targetPoint, double[][] centroids)
    {
        int nearestCentroidIndex = 0;
        Double minDistance = Double.MAX_VALUE;
        double[] currentCentroid = null;
        Double currentDistance;
        for (int index = 0; index < centroids.length; index++)
        {
            currentCentroid = centroids[index];
            if (currentCentroid != null)
            {
                currentDistance = MathUtils.distance(targetPoint, currentCentroid);
                if (currentDistance < minDistance)
                {
                    minDistance = currentDistance;
                    nearestCentroidIndex = index;
                }
            }
        }

        currentCentroid = centroids[nearestCentroidIndex];

        KmeansResult result = new KmeansResult();
        result.setDataPoint(targetPoint);
        result.setCentroidIndex(nearestCentroidIndex);
        result.setCentroid(currentCentroid);
        result.setDistance(minDistance);

        return result;
    }

    /**
     * 対象点と中心点配列を指定し、対象点に最も近い中心点のインデックスを取得し、結果エンティティを返す
     * 
     * @param targetPoint 判定対象ポイント
     * @param centroids 中心点リスト
     * @return Kmeans算出結果
     */
    public static KmeansResult nearestCentroid(KmeansPoint targetPoint, List<KmeansPoint> centroids)
    {
        int nearestCentroidIndex = 0;
        Double minDistance = Double.MAX_VALUE;
        KmeansPoint currentCentroid = null;
        Double currentDistance;
        for (int index = 0; index < centroids.size(); index++)
        {
            currentCentroid = centroids.get(index);
            if (currentCentroid != null && currentCentroid.getDataPoint() != null)
            {
                currentDistance = MathUtils.distance(targetPoint.getDataPoint(),
                        currentCentroid.getDataPoint());
                if (currentDistance < minDistance)
                {
                    minDistance = currentDistance;
                    nearestCentroidIndex = index;
                }
            }
        }

        currentCentroid = centroids.get(nearestCentroidIndex);

        KmeansResult result = new KmeansResult();
        result.setDataPoint(targetPoint.getDataPoint());
        result.setCentroidIndex(nearestCentroidIndex);
        result.setCentroid(currentCentroid.getDataPoint());
        result.setDistance(minDistance);

        return result;
    }

    /**
     * 対象点と学習モデルを指定し、どのクラスタに判別されるかを取得する。
     * 
     * @param targetPoint 判定対象ポイント
     * @param dataSet 学習モデル
     * @return どのクラスタに判別されるかのインデックス
     */
    public static KmeansResult classify(KmeansPoint targetPoint, KmeansDataSet dataSet)
    {
        // KMeanクラスタリング結果を算出
        int nearestCentroidIndex = 0;
        Double minDistance = Double.MAX_VALUE;
        double[] currentCentroid = null;
        Double currentDistance;
        for (int index = 0; index < dataSet.getCentroids().length; index++)
        {
            currentCentroid = dataSet.getCentroids()[index];
            if (currentCentroid != null)
            {
                currentDistance = MathUtils.distance(targetPoint.getDataPoint(), currentCentroid);
                if (currentDistance < minDistance)
                {
                    minDistance = currentDistance;
                    nearestCentroidIndex = index;
                }
            }
        }

        currentCentroid = dataSet.getCentroids()[nearestCentroidIndex];

        KmeansResult result = new KmeansResult();
        result.setDataPoint(targetPoint.getDataPoint());
        result.setCentroidIndex(nearestCentroidIndex);
        result.setCentroid(currentCentroid);
        result.setDistance(minDistance);

        return result;
    }

    /**
     * KMeans++アルゴリズムで初期の中心点リストを生成する。
     * 
     * @param basePoints 中心点を生成するベースデータ
     * @param clusterNum クラスタ数
     * @return 中心点リスト
     */
    public static List<KmeansPoint> createInitialCentroids(List<KmeansPoint> basePoints,
            int clusterNum)
    {
        Random random = new Random();
        List<KmeansPoint> resultList = Lists.newArrayList();
        // ベースデータリストを更新しないためにコピーを生成して用いる
        List<KmeansPoint> pointList = Lists.newArrayList(basePoints);
        KmeansPoint firstCentroid = pointList.remove(random.nextInt(pointList.size()));
        resultList.add(firstCentroid);

        double[] dxs;
        // KMeans++方式による中心点算出をクラスタの数だけ実施する。
        // 最初の1点はランダムで算出しているため、1オリジンで開始している。
        for (int centroidIndex = 1; centroidIndex < clusterNum; centroidIndex++)
        {
            // 各データポイントに対する距離を用いて重みつき確率分布を算出する。
            dxs = computeDxs(pointList, resultList);

            // 重みつき確率分布を用いて次の中心点を選出する。
            double r = random.nextDouble() * dxs[dxs.length - 1];
            int next = Arrays.binarySearch(dxs, r);
            int index = 0;
            if (next > 0)
            {
                index = next - 1;
            }
            else if (next < 0)
            {
                index = COMPENSATE_INDEX - next;
            }

            while (index > 0 && resultList.contains(pointList.get(index)))
            {
                index = index - 1;
            }

            resultList.add(pointList.get(index));
        }

        return resultList;
    }

    /**
     * 点リストを用いて重みつき確率分布を算出する。
     * 
     * @param basePoints 算出元となる点リスト
     * @param centroids 現状の中心点リスト
     * @return 重みつき確率分布
     */
    public static double[] computeDxs(List<KmeansPoint> basePoints, List<KmeansPoint> centroids)
    {
        double[] dxs = new double[basePoints.size()];

        double sum = 0.0d;
        double[] nearestCentroid;
        for (int pointIndex = 0; pointIndex < basePoints.size(); pointIndex++)
        {
            // 対象点に対する最近傍中心との距離(dx)を算出し、二乗に比例する重み付き確率分布値を算出
            KmeansPoint targetPoint = basePoints.get(pointIndex);
            KmeansResult kmeanResult = KmeansCalculator.nearestCentroid(targetPoint, centroids);
            nearestCentroid = kmeanResult.getCentroid();
            double dx = MathUtils.distance(targetPoint.getDataPoint(), nearestCentroid);
            double probabilityDist = Math.pow(dx, 2);
            sum += probabilityDist;
            dxs[pointIndex] = sum;
        }

        return dxs;
    }

    /**
     * Kmeansクラスタリングのデータをマージする。<br>
     * 以下の順でマージを実施する。<br>
    * <ol>
    * <li>ベース中心点配列とマージ対象中心点配列の各々のユークリッド距離を算出する。(中心点がn個あった場合nの二乗個のユークリッド距離が算出される)</li>
    * <li>nの二乗個のユークリッド距離のうち、値が小さいものからベース中心点、マージ対象中心点のマッピングを行う。その際、既に使用されている中心点はマッピングに使用しない。</li>
    * <li>マッピングした中心点同士を用いて中心点のマージを行う。</li>
    * </ol>
     * 
     * @param baseKmeans マージ元Kmeansクラスタリング
     * @param targetKmeans マージ対象Kmeansクラスタリング
     * @return マージ後のクラスタリングデータ
     */
    public static final KmeansDataSet mergeKmeans(KmeansDataSet baseKmeans,
            KmeansDataSet targetKmeans)
    {
        KmeansDataSet merged = new KmeansDataSet();
        int centroidNum = (int) ComparatorUtils.min(baseKmeans.getCentroids().length,
                targetKmeans.getCentroids().length, ComparatorUtils.NATURAL_COMPARATOR);

        // ベース中心点配列とマージ対象中心点配列の各々のユークリッド距離を算出
        List<CentroidMapping> allDistance = calculateDistances(baseKmeans.getCentroids(),
                targetKmeans.getCentroids(), centroidNum);

        // nの二乗個のユークリッド距離のうち、値が小さいものからベース中心点、マージ対象中心点のマッピングを生成する
        Collections.sort(allDistance, new CentroidsComparator());
        Map<Integer, Integer> resultMapping = createCentroidMappings(centroidNum, allDistance);

        // マッピングを用いて中心点をマージ
        double[][] mergedCentroids = mergeCentroids(baseKmeans.getCentroids(),
                targetKmeans.getCentroids(), resultMapping);
        merged.setCentroids(mergedCentroids);

        return merged;
    }

    /**
     * クラスタリングのCounts値のマージを行う。
     * 
     * @param baseCounts ベースCounts
     * @param targetCounts マージ対象Counts
     * @param resultMapping ベース学習モデルとマージ対象学習モデルの中心点マッピング
     * @return マージ結果Counts
     */
    protected static List<Long> mergeCounts(List<Long> baseCounts, List<Long> targetCounts,
            Map<Integer, Integer> resultMapping)
    {
        int countNum = resultMapping.size();
        List<Long> mergedCounts = new ArrayList<>(countNum);
        for (int count = 0; count < countNum; count++)
        {
            mergedCounts.add(0L);
        }

        for (Entry<Integer, Integer> resultEntry : resultMapping.entrySet())
        {
            mergedCounts.set(resultEntry.getKey(), baseCounts.get(resultEntry.getKey())
                    + targetCounts.get(resultEntry.getValue()));
        }

        return mergedCounts;
    }

    /**
     * クラスタリングの初期化ポイントリストのマージを行う。
     * 
     * @param basePoints ベース初期化ポイントリスト
     * @param targetPoints マージ対象初期化ポイントリスト
     * @return マージ結果Counts
     */
    protected static List<double[]> mergeInitPoints(List<double[]> basePoints,
            List<double[]> targetPoints)
    {
        List<double[]> mergedFeatures = new ArrayList<>();
        mergedFeatures.addAll(basePoints);
        mergedFeatures.addAll(targetPoints);

        return mergedFeatures;
    }

    /**
     * クラスタリングの中心座標情報のマージを行い、マージ結果を新たな配列に設定して返す。<br>
     * マッピングした中心点同士の平均値を取り、結果とする。<br>
     * 
     * @param baseCentroids ベース中心点配列
     * @param targetCentroids マージ対象中心点配列 
     * @param resultMapping ベース学習モデルとマージ対象学習モデルの中心点マッピング
     * @return マージ結果中心点配列
     */
    public static double[][] mergeCentroids(double[][] baseCentroids, double[][] targetCentroids,
            Map<Integer, Integer> resultMapping)
    {
        // マッピングした中心点同士の平均値を取る
        double[][] mergedCentroids = new double[resultMapping.size()][];

        for (Map.Entry<Integer, Integer> targetEntry : resultMapping.entrySet())
        {
            double[] baseCentroid = baseCentroids[targetEntry.getKey()];
            double[] targetCentroid = targetCentroids[targetEntry.getValue()];
            mergedCentroids[targetEntry.getKey()] = average(baseCentroid, targetCentroid);
        }

        return mergedCentroids;
    }

    /**
     * ベース中心点、マージ対象中心点のマッピングを行う。
     * 
     * @param centroidNum 中心点数
     * @param allDistance ユークリッド距離エンティティリスト
     * @return マッピング
     */
    protected static Map<Integer, Integer> createCentroidMappings(int centroidNum,
            List<CentroidMapping> allDistance)
    {
        Set<Integer> baseSet = new HashSet<>();
        Set<Integer> targetSet = new HashSet<>();
        Map<Integer, Integer> resultMapping = new TreeMap<>();
        int mappingNum = 0;

        // 算出したユークリッド処理リストをソートして使用
        for (CentroidMapping targetDistance : allDistance)
        {
            // 既にマッピングに使用されている場合は省略する。
            if (baseSet.contains(targetDistance.getBaseIndex())
                    || targetSet.contains(targetDistance.getTargetIndex()))
            {
                continue;
            }

            baseSet.add(targetDistance.getBaseIndex());
            targetSet.add(targetDistance.getTargetIndex());
            resultMapping.put(targetDistance.getBaseIndex(), targetDistance.getTargetIndex());
            mappingNum++;

            // マッピングが必要数確保できた時点でマッピングを終了
            if (mappingNum >= centroidNum)
            {
                break;
            }
        }

        return resultMapping;
    }

    /**
     * ベース中心点配列とマージ対象中心点配列の各々のユークリッド距離を算出する。
     * 
     * @param baseCentroids ベース中心点配列
     * @param targetCentroids マージ対象中心点配列 
     * @param centroidNum 中心点数
     * @return ユークリッド距離リスト
     */
    protected static List<CentroidMapping> calculateDistances(double[][] baseCentroids,
            double[][] targetCentroids, int centroidNum)
    {
        // ベース中心点配列とマージ対象中心点配列の各々のユークリッド距離を算出
        List<CentroidMapping> allDistance = new ArrayList<>();

        for (int baseIndex = 0; baseIndex < centroidNum; baseIndex++)
        {
            for (int targetIndex = 0; targetIndex < centroidNum; targetIndex++)
            {
                CentroidMapping centroidMapping = new CentroidMapping();
                centroidMapping.setBaseIndex(baseIndex);
                centroidMapping.setTargetIndex(targetIndex);
                double distance = MathUtils.distance(baseCentroids[baseIndex],
                        targetCentroids[targetIndex]);
                centroidMapping.setEuclideanDistance(distance);
                allDistance.add(centroidMapping);
            }
        }
        return allDistance;
    }

    /**
     * 配列の平均値を算出する。
     * 
     * @param base ベース配列
     * @param target 平均算出対象配列
     * @return 平均値配列
     */
    protected static double[] average(double[] base, double[] target)
    {
        int dataNum = base.length;
        double[] average = new double[dataNum];

        for (int index = 0; index < dataNum; index++)
        {
            average[index] = (base[index] + target[index]) / 2.0;
        }

        return average;
    }

    /**
     * double配列の除算結果を算出する。
     * 
     * @param base ベース配列
     * @param subNumber 除算を行う数
     * @return 除算結果
     */
    protected static double[] sub(double[] base, double subNumber)
    {
        int dataNum = base.length;
        double[] result = new double[dataNum];

        for (int index = 0; index < dataNum; index++)
        {
            result[index] = base[index] / subNumber;
        }

        return result;
    }
}
