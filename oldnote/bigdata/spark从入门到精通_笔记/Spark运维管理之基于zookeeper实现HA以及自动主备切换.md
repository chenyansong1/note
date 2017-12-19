---
title: Spark运维管理之基于zookeeper实现HA以及自动主备切换
categories: spark  
tags: [spark]
---

默认情况下，standalone cluster manager对于worker节点的失败是具有容错性的（迄今为止，Spark自身而言对于丢失部分计算工作是有容错性的，它会将丢失的计算工作迁移到其他worker节点上执行）。然而，调度器是依托于master进程来做出调度决策的，这就会造成单点故障：如果master挂掉了，就没法提交新的应用程序了。为了解决这个问题，spark提供了两种高可用性方案，分别是基于zookeeper的HA方案以及基于文件系统的HA方案。


<!--more-->

基于zookeeper的HA方案

# 概述

使用zookeeper来提供leader选举以及一些状态存储，你可以在集群中启动多个master进程，让它们连接到zookeeper实例。其中一个master进程会被选举为leader，其他的master会被指定为standby模式。如果当前的leader master进程挂掉了，其他的standby master会被选举，从而恢复旧master的状态。并且恢复作业调度。整个恢复过程（从leader master挂掉开始计算）大概会花费1~2分钟。要注意的是，这只会推迟调度新的应用程序，master挂掉之前就运行的应用程序是不被影响的。

# 配置

如果要启用这个恢复模式，需要在spark-env.sh文件中，设置SPARK_DAEMON_JAVA_OPTS选项：
```
spark.deploy.recoveryMode		设置为ZOOKEEPER来启用standby master恢复模式（默认为NONE）
spark.deploy.zookeeper.url		zookeeper集群url（举例来说，192.168.0.103:2181,192.168.0.104:2181）
spark.deploy.zookeeper.dir		zookeeper中用来存储恢复状态的目录（默认是/spark）
```
备注：如果在集群中启动了多个master节点，但是没有正确配置master去使用zookeeper，master在挂掉进行恢复时是会失败的，
因为没法发现其他master，并且都会认为自己是leader。这会导致集群的状态不是健康的，因为所有master都会自顾自地去调度。

# 细节

在启动一个zookeeper集群之后，启用高可用性是很直接的。简单地在多个节点上启动多个master进程，并且给它们相同的zookeeper配置（zookeeper url和目录）。master就可以被动态加入master集群，并可以在任何时间被移除掉。

为了调度新的应用程序或者向集群中添加worker节点，它们需要知道当前leader master的ip地址。这可以通过传递一个master列表来完成。举例来说，我们可以将我们的SparkContext连接的地址指向spark://host1:port1,host2:port2。这就会导致你的SparkContext尝试去注册所有的master，如果host1挂掉了，那么配置还是正确的，因为会找到新的leader master，也就是host2。

对于注册一个master和普通的操作，这是一个重要的区别。当一个应用程序启动的时候，或者worker需要被找到并且注册到当前的leader master的时候。一旦它成功注册了，就被保存在zookeeper中了。如果故障发生了，new leader master会去联系所有的之前注册过的应用程序和worker，并且通知它们master的改变。这样的话，它们甚至在启动的时候都不需要知道new master的存在。

正是由于这个属性，new master可以在任何时间被创建，并且我们唯一需要担心的一件事情就是新的应用程序和worker可以找到并且注册到master。一旦注册上去之后，我们就不用担心它了。


# 实验


1、将192.168.0.103机器上的spark集群先停止
```
./sbin/stop-all.sh
```
2、修改机器上的spark-env.sh文件，在其中加入上述三个属性
```
export SPARK_DAEMON_JAVA_OPTS="-Dspark.deploy.recoveryMode=ZOOKEEPER -Dspark.deploy.zookeeper.url=192.168.0.103:2181,192.168.0.104:2181 -Dspark.deploy.zookeeper.dir=/spark"
```

3、启动集群,这样就启动了一个master和一个worker
在192.168.0.103上直接用启动集群：
```
./sbin/start-all.sh
```

