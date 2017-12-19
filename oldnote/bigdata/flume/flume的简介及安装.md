---
title: flume的简介及安装
categories: flume   
toc: true  
tag: [flume]
---



# 1.概述
1. Flume是一个分布式、可靠、和高可用的海量日志采集、聚合和传输的系统。
2. Flume可以采集文件，socket数据包等各种形式源数据，又可以将采集到的数据输出到HDFS、hbase、hive、kafka等众多外部存储系统中
3. 一般的采集需求，通过对flume的简单配置即可实现
4. Flume针对特殊场景也具备良好的自定义扩展能力，因此，flume可以适用于大部分的日常数据采集场景

<!--more-->

# 2.运行机制
1. Flume分布式系统中最核心的角色是agent，flume采集系统就是由一个个agent所连接起来形成
2. 通常会将一个文件中的<font color=red>一行单做一个Event事件</font>
3. 每一个agent相当于一个数据传递员(Source 到 Channel 到 Sink之间传递数据的形式是Event事件；Event事件是一个数据流单元)，内部有三个组件:
* Source：采集源，用于跟数据源对接，以获取数据
* Sink：下沉地，采集数据的传送目的，用于往下一级agent传递数据或者往最终存储系统传递数据
* Channel：angent内部的数据传输通道，用于从source将数据传递到sink

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/flume/1.png)



 
# 3.Flume采集系统结构图
## 3.1.简单结构
 单个agent采集数据

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/flume/2.png)

## 3.2.复杂结构
多级agent之间串联
 
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/flume/3.png)


# 4.Flume的安装部署

## 4.1.解压

```
cd /home/hadoop/app/
tar -zxvf apache-flume-1.6.0-bin.tar.gz 
ln -s apache-flume-1.6.0-bin flume 

#修改conf下的flume-env.sh，在里面配置JAVA_HOME
# vim flume-env.sh
export JAVA_HOME=/home/hadoop/app/jdk1.7.0_80
```
 
## 4.2.测试案例
从网络端口接收数据，下沉到logger
先在flume的conf目录下新建一个文件: 
vi netcat-logger.conf

```
# 定义这个agent中各组件的名字
a1.sources = r1                    
a1.sinks = k1
a1.channels = c1
 
# 描述和配置source组件：r1（上面指定的逻辑名称）
a1.sources.r1.type = netcat            #协议
a1.sources.r1.bind = localhost        #绑定的地址和端口
a1.sources.r1.port = 44444
 
# 描述和配置sink组件：k1（上面指定的逻辑名称）
a1.sinks.k1.type = logger        #下沉到logger中
 
# 描述和配置channel组件，此处使用是内存缓存的方式，下沉的时候是一批一批的, 下沉的时候是一个个eventChannel
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000                        #默认该通道中最大的可以存储的event数量
a1.channels.c1.transactionCapacity = 100        #每次最大可以从source中拿到或者送到sink中的event数量
 
# 描述和配置source  channel   sink之间的连接关系
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
#注意：上面的a1.sources.r1.channels和a1.sinks.k1.channel 一个没有复数的情况，一个有，这是因为对于Channel来说，源可以有多个，但是对于sink来说，Channel只能有一个，因为一个sink从Channel中取走了数据之后，Channel之中就没有了该数据

```

 2.启动agent去采集数据
 
```
bin/flume-ng agent --conf conf --conf-file conf/netcat-logger.conf --name a1 -Dflume.root.logger=INFO,console   #或者下面的方式
bin/flume-ng agent -c conf -f conf/netcat-logger.conf -n a1  -Dflume.root.logger=INFO,console

/*
-c conf   指定flume自身的配置文件所在目录（这个目录应该包含： flume-env.sh  log4j properties file）
-f conf/netcat-logger.con  指定我们所描述的采集方案
-n a1  指定我们这个agent的名字
-Dflume 是给log4j传递的参数，即log4j日志级别
-Dflume.root.logger=INFO,console  指定DEBUF模式在console输出INFO信息

*/

```

 打印结果
 
```

2016-11-24 20:48:30,934 (conf-file-poller-0) [INFO - org.apache.flume.node.Application.startAllComponents(Application.java:145)] Starting Channel c1
2016-11-24 20:48:31,017 (lifecycleSupervisor-1-0) [INFO - org.apache.flume.instrumentation.MonitoredCounterGroup.register(MonitoredCounterGroup.java:120)] Monitored counter group for type: CHANNEL, name: c1: Successfully registered new MBean.
2016-11-24 20:48:31,017 (lifecycleSupervisor-1-0) [INFO - org.apache.flume.instrumentation.MonitoredCounterGroup.start(MonitoredCounterGroup.java:96)] Component type: CHANNEL, name: c1 started
2016-11-24 20:48:31,021 (conf-file-poller-0) [INFO - org.apache.flume.node.Application.startAllComponents(Application.java:173)] Starting Sink k1
2016-11-24 20:48:31,022 (conf-file-poller-0) [INFO - org.apache.flume.node.Application.startAllComponents(Application.java:184)] Starting Source r1
2016-11-24 20:48:31,025 (lifecycleSupervisor-1-4) [INFO - org.apache.flume.source.NetcatSource.start(NetcatSource.java:150)] Source starting
2016-11-24 20:48:31,113 (lifecycleSupervisor-1-4) [INFO - org.apache.flume.source.NetcatSource.start(NetcatSource.java:164)] Created serverSocket:sun.nio.ch.ServerSocketChannelImpl[/'127.0.0.1:44444']
```


3.测试
 先要往agent采集监听的端口上发送数据，让agent有数据可采，随便在一个能跟agent节点联网的机器上

```
# telnet anget-hostname  port   （telnet localhost 44444） 

$ telnet localhost 44444
Trying 127.0.0.1...
Connected to localhost.localdomain (127.0.0.1).
Escape character is '^]'.
Hello world! <ENTER>
OK

```

