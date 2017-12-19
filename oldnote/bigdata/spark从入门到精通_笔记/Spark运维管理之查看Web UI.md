---
title: Spark运维管理之查看Web UI进行作业监控
categories: spark  
tags: [spark]
---


对于Spark作业的监控，Spark给我们提供了很多种方式：Spark Web UI，Spark History Web UI，RESTFUL API以及Metrics。

<!--more-->


# Spark Web UI

每提交一个Spark作业，并且启动SparkContext之后，都会启动一个对应的Spark Web UI服务。默认情况下Spark Web UI的访问地址是driver进程所在节点的4040端口。在Spark Web UI上会展示作业相关的详细信息，非常有用，是Spark作业监控的最主要的手段。

Spark Web UI包括了以下信息：

1、stage和task列表
2、RDD大小以及内存使用的概览
3、环境信息
4、作业对应的executor的信息

可以通过在浏览器中访问http://<driver-node>:4040地址，来进入Spark Web UI界面。如果多个driver在一个机器上运行，它们会自动绑定到不同的端口上。默认从4040端口开始，如果发现已经被绑定了，那么会选择4041、4042等端口，以此类推。

要注意的是，这些信息默认情况下仅仅在作业运行期间有效并且可以看到。一旦作业完毕，那么driver进程以及对应的web ui服务也会停止，我们就无法看到已经完成的作业的信息了。如果要在作业完成之后，也可以看到其Spark Web UI以及详细信息，那么就需要启用Spark的History Server。

监控实验

1、通过spark-shell以standalone模式执行一个wordcount作业，通过直接访问4040端口以及从8080端口(这是spark集群的web ui,然后从集群的web ui进入到作业的web ui)两种方式进入web ui。
```
spark-shell --master spark://192.168.0.103:7077


```

直接访问4040端口,查看的是spark作业的web ui

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_1.png)


通过spark集群的web ui查看

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_2.png)





2、在作业运行完毕之后，再尝试看看作业的Web UI。
在spark-shell中执行下面的wordcount
```
scala>val lines = sc.textFile("hdfs://192.168.0.103:9000/test/hello.txt")
scala>val words = lines.flatMap(_.split(" ")).map((_,1))
scala>val counts = words.reduceByKey(_+_)
scala>counts.collect

```
查看一个作业的所有的job

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_3.png)


查看一个job的详情
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_4.png)


查看一个stage的详情
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_5.png)

如果,我们进行了cache,那么可以看到Storage的详情

```
scala>val lines = sc.textFile("hdfs://192.168.0.103:9000/test/hello.txt")
scala>val words = lines.flatMap(_.split(" ")).map((_,1))
scala>val counts = words.reduceByKey(_+_)
scala>counts.cache
scala>counts.collect

```
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_6.png)


查看作业的executor
```
scala>val lines = sc.textFile("hdfs://192.168.0.103:9000/test/hello.txt")
scala>val words = lines.flatMap(_.split(" ")).map((_,1))
scala>val counts = words.reduceByKey(_+_)
scala>counts.foreach(println)

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_7.png)







3、通过spark-shell以yarn模式执行一个wordcount作业，并重复上述过程。

```
spark-shell --master yarn-client

scala>val lines = sc.textFile("hdfs://192.168.0.103:9000/test/hello.txt")
scala>val words = lines.flatMap(_.split(" ")).map((_,1))
scala>val counts = words.reduceByKey(_+_)
scala>counts.collect

