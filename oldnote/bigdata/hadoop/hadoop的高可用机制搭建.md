---
title: hadoop的高可用机制搭建
categories: hadoop   
toc: true  
tag: [hadoop]
---

[TOC]




# 1.前期准备
1. 修改Linux主机名
2. 修改IP
3. 修改主机名和IP的映射关系 /etc/hosts （注意：如果你们公司是租用的服务器或是使用的云主机（如华为用主机、阿里云主机等））
 /etc/hosts里面要配置的是内网IP地址和主机名的映射关系
4. 关闭防火墙
5. ssh免登陆
6. 安装JDK，配置环境变量等

<!--more-->


# 2.集群规划
## 2.1.分布示意图

![](E:/git-workspace/note/img/bigdata/hadoop/hadoop_ha.png)

 

 


## 2.2.规划表

 主机名|  IP 			|安装的软件 			|运行的进程
:-------:|:--------------:|:---------------------:|:------------------:
 mini1	|192.168.1.200	| jdk、hadoop 			|	NameNode、DFSZKFailoverController(zkfc)
 mini2 |192.168.1.201 	|jdk、hadoop     		|	NameNode、DFSZKFailoverController(zkfc)
 mini3	|192.168.1.202	| jdk、hadoop    		|	 ResourceManager
 mini4	|192.168.1.203	| jdk、hadoop    		|	 ResourceManager
 mini5	|192.168.1.205	| jdk、hadoop、zookeeper|	DataNode、NodeManager、JournalNode、QuorumPeerMain（zk）
mini6 	|192.168.1.206 	| jdk、hadoop、zookeeper|	 DataNode、NodeManager、JournalNode、QuorumPeerMain
mini7	|192.168.1.207	| jdk、hadoop、zookeeper|	 DataNode、NodeManager、JournalNode、QuorumPeerMain

说明：

1. 在hadoop2.0中通常由两个NameNode组成，一个处于active状态，另一个处于standby状态。Active NameNode对外提供服务，而Standby NameNode则不对外提供服务，仅同步active namenode的状态，以便能够在它失败时快速进行切换。hadoop2.0官方提供了两种HDFS HA的解决方案，一种是NFS，另一种是QJM。这里我们使用简单的QJM。在该方案中，主备NameNode之间通过一组JournalNode同步元数据信息，一条数据只要成功写入多数JournalNode即认为写入成功。通常配置奇数个JournalNode
2. 这里还配置了一个zookeeper集群，用于ZKFC（DFSZKFailoverController）故障转移，当Active NameNode挂掉了，会自动切换Standby NameNode为standby状态
3. hadoop-2.2.0中依然存在一个问题，就是ResourceManager只有一个，存在单点故障，hadoop-2.6.4解决了这个问题，有两个ResourceManager，一个是Active，一个是Standby，状态由zookeeper进行协调



# 3.安装步骤
## 3.1.安装配置zooekeeper集群（在mini5上）
 1.1解压
```
 tar -zxvf zookeeper-3.4.5.tar.gz -C /home/hadoop/app/

```
 1.2修改配置
```
   cd /home/hadoop/app/zookeeper-3.4.5/conf/
   cp zoo_sample.cfg zoo.cfg
   vim zoo.cfg
   #修改：dataDir=/home/hadoop/app/zookeeper-3.4.5/tmp
   #在最后添加：
   server.1=hadoop05:2888:3888
   server.2=hadoop06:2888:3888
   server.3=hadoop07:2888:3888
   #保存退出
```

 1.3.创建一个tmp文件夹，创建myid文件
```
   mkdir /home/hadoop/app/zookeeper-3.4.5/tmp
   echo 1 > /home/hadoop/app/zookeeper-3.4.5/tmp/myid

```
 1.4.将配置好的zookeeper拷贝到其他节点(首先分别在mini6、mini7根目录下创建一个hadoop目录：mkdir /hadoop)
