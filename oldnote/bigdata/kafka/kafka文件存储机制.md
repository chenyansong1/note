---
title: kafka文件存储机制
categories: kafka   
toc: true  
tag: [kafka]
---


# Kafka文件存储基本结构
* 在Kafka文件存储中，同一个topic下有多个不同partition，每个partition为一个目录，partiton命名规则为topic名称+有序序号，第一个partiton序号从0开始，序号最大值为partitions数量减1。

<!--more-->

```
#日志文件
在config/server.properties配置文件中，可以配置日志目录，如下：
log.dirs=/export/servers/log/kafka   

#目录结构如下：
[root@hdp-node-01 first-2]# tree /export/servers/log/kafka/
/export/servers/log/kafka/
|-- cleaner-offset-checkpoint
|-- first-2                            #first是我创建的topic的名字，first-2是这个主题的第3个分区（从0开始），该分区下面有inde、log文件，启动log文件章存放的是消息队列的消息
|   |-- 00000000000000000000.index
|   |-- 00000000000000000000.log
|-- meta.properties
|-- recovery-point-offset-checkpoint
|-- replication-offset-checkpoint

```

* 每个partion(目录)相当于一个巨型文件被平均分配到多个大小相等segment(段)数据文件中。**但每个段segment file消息数量不一定相等（默认大小都是1G）**，这种特性方便old segment file快速被删除。默认保留7天的数据。
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/kafka/store/1.png)


* 每个partiton只需要支持顺序读写就行了，segment文件生命周期由服务端配置参数决定。（什么时候创建，什么时候删除）
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/kafka/store/2.png)

数据有序的讨论？
一个partition的数据是否是有序的？
&emsp;&emsp;间隔性有序，不连续,针对一个topic里面的数据，只能做到partition内部有序，不能做到全局有序.

特别加入消费者的场景后，如何保证消费者消费的数据全局有序的？
&emsp;&emsp;伪命题,如果要全局有序的，必须保证生产有序，存储有序，消费有序。由于生产可以做集群，存储可以分片，消费可以设置为一个consumerGroup，要保证全局有序，就需要保证每个环节都有序。只有一个可能，就是一个生产者，一个partition，一个消费者。这种场景和大数据应用场景相悖。


# Kafka Partition Segment
Segment file组成：由2大部分组成，分别为index file和data file，此2个文件一一对应，成对出现，后缀".index"和“.log”分别表示为segment索引文件、数据文件。
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/kafka/store/3.png)



Segment文件命名规则：partion全局的第一个segment从0开始，后续每个segment文件名为上一个segment文件最后一条消息的offset值。数值最大为64位long大小，19位数字字符长度，没有数字用0填充。


索引文件存储大量元数据，数据文件存储大量消息，索引文件中元数据指向对应数据文件中message的物理偏移地址。
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/kafka/store/4.png)

在00000000000000368769.index索引文件中记录着的是稀疏索引,记录如下:
00000000000000368769.log文件中第1条信息("Message368700"),在磁盘的0的位置;
00000000000000368769.log文件中第3条信息("Message3687772"),在磁盘的497的位置;



# Kafka 查找message
读取offset=368776的message，需要通过下面2个步骤查找。

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/kafka/store/5.png)


## 查找segment file

00000000000000000000.index表示最开始的文件，起始偏移量(offset)为0
00000000000000368769.index的消息量起始偏移量为368770 = 368769 + 1
00000000000000737337.index的起始偏移量为737338=737337 + 1
其他后续文件依次类推。
以起始偏移量命名并排序这些文件，只要根据offset **二分查找**文件列表，就可以快速定位到具体文件。当offset=368776时定位到00000000000000368769.index和对应log文件。

## 通过segment file查找message	
当offset=368776时，依次定位到00000000000000368769.index的元数据物理位置和00000000000000368769.log的物理偏移地址
然后再通过00000000000000368769.log顺序查找直到offset=368776为止。





