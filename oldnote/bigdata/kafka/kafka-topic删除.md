---
title: kafka文件存储机制
categories: kafka   
toc: true  
tag: [kafka]
---


彻底删除kafka的topic数据


* 1.删除kafka存储目录（server.properties文件log.dirs配置，默认为"/tmp/kafka-logs"）相关topic目录
* 2.Kafka 删除topic的命令是：

```
     ./bin/kafka-topics  --delete --zookeeper 【zookeeper server】  --topic 【topic name】
```

如果kafaka启动时加载的配置文件中server.properties没有配置delete.topic.enable=true，那么此时的删除并不是真正的删除，而是把topic标记为：marked for deletion,你可以通过命令：./bin/kafka-topics --zookeeper 【zookeeper server】 --list 来查看所有topic

此时你若想真正删除它，可以如下操作：

* 1.登录zookeeper客户端：命令：./bin/zookeeper-client
* 2.找到topic所在的目录：ls /brokers/topics
* 3.找到要删除的topic，执行命令：rmr /brokers/topics/【topic name】即可，此时topic被彻底删除。

另外被标记为marked for deletion的topic你可以在zookeeper客户端中通过命令获得：ls /admin/delete_topics/【topic name】，

如果你删除了此处的topic，那么marked for deletion 标记消失，zookeeper 的config中也有有关topic的信息： ls /config/topics/【topic name】暂时不知道有什么用

总结：
彻底删除topic：
 1、删除kafka存储目录（server.properties文件log.dirs配置，默认为"/tmp/kafka-logs"）相关topic目录
 2、如果配置了delete.topic.enable=true直接通过命令删除，如果命令删除不掉，直接通过zookeeper-client 删除掉broker下的topic即可。