```
   scp -r /home/hadoop/app/zookeeper-3.4.5/ mini6:/home/hadoop/app/
   scp -r /home/hadoop/app/zookeeper-3.4.5/ mini7:/home/hadoop/app/
```


 1.5.修改mini6、mini7对应/hadoop/zookeeper-3.4.5/tmp/myid内容
```
   #mini6：
    echo 2 > /home/hadoop/app/zookeeper-3.4.5/tmp/myid
   #mini7：
    echo 3 > /home/hadoop/app/zookeeper-3.4.5/tmp/myid
```


## 3.2.安装配置hadoop集群（在mini1上操作）
### 3.2.1解压
```
   tar -zxvf hadoop-2.6.4.tar.gz -C /home/hadoop/app/
```

### 3.2.2配置HDFS
&emsp;hadoop2.0所有的配置文件都在$HADOOP_HOME/etc/hadoop目录下
  cd /home/hadoop/app/hadoop-2.6.4/etc/hadoop
```
   #将hadoop添加到环境变量中
   vim /etc/profile
   export JAVA_HOME=/usr/java/jdk1.7.0_55
   export HADOOP_HOME=/hadoop/hadoop-2.6.4
   export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin
```

### 3.2.3.修改hadoo-env.sh
```
    export JAVA_HOME=/home/hadoop/app/jdk1.7.0_55

```

### 3.2.4.修改core-site.xml
```
###############################################################################
<configuration>
<!-- 指定hdfs的nameservice为ns1 -->
<property>
<name>fs.defaultFS</name>
<value>hdfs://bi/</value>  #这是两个namenode的逻辑名称
</property>
<!-- 指定hadoop临时目录 -->
<property>
<name>hadoop.tmp.dir</name>
<value>/home/hadoop/app/hdpdata/</value>
</property>
 
<!-- 指定zookeeper主机所在地址 -->
<property>
<name>ha.zookeeper.quorum</name>
<value>mini5:2181,mini6:2181,mini7:2181</value>
</property>
</configuration>
 
###############################################################################

```

### 3.2.5.修改hdfs-site.xml
```
###############################################################################
 
<configuration>
<!--指定hdfs的nameservice为bi，需要和core-site.xml中的保持一致 -->
<property>
<name>dfs.nameservices</name> 
<value>bi</value>  #配置的namenode的名称空间，可以有多个，多个就是联邦机制
</property>
<!-- bi下面有两个NameNode，分别是nn1，nn2 -->
<property>
<name>dfs.ha.namenodes.bi</name>
<value>nn1,nn2</value>  #指定只是指定名称，下面会指定物理地址 
</property>
 
<!-- nn1的RPC通信地址 -->
<property>
<name>dfs.namenode.rpc-address.bi.nn1</name> #物理地址
<value>mini1:9000</value>
</property>
<!-- nn1的http通信地址 -->
<property>
<name>dfs.namenode.http-address.bi.nn1</name>
<value>mini1:50070</value>
</property>
<!-- nn2的RPC通信地址 -->
<property>
<name>dfs.namenode.rpc-address.bi.nn2</name>
<value>mini2:9000</value>
</property>
<!-- nn2的http通信地址 -->
<property>
<name>dfs.namenode.http-address.bi.nn2</name>
<value>mini2:50070</value>
</property>
 
 
<!-- 指定NameNode的edits元数据在JournalNode上的存放位置 -->
<property>
<name>dfs.namenode.shared.edits.dir</name>
<value>qjournal://mini5:8485;mini6:8485;mini7:8485/bi</value>
</property>
<!-- 指定JournalNode在本地磁盘存放数据的位置 -->
<property>
<name>dfs.journalnode.edits.dir</name>
<value>/home/hadoop/journaldata</value>
</property>
 
 
<!-- 开启NameNode失败自动切换 -->
<property>
<name>dfs.ha.automatic-failover.enabled</name>
<value>true</value>
</property>
<!-- 配置失败自动切换实现方式 -->
<property>
<name>dfs.client.failover.proxy.provider.bi</name>
<value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
</property>
 
 
<!-- 配置隔离机制方法：一个是通过ssh发送kill命令，另一个是调用用户自己的shell脚本，多个机制用换行分割，即每个机制暂用一行-->
<property>
<name>dfs.ha.fencing.methods</name>
<value>
sshfence
shell(/bin/true)
</value>
</property>
 
 
<!-- 使用sshfence隔离机制时需要ssh免登陆 -->
<property>
<name>dfs.ha.fencing.ssh.private-key-files</name>
<value>/home/hadoop/.ssh/id_rsa</value>
</property>
 
<!-- 配置sshfence隔离机制超时时间 -->
<property>
<name>dfs.ha.fencing.ssh.connect-timeout</name>
<value>30000</value>
</property>
 
</configuration>
 
###############################################################################

```



