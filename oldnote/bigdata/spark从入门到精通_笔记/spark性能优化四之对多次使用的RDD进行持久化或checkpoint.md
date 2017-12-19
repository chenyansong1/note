---
title: spark性能优化四之对多次使用的RDD进行持久化或checkpoint
categories: spark  
tags: [spark]
---


如果程序中,对某一个RDD,基于他进行了多次Transformation或者action操作,那么就非常有必要对其进行持久化操作,以避免对一个RDD反复进行计算


此外,如果要保证在RDD的持久化数据可能丢失的情况下,还要保证高性能,那么可以对RDD进行checkpoint操作



![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/checkpoint_cache.png)




