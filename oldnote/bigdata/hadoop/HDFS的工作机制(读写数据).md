---
title: HDFS的工作机制(读写数据)
categories: hadoop   
toc: true  
tag: [hadoop]
---




# 1.概述
1. HDFS集群分为两大角色：NameNode、DataNode  (Secondary Namenode)
2. NameNode负责管理整个文件系统的元数据
3. DataNode 负责管理用户的文件数据块
4. 文件会按照固定的大小（blocksize）切成若干块后分布式存储在若干台datanode上
5. 每一个文件块可以有多个副本，并存放在不同的datanode上
6. Datanode会定期向Namenode汇报自身所保存的文件block信息，而namenode则会负责保持文件的副本数量
7. HDFS的内部工作机制对客户端保持透明，客户端请求访问HDFS都是通过向namenode申请来进行

<!--more-->

# 2.HDFS写数据流程
客户端要向HDFS写数据，首先要跟namenode通信以确认可以写文件并获得接收文件block的datanode，然后，客户端按顺序将文件逐个block传递给相应datanode，并由接收到block的datanode负责向其他datanode复制block的副本


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/read_write_data/write_data.png)



# 3.HDFS读数据流程

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/read_write_data/read_data.png)


1、跟namenode通信查询元数据，找到文件块所在的datanode服务器
2、挑选一台datanode（就近原则，然后随机）服务器，请求建立socket流
3、datanode开始发送数据（从磁盘里面读取数据放入流，以packet为单位来做校验）
4、客户端以packet为单位接收，现在本地缓存，然后写入目标文件