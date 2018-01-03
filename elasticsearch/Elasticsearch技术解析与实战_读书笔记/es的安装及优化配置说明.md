es的安装及配置说明

# 1.安装Java

es需要安装jdk1.7及以上版本，建议安装jdk1.8,下面查看Java的版本，看是否安装Java

```
java -version

```


# 2.安装es

到es的官网去下载es,然后解压即可使用

# 3.配置说明

## 3.1.es的JAVA_OPTS参数

因为es是Java开发的，所以JVM的环境变量JAVA\_OPTS对es是非常重要的，在JAVA_OPTS中对es最重要的参数是-Xmx（最大可以使用内存参数），**一般情况下大内存更能发挥es的作用，建议设置-Xmx为物理内存的一半，为了减小内存分配带来的性能损耗，最好一开始就设置初始内存和最大内存都为物理内存的一半（即Xms和Xmx着两个参数）**

由于JAVA_OPTS大多数对整个机器环境起作用，所以最好保留默认的JAVA_OPTS，最好用ES_JAVA_OPTS环境变量来设置JAVA_OPTS参数

默认的配置文件在elasticsearch/bin/elasticsearch.in.sh中

```

if [ "x$ES_MIN_MEM" = "x" ]; then
    ES_MIN_MEM=256m
fi
if [ "x$ES_MAX_MEM" = "x" ]; then
    ES_MAX_MEM=1g
fi
if [ "x$ES_HEAP_SIZE" != "x" ]; then
    ES_MIN_MEM=$ES_HEAP_SIZE
    ES_MAX_MEM=$ES_HEAP_SIZE
fi

# min and max heap sizes should be set to the same value to avoid
# stop-the-world GC pauses during resize, and so that we can lock the
# heap in memory on startup to prevent any of it from being swapped
# out.
JAVA_OPTS="$JAVA_OPTS -Xms${ES_MIN_MEM}"
JAVA_OPTS="$JAVA_OPTS -Xmx${ES_MAX_MEM}"

```

ES\_HEAP\_SIZE环境变量允许设置被es中Java进程的堆内存大小，最小值和最大值将分配相同的值，可以通过ES\_MIN\_MEM(默认是256M),和ES\_MAX\_MEM（默认是1g)对堆内存进行设置


## 3.2.参数配置的几种方式

在es的config目录下有两个文件

* elasticsearch.yml（es配置不同模块的配置文件）
* logging.yml（es的日志配置文件)


```
[elsearch@hdp-node-02 bigdata_installed]$ ll  ./elasticsearch/config/
-rw-rw-r--. 1 elsearch elsearch 3194 Jun  8 12:00 elasticsearch.yml
-rw-rw-r--. 1 elsearch elsearch 2571 Aug 24  2016 logging.yml
```
ES提供了多种方式进行设置，下面是使用不同的个格式进行设置：

1.在yml文件中配置

```
node.name:node-01
```

2.在json文件中配置

只需要把elasticsearch.yml文件修改成elasticsearch.json,然后配置方式如下：

```
{
"node":{"name":"node-01"}
}
```

3.通过es命令参数来设置配置信息

```
./bin/elasticserach -Des.node.name=node-01
```

## 3.3.elasticsearch.yml配置文件说明

在路径：/bigdata_installed/elasticsearch/config

在这个配置文件中会配置：**集群节点的名字，node的名字，ip,port 等**

```
# ---------------------------------- Cluster -----------------------------------
# 集群名称：确保在不同的环境中集群的名称不能重复，否则，节点可能连接到错误的集群上
 cluster.name: my-application


# ------------------------------------ Node ------------------------------------
# Use a descriptive name for the node:
# 节点名称：默认情况下，当节点启动的时候，es将随机从一份3000个名字的列表中随机指定一个，如果机器只是运行在一个集群节点，可以用${hostname}设置节点的名称为主机名
# node.name: node-1
#
# 节点描述
# node.rack: r1

# ----------------------------------- Paths ------------------------------------
# Path to directory where to store the data (separate multiple locations by comma):
# 索引存储位置，多个用逗号分隔
# path.data: ${elasticsearch_home}/data
#
# 日志存储位置
# path.logs: ${elasticsearch_home}/logs


# ----------------------------------- Memory -----------------------------------
# Lock the memory on startup:
# 内存分配模式
# bootstrap.memory_lock: true
#
# Make sure that the `ES_HEAP_SIZE` environment variable is set to about half the memory
# available on the system and that the owner of the process is allowed to use this limit.
#
# Elasticsearch performs poorly when the system is swapping the memory.



# ---------------------------------- Network -----------------------------------
#
# Set the bind address to a specific IP (IPv4 or IPv6):
# 绑定网卡IP
 network.host: 192.168.153.202
#
# Set a custom port for HTTP:
# http协议端口
 http.port: 9200



# --------------------------------- Discovery ----------------------------------
#
# Pass an initial list of hosts to perform discovery when new node is started:
# The default list of hosts is ["127.0.0.1", "[::1]"]
# 开始发现新节点的IP地址
# discovery.zen.ping.unicast.hosts: ["host1", "host2"]
#
# Prevent the "split brain" by configuring the majority of nodes (total number of nodes / 2 + 1):
# 最多发现主节点的个数
# discovery.zen.minimum_master_nodes: 3
#
# For more information, see the documentation at:
# <http://www.elastic.co/guide/en/elasticsearch/reference/current/modules-discovery.html>



# ---------------------------------- Gateway -----------------------------------
# 当重启集群节点后最少启动N个节点后开始做恢复工作
# Block initial recovery after a full cluster restart until N nodes are started:
# gateway.recover_after_nodes: 3


# ---------------------------------- Various -----------------------------------
#
# Disable starting multiple nodes on a single system:
# 一台机器上最多启动的节点数
# node.max_local_storage_nodes: 1
#
# Require explicit names when deleting indices:
# 当删除一个索引的时候需要指定具体索引的名称
# action.destructive_requires_name: true

```



