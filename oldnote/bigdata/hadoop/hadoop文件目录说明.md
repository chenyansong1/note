---
title: hadoop文件目录说明
categories: hadoop
toc: true
tag: [hadoop]
---


# 1.主文件列表
```
[root@hdp-node-01 hadoop]# cd app/hadoop-2.6.4/
[root@hdp-node-01 hadoop-2.6.4]# ll
total 68
drwxrwxr-x 2 hadoop hadoop  4096 Mar  8  2016 bin
drwxrwxr-x 3 hadoop hadoop  4096 Mar  8  2016 etc
drwxrwxr-x 2 hadoop hadoop  4096 Mar  8  2016 include
drwxrwxr-x 3 hadoop hadoop  4096 Mar  8  2016 lib
drwxrwxr-x 2 hadoop hadoop  4096 Mar  8  2016 libexec
-rw-r--r-- 1 hadoop hadoop 15429 Mar  8  2016 LICENSE.txt
drwxr-xr-x 3 root   root    4096 Nov 18 12:09 logs
-rw-r--r-- 1 hadoop hadoop   101 Mar  8  2016 NOTICE.txt
-rw-r--r-- 1 hadoop hadoop  1366 Mar  8  2016 README.txt
-rw-r--r-- 1 root   root    2025 Nov 18 02:55 root@hdp-node-02
-rw-r--r-- 1 root   root    2025 Nov 18 02:55 root@hdp-node-03
-rw-r--r-- 1 root   root    2025 Nov 18 02:55 root@hdp-node-04
drwxrwxr-x 2 hadoop hadoop  4096 Mar  8  2016 sbin
drwxrwxr-x 4 hadoop hadoop  4096 Mar  8  2016 share
[root@hdp-node-01 hadoop-2.6.4]#
 

```

<!--more-->

# 2.include目录
是c语言本地库的头文件
# 3.lib
是本地库
# 4.share
是存放的jar包（common、hdfs、yarn等jar），和doc文档
# 5.logs
是日志文件，如果出现错误，看日志，很重要
```

#启动hdfs的时候，会有下面的打印信息
[root@hdp-node-01 current]# start-dfs.sh
Starting namenodes on [hdp-node-01]
hdp-node-01: starting namenode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-namenode-hdp-node-01.out
#hdp-node-01: starting namenode 在hdp-node-01上启动了namenode
hdp-node-01: starting datanode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-datanode-hdp-node-01.out
hdp-node-03: starting datanode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-datanode-hdp-node-03.out
hdp-node-02: starting datanode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-datanode-hdp-node-02.out

#到hdp-node-01机器下，执行：可以看打印的日志，一般的报错信息会显示在下面的日志文件中
tail /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-namenode-hdp-node-01.log


```


