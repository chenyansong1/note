---
title: spark core之主要的几个术语
categories: spark   
toc: true  
tag: [spark]
---


术语|解释
:--:|:-----
Application|spark应用程序,就是用户基于spark API开发的程序,一定是通过一个有main方法的类执行的
Application jar|这个就是把写好的spark工程，打包成一个jar包，其中包括了所有的第三方jar依赖包，比如java中，就用maven+assembly插件打包最方便
Driver|在使用spark-submit提交应用的时候,会指定一个主类,这个类有一个main方法,driver进程就是在运行程序中的main方法的进程,这就是driver
cluster manager|集群管理器,就是为每个spark application，在集群中调度和分配资源的组件，比如Spark Standalone、YARN、Mesos等
deploy mode|部署模式，无论是基于哪种集群管理器(Standalone、YARN、Mesos)，spark作业部署或者运行模式，都分为两种，client和cluster，client模式下driver运行在提交spark作业的机器上,即执行应用的main类(主要用于测试)；cluster模式下，driver运行在spark集群中的某一个节点上
Worker Node|集群中的工作节点，能够运行executor进程，运行作业代码的节点
Executor|集群管理器为application分配的进程，运行在worker节点上，负责执行作业的任务，并将数据保存在内存或磁盘中，每个application都有自己的executor
Job|每个spark application，根据你执行了多少次action操作，就会有多少个job
Stage|根据是否有shuffle,每个job都会划分为多个stage（阶段），每个stage都会有对应的一批task，分配到executor上去执行
Task|driver发送到executor上执行的计算单元，每个task负责在一个阶段（stage），处理一小片数据，计算出对应的结果

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/spark core之主要的几个术语.png)
