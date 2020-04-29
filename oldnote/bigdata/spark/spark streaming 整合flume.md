---
title: spark streaming 整合flume
categories: spark  
tags: [spark]
---


# 官方文档

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/spark_streaming/3.png)

 

# 1.方式一:flume直接和spark streaming 结合

如果数据量不是很大,那么直接将数据通过flume采集到spark streaming中

## 1.1.flume向spark streaming 中push

 spark streaming 代码
 
```
package cn.itcast.spark.day5
 
import org.apache.spark.SparkConf
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
 
object FlumePushWordCount {
 
  def main(args: Array[String]) {
    val host = args(0)
    val port = args(1).toInt
    LoggerLevels.setStreamingLogLevels()
    val conf = new SparkConf().setAppName("FlumeWordCount")//.setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(5))
    //推送方式: flume向spark发送数据:这里只能够指定一个主机和端口,那么在集群环境下,这种方式肯定是不可取的
    val flumeStream = FlumeUtils.createStream(ssc, host, port)//指定flume向spark streaming发送数据的ip和port
    //flume中的数据通过event.getBody()才能拿到真正的内容
    val words = flumeStream.flatMap(x => new String(x.event.getBody().array()).split(" ")).map((_, 1))
 
    val results = words.reduceByKey(_ + _)
    results.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

```


 flume配置文件 flume-push.conf
 
```
# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1
 
# source
a1.sources.r1.type = spooldir
a1.sources.r1.spoolDir = /export/data/flume
a1.sources.r1.fileHeader = true
 
# Describe the sink
a1.sinks.k1.type = avro
#这是接收方
a1.sinks.k1.hostname = 192.168.31.172
a1.sinks.k1.port = 8888
 
# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100
 
# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1

```

 启动
 
```
#首先启动spark-streaming应用程序
bin/spark-submit --class cn.itcast.spark.streaming.FlumeWordCount /root/streaming-1.0.jar
#再启动flmue
bin/flume-ng agent -n a1 -c conf/ -f conf/flume-push.conf -Dflume.root.logger=WARN,console
```


## 1.2.spark streaming 从flume中拉取数据(poll)

 spark streaming 代码
 
```
package cn.itcast.spark.day5
 
import java.net.InetSocketAddress
 
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
 
object FlumePollWordCount {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("FlumePollWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(5))
    //从多个flume中拉取数据(flume的地址)
    val address = Seq(new InetSocketAddress("172.16.0.11", 8888))
    val flumeStream = FlumeUtils.createPollingStream(ssc, address, StorageLevel.MEMORY_AND_DISK)
    //flume中的数据通过event.getBody()才能拿到真正的内容
    val words = flumeStream.flatMap(x => new String(x.event.getBody().array()).split(" ")).map((_,1))
    val results = words.reduceByKey(_+_)
    results.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

```

 flume配置文件 flume-poll.conf
 
```
# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1
 
# source
a1.sources.r1.type = spooldir
a1.sources.r1.spoolDir = /export/data/flume
a1.sources.r1.fileHeader = true
 
# Describe the sink
a1.sinks.k1.type = org.apache.spark.streaming.flume.sink.SparkSink
a1.sinks.k1.hostname = master
a1.sinks.k1.port = 8888
 
# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100
 
# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1

```

 启动
 
```
#首先将下载好的spark-streaming-flume-sink_2.10-1.6.1.jar和scala-library-2.10.5.jar还有commons-lang3-3.3.2.jar三个包放入到flume的lib目录下
#启动flume
bin/flume-ng agent -n a1 -c conf/ -f conf/flume-poll.conf -Dflume.root.logger=WARN,console
#再启动spark-streaming应用程序
bin/spark-submit --class cn.itcast.spark.streaming.FlumePollWordCount /root/streaming-1.0.jar

```




