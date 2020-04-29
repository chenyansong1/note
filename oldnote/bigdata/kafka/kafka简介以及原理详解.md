---
title: kafka简介
categories: kafka   
toc: true  
tag: [kafka]
---



# Kafka是什么
在流式计算中，Kafka一般用来缓存数据，Storm通过消费Kafka的数据进行计算。如: KAFKA + STORM +REDIS

* Apache Kafka是一个开源消息系统，由Scala写成。是由Apache软件基金会开发的一个开源消息系统项目。
* Kafka是一个分布式消息队列：生产者、消费者的功能。它提供了类似于JMS的特性，但是在设计实现上完全不同，此外它并不是JMS规范的实现。
* Kafka对消息保存时根据Topic进行归类，发送消息者称为Producer,消息接受者称为Consumer,此外kafka集群有多个kafka实例组成，每个实例(server)成为broker。
* 无论是kafka集群，还是producer和consumer都依赖于zookeeper集群保存一些meta信息，来保证系统可用性


<!--more-->


# JMS是什么
## JMS的基础
JMS是什么：JMS是Java提供的一套技术规范
JMS干什么用：用来异构系统 集成通信，缓解系统瓶颈，提高系统的伸缩性增强系统用户体验，使得系统模块化和组件化变得可行并更加灵活
通过什么方式：生产消费者模式（生产者、服务器、消费者）


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/kafka/jian_jie/1.png)


jdk，kafka，activemq……




## JMS消息传输模型

* 点对点模式（一对一，消费者**主动拉取**数据，消息收到后消息清除）
点对点模型通常是一个基于拉取或者轮询的消息传送模型，这种模型从队列中请求信息，而不是将消息推送到客户端。这个模型的特点是发送到队列的消息被一个且只有一个接收者接收处理，即使有多个消息监听者也是如此。

* 发布/订阅模式（一对多，数据生产后，**推送**给所有订阅者）
发布订阅模型则是一个基于推送的消息传送模型。发布订阅模型可以有多种不同的订阅者，临时订阅者只在主动监听主题时才接收消息，而持久订阅者则监听主题的所有消息，即时当前订阅者不可用，处于离线状态。

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/kafka/jian_jie/2.png)

queue.put（object）  数据生产
queue.take(object)    数据消费


## JMS核心组件

* Destination：消息发送的目的地，也就是前面说的Queue和Topic。
* Message:从字面上就可以看出是被发送的消息
	* StreamMessage：Java 数据流消息，用标准流操作来顺序的填充和读取。
	* MapMessage：一个Map类型的消息；名称为 string 类型，而值为 Java 的基本类型。
	* TextMessage：普通字符串消息，包含一个String。
	* ObjectMessage：对象消息，包含一个可序列化的Java 对象
	* BytesMessage：二进制数组消息，包含一个byte[]。
	* XMLMessage:  一个XML类型的消息。
最常用的是TextMessage和ObjectMessage。
* Producer： 消息的生产者，要发送一个消息，必须通过这个生产者来发送
* MessageConsumer： 与生产者相对应，这是消息的消费者或接收者，通过它来接收一个消息



# 为什么需要消息队列

消息系统的核心作用就是三点：解耦，异步和并行

以用户注册的案列来说明消息系统的作用:

用户注册的一般流程
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/kafka/jian_jie/3.png)
问题：随着后端流程越来越多，每步流程都需要额外的耗费很多时间，从而会导致用户更长的等待延迟。

用户注册的并行执行
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/kafka/jian_jie/4.png)


问题：系统并行的发起了4个请求，4个请求中，如果某一个环节执行1分钟，其他环节再快，用户也需要等待1分钟。如果其中一个环节异常之后，整个服务挂掉了。

用户注册的最终一致
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/kafka/jian_jie/5.png)
1、保证主流程的正常执行、执行成功之后，发送MQ消息出去。
2、需要这个destination的其他系统通过消费数据再执行，最终一致。

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/kafka/jian_jie/6.png)


# Kafka核心组件

* Topic ：消息根据Topic进行归类
* Producer：发送消息者
* Consumer：消息接受者
* broker：每个kafka实例(server)
* Zookeeper：依赖集群保存meta信息


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/kafka/jian_jie/7.png)

