[TOC]

#### 1.offset位置的查看

kafka消费者在会保存其消费的进度，也就是offset，存储的位置根据选用的kafka api不同而不同。

首先来说说消费者如果是根据javaapi来消费，也就是【kafka.javaapi.consumer.ConsumerConnector】，通过配置参数【zookeeper.connect】来消费。这种情况下，消费者的offset会更新到zookeeper的【consumers/{group}/offsets/{topic}/{partition}】目录下，例如：

 ```
[zk: localhost(CONNECTED) 0] get /kafka/consumers/zoo-consumer-group/offsets/my-topic/0
5662
cZxid = 0x20006d28a
ctime = Wed Apr 12 18:20:51 CST 2017
mZxid = 0x30132b0ed
mtime = Tue Aug 22 18:53:22 CST 2017
pZxid = 0x20006d28a
cversion = 0
dataVersion = 5758
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 4
numChildren = 0
 ```

如果是根据kafka默认的api来消费，即【org.apache.kafka.clients.consumer.KafkaConsumer】，我们会配置参数【bootstrap.servers】来消费。而其消费者的offset会更新到一个kafka自带的topic【__consumer_offsets】下面，查看当前group的消费进度，则要依靠kafka自带的工具【kafka-consumer-offset-checker】，例如： 



```
[root@localhost data]# kafka-consumer-offset-checker --zookeeper localhost :2181/kafka --group test-consumer-group  --topic stable-test
[2017-08-22 19:24:24,222] WARN WARNING: ConsumerOffsetChecker is deprecated and will be dropped in releases following 0.9.0. Use ConsumerGroupCommand instead. (kafka.tools.ConsumerOffsetChecker$)
Group           Topic                          Pid Offset          logSize         Lag             Owner
test-consumer-group stable-test                    0   601808          601808          0               none
test-consumer-group stable-test                    1   602826          602828          2               none
test-consumer-group stable-test                    2   602136          602136          0               none
```



#### 2.更新offset的方式

offset更新的方式，不区分是用的哪种api，大致分为两类：

1. 自动提交，设置enable.auto.commit=true，更新的频率根据参数【auto.commit.interval.ms】来定。这种方式也被称为【at most once】，fetch到消息后就可以更新offset，无论是否消费成功。
2. 手动提交，设置enable.auto.commit=false，这种方式称为【at least once】。fetch到消息后，等消费完成再调用方法【consumer.commitSync()】，手动更新offset；如果消费失败，则offset也不会更新，此条消息会被重复消费一次。



#### 3.offset记录由zk到kafka的转变



###### Kafka 0.10以后的版本

- Kafka 引入了`Timestamp`, 具体可参考[Add a time based log index](https://link.jianshu.com?t=https://cwiki.apache.org/confluence/display/KAFKA/KIP-33+-+Add+a+time+based+log+index), 这样就可以方便的根据时间来获取并回滚相应的消费啦,真是太方便了;
- 不仅如此, Kafka还提供了专门的工具来作Offset rest, 具体不累述,请参考[Add Reset Consumer Group Offsets tooling](https://link.jianshu.com?t=https://cwiki.apache.org/confluence/display/KAFKA/KIP-122%3A+Add+Reset+Consumer+Group+Offsets+tooling)

 参见：https://www.jianshu.com/p/2945a90b48af





kafka官网建议：http://kafka.apache.org/0102/documentation.html#offsetmigration

##### [Migrating offsets from ZooKeeper to Kafka](http://kafka.apache.org/0102/documentation.html#offsetmigration)

Kafka consumers in earlier releases store their offsets by default in ZooKeeper. It is possible to migrate these consumers to commit offsets into Kafka by following these steps:

1. Set `offsets.storage=kafka` and `dual.commit.enabled=true` in your consumer config.
2. Do a rolling bounce of your consumers and then verify that your consumers are healthy.
3. Set `dual.commit.enabled=false` in your consumer config.
4. Do a rolling bounce of your consumers and then verify that your consumers are healthy.

A roll-back (i.e., migrating from Kafka back to ZooKeeper) can also be performed using the above steps if you set 

```
offsets.storage=zookeeper
```

.







 

 

 

 