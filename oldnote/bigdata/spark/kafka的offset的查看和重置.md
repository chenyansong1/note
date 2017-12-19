
在kafka上有对每个topic对应的partiiton的offset，这是一种offset，这是kafka自身的offset；
还有一种offset是消费者组（consumerGroup）对应的offset，这个offset记录是当前消费者组所消费的offset的位置，这样offset可能落后于kafka自身的offset

最近在spark读取kafka消息时，每次读取都会从kafka最新的offset读取。但是如果数据丢失，如果在使用Kafka来分发消息，在数据处理的过程中可能会出现处理程序出异常或者是其它的错误，会造成数据丢失或不一致。这个时候你也许会想要通过kafka把数据从新处理一遍，或者指定kafka的offset读取。kafka默认会在磁盘上保存到7天的数据，**你只需要把kafka的某个topic的consumer的offset设置为某个值或者是最小值，就可以使该consumer从你设置的那个点开始消费。这就需要从zk里面修改offset的值。**

# 1.查询topic的offset的范围


查询offset的最小值

```
[root@hdp-node-01 ~]# cd /bigdata_installed/kafka
[root@hdp-node-01 kafka]# bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list hdp-node-01:9092 -topic first --time -2
first:2:0
first:1:0
first:0:0

```

查询offset的最大值

```
[root@hdp-node-01 kafka]# bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list hdp-node-01:9092 -topic first --time -1
first:2:29
first:1:27
first:0:28
[root@hdp-node-01 kafka]# 

```

从上面的输出可以看出topic:first的三个分区的 offset范围

# 2.设置consumer group的offset

启动zookeeper client

```
/zookeeper/bin/zkCli.sh
```

通过下面命令设置consumer group:DynamicRangeGroup topic:first partition:0的offset为1288:
```
set /consumers/DynamicRangeGroup/offsets/first/0 1288
```

注意如果你的kafka设置了zookeeper root，比如为/kafka，那么命令应该改为：
```
set /kafka/consumers/DynamicRangeGroup/offsets/DynamicRange/0 1288
```

在kafka的配置文件server.properties 关于zookeeper有如下的配置（其中指定了kafka的root目录）
```
zookeeper.connect=hadoop01199:41810,hadoop01200:41810,hadoop01201:41810,hadoop01202:41810,hadoop01203:41810/kafka
```

# 3.生效 

重启相关的应用程序，就可以从设置的offset开始读数据了。 



参考：http://www.cnblogs.com/hd-zg/p/5831219.html


