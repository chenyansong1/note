---
title: Hadoop集群搭建
categories: hadoop
toc: true
tag: [hadoop]
---



[TOC]



# 1.hdfs和yarn的作用

1. HDFS就是负责管理文件的，其中data node 就是数据节点存储数据的，而name node就是管理data node的
2. yarn是负责跑mapreduce，调度硬件资源给执行的程序用的，node manager 负责调用mapreduce程序，而resource manager是负责管理node manager的




![](E:/git-workspace/note/img/bigdata/hadoop/install/install_hadoop.png)



# 2.主机规划

节点|说明
:-----:|:-------:
hdp-node-01|NameNode  、ResourceManager
hdp-node-02| 	DataNode  、NodeManager
hdp-node-03|DataNode  、NodeManager
hdp-node-04|DataNode  、NodeManager


 # 3.准备工作
## 3.1.添加HADOOP用户，设置密码
```
useradd hadoop
passwd hadoop

```


## 3.2.为HADOOP用户分配sudoer权限
```
#vim    /etc/sudoers

root    ALL=(ALL)       ALL
//添加
hadoop  ALL=(ALL)       ALL

```
## 3.3.时间同步
```
#d.time sync
/usr/sbin/ntpdate time.nist.gov
echo '#time sync by oldboy at 2010-2-1' >>/var/spool/cron/root
echo '*/5 * * * * /usr/sbin/ntpdate time.nist.gov >/dev/null 2>&1' >>/var/spool/cron/root
crontab -l
 
```


## 3.4.设置主机名
```
#vim /etc/sysconfig/network
 
NETWORKING=yes
HOSTNAME=hdp-node-01

'对应的机器设置为hdp-node-01、hdp-node-02、hdp-node-03、hdp-node-04'
```

## 3.5.配置内网域名映射（hosts）
```
#vim /etc/hosts

192.168.0.11    hdp-node-01
192.168.0.22    hdp-node-02
192.168.0.33    hdp-node-03
192.168.0.44    hdp-node-04

```

## 3.6.配置防火墙
```
/etc/init.d/iptables stop
/etc/init.d/iptables status        #查看状态
```

## 3.7.配置ssh免密登陆
```
ssh-keygen -t rsa            #一直回车
ssh-copy-id -i .ssh/id_rsa.pub root@hdp-node-04        #另外的机器上也要拷贝：hdp-node-01（本机）、hdp-node-02、hdp-node-03、hdp-node-04

```


## 3.8.安装JDK
```
#1.解压jdk

mkdir /home/hadoop/app -p            #jdk解压目录
tar -zxvf jdk-7u80-linux-x64.tar.gz -C /home/hadoop/app/            #解压到指定文件


#2.修改配置/etc/profile，添加

JAVA_HOME=/home/hadoop/app/jdk1.7.0_80
CLASSPATH=.:$JAVA_HOME/lib/tools.jar
PATH=$JAVA_HOME/bin:$PATH
export JAVA_HOME CLASSPATH PATH

#3.使配置生效
source /etc/profile

#4.检查
[root@hdp-node-01 app]# java -version
java version "1.7.0_80"
Java(TM) SE Runtime Environment (build 1.7.0_80-b15)
Java HotSpot(TM) 64-Bit Server VM (build 24.80-b11, mixed mode)
[root@hdp-node-01 app]#
 
```


# 4.Hadoop安装
## 4.1.解压

```
上传：cenos-6.5-hadoop-2.6.4.tar.gz        #这是一个经过编译的文件

tar -zxvf cenos-6.5-hadoop-2.6.4.tar.gz -C    /home/hadoop/app/        #解压到指定的目录

```

## 4.2.修改配置文件
所有的配置文件在官网可以看到：

Configuration

* core-default.xml
* hdfs-default.xml
* mapred-default.xml
* yarn-default.xml
* slaves
* Deprecated Properties



>cd /home/hadoop/app/hadoop-2.6.4/

```

#vim ./etc/hadoop/hadoop-env.sh

export JAVA_HOME=/home/hadoop/app/jdk1.7.0_80


#vim ./etc/hadoop/core-site.xml

<configuration>
        <property>
                <name>fs.defaultFS</name>        #指定HADOOP所使用的文件系统schema（URI），HDFS的老大（NameNode）的地址
                <value>hdfs://hdp-node-01:9000</value>
        </property>
        <property>
                <name>hadoop.tmp.dir</name>        #存放hdfs的数据目录，这是一个基础的目录，如果会和namenode和datanode以及secondarynamenode的目录相关
                <value>/home/hadoop/app/hadoop-2.6.4/hadoopdata</value>
        </property>
</configuration>


#vim ./etc/hadoop/hdfs-site.xml

<configuration>
        <property>
                <name>dfs.namenode.name.dir</name>    #name node 的镜像文件存储目录，如果没有指定，默认是：file://${hadoop.tmp.dir}/dfs/name
                <value>/home/hadoop/data/name</value>
        </property>
        <property>
                <name>dfs.datanode.data.dir</name>        #如果没有指定，默认是： file://${hadoop.tmp.dir}/dfs/data 
                <value>/home/hadoop/data/data</value>
        </property>
       <property>
                <name>dfs.namenode.checkpoint.dir</name>        #如果没有指定，默认是：file://${hadoop.tmp.dir}/dfs/namesecondary
                <value>/home/hadoop/data/namesecondary</value>
        </property>
        <property>
                <name>dfs.replication</name>        #副本数量
                <value>3</value>
        </property>
</configuration>


#mv ./etc/hadoop/mapred-site.xml.template ./etc/hadoop/mapred-site.xml
vim ./etc/hadoop/mapred-site.xml

<configuration>
        <property>
                <name>mapreduce.framework.name</name>        #程序是要放在一个指定的资源平台上去跑，这里指定yarn，默认是local
                <value>yarn</value>
        </property>
</configuration>


#vim ./etc/hadoop/yarn-site.xml

<configuration>
         <property>
                <name>yarn.resourcemanager.hostname</name>        #配置yarn的resourcemanager存放的主机
               <value>hdp-node-01</value>
        </property>
        <property>
                <name>yarn.nodemanager.aux-services</name>
                <value>mapreduce_shuffle</value>
        </property>
</configuration>


'vim ./etc/hadoop/slaves '
hdp-node-01
hdp-node-02
hdp-node-03
hdp-node-04
```

