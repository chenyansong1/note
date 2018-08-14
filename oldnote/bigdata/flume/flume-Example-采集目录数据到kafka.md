> 采集目录下的文件数据到kafka





**可以配置的字段参见：flume源码中的 SpoolDirectorySourceConfigurationConstants.java**



```shell

[root@soc60 flume-bak]# cat  apache-flume-1.8.0-bin/conf/example.conf

# example.conf: A single-node Flume configuration

#####################  agent  ############################
a1.sources = r1 
a1.sinks = k1
a1.channels = c1


#####################  sources  ############################

a1.sources.r1.type = spooldir                 
#监控的目录
a1.sources.r1.spoolDir = /root/logs/
a1.sources.r1.basenameHeader = true
# 在header中包含文件名的绝对路径
a1.sources.r1.fileHeader = true
#读取完毕之后，是否删除文件：never 标识不删除（将文件名变成xx.COMPLETED） ； immediate 标识立即删除
a1.sources.r1.deletePolicy = immediate
#是否递归搜索
a1.sources.r1.recursiveDirectorySearch = true

#####################  channels  ############################
a1.channels.c1.type = memory
a1.channels.c1.capacity = 100000
a1.channels.c1.transactionCapacity = 100


##################### Bind the source to the channel ##############
a1.sources.r1.channels = c1

## source 拦截器
a1.sources.r1.interceptors = i1
#是否配置时间戳 
a1.sources.r1.interceptors.i1.type = com.bluedon.flume.FlumeInterCeptorFileName$Builder
# 这里设置了一个拦截器：对指定的文件路径 进行操作
a1.sources.r1.interceptors.i1.filename=true

###################### sinks ###########################
a1.sinks.k1.type= org.apache.flume.sink.kafka.KafkaSink
a1.sinks.k1.brokerList=10.130.10.60:9092,10.130.10.61:9092
#a1.sinks.k1.topic=testflume
a1.sinks.k1.topic=test-topic
a1.sinks.k1.serializer.class=kafka.serializer.StringEncoder


###################### Bind the sink to the channel ###########################
a1.sinks.k1.channel = c1


```







