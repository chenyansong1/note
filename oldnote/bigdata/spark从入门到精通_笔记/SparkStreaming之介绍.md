---
title: SparkStreaming之介绍
categories: spark  
tags: [spark]
---


SparkStreaming其实就是一种spark提供的一种实时计算框架,他的底层组件或者概念,其实还是最核心的RDD,只不过,针对实时计算的特点,在RDD之上,进行了一层封装,叫做Dstream,就像spark sql针对数据查询应用提供了一种基于RDD之上的全新的概念叫DataFrame一样


spark Streaming是spark core API的一种扩展,他可以用于进行大规模,高吞吐,容错的实时数据流的处理,他支持从多种数据源中消费数据,比如:kafka,flume,Twitter,ZeroMQ,或者TCP Socket,并且能够使用类似高阶函数的复杂算法来进行数据处理,比如:map,reduce,join,和window,处理后的数据可以被保存到文件系统,数据库等存储中


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/sparkstreaming_jianjie.png)









