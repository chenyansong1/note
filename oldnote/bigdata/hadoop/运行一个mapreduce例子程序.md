---
title: 运行一个mapreduce例子程序
categories: hadoop
toc: true
tag: [hadoop]
---


hadoop-mapreduce-examples-2.6.4.jar是一个hadoop中自带的一个词频统计程序

```

#例子程序的路径
share/hadoop/mapreduce/

#需要启动hdfs和yarn
./sbin/start-dfs.sh
./sbin/start-yarn.sh

#执行程序
hadoop jar hadoop-mapreduce-examples-2.6.4.jar wordcount    /wordcount/input/    /wordcount/output

wordcount 是运行的主类(会找wordcount中的main)
 /wordcount/input/      输入目录
 /wordcount/output      输出目录

```