## 3.4.索引配置说明

在集群中创建的索引可以提供每个索引自己的设置，例如：下面创建一个索引刷新间隔是5秒钟而不是默认的刷新间隔（格式可以是YML或JSON)

1.在yml中配置

```
# vim elasticsearch.yml
index.refresh.interval:5s
```

2.在启动es的用参数指定

```
./bin/elasticsearch -Des.index.refresh.interval=5s
```

这样以后创建的索引，默认的刷新间隔就是5s

## 3.5.日志配置说明

日志的配置文件在：es_home/config/logging.yml中进行配置，他也是支持json格式的，可以加载多个配置文件，在启动es后，系统会自动合并多个配置文件，通知es也是支持不同的日志后缀格式的（.yml,.yaml,.json,.properties)

下面是logging.yml的部分配置，因为下面的配置，所以在logs/目录下生成了一个配置文件

```
appender:
  console:
    type: console
    layout:
      type: consolePattern
      conversionPattern: "[%d{ISO8601}][%-5p][%-25c] %m%n"

  file:#这是一类日志#
    type: dailyRollingFile
    file: ${path.logs}/${cluster.name}.log
    datePattern: "'.'yyyy-MM-dd"
    layout:
      type: pattern
      conversionPattern: "[%d{ISO8601}][%-5p][%-25c] %.10000m%n"

  deprecation_log_file:	#这是一类日志#
    type: dailyRollingFile
    file: ${path.logs}/${cluster.name}_deprecation.log
    datePattern: "'.'yyyy-MM-dd"
    layout:
      type: pattern
      conversionPattern: "[%d{ISO8601}][%-5p][%-25c] %m%n"

  index_search_slow_log_file:#这是一类日志#
    type: dailyRollingFile
    file: ${path.logs}/${cluster.name}_index_search_slowlog.log
    datePattern: "'.'yyyy-MM-dd"
    layout:
      type: pattern
      conversionPattern: "[%d{ISO8601}][%-5p][%-25c] %m%n"

  index_indexing_slow_log_file:#这是一类日志#
    type: dailyRollingFile
    file: ${path.logs}/${cluster.name}_index_indexing_slowlog.log
    datePattern: "'.'yyyy-MM-dd"
    layout:
      type: pattern
      conversionPattern: "[%d{ISO8601}][%-5p][%-25c] %m%n"

```

在日志的配置文件中，可以配置文件日志，索引搜索日志文件，索引慢查询日志文件，普通的日志文件，在es_home/logs下有下面的文件

```
[elsearch@hdp-node-02 logs]$ ls

total 48
-rw-r--r--. 1 elsearch elsearch     0 Jun  7 17:43 elasticsearch_deprecation.log
-rw-r--r--. 1 elsearch elsearch     0 Jun  7 17:43 elasticsearch_index_indexing_slowlog.log
-rw-r--r--. 1 elsearch elsearch     0 Jun  7 17:43 elasticsearch_index_search_slowlog.log
-rw-r--r--  1 root     root       419 Oct  1 10:15 elasticsearch.log
-rw-rw-r--. 1 elsearch elsearch 26539 Jun 11 16:22 elasticsearch.log.2017-06-11
-rw-rw-r--  1 elsearch elsearch 13761 Sep 19 18:25 elasticsearch.log.2017-09-19

```


# 4.运行es

进入es_home/bin目录下，**启动es不能是root权限**，所以我们需要新建一个用户，如：这里指定的是elsearch用户

```
#切换用户
sudo su - elsearch

#启动，这样的启动方式是前台启动，如果需要后台启动，那么需要指定参数-d
bin/elasticsearch 

#后台启动
bin/elasticsearch -d
```

当然，我们也是可以在启动的时候指定一些其他的参数，如指定集群的名称和结点的名称

```
bin/elasticsearch --cluster.name my_cluster_name --node.name my_node_name

```

默认情况下es使用9200端口提供的REST API,当然该端口是可以配置的,浏览器输入：http://192.168.153.202:9200/ ，得到如下的内容：

```
{
  "name" : "Hitman",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "F3Hb7C6vREKDWvN5rkisiw",
  "version" : {
    "number" : "2.4.2",
    "build_hash" : "161c65a337d4b422ac0c805f284565cf2014bb84",
    "build_timestamp" : "2016-11-17T11:51:03Z",
    "build_snapshot" : false,
    "lucene_version" : "5.5.2"
  },
  "tagline" : "You Know, for Search"
}
```


# 5.停止es

```
#如果是控制台启动的，即没有加-d参数
Ctrl+C

#如果是后台启动，那么需要使用kill
#ps查看进程的pid，然后使用kill杀掉进程
ps -ef|grep elasticserach

kill pid

```


