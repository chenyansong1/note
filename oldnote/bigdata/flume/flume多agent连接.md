---
title: flume多agent连接
categories: flume   
toc: true  
tag: [flume]
---



# 1.需求
下面是多个agent的使用示例,从tail到avro ,再从avro到控制台
<!--more-->



# 2.tail 到avro
vim ./conf/tail-avro.conf
```
# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1
 
# Describe/configure the source
a1.sources.r1.type = exec
a1.sources.r1.command = tail -F /home/hadoop/log/test.log
a1.sources.r1.channels = c1
 
# Describe the sink
#绑定的不是本机, 是另外一台机器的服务地址, sink端的avro是一个发送端, avro的客户端, 往hadoop01这个机器上发
a1.sinks = k1
a1.sinks.k1.type = avro
a1.sinks.k1.hostname =hadoop01
a1.sinks.k1.port = 4141
a1.sinks.k1.batch-size = 2
 
 
 
# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100
 
# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1

```

启动命令
```
bin/flume-ng agent -c conf -f conf/tail-avro.conf   -n al

```


# 3.avro到logger(console)
vim ./conf/avro-logger.conf
```
# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1
 
# Describe/configure the source
#source中的avro组件是接收者服务, 绑定本机
a1.sources.r1.type = avro
a1.sources.r1.bind = 0.0.0.0
a1.sources.r1.port = 4141
 
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

启动命令
```
bin/flume-ng agent -c conf -f conf/avro-logger.conf -n al -Dflume.root.logger=INFO,console
 
```