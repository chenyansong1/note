---
title: spark streaming整合kafka的两种方式
categories: spark  
tags: [spark]
---


# 方式一
有receiver,会记录log日志

 启动

```

#首先启动zk

#启动kafka
bin/kafka-server-start.sh config/server.properties

#创建topic
bin/kafka-topics.sh --create --zookeeper 192.168.80.10:2181 --replication-factor 1 --partitions 1 --topic wordcount

#查看主题
bin/kafka-topics.sh --list --zookeeper 192.168.80.10:2181


#启动spark-streaming应用程序:KafkaWordCount  如下
bin/spark-submit --class cn.itcast.spark.streaming.KafkaWordCount /root/streaming-1.0.jar 192.168.80.10:2181 group1 wordcount 1
 
 
bin/spark-submit --class cn.itcast.spark.UrlCount --master spark://node1.itcast.cn:7077 --executor-memory 1G --total-executor-cores 2 /root/SparkDemo-1.0.jar node1.itcast.cn:2181,node2.itcast.cn:2181,node3.itcast.cn:2181 group1 weblog 2


```

 KafkaWordCount 代码
 
```
package cn.itcast.spark.day5
 
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{HashPartitioner, SparkConf}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
 
object KafkaWordCount {
 
  val updateFunc = (iter: Iterator[(String, Seq[Int], Option[Int])]) => {
    //iter.flatMap(it=>Some(it._2.sum + it._3.getOrElse(0)).map(x=>(it._1,x)))
    iter.flatMap { case (x, y, z) => Some(y.sum + z.getOrElse(0)).map(i => (x, i)) }
  }
 
  def main(args: Array[String]) {
    LoggerLevels.setStreamingLogLevels()
    val Array(zkQuorum, group, topics, numThreads) = args
    val sparkConf = new SparkConf().setAppName("KafkaWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    ssc.checkpoint("c://ck2")
    //"alog-2016-04-16,alog-2016-04-17,alog-2016-04-18"
    //"Array((alog-2016-04-16, 2), (alog-2016-04-17, 2), (alog-2016-04-18, 2))"
    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val data = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap, StorageLevel.MEMORY_AND_DISK_SER)

    //kafka发送下来的是(key,value),我们只是需要的是value
    val words = data.map(_._2).flatMap(_.split(" "))
    val wordCounts = words.map((_, 1)).updateStateByKey(updateFunc, new HashPartitioner(ssc.sparkContext.defaultParallelism), true)
    ssc.start()
    ssc.awaitTermination()
  }
}

```


 启动一个生产者发送消息
 
```
 
#启动一个生产者发送消息
bin/kafka-console-producer.sh --broker-list 192.168.80.10:9092 --topic wordcount

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/spark_streaming/4.jpg)
 
# 方式二
直连方式,一个kafka的partition对应DStream里Rdd的一个分区,要自己来管理偏移量

 方式二的DirectKafkaWordCount 


```
package cn.itcast.spark.day5
 
import kafka.serializer.StringDecoder
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka.{KafkaManager, KafkaUtils}
import org.apache.spark.streaming.{Seconds, StreamingContext}
 
 
object DirectKafkaWordCount {
 
  /*  def dealLine(line: String): String = {
      val list = line.split(',').toList
  //    val list = AnalysisUtil.dealString(line, ',', '"')// 把dealString函数当做split即可
      list.get(0).substring(0, 10) + "-" + list.get(26)
    }*/
 
  def processRdd(rdd: RDD[(String, String)]): Unit = {
    val lines = rdd.map(_._2)
    val words = lines.map(_.split(" "))
    val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
    wordCounts.foreach(println)
  }
 
  def main(args: Array[String]) {
    if (args.length < 3) {
      System.err.println(
        s"""
           |Usage: DirectKafkaWordCount <brokers> <topics> <groupid>
           |  <brokers> is a list of one or more Kafka brokers
           |  <topics> is a list of one or more kafka topics to consume from
           |  <groupid> is a consume group
           |
        """.stripMargin)
      System.exit(1)
    }
 
    Logger.getLogger("org").setLevel(Level.WARN)
 
    val Array(brokers, topics, groupId) = args
 
    // Create context with 2 second batch interval
    val sparkConf = new SparkConf().setAppName("DirectKafkaWordCount")
    sparkConf.setMaster("local[*]")
    sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "5")
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
 
    val ssc = new StreamingContext(sparkConf, Seconds(2))
 
    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> brokers,//直接连broker
      "group.id" -> groupId,
      "auto.offset.reset" -> "smallest"
    )
 
    val km = new KafkaManager(kafkaParams)
 
    val messages = km.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)
 
    messages.foreachRDD(rdd => {
      if (!rdd.isEmpty()) {
        // 先处理消息
        processRdd(rdd)
        // 再更新offsets
        km.updateZKOffsets(rdd)
      }
    })
 
    ssc.start()
    ssc.awaitTermination()
  }
}


```

 org.apache.spark.streaming.kafka.KafkaManager 类
 
```
package org.apache.spark.streaming.kafka
 
import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.serializer.Decoder
import org.apache.spark.SparkException
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.KafkaCluster.LeaderOffset
 
import scala.reflect.ClassTag
 
/**
  * 自己管理offset
  */
class KafkaManager(val kafkaParams: Map[String, String]) extends Serializable {
  //因为new KafkaCluster 是 private[spark] 所以取包名为:org.apache.spark.streaming.kafka
  private val kc = new KafkaCluster(kafkaParams)
 
