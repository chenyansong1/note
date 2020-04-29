---
title: spark内核源码九之shuffle原理剖析与源码分析
categories: spark  
tags: [spark]
---


# 在spark中,什么情况下会发生shuffle?

reduceByKey,groupByKey,sortByKey,countByKey,join,cogroup


# 默认的shuffle操作的原理 vs 优化后的shuffle操作的原理


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/spark内核源码九之shuffle原理剖析与源码分析.png)



# shuffle相关源码

shuffle的写源码:

ShuffleMapTask.runTask()是入口,writer默认是HashShuffleWriter



![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/shuffle的IO写.png)



shuffle的读源码:

可以从一个rdd开始入手,如ShuffledRDD的compute方法

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/shuffle的读.png)








