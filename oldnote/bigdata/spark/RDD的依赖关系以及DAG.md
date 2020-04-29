---
title: RDD的依赖关系以及DAG
categories: spark  
tags: [spark]
---

# RDD的依赖关系


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/DAG/1.jpg)

 
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/DAG/2.png)
 
# DAG

&emsp;DAG(Directed Acyclic Graph)叫做有向无环图，原始的RDD通过一系列的转换就就形成了DAG，根据RDD之间的依赖关系的不同将DAG划分成不同的Stage，对于窄依赖，partition的转换处理在Stage中完成计算。对于宽依赖，由于有Shuffle的存在，只能在parent RDD处理完成后，才能开始接下来的计算，因此宽依赖是划分Stage的依据。

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/DAG/3.jpg)

