---
title: storm集群任务提交流程
categories: storm   
toc: true  
tag: [storm]
---


# 1.启动集群
程序员启动集群，如启动nimbus、supervisor、ui等
```
//客户端运行storm nimbus 时，会调用storm的Python脚本，该脚本中为每个命令编写一个方法，每个方法都可以生成一条相应的Java命令， 命令格式如下：java -server  xxx.ClasName  -args 
#如启动nimbus：./bin/storm nimbus     调用的是下面的方法
 nimbus    ------> Running: /export/servers/jdk/bin/java    -server  backtype.storm.daemon.nimbus    
#启动supervisor：./bin/storm supervisor  调用的是下面的方法    
 supervisor ----->Running: /export/servers/jdk/bin/java    -server  backtype.storm.daemon.supervisor
```
<!--more-->

# 2.客户端提交任务
```
#命令格式：storm jar xxx.jar       xx驱动类   参数
bin/storm jar examples/storm-starter/storm-starter-topologies-0.9.6.jar storm.starter.WordCountTopology wordcount-28            #wordcount 是拓扑的名

#上诉命令实际上回执行下面的方法：
  Running: /export/servers/jdk/bin/java  -client   -Dstorm.jar=/export/servers/storm/examples/storm-starter/storm-starter-topologies-0.9.6.jar storm.starter.WordCountTopology    wordcount-28
# 该命令会执行：storm-starter-topologies-0.9.6.jar 中 storm.starter.WordCountTopology 的main方法，在main方法中会执行以下代码：
```

 执行上述命令会带来下面的操作

1.上传jar包到/storm/workdir/nimbus/inbox目录下，并且改名，改名规则是添加了一个UUID字符串
2.调用jar包中指定驱动类的main方法，在main方法中调用了topologyBuilder.createTopology()，这个方法将程序员编写的<font color=red>spout对象和bolt对象进行序列化（查看createTopology()方法源码知）</font>，
 生成一个任务目录，里面包含三个文件
```
[root@hdp-node-01 export]# tree /export/data/storm/workdir/nimbus/
/export/data/storm/workdir/nimbus/
|-- inbox
|   -- stormjar-4588b45e-9868-4d8d-87bb-6534eb9da61d.jar
|-- stormdist
    |-- wordcount-1-1480906166
    |   |-- stormcode.ser        #序列化对象文件( spout对象和bolt对象)
    |   |-- stormconf.ser        #配置文件 
    |   |-- stormjar.jar        #jar包 (从nimbus/inbox里面挪过来的)
    |-- wordcount-8-1-1481088186
        |-- stormcode.ser
        |-- stormconf.ser
        |-- stormjar.jar

```
3.nimbus接收到任务之后，将其封装成assignment对象，保存在zookeeper的目录上：/storm/assignment  ，该目录只会保存正在运行的topology任务
```
[zk: localhost:2181(CONNECTED) 3] ls /storm/assignments
[wordcount-8-1-1481088186, wordcount-1-1480906166]
```


# 3.supervisor启动worker，创建task任务
## 3.1.启动worker
&emsp;supervisor通过watch机制，感知（watch会监听zk中节点的变化）到nimbus在zookeeper上的任务分配信息，从zookeeper上拉取任务信息，分辨出属于自己的任务
```
#zookeeper上的任务信息
       newAssignment=Assignment[
		masterCodeDir=C:\Users\MAOXIA~1\AppData\Local\Temp\\e73862a8-f7e7-41f3-883d-af494618bc9f\nimbus\stormdist\double11-1-1458909887,    #代码目录
		nodeHost={61ce10a7-1e78-4c47-9fb3-c21f43a331ba=192.168.1.106},        #主机
		taskStartTimeSecs={1=1458909910, 2=1458909910, 3=1458909910, 4=1458909910, 5=1458909910, 6=1458909910, 7=1458909910, 8=1458909910},
		workers=[                                         #workers
					ResourceWorkerSlot[    
						hostname=192.168.1.106,        #主机
						memSize=0,
						cpu=0,
						tasks=[1, 2, 3, 4, 5, 6, 7, 8],        #在一个worker中要启动的tasks
						jvm=<null>,
						nodeId=61ce10a7-1e78-4c47-9fb3-c21f43a331ba,
						port=6900                #绑定的端口
					]
				],
		timeStamp=1458909910633,type=Assign
	]

```

 supervisor 根据自己的任务信息，启动自己的worker，并发配一个端口，如下：
```
'export/servers/jdk/bin/java'   -server   -Xmx768m   export/data/storm/workdir/supervisor/stormdist/wordcount1-3-1461683066/stormjar.jar   'backtype.storm.daemon.worker'   'wordcount1-3-1461683066'   'abdfsdf-3083-4d55-b51f-e389b066f90b'   '6701'   'sfsdf39fd3-7d2b-4e40-aabc-1c88c9848d74'
```


## 3.2.创建task任务
&emsp; worker启动之后，连接zk，拉取任务
```
#根据任务中的配置信息，去启动task任务，如下是一个work的配置信息
ResourceWorkerSlot[    
	hostname=192.168.1.106,        #主机
	memSize=0,
	cpu=0,
	tasks=[1, 2, 3, 4, 5, 6, 7, 8],        #在一个worker中要启动的tasks
	jvm=<null>,
	nodeId=61ce10a7-1e78-4c47-9fb3-c21f43a331ba,
	port=6900                #绑定的端口
]

/*
在supervisor所在的机器上，/export/data/storm/workdir/supervisor/stormdist目录下，有当前正在运行的topology的jar包和配置文件、序列化对象文件（都是从nimbus机器上下载下来的），然后supervisor用上面的jar和从zk中拿到的配置信息去启动task任务
*/
#假设任务信息：
任务序号1-------->spout---------type:spout
任务序号2--------->bolt----------type:bolt
任务序号3---------->acker-------type:bolt

worker根据任务类型，分别执行spout任务或者bolt任务，worker通过反序列化，得到程序员自己定义的spout和bolt对象，然后就可以调用spout任务或者bolt任务的生命周期方法：
 spout的生命周期是：open、nextTuple、outputField
 bolt的生命周期是：prepare、execute（Tuple） 、 outputField

```



# 4.总结
1、集群如何启动，任务如何执行？
```
	java -server nimubs，supervisor
	client--->createTopology(序列化)--->提交jar到nimbuinbox--->nimbus分配任务(task总数/worker数)---写到zk。
										启动worker<------识别自己的任务<----supervisor---->watch----zk
										                启动Spout/Bolt----TaskInfo<------worker---->zk

```
# 5.图解任务提交流程简图

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_liucheng/1.png)
 
# 6.图解任务提交流程详细解说

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_liucheng/2.png)

# 7.Storm组件本地目录树

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_liucheng/3.png)

# 8.Storm zookeeper目录树

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_liucheng/4.png)
 
