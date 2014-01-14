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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.util.MathUtils;

import acromusashi.stream.ml.anomaly.lof.entity.DistanceResult;
import acromusashi.stream.ml.anomaly.lof.entity.DistanceResultComparator;
import acromusashi.stream.ml.anomaly.lof.entity.KDistanceResult;
import acromusashi.stream.ml.anomaly.lof.entity.LofDataSet;
import acromusashi.stream.ml.anomaly.lof.entity.LofPoint;
import acromusashi.stream.ml.anomaly.lof.entity.LofPointComparator;

/**
 * Local Outlier Factorの計算を行うユーティリティクラス
 * 
 * @author kimura
 */
public class LofCalculator
{
    /**
     * インスタンス化を防止するためのコンストラクタ
     */
    private LofCalculator()
    {}

    /**
     * 指定したK値、対象点、データセットを基に局所外れ係数スコアを算出する。その際、データセットが中間データを使用せずに処理を行う。<br>
     * 本メソッド呼び出しによってデータセットの更新は行われない。<br>
     * 
     * @param kn K値
     * @param targetPoint 対象点
     * @param dataSet 学習データセット
     * @return LOFスコア
     */
    public static double calculateLofNoIntermediate(int kn, LofPoint targetPoint, LofDataSet dataSet)
    {
        // 対象点のK距離、K距離近傍を算出する。
        KDistanceResult kResult = calculateKDistance(kn, targetPoint, dataSet);

        LofPoint tmpTargetPoint = targetPoint.deepCopy();
        tmpTargetPoint.setkDistance(kResult.getkDistance());
        tmpTargetPoint.setkDistanceNeighbor(kResult.getkDistanceNeighbor());

        // データセット用の学習データモデルを一時的に生成する。
        LofDataSet tmpDataSet = dataSet.deepCopy();
        initDataSet(kn, tmpDataSet);

        updateLrd(tmpTargetPoint, tmpDataSet);

        // 対象の局所外れ係数を算出する。
        double lof = calculateLof(tmpTargetPoint, tmpDataSet);
        return lof;
    }

    /**
     * 学習データセットに対象点を追加し、学習データセットの構造を更新する。<br>
     * データ保持数が最大値に達していた場合は学習データセットが保持しているもっとも古い点の削除を行い、削除したデータのIdを返す。
     * 
     * @param max データ保持数最大値
     * @param addedPoint 対象点
     * @param dataSet 学習データセット
     * @return 削除したデータのId。削除しなかった場合はnull
     */
    public static String addPointToDataSet(int max, LofPoint addedPoint, LofDataSet dataSet)
    {
        // データ数が最大に達している場合には古い方からデータの削除を行う
        boolean dateDelete = false;
        if (max <= dataSet.getDataIdList().size())
        {
            dateDelete = true;
        }

        // 削除フラグがONの場合は古いデータから削除を行う。
        String deleteId = null;
        if (dateDelete)
        {
            deleteId = dataSet.getDataIdList().get(0);
            dataSet.deleteData(deleteId);
        }

        dataSet.addData(addedPoint);

        return deleteId;
    }

    /**
     * 指定したK値、対象点、データセットを基に局所外れ係数スコアを算出する。<br>
     * 本メソッド呼び出しによってデータセットの更新は行われない。<br>
     * 学習データの更新を伴わないため、高速に処理が可能となっている。
     * 
     * @param kn K値
     * @param targetPoint 対象点
     * @param dataSet 学習データセット
     * @return LOFスコア
     */
    public static double calculateLofWithoutUpdate(int kn, LofPoint targetPoint, LofDataSet dataSet)
    {
        // 対象点のK距離、K距離近傍を算出する。
        KDistanceResult kResult = calculateKDistance(kn, targetPoint, dataSet);

        LofPoint tmpPoint = targetPoint.deepCopy();
        tmpPoint.setkDistance(kResult.getkDistance());
        tmpPoint.setkDistanceNeighbor(kResult.getkDistanceNeighbor());

        updateLrd(tmpPoint, dataSet);

        // 対象の局所外れ係数を算出する。
        double lof = calculateLof(tmpPoint, dataSet);
        return lof;
    }

