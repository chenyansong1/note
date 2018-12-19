
转自：http://wzktravel.github.io/2015/12/31/kafka-reassign/


[TOC]



# kafka重新分配partition

今天kafka测试环境中机器磁盘告警，占用率超过了80%，原来是某一个`topic`的`partition`为1，只往一台机器上写数据，造成kafka集群空间使用不均。
下面主要使用`kafka-topics.sh`和`kafka-reassign-partitions.sh`来解决问题。
推荐使用[kafka manager](https://github.com/yahoo/kafka-manager)来管理kafka集群。

## 修改topic的partitions

```
./bin/kafka-topics.sh --zookeeper vlnx111122:2181 --alter --topic test --partitions 6
```

此命令执行完之后即可再kafka集群其他机器中找到此topic的目录

## 扩容、删除机器

只要配置zookeeper.connect为要加入的集群，再启动Kafka进程，就可以让新的机器加入到Kafka集群。但是新的机器只针对新的Topic才会起作用，在之前就已经存在的Topic的分区，不会自动的分配到新增加的物理机中。为了使新增加的机器可以分担系统压力，必须进行消息数据迁移。Kafka提供了`kafka-reassign-partitions.sh`进行数据迁移。

这个脚本提供3个命令：

- `--generate`: 根据给予的Topic列表和Broker列表生成迁移计划。generate并不会真正进行消息迁移，而是将消息迁移计划计算出来，供execute命令使用。
- `--execute`: 根据给予的消息迁移计划进行迁移。
- `--verify`: 检查消息是否已经迁移完成。

### 示例

topic为`test`目前在broker id为1,2,3的机器上，现又添加了两台机器，broker id为4,5，现在想要将压力平均分散到这5台机器上。

#### 手动生成一个json文件`topic.json`

```
{ 
    "topics": [
        {"topic": "test"}
    ],
    "version": 1
}
```

#### 调用`--generate`生成迁移计划，将`test`扩充到所有机器上

```
./bin/kafka-reassign-partitions.sh --zookeeper vlnx111122:2181 --topics-to-move-json-file topic.json  --broker-list  "1,2,3,4,5"  --generate
```

生成类似于下方的结果

```
Current partition replica assignment

{"version":1,
 "partitions":[....]
}

Proposed partition reassignment configuration

{"version":1,
 "partitions":[.....]
}
```

`Current partition replica assignment`表示当前的消息存储状况。`Proposed partition reassignment configuration`表示迁移后的消息存储状况。
将迁移后的json存入一个文件`reassignment.json`，供`--execute`命令使用。

#### 执行`--execute`进行扩容。

```
./bin/kafka-reassign-partitions.sh --zookeeper vlnx111122:2181 --reassignment-json-file reassignment.json --execute
Current partition replica assignment
... 

Save this to use as the --reassignment-json-file option during rollback
...
```

#### 使用`--verify`查看进度

```
./bin/kafka-reassign-partitions.sh --zookeeper vlnx111122:2181 --reassignment-json-file reassignment.json --verify
```

## 相关命令

1. `./bin/kafka-console-producer.sh --broker-list vlnx111111:9092 --topic test`
2. `./bin/kafka-console-consumer.sh --zookeeper vlnx111122:2181 --topic test --from-beginning`
3. `./bin/kafka-topics.sh --zookeeper vlnx111122:2181 --list`
4. `./bin/kafka-topics.sh --zookeeper vlnx111122:2181 --create --replication-factor 2 --partition 6 --topic test`
5. `./bin/kafka-topics.sh --zookeeper vlnx111122:2181 --delete --topic test`
6. `./bin/kafka-topics.sh --zookeeper vlnx111122:2181 --describe --topic test`

## 参考

- <http://kafka.apache.org/082/documentation.html#basic_ops_modify_topic>
- [How to choose the number of topics/partitions in a Kafka cluster?](http://www.confluent.io/blog/how-to-choose-the-number-of-topicspartitions-in-a-kafka-cluster/)
- [Kafka 0.9 Configuration Best Practices](https://community.hortonworks.com/articles/49789/kafka-best-practices.html)
- [Apche Kafka 的生与死 – failover 机制详解](http://www.cnblogs.com/fxjwind/p/4972244.html)