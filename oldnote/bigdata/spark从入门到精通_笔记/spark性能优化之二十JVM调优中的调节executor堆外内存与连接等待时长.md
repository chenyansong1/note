---
title: spark性能优化之二十JVM调优中的调节executor堆外内存与连接等待时长
categories: spark  
tags: [spark]
---



# executor堆外内存

## executor堆外内存报错的场景

有时候,如果你的spark作业处理的数据量特别大,几亿数据量,然后spark作业一运行,时不时的报错
```
shuffle file cannot find 
executor,task lost 
out of memory(内存溢出)

```

<!--more-->

可能是说executor的堆外内存不太够用,导致executor在运行的过程中,可能会内存溢出,然后导致后续的stage的task的在运行的时候,可能要从executor中拉取shuffle map output文件,但是executor可能已经挂掉了,关联的BlockManager也没有了,所以可能回报
```
shuffle output file not find
resubmitting task
executor lost 
```
此时spark作业彻底崩溃

上述情况下,就可以考虑调节一下executor的堆外内存,也许就可以避免报错,此外有时,堆外内存调节的比较大的时候,对于性能来说,也会带来一定的提升



## executor堆外内存报错的原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_executor_memory.png)



## executor堆外内存配置

```
--conf spark.yarn.executor.memoryOverhead=2048
```

spark-submit脚本里面，去用--conf的方式，去添加配置；一定要注意！！！切记，不是在你的spark作业代码中，用new SparkConf().set()这种方式去设置，不要这样去设置，是没有用的！一定要在spark-submit脚本中去设置。

spark.yarn.executor.memoryOverhead（看名字，顾名思义，针对的是基于yarn的提交模式）

默认情况下，这个堆外内存上限大概是300多M；后来我们通常项目中，真正处理大数据的时候，这里都会出现问题，导致spark作业反复崩溃，无法运行；此时就会去调节这个参数，到至少1G（1024M），甚至说2G、4G

通常这个参数调节上去以后，就会避免掉某些JVM OOM的异常问题，同时呢，会让整体spark作业的性能，得到较大的提升。


# 连接等待时长


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_executor_wait_timeout.png)




# 总结

为什么在这里讲这两个参数呢？

因为比较实用，在真正处理大数据（不是几千万数据量、几百万数据量），几亿，几十亿，几百亿的时候。很容易碰到executor堆外内存，以及gc引起的连接超时的问题。file not found，executor lost，task lost。

调节上面两个参数，还是很有帮助的。

这是实际生产中提交的spark-submit
```
/usr/local/spark/bin/spark-submit \
--class com.ibeifeng.sparkstudy.WordCount \
--num-executors 80 \
--driver-memory 6g \
--executor-memory 6g \
--executor-cores 3 \
--master yarn-cluster \
--queue root.default \
--conf spark.yarn.executor.memoryOverhead=2048 \
--conf spark.core.connection.ack.wait.timeout=300 \
/usr/local/spark/spark.jar \
${1}

//${1}筛选条件
```






