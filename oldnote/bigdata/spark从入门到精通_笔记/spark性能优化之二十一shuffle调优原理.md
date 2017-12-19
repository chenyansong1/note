---
title: spark性能优化之二十一shuffle调优之合并map端输出文件
categories: spark  
tags: [spark]
---


# shuffle的原理

什么样的情况下，会发生shuffle？

在spark中，主要是以下几个算子：groupByKey、reduceByKey、countByKey、join，等等。

<!--more-->

什么是shuffle？

groupByKey，要把分布在集群各个节点上的数据中的同一个key，对应的values，都给集中到一块儿，集中到集群中同一个节点上，更严密一点说，就是集中到一个节点的一个executor的一个task中。

然后呢，集中一个key对应的values之后，才能交给我们来进行处理，<key, Iterable<value>>；reduceByKey，算子函数去对values集合进行reduce操作，最后变成一个value；countByKey，需要在一个task中，获取到一个key对应的所有的value，然后进行计数，统计总共有多少个value；join，RDD<key, value>，RDD<key, value>，只要是两个RDD中，key相同对应的2个value，都能到一个节点的executor的task中，给我们进行处理。




![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_shuffle_yuanli.png)



# 合并map端输出文件

如果不合并map端输出文件的话，会怎么样？


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_shuffle_solidate.png)

```
new SparkConf().set("spark.shuffle.consolidateFiles", "true")
//这个参数在1.5.2之前是存在的,1.6.1之后,程序中默认是开启map端合并的

```
开启shuffle map端输出文件合并的机制；默认情况下，是不开启的，就是会发生如上所述的大量map端输出文件的操作，严重影响性能。


开启map合并的效果,如图:

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_shuffle_solidate.png)



实际生产环境的条件：
100个节点（每个节点一个executor）：100个executor
每个executor：2个cpu core
总共1000个task：每个executor平均10个task

每个节点，2个cpu core，有多少份输出文件呢？2 * 1000 (假设reduce端的task数量为1000)= 2000个,总共100个节点，总共创建多少份输出文件呢？100 * 2000 = 20万个文件,相比较开启合并机制之前的情况，100万个,map端输出文件，在生产环境中，立减5倍！


合并map端输出文件，对咱们的spark的性能有哪些方面的影响呢？

1、map task写入磁盘文件的IO，减少：100万文件 -> 20万文件
2、第二个stage，原本要拉取第一个stage的task数量份文件，1000个task，第二个stage的每个task，都要拉取1000份文件，走网络传输；合并以后，100个节点，每个节点2个cpu core，第二个stage的每个task，主要拉取100 * 2 = 200个文件即可；网络传输的性能消耗是不是也大大减少

分享一下，实际在生产环境中，使用了spark.shuffle.consolidateFiles机制以后，实际的性能调优的效果：对于上述的这种生产环境的配置，性能的提升，还是相当的客观的。spark作业，5个小时 -> 2~3个小时。

大家不要小看这个map端输出文件合并机制。实际上，在数据量比较大，你自己本身做了前面的性能调优，executor上去->cpu core上去->并行度（task数量）上去，shuffle没调优，shuffle就很糟糕了；大量的map端输出文件的产生。对性能有比较恶劣的影响。

这个时候，去开启这个机制，可以很有效的提升性能。


