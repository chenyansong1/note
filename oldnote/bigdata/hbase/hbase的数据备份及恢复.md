# 使用distcp进行关机全备份

distcp(distributed copy分布式复制），是由Hadoop提供的一个用于在同一hdfs集群或者不同hdfs集群之间复制大型数据集，他使用mapReduce来进行复制文件，处理错误，并进行恢复，报告任务运行状态

Hbase的 所有文件（包括系统文件）存储在hdfs上，因此只要使用distcp将hbase目录复制到同一hdfs或其他很多粉丝少的另一个目录中，就可以完成对源hbase集群的备份工作

请注意，这是一种关机情况下的全备份方案，我们可以使用distcp工具来进行备份的原因是Hbase集群已被关闭（或者是所有表都已被禁用），因此在备份过程中不会有对文件的修改操作，不要在运行中的hbase集群上使用distcp，因此，这种解决方案适合那种允许对hbase集群进行周期性关闭的环境，例如：一个仅用于后端批处理业务而不对前端请求进行响应的集群

## 操作步骤

因为要使用hdfs的distcp命令，所以需要启动hdfs和yarn

1.关闭源hbase集群

```
[root@hdp-node-01 logs]# /bigdata_installed/hbase/bin/stop-hbase.sh 
stopping hbase..................


#关闭之后检查进程是否完全关闭
[root@hdp-node-01 logs]# jps
1610 NameNode
1865 SecondaryNameNode
1712 DataNode
5140 Jps
2046 QuorumPeerMain

```

2.创建备份的目标目录

这里的目标目录可以是一个新的集群或者是源集群中的另外的目录

```
[root@hdp-node-01 logs]# /bigdata_installed/hadoop/bin/hdfs dfs -mkdir /backup_hbase

```

3.使用distcp命令进行备份
```
/bigdata_installed/hadoop/bin/hadoop distcp /hbase /backup_hbase


#查看
[root@hdp-node-01 logs]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /backup_hbase/hbase
drwxr-xr-x   - root supergroup          0 2017-06-27 11:42 /backup_hbase/hbase/.tmp
drwxr-xr-x   - root supergroup          0 2017-06-27 11:42 /backup_hbase/hbase/MasterProcWALs
drwxr-xr-x   - root supergroup          0 2017-06-27 11:42 /backup_hbase/hbase/WALs
drwxr-xr-x   - root supergroup          0 2017-06-27 11:42 /backup_hbase/hbase/archive
drwxr-xr-x   - root supergroup          0 2017-06-27 11:42 /backup_hbase/hbase/data
-rw-r--r--   3 root supergroup         42 2017-06-27 11:42 /backup_hbase/hbase/hbase.id
-rw-r--r--   3 root supergroup          7 2017-06-27 11:42 /backup_hbase/hbase/hbase.version
drwxr-xr-x   - root supergroup          0 2017-06-27 11:42 /backup_hbase/hbase/oldWALs
[root@hdp-node-01 logs]# 

```

3.将hbase的根目录移动到tmp下，然后进行恢复

```
[root@hdp-node-01 logs]# /bigdata_installed/hadoop/bin/hdfs dfs -mkdir /tmp



[root@hdp-node-01 logs]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /
drwxr-xr-x   - root supergroup          0 2017-06-27 11:42 /backup_hbase
drwxr-xr-x   - root supergroup          0 2017-06-27 17:33 /hbase
-rw-r--r--   3 root supergroup         84 2017-06-23 20:36 /simple.cvs
drwxr-xr-x   - root supergroup          0 2017-06-24 19:21 /storefile-outputdir
drwxr-xr-x   - root supergroup          0 2017-06-27 17:33 /tmp

#移动之后，再次查看目录结构
[root@hdp-node-01 logs]# /bigdata_installed/hadoop/bin/hdfs dfs -mv /hbase /tmp
[root@hdp-node-01 logs]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /tmp
drwx------   - root supergroup          0 2017-06-27 11:41 /tmp/hadoop-yarn
drwxr-xr-x   - root supergroup          0 2017-06-27 17:33 /tmp/hbase

```

恢复

将备份的目录拷贝到hbase的root目录下面，然后启动hbase即可

```
[root@hdp-node-01 logs]# /bigdata_installed/hadoop/bin/hadoop distcp /backup_hbase/hbase /


#查看目录结构
[root@hdp-node-01 logs]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /                     
drwxr-xr-x   - root supergroup          0 2017-06-27 11:42 /backup_hbase
drwxr-xr-x   - root supergroup          0 2017-06-27 11:55 /hbase
-rw-r--r--   3 root supergroup         84 2017-06-23 20:36 /simple.cvs
drwxr-xr-x   - root supergroup          0 2017-06-24 19:21 /storefile-outputdir
drwxr-xr-x   - root supergroup          0 2017-06-27 17:33 /tmp

[root@hdp-node-01 logs]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /hbase
drwxr-xr-x   - root supergroup          0 2017-06-27 11:55 /hbase/.tmp
drwxr-xr-x   - root supergroup          0 2017-06-27 11:55 /hbase/MasterProcWALs
drwxr-xr-x   - root supergroup          0 2017-06-27 11:55 /hbase/WALs
drwxr-xr-x   - root supergroup          0 2017-06-27 11:55 /hbase/archive
drwxr-xr-x   - root supergroup          0 2017-06-27 11:55 /hbase/data
-rw-r--r--   3 root supergroup         42 2017-06-27 11:55 /hbase/hbase.id
-rw-r--r--   3 root supergroup          7 2017-06-27 11:55 /hbase/hbase.version
drwxr-xr-x   - root supergroup          0 2017-06-27 11:55 /hbase/oldWALs

#启动hbase，观察表的情况
[root@hdp-node-01 logs]# /bigdata_installed/hbase/bin/start-hbase.sh 

#进入hbase shell查看表的情况
hbase(main):001:0> list
TABLE                                                                                                                                     
student2                                                                                                                                  
test_table                                                                                                                                

=> ["student2", "test_table"]
hbase(main):002:0> scan 'student2'
ROW                                 COLUMN+CELL                                                                                           
 rowkey                             column=info2:age, timestamp=1498463035295, value=22                                                   
 rowkey                             column=info2:gender, timestamp=1498470529792, value=man                                               
 rowkey                             column=info2:name, timestamp=1498463019890, value=zhangsan                                            

```

# 使用CopyTable在表间复制数据

CopyTable可将一张表中的数据复制到同一集群或其他hbase集群的另一张表中

CopyTable还可以带有开始时间和结束时间两个参数，如果指定开始时间和结束时间，该命令就会只对那些时间戳在指定时间范围内的数据进行复制，利用这一特性在某些情况下可以进行增量备份


## 语法

```
$ ./bin/hbase org.apache.hadoop.hbase.mapreduce.CopyTable --help
/bin/hbase org.apache.hadoop.hbase.mapreduce.CopyTable --help
Usage: CopyTable [general options] [--starttime=X] [--endtime=Y] [--new.name=NEW] [--peer.adr=ADR] <tablename>

Options:
 rs.class     hbase.regionserver.class of the peer cluster,
              specify if different from current cluster
 rs.impl      hbase.regionserver.impl of the peer cluster,
 startrow     the start row
 stoprow      the stop row
 starttime    beginning of the time range (unixtime in millis)
              without endtime means from starttime to forever
 endtime      end of the time range.  Ignored if no starttime specified.如果没有指定结束时间，那么该选项将会被忽略
 versions     number of cell versions to copy	
 new.name     new table's name		目标表的表名，如果是同名，则可以不写
 peer.adr     Address of the peer cluster given in the format (zk1，zk2,zk3:2181:/hbase)
              hbase.zookeeer.quorum:hbase.zookeeper.client.port:zookeeper.znode.parent
 families     comma-separated list of families to copy(这里有一个源列族和目标列族的映射，如果不指定目标列族，那么将不会改变列族名)
              To copy from cf1 to cf2, give sourceCfName:destCfName.
              To keep the same name, just give "cfName"
 all.cells    also copy delete markers and deleted cells	（将会拷贝删除的列和标记了删除的列）

Args:
 tablename    Name of the table to copy

Examples:举例
 To copy 'TestTable' to a cluster that uses replication for a 1 hour window:
 $ bin/hbase org.apache.hadoop.hbase.mapreduce.CopyTable --starttime=1265875194289 --endtime=1265878794289 --peer.adr=server1,server2,server3:2181:/hbase --families=myOldCf:myNewCf,cf2,cf3 TestTable

For performance consider the following general options:
  It is recommended that you set the following to >=100. A higher value uses more memory but
  decreases the round trip time to the server and may increase performance.
    -Dhbase.client.scanner.caching=100
  The following should always be set to false, to prevent writing data twice, which may produce
  inaccurate results.
    -Dmapred.map.tasks.speculative.execution=false

```


## 在相同集群之间进行表备份

```
#创建表
srcCluster$ echo "create 'tableOrig', 'cf1', 'cf2'" | hbase shell

#在同一个集群中进行表的复制
srcCluster$ hbase org.apache.hadoop.hbase.mapreduce.CopyTable --new.name=tableCopy tableOrig

```

## 在不同集群之间进行表备份
```
# 创建一个新表在目标集群中
dstCluster$ echo "create 'tableOrig', 'cf1', 'cf2'" | hbase shell

# on source cluster run copy table with destination ZK quorum （目标hbase去zk注册的节点地址）specified using --peer.adr
srcCluster$ hbase org.apache.hadoop.hbase.mapreduce.CopyTable --peer.adr=dstClusterZK:2181:/hbase tableOrig
```


copy表的时候修改表的名字

```
# create new tableCopy on destination cluster
dstCluster$ echo "create 'tableCopy', 'cf1', 'cf2'" | hbase shell

# on source cluster run copy table with destination --peer.adr and --new.name arguments.
srcCluster$ hbase org.apache.hadoop.hbase.mapreduce.CopyTable --peer.adr=dstClusterZK:2181:/hbase --new.name=tableCopy tableOrig
```

## 增量拷贝

通过制定“开始时间”和“结束时间”的时间戳

```
# copy from beginning of time until timeEnd 
# NOTE: Must include start time for end time to be respected. start time cannot be 0.开始时间不能为0
srcCluster$ hbase org.apache.hadoop.hbase.mapreduce.CopyTable ... --starttime=1 --endtime=timeEnd ...

# Copy from starting from and including timeStart until the end of time.
srcCluster$ hbase org.apache.hadoop.hbase.mapreduce.CopyTable ... --starttime=timeStart ...

# Copy entries rows with start time1 including time1 and ending at timeStart excluding timeEnd.
srcCluster$ hbase org.apache.hadoop.hbase.mapreduce.CopyTable ... --starttime=timestart --endtime=timeEnd
```


## 指定要拷贝的列族

```
By adding these arguments we only copy data from the specified column families.

–families=srcCf1
–families=srcCf1,srcCf2
Starting from 0.92.0 you can copy while changing the column family name:

–families=srcCf1:dstCf1 将原列族映射为目标列族 copy from srcCf1 to dstCf1 
–families=srcCf1:dstCf1,srcCf2,srcCf3:dstCf3  将原列族映射为目标列族（srcCf2没有改名）
copy from srcCf1 to destCf1, copy dstCf2 to dstCf2 (no rename), and srcCf3 to dstCf3

```



## 其他选项

从0.94.0开始CopyTable将会删除表中已经被delete的数据，即这样的数据是不会被拷贝的

```
–versions=vers 可以指定要拷贝的版本，默认是拷贝最新的版本 where vers is the number of cell versions to copy (default is 1 aka the latest only)
–all.cells 拷贝删除的cell
also copy delete markers and deleted cells
```


## CopyTable的原理

数据的复制工作是在一个MapReduce任务中进行的，该MapReduce任务会对源表进行扫描，读取目标列族的数据条目，然后使用常规的客户端API（put)将他们写入到备份集群的目录表中


