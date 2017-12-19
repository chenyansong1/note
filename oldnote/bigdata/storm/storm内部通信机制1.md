---
title: storm内部通信机制1
categories: storm   
toc: true  
tag: [storm]
---


# 1.storm内部各组件之间的关系


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_tongxin/1.png)
 
* 一个物理机（Supervisor）上可以启多个Worker JVM
* 一个Worker JVM可以有多个Executor
* 一个Executor可以有多个task（SpoutTask、BoltTask）

<!--more-->

# 2.内部通信机制(一个worker内，一个Executor)



![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_tongxin/2.png)
 
>对worker来说：

* 对于worker进程来说，为了管理流入和传出的消息，每个worker进程有一个独立的接收线程，对配置的TCP端口supervisor.slots.ports进行监听
 一个worker进程运行一个专用的接收线程来负责将外部发送过来的消息移动到对应的executor线程的incoming-queue中
* 对应Worker接收线程，每个worker存在一个独立的发送线程，它负责从worker的transfer-queue中读取消息，并通过网络发送给其他worker
   transfer-queue的大小由参数topology.transfer.buffer.size来设置。transfer-queue的每个元素实际上代表一个tuple的集合
* 每个worker进程控制一个或多个executor线程，用户可在代码中进行配置。<font color=red>其实就是我们在代码中设置的并发度个数</font>

>对executor来说：

* 每个executor有自己的incoming-queue和outgoing-queue，Worker接收线程将收到的消息通过task编号传递给对应的executor(一个或多个)的incoming-queues;
   executor的incoming-queue的大小用户可以自定义配置
   executor的outgoing-queue的大小用户可以自定义配置
* 每个executor有单独的线程分别来处理spout/bolt的业务逻辑，业务逻辑输出的中间数据会存放在outgoing-queue中，当executor的outgoing-queue中的tuple达到一定的<font color=red>阀值</font>，executor的发送线程将批量获取outgoing-queue中的tuple,并发送到transfer-queue中。


# 3.内部通信机制(一个worker内，多个Executor)



![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_tongxin/3.png)


 