此时去zookeeper中看是否有个"/spark"目录生成
```
#连接zk
zkCli.sh
#执行ls命令
ls /
```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_ha.png)



4、在192.168.0.104上部署spark安装包，并启动一个master进程

4.1.安装scala 2.11.4
```
1、将课程提供的scala-2.11.4.tgz使用WinSCP拷贝到/usr/local/src目录下。
2、对scala-2.11.4.tgz进行解压缩：tar -zxvf scala-2.11.4.tgz
3、对scala目录进行重命名：mv scala-2.11.4 scala
4、配置scala相关的环境变量
vi ~/.bashrc
export SCALA_HOME=/usr/local/scala
export PATH=$SCALA_HOME/bin
source ~/.bashrc
```
查看scala是否安装成功：scala -version

4.2.安装spark客户端

1、将spark-1.5.1-bin-hadoop2.4.tgz使用WinSCP上传到/usr/local/src目录下。
2、解压缩spark包：tar -zxvf spark-1.5.1-bin-hadoop2.4.tgz。
3、重命名spark目录：mv spark-1.5.1-bin-hadoop2.4 spark
4、修改spark环境变量
```
vi ~/.bashrc
export SPARK_HOME=/usr/local/spark
export PATH=$SPARK_HOME/bin
export CLASSPATH=.:$CLASSPATH:$JAVA_HOME/lib:$JAVA_HOME/jre/lib

source ~/.bashrc
```

修改spark-env.sh文件

1、cd /usr/local/spark/conf
2、cp spark-env.sh.template spark-env.sh
3、vi spark-env.sh
```
export JAVA_HOME=/usr/java/latest
export SCALA_HOME=/usr/local/scala
export HADOOP_HOME=/usr/local/hadoop
export HADOOP_CONF_DIR=/usr/local/hadoop/etc/hadoop
export SPARK_MASTER_IP=192.168.0.104
export SPARK_DAEMON_MEMORY=100m
export SPARK_DAEMON_JAVA_OPTS="-Dspark.deploy.recoveryMode=ZOOKEEPER -Dspark.deploy.zookeeper.url=192.168.0.103:2181,192.168.0.104:2181 -Dspark.deploy.zookeeper.dir=/spark"
```
4.3.在192.168.0.104上单独启动一个standby master进程：
```
./sbin/start-master.sh
```

查看启动的两个master的web UI情况


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_ha_2.png)

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_ha_3.png)




4、提交应用程序
在我们编写的程序中将master地址修改为192.168.0.103:7077,192.168.0.103:7078

这里使用spark-shell去模拟提交应用程序
```
spark-shell --master spark://192.168.0.103:7077,192.168.0.104:7077

scala>val lines = sc.textFile("hdfs://192.168.0.103:9000/test/hello.txt")
scala>val words = lines.flatMap(_.split(" ")).map((_,1))
scala>val counts = words.reduceByKey(_+_)
scala>counts.collect

```
此时在web ui上查看(alive master)
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_ha_4.png)

5、杀掉原先的leader master

```
#jps查看192.168.0.103机器上的master的pid
jps
#杀掉master进程
kill -9 xxxx
#再次jps,看是否杀掉
```
此时查看103的web ui,发现访问不了

此时查看104的web ui上查看(standby master)

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_ha_5.png)


5.等到standby master接管集群再次提交应用程序
这里使用spark-shell去模拟提交应用程序
```
spark-shell --master spark://192.168.0.103:7077,192.168.0.104:7077
//此时查看启动日志的时候,会看到去连接192.168.0.103:7077会报错,然后会再去连接192.168.0.104:7077

scala>val lines = sc.textFile("hdfs://192.168.0.103:9000/test/hello.txt")
scala>val words = lines.flatMap(_.split(" ")).map((_,1))
scala>val counts = words.reduceByKey(_+_)
scala>counts.collect

```

查看104的web ui

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_ha_6.png)



6、再次手动启动原来的leader master（死掉）

在103的机器上启动master
```
./sbin/start-master.sh
```
查看103的web ui

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_ha_7.png)






