---
title: kafka集群安装及常用shell命令
categories: kafka   
toc: true  
tag: [kafka]
---


# 1.集群安装
```
#解压，创建软链接
cd /home/hadoop/app/
tar -zxvf kafka_2.11-0.9.0.1.tgz
ln -s kafka_2.11-0.9.0.1 kafka
rm -rf kafka_2.11-0.9.0.1.tgz            #删除安装文件，节省空间


#修改配置文件
cd kafka/config/
vim server.properties

broker.id=1            #每个机器上不同，如（broker.id=2， broker.id=3）
log.dirs=/export/servers/log/kafka                 #是kafka的日志文件目录，存放的是消息数据，以主题命名的分区
zookeeper.connect=zk03:2181,zk02:2181,zk01:2181

#################################################

#分发到其他机器
mkdir /export/servers/log/kafka -p
scp -rp ./kafka hdp-node-02:/home/hadoop/app/
scp -rp ./kafka hdp-node-03:/home/hadoop/app/


#启动
#依次在各节点上启动kafka
bin/kafka-server-start.sh  config/server.properties

#后台启动
kafka-server-start.sh config/server.properties &

```


# 2.Kafka常用操作命令
1. 查看当前服务器中的所有topic
bin/kafka-topics.sh --list --zookeeper  zk01:2181
2. 创建topic
./kafka-topics.sh --create --zookeeper zk01:2181 --replication-factor 1 --partitions 3 --topic first
3. 删除topic
sh bin/kafka-topics.sh --delete --zookeeper zk01:2181 --topic test
需要server.properties中设置delete.topic.enable=true否则只是标记删除或者直接重启。
4. 通过shell命令发送消息
kafka-console-producer.sh --broker-list kafka01:9092 --topic itheima
5. 通过shell消费消息
sh bin/kafka-console-consumer.sh --zookeeper zk01:2181 --from-beginning --topic test1
写--from-beginning会显示历史消息，如果只想显示最新的可以不写
6. 查看消费位置
sh kafka-run-class.sh kafka.tools.ConsumerOffsetChecker --zookeeper zk01:2181 --group testGroup
7. 查看某个Topic的详情
sh kafka-topics.sh --topic test --describe --zookeeper zk01:2181
8. 停止服务
./kafka-server-stop.sh
9.添加分区
bin/kafka-topics.sh --alter --zookeeper localhost:2181 --topic test_topic --partitions 3
sh bin./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic kafkatest
10.查看topic的偏移
sh kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list localhost:9092 -topic kafkatest2 --time -1
sh kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list localhost:9092 -topic kafkatest2 --time -2


用ConsumerGroupCommand工具，我们可以使用list，describe，或delete消费者组（注意，删除只有在分组元数据存储在zookeeper的才可用）。当使用新消费者API（broker协调处理分区和重新平衡），当该组的最后一个提交的偏移到期时，该组被删除。 例如，要列出所有主题中的所有用户组：

```
#查看所有的消费者组
bin/kafka-consumer-groups.sh --bootstrap-server broker1:9092 --list

Note: This will only show information about consumers that use the Java consumer API (non-ZooKeeper-based consumers).

monitorwebsitegroup
syslog
ntaflowgroup
aptgroup
```

To view offsets as in the previous example with the ConsumerOffsetChecker, we "describe" the consumer group like this:
要使用ConsumerOffsetChecker查看上一个示例中消费者组的偏移量，我们按如下所示“describe”消费者组：
bin/kafka-consumer-groups.sh --bootstrap-server broker1:9092 --describe --group test-consumer-group

  GROUP                          TOPIC                          PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             OWNER
  test-consumer-group            test-foo                       0          1               3               2               consumer-1_/127.0.0.1

If you are using the old high-level consumer and storing the group metadata in ZooKeeper (i.e. offsets.storage=zookeeper), pass --zookeeper instead of bootstrap-server:
如果你使用老的高级消费者并存储分组元数据在zookeeper（即。offsets.storage=zookeeper）通过--zookeeper，而不是bootstrap-server：
  > bin/kafka-consumer-groups.sh --zookeeper localhost:2181 --list





更多的脚本参见：
http://www.cnblogs.com/fxjwind/p/3794495.html
https://www.iteblog.com/archives/1605.html
http://www.cnblogs.com/tonychai/p/4626567.html

