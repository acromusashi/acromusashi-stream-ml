## 概要
acromusashi-stream-ml は、[acromusashi-stream](https://github.com/acromusashi/acromusashi-stream) をベースとした、オンライン機械学習を行うためのプラットフォームです。  
acromusashi-stream-ml を利用することで、機械学習の処理を[Storm](http://storm-project.net/)上でリアルタイムで動作させることができます

## システム構成イメージ
![Abstract Image](http://acromusashi.github.io/acromusashi-stream-ml/images/MlAbstract.png)

#### Infinispan
acromusashi-stream-ml では[Infinispan](http://infinispan.org/)を学習データのキャッシュ先として用いています。  
Infinispanはメモリ上でデータを保持する分散KVSデータグリッドで、データへの高速なアクセスが可能です。  
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

#### KMeans++
acromusashi.stream.ml.clustering.kmeans パッケージ配下のコンポーネントを使用することでKMeans++アルゴリズムを用いたクラスタリングを行うことができます。  
KmeansUpdaterにdataNotifierを設定することで1データ処理するごとに追加処理を実行可能です。  
batchNotifierを設定することで1バッチ分データ処理するごとに追加処理を実行可能です。   
実装例は[KmeansTopology](https://github.com/acromusashi/acromusashi-stream-example/blob/master/src/main/java/acromusashi/stream/example/ml/topology/KmeansTopology.java)を確認してください。  

### 異常値

#### LOF
acromusashi.stream.ml.anomaly.lof パッケージ配下のコンポーネントを使用することでLOFアルゴリズムを用いた外れ値検知を行うことができます。  
実装例は[LofTopology](https://github.com/acromusashi/acromusashi-stream-example/blob/master/src/main/java/acromusashi/stream/example/ml/topology/LofTopology.java)を確認してください。

#### ChangeFinder
acromusashi.stream.ml.anomaly.cf パッケージ配下のコンポーネントを使用することでChangeFinderアルゴリズムを用いた変化点検出を行うことができます。  
実装例は[EndoSnipeTridentTopology](https://github.com/acromusashi/acromusashi-stream-example/blob/master/src/main/java/acromusashi/stream/example/ml/topology/EndoSnipeTridentTopology.java)を確認してください。

## Javadoc
[Javadoc](http://acromusashi.github.io/acromusashi-stream-ml/javadoc-0.2.0/)

## ライセンス
This software is released under the [MIT License](http://choosealicense.com/licenses/mit/), see LICENSE.txt.
