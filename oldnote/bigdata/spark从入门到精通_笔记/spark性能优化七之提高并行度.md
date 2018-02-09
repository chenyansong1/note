---
title: spark性能优化七之提高并行度
categories: spark  
tags: [spark]
---




![](http://ols7leonh.bkt.clouddn.com/assert/img/bigdata/spark从入门到精通_笔记/提高并行度原理.png)



实际上spark集群的资源并不一定会被充分利用到,所以要尽量设置合理的并行度,来充分地利用集群的资源,才能充分提高spark应用程序的性能

spark会自动设置以文件作为输入源的rdd的并行度,依据其大小,比如HDFS,就会给每一个block创建一个partition,也依据这个设置并行度,对于reduceByKey等会发生shuffle的操作,就使用并行度最大的父rdd的并行度即可

可以手动使用textFile(),parallelize()等方法的第二个参数来设置并行度,也可以是使用
```
spark.default.parallelism
```
参数来统一设置并行度,spark官方的推荐是,给集群中的每一个cpu core设置2-3个task

比如说,spark-submit设置了executor数量为10个,每个executor要求分配2个core,那么application总共会有20个core,此时可以设置
```
SparkConf().set("spark.default.parallelism", "60")
```
即设置集群cpu数量的2-3倍