    /**
     * 指定したK値、対象点、データセットを基に局所外れ係数スコアを算出する。<br>
     * 本メソッド呼び出しによって対象点、データセット内の学習データが更新され、データ保持数を超過していた場合は古いデータから削除が行われる。
     * 
     * @param kn K値
     * @param max データ保持数最大値
     * @param addedPoint 対象点
     * @param dataSet 学習データセット
     * @return LOFスコア
     */
    public static double calculateLofWithUpdate(int kn, int max, LofPoint addedPoint,
            LofDataSet dataSet)
    {
        // データ数が最大に達している場合には古い方からデータの削除を行う
        String deleteId = addPointToDataSet(max, addedPoint, dataSet);

        // K距離、K距離近傍、局所到達可能密度の更新を行う対象点の一覧を取得する。
        Set<String> updateTargets = generateUpdateTargets(addedPoint, dataSet, deleteId);

        Collection<LofPoint> targetList = dataSet.getDataMap().values();
        // K距離、K距離近傍を全て更新した後局所到達可能密度を更新する必要があるため、2ブロックに分けて行う。
        for (LofPoint targetPoint : targetList)
        {
            if (updateTargets.contains(targetPoint.getDataId()))
            {
                // 対象点のK距離、K距離近傍を更新する。
                updateKDistance(kn, targetPoint, dataSet);
            }
        }

        // K距離の更新によって「K距離が更新された点」をK距離近傍に含む点の到達可能距離/局所到達可能密度も更新される
        // 実質的にほぼすべての点の到達可能距離/局所到達可能密度が更新される形になるため、全点に対して算出を行う。 
        for (LofPoint targetPoint : targetList)
        {
            // 対象点の局所到達可能密度を更新する。
            updateLrd(targetPoint, dataSet);
        }

        // 対象の局所外れ係数を算出する。「addedPoint」は必ずK距離、K距離近傍、局所到達可能密度の対象となり、値が設定されているためそのまま用いる。
        double lof = calculateLof(addedPoint, dataSet);
        return lof;
    }

    /**
     * 指定したK値、データセットを基に学習データセットの初期化を行う。<br>
     * データセット中の各対象点の以下の値を再計算する。
    * <ol>
    * <li>K距離値</li>
    * <li>K距離近傍データのIDリスト</li>
    * <li>局所到達可能密度</li>
    * </ol>
     * 
     * @param kn K値
     * @param dataSet 学習データセット
     */
    public static void initDataSet(int kn, LofDataSet dataSet)
    {
        Collection<LofPoint> pointList = dataSet.getDataMap().values();
        // K距離、K距離近傍を全て更新した後局所到達可能密度を更新する必要があるため、2ブロックに分けて行う。
        for (LofPoint targetPoint : pointList)
        {
            // 対象点のK距離、K距離近傍を更新する。
            updateKDistance(kn, targetPoint, dataSet);
        }

        for (LofPoint targetPoint : pointList)
        {
            // 対象点の局所到達可能密度を更新する。
            updateLrd(targetPoint, dataSet);
        }
    }

    /**
     * 学習データのマージを行う。<br>
     * 中間データは生成されないため、必要な場合は本メソッド実行後に{@link #initDataSet(int, LofDataSet)}メソッドを実行すること。
     * 
     * @param baseDataSet マージのベース学習データ
     * @param targetDataSet マージ対象の学習データ
     * @param max データ保持数最大値
     * @return マージ後の学習データ
     */
    public static LofDataSet mergeDataSet(LofDataSet baseDataSet, LofDataSet targetDataSet, int max)
    {
        Collection<LofPoint> basePointList = baseDataSet.getDataMap().values();
        Collection<LofPoint> targetPointList = targetDataSet.getDataMap().values();

        // LOFの対象点を時刻でソートしたリストを生成する。
        List<LofPoint> mergedList = new ArrayList<>();
        mergedList.addAll(basePointList);
        mergedList.addAll(targetPointList);
        Collections.sort(mergedList, new LofPointComparator());

        // ソート後、新しい方から扱うため順番を逆にする。
        Collections.reverse(mergedList);

        // 新しいデータから順にマージ後のモデルに反映する。
        // 但し、お互いに非同期でマージが行われるため同様のIDを持つデータが複数存在するケースがある。
        // そのため、IDを比較してそれまでに取得していないデータの追加のみを行う。
        Set<String> registeredId = new HashSet<>();
        int addedCount = 0;
        LofDataSet resultDataSet = new LofDataSet();

        for (LofPoint targetPoint : mergedList)
        {
            if (registeredId.contains(targetPoint.getDataId()) == true)
            {
                continue;
            }

            registeredId.add(targetPoint.getDataId());
            resultDataSet.addData(targetPoint);
            addedCount++;

            if (addedCount >= max)
            {
                break;
            }
        }

        return resultDataSet;
    }