# 6.sbin目录
```
'是一些启动停止脚本'
-rwxr-xr-x 1 hadoop hadoop 6452 Mar  8  2016 hadoop-daemon.sh
-rwxr-xr-x 1 hadoop hadoop 1360 Mar  8  2016 hadoop-daemons.sh

-rwxr-xr-x 1 hadoop hadoop 1471 Mar  8  2016 start-all.sh
-rwxr-xr-x 1 hadoop hadoop 1462 Mar  8  2016 stop-all.sh

-rwxr-xr-x 1 hadoop hadoop 3206 Mar  8  2016 stop-dfs.sh
-rwxr-xr-x 1 hadoop hadoop 3705 Mar  8  2016 start-dfs.sh

-rwxr-xr-x 1 hadoop hadoop 1347 Mar  8  2016 start-yarn.sh
-rwxr-xr-x 1 hadoop hadoop 1340 Mar  8  2016 stop-yarn.sh

-rwxr-xr-x 1 hadoop hadoop 4295 Mar  8  2016 yarn-daemon.sh
-rwxr-xr-x 1 hadoop hadoop 1353 Mar  8  2016 yarn-daemons.sh

#可以使用hadoop-daemon来启动
[root@hdp-node-01 sbin]# hadoop-daemon.sh
Usage: hadoop-daemon.sh [--config <conf-dir>] [--hosts hostlistfile] [--script script] (start|stop) <hadoop-command> <args...>

[root@hdp-node-01 sbin]# hadoop-daemon.sh start datanode
starting datanode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-datanode-hdp-node-01.out
[root@hdp-node-01 sbin]# jps
1082 Jps
1052 DataNode

[root@hdp-node-01 sbin]# hadoop-daemon.sh start namenode
starting namenode, logging to /home/hadoop/app/hadoop-2.6.4/logs/hadoop-root-namenode-hdp-node-01.out
[root@hdp-node-01 sbin]# jps
1052 DataNode
1205 Jps
1137 NameNode
[root@hdp-node-01 sbin]#

/*
hadoop-daemon.sh start  datanode #在本地手动启动datanode
hadoop-daemon.sh start  namenode #在本地手动启动namenode
动态扩容：可以使用上面的方式手动启动增加的机器之后，再添加slave，这样就可以运行了，然后下一次启动的时候，会读slave，也是可以自动启动添加的机器

start-all.sh是将hdfs启动之后，然后启动yarn的，我们并不建议这样做
因为我们先启动hdfs看是否报错，然后启动yarn看，看是否报错，这样不容易出错
*/
```



# 7.配置文件
```
[root@hdp-node-01 hadoop-2.6.4]# tree /home/hadoop/app/hadoop-2.6.4/etc/ 
/home/hadoop/app/hadoop-2.6.4/etc/
-- hadoop
    |-- capacity-scheduler.xml
    |-- configuration.xsl
    |-- container-executor.cfg
    |-- 'core-site.xml'
    |-- 'hadoop-env.sh'
    |-- hadoop-policy.xml
    |-- 'hdfs-site.xml'
    |-- httpfs-env.sh
    |-- httpfs-log4j.properties
    |-- httpfs-signature.secret
    |-- httpfs-site.xml
    |-- kms-log4j.properties
    |-- kms-site.xml
    |-- log4j.properties
    |-- mapred-env.cmd
    |-- mapred-env.sh
    |-- mapred-queues.xml.template
    |-- mapred-site.xml
    |--'slaves'      #这里配置就是为了自动化启动脚本(start-dfs.sh、start-yarn.sh)要用，如果不是自动化启动，我们也可以手动启动，那么就不必要使用该配置文件
    |-- ssl-client.xml.example
    |-- ssl-server.xml.example
    |-- yarn-env.sh
    -- 'yarn-site.xml'
 
1 directory, 29 files
```




# 8.基础目录
在core-site.xml配置文件中配置如下：
```
<configuration>
        <property>
                <name>hadoop.tmp.dir</name>
                <value>/home/hadoop/app/hadoop-2.6.4/hadoopdata</value>
        </property>
</configuration>

/*
namenode、datanode、secondary namenode的目录默认是在基础目录的前提下配置的，如：
dfs.namenode.name.dir                file://${hadoop.tmp.dir}/dfs/name

dfs.datanode.data.dir                     file://${hadoop.tmp.dir}/dfs/data
 
dfs.namenode.checkpoint.dir=file://${hadoop.tmp.dir}/dfs/namesecondary      #以上两个参数做checkpoint操作时，secondary namenode的本地工作目录
dfs.namenode.checkpoint.edits.dir=${dfs.namenode.checkpoint.dir}

*/
```

# 9.namenode、datanode的目录
 在hdfs-site.xml配置文件中配置如下：

```
<configuration>
        <property>
                <name>dfs.namenode.name.dir</name>
                <value>/home/hadoop/data/name</value>
        </property>
        <property>
                <name>dfs.datanode.data.dir</name>
                <value>/home/hadoop/data/data</value>
        </property>
        <property>
                <name>dfs.replication</name>
                <value>3</value>
        </property>
</configuration>

```

