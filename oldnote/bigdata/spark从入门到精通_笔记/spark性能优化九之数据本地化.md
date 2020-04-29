---
title: spark性能优化九之数据本地化
categories: spark  
tags: [spark]
---


数据本地化背景

数据本地化对于spark job性能有着巨大的影响,如果数据以及要计算他的代码是在一起的,那么性能当然非常高,但是,如果数据和计算他的代码是分开的,那么其中之一必须到另外一方的机器上,通常来说,移动代码到其他节点上,会比移动数据到代码所在的节点上去,速度要快,因为代码比较小,spark也正是基于这个数据本地化的原则来构建task调度算法的

数据本地化,指的是:数据离计算他的代码有多近,基于数据距离代码的举例,有几种数据本地化的级别(其实在剖析TaskSchedulerImpl中有讲到,可以回顾下):
1.PROCESS_LOCAL:数据和计算他的代码在同一个JVM进程中
2.NODE_LOCAL:数据和计算他的代码在一个节点上,但是不在一个进程中,比如在不同的executor进行中,或者是数据在HDFS文件的block中
3.NO_PREF:数据从哪里过来,性能都是一样的
4.RACK_LOCAL:数据和计算他的代码在一个机架上
5.ANY:数据可能子任意地方,比如其他网络环境内,或者其他机架上




数据本地化原理


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/数据本地化原理.png)



spark倾向于使用最好的本地化级别来调度task,但是这是不可能的,如果没有任何未处理的数据在空闲的executor上,那么spark就会放低本地化级别,这时有两个选择:第一,等待直到executor上的cpu释放出来,那么就分配task过去;第二,立即在任意一个executor上启动一个task

spark默认会等待一会儿,来期望task要处理的数据所在的节点神的executor空想出一个cpu,从而将task分配过去,只要超过了时间,那么spark就会将task分配到其他任意一个空闲的executor上

可以设置一个参数:spark.locality系列参数,来调节spark等待task可以进行数据本地化的时间
```
//通用的
spark.locality.wait(3000 毫秒)

//在等待node级别的时候,等待的时间
spark.locality.wait.node
//在等待process级别的时候,等待的时间
spark.locality.wait.process
//在等待rack级别的时候,等待的时间
spark.locality.wait.rack
```

