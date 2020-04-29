---
title: spark性能优化之二十八之算子调优之reduceByKey本地聚合介绍
categories: spark  
tags: [spark]
---


reduceByKey实现原理图

<!--more-->


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_reduceByKey.png)



reduceByKey，相较于普通的shuffle操作（比如groupByKey），它的一个特点，就是说，会进行map端的本地聚合。

对map端给下个stage每个task创建的输出文件中，写数据之前，就会进行本地的combiner操作，也就是说对每一个key，对应的values，都会执行你的算子函数（) + _）


用reduceByKey对性能的提升：

1、在本地进行聚合以后，在map端的数据量就变少了，减少磁盘IO。而且可以减少磁盘空间的占用。
2、下一个stage，拉取数据的量，也就变少了。减少网络的数据传输的性能消耗。
3、在reduce端进行数据缓存的内存占用变少了。
4、reduce端，要进行聚合的数据量也变少了。


总结：

reduceByKey在什么情况下使用呢？

1、非常普通的，比如说，就是要实现类似于wordcount程序一样的，对每个key对应的值，进行某种数据公式或者算法的计算（累加、类乘）
2、对于一些类似于要对每个key进行一些字符串拼接的这种较为复杂的操作，可以自己衡量一下，其实有时，也是可以使用reduceByKey来实现的。但是不太好实现。如果真能够实现出来，对性能绝对是有帮助的。（shuffle基本上就占了整个spark作业的90%以上的性能消耗，主要能对shuffle进行一定的调优，都是有价值的）





