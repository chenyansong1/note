---
title: storm的组件(nimbus、supervisor、worker、spout、bolt)关系图示
categories: storm   
toc: true  
tag: [storm]
---



下面是storm中各组件的关系示意图:

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/component/1.png)


<!--more-->

* 并发度：用户指定的一个任务，可以被多个线程执行，并发度的数量等于线程的数量。一个任务的多个线程，会被运行在多个Worker（JVM）上，有一种类似于平均算法的负载均衡策略。尽可能减少网络IO，和Hadoop中的MapReduce中的本地计算的道理一样。
* Nimbus：任务分配
* Supervisor：接受任务，并启动worker。worker的数量根据端口号来的。
* Worker:执行任务的具体组件（其实就是一个JVM）,可以执行两种类型的任务，Spout任务或者bolt任务。
* Task：Task=线程=executor。 一个Task属于一个Spout或者Bolt并发任务。
* Zookeeper：保存任务分配的信息、心跳信息、元数据信息。
* Worker与topology关系
 一个worker只属于一个topology,每个worker中运行的task只能属于这个topology。    反之，一个topology包含多个worker，其实就是这个topology运行在多个worker上。
一个topology要求的worker数量如果不被满足，集群在任务分配时，根据现有的worker先运行topology。如果当前集群中worker数量为0，那么最新提交的topology将只会被标识active，不会运行，只有当集群有了空闲资源之后，才会被运行。
 
 
# worker、Executor、task的关系


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/component/2.png)

* Task：Spout/Bolt在运行时所表现出来的实体，都称为Task，一个Spout/Bolt在运行时可能对应一个或多个Spout Task/Bolt Task，与实际在编写Topology时进行配置有关。
* Worker：运行时Task所在的一级容器，Executor运行于Worker中，一个Worker对应于Supervisor上创建的一个JVM实例
* Executor：运行时Task所在的直接容器，在Executor中执行Task的处理逻辑；一个或多个Executor实例可以运行在同一个Worker进程中，一个或多个Task可以运行于同一个Executor中；在Worker进程并行的基础上，Executor可以并行，进而Task也能够基于Executor实现并行计算
 

  