# 将hbase表导出为hdfs上的存储文件

## 语法

```
$ bin/hbase org.apache.hadoop.hbase.mapreduce.Export <tablename> <outputdir> [<versions> [<starttime> [<endtime>]]]

```

默认情况下是导出的是最新版本的cell，我们可以指定version来定制导出的数据的版本



# 以从hdfs上导入转储文件方式恢复hbase数据

```
 bin/hbase org.apache.hadoop.hbase.mapreduce.Import <tablename> <inputdir>
```


# importTsv

参见：使用Bulk Load快速向HBase中导入数据.md


# 备份NameNode的元数据

略

# 备份区域开始键

除了备份hbase中的表以外，我们还需要备份每张表的region的开始键（start key)，因为region是按照region start key来分割的，所以region start key决定了数据在表中的分布情况，region是Hbase中负载均衡和性能指标收集的基本单位

在数据的分布情况难以预先计算或对区域进行手动分割的情况下，其重要的原因在于：各种在线备份方案（包括使用CopyTable和Export的方案在内）都要在MapReduce任务中使用常规的Hbase客户端API来恢复数据，若干能在恢复MapReduce任务执行之前预先创建好一些适当分隔的region，就可以显著提高恢复的速度

参见：《Hbase集群管理指南》中4.7节


