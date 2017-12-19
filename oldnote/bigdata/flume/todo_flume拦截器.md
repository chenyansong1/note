flume-ng自带了各种拦截器，以实现不同的需求

flume-ng  interceptors 可以理解为一个过滤器，通过配置可以收集到符合自己需要类型的日志
官网提供了以下几种interceptors：

# 1.Timestamp Interceptor

时间戳拦截器，将当前时间戳（毫秒）加入到events header中，key名字为：timestamp，值为当前时间戳。用的不是很多。比如在使用HDFS Sink时候，根据events的时间戳生成结果文件，

```
#配置文件：timestamp_case16.conf  
# Name the components on this agent  
a1.sources = r1  
a1.sinks = k1  
a1.channels = c1  
   
# Describe/configure the source  
a1.sources.r1.type = syslogtcp  
a1.sources.r1.port = 50000  
a1.sources.r1.host = 192.168.233.128  
a1.sources.r1.channels = c1  
   
a1.sources.r1.interceptors = i1  
a1.sources.r1.interceptors.i1.preserveExisting= false  
a1.sources.r1.interceptors.i1.type = timestamp  
   
   
# Describe the sink  
a1.sinks.k1.type = hdfs  
a1.sinks.k1.channel = c1  
#这样生成的文件就放在了指定目录中
a1.sinks.k1.hdfs.path =hdfs://carl:9000/flume/%Y-%m-%d/%H%M  
#文件指定前缀
a1.sinks.k1.hdfs.filePrefix = looklook5.  
a1.sinks.k1.hdfs.fileType=DataStream  
   
# Use a channel which buffers events inmemory  
a1.channels.c1.type = memory  
a1.channels.c1.capacity = 1000  
a1.channels.c1.transactionCapacity = 100 
```


![](/Users/chenyansong/Documents/note/images/bigdata/flume/timestamp-interceptor.jpeg)


# 2.Host Interceptor

该拦截器可以往event的header中插入关键词默认为host主机名或者ip地址（注意是agent运行的机器的主机名或者ip地址）

```
#配置文件：time_host_case17.conf  
# Name the components on this agent  
a1.sources = r1  
a1.sinks = k1  
a1.channels = c1  
   
# Describe/configure the source  
a1.sources.r1.type = syslogtcp  
a1.sources.r1.port = 50000  
a1.sources.r1.host = 192.168.233.128  
a1.sources.r1.channels = c1  
   
a1.sources.r1.interceptors = i1 i2  
a1.sources.r1.interceptors.i1.preserveExisting= false  
a1.sources.r1.interceptors.i1.type =timestamp  
a1.sources.r1.interceptors.i2.type = host  
a1.sources.r1.interceptors.i2.hostHeader =hostname  
a1.sources.r1.interceptors.i2.useIP = false  
   
# Describe the sink  
a1.sinks.k1.type = hdfs  
a1.sinks.k1.channel = c1  
a1.sinks.k1.hdfs.path =hdfs://carl:9000/flume/%Y-%m-%d/%H%M  
a1.sinks.k1.hdfs.filePrefix = %{hostname}  
a1.sinks.k1.hdfs.fileType=DataStream  
   
# Use a channel which buffers events inmemory  
a1.channels.c1.type = memory  
a1.channels.c1.capacity = 1000  
a1.channels.c1.transactionCapacity = 100 
```

增加一个拦截器，类型是host,h将hostname作为文件前缀


# 3.Static Interceptor

Static Interceptor拦截器允许用户增加一个static的header并为所有的事件赋值。范围是所有事件。

```
#配置文件：static_case18.conf  
# Name the components on this agent  
a1.sources = r1  
a1.sinks = k1  
a1.channels = c1  
   
# Describe/configure the source  
a1.sources.r1.type = syslogtcp  
a1.sources.r1.port = 50000  
a1.sources.r1.host = 192.168.233.128  
a1.sources.r1.channels = c1  
a1.sources.r1.interceptors = i1  
a1.sources.r1.interceptors.i1.type = static  
a1.sources.r1.interceptors.i1.key = looklook5  
a1.sources.r1.interceptors.i1.value =looklook10  
   
# Describe the sink  
a1.sinks.k1.type = logger  
   
# Use a channel which buffers events inmemory  
a1.channels.c1.type = memory  
a1.channels.c1.capacity = 1000  
a1.channels.c1.transactionCapacity = 100  
   
# Bind the source and sink to the channel  
a1.sources.r1.channels = c1  
a1.sinks.k1.channel = c1

```


![](/Users/chenyansong/Documents/note/images/bigdata/flume/static-interceptor.jpeg)



# Regex FilteringInterceptor

Regex Filtering Interceptor拦截器用于过滤事件，筛选出与配置的正则表达式相匹配的事件。可以用于包含事件和排除事件。常用于数据清洗，通过正则表达式把数据过滤出来。

```
#配置文件：regex_filter_case19.conf  
# Name the components on this agent  
a1.sources = r1  
a1.sinks = k1  
a1.channels = c1  
   
# Describe/configure the source  
a1.sources.r1.type = syslogtcp  
a1.sources.r1.port = 50000  
a1.sources.r1.host = 192.168.233.128  
a1.sources.r1.channels = c1  
a1.sources.r1.interceptors = i1  
a1.sources.r1.interceptors.i1.type =regex_filter  
a1.sources.r1.interceptors.i1.regex =^[0-9]*$  
a1.sources.r1.interceptors.i1.excludeEvents =true  
   
# Describe the sink  
a1.sinks.k1.type = logger  
   
# Use a channel which buffers events inmemory  
a1.channels.c1.type = memory  
a1.channels.c1.capacity = 1000  
a1.channels.c1.transactionCapacity = 100  
   
# Bind the source and sink to the channel  
a1.sources.r1.channels = c1  
a1.sinks.k1.channel = c1 

```

我们对开头字母是数字的数据，全部过滤。










    转自：http://blog.csdn.net/looklook5/article/details/40588669