### 3.2.6.修改mapred-site.xml
```
###############################################################################
 
<configuration>
<!-- 指定mr框架为yarn方式 -->
<property>
<name>mapreduce.framework.name</name>
<value>yarn</value>
</property>
</configuration>
 
###############################################################################
```


### 3.2.7.修改yarn-site.xml

```
###############################################################################
 
<configuration>
<!-- 开启RM高可用 -->
<property>
<name>yarn.resourcemanager.ha.enabled</name>
<value>true</value>
</property>
 
<!-- 指定RM的cluster id -->
<property>
<name>yarn.resourcemanager.cluster-id</name>
<value>yrc</value>
</property>
 
<!-- 指定RM的名字 -->
<property>
<name>yarn.resourcemanager.ha.rm-ids</name>
<value>rm1,rm2</value>
</property>
 
<!-- 分别指定RM的地址 -->
<property>
<name>yarn.resourcemanager.hostname.rm1</name>
<value>mini3</value>
</property>
<property>
<name>yarn.resourcemanager.hostname.rm2</name>
<value>mini4</value>
</property>
 
 
<!-- 指定zk集群地址（yarn的高可用，也是交给zk） -->
<property>
<name>yarn.resourcemanager.zk-address</name>
<value>mini5:2181,mini6:2181,mini7:2181</value>
</property>
<property>
<name>yarn.nodemanager.aux-services</name>
<value>mapreduce_shuffle</value>
</property>
</configuration>
 
###############################################################################
```

### 3.2.8.修改slaves
slaves是指定子节点的位置，因为要在mini1上启动HDFS、在mini3启动yarn，所以mini1上的slaves文件指定的是datanode的位置，mini3上的slaves文件指定的是nodemanager的位置)
mini5
mini6
mini7

### 3.2.9.配置免密码登陆
```
    #首先要配置hadoop00到hadoop01、hadoop02、hadoop03、hadoop04、hadoop05、hadoop06、hadoop07的免密码登陆
    #在hadoop01上生产一对钥匙
    ssh-keygen -t rsa
    #将公钥拷贝到其他节点，包括自己
    ssh-coyp-id hadoop00
    ssh-coyp-id hadoop01
    ssh-coyp-id hadoop02
    ssh-coyp-id hadoop03
    ssh-coyp-id hadoop04
    ssh-coyp-id hadoop05
    ssh-coyp-id hadoop06
    ssh-coyp-id hadoop07
    #配置hadoop02到hadoop04、hadoop05、hadoop06、hadoop07的免密码登陆
    #在hadoop02上生产一对钥匙
    ssh-keygen -t rsa
    #将公钥拷贝到其他节点
    ssh-coyp-id hadoop03   
    ssh-coyp-id hadoop04
    ssh-coyp-id hadoop05
    ssh-coyp-id hadoop06
    ssh-coyp-id hadoop07
    #注意：两个namenode之间要配置ssh免密码登陆，别忘了配置hadoop01到hadoop00的免登陆
    在hadoop01上生产一对钥匙
    ssh-keygen -t rsa
    ssh-coyp-id -i hadoop00 

```


