[TOC]



Kafka中topic状态Leader:none的问题解决

创建topic的时候没有问题，但是通过console去生产消息的时候，就会有如下的问题

```shell
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
nihao
[2016-10-08 17:58:54,821] WARN Error while fetching metadata with correlation id 0 : {test=UNKNOWN_TOPIC_OR_PARTITION} (org.apache.kafka.clients.NetworkClient)
[2016-10-08 17:58:55,026] WARN Error while fetching metadata with correlation id 1 : {test=UNKNOWN_TOPIC_OR_PARTITION} (org.apache.kafka.clients.NetworkClient)
[2016-10-08 17:58:55,128] WARN Error while fetching metadata with correlation id 2 : {test=UNKNOWN_TOPIC_OR_PARTITION} (org.apache.kafka.clients.NetworkClient)
[2016-10-08 17:58:55,230] WARN Error while fetching metadata with correlation id 3 : {test=UNKNOWN_TOPIC_OR_PARTITION} (org.apache.kafka.clients.NetworkClient)
```

察看了 topic信息**发现这个topic的Leader: none**

```
bin/kafka-topics.sh --zookeeper hdfmaster:2181 --describe opic:test PartitionCount:1 ReplicationFactor:1 Configs:Topic: test Partition: 0 Leader: none Replicas: 1002 Isr: 
```

去zookeeper下查看对应topic的partition信息，也是为null

```shell
[zk: localhost:2181(CONNECTED) 1] ls /brokers/topics/test
[]
[zk: localhost:2181(CONNECTED) 2] 

#正常情况下，本应该让如下
[zk: localhost:2181(CONNECTED) 1] ls /brokers/topics/first_cys
[partitions]
[zk: localhost:2181(CONNECTED) 2]
```



**报获取不到元数据**的错误，产生的原因可能是如下的方面：

1. 本地没有配置hosts
2. service.propeties配置文件，ip配置不对，[参看](https://stackoverflow.com/questions/35788697/leader-not-available-kafka-in-console-producer/36007347)
3. 可能是zookeeper中的kafka数据信息不对，需要删除`rmr /controller`



**产生第三种情况的原因是：zookeeper和kafka所在的机器重启之后导致的，具体原因待查**



参考：

https://ask.hellobi.com/blog/seng/15495

https://community.hortonworks.com/content/supportkb/210187/errorpartition-0-leader-none-replicas-100310011002.html