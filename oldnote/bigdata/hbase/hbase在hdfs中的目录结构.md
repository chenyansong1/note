hbase在hdfs中的目录结构


# 根目录结构
```
chenyansong@hadoop01199:/usr/local/hadoop/bin$ ./hdfs dfs -ls /hbase/

drwxr-xr-x   - aphadoop supergroup          0 2016-03-30 11:39 /hbase/.hbase-snapshot
drwxr-xr-x   - aphadoop supergroup          0 2017-06-20 10:13 /hbase/.tmp
drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 14:03 /hbase/MasterProcWALs
drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 10:01 /hbase/WALs	#预写日志目录
drwxr-xr-x   - aphadoop supergroup          0 2015-12-04 17:52 /hbase/archive
drwxr-xr-x   - aphadoop supergroup          0 2015-10-13 16:02 /hbase/corrupt
drwxr-xr-x   - aphadoop supergroup          0 2015-09-25 16:41 /hbase/data  #数据目录(表数据存放在此)
-rw-r--r--   3 aphadoop supergroup         42 2015-09-25 15:13 /hbase/hbase.id
-rw-r--r--   3 aphadoop supergroup          7 2015-09-25 15:13 /hbase/hbase.version
drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 14:45 /hbase/oldWALs
```

# 预写日志目录结构

对于每个HRegionserServer，在WALS目录中都包含一个对应的子目录，在每个子目录中有多个HLog文件（因为日志滚动），一个region服务器的所有region共享同一组HLog文件

```
chenyansong@hadoop01199:/usr/local/hadoop/bin$ ./hdfs dfs -ls /hbase/WALs

drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 14:33 /hbase/WALs/hadoop01194,26020,1497921445748
drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 14:36 /hbase/WALs/hadoop01195,26020,1497921511503
drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 14:20 /hbase/WALs/hadoop01196,26020,1497921639356
drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 14:00 /hbase/WALs/hadoop01197,26020,1497877203762
drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 14:44 /hbase/WALs/hadoop01199,26020,1497924062163
drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 14:45 /hbase/WALs/hadoop01200,26020,1497880406053
drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 14:20 /hbase/WALs/hadoop01201,26020,1497916984491
drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 13:54 /hbase/WALs/hadoop01202,26020,1497921176026
drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 13:56 /hbase/WALs/hadoop01203,26020,1497876957380
drwxr-xr-x   - aphadoop supergroup          0 2017-06-21 14:42 /hbase/WALs/hadoop01205,26020,1497890498093

```

查看某一个HRegistorServer的HLog

```
chenyansong@hadoop01199:/usr/local/hadoop/bin$ ./hdfs dfs -ls /hbase/WALs/hadoop01197,26020,1497877203762

/hbase/WALs/hadoop01197,26020,1497877203762/hadoop01197%2C26020%2C1497877203762.default.1498021237288
/hbase/WALs/hadoop01197,26020,1497877203762/hadoop01197%2C26020%2C1497877203762.default.1498024837378

观察文件的后缀，可以看到文件的修改修改时间是相差一个小时的，
hadoop01197%2C26020%2C1497877203762.default.1498021237288
hadoop01197%2C26020%2C1497877203762.default.1498024837378
1498021237288 --->  2017/6/21 13:0:37
1498024837378 --->  2017/6/21 14:0:37
	  3600000 	

等待一个小时之后，日志文件被滚动（这个时间由：hbase.regionserver.logroll.period=3600000 单位是ms,默认是60分钟)，
由于日志文件被关闭，会产生一个新的日志文件，大小从0开始

当所有包含的修改都被持久化到存储文件中，从而不再需要日志文件时，他们会被放到HBase的根目录下的oldWALS目录中，
在 10分钟（hbase.master.logcleaner.ttl）后，旧的日志文件将被maser删除

```


# hbase.id和hbase.version

表示的是集群的唯一Id和文件格式版本信息
```
chenyansong@hadoop01199:/usr/local/hadoop/bin$ ./hdfs dfs -cat /hbase/hbase.id
PBUF
$c0a1b2d1-6d4d-4aee-8b69-8f0207606c4b

chenyansong@hadoop01199:/usr/local/hadoop/bin$ ./hdfs dfs -cat /hbase/hbase.version
PBUF
8
chenyansong@hadoop01199:/usr/local/hadoop/bin$ 

```



# 其他根目录结构

随着时间的推移，会有更多的根级别的目录结构出现，如splitlog和corrupt文件分别 用来存储日志拆分过程中产生的中间拆分文件和损坏的日志


# 表级别文件

在hbase的根目录下有一个data目录，用来存放表的数据，该目录下有**命名空间**,这里默认是有2个命名空间的

```
[root@hdp-node-02 bin]# ./hdfs dfs -ls /hbase/data
drwxr-xr-x   - root supergroup          0 2017-06-20 19:59 /hbase/data/default
drwxr-xr-x   - root supergroup          0 2017-06-20 10:34 /hbase/data/hbase
```

