## 概要
acromusashi-stream-mlは、acromusashi-streamをベースとした、  
ビッグデータをリアルタイム機械学習するためのプラットフォームです。  
acromusashi-stream-mlを使用することで、Storm上で機械学習アルゴリズムを  
リアルタイムで動作させることができます。   

## システム構成イメージ
![Abstract Image](http://acromusashi.github.io/acromusashi-stream-ml/images/MlAbstract.png)
#### Infinispan
acromusashi-stream-mlでは[Infinispan](http://infinispan.org/)を学習データのキャッシュ先として用いています。  
Infinispanはメモリ上でデータを保持する分散KVSデータグリッドで、データへの高速なアクセスが可能です。  
Infinispanのインストール方法／利用方法については[Infinispanの利用方法](https://github.com/acromusashi/acromusashi-stream-example/wiki/Infinispan-Usage)を確認してください。  
## スタートガイド
### Integration
```xml
<dependency>
  <groupId>jp.co.acroquest.acromusashi</groupId>
  <artifactId>acromusashi-stream-ml</artifactId>
  <version>0.2.0</version>
</dependency>
``` 

## 機械学習API
### クラスタリング
#### KMeans++
acromusashi.stream.ml.clustering.kmeans パッケージ配下のコンポーネントを使用することで  
KMeans++アルゴリズムを用いたクラスタリングを行うことができます。  
KmeansUpdaterにdataNotifierを設定することで1データ処理するごとに追加処理を実行可能です。  
batchNotifierを設定することで1バッチ分データ処理するごとに追加処理を実行可能です。   
実装例は[KmeansTopology](https://github.com/acromusashi/acromusashi-stream-example/blob/master/src/main/java/acromusashi/stream/example/ml/topology/KmeansTopology.java)を確認してください。  
### 外れ値検知
#### LOF
acromusashi.stream.ml.anomaly.lof パッケージ配下のコンポーネントを使用することで  
LOFアルゴリズムを用いた外れ値検知を行うことができます。  
実装例は[LofTopology](https://github.com/acromusashi/acromusashi-stream-example/blob/master/src/main/java/acromusashi/stream/example/ml/topology/LofTopology.java)を確認してください。
#### ChangeFinder
acromusashi.stream.ml.clustering.kmeans パッケージ配下のコンポーネントを使用することで  
ChangeFinderアルゴリズムを用いた外れ値検知を行うことができます。  
実装例は[EndoSnipeTridentTopology](https://github.com/acromusashi/acromusashi-stream-example/blob/master/src/main/java/acromusashi/stream/example/ml/topology/EndoSnipeTridentTopology.java)を確認してください。

## Javadoc
[Javadoc](http://acromusashi.github.io/acromusashi-stream-ml/javadoc-0.2.0/)

## Integration

## License
This software is released under the MIT License, see LICENSE.txt.

