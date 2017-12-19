---
title: SparkStreaming之flume
categories: spark  
tags: [spark]
---



flume的安装参见详细的文档


<!--more-->

# 接收flume实时数据流之push方式


Flume被设计为可以在agent之间推送数据(数据从一个agent到另外一个agent)，而不一定是从agent将数据传输到sink中。在这种方式下，Spark Streaming需要启动一个作为Avro Agent的Receiver，来让flume可以推送数据过来。下面是我们的整合步骤：

## 前提需要

选择一台机器：
1、Spark Streaming与Flume都可以在这台机器上启动，Spark的其中一个Worker必须运行在这台机器上面
2、Flume可以将数据推送到这台机器上的某个端口

由于flume的push模型，Spark Streaming必须先启动起来，Receiver要被调度起来并且监听本地某个端口，来让flume推送数据。

## 配置flume
接收来自目录的变化,然后将变化的文件push到spark streaming中,在flume-conf.properties文件中，配置flume的sink是将数据推送到其他的agent中
```
agent1.sinks.sink1.type = avro
agent1.sinks.sink1.channel = channel1
agent1.sinks.sink1.hostname = 192.168.0.103
agent1.sinks.sink1.port = 8888

```

整个配置文件如下:

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/flume.png)




## 配置spark streaming

在我们的spark工程的pom.xml中加入spark streaming整合flume的依赖
```
groupId = org.apache.spark
artifactId = spark-streaming-flume_2.10
version = 1.5.0
```

在代码中使用整合flume的方式创建输入DStream
```
import org.apache.spark.streaming.flume.*;

JavaReceiverInputDStream<SparkFlumeEvent> flumeStream =
	FlumeUtils.createStream(streamingContext, [chosen machine's hostname], [chosen port]);
	
```

这里有一点需要注意的是，这里监听的hostname，必须与cluster manager（比如Standalone Master、YARN ResourceManager）是同一台机器，这样cluster manager
才能匹配到正确的机器，并将receiver调度在正确的机器上运行。

## 部署spark streaming应用
```
package cn.spark.study.streaming;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.flume.FlumeUtils;
import org.apache.spark.streaming.flume.SparkFlumeEvent;

import scala.Tuple2;

/**
 * 基于Flume Push方式的实时wordcount程序
 * @author Administrator
 *
 */
public class FlumePushWordCount {

	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setMaster("local[2]")
				.setAppName("FlumePushWordCount");  
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
		
		JavaReceiverInputDStream<SparkFlumeEvent> lines =
				FlumeUtils.createStream(jssc, "192.168.0.103", 8888);  
		
		JavaDStream<String> words = lines.flatMap(
				
				new FlatMapFunction<SparkFlumeEvent, String>() {

					private static final long serialVersionUID = 1L;

					@Override
					public Iterable<String> call(SparkFlumeEvent event) throws Exception {
						String line = new String(event.event().getBody().array());  
						return Arrays.asList(line.split(" "));   
					}
					
				});
		
		JavaPairDStream<String, Integer> pairs = words.mapToPair(
				
				new PairFunction<String, String, Integer>() {

					private static final long serialVersionUID = 1L;

					@Override
					public Tuple2<String, Integer> call(String word) throws Exception {
						return new Tuple2<String, Integer>(word, 1);
					}
					
				});
		
		JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
				
				new Function2<Integer, Integer, Integer>() {

					private static final long serialVersionUID = 1L;

					@Override
					public Integer call(Integer v1, Integer v2) throws Exception {
						return v1 + v2;
					}
					
				});
		
		wordCounts.print();
		
		jssc.start();
		jssc.awaitTermination();
		jssc.close();
	}
	
}


```
打包工程为一个jar包，使用spark-submit来提交作业



## 启动flume agent

flume-ng agent -n agent1 -c conf -f /usr/local/flume/conf/flume-conf.properties -Dflume.root.logger=DEBUG,console

