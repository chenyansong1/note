---
title: spark内核架构
categories: spark   
toc: true  
tag: [spark]
---



![](/assert/img/bigdata/spark内核解密/spark内核解密.png)


<!--more-->

Driver部分的代码:Sparkconf+SparkContext (其实就是new SparkContext的过程)

接下来创建RDD (会在Executor中执行的代码,会在worker上的Executor上处理的逻辑)

worker管理当前node的资源,并接受Master的指令来分配具体的计算资源Executor(在新的进程中分配)


Worker不会向Master汇报自身的资源情况,发送心跳的时候只是发送workID


Job:一般每个action会触发一个作业,

spark的快不是基于内存,


依赖:有一个情况是range级别的依赖,不会因为数据规模的增大,而改变依赖的个数(即父rdd的个数)


stage内部计算逻辑,完全一样,只是计算的数据不同罢了




启动spark:sbin/start-all.sh
启动历史命令,这样在程序运行完成之后依然可以看到log:sbin/start-history-server.sh
jps	---->HistoryServer
UI------->http://master:18080


内核架构图:

![](/assert/img/bigdata/spark内核解密/spark内核架构.png)