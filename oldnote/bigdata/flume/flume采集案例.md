---
title: flume采集案例
categories: flume   
toc: true  
tag: [flume]

---

[TOC]




下面是flume的几种使用案例,更多的案例可以参见[flume官网](http://flume.apache.org/FlumeUserGuide.html)
<!--more-->

# 1.采集文件到HDFS

## 1.1.采集需求
比如业务系统使用log4j生成的日志，日志内容不断增加，需要把追加到日志文件中的数据实时采集到hdfs

根据需求，首先定义以下3大要素
* 采集源，即source——监控文件内容更新 :  exec  ‘tail -F file’
* 下沉目标，即sink——HDFS文件系统  :  hdfs sink
* Source和sink之间的传递通道——channel，可用file channel 也可以用 内存channel

&emsp;就是通过去执行（exec)一个命令(tail) 看文件的内容是否有更新，如果有就将更新的内容添加到hdfs中


## 1.2.配置文件
用tail命令获取数据，下沉到hdfs
vim ./conf/tail-hdfs.conf

```
# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1
 
#exec 指的是命令
# Describe/configure the source
a1.sources.r1.type = exec
#F根据文件名追中, f根据文件的nodeid追中
a1.sources.r1.command = tail -F /home/hadoop/log/test.log
a1.sources.r1.channels = c1
 
# Describe the sink
#下沉目标
a1.sinks.k1.type = hdfs
a1.sinks.k1.channel = c1
#指定目录, flum帮做目的替换
a1.sinks.k1.hdfs.path = /flume/events/%y-%m-%d/%H%M/                    #采集到hdfs中, 文件中的目录不用自己建的
#文件的命名, 前缀
a1.sinks.k1.hdfs.filePrefix = events-
 
#10 分钟就改目录
a1.sinks.k1.hdfs.round = true
a1.sinks.k1.hdfs.roundValue = 10
a1.sinks.k1.hdfs.roundUnit = minute
 
#文件滚动之前的等待时间(秒)
a1.sinks.k1.hdfs.rollInterval = 3
 
#文件滚动的大小限制(bytes)
a1.sinks.k1.hdfs.rollSize = 500
 
#写入多少个event数据后滚动文件(事件个数)
a1.sinks.k1.hdfs.rollCount = 20
 
#5个事件就往里面写入
a1.sinks.k1.hdfs.batchSize = 5
 
#用本地时间格式化目录
a1.sinks.k1.hdfs.useLocalTimeStamp = true
 
#下沉后, 生成的文件类型，默认是Sequencefile，可用DataStream，则为普通文本
a1.sinks.k1.hdfs.fileType = DataStream
 
# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100
 
# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
```


## 1.3.提供测试数据
创建目录，循环向文件添加内容
```
mkdir /home/hadoop/log
 
while true
do
echo 111111 >> /home/hadoop/log/test.log
sleep 0.5
done
 
```


## 1.4.启动命令
```
bin/flume-ng agent -c conf -f conf/tail-hdfs.conf -n a1
```


## 1.5.前端页面查看
在： master:50070, 文件目录: /flum/events/


# 2.采集目录到HDFS

## 2.1.采集需求
&emsp;采集需求：某服务器的某特定目录下，会不断产生新的文件(而不是以存在的文件中的内容的变化)，每当有新文件出现，就需要把文件采集到HDFS中去，根据需求，首先定义以下3大要素：
* 采集源，即source——监控文件目录 :  spooldir
* 下沉目标，即sink——HDFS文件系统  :  hdfs sink
* source和sink之间的传递通道——channel，可用file channel 也可以用内存channel


