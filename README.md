## 概要
AcroMUSASHI Stream - Machine Learning Library は、[acromusashi-stream](https://github.com/acromusashi/acromusashi-stream) をベースとした、オンライン機械学習を行うためのライブラリです。AcroMUSASHI Stream - Machine Learning Library を利用することで、機械学習の処理を[Storm](http://storm-project.net/)上でリアルタイムで動作させることができます。

## システム構成イメージ
![Abstract Image](http://acromusashi.github.io/acromusashi-stream-ml/images/MlAbstract.png)

#### Storm Trident Topology
AcroMUSASHI Stream - Machine Learning Library は、StormのTrident機能を利用して実現しています。  
Tridentに関しては、[Trident tutorial](https://github.com/nathanmarz/storm/wiki/Trident-tutorial)を参照してください。

#### In-MemoryDB
AcroMUSASHI Stream - Machine Learning Library  では[Infinispan](http://infinispan.org/)を学習データのキャッシュ先として用いています。Infinispanはメモリ上でデータを保持する分散KVSデータグリッドで、データへの高速なアクセスが可能です。  
Infinispanのインストール方法／利用方法については[Infinispanの利用方法](https://github.com/acromusashi/acromusashi-stream-example/wiki/Infinispan-Usage)を確認してください。  

## スタートガイド

### 開発
acromusashi-stream-ml を用いて開発を行うためには、Mavenのビルド定義ファイルであるpom.xmlに以下の内容を記述してください。
```xml
<dependency>
  <groupId>jp.co.acroquest.acromusashi</groupId>
  <artifactId>acromusashi-stream-ml</artifactId>
  <version>0.2.0</version>
</dependency>
``` 

## 機械学習API

現在は、以下のアルゴリズムをサポートしています。

- クラスタリング
 - 教師なし学習（KMeans++）
- 異常値
 - 外れ値検出（LOF:Local Outlier Factor）
 - 変化点検出（ChangeFinder）

### クラスタリング

#### 教師なし学習（KMeans++）
acromusashi.stream.ml.clustering.kmeans パッケージ配下のコンポーネントを使用することでKMeans++アルゴリズムを用いたクラスタリングを行うことができます。  
KmeansUpdaterにdataNotifierを設定することで1データ処理するごとに追加処理を実行可能です。  
batchNotifierを設定することで1バッチ分データ処理するごとに追加処理を実行可能です。   
実装例は[KmeansTopology](https://github.com/acromusashi/acromusashi-stream-example/blob/master/src/main/java/acromusashi/stream/example/ml/topology/KmeansTopology.java)を確認してください。  

### 異常値検知

#### 外れ値検出（LOF:Local Outlier Factor）
acromusashi.stream.ml.anomaly.lof パッケージ配下のコンポーネントを使用することでLOFアルゴリズムを用いた外れ値検出を行うことができます。  
実装例は[LofTopology](https://github.com/acromusashi/acromusashi-stream-example/blob/master/src/main/java/acromusashi/stream/example/ml/topology/LofTopology.java)を確認してください。

#### 変化点検出（ChangeFinder）
acromusashi.stream.ml.anomaly.cf パッケージ配下のコンポーネントを使用することでChangeFinderアルゴリズムを用いた変化点検出を行うことができます。  

##### 実装例[(ChangeFindTopology)](./src/main/java/acromusashi/stream/example/ml/topology/ChangeFindTopology.java)
ここでは、Apacheのログを解析し、レスポンスタイムの異常を検知する例を示します。
```java
// TridentKafkaSpoutを初期化
// Kafkaの接続先ZooKeeperのサーバアドレスとZooKeeper上のパスを定義
ZkHosts zkHosts = new ZkHosts("192.168.0.1:2181,192.168.0.2:2181,192.168.0.3:2181", "/brokers");
// Kafka上のTopic(キュー名称)と利用者IDを定義
TridentKafkaConfig kafkaConfig = new TridentKafkaConfig(zkHosts, "ApacheLog", "ChangeFindTopology");
// デシリアライズ方式を設定
kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
OpaqueTridentKafkaSpout tridentKafkaSpout = new OpaqueTridentKafkaSpout(kafkaConfig);

// ApacheLogSplitFunctionを初期化
ApacheLogSplitFunction apacheLogSplitFunction = new ApacheLogSplitFunction();
// 読みこむ際の時刻フォーマットを指定
apacheLogSplitFunction.setDateFormatStr("yyyy-MM-dd'T'HH:mm:SSSZ");

// ChangeFindFunctionを初期化
ChangeFindFunction cfFunction = new ChangeFindFunction();
// 自己回帰モデルの次数「k」を設定
cfFunction.setArDimensionNum(4);
// オンライン忘却パラメータ「r」を設定
cfFunction.setForgetability(0.05d);
// 平滑化ウィンドウサイズ「T」を設定
cfFunction.setSmoothingWindow(5);
// 変化点として検出するスコア閾値を設定
cfFunction.setScoreThreshold(15.0d);

TridentTopology topology = new TridentTopology();

// 以下の順でTridentTopologyにSpout/Functionを登録する。
// 1.TridentKafkaSpout:KafkaからApacheログ(JSON形式)を取得
// 2.ApacheLogSplitFunction:受信したApacheログ(JSON形式)をエンティティに変換し、送信
// 3.ChangeFindFunction:受信したApacheログのレスポンスタイムを用いて変化点スコアを算出
// 4.ApacheLogAggregator:受信したApacheログの統計値を算出
// 5.ResultPrintFunction:受信した統計値をログ出力
topology.newStream("TridentKafkaSpout", tridentKafkaSpout).parallelismHint(parallelism)
    .each(new Fields("str"), apacheLogSplitFunction, new Fields("IPaddress", "responseTime"))
    .groupBy(new Fields("IPaddress"))
    .each(new Fields("IPaddress", "responseTime"), cfFunction, new Fields("webResponse"))
    .partitionAggregate(new Fields("webResponse"), new CombinerAggregatorCombineImpl(new ApacheLogAggregator()), new Fields("average"))
    .each(new Fields("average"), new ResultPrintFunction(), new Fields("count"));

// Topology内でTupleに設定するエンティティをシリアライズ登録
this.config.registerSerialization(ApacheLog.class);
```

変化点検出を行う主要なコンポーネントには、以下のようなものがあります。

|クラス|説明|
|:--|:--|
|[ApacheLogSplitFunction](./src/main/java/acromusashi/stream/ml/loganalyze/ApacheLogSplitFunction.java)|JSON形式で表されているApacheのログデータを取得し、Javaのオブジェクトに変換します。|
|[ChangeFindFunction](./src/main/java/acromusashi/stream/ml/loganalyze/ChangeFindFunction.java)|Apacheのログのレスポンスタイムに対して変化点検出を行います。|
|[ApacheLogAggregator](./src/main/java/acromusashi/stream/ml/loganalyze/ApacheLogAggregator.java)|Apacheのログの統計を算出します。|

## Javadoc
[Javadoc](http://acromusashi.github.io/acromusashi-stream-ml/javadoc-0.2.0/)

## ライセンス
This software is released under the [MIT License](http://choosealicense.com/licenses/mit/), see LICENSE.txt.
