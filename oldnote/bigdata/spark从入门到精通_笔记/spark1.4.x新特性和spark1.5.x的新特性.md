---
title: spark1.4.x新特性和spark1.5.x的新特性
categories: spark  
tags: [spark]
---

# spark1.4.x新特性

1.spark core

1.1.提供REST API供外界开发者获取spark内部的各种信息(jobs/stages/tasks/storage info),基于这些API,可以搭建自己的spark监控系统
1.2.shuffle阶段,默认将map端写入磁盘的数据进行序列化,优化IO性能
1.3.钨丝计划(Project Tungsten),提供了UNSafeShuffleManager,使用缓存友好的排序算法,降低了shuffle的内存使用,提高了排序性能


2.spark streaming
2.1.提供了新的spark streaming的UI,能够更好,更清晰的监控spark streaming应用程序的运行状况
2.2.支持kafka0.8.2版本

3.spark sql and DataFrame
3.1.支持ORCFile
3.2.提供了一些window function(窗口函数)
3.3.优化了join的性能



# spark1.5.x新特性
1.DataFrame底层执行的性能优化(钨丝计划的第一阶段)
1.1.spark自己来管理内存,而不再依靠JVM管理内存,这样就可以避免JVM GC的性能开销,并且能够控制OOM的问题
1.2.java对象直接使用内部的二进制格式化存储和计算,省去了序列化和反序列的性能开销,而且更加节省内存开销
1.3.完善了shuffle阶段的UnsafeShuffleManager,增加了不少新功能,优化shuffle性能
1.4.默认使用code-gen,使用cache-aware算法,加强了join,aggregation,shuffle,sorting的性能,增强了window function的性能,性能比1.4.x版本提高了数倍

2.DataFrame
2.1.实现了新的聚合函数接口,AggregateFunction2,并且提供了7个新的内置聚合函数
2.2.实现了100多个新的expression function,例如unix_timestamp等,增强了对NaN的处理
2.3.支持连接不通版本的hive metastore
2.4.支持Parquet1.7

3.Spark Streaming,更完善的Python支持,非试验的Kafka Direct API等等