## 2.2.配置文件
```
#spooldir-hdfs.conf
#定义三大组件的名称
agent1.sources = source1
agent1.sinks = sink1
agent1.channels = channel1
 
# 配置source组件
#采集源使用的协议类型
agent1.sources.source1.type = spooldir                 
#监控的目录
agent1.sources.source1.spoolDir = /home/hadoop/logs/        
agent1.sources.source1.fileHeader = false
 
#配置拦截器
#agent1.sources.source1.interceptors = i1
#agent1.sources.source1.interceptors.i1.type = host
#agent1.sources.source1.interceptors.i1.hostHeader = hostname
 
# 配置sink组件
#下沉到hdfs
agent1.sinks.sink1.type = hdfs                
 #指定目录, flum帮做目的替换
agent1.sinks.sink1.hdfs.path =hdfs://hdp-node-01:9000/weblog/flume-collection/%y-%m-%d/%H-%M       
 #文件的命名, 前缀 
agent1.sinks.sink1.hdfs.filePrefix = access_log           
agent1.sinks.sink1.hdfs.maxOpenFiles = 5000
 #100个事件就往里面写入
agent1.sinks.sink1.hdfs.batchSize= 100       
  #下沉后, 生成的文件类型，默认是Sequencefile，可用DataStream，则为普通文本
agent1.sinks.sink1.hdfs.fileType = DataStream      
agent1.sinks.sink1.hdfs.writeFormat =Text

 #文件滚动的大小限制(bytes)
agent1.sinks.sink1.hdfs.rollSize = 102400           
#写入多少个event数据后滚动文件(事件个数)
agent1.sinks.sink1.hdfs.rollCount = 1000000        
#文件滚动（生成新文件）之前的等待时间(秒)
agent1.sinks.sink1.hdfs.rollInterval = 60            

#10 分钟就改目录
#agent1.sinks.sink1.hdfs.round = true
#agent1.sinks.sink1.hdfs.roundValue = 10
#agent1.sinks.sink1.hdfs.roundUnit = minute

#用本地时间格式化目录
agent1.sinks.sink1.hdfs.useLocalTimeStamp = true        


# Use a channel which buffers events in memory
#events in memory
agent1.channels.channel1.type = memory        
#event添加到通道中或者移出的允许时间
agent1.channels.channel1.keep-alive = 120               
#默认该通道中最大的可以存储的event数量 
agent1.channels.channel1.capacity = 500000            
#每次最大可以从source中拿到或者送到sink中的event数量
agent1.channels.channel1.transactionCapacity = 600         


# Bind the source and sink to the channel
agent1.sources.source1.channels = channel1
agent1.sinks.sink1.channel = channel1
```

## 2.3.启动命令
```
bin/flume-ng agent -c conf -f conf/spooldir-hdfs.conf  -n agent1
```

 注意：
1. 添加到监控目录中的文件，最后将会被改名（添加后缀：COMPLETED  ） 
2. 前端页面查看
在： master:50070, 文件目录: /weblog/flume-collection/



# 3.目录到控制台


## 3.1.配置文件

源：目录文件的变化
目标：console（控制台）

&emsp;使用spooldir协议去监听指定目录（/home/hadoop/flumespool）是否有变化，如果有就将变化打印到console中

```
#[root@hdp-node-01 flume]# cat ./conf/spooldir-hdfs.conf
#Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1
 
# Describe/configure the source
a1.sources.r1.type = spooldir
a1.sources.r1.spoolDir = /home/hadoop/flumespool
a1.sources.r1.fileHeader = true
 
# Describe the sink
a1.sinks.k1.type = logger
 
# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100
 
# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1


```

## 3.2.启动
```
bin/flume-ng agent -c ./conf -f ./conf/spooldir-hdfs.conf -n a1 -Dflume.root.logger=INFO,console
```

## 3.3测试

```
[root@hdp-node-01 hadoop]# cat test.txt
aa_test
bb_test
cc_test

[root@hdp-node-01 hadoop]# mv test.txt /home/hadoop/flumespool/

'console打印信息'
2016-11-24 19:23:33,941 (pool-3-thread-1) [INFO - org.apache.flume.client.avro.ReliableSpoolingFileEventReader.rollCurrentFile(ReliableSpoolingFileEventReader.java:348)] Preparing to move file /home/hadoop/flumespool/test.txt to /home/hadoop/flumespool/test.txt.COMPLETED                #将mv 到目录的文件改名，以后缀：.COMPLETED  结尾

#从下面可以看出，向目录中添加了一个文件，实际上是将文件中的每一行当做一个Event，下沉到console中
2016-11-24 19:23:33,941 (SinkRunner-PollingRunner-'DefaultSinkProcessor') [INFO - org.apache.flume.'sink'.LoggerSink.process(LoggerSink.java:94)] Event: { headers:{file=/home/hadoop/flumespool/test.txt} body: 61 61 5F 74 65 73 74       'aa_test '}            
2016-11-24 19:23:33,942 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:94)] Event: { headers:{file=/home/hadoop/flumespool/test.txt} body: 62 62 5F 74 65 73 74      'bb_test '}
2016-11-24 19:23:33,942 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:94)] Event: { headers:{file=/home/hadoop/flumespool/test.txt} body: 63 63 5F 74 65 73 74     ' cc_test' }

```

# Telnet到kafka

```

```


注意:

1. 如果向监听目录中添加一个文件，那么会将文件中的内容<font color=red>以行的形式</font>下沉到console中
2. 添加到监控目录中的文件，最后将会被改名（添加后缀：COMPLETED  ） 









