---
title: hbase分布式安装
categories: hbase   
toc: true  
tag: [hbase]
---




# 1.安装前准备
* 安装Hadoop和zookeeper
* 启动Hadoop的hdfs和yarn
* 启动zookeeper的zkServer


# 2.机器规划表


| | | | | | | | | |
|:--:|:----:|:----:|:-----:|:-----:|:-----:|:-----:|:-----:|
|hdp-node-01	|	NameNode	|	dataNode	|resourceManger|secondaryNameNode|zookeeper|Hmaster		| -|
|hdp-node-02	|	-			|	dataNode	|-				|-				|zookeeper	|-			|regionserver|
|hdp-node-03 |	-			|	dataNode	|-				|-				|zookeeper	|-			|		regionserver|



# 3.安装hbase
```
#解压，并创建软链接
tar -zxvf hbase-0.99.2-bin.tar.gz 
ln -s hbase-0.99.2/ hbase

#删除软件包，因为占用空间
rm -rf hbase-0.99.2-bin.tar.gz


#添加环境变量
vim /etc/profile

 
#HBASE_HOME
export HBASE_HOME=/home/hadoop/app/hbase
export PATH=$PATH:$HBASE_HOME/bin

#使生效
source /etc/profile


# vim hbase-env.sh
export JAVA_HOME=/home/hadoop/app/jdk1.7.0_80
export HBASE_MANAGES_ZK=false                                //表示使用外部的zookeeper，而不是使用hbase自带的zookeeper



# vim hbase-site.xml
#####################################################################
<configuration>
<property>
<name>hbase.master</name>         #hbasemaster的主机和端口
<value>hdp-node-01:60000</value>
</property>
<property>
<name>hbase.master.maxclockskew</name>        #时间同步允许的时间差
<value>180000</value>
</property>
<property>
<name>hbase.rootdir</name>
<value>hdfs://hdp-node-01:9000/hbase</value>        #hdfs目录路径
</property>
<property>
<name>hbase.cluster.distributed</name>        #是否分布式运行
<value>true</value>
</property>
<property>
<name>hbase.zookeeper.quorum</name>
<value>hdp-node-01,hdp-node-02,hdp-node-03</value>
</property>
<property>
<name>hbase.zookeeper.property.dataDir</name>        #zookeeper地址
<value>/home/hadoop/hbase/tmp/zookeeper</value>
</property>
</configuration>

#####################################################################


# vim  ./conf/regionservers
hdp-node-01
hdp-node-02
hdp-node-03


#需要将hbase的目录拷贝到另外两台机器上
scp -r /home/hadoop/app/hbase hdp-node-02:/home/hadoop/app/
scp -r /home/hadoop/app/hbase hdp-node-03:/home/hadoop/app/



#启动hbase（在一个机器启动即可）
start-hbase.sh 


stop-hbase.sh    #停止


```

# 4.测试
进入命令行

```
hbase shell
```
页面监控

http://master:16010/