    /**
     * 学習データの中間データを更新する必要があるDataIdの一覧を生成する。<br>
     * 以下のいずれかの条件を満たす場合に更新する必要があると判断する。
     * <ol>
     * <li>削除されたデータをK距離近傍に含む</li>
     * <li>追加点との距離がK距離より小さい</li>
     * </ol>
     * 
     * @param addedPoint 追加点
     * @param dataSet 学習データセット
     * @param deleteId 削除データのId
     * @return 学習データの中間データを更新する必要があるDataIdの一覧
     */
    protected static Set<String> generateUpdateTargets(LofPoint addedPoint, LofDataSet dataSet,
            String deleteId)
    {
        Set<String> updateTargets = new HashSet<>();
        // 以下の条件のいずれかを満たす対象点と追加された対象点に対してK距離、K距離近傍、局所到達可能密度の更新を行う必要がある。
        // 但し、今回追加された対象点は常時対象となるため、判定は行わず、中間データ更新が必要として扱う。
        // 1.削除されたデータをK距離近傍に含む
        // 2.追加された対象点との距離がK距離より小さい。
        updateTargets.add(addedPoint.getDataId());
        Collection<LofPoint> pointList = dataSet.getDataMap().values();
        for (LofPoint targetPoint : pointList)
        {
            boolean isDeteted = false;
            boolean kDistUpdate = false;

            // 今回追加された対象点は判定をスキップ
            if (StringUtils.equals(addedPoint.getDataId(), targetPoint.getDataId()) == true)
            {
                continue;
            }

            // 「1.削除されたデータをK距離近傍に含む」判定
            // K値は高々20～30の値になるのが一般的なため、Listへのcontainsメソッド実行は性能負荷にはならないと判断する。
            if (deleteId != null && targetPoint.getkDistanceNeighbor().contains(deleteId) == true)
            {
                isDeteted = true;
            }

            // 「2.追加された対象点との距離がK距離より小さい。」判定
            if (MathUtils.distance(addedPoint.getDataPoint(), targetPoint.getDataPoint()) < targetPoint.getkDistance())
            {
                kDistUpdate = true;
            }

            if (isDeteted || kDistUpdate)
            {
                updateTargets.add(targetPoint.getDataId());
            }
        }

        return updateTargets;
    }

    /**
     * 対象点の局所到達可能密度を更新する。
     * 
     * @param targetPoint 判定対象点
     * @param dataSet 全体データ
     */
    protected static void updateLrd(LofPoint targetPoint, LofDataSet dataSet)
    {
        // 対象点の局所到達可能密度を更新する。
        double lrd = calculateLrd(targetPoint, dataSet);
        targetPoint.setLrd(lrd);
    }

    /**
     * 対象点のK距離とK距離近傍データのIDを更新する。
     * 
     * @param kn K値
     * @param targetPoint 判定対象点
     * @param dataSet 全体データ
     */
    protected static void updateKDistance(int kn, LofPoint targetPoint, LofDataSet dataSet)
    {
        // 対象点のK距離、K距離近傍を算出する。
        KDistanceResult kResult = calculateKDistance(kn, targetPoint, dataSet);
        targetPoint.setkDistance(kResult.getkDistance());
        targetPoint.setkDistanceNeighbor(kResult.getkDistanceNeighbor());
    }

    /**
     * 指定したK値、対象点、データセットを基にK距離とK距離近傍データのIDを算出する。
     * 
     * @param kn K値
     * @param targetPoint 判定対象点
     * @param dataSet 全体データ
     * @return K距離とK距離近傍データのIDを保持するエンティティ。但し、K距離算出に必要なデータ個数を満たさない場合は判定対象点から最遠の点に対する距離となる。
     */
    protected static KDistanceResult calculateKDistance(int kn, LofPoint targetPoint,
            LofDataSet dataSet)
    {
        // 距離算出結果保持マップを算出
        List<DistanceResult> distances = calculateDistances(targetPoint, dataSet);

        // 算出した距離を用いてK距離とK距離近傍データのIDを算出
        KDistanceResult kResult = calculateKDistance(kn, distances);
        return kResult;
    }

