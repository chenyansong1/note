---
title: spark性能优化十一之shuffle性能优化
categories: spark  
tags: [spark]
---


```
new SparkConf().set("spark.shuffle.consolidateFiles", "true")

spark.shuffle.consolidateFiles:是否开启shuffle block file的合并,默认是false
spark.reducer.maxSizeFlight: reduce task的拉取缓存,默认48M
spark.shuffle.file.buffer: map task的写磁盘缓存,默认32k
spark.shuffle.io.maxRetries:拉取失败的最大重试次数,默认3次
spark.shuffle.io.retryWait:拉取失败的重试间隔,默认5s
spark.shuffle.memoryFraction:用于reduce端聚合的内存比例,默认0.2,超过比例机会溢出到磁盘上

```








