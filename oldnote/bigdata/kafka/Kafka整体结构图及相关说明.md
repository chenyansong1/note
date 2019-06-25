---
title: Kafka整体结构图及相关说明
categories: kafka   
toc: true  
tag: [kafka]
---


# Kafka整体结构图

![](https://github.com/chenyansong1/note/blob/master/img/bigdata/kafka/structure/1.png)

 

# 架构图中的组件解释
* 类JMS消息队列，结合JMS中的两种模式，可以有多个消费者主动拉取数据，在JMS中只有点对点模式才有消费者主动拉取数据，kafka是一个生产-消费模型。
* Producer：生产者，只负责数据生产，生产者的代码可以集成到任务系统中。  数据的分发策略由producer决定，默认是由设定：partitioner.class=kafka.producer.DefaultPartitioner ，算法：Utils.abs(key.hashCode) % numPartitions
* Broker：当前服务器上的Kafka进程,俗称拉皮条。只管数据存储，不管是谁生产，不管是谁消费。**在集群中每个broker都有一个唯一brokerid，不得重复** ,一台kafka服务器就是一个broker。一个集群由多个broker组成。一个broker可以容纳多个topic。
* Topic:目标发送的目的地，这是一个逻辑上的概念，落到磁盘上是一个partition的目录。**partition的目录中有多个segment组合(index,log)，一个Topic对应多个partition[0,1,2,3]，一个partition对应多个segment组合。一个segment有默认的大小是1G**。每个partition可以设置多个副本(replication-factor 1),会从所有的副本中选取一个leader出来。所有读写操作都是通过leader来进行的。特别强调，和mysql中主从有区别，mysql做主从是为了读写分离，**在kafka中读写操作都是leader**。
* ConsumerGroup：数据消费者组，ConsumerGroup可以有多个，每个ConsumerGroup消费的数据都是一样的。可以把多个consumer线程划分为一个组，组里面所有成员共同消费一个topic的数据，组员之间不能重复消费。
* Partition：为了实现扩展性，一个非常大的topic可以分布到多个broker（即服务器）上，一个topic可以分为多个partition，每个partition是一个有序的队列。partition中的每条消息都会被分配一个有序的id（offset）。kafka只保证按一个partition中的顺序将消息发给consumer，不保证一个topic的整体（多个partition间）的顺序。


# Kafka消息的分发

* kafka集群中的任何一个broker都可以向producer提供metadata信息,这些metadata中包含"集群中存活的servers列表"/"partitions leader列表"等信息；
* 当producer获取到metadata信息之后, producer将会和Topic下所有partition leader保持socket连接；
* 消息由producer直接通过socket发送到broker，中间不会经过任何"路由层"，事实上，消息被路由到哪个partition上由producer客户端决定；
比如可以采用"random""key-hash""轮询"等,如果一个topic中有多个partitions,那么在producer端实现"消息均衡分发"是必要的。
* 在producer端的配置文件中,开发者可以指定partition路由的方式。
 默认是`defaultPartition  Utils.abs(key.hashCode) % numPartitions`
上文中的key是producer在发送数据时传入的，`produer.send(KeyedMessage(topic,myPartitionKey,messageContent))`


# kafka如何保证数据的完全生产
```

设置一个参数：request.required.acks = 0   
##该参数是消息的确认模式（生产者发送消息到partition，partition是否确认（ack））
## 0：不保证消息的到达确认，只管发送，低延迟但是会出现消息的丢失
## 1：发送消息，并会等待leader 收到消息之后发送ack
## -1：发送消息，当所有的follower都同步消息成功后发送ack（follower是partition的副本）
```
	
# broker如何保存数据
	在理论环境下，broker按照顺序读写的机制，可以每秒保存600M的数据。主要通过pagecache机制，尽可能的利用当前物理机器上的空闲内存来做缓存。
	当前topic所属的broker，必定有一个该topic的partition，partition是一个磁盘目录。partition的目录中有多个segment组合(index,log)

# partition如何分布在不同的broker上
```
	int i = 0
	list{kafka01,kafka02,kafka03}//这是所有的broker服务器
	
	for(int i=0;i<5;i++){//在创建topic的时候会指定分区数量,假如是5
		brIndex = i%broker; //broker=3
		hostName = list.get(brIndex)
	}
```
	
# consumerGroup的组员和partition之间如何做负载均衡
```
	最好是一一对应，一个partition对应一个consumer。
	如果consumer的数量过多，必然有空闲的consumer。
	
	算法：
		假如topic1,具有如下partitions: P0,P1,P2,P3
		加入group中,有如下consumer: C1,C2
		首先根据partition索引号对partitions排序: P0,P1,P2,P3
		根据consumer.id排序: C0,C1
		计算倍数: M = [P0,P1,P2,P3].size / [C0,C1].size,本例值M=2(向上取整)
		然后依次分配partitions: C0 = [P0,P1],C1=[P2,P3],即Ci = [P(i * M),P((i + 1) * M -1)]
                #就是consumer排序之后,每排好序的consumer每个分M个排好序的分区
```


# Consumer与topic关系
本质上kafka只支持Topic；
* 每个group中可以有多个consumer，每个consumer属于一个consumer group；
通常情况下，一个group中会包含多个consumer，这样不仅可以提高topic中消息的并发消费能力，而且还能提高"故障容错"性，如果group中的某个consumer失效那么其消费的partitions将会有其他consumer自动接管。
* 对于Topic中的一条特定的消息，只会被订阅此Topic的每个group中的其中一个consumer消费，此消息不会发送给一个group的多个consumer；
那么一个group中所有的consumer将会交错的消费整个Topic，每个group中consumer消息消费互相独立，我们可以认为一个group是一个"订阅"者。
* 在kafka中,一个partition中的消息只会被group中的一个consumer消费(同一时刻)；
一个Topic中的每个partions，只会被一个"订阅者"中的一个consumer消费，不过一个consumer可以同时消费多个partitions中的消息。
* kafka的设计原理决定,对于一个topic，同一个group中不能有多于partitions个数的consumer同时消费，否则将意味着某些consumer将无法得到消息。
* kafka只能保证一个partition中的消息被某个consumer消费时是顺序的；事实上，从Topic角度来说,当有多个partitions时,消息仍不是全局有序的。



 ![](https://github.com/chenyansong1/note/blob/master/img/bigdata/kafka/structure/2.png)
















