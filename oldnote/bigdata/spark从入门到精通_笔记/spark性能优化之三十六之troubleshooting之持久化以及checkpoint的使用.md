---
title: spark性能优化之三十六之troubleshooting之持久化以及checkpoint的使用
categories: spark  
tags: [spark]
---


错误的持久化使用方式

<!--more-->

usersRDD，想要对这个RDD做一个cache，希望能够在后面多次使用这个RDD的时候，不用反复重新计算RDD；可以直接使用通过各个节点上的executor的BlockManager管理的内存 / 磁盘上的数据，避免重新反复计算RDD。
```
usersRDD.cache()
usersRDD.count()
usersRDD.take()
```
上面这种方式，不要说会不会生效了，实际上是会报错的。会报什么错误呢？会报一大堆file not found的错误。

正确的持久化使用方式：
```
usersRDD
usersRDD = usersRDD.cache()
val cachedUsersRDD = usersRDD.cache()
```

之后再去使用usersRDD，或者cachedUsersRDD，就可以了。就不会报错了。所以说，这个是咱们的持久化的正确的使用方式。



![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_checkpoint.png)



checkpoint原理：

1、在代码中，用SparkContext，设置一个checkpoint目录，可以是一个容错文件系统的目录，比如hdfs；
2、在代码中，对需要进行checkpoint的rdd，执行RDD.checkpoint()；
3、RDDCheckpointData（spark内部的API），接管你的RDD，会标记为marked for checkpoint，准备进行checkpoint
4、你的job运行完之后，会调用一个finalRDD.doCheckpoint()方法，会顺着rdd lineage，回溯扫描，发现有标记为待checkpoint的rdd，就会进行二次标记，inProgressCheckpoint，正在接受checkpoint操作
5、job执行完之后，就会启动一个内部的新job，去将标记为inProgressCheckpoint的rdd的数据，都写入hdfs文件中。（备注，如果rdd之前cache过，会直接从缓存中获取数据，写入hdfs中；如果没有cache过，那么就会重新计算一遍这个rdd，再checkpoint）
6、将checkpoint过的rdd之前的依赖rdd，改成一个CheckpointRDD*，强制改变你的rdd的lineage。后面如果rdd的cache数据获取失败，直接会通过它的上游CheckpointRDD，去容错的文件系统，比如hdfs，中，获取checkpoint的数据。


说一下checkpoint的使用

1、SparkContext，设置checkpoint目录

```
sc.checkpointFile(“hdfs://...”)
```

2、对RDD执行checkpoint操作

```
rdd.checkpoint
```