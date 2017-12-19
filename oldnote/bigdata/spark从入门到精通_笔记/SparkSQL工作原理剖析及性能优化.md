---
title: SparkSQL工作原理流程剖析及性能优化
categories: spark  
tags: [spark]
---



# SparkSQL工作原理流程



![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/SparkSQL工作原理流程.png)



# spark sql核心源码






# spark sql优化的点

1.设置shuffle过程中的并行度:spark.sql.shuffle.partitions(SqlContext.setconf())
2.在hive数据仓库建设的过程中,合理设置数据类型,比如能设置为int的,就不要设置为BigInt,减少数据类型导致的不必要的内存开销
3.编写sql时尽量列出列名,不要写select *
4.并行处理查询结果,对于spark sql查询的结果,如果数据量比较大,比如超过1000条,那么就不要一次性collect到Driver里面,使用foreach()算子,并行处理查询结果
5.缓存表:对于一条sql语句中可能多次使用到的表,可以对其进行缓存,使用
```
SQLContext.cacheTable(tableName)
//或者
DataFrame.cache()
```
spark sql会用内存列存储的格式进行表的缓存,然后spark sql就可以仅仅扫描需要使用的列,并且自动优化压缩,来最小化内存使用和GC开销,SQLContext.uncacheTable(tableName)可以将表从缓存中移除,用SQLContext.setConf(),设置
```
spark.sql.inMemoryColumnarStorage.batchSize  
//默认是1000
```
可以配置列存储的单位
6.广播join表:
```
spark.sql.autoBroadcastJoinThreshold 
//默认10485760(10M) ,会将10M以内的表自动广播出去
```

在内存够用的情况下,可以增加其大小,可以让更多的表被自动广播出去,可以将join中的较小的表广播出去,而不用进行网络数据传输了
7.钨丝计划:
```
spark.sql.tungsten.enabled
//默认是true
```
自动管理内存



