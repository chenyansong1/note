> 采集目录下的文件数据到kafka



[TOC]



**可以配置的字段参见：flume源码中的 SpoolDirectorySourceConfigurationConstants.java**



# 配置文件



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



# 可能报错的情况





**如果监控的目录下有重复的文件名称，那么此时会报下面的错误：**

```shell
2014-06-02 12:01:04,070 (pool-6-thread-1) [ERROR - org.apache.flume.source.SpoolDirectorySource$SpoolDirectoryRunnable.run(SpoolDirectorySource.java:256)] FATAL: Spool Directory source source1: { spoolDir: /usr/aboutyunlog }: Uncaught exception in SpoolDirectorySource thread. Restart or reconfigure Flume to continue processing. java.lang.IllegalStateException: File name has been re-used with different files. Spooling assumptions violated for /usr/aboutyunlog/test1.COMPLETED at org.apache.flume.client.avro.ReliableSpoolingFileEventReader.rollCurrentFile(ReliableSpoolingFileEventReader.java:362) 
```



他的错是因为flume监控目录下有重复文件名称了， 



> 解决的方式：
>
> 将目录遍历的策略变成：遍历删除：a1.sources.r1.deletePolicy = immediate
>
> 这样就不会有重复的文件名问题了



参见：http://www.aboutyun.com/blog-61-218.html







**修改了正在正在读取的文件时间 or 修改了文件的大小**

报错如下：

```
 File has been modified since being read
 or
 File has changed size since being read
```



原因：是文件太大，或者由于网络的原因文件传输没有完成，而此时flume就开始在读取文件，那么就会报上面的错误



解决方式1：将上传的文件放到一个临时的目录下面，待文件全部上传完成之后，再移动到flume监控的目录下

解决方式2：修改flume的源码如下：

参见：https://www.cnblogs.com/pingjie/p/4146727.html





