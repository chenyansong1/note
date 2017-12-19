---
title: mapreduce参数优化
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---





# 1.资源相关参数
```
//以下参数是在用户自己的mr应用程序中配置就可以生效
(1) mapreduce.map.memory.mb: 一个Map Task可使用的资源上限（单位:MB），默认为1024。如果Map Task实际使用的资源量超过该值，则会被强制杀死。
(2) mapreduce.reduce.memory.mb: 一个Reduce Task可使用的资源上限（单位:MB），默认为1024。如果Reduce Task实际使用的资源量超过该值，则会被强制杀死。
(3) mapreduce.map.java.opts: Map Task的JVM参数，你可以在此配置默认的java heap size等参数, e.g.
“-Xmx1024m -verbose:gc -Xloggc:/tmp/@taskid@.gc” （@taskid@会被Hadoop框架自动换为相应的taskid）, 默认值: “”
(4) mapreduce.reduce.java.opts: Reduce Task的JVM参数，你可以在此配置默认的java heap size等参数, e.g.
“-Xmx1024m -verbose:gc -Xloggc:/tmp/@taskid@.gc”, 默认值: “”
(5) mapreduce.map.cpu.vcores: 每个Map task可使用的最多cpu core数目, 默认值: 1
(6) mapreduce.reduce.cpu.vcores: 每个Reduce task可使用的最多cpu core数目, 默认值: 1
 
//应该在yarn启动之前就配置在服务器的配置文件中才能生效
(7) yarn.scheduler.minimum-allocation-mb   1024   给应用程序container分配的最小内存
(8) yarn.scheduler.maximum-allocation-mb   8192 给应用程序container分配的最大内存
(9) yarn.scheduler.minimum-allocation-vcores 1
(10)yarn.scheduler.maximum-allocation-vcores 32
(11)yarn.nodemanager.resource.memory-mb   8192 
 
//shuffle性能优化的关键参数，应在yarn启动之前就配置好
(12)mapreduce.task.io.sort.mb   100         //shuffle的环形缓冲区大小，默认100m
mapreduce.map.sort.spill.percent   0.8    //环形缓冲区溢出的阈值，默认80%

```
<!--more-->

# 2.容错相关参数

```
 
(1) mapreduce.map.maxattempts: #每个Map Task最大重试次数，一旦重试参数超过该值，则认为Map Task运行失败，默认值：4。
(2) mapreduce.reduce.maxattempts: #每个Reduce Task最大重试次数，一旦重试参数超过该值，则认为Map Task运行失败，默认值：4。
(3) mapreduce.map.failures.maxpercent:    #当失败的Map Task失败比例超过该值为，整个作业则失败，默认值为0. 如果你的应用程序允许丢弃部分输入数据，则该该值设为一个大于0的值，比如5，表示如果有低于5%的Map Task失败（如果一个Map Task重试次数超过mapreduce.map.maxattempts，则认为这个Map Task失败，其对应的输入数据将不会产生任何结果），整个作业扔认为成功。
(4) mapreduce.reduce.failures.maxpercent:    #当失败的Reduce Task失败比例超过该值为，整个作业则失败，默认值为0.
(5) mapreduce.task.timeout: #Task超时时间，经常需要设置的一个参数，该参数表达的意思为：如果一个task在一定时间内没有任何进入，即不会读取新的数据，也没有输出数据，则认为该task处于block状态，可能是卡住了，也许永远会卡主，为了防止因为用户程序永远block住不退出，则强制设置了一个该超时时间（单位毫秒），默认是300000。如果你的程序对每条输入数据的处理时间过长（比如会访问数据库，通过网络拉取数据等），建议将该参数调大，该参数过小常出现的错误提示是“AttemptID:attempt_14267829456721_123456_m_000224_0 Timed out after 300 secsContainer killed by the ApplicationMaster.”。

```

# 3.本地运行mapreduce 作业

```
设置以下几个参数:
mapreduce.framework.name=local
mapreduce.jobtracker.address=local
fs.defaultFS=local

```

# 4.效率和稳定性相关参数
```

(1) mapreduce.map.speculative: #是否为Map Task打开推测执行机制，默认为false
(2) mapreduce.reduce.speculative: #是否为Reduce Task打开推测执行机制，默认为false
(3) mapreduce.job.user.classpath.first & mapreduce.task.classpath.user.precedence： #当同一个class同时出现在用户jar包和hadoop jar中时，优先使用哪个jar包中的class，默认为false，表示优先使用hadoop jar中的class。
(4) mapreduce.input.fileinputformat.split.minsize: #FileInputFormat做切片时的最小切片大小，
(5)mapreduce.input.fileinputformat.split.maxsize:  #FileInputFormat做切片时的最大切片大小
(切片的默认大小就等于blocksize，即 134217728)
```

















