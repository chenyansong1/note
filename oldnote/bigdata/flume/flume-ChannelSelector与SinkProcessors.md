[TOC]

# Channel Selector与Sink Processors

**前言:** 继上一篇从source到channel到sink实现了一整套的流程之后，我们这次学习一下Channel Selector与Sink Processors。我个人为了方便理解把这两个组件想象成在Source和Channel之间以及Channel和Sink之间。(注：这两个组件不是必须要设置的。)

### 一、Channel Selector

​    flume channel selectors允许给一个source可以配置多个channel的能力。这种模式有两种方式，一种是用来复制（Replication），这也是默认配置，另一种是用来分流（Multiplexing）。

​    **1) Replication方式**：可以将数据源复制多份，分别传递到多个channel中，每个channel接收到的数据都是相同的。如下图所示：

![Flume学习系列(三)---- Channel Selector与Sink Processors](https://www.javazhiyin.com/wp-content/uploads/2018/11/java2-1543575673.jpeg)





这种方式的配置主要有两个key：



![Flume学习系列(三)---- Channel Selector与Sink Processors](https://www.javazhiyin.com/wp-content/uploads/2018/11/java7-1543575673.jpeg)



```
a1.sources = r1 
a1.channels = c1 c2 c3 a1.sources.r1.selector.type = replicating a1.sources.r1.channels = c1 c2 c3 #这意味着c3是可选的，向c3写入失败会被忽略。但是向c1，c2写入失败会出错 a1.sources.r1.selector.optional = c3
```

​    **2）Multiplexing方式**：selector可以根据header的值来确定数据传递到哪一个channel，其中header的值可以通过interceptor去设置。如果现在不明白interceptor没关系，就把它当作能在header中添加一个key-value对的玩意就可以。

![Flume学习系列(三)---- Channel Selector与Sink Processors](https://www.javazhiyin.com/wp-content/uploads/2018/11/java5-1543575673.jpeg)





![Flume学习系列(三)---- Channel Selector与Sink Processors](https://www.javazhiyin.com/wp-content/uploads/2018/11/java8-1543575673.jpeg)



​    假设我们通过拦截器向header中添加了key为state的一个属性，他的值根据具体需求可以为CZ和US等。那我们想把值为CZ的数据流通过c1处理，把值为US的数据流通过c2,c3处理，其他情况用c4处理。则flume.conf 配置如下：

```
a1.sources = r1 a1.channels = c1 c2 c3 c4 #设置selector类型 a1.sources.r1.selector.type = multiplexing #设置根据header中的什么key去分流 a1.sources.r1.selector.header = state #设置根据key的具体值选择哪个channel a1.sources.r1.selector.mapping.CZ = c1 a1.sources.r1.selector.mapping.US = c2 c3 #设置默认channel a1.sources.r1.selector.default = c4
```

### 二、Sink Processors

​    Sink Processors，顾名思义，就是沉槽处理器，也就是数据向哪里流，怎么流由处理器控制。以sinkgroup的形式出现。简单的说就是一个source 对应一个Sinkgroups，即多个sink, 其实与selector情况差不多，只是processor考虑更多的是可靠性和性能，即故障转移与负载均衡的设置。
​    SinkGroup允许组织多个sink到一个实体上。SinkProcessors 能够提供在组内所有sink之间实现负载均衡的能力（配置load_balance）。而且在失败的情况下能够进行故障转移，从一个Sink到另一个Sink（配置failover ）。

![Flume学习系列(三)---- Channel Selector与Sink Processors](https://www.javazhiyin.com/wp-content/uploads/2018/11/java7-1543575673-1.jpeg)



```
#设置组名 a1.sinkgroups = g1 #设置组内的sink a1.sinkgroups.g1.sinks = k1 k2 #设置processor的类别，这里是负载均衡 a1.sinkgroups.g1.processor.type = load_balance
```

#### 2.1 负载均衡（load_balance）

​    过程：source里的event流经channel，进入sink组，在sink组内部根据负载算法（我们在配置文件中配的round_robin、random）选择sink，后续可以选择不同机器上的agent实现负载均衡。
借图：https://blog.csdn.net/silentwolfyh/article/details/51165804

![Flume学习系列(三)---- Channel Selector与Sink Processors](https://www.javazhiyin.com/wp-content/uploads/2018/11/java1-1543575674.jpeg)


配置如下：



```
# Name the components on this agent a1.sources = r1 a1.sinks = k1 k2 a1.channels = c1  # Describe/configure the source #exec源从执行Unix命令的标准输出获取数据 a1.sources.r1.type = exec a1.sources.r1.channels=c1 #要执行的Unix命令是tail，tail命令默认在屏幕上显示指定文件的末尾10行 a1.sources.r1.command=tail -F /home/flume/xx.log   #define sinkgroups #这里定义的就是Processor a1.sinkgroups=g1 a1.sinkgroups.g1.sinks=k1 k2 #类型为负载均衡 a1.sinkgroups.g1.processor.type=load_balance #是否指数增长超时恢复时间 a1.sinkgroups.g1.processor.backoff=true#选择下一个sink的算法 a1.sinkgroups.g1.processor.selector=round_robin  #define the sink 1a1.sinks.k1.type=avro a1.sinks.k1.hostname=192.168.1.112 a1.sinks.k1.port=9876  #define the sink 2a1.sinks.k2.type=avro a1.sinks.k2.hostname=192.168.1.113 a1.sinks.k2.port=9876# Use a channel which buffers events in memory a1.channels.c1.type = memory a1.channels.c1.capacity = 1000a1.channels.c1.transactionCapacity = 100# Bind the source and sink to the channel a1.sources.r1.channels = c1 a1.sinks.k1.channel = c1 a1.sinks.k2.channel=c1
```

#### 2.2 故障转移（Failover）

​    配置一组sink，这组sink组成一个Failover Sink Processor，当有一个sink处理失败，Flume将这个sink放到一个地方，等待冷却时间，可以正常处理event时再拿回来。

​    event通过一个channel流向一个sink组，在sink组内部根据优先级选择具体的sink，一个失败后再转向另一个sink，流程图如下：



![Flume学习系列(三)---- Channel Selector与Sink Processors](https://www.javazhiyin.com/wp-content/uploads/2018/11/java6-1543575674.jpeg)



相应配置如下：

```
# Name the components on this agent a1.sources = r1 a1.sinks = k1 k2 a1.channels = c1  # Describe/configure the source a1.sources.r1.type = exec a1.sources.r1.channels=c1 a1.sources.r1.command=tail -F /home/flume/xx.log  #define sinkgroups a1.sinkgroups=g1 a1.sinkgroups.g1.sinks=k1 k2 a1.sinkgroups.g1.processor.type=failover #在Sink中的两个数据为优先级设置默认为5，数字越大越优先 a1.sinkgroups.g1.processor.priority.k1=10a1.sinkgroups.g1.processor.priority.k2=5a1.sinkgroups.g1.processor.maxpenalty=10000#define the sink 1a1.sinks.k1.type=avro a1.sinks.k1.hostname=192.168.1.112 a1.sinks.k1.port=9876#define the sink 2a1.sinks.k2.type=avro a1.sinks.k2.hostname=192.168.1.113 a1.sinks.k2.port=9876# Use a channel which buffers events in memory a1.channels.c1.type = memory a1.channels.c1.capacity = 1000a1.channels.c1.transactionCapacity = 100# Bind the source and sink to the channel a1.sources.r1.channels = c1 a1.sinks.k1.channel = c1 a1.sinks.k2.channel=c1
```

### 三、总结

channel selector和sink processor都不是必须配置的，他们有自己的默认值。channel selector注重处理数据的流向，sink processor注重处理可靠性和性能。



![Flume学习系列(三)---- Channel Selector与Sink Processors](https://www.javazhiyin.com/wp-content/uploads/2018/11/java9-1543575674.jpeg)



> 参考资料：
>
> - https://blog.csdn.net/silentwolfyh/article/details/51165804