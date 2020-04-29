---
title: Spark运维管理之基于文件系统实现HA高可用以及手动主备切换
categories: spark  
tags: [spark]
---

# 概述

zookeeper是实现生产级别的高可用性的最佳方式，但是如果你就是想要在master进程挂掉的时候，手动去重启它，而不是依靠zookeeper实现自动主备切换，那么可以使用FILESYSTEM模式。当应用程序和worker都注册到master之后，**master就会将它们的信息写入指定的文件系统目录中**，以便于当master重启的时候可以从文件系统中恢复注册的应用程序和worker状态。

<!--more-->

# 配置

要启用这种恢复模式，需要在spark-env.sh中设置SPARK_DAEMON_JAVA_OPTS
```
spark.deploy.recoveryMode		设置为FILESYSTEM来启用单点恢复（默认值为NONE）
spark.deploy.recoveryDirectory	spark在哪个文件系统目录内存储状态信息，必须是master可以访问的目录
```

# 细节

1、这个解决方案可以与进程监控或管理器（比如monit）结合使用，或者就仅仅是启用手动重启恢复机制即可。
2、文件系统恢复比不做任何恢复机制肯定是要好的，这个模式更加适合于开发和测试环境，而不是生产环境。此外，通过stop-master.sh脚本杀掉一个master进程是不会清理它的恢复状态的，所以当你重启一个新的master进程时，它会进入恢复模式。这会增加你的恢复时间至少1分钟，因为它需要等待之前所有已经注册的worker等节点先timeout。
3、这种方式没有得到官方的支持，也可以使用一个NFS目录作为恢复目录。如果原先的master节点完全死掉了，你可以在其他节点上启动一个master进程，它会正确地恢复之前所有注册的worker和应用程序。之后的应用程序可以找到新的master，然后注册。

# 实验
1、关闭两台机器上的master和worker
2、修改192.168.0.103机器上的spark-env.sh
export SPARK_DAEMON_JAVA_OPTS="-Dspark.deploy.recoveryMode=FILESYSTEM -Dspark.deploy.recoveryDirectory=/usr/local/spark_recovery"
3、在192.168.0.103上启动spark集群
这样会生成一个我们配置的目录


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_file.png)


4、在spark-shell中进行wordcount计数，到一半，有一个running application
这里使用spark-shell去模拟提交应用程序
```
spark-shell --master spark://192.168.0.103:7077

scala>val lines = sc.textFile("hdfs://192.168.0.103:9000/test/hello.txt")
scala>val words = lines.flatMap(_.split(" ")).map((_,1))
scala>val counts = words.reduceByKey(_+_)
scala>counts.collect

```
查看web ui

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_file_2.png)

在指定的目录下会生成我们提交应用的文件

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_file_3.png)



5、杀掉192.168.0.103上的master进程
```
./sbin/stop-master.sh
```

6、重启192.168.0.103上的master进程
```
./sbin/start-master.sh
```
因为我们配置的目录文件的存在,再次启动的时候,会根据文件重新运行原来的应用

我们观察一下重启时的日志

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_file_4.png)

再看一下配置的目录文件下application的文件是否存在

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_file_5.png)



7、观察web ui上，是否恢复了worker以及原先正在运行的application

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master_file_6.png)