    /**
     * basePointのtargetPointに関する到達可能距離(Reachability distance)を算出する。
     * 
     * @param basePoint 算出元対象点
     * @param targetPoint 算出先対象点
     * @return 到達可能距離
     */
    protected static double calculateReachDistance(LofPoint basePoint, LofPoint targetPoint)
    {
        double distance = MathUtils.distance(basePoint.getDataPoint(), targetPoint.getDataPoint());

        double reachDistance = (double) ComparatorUtils.max(distance, targetPoint.getkDistance(),
                ComparatorUtils.NATURAL_COMPARATOR);
        return reachDistance;
    }

    /**
     * basePointに対する局所到達可能密度(Local reachability density)を算出する。
     * 
     * @param basePoint 算出元対象点
     * @param dataSet 全体データ
     * @return 局所到達可能密度
     */
    protected static double calculateLrd(LofPoint basePoint, LofDataSet dataSet)
    {
        int countedData = 0;
        double totalAmount = 0.0d;

        for (String targetDataId : basePoint.getkDistanceNeighbor())
        {
            LofPoint targetPoint = dataSet.getDataMap().get(targetDataId);

            if (targetPoint == null)
            {
                continue;
            }

            double reachDist = calculateReachDistance(basePoint, targetPoint);
            totalAmount = totalAmount + reachDist;
            countedData++;
        }

        if (totalAmount == 0.0d)
        {
            return totalAmount;
        }

        return (countedData) / totalAmount;
    }

    /**
     * basePointの局所外れ係数(Local outlier factor)を算出する。
     * 
     * @param basePoint 算出元対象点
     * @param dataSet 全体データ
     * @return 局所外れ係数
     */
    protected static double calculateLof(LofPoint basePoint, LofDataSet dataSet)
    {
        int countedData = 0;
        double totalAmount = 0.0d;
        for (String targetDataId : basePoint.getkDistanceNeighbor())
        {
            LofPoint targetPoint = dataSet.getDataMap().get(targetDataId);

            totalAmount = totalAmount + (targetPoint.getLrd() / basePoint.getLrd());
            countedData++;
        }

        if (countedData == 0)
        {
            return totalAmount;
        }

        return totalAmount / (countedData);
    }

    /**
     * 指定した対象点、データセットを基に対象点とデータセット中の各点間の距離を算出し、距離の昇順のリストを生成して返す。
     * 
     * @param targetPoint 判定対象点
     * @param dataSet 全体データ
     * @return 距離算出結果リスト。距離＞データIDの優先度でソートした状態で返す。
     */
    protected static List<DistanceResult> calculateDistances(LofPoint targetPoint,
            LofDataSet dataSet)
    {
        List<DistanceResult> distances = new ArrayList<>();
        for (Map.Entry<String, LofPoint> targetEntry : dataSet.getDataMap().entrySet())
        {
            // 同一データの場合除外する
            if (StringUtils.equals(targetEntry.getKey(), targetPoint.getDataId()) == true)
            {
                continue;
            }

            double distance = MathUtils.distance(targetEntry.getValue().getDataPoint(),
                    targetPoint.getDataPoint());

            DistanceResult result = new DistanceResult(targetEntry.getKey(), distance);

            // リストに保持し、算出後ソート
            distances.add(result);
        }

        Collections.sort(distances, new DistanceResultComparator());
        return distances;
    }

    /**
     * 距離算出結果リストを基にK距離とK距離近傍データのIDを算出する。
     * 
     * @param kn K値
     * @param distances 距離算出結果リスト
     * @return K距離とK距離近傍データのIDを保持するエンティティ。但し、K距離算出に必要なデータ個数を満たさない場合は判定対象点から最遠の点に対する距離となる。
     */
    protected static KDistanceResult calculateKDistance(int kn, List<DistanceResult> distances)
    {
        // 算出した距離を用いてK距離とK距離近傍データのIDを算出
        int countedDataNum = 0;
        List<String> idList = new ArrayList<>();
        double nowDistance = 0.0d;

        // K値の値までIDのカウントを行い、距離を設定する。
        for (DistanceResult distanceResult : distances)
        {
            nowDistance = distanceResult.getDistance();
            idList.add(distanceResult.getDataId());
            countedDataNum++;

            if (kn <= countedDataNum)
            {
                break;
            }
        }

        KDistanceResult kResult = new KDistanceResult();
        kResult.setkDistance(nowDistance);
        kResult.setkDistanceNeighbor(idList);
        return kResult;
    }
}
