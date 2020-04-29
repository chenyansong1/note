---
title: zk详细配置说明
categories: hadoop   
tags: [zookeeper]
---


两种配置形式
* 基于java的系统属性配置:比如:-Djava.library.path
* zk自身的zoo.cfg文件

属性名称|作用|说明
-------|---|-----
dataLogDir|配置事务日志文件存储目录|1.不支持系统属性配置<br/>2.默认为属性dataDir的值<br/>3.在高并发下,有大量的事务日志和快照，会导致磁盘IO瓶颈，因此在高并发下,不建议使用默认配置，最好把dataDir和此属性配置的目录不同的磁盘下，从而提高IO
snapCount|两次快照间隔的事务日志条数|1.事务日志条数达到这个数目，就要触发数据快照<br/>2.默认值Ѫ 100000<br/>3.仅支持系统属性配置方式
preAllocSize|事务日志文件预分配的磁盘空间大小|1.仅支持系统属性配置， zookeeper.preAllocSize<br/>2.默认值Ѫ 65535，即64M<br/>3.此参数与snapCount有关， snapCount大，就需要多分配
minSessionTimeout<br/>maxSessionTimeout|会话失效的时间的边界控制(服务器端)|1.不支持系统属性<br/>2.默认为ticktime的2倍和20倍<br/>3.当客户端传递过来的超时时间不在这两个参数之间，则最小取minSessionTimeout，最大取maxSessionTimeout
maxClientCnxns|从socket层限制客户端与单台服务器的并发连接数|1.不支持系统属性,默认值为60,0表示不限制<br/>2.以IP地址为粒度进行控制<br/>3.只能控制单台机器,不能控制总连接
jute.maxbuffer|配置单个节点最大的数据大小|1.默认是10M，单位是字节，仅支持系统属性方式配置<br/>2.Zk上存储的数据不易过多,主要是考虑多节点写入的性能<br/>3.需要在服务器端和客户端都配置才能生效
Autopurge.snapRetainCount|自动清理快照和事务日志时需要保留的文件数|1.不支持系统属性配置,系统默认为3,可以不用配置<br/>2.最小值为3,避免磁盘损坏后不能回复数据
Autopurge.purgeInterval|自动清理快照和事务的周期|1.不支持系统属性,默认为0,表示不开启自动清理<br/>2.与Autopurge.snapRetainCount属性一起配合使用<br/>3.配置为负数也表示不清理
fsync.warningthresholdms|事务日志刷新到磁盘的报警阈值|1.支持系统属性,默认值是1000ms<br/>2.如果fsync的操作超过此时间就会在日志中打印报警日志
forceSync| 日志提交时是否强磁盘|1.默认为true<br/>2.仅支持系统属性配置:zookeeper.forceSync,<br/>3.如果设置为true,可以提升写入性能,但是会有数据丢失风险
cnxTimeout| 选举过程中，服务器之间创建tcp连接的超时时间|1.仅支持系统属性配置:zokeeper.cnxTimeout <br/>2.默认为5000ms


参见:
[zookeeper配置文件详解](http://blog.csdn.net/lengzijian/article/details/9226867)









