---
title: storm相关问题集锦
categories: storm   
toc: true  
tag: [storm]
---


下面是关于storm的一些常见的问题:

<!--more-->


1.如何保证一条数据在经过kafka和storm之时，消息被完整处理。
```
		kakfa，生产和消费  storm，ackfail
		producer---<同步/异步（缓冲区）>------------------->broker-------------------zk-------------> KafkaSpout------------------------------->Bolt(订单ID,去重)
				'缓冲区的时间阈值和数量阈值'					partition的目录         offset=1w          ack fail(重发)								Redis Set
										'消息响应机制：ack (0,1,-1)'						   自定义的Spout需要Map或外部存储保存数据
				
																					一批一批拉取(1w+num)
																					时间阈值和数量阈值触发 更新操作 offset 1w+num 
																					正在此时，KafkaSpout失败了，会导致重复消费

```
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/questions.png)

2.Kafka和storm组合有没有丢数据可能。

&emsp;由上图知：可能丢数据的地方有：
* 在buff缓冲区堆积过多的数据，而向partition中发送的过程较慢，那么缓冲区有两种情况：
	* 1.1. 删除缓冲区数据
	* 1.2. buff继续阻塞


3.对重复消费的问题，该如何解决？

&emsp;将已经处理的消息存入redis中，然后将ack中返回的Tuple去redis中去比较，如果存在那么就将该Tuple抛弃，否则继续发往下一个bolt



4.Storm怎么处理重复的tuple？
 
因为Storm要保证tuple的可靠处理，当tuple处理失败或者超时的时候，spout会fail并重新发送该tuple，那么就会有tuple重复计算的问题。这个问题是很难解决的，storm也没有提供机制帮助你解决。一些可行的策略：
（1）不处理，这也算是种策略。因为实时计算通常并不要求很高的精确度，后续的批处理计算会更正实时计算的误差。
（2）使用第三方集中存储来过滤，比如利用mysql,memcached或者redis根据逻辑主键来去重。
（3）使用bloom filter做过滤，简单高效。


5.你们有没有想过如果某一个task节点处理的tuple一直失败，消息一直重发会怎么样？



&emsp;我们都知道，spout作为消息的发送源，在没有收到该tuple来至左右bolt的返回信息前，是不会删除的，那么如果消息一直失败，就会导致spout节点存储的tuple数据越来越多，导致内存溢出。

 

6.有没有想过，如果该tuple的众多子tuple中，某一个子tuple处理failed了，但是另外的子tuple仍然会继续执行，如果子tuple都是执行数据存储操作，那么就算整个消息失败，那些生成的子tuple还是会成功执行而不会回滚的。

 

7.tuple的追踪并不一定要是从spout结点到最后一个bolt,只要是spout开始，可以在任意层次bolt停止追踪做出应答。


	

```

问题：
	1，kafka+storm如何保证消息完整处理。		
		一条消息产生----Kafka--KafkaSpout-Storm--->Redis
		问题1：kafka数据生产消费如何保证消息的完整处理
			Producer-batch(缓存机制queue)--重试机制---->ack(-1,1,0)---Broker(partition leader/slave)------>KafkaConsumer(内存中，new offset)---->zk( old offset)
		Producer发送时缓存数据，需要阈值设定（数量阈值，时间阈值），当数据太多并来不及发送时，会产生老数据是否保留的问题，如何配置文件中配置的是删除掉，数据就丢失了。
		
		KafkaConsumer 消费数据时，由于offset是周期性更新，导致zk上的offset值必然小于Kafkaconsumer内存中的值，当KafkaConsumer挂掉后重启，必然会导致数据重复消费。
		如何解决重复消费：1，技术方法(不可取)，2，业务方法（标识数据：找到消息中的唯一标识 <messageTag,isProcess>）

			
		问题2：Storm中如何保证消息的完整处理  ack - fail 
		 自己定义 缓存Map<msgid,messageObj>
		 Spout---->nextTuple----Tuple---Bolt(ack(tuple))
										Bolt1-----Tuple1-1
										Bolt1-----Tuple1-2
										Bolt1-----Tuple1-3
										Bolt1-----Tuple1-4
													Bolt2(ack(Tuple1-1))
													Bolt2(ack(Tuple1-2))
													Bolt2(ack(Tuple1-3))
													Bolt2(ack(Tuple1-4))
													
		 如果成功（spout.ack(msg)）
			map.remove(msgid)
		 如果失败（spout.fail(msg)）
			messageObj = map.get(msgid)
			collector.emit(messageObj,msgid)

		问题3：如何保证一条消息路过各个组件时保证全局的完整处理
			保存每个环节的数据不丢失，自然就全局不丢失。
		
		
		问题4：在storm环节中自定义的map如何存储，在KafkaConsumer如何处理重复消费的问题？
			1，保存在当前Jvm中，既然出异常导致消息不能完全处理，存放在jvm中的标识数据，缓存Map必然会丢失
			2，保存在外部的存储中，redis。  标识数据<Set>  缓存Map<Map> 
				问题：请问存储redis时，由于网络原因或其他异常导致数据不能成功存储 怎么办？
				重试机制，保存3次，打印log日志，redis存储失败。

	2，数据量大如何保证到Kakfa中，storm如何消费，解决延迟。
		问题1：请问Kafka如何处理海量数据       -----整体数据：100G
			  数据从何而来：producer 集群----DefaultPartition  ------保存数据'平均分配'
			  
			  数据保存在哪里： broker 集群 
								topic ---partition  10   -------------->每个'分片'保存10G 
			  数据保存'如何快'：pageCache 600M/S的磁盘写入速度  sendfile技术

		问题2: 请问Storm如何处理海量数据，尽可能快
			  数据输入： KafkaSpout(consumerGroup,10) 读取外部数据源
			  数据计算： 数据计算是根据对数据处理的业务复杂度来的，越复杂并发度越大。
			             如果bolt的并发读要设置成1万个，才能提高处理速度，很显然是不行的。'需要将bolt中的代码逻辑分解出来，形成多个bolt组合'。
						 bolt1--->bolt2--->bolt3......			 
						 
	3，Flume监控文件，异常之后重新启动，如何避免重复读取。
	   异常的范围是 flumeNg的异常
	   log文件，读取文件，保存记录的行号
	   问题1：如何保存行号
			command.type = exec shell<tail -F  xxx.log>  自定义shell脚本，保存消费的行号到工作目录的某个文件里。
			当下次启动时，从行号记录文件中拿取上次读到多少行。
			 
```

