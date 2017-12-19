---
title: hadoop安装过程中的问题集锦
categories: hadoop
toc: true
tag: [hadoop]
---


1. yarn没有启动
```
16/11/18 13:06:22 INFO client.RMProxy: Connecting to ResourceManager at hdp-node-01/192.168.0.11:8032    #ResourceManager (没有连接上ResourceManager 的话就说明yarn没有启动
16/11/18 13:06:23 INFO ipc.Client: Retrying connect to server: hdp-node-01/192.168.0.11:8032. Already tried 0 time(s); retry policy is RetryUpToMaximumCountWithFixedSleep(maxRetries=10, sleepTime=1000 MILLISECONDS)

```

2. datanode不被namenode识别的问题
namenode在format初始化的时候会形成两个标识：blockPoolId、clusterId
新的datanode加入的时候，会获取这两个标识作为自己的工作目录中你的标识
一旦namenode重新format后，namenode的身份标识已变，而datanode持有原来的id，就不会被namenode识别

修改了name node 的存储目录
&emsp;如果修改了name node的存储目录，那么需要重新格式化name node ，此时和原来关联的data node的工作目录中的数据将不能使用了，因为原来data node中的数据version是和name node 关联的，所以此时需要将data node中的数据删除，因为name node是记录data node中的数据的，所以如果name node 重新初始化了，那么data node留着就没有意义了



3. 副本数量的问题
副本数由客户端的参数dfs.replication决定（优先级： conf.setProperty>  自定义配置文件 > jar包中的hdfs-default.xml）