```

那么此时的集群管理器的web ui是通过yarn来查看的
http://resourceManager_host:8088


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_8.png)


# standalone模式下查看历史作业的web ui

默认情况下，一个作业运行完成之后，就再也无法看到其web ui以及执行信息了，在生产环境中，这对调试以及故障定位有影响。

如果要在作业执行完之后，还能看到其web ui，那么必须将作业的spark.eventLog.enabled属性设置为true，这个属性会告诉spark去记录该作业的所有要在web ui上展示的事件以及信息。

如果spark记录下了一个作业生命周期内的所有事件，那么就会在该作业执行完成之后，我们进入其web ui时，自动用记录的数据
重新绘制作业的web ui。

有3个属性我们可以设置

1、spark.eventLog.enabled，必须设置为true
2、spark.eventLog.dir，默认是/tmp/spark-events，建议自己手动调整为其他目录，比如/usr/local/spark-event或是hdfs目录，必须手动创建
3、spark.eventLog.compress ，是否压缩数据，默认为false，建议可以开启压缩以减少磁盘空间占用

这些属性可以在提交一个作业的时候设置如果想要对所有作业都启用该机制，那么可以在spark-defaults.conf文件中配置这三个属性

实验

1、先看看之前的已经执行完成的作业，是否可以进入spark web ui界面

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_9.png)

2、关闭现有的master和worker进程
```
./sbin/stop-all.sh

```
3、修改spark-defaults.conf文件，配置上述三个属性，启用standalone模式下的作业历史信息记录，手动创建hdfs目录

```
#vim spark-defaults.conf

spark.eventLog.enabled	true
spark.eventLog.dir	hdfs://192.168.0.103:9000/spark-events
spark.eventLog.compress	true

#手动创建HDFS目录
hdfs dfs -mkdir /spark-events

```


4、重新启动spark集群
```
./sbin/start-all.sh
```

5、使用spark-shell提交一个作业，然后再次尝试进入spark web ui界面

```
spark-shell --master spark://192.168.0.103:7077

scala>val lines = sc.textFile("hdfs://192.168.0.103:9000/test/hello.txt")
scala>val words = lines.flatMap(_.split(" ")).map((_,1))
scala>val counts = words.reduceByKey(_+_)
scala>counts.collect

```

可以看到在HDFS的目录下有对应的文件生成,这些文件就是记录spark历史作业的文件

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_10.png)

注意：如果要让spark完成作业的事件记录，那么必须最后以sc.stop()结尾。


# 启动HistoryServer查看历史作业的web ui

1、停止集群
```
./sbin/stop-all.sh
```

2、配置spark-env.sh和spark-defaults.conf

```
#vim spark-defaults.conf

spark.eventLog.enabled  true
spark.eventLog.dir      hdfs://192.168.0.103:9000/spark-events
spark.eventLog.compress true

#vim spark-env.sh

export SPARK_HISTORY_OPTS="-Dspark.history.ui.port=18080 -Dspark.history.retainedApplications=50 -Dspark.history.fs.logDirectory=hdfs://192.168.0.103:9000/spark-events"

#history Server的端口是:18080
#retainedApplications保留50个的application的历史

```

注意:
* 务必预先创建好hdfs://192.168.0.103:9000/spark-events目录
* spark.eventLog.dir与spark.history.fs.logDirectory指向的必须是同一个目录,因为spark.eventLog.dir会指定作业事件记录在哪里，spark.history.fs.logDirectory会指定从哪个目录中去读取作业数据

3、重启集群
```
./sbin/start-all.sh

```

4、启动history server

```
./sbin/start-history-server.sh
```


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_11.png)


访问地址: 192.168.0.103:18080


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_12.png)



5、运行spark-shell，在standalone模式下和yarn模式下，分别执行一个作业

6、通过192.168.80.103:18080的HistoryServer UI可以看到所有运行后的作业信息

standalone模式下

```
spark-shell --master spark://192.168.0.103:7077

scala>val lines = sc.textFile("hdfs://192.168.0.103:9000/test/hello.txt")
scala>val words = lines.flatMap(_.split(" ")).map((_,1))
scala>val counts = words.reduceByKey(_+_)
scala>counts.collect

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_13.png)

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_14.png)




yarn模式下

```
spark-shell --master yarn-client

scala>val lines = sc.textFile("hdfs://192.168.0.103:9000/test/hello.txt")
scala>val words = lines.flatMap(_.split(" ")).map((_,1))
scala>val counts = words.reduceByKey(_+_)
scala>counts.collect

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_15.png)

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/monitor_16.png)



