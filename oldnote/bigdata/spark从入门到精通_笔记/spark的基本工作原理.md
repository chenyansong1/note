---
title: spark的基本工作原理
categories: spark   
toc: true  
tag: [spark]
---



下面的这张图表示的是spark的基本的工作原理图(简图)
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/spark的基本工作原理.png)


下面是对RDD的概念解释
1.RDD在抽象上来说是一种元素的集合,包含了数据,他是被分区的,分为多个分区,每个分区分布在集群中的不同节点上,从而让RDD中的数据可以被并行操作(分布式数据集)
2.RDD的创建:通过HDFS文件或hive表创建;通过应用程序的集合来创建
3.RDD的数据默认情况下存放在内存中,但是在内存资源不足时,spark会自动将RDD数据写入磁盘(弹性)
4.RDD最重要的特性是:提供了容错性,可以自动从节点失败中恢复过来,即:如果某个节点山的RDD partition因为节点故障,导致数据丢了,那么RDD会自动通过自己的数据来源重新计算该partition,这一切对使用者是透明的
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/rdd的概念理解.png)