### 3.2.10.将配置好的hadoop拷贝到其他节点
```
  scp -r /hadoop/ hadoop02:/
   scp -r /hadoop/ hadoop03:/
   scp -r /hadoop/hadoop-2.6.4/ hadoop@hadoop04:/hadoop/
   scp -r /hadoop/hadoop-2.6.4/ hadoop@hadoop05:/hadoop/
   scp -r /hadoop/hadoop-2.6.4/ hadoop@hadoop06:/hadoop/
   scp -r /hadoop/hadoop-2.6.4/ hadoop@hadoop07:/hadoop/

```

# 4.启动
## 4.1.启动zookeeper集群
 分别在mini5、mini6、mini7上启动zk
```
   cd /hadoop/zookeeper-3.4.5/bin/
   ./zkServer.sh start
   #查看状态：一个leader，两个follower
   ./zkServer.sh status

```

## 4.2.启动journalnode
 分别在在mini5、mini6、mini7上执行
```
   cd /hadoop/hadoop-2.6.4
   sbin/hadoop-daemon.sh start journalnode
   #运行jps命令检验，hadoop05、hadoop06、hadoop07上多了JournalNode进程
```

## 4.3.格式化HDFS
```
   #在mini1上执行命令:
   hdfs namenode -format
   #格式化后会在根据core-site.xml中的hadoop.tmp.dir配置生成个文件，这里我配置的是/hadoop/hadoop-2.6.4/tmp，然后将/hadoop/hadoop-2.6.4/tmp拷贝到hadoop02的/hadoop/hadoop-2.6.4/下。因为两个namenode要完全一致
   scp -r tmp/ hadoop02:/home/hadoop/app/hadoop-2.6.4/
   ##也可以这样，建议hdfs namenode -bootstrapStandby

```

## 4.4.格式化ZKFC
 在mini1上执行一次即可
```
   hdfs zkfc -formatZK
```

## 4.5.启动HDFS
 在mini1上执行
```
   sbin/start-dfs.sh
```

## 4.6.启动YARN
```
###注意#####：是在mini2上执行start-yarn.sh，把namenode和resourcemanager分开是因为性能问题，因为他们都要占用大量资源，所以把他们分开了，他们分开了就要分别在不同的机器上启动)
   sbin/start-yarn.sh #启动yarn的机器到其他的机器也是要配置免密登录


#在另外一台resource manager的机器上启动yarn
   yarn-daemon.sh start resourcemanager 

```



# 5.测试
```
 
#统计浏览器访问:
  http://mini1:50070
  NameNode 'mini1:9000' (active)
  http://mini2:50070
  NameNode 'mini2:9000' (standby)
 
#验证HDFS HA
#1.首先向hdfs上传一个文件
  hadoop fs -put /etc/profile /profile
  hadoop fs -ls /
 
  #2.然后再kill掉active的NameNode
  kill -9 <pid of NN>
  
#3.通过浏览器访问：http://192.168.1.202:50070
  NameNode 'hadoop02:9000' (active)
  
#4.这个时候hadoop02上的NameNode变成了active， 在执行命令：
  hadoop fs -ls /
  -rw-r--r--   3 root supergroup       1926 2014-02-06 15:36 /profile

#5. 刚才上传的文件依然存在！！！  手动启动那个挂掉的NameNode
  sbin/hadoop-daemon.sh start namenode

#6. 通过浏览器访问：http://192.168.1.201:50070
  NameNode 'hadoop01:9000' (standby)
 
#7.验证YARN：
  ##运行一下hadoop提供的demo中的WordCount程序：
  hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.4.1.jar wordcount /profile /out
 
OK，大功告成！！！

```



# 6.测试集群工作状态的一些指令
```
bin/hdfs dfsadmin -report        #查看hdfs的各节点状态信息
 
 
bin/hdfs haadmin -getServiceState nn1   #获取一个namenode节点的HA状态
 
sbin/hadoop-daemon.sh start namenode  #单独启动一个namenode进程
 
 
./hadoop-daemon.sh start zkfc   #单独启动一个zkfc进程
```


