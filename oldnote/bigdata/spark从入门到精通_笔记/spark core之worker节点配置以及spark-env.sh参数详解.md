---
title: spark core之worker节点配置以及spark-env.sh参数详解
categories: spark   
toc: true  
tag: [spark]
---

# worker节点配置

场景:如果在已有的spark集群中,你想要加入一台新的worker节点

如果你想将某台机器部署成Standalone集群架构中的worker节点,那么就必须在该机器上部署spark安装包,并修改配置文件如下:
```
#修改配置文件
cd spark/conf/
mv spark-env.sh.template spark-env.sh
vim spark-env.sh  //添加
export JAVA_HOME=/home/hadoop/app/jdk1.7.0_80
export SPARK_MASTER_IP=hdp-node-01            //配置master的机器
export SPARK_MASTER_PORT=7077
#######################################################
mv slaves.template slaves  
vim slaves      //添加worker的节点
hdp-node-01
hdp-node-02
#######################################################
// 注意要配置多个机器之间的ssh免密码登录

```



# spark-env.sh参数详解

```

SPARK_MASTER_IP					#指定master进程所在的机器的ip地址
SPARK_MASTER_PORT				#指定master监听的端口号（默认是7077）
SPARK_MASTER_WEBUI_PORT			#指定master web ui的端口号（默认是8080）

#其实使用spark-env.sh配置的参数和我们手动启动master时指定的参数是一样的,sbin/start-master.sh --port 7078，类似这种方式，貌似可以指定一样的配置属性
我明确告诉大家，这个作用的确是一模一样的

#你可以在spark-evn.sh中就去配置好,但是有时呢，可能你会遇到需要临时更改配置，并启动master或worker进程的情况
#此时就比较适合，用sbin/start-master.sh这种脚本的命令行参数，来设置这种配置属性
#但是通常来说呢，还是推荐在部署的时候，通过spark-env.sh来设定
脚本命令行参数通常用于临时的情况

SPARK_MASTER_OPTS				#设置master的额外参数，使用"-Dx=y"设置各个参数(x对应的是参数名,y对应的是参数的值)
比如说export SPARK_MASTER_OPTS="-Dspark.deploy.defaultCores=1"

参数名											默认值						含义
spark.deploy.retainedApplications				200							在spark web ui上最多显示多少个application的信息
spark.deploy.retainedDrivers					200							在spark web ui上最多显示多少个driver的信息
spark.deploy.spreadOut							true						资源调度策略，spreadOut会尽量将application的executor进程分布在更多worker上，适合基于hdfs文件计算的情况，提升数据本地化概率；非spreadOut会尽量将executor分配到一个worker上，适合计算密集型的作业
spark.deploy.defaultCores						无限大						每个spark作业最多在standalone集群中使用多少个cpu core，默认是无限大，有多少用多少
spark.deploy.timeout							60							单位秒，一个worker多少时间没有响应之后，master认为worker挂掉了

------------------------------------------------------------


SPARK_LOCAL_DIRS				spark的工作目录，包括了shuffle map输出文件，以及持久化到磁盘的RDD等

SPARK_WORKER_PORT				worker节点的端口号，默认是随机的
SPARK_WORKER_WEBUI_PORT			worker节点的web ui端口号，默认是8081
SPARK_WORKER_CORES				worker节点上，允许spark作业使用的最大cpu数量，默认是机器上所有的cpu core
SPARK_WORKER_MEMORY				worker节点上，允许spark作业使用的最大内存量，格式为1000m，2g等，默认最小是1g内存

就是说，有些master和worker的配置，可以在spark-env.sh中部署时即配置，但是也可以在start-slave.sh脚本启动进程时命令行参数设置
但是命令行参数的优先级比较高，会覆盖掉spark-env.sh中的配置
比如说，上一讲我们的实验，worker的内存默认是1g，但是我们通过--memory 500m，是可以覆盖掉这个属性的

SPARK_WORKER_INSTANCES			当前机器上的worker进程数量，默认是1，可以设置成多个，但是这时一定要设置SPARK_WORKER_CORES，限制每个worker的cpu数量
SPARK_WORKER_DIR				spark作业的工作目录，包括了作业的日志等，默认是spark_home/work
SPARK_WORKER_OPTS				worker的额外参数，使用"-Dx=y"设置各个参数

参数名											默认值						含义
spark.worker.cleanup.enabled					false						是否启动自动清理worker工作目录，默认是false
spark.worker.cleanup.interval					1800						单位秒，自动清理的时间间隔，默认是30分钟
spark.worker.cleanup.appDataTtl					7 * 24 * 3600				默认将一个spark作业的文件在worker工作目录保留多少时间，默认是7天
-----------------------------------------------------------------

SPARK_DAEMON_MEMORY				分配给master和worker进程自己本身的内存，默认是1g
SPARK_DAEMON_JAVA_OPTS			设置master和worker自己的jvm参数，使用"-Dx=y"设置各个参数
SPARK_PUBLISC_DNS				master和worker的公共dns域名，默认是没有的

这里提示一下，大家可以观察一下，咱们的内存使用情况
在没有启动spark集群之前，我们的内存使用是1个多g，启动了spark集群之后，就一下子耗费到2个多g
每次又执行一个作业时，可能会耗费到3个多g左右

所以大家就明白了，为什么之前用分布式的集群，每个worker节点才1个g内存，根本是没有办法使用standalone模式和yarn模式运行作业的


```



下面是给大家列出spark所有的启动和关闭shell脚本
```
sbin/start-all.sh				根据配置，在集群中各个节点上，启动一个master进程和多个worker进程
sbin/stop-all.sh				在集群中停止所有master和worker进程
sbin/start-master.sh			在本地启动一个master进程
sbin/stop-master.sh				关闭master进程
sbin/start-slaves.sh			根据conf/slaves文件中配置的worker节点，启动所有的worker进程
sbin/stop-slaves.sh				关闭所有worker进程
sbin/start-slave.sh				在本地启动一个worker进程

```