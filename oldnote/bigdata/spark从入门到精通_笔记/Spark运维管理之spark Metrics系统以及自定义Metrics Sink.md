---
title: Spark运维管理之spark Metrics系统以及自定义Metrics Sink
categories: spark  
tags: [spark]
---




Spark有一套可配置的metrics系统，是基于Coda Hale Metrics类库实现的。该metrics系统允许用户将Spark的metrics统计指标上报到多种目标源（sink）中，包括http，jmx和csv文件。这个metrics系统是通过一个配置文件进行配置的，在$SPARK_HOME目录的conf目录下，用一个metrics.properties文件来配置。可以通过在spark-defaults.conf中配置spark.metrics.conf属性来配置
自定义的文件路径。spark metrics依据不同的spark组件划分为了不同的实例。在每一个实例中，你都可以配置一系列的sink来指定该实例的metrics要上报到哪里去。


<!--more-->


以下实例是目前被支持的

master: spark standalone master进程,也就是说master进程的统计信息上报到哪
applications: master中的组件，可以上报所有application的metrics
worker: spark standalone worker进程
executor: spark executor进程
driver: spark driver进程

每个实例都可以上报metrics到0个或多个sink中。sink被包含在了org.apache.spark.metrics.sink包下。

ConsoleSink: 日志metrics，打印到控制台
CSVSink: 以固定的频率将metrics数据导出到CSV文件中
JmxSink: 注册metrics到JMX console中
MetricsServlet: 在Spark UI中添加一个servlet来通过JSON数据提供metrics数据（之前的REST API就是通过该方式进行的）
Slf4jSink: 以日志的形式发送metrics到slf4j

GraphiteSink: 发送metrics到Graphite节点
GangliaSink: 发送metrics到Ganglia节点。
Spark也支持Ganglia sink，但是没有包含在默认的打包内，因为有版权的问题。
要安装GangliaSink，就需要自己编译一个spark。要注意，必须要提供必要的授权信息。

metrics系统的意义

1、metrics只能在spark web ui上看到，或者是history server上看历史作业的web ui。
2、如果你希望将metrics数据，结构化处理以后导入到，比如mysql里面，然后进行一个存储，开发一个系统对外开放
3、spark集群运行分析系统
4、自定义metrics sink，将所有的metrics全部写入外部的你指定的存储文件中，然后定时导入到你的mysql中

实验: 自定义metrics sink

1、停止集群
```
./sbin/stop-all.sh
```

2、配置spark.metrics.conf文件，启用CSVSink
```
cd conf
cp metrics.properties.template metrics.properties

vim metrics.properties

# Enable CsvSink for all instances(启用所有的实例,包括master,applications,worker,executor,driver)
*.sink.csv.class=org.apache.spark.metrics.sink.CsvSink

# Polling period for CsvSink(推送间隔为1min)
*.sink.csv.period=1

*.sink.csv.unit=minutes

# Polling directory for CsvSink(推送的目录)
*.sink.csv.directory=/usr/local/spark-metrics

#需要创建一个目录
mkdir /usr/local/spark-metrics

```

3、重启集群
```
./sbin/start-all.sh
```

4、运行一个作业，查看指定目录下的csv文件

使用spark-shell去模拟一个作业

```
spark-shell --master spark://192.168.0.103:7077

scala>val lines = sc.textFile("hdfs://192.168.0.103:9000/test/hello.txt")
scala>val words = lines.flatMap(_.split(" ")).map((_,1))
scala>val counts = words.reduceByKey(_+_)
scala>counts.collect

```


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/metrics.png)