  /**
    * 创建数据流
    */
  def createDirectStream[K: ClassTag, V: ClassTag, KD <: Decoder[K]: ClassTag, VD <: Decoder[V]: ClassTag](
                                                                                                            ssc: StreamingContext, kafkaParams: Map[String, String], topics: Set[String]): InputDStream[(K, V)] =  {
    val groupId = kafkaParams.get("group.id").get
    // 在zookeeper上读取offsets前先根据实际情况更新offsets
    setOrUpdateOffsets(topics, groupId)
 
    //从zookeeper上读取offset开始消费message
    val messages = {
      val partitionsE = kc.getPartitions(topics)
      if (partitionsE.isLeft)
        throw new SparkException(s"get kafka partition failed: ${partitionsE.left.get}")
      val partitions = partitionsE.right.get
      val consumerOffsetsE = kc.getConsumerOffsets(groupId, partitions)
      if (consumerOffsetsE.isLeft)
        throw new SparkException(s"get kafka consumer offsets failed: ${consumerOffsetsE.left.get}")
      val consumerOffsets = consumerOffsetsE.right.get
      KafkaUtils.createDirectStream[K, V, KD, VD, (K, V)](
        ssc, kafkaParams, consumerOffsets, (mmd: MessageAndMetadata[K, V]) => (mmd.key, mmd.message))
    }
    messages
  }
 
  /**
    * 创建数据流前，根据实际消费情况更新消费offsets
    * @param topics
    * @param groupId
    */
  private def setOrUpdateOffsets(topics: Set[String], groupId: String): Unit = {
    topics.foreach(topic => {
      var hasConsumed = true
      val partitionsE = kc.getPartitions(Set(topic))
      if (partitionsE.isLeft)
        throw new SparkException(s"get kafka partition failed: ${partitionsE.left.get}")
      val partitions = partitionsE.right.get
      val consumerOffsetsE = kc.getConsumerOffsets(groupId, partitions)
      if (consumerOffsetsE.isLeft) hasConsumed = false
      if (hasConsumed) {// 消费过
        /**
          * 如果streaming程序执行的时候出现kafka.common.OffsetOutOfRangeException，
          * 说明zk上保存的offsets已经过时了，即kafka的定时清理策略已经将包含该offsets的文件删除。
          * 针对这种情况，只要判断一下zk上的consumerOffsets和earliestLeaderOffsets的大小，
          * 如果consumerOffsets比earliestLeaderOffsets还小的话，说明consumerOffsets已过时,
          * 这时把consumerOffsets更新为earliestLeaderOffsets
          */
        val earliestLeaderOffsetsE = kc.getEarliestLeaderOffsets(partitions)
        if (earliestLeaderOffsetsE.isLeft)
          throw new SparkException(s"get earliest leader offsets failed: ${earliestLeaderOffsetsE.left.get}")
        val earliestLeaderOffsets = earliestLeaderOffsetsE.right.get
        val consumerOffsets = consumerOffsetsE.right.get
 
        // 可能只是存在部分分区consumerOffsets过时，所以只更新过时分区的consumerOffsets为earliestLeaderOffsets
        var offsets: Map[TopicAndPartition, Long] = Map()
        consumerOffsets.foreach({ case(tp, n) =>
          val earliestLeaderOffset = earliestLeaderOffsets(tp).offset
          if (n < earliestLeaderOffset) {
            println("consumer group:" + groupId + ",topic:" + tp.topic + ",partition:" + tp.partition +
              " offsets已经过时，更新为" + earliestLeaderOffset)
            offsets += (tp -> earliestLeaderOffset)
          }
        })
        if (!offsets.isEmpty) {
          kc.setConsumerOffsets(groupId, offsets)//针对某一个topic的组来设置offset
        }
      } else {// 没有消费过
      val reset = kafkaParams.get("auto.offset.reset").map(_.toLowerCase)
        var leaderOffsets: Map[TopicAndPartition, LeaderOffset] = null
        if (reset == Some("smallest")) {
          val leaderOffsetsE = kc.getEarliestLeaderOffsets(partitions)
          if (leaderOffsetsE.isLeft)
            throw new SparkException(s"get earliest leader offsets failed: ${leaderOffsetsE.left.get}")
          leaderOffsets = leaderOffsetsE.right.get
        } else {
          val leaderOffsetsE = kc.getLatestLeaderOffsets(partitions)
          if (leaderOffsetsE.isLeft)
            throw new SparkException(s"get latest leader offsets failed: ${leaderOffsetsE.left.get}")
          leaderOffsets = leaderOffsetsE.right.get
        }
        val offsets = leaderOffsets.map {
          case (tp, offset) => (tp, offset.offset)
        }
        kc.setConsumerOffsets(groupId, offsets)
      }
    })
  }
 
  /**
    * 更新zookeeper上的消费offsets
    * @param rdd
    */
  def updateZKOffsets(rdd: RDD[(String, String)]) : Unit = {
    val groupId = kafkaParams.get("group.id").get
    val offsetsList = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
 
    for (offsets <- offsetsList) {
      val topicAndPartition = TopicAndPartition(offsets.topic, offsets.partition)
      val o = kc.setConsumerOffsets(groupId, Map((topicAndPartition, offsets.untilOffset)))
      if (o.isLeft) {
        println(s"Error updating the offset to Kafka cluster: ${o.left.get}")
      }
    }
  }
}
 
```


参考: http://blog.csdn.net/ligt0610/article/details/47311771








