---
title: spark core之local模式提交spark的作业
categories: spark   
toc: true  
tag: [spark]
---


local模式下,没有所谓的master+worker这种概念
local模式相当于启动一个本地进程,然后在一个进程内,模拟spark集群中作业的运行,一个spark作业,就对应了进程的一个或者多个executor线程

在实际工作中,local模式,主要用于测试,最常见的就是我们的开发环境中,比如IDEA中,通常在local模式下,我们都会手工生成一份测试数据配合测试使用

local模式的提交脚本如下:
```
[root@hdp-node-01 spark]# cat word_count.sh 
/export/servers/spark/bin/spark-submit \
--class cn.spark.study.core.WordCount \
--num-executors 1 \
--driver-memory 100m \
--executor-memory 100m \
--executor-cores 1 \
/usr/xx/spark-study-java-0.0.1-SNAPSHOT-jar-with-dependencies.jar \

```