# 3.报错和解决
```
[2015-06-16 11:24:13,015] ERROR Failed to send requests for topics mykafka with correlation ids in [0,8] (kafka.producer.async.DefaultEventHandler) 
[2015-06-16 11:24:13,015] ERROR Error in handling batch of 1 events (kafka.producer.async.ProducerSendThread) 
kafka.common.FailedToSendMessageException: Failed to send messages after 3 tries. 
    at kafka.producer.async.DefaultEventHandler.handle(DefaultEventHandler.scala:90) 
    at kafka.producer.async.ProducerSendThread.tryToHandle(ProducerSendThread.scala:105) 
    at kafka.producer.async.ProducerSendThread$$anonfun$processEvents$3.apply(ProducerSendThread.scala:88) 
    at kafka.producer.async.ProducerSendThread$$anonfun$processEvents$3.apply(ProducerSendThread.scala:68) 
    at scala.collection.immutable.Stream.foreach(Stream.scala:594) 
    at kafka.producer.async.ProducerSendThread.processEvents(ProducerSendThread.scala:67) 
    at kafka.producer.async.ProducerSendThread.run(ProducerSendThread.scala:45)  
```
这个错误是config/server.properties的host.name写的不对，可能是前面的“#”没有去掉或是写的主机名称，改成服务器ip地址就可以了，如果改成localhost单机模式不会有问题，但分布式的时候会报下面错误。
```
[2015-06-16 14:20:59,519] WARN Fetching topic metadata with correlation id 9 for topics [Set(mykafka)] from broker [id:0,host:192.168.10.114,port:9092] failed (kafka.client.ClientUtils$) 
java.nio.channels.ClosedChannelException 
    at kafka.network.BlockingChannel.send(BlockingChannel.scala:100) 
    at kafka.producer.SyncProducer.liftedTree1$1(SyncProducer.scala:73) 
    at kafka.producer.SyncProducer.kafka$producer$SyncProducer$$doSend(SyncProducer.scala:72) 
    at kafka.producer.SyncProducer.send(SyncProducer.scala:113) 
    at kafka.client.ClientUtils$.fetchTopicMetadata(ClientUtils.scala:58) 
    at kafka.producer.BrokerPartitionInfo.updateInfo(BrokerPartitionInfo.scala:82) 
    at kafka.producer.async.DefaultEventHandler$$anonfun$handle$1.apply$mcV$sp(DefaultEventHandler.scala:67) 
    at kafka.utils.Utils$.swallow(Utils.scala:172) 
    at kafka.utils.Logging$class.swallowError(Logging.scala:106) 
    at kafka.utils.Utils$.swallowError(Utils.scala:45) 
    at kafka.producer.async.DefaultEventHandler.handle(DefaultEventHandler.scala:67) 
    at kafka.producer.async.ProducerSendThread.tryToHandle(ProducerSendThread.scala:105) 
    at kafka.producer.async.ProducerSendThread$$anonfun$processEvents$3.apply(ProducerSendThread.scala:88) 
    at kafka.producer.async.ProducerSendThread$$anonfun$processEvents$3.apply(ProducerSendThread.scala:68) 
    at scala.collection.immutable.Stream.foreach(Stream.scala:594) 
    at kafka.producer.async.ProducerSendThread.processEvents(ProducerSendThread.scala:67) 
    at kafka.producer.async.ProducerSendThread.run(ProducerSendThread.scala:45) 
[2015-06-16 14:20:59,520] ERROR fetching topic metadata for topics [Set(mykafka)] from broker [ArrayBuffer(id:0,host:192.168.10.114,port:9092)] failed (kafka.utils.Utils$) 
kafka.common.KafkaException: fetching topic metadata for topics [Set(mykafka)] from broker [ArrayBuffer(id:0,host:192.168.10.114,port:9092)] failed 
    at kafka.client.ClientUtils$.fetchTopicMetadata(ClientUtils.scala:72) 
    at kafka.producer.BrokerPartitionInfo.updateInfo(BrokerPartitionInfo.scala:82) 
    at kafka.producer.async.DefaultEventHandler$$anonfun$handle$1.apply$mcV$sp(DefaultEventHandler.scala:67) 
    at kafka.utils.Utils$.swallow(Utils.scala:172) 
    at kafka.utils.Logging$class.swallowError(Logging.scala:106) 
    at kafka.utils.Utils$.swallowError(Utils.scala:45) 
    at kafka.producer.async.DefaultEventHandler.handle(DefaultEventHandler.scala:67) 
    at kafka.producer.async.ProducerSendThread.tryToHandle(ProducerSendThread.scala:105) 
    at kafka.producer.async.ProducerSendThread$$anonfun$processEvents$3.apply(ProducerSendThread.scala:88) 
    at kafka.producer.async.ProducerSendThread$$anonfun$processEvents$3.apply(ProducerSendThread.scala:68) 
    at scala.collection.immutable.Stream.foreach(Stream.scala:594) 
    at kafka.producer.async.ProducerSendThread.processEvents(ProducerSendThread.scala:67) 
    at kafka.producer.async.ProducerSendThread.run(ProducerSendThread.scala:45) 
Caused by: java.nio.channels.ClosedChannelException 
    at kafka.network.BlockingChannel.send(BlockingChannel.scala:100) 
    at kafka.producer.SyncProducer.liftedTree1$1(SyncProducer.scala:73) 
    at kafka.producer.SyncProducer.kafka$producer$SyncProducer$$doSend(SyncProducer.scala:72) 
    at kafka.producer.SyncProducer.send(SyncProducer.scala:113) 
    at kafka.client.ClientUtils$.fetchTopicMetadata(ClientUtils.scala:58) 
    ... 12 more  
```