# 集群复制

Hbase支持集群复制（cluster replication)，这是一种在hbase集群之间复制数据的方式，例如：我们可以使用这种方式来轻松的将前端实时集群中的数据修改运送到后端以批处理为主要目录的集群中

主集群负责捕获预写日志（WAL），然后将日志中的可复制键/值对写到复制队列中，然后，这些复制消息会被发送给对端的集群，对端集群会使用常规的Hbase客户端API来对这IE复制消息进行重放，为了实现故障转移，主集群还会在zk中保存WAL的当前已经复制的位置

## 准备

你将需要两个Hbase集群，一个作为主集群，另一个作为复制对端集群（从集群），本例假设主集群的地址为master:2181/hbase ，对端集群的地址为1-master1:2181/hbase，这两个集群并不需要具有同等的规模


## 实现步骤

1.需要在主集群中配置启用复制功能
```
#vim hbase-site.xml

hbase.replication=true

```

2.启用要复制的表的复制功能


```
#如果表存在
create 'reptable', {NAME=>'cf1', REPLICATION_SCOPE=>1}


#如果表已经存在
disable 'reptable'
alter 'reptable', NAME=>'cf1',REPLICATION_SCOPE=>1}
enable 'reptable'

```

3.在复制集群（从集群上）上执行上面的1-2步

4.在主机群中，通过Hbase shell增加一个对等的复制集群
```
add_peer '1', '1-master1:2181/hbase'
```

5.在主集群上启动复制功能

```
hbase>start_replication

```

6.在主集群中添加一些数据

```
put 'reptable', 'row1','cf:v1','foo'
put 'reptable', 'row1','cf:v2','bar'
put 'reptable', 'row2','cf:v1','foo'

```

很短时间后，你就能在对端从集群上看到这些数据了

8.连接从集群，检查数据是否同步

```
scan 'reptable'

```

9.关闭主集群的复制功能

```
stop_replication

```

10.将主机群上的复制对端删除

```
remove_peer '1'

```

参见:《HBase复制详解.md》
