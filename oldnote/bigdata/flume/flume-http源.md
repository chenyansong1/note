flume-http源

flume所在ip：172.16.12.111

监听端口：51400



# 配置文件

```
#####################  agent  ############################
a1.sources = r1 
a1.sinks = k1
a1.channels = c1


#####################  sources  ############################

a1.sources.r1.type = http                 
a1.sources.r1.port = 51400
a1.sources.r1.handler = org.apache.flume.source.http.JSONHandler

#####################  channels  ############################
a1.channels.c1.type = memory
a1.channels.c1.capacity = 100000
a1.channels.c1.transactionCapacity = 100


##################### Bind the source to the channel ##############
a1.sources.r1.channels = c1


###################### sinks ###########################
a1.sinks.k1.type= org.apache.flume.sink.kafka.KafkaSink
a1.sinks.k1.brokerList=10.130.10.60:9092,10.130.10.61:9092
#a1.sinks.k1.topic=testflume
a1.sinks.k1.topic=test-topic
a1.sinks.k1.serializer.class=kafka.serializer.StringEncoder


###################### Bind the sink to the channel ###########################
a1.sinks.k1.channel = c1
```





# 发送测试



```
curl -X POST -d'[{"headers":{"h1":"v1","h2":"v2"},"body":"hello body from cys"}]'  http://172.16.12.111:51400
```

