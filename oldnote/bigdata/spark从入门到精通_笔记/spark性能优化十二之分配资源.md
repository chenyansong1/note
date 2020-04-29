---
title: spark性能优化十二之分配资源
categories: spark  
tags: [spark]
---



1.分配哪些资源?

executor,cpu per executor, memory per executor, drivere memory

<!--more-->


2.在哪里分配这些资源?
在实际的生产环境中,提交的spark作业,用的是spark-submit shell脚本,我们在里面调整对应的参数配置

```
/usr/local/spark/bin/spark-submit \
--class cn.spark.test.WordCount \
#配置executor的数量
--num-executor 3 \
#配置driver的内存(影响不大)
--driver-memory 100m \	
#配置每个executor的内存大小
--executor-memory 100m \
#配置每个executor的cpu core数量
--executor-cores 3 \
/usr/local/SparkTest-xx.dependencies.jar \

```


3.调节到多大,算是最大呢?
第一种,spark Standalone模式下,公司在集群上,搭建了一套spark集群,你心里应该清楚每台机器还能给你使用的大概还有多少内存和cpu core,那么设置的时候,就根据这个实际情况,去调节每个spark作业的资源分配,比如说你的每台机器能够给你使用4G内存,2个cpu core,供20台机器,那么平均每个executor就是4G内存,2个cpu core(根据每个机器上跑一个executor)

第二种,Yarn集群,yarn里面有一个资源队列的概念,此时就应该去查看你的spark作业要提交到的资源队列,大概有多少资源?比如此时的资源队列中有500G内存,100个cpu core,如果你想要跑50个executor,那么每个executor的资源是10G内存,2cpu core

调整参数的一个原则:你能使用的资源有多大,就尽量去调节到最大的大小(其中executor的数量,几十个到上百个不等,然后将每个executor的内存调节到最大,cpu core调节到最大)


4.为什么调节了资源以后,性能可以提升?


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_ziyuan.png)













