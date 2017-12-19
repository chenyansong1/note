---
title: SparkStreaming之基本工作原理
categories: spark  
tags: [spark]
---



接收实时输入数据流,然后将数据拆分成多个batch,比如每收集1s的数据封装为一个batch,然后将每个batch交给spark的计算引擎进行处理,最后会产生出一个结果数据流,其中的数据,也是由一个一个的batch所组成的

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/sparkstreaming_yuanli.png)


DStream

Spark Streaming提供了一种高级的抽象,叫做Dstream(Discretized Stream 离散流),他代表了一个持续不断的数据流,DStream可以通过输入数据源来创建,比如:Kafka,Flume,Kinesis,也可以通过对其他DStream应用高阶函数来创建,比如:map,reduce,join,window

DStream的内部,其实是一系列持续不断产生的RDD,DStream中的每个RDD都包含了一个时间段内的数据


对DStream应用的算子,比如map,其实在底层会被翻译为对DStream中每个RDD的操作,比如对一个DStream执行一个map操作,会产生一个新的DStream,但是,在底层,其实其原理为,对输入DStream中每个时间段的RDD,都应用一遍map操作,然后生成新的RDD,即作为新的DStream中的那个时间段的一个RDD,底层的RDD的Transformation操作,其实,还是由spark core的计算引擎来实现的,spark Streaming对spark core进行了一层封装,隐藏了细节,然后对开发人员提供了方便易用的高层次的API

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/DStream2.png)

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/SparkStreaming之基本工作原理.png)


