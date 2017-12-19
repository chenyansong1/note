---
title: SparkStreaming之输入DStream之Kafka基础数据源
categories: spark  
tags: [spark]
---


# 基于Receiver的方式

这种方式使用Receiver来获取数据,Receiver是使用Kafka的高层次ConsumerAPI来实现的,receive从kafka中获取的数据都是存储在spark executor的内存中的,然后spark Streaming启动额job会去处理那些数据

然而,在默认的配置下,这种方式可能会因为底层的失败而丢失数据,如果要启用高可靠机制,让数据零丢失,就必须启用spark streaming 的预写日志机制(Write Ahead Log,WAL),该机制会tongue的将接收到的kafka数据写入分布式文件系统(比如HDFS)山的预写日志中,所以即使底层节点出现了失败,也可以使用预写日志中的数据进行恢复

前提:
1.maven添加依赖
```
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-streaming-kafka_2.11</artifactId>
    <version>1.6.3</version>
</dependency>
```

2.使用第三方工具类创建输入DStream
```
 KafkaUtils.createStream(StreamingContext,[ZK quorum], [consumer group id], [per-topic number of kakfa partitions to consume])


```

注意事项:
1.kafka的topic的partition,与spark中的RDD的partition是没有关系的,所以在KafkaUtils.createStream()中,提高partition的数量,只会增加一个Receiver中读取partition的线程的数量,不会增加spark处理数据的并行度
2.可以创建多个kafka输入DStream,使用不同的consumer group和topic,来通过多个receiver并行接收数据
3.如果基于容错的文件系统,比如HDFS,启用了预写日志机制,接收到的数据都会被复制一份到预写日志中,因此在KafkaUtils.createStream()中,设置的持久化级别是:StorageLevel.MEMORY_AND_DISK_SER_2



```
//创建topic
bin/kafka-topic.sh --zookeeper zk01:2181,zk02:2181,zk03:2181 --topic WordCount --replication-factor 1 --partitions 1 --create


//创建consumer生产者
bin/kafka-console-producer.sh --broker-list 192.168.1.107:9092,192.168.1.108:9092,192.168.1.109:9092, --topic WordCount

```

实例代码

```
 // local后面必须跟一个方括号,里面填写一个数字,代表了用几个线程来执行我们的spark streaming程序
 val conf = new SparkConf()
   .setAppName("Streaming")
   .setMaster("local[2]")

 // 每收集多长时间的数据就划分为一个batch进行处理,这里设置为1秒:Seconds(1)
 val ssc = new StreamingContext(conf,Seconds(1))

 // 创建针对Kafka的输入流
 val zk = "192.168.0.107:2181,192.168.0.108:2181,192.168.0.109:2181"
 val  topicThreadMap = Map(
   "WordCount"->1
 )
 
 // zk是zookeeper的节点地址
 // DefalutConsumerGroup是kafka的groupId
 // topicThreadMap是指定去消费哪个topic
 //Map of (topic_name -> numPartitions) to consume. Each partition is consumed in its own thread
 // topic名字->分区数量,每个分区将会启动一个Receiver线程去消费(而一个Receiver需要一个cpu core)
 val lines = KafkaUtils.createStream(ssc,zk,"DefalutConsumerGroup",topicThreadMap)
 
 // 这里需要注意的是lines中是Tuple(index,line)这样的数据,所以_._2就是一行的的数据
 val words = lines.flatMap(_._2.split(" "))
 val pairs = words.map((_,1))
 val wordcount = pairs.reduceByKey(_ + _)

 // 打印测试
 wordcount.print


 ssc.start()
 ssc.awaitTermination()
 ssc.stop()

```


# 基于Direct的方式

这种是不基于Receiver的直接方式,是在spark1.3中引入,从而能够确保更加健壮的机制,替代掉使用Receiver来接收数据后,这种方式会周期性(就是我们指定的batch的时间)的查询Kafka,来获取每个topic+partition的最新的offset,从而定义每个batch的offset的范围(而每个batch会形成一个Rdd),当处理数据的job启动时,就会使用kafka的简单consumer API来获取kafka指定offset范围的数据,这就得到了这个Rdd的数据