# 5.格式化namenode

这里只是初始化了namenode的工作目录（/home/hadoop/data/name），而datanode的工作目录是在datanode启动后自动初始化的
注意：namenode的初始化，只需要一次就够了
```
'格式化namenode（是对namenode进行初始化，相当于生成账本）'
  hdfs namenode -format                # (过时的一个：hadoop namenode -format)

#出现下面的语句表示成功
Storage directory /home/hadoop/data/name has been successfully formatted.

```

# 6.先启动HDFS
```
#sbin/start-dfs.sh

[root@hdp-node-01 hadoop-2.6.4]# ./sbin/start-dfs.sh
Starting namenodes on [hdp-node-01]
hdp-node-01: starting namenode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-namenode-hdp-node-01.out
hdp-node-01: starting datanode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-datanode-hdp-node-01.out
hdp-node-03: starting datanode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-datanode-hdp-node-03.out
hdp-node-02: starting datanode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-datanode-hdp-node-02.out
hdp-node-04: starting datanode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-datanode-hdp-node-04.out

'结果：'
/*
[root@hdp-node-01 hadoop-2.6.4]# jps
1561 DataNode
1901 Jps
1474 NameNode
1682 SecondaryNameNode

[root@hdp-node-02 ~]# jps
1293 Jps
1263 DataNode

[root@hdp-node-03 ~]# jps
1237 Jps
1207 DataNode

[root@hdp-node-04 ~]# jps
1223 Jps
1188 DataNode

*/
```

# 7.再启动YARN
```
#sbin/start-yarn.sh

[root@hdp-node-01 hadoop-2.6.4]# sbin/start-yarn.sh
starting yarn daemons
starting resourcemanager, logging to /home/hadoop/app/hadoop-2.6.4/logs/yarn-root-resourcemanager-hdp-node-01.out
hdp-node-01: starting nodemanager, logging to /home/hadoop/app/hadoop-2.6.4/logs/yarn-root-nodemanager-hdp-node-01.out
hdp-node-03: starting nodemanager, logging to /home/hadoop/app/hadoop-2.6.4/logs/yarn-root-nodemanager-hdp-node-03.out
hdp-node-04: starting nodemanager, logging to /home/hadoop/app/hadoop-2.6.4/logs/yarn-root-nodemanager-hdp-node-04.out
hdp-node-02: starting nodemanager, logging to /home/hadoop/app/hadoop-2.6.4/logs/yarn-root-nodemanager-hdp-node-02.out


'结果：'
/*
[root@hdp-node-01 hadoop-2.6.4]# jps
1561 DataNode
1474 NameNode
1682 SecondaryNameNode
27803 ResourceManager
2039 NodeManager
2158 Jps

[root@hdp-node-02 ~]# jps
1473 Jps
1263 DataNode
1376 NodeManager


[root@hdp-node-03 ~]# jps
1320 NodeManager
1207 DataNode
1416 Jps
 

[root@hdp-node-04 ~]# jps
1188 DataNode
1397 Jps
1301 NodeManager
*/
```

# 8.测试
```
'向hdfs中put一个文件'
hadoop fs -put /etc/profile /  

#查看
[root@hdp-node-02 ~]# hadoop fs -ls /
Found 1 items
-rw-r--r--   3 root supergroup       2025 2016-11-18 02:59 /profile

```

http://192.168.0.11:50070


![](E:/git-workspace/note/img/bigdata/hadoop/install/install_test.png)



# 9.停止

```
[root@hdp-node-01 hadoop-2.6.4]# ./sbin/stop-yarn.sh
stopping yarn daemons
no resourcemanager to stop
hdp-node-01: no nodemanager to stop
hdp-node-03: no nodemanager to stop
hdp-node-04: no nodemanager to stop
hdp-node-02: no nodemanager to stop
no proxyserver to stop


[root@hdp-node-01 hadoop-2.6.4]# ./sbin/stop-dfs.sh
Stopping namenodes on [hdp-node-01]
hdp-node-01: stopping namenode
hdp-node-01: stopping datanode
hdp-node-02: stopping datanode
hdp-node-03: stopping datanode
hdp-node-04: stopping datanode
Stopping secondary namenodes [0.0.0.0]
0.0.0.0: stopping secondarynamenode


[root@hdp-node-01 hadoop-2.6.4]# jps
2927 Jps

```
