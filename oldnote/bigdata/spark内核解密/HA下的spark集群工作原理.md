---
title: HA下的spark集群工作原理
categories: spark   
toc: true  
tag: [spark]
---


![](/assert/img/bigdata/spark内核解密/ha-spark.png)


# 添加zk来解决master的单点问题

到此为止，Spark集群安装完毕，但是有一个很大的问题，那就是Master节点存在单点故障，要解决此问题，就要借助zookeeper，并且启动至少两个Master节点来实现高可靠，配置方式比较简单：
Spark集群规划：node1，node2是Master；node3，node4，node5是Worker  , 安装配置zk集群，并启动zk集群, 停止spark所有服务，修改配置文件spark-env.sh，在该配置文件中删掉SPARK_MASTER_IP并添加如下配置
```
export SPARK_DAEMON_JAVA_OPTS="-Dspark.deploy.recoveryMode=ZOOKEEPER -Dspark.deploy.zookeeper.url=zk1,zk2,zk3 -Dspark.deploy.zookeeper.dir=/spark"
```
1. 在node1节点上修改slaves配置文件内容指定worker节点
2. 在node1上执行sbin/start-all.sh脚本，然后在node2上执行sbin/start-master.sh启动第二个Master

启动集群的时候,我们需要向zk要master
```
--master spark://node1:7077,node2:7077
```