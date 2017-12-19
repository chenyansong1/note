---
title: namenode目录故障时解决方案
categories: hadoop
toc: true
tag: [hadoop]
---




如果我们人为的将namenode的目录删除,那么该如何处理呢?  
<!--more-->



正常情况下,mk一个目录
```
[root@hdp-node-01 dfs]# hadoop fs -mkdir /test
[root@hdp-node-01 dfs]# hadoop fs -ls /
Found 6 items
drwxr-xr-x   - root supergroup          0 2016-12-02 08:26 /hbase
drwxr-xr-x   - root supergroup          0 2016-11-28 10:20 /student
drwxr-xr-x   - root supergroup          0 2017-02-25 23:52 /test
drwx-wx-wx   - root supergroup          0 2016-11-28 10:01 /tmp
drwxr-xr-x   - root supergroup          0 2016-11-27 23:34 /user
drwxr-xr-x   - root supergroup          0 2016-12-17 21:13 /wordcount
```

namenode和SecondaryNameNode的目录确定
```
#namenode的目录
# cat /home/hadoop/app/hadoop-2.6.4/etc/hadoop/hdfs-site.xml    
<property>
	<name>dfs.namenode.name.dir</name>
	<value>/home/hadoop/data/name</value>
</property>



#SecondaryNameNode
<property>
     <name>dfs.namenode.checkpoint.dir</name>        #如果没有指定，默认是：file://${hadoop.tmp.dir}/dfs/namesecondary
     <value>/home/hadoop/data/namesecondary</value>
</property>

#如果没有配置,就去看core-site.xml
#cat ./etc/hadoop/core-site.xml


<property>
        <name>hadoop.tmp.dir</name>        #存放hdfs的数据目录，这是一个基础的目录，如果会和namenode和datanode以及secondarynamenode的目录相关
        <value>/home/hadoop/app/hadoop-2.6.4/hadoopdata</value>
</property>


#此文中我们没有指定SecondaryNameNode的目录,那么就是:${hadoop.tmp.dir}/dfs/namesecondary
[root@hdp-node-01 namesecondary]# pwd
/home/hadoop/app/hadoop-2.6.4/hadoopdata/dfs/namesecondary

```

只是将namenode的工作目录删除
```
[root@hdp-node-01 data]# pwd
/home/hadoop/data
[root@hdp-node-01 data]# ll
total 12
drwxr-xr-x 2 root root 4096 Dec 23 23:55 click-data
drwx------ 3 root root 4096 Feb 25 23:44 data
drwxr-xr-x 3 root root 4096 Feb 25 23:44 name

[root@hdp-node-01 data]# rm -rf ./name/
[root@hdp-node-01 data]# ll
total 8
drwxr-xr-x 2 root root 4096 Dec 23 23:55 click-data
drwx------ 3 root root 4096 Feb 25 23:44 data

```


 还能正常工作，如下图：因为读取的是内存的数据

```

[root@hdp-node-01 data]# hadoop fs -ls /
Found 6 items
drwxr-xr-x   - root supergroup          0 2016-12-02 08:26 /hbase
drwxr-xr-x   - root supergroup          0 2016-11-28 10:20 /student
drwxr-xr-x   - root supergroup          0 2017-02-25 23:52 /test
drwx-wx-wx   - root supergroup          0 2016-11-28 10:01 /tmp
drwxr-xr-x   - root supergroup          0 2016-11-27 23:34 /user
drwxr-xr-x   - root supergroup          0 2016-12-17 21:13 /wordcount
[root@hdp-node-01 data]# 

```



kill掉namenode进程后,重新启动它,发现起不来了
```
[root@hdp-node-01 data]# jps
1117 NameNode
1220 DataNode
1350 SecondaryNameNode
1774 Jps
[root@hdp-node-01 data]# kill -9 1117
[root@hdp-node-01 data]# jps
1220 DataNode
1350 SecondaryNameNode
1786 Jps
[root@hdp-node-01 data]# hadoop-daemon.sh start namenode
starting namenode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-namenode-hdp-node-01.out
[root@hdp-node-01 data]# jps
1220 DataNode
1350 SecondaryNameNode
1861 Jps

#执行命令
[root@hdp-node-01 data]# hadoop fs -ls /
ls: Call From hdp-node-01/192.168.0.11 to hdp-node-01:9000 failed on connection exception: java.net.ConnectException: Connection refused; For more details see:  http://wiki.apache.org/hadoop/ConnectionRefused

```

cp拷贝恢复：
```
[root@hdp-node-01 dfs]# pwd
/home/hadoop/app/hadoop-2.6.4/hadoopdata/dfs

[root@hdp-node-01 dfs]# cp -r ./namesecondary/ /home/hadoop/data/name
[root@hdp-node-01 dfs]# ll /home/hadoop/data/name/
total 8
drwxr-xr-x 2 root root 4096 Feb 26 00:13 current
-rw-r--r-- 1 root root   16 Feb 26 00:13 in_use.lock
[root@hdp-node-01 dfs]# 
```


```
[root@hdp-node-01 dfs]# hadoop-daemon.sh start namenode
starting namenode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-namenode-hdp-node-01.out
[root@hdp-node-01 dfs]# jps
1927 NameNode
1220 DataNode
1995 Jps
1350 SecondaryNameNode
[root@hdp-node-01 dfs]# 

```

查看数据是否完整
```
#执行命令
[root@hdp-node-01 dfs]# hadoop fs -ls /
Found 3 items
-rw-r--r--   3 root supergroup        258 2016-11-18 19:11 /hosts
-rw-r--r--   3 root supergroup       2025 2016-11-19 23:00 /profile
drwxr-xr-x   - root supergroup          0 2016-11-19 23:01 /test

#其实有数据的丢失,通过比较上面的ls /的目录就知道
```


这里的恢复只能是恢复到secondarynamenode 中存在的镜像位置，如果对于没有从edits中合并到secondarynamenode 的部分是无法恢复的

为了避免namenode数据损坏，将namenode的工作目录放在多个磁盘上，如下配置


```
<property>
	<name>dfs.namenode.name.dir</name>
	<value>/home/hadoop/data/name1,/home/hadoop/data/name2</value>
</property>

```



