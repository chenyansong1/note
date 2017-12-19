---
title: kafka-offset监控
categories: kafka   
toc: true  
tag: [kafka]
---


1.下载jar包

https://github.com/quantifind/KafkaOffsetMonitor/releases

2.启动

```
#!/bash/sh

java -cp KafkaOffsetMonitor-assembly-0.2.0.jar \
com.quantifind.kafka.offsetapp.OffsetGetterWeb  \
--zk localhost:2181 \
--port 8089 \
--refresh 10.seconds \
--retain 30.days \
1>./stdout.log \
2>.stderr.log

########
# zk ：zookeeper主机地址，如果有多个，用逗号隔开
# port ：应用程序端口
# refresh ：应用程序在数据库中刷新和存储点的频率
# retain ：在db中保留多长时间
# dbName ：保存的数据库文件名，默认为offsetapp
########

```


3.查看

输入网址，我的是http://monitor-ip:8089

![](/Users/chenyansong/Documents/note/images/flume/offset-monitor.png)

参数说明

        Topic：创建Topic名称
        Partition：分区编号
        Offset：表示该Parition已经消费了多少Message
        LogSize：表示该Partition生产了多少Message
        Lag：表示有多少条Message未被消费
        Owner：表示消费者
        Created：表示该Partition创建时间
        Last Seen：表示消费状态刷新最新时间