hbase命名空间下，存放的是2张表

```
chenyansong@hadoop01199:/usr/local/hadoop/bin$ ./hdfs dfs -ls /hbase/data/hbase
drwxr-xr-x   - aphadoop supergroup          0 2015-09-25 15:13 /hbase/data/hbase/meta
drwxr-xr-x   - aphadoop supergroup          0 2015-09-25 16:40 /hbase/data/hbase/namespace

#meta表
chenyansong@hadoop01199:/usr/local/hadoop/bin$ ./hdfs dfs -ls /hbase/data/hbase/meta
drwxr-xr-x   - aphadoop supergroup          0 2015-09-25 15:13 /hbase/data/hbase/meta/.tabledesc
drwxr-xr-x   - aphadoop supergroup          0 2015-09-25 15:13 /hbase/data/hbase/meta/.tmp
drwxr-xr-x   - aphadoop supergroup          0 2017-06-20 09:20 /hbase/data/hbase/meta/1588230740

#namespace表
chenyansong@hadoop01199:/usr/local/hadoop/bin$ ./hdfs dfs -ls /hbase/data/hbase/namespace
drwxr-xr-x   - aphadoop supergroup          0 2015-09-25 16:40 /hbase/data/hbase/namespace/.tabledesc
drwxr-xr-x   - aphadoop supergroup          0 2015-09-25 16:40 /hbase/data/hbase/namespace/.tmp
drwxr-xr-x   - aphadoop supergroup          0 2015-10-13 14:18 /hbase/data/hbase/namespace/4212f91ee366c461631ae7a3d1f03363

```

在default目录下存放的是我们通过shell或者是API创建的表

```
#default命名空间下的所有的表
[root@hdp-node-02 bin]# ./hdfs dfs -ls /hbase/data/default
drwxr-xr-x   - root supergroup          0 2017-06-20 17:54 /hbase/data/default/tb1
drwxr-xr-x   - root supergroup          0 2017-06-20 17:54 /hbase/data/default/tb2
drwxr-xr-x   - root supergroup          0 2017-06-20 19:59 /hbase/data/default/test
drwxr-xr-x   - root supergroup          0 2017-06-20 16:16 /hbase/data/default/user
```


我们可以看其中一个表的目录结构,如：user
```
#查看user表的结构
[root@hdp-node-02 bin]# ./hdfs dfs -ls /hbase/data/default/user
drwxr-xr-x   - root supergroup          0 2017-06-20 16:16 /hbase/data/default/user/.tabledesc
drwxr-xr-x   - root supergroup          0 2017-06-20 16:16 /hbase/data/default/user/.tmp
drwxr-xr-x   - root supergroup          0 2017-06-20 17:24 /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326

/*
.tabledesc目录中对应的是序列化后的HTableDescriptor,其中包括表和列族的定义
.tmp目录包含一些临时数据
*/

#在user表下有列族（info1和info2目录）
[root@hdp-node-02 bin]# ./hdfs dfs -ls /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326
-rw-r--r--   3 root supergroup         39 2017-06-20 16:16 /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326/.regioninfo
drwxr-xr-x   - root supergroup          0 2017-06-20 17:18 /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326/info1
drwxr-xr-x   - root supergroup          0 2017-06-20 16:16 /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326/info2
drwxr-xr-x   - root supergroup          0 2017-06-20 17:52 /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326/recovered.edits

#查看一个列族的数据（不能cat，因为不是文本文件）
[root@hdp-node-02 bin]# ./hdfs dfs -ls /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326/info1
-rw-r--r--   3 root supergroup       5078 2017-06-20 17:18 /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326/info1/571951c978634e4ba9d6b8fd14fc9143
```

# region级文件

在tablename目录下有多个随机数生成的region名字，region文件的总体结构是：
特定的region下面是对应表的列族

```
/<hbase-root-dir>/data/<tablename>/<encoded-regionname>/<column-family>/<filename>
```

```
[root@hdp-node-01 bin]# ./hdfs dfs -ls /hbase/data/default/user
drwxr-xr-x   - root supergroup          0 2017-06-20 16:16 /hbase/data/default/user/.tabledesc
drwxr-xr-x   - root supergroup          0 2017-06-20 16:16 /hbase/data/default/user/.tmp
drwxr-xr-x   - root supergroup          0 2017-06-20 17:24 /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326

#查看具体的region对应的内容（该region下，会有表的列族）
[root@hdp-node-01 bin]# ./hdfs dfs -ls /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326
-rw-r--r--   3 root supergroup         39 2017-06-20 16:16 /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326/.regioninfo
drwxr-xr-x   - root supergroup          0 2017-06-20 17:18 /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326/info1
drwxr-xr-x   - root supergroup          0 2017-06-20 16:16 /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326/info2
drwxr-xr-x   - root supergroup          0 2017-06-20 17:52 /hbase/data/default/user/40404b875d9d5c1d9f885de946a27326/recovered.edits

```




