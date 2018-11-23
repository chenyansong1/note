

```
[root@soc30 flume-bak]# cat apache-flume-1.8.0-bin/conf/example.conf

example.conf: A single-node Flume configuration

###############  agent

a1.sources = r1 

a1.sinks = k1

a1.channels = c1

###############  sources

需要加上remote ip

a1.sources.r1.type = com.bluedon.flume.MyNetcatUdpSource

a1.sources.r1.bind=0.0.0.0

a1.sources.r1.port=5143

a1.sources.r1.remoteAddress=myremoteIP

a1.sources.r1.max-line-length = 2048

###############  channels

a1.channels.c1.type = memory

a1.channels.c1.capacity = 100000

a1.channels.c1.transactionCapacity = 100

############### Bind the source to the channel

a1.sources.r1.channels = c1

source 拦截器

a1.sources.r1.interceptors = i1

a1.sources.r1.interceptors.i1.type = search_replace

a1.sources.r1.interceptors.i1.searchPattern = [0-9]+

a1.sources.r1.interceptors.i1.replaceString = lxw1234

a1.sources.r1.interceptors.i1.charset = UTF-8

是否配置时间戳

a1.sources.r1.interceptors.i1.type = com.bluedon.flume.FlumeInterCeptor$Builder

a1.sources.r1.interceptors.i1.timestamp = true

a1.sources.r1.interceptors.i1.staicfield = ~user~notice~Flume222

a1.sources.r1.interceptors.i1.ip = true

################ sinks

a1.sinks.k1.type= org.apache.flume.sink.kafka.KafkaSink

a1.sinks.k1.brokerList=10.26.52.30:9092

a1.sinks.k1.topic=log-topic

a1.sinks.k1.serializer.class=kafka.serializer.StringEncoder

################ Bind the sink to the channel

a1.sinks.k1.channel = c1

```



测试

```
#发送测试数据
echo "111221-chenyansong-ccc"|nc -u 127.0.0.1 5143

#输出结果
lxw1234-chenyansong-ccc
```



