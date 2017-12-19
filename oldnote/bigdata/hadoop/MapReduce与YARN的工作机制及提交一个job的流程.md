---
title: MapReduce与YARN的工作机制及提交一个job的流程
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---




# 1 YARN概述
Yarn是一个资源调度平台，负责为运算程序提供服务器运算资源，相当于一个分布式的操作系统平台，而mapreduce等运算程序则相当于运行于操作系统之上的应用程序

<!--more-->


# 2.YARN的重要概念
* yarn并不清楚用户提交的程序的运行机制
* yarn只提供运算资源的调度（用户程序向yarn申请资源，yarn就负责分配资源）
* yarn中的主管角色叫ResourceManager
* yarn中具体提供运算资源的角色叫NodeManager
* 这样一来，yarn其实就与运行的用户程序完全解耦，就意味着yarn上可以运行各种类型的分布式运算程序（mapreduce只是其中的一种），比如mapreduce、storm程序，spark程序，tez ……
* 所以，spark、storm等运算框架都可以整合在yarn上运行，只要他们各自的框架中有符合yarn规范的资源请求机制即可
* Yarn就成为一个通用的资源调度平台，从此，企业中以前存在的各种运算集群都可以整合在一个物理集群上，提高资源利用率，方便数据共享


# 3.提交一个job的流程示意图


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/yarn/mapreduce_yarn.png "请在新标签页中打开")


* 提交job所需的资源文件说明
job.split #任务的切片信息
job.xml #里面有各种配置组件信息
wordcount.jar	#待运行的程序jar包