这种方式有如下的优点:
1.简化并行读取,如果要有多个partition,不需要创建多个输入DStream然后对他们进行union操作,spark会创建跟kafka partition一样多的Rdd partition,并且会并行从kafka中读取数据,所在kafka partition和RDD partition之间,有一个一对一的映射关系
2.高性能:如果要保证零数据丢失,在语句Receiver的方式中,需要开启WAL机制,这种方式其实效率低下,因为数据实际上被复制了两份,kafka自己本身就有高可靠的机制,会对数据复制一份,而这里又会复制一份到WAL中,而基于direct的方式,不依赖Receiver,不需要开启WAL机制,只要kafka中作了数的复制,那么就可以通过kafka的副本进行恢复
3.一次仅且一次的事务机制
基于Receiver的方式,是使用kafka的高阶API来在zookeeper中保存消费过的offset,这是消费kafka数据的传统的方式,这种方式配合着WAL机制可以保证数据零丢失的高可靠性,但是却无法保证数据被处理一次且仅一次,可能会处理两次,因为spark和zookeeper之间可能是不同步的

基于direct的方式,使用kafka的简单API,spark streaming自己会负责追踪消费的offset并保存在checkpoint中,spark自己一定是同步的,因此可以保证数据是消费一次且仅消费一次

createDirectStream()方法参数说明

```

   *
   * @param ssc StreamingContext object	这里是传入的一个StreamingContext
   * @param kafkaParams Kafka <a href="http://kafka.apache.org/documentation.html#configuration">
   *   configuration parameters</a>. Requires "metadata.broker.list" or "bootstrap.servers" 必须要指定:"metadata.broker.list" or "bootstrap.servers"中的一个
   *   to be set with Kafka broker(s) (NOT zookeeper servers), specified in
   *   host1:port1,host2:port2 form.	//指定的格式
   *   If not starting from a checkpoint, "auto.offset.reset" may be set to "largest" or "smallest"	//如果没有初始化的offset,那么从哪里开始消费(largest从头开始,smallest:从最近开始消费)
   *   to determine where the stream starts (defaults to "largest")
   *   如果开始消费的数据不是从checkpoint中开始的,那么使用"auto.offset.reset" 参数设置成"largest" or "smallest"来决定从Stream流的哪里开始消费数据
   * @param topics Names of the topics to consume	topic名称
   * @tparam K type of Kafka message key	消息key的类型
   * @tparam V type of Kafka message value	消息value的类型
   * @tparam KD type of Kafka message key decoder	key的编码格式
   * @tparam VD type of Kafka message value decoder	value的编码格式
   * @return DStream of (Kafka message key, Kafka message value)
   */
  def createDirectStream[
    K: ClassTag,
    V: ClassTag,
    KD <: Decoder[K]: ClassTag,
    VD <: Decoder[V]: ClassTag] (
      ssc: StreamingContext,
      kafkaParams: Map[String, String],
      topics: Set[String]
  ): InputDStream[(K, V)]


=========================================
在kafka中对auto.offset.reset参数的解释是:

What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server (e.g. because that data has been deleted):
当没有初始化的offset的时候,此时该从哪里读取数据

earliest: automatically reset the offset to the earliest offset	设置offset为最开始的处,即从头开始消费
latest: automatically reset the offset to the latest offset	从设置offset为最近的offset
none: throw exception to the consumer if no previous offset is found for the consumer's group
anything else: throw exception to the consumer.



```

实例代码
```
 val conf = new SparkConf()
   .setAppName("Streaming")
   .setMaster("local[2]")

 // 每收集多长时间的数据就划分为一个batch进行处理,这里设置为1秒:Seconds(1)
 val ssc = new StreamingContext(conf,Seconds(1))

 // 创建针对Kafka的输入流
 val zk = "192.168.0.107:2181,192.168.0.108:2181,192.168.0.109:2181"
 val  kafkaParams = Map(
   // kafka的broker-list
   "meta.broker.list"->"192.168.1.107:9092,192.168.1.108:9092,192.168.1.109:9092",
 )

 val topics = Set(
   "WordCount"
 )

 val lines = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc,kafkaParams,topics)
 val words = lines.flatMap(_._2.split(" "))
 val pairs = words.map((_,1))
 val wordcount = pairs.reduceByKey(_ + _)

 // 打印测试
 wordcount.print


 ssc.start()
 ssc.awaitTermination()
 ssc.stop()

```