查看

```
[root@hdp-node-01 hadoop]# tree /home/hadoop/data/
/home/hadoop/data/
|-- data                                                               # 'datanode对应的目录'
|   |-- current                                    
|   |   |-- BP-1263609123-192.168.0.11-1479467214259
|   |   |   |-- current
|   |   |   |   |-- VERSION
|   |   |   |   |-- finalized
|   |   |   |   |   `-- subdir0
|   |   |   |   |       `-- subdir0
|   |   |   |   |           |-- blk_1073741825
|   |   |   |   |           `-- blk_1073741825_1001.meta
|   |   |   |   `-- rbw
|   |   |   |-- dncp_block_verification.log.curr
|   |   |   |-- dncp_block_verification.log.prev
|   |   |   `-- tmp
|   |   `-- VERSION
|   `-- in_use.lock
`-- name                                                #   'namenode对应的目录'
    |-- current
    |   |-- VERSION
    |   |-- edits_0000000000000000001-0000000000000000002
    |   |-- edits_0000000000000000003-0000000000000000010
    |   |-- edits_0000000000000000011-0000000000000000012
    |   |-- edits_0000000000000000013-0000000000000000014
    |   |-- edits_0000000000000000015-0000000000000000016
    |   |-- edits_0000000000000000017-0000000000000000018
    |   |-- edits_0000000000000000019-0000000000000000020
    |   |-- edits_inprogress_0000000000000000021
    |   |-- fsimage_0000000000000000018
    |   |-- fsimage_0000000000000000018.md5
    |   |-- fsimage_0000000000000000020
    |   |-- fsimage_0000000000000000020.md5
    |   `-- seen_txid
    `-- in_use.lock
 
#上面是既有namenode又有datanode的机器，例如如果是只有datanode的机器，那么将只会有data目录
```


# 11.secondary namenode的目录
在hdfs-site.xml配置文件中配置如下：

```
#如果没有配置，默认如下：
dfs.namenode.checkpoint.dir=file://${hadoop.tmp.dir}/dfs/namesecondary         
 
```

在core-site.xml中${hadoop.tmp.dir}配置如下：
```
<configuration>
        <property>
                <name>hadoop.tmp.dir</name>
                <value>/home/hadoop/app/hadoop-2.6.4/hadoopdata</value>
        </property>
</configuration>
```
所以namesecondary的目录结构为：
```
[root@hdp-node-01 hadoop-2.6.4]# tree /home/hadoop/app/hadoop-2.6.4/hadoopdata/
/home/hadoop/app/hadoop-2.6.4/hadoopdata/
|-- dfs
|   `-- namesecondary
|       |-- current
|       |   |-- VERSION
|       |   |-- edits_0000000000000000001-0000000000000000002
|       |   |-- edits_0000000000000000003-0000000000000000010
|       |   |-- edits_0000000000000000011-0000000000000000012
|       |   |-- edits_0000000000000000013-0000000000000000014
|       |   |-- edits_0000000000000000015-0000000000000000016
|       |   |-- edits_0000000000000000017-0000000000000000018
|       |   |-- edits_0000000000000000019-0000000000000000020
|       |   |-- fsimage_0000000000000000018
|       |   |-- fsimage_0000000000000000018.md5
|       |   |-- fsimage_0000000000000000020
|       |   `-- fsimage_0000000000000000020.md5
|       `-- in_use.lock
`-- nm-local-dir
    |-- filecache
    |-- nmPrivate
    `-- usercache
 
7 directories, 13 files
```

# 12.edits（日志文件）的目录
在hdfs-site.xml配置文件中配置如下：
```
#如果没有配置，默认如下：
dfs.namenode.checkpoint.edits.dir=${dfs.namenode.checkpoint.dir}
```
