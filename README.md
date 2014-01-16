## 概要
acromusashi-stream-mlは、acromusashi-streamをベースとした、ビッグデータをリアルタイム機械学習するためのプラットフォームです。  
acromusashi-stream-mlを使用することで、Storm上で機械学習アルゴリズムをリアルタイムで動作させることができます。  

## システム構成イメージ
![Abstract Image](http://acromusashi.github.io/acromusashi-stream-ml/images/MlAbstract.png)

## スタートガイド
### ビルド環境
* JDK 7以降  
* Maven 2.2.1以降

### ビルド手順
* ソースをGitHubから取得後、取得先ディレクトリに移動し下記のコマンドを実行してください。  
** コマンド実行の結果、 acromusashi-stream-ml.zip が生成されます。  

```
# mvn clean package  
```  

## アルゴリズム一覧
### クラスタリング
#### KMeans++
acromusashi.stream.ml.clustering.kmeans パッケージ配下のコンポーネントを使用することで  
KMeans++アルゴリズムを用いたクラスタリングを行うことができます。  
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