## flume的使用场景

什么时候我们应该用Spark Streaming整合Kafka去用，做实时计算？
什么使用应该整合flume？

看你的实时数据流的产出频率
1、如果你的实时数据流产出特别频繁，比如说一秒钟10w条，那就必须是kafka，分布式的消息缓存中间件，可以承受超高并发
2、如果你的实时数据流产出频率不固定，比如有的时候是1秒10w，有的时候是1个小时才10w，可以选择将数据用nginx日志来表示，每隔一段时间将日志文件
放到flume监控的目录中，然后呢，spark streaming来计算


# 接收flume实时数据流之poll方式

除了让flume将数据推送到spark streaming，还有一种方式，可以运行一个自定义的flume sink
1、Flume推送数据到sink中，然后数据缓存在sink中
2、spark streaming用一个可靠的flume receiver以及事务机制从sink中拉取数据

## 前提条件
1、选择一台可以在flume agent中运行自定义sink的机器
2、将flume的数据管道流配置为将数据传送到那个sink中
3、spark streaming所在的机器可以从那个sink中拉取数据

## 配置flume

1、加入sink jars，将以下jar加入flume的classpath中
```
groupId = org.apache.spark
artifactId = spark-streaming-flume-sink_2.10
version = 1.5.1

groupId = org.scala-lang
artifactId = scala-library
version = 2.10.4

groupId = org.apache.commons
artifactId = commons-lang3
version = 3.3.2
```
2、修改配置文件
```
agent1.sinks.sink1.type = org.apache.spark.streaming.flume.sink.SparkSink
agent1.sinks.sink1.hostname = 192.168.0.103
agent1.sinks.sink1.port = 8888
agent1.sinks.sink1.channel = channel1
```


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/flume2.png)





## 配置spark streaming
```
import org.apache.spark.streaming.flume.*;

JavaReceiverInputDStream<SparkFlumeEvent>flumeStream =
	FlumeUtils.createPollingStream(streamingContext, [sink machine hostname], [sink port]);
```


代码
```
package cn.spark.study.streaming;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.flume.FlumeUtils;
import org.apache.spark.streaming.flume.SparkFlumeEvent;

import scala.Tuple2;

/**
 * 基于Flume Poll方式的实时wordcount程序
 * @author Administrator
 *
 */
public class FlumePollWordCount {

	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setMaster("local[2]")
				.setAppName("FlumePollWordCount");  
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
		
		JavaReceiverInputDStream<SparkFlumeEvent> lines =
				FlumeUtils.createPollingStream(jssc, "192.168.0.103", 8888);  
		
		JavaDStream<String> words = lines.flatMap(
				
				new FlatMapFunction<SparkFlumeEvent, String>() {

					private static final long serialVersionUID = 1L;

					@Override
					public Iterable<String> call(SparkFlumeEvent event) throws Exception {
						String line = new String(event.event().getBody().array());  
						return Arrays.asList(line.split(" "));   
					}
					
				});
		
		JavaPairDStream<String, Integer> pairs = words.mapToPair(
				
				new PairFunction<String, String, Integer>() {

					private static final long serialVersionUID = 1L;

					@Override
					public Tuple2<String, Integer> call(String word) throws Exception {
						return new Tuple2<String, Integer>(word, 1);
					}
					
				});
		
		JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
				
				new Function2<Integer, Integer, Integer>() {

					private static final long serialVersionUID = 1L;

					@Override
					public Integer call(Integer v1, Integer v2) throws Exception {
						return v1 + v2;
					}
					
				});
		
		wordCounts.print();
		
		jssc.start();
		jssc.awaitTermination();
		jssc.close();
	}
	
}


```

一定要先启动flume，再去提交spark streaming

## 启动flume
```
flume-ng agent -n agent1 -c conf -f /usr/local/flume/conf/flume-conf.properties -Dflume.root.logger=DEBUG,console
```






