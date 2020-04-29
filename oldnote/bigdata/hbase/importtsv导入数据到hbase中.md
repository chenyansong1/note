
# 数据先到metastore

## 1.首先是将csv文件上传到hdfs上。 

上面的步骤是将simple.csv文件上传到hdfs中的/user/name中 (数据的间隔是制表符，若是逗号间隔符需要指定间隔符)

```
[root@hdp-node-03 ~]# /bigdata_installed/hadoop/bin/hdfs dfs -put simple.cvs /
#查看
[root@hdp-node-03 ~]# /bigdata_installed/hadoop/bin/hdfs dfs -cat /simple.cvs
111,chenyansong,21,man,chengxuyuan,ap_111
112,chenyansong,21,man,chengxuyuan,ap_112

```

## 2.通过hbase shell 界面创建相应的表

* 进入shell界面： hbase shell
* 创建表：create 'student','info'

## 3.查看hdfs变化
```
#此时是没有文件的
[root@hdp-node-02 ~]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /hbase/data/default/student/613ae6b498e5f8cc089c65b6857587a3/info
[root@hdp-node-02 ~]# 
```

## 4.向表中插入数据


```
./hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.columns=HBASE_ROW_KEY,info:f11,info:f12,info:f13,info:f14,info:f15 '-Dimporttsv.separator=,' student  hdfs://hdp-node-01:9000/simple.cvs

```

如果是制表符，不是使用逗号分隔

```
./hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.columns=HBASE_ROW_KEY,info:f1,info:f2,info:f3,info:f4,info:f5 student  hdfs://hdp-node-01:9000/simple.cvs
```

## 观察

hbase shell中
```
hbase(main):041:0> scan 'student'
ROW                                 COLUMN+CELL                                                                                           
 111                                column=info:f11, timestamp=1498221834515, value=chenyansong                                           
 111                                column=info:f12, timestamp=1498221834515, value=21                                                    
 111                                column=info:f13, timestamp=1498221834515, value=man                                                   
 111                                column=info:f14, timestamp=1498221834515, value=chengxuyuan                                           
 111                                column=info:f15, timestamp=1498221834515, value=ap_111                                                
 112                                column=info:f11, timestamp=1498221834515, value=chenyansong                                           
 112                                column=info:f12, timestamp=1498221834515, value=21                                                    
 112                                column=info:f13, timestamp=1498221834515, value=man                                                   
 112                                column=info:f14, timestamp=1498221834515, value=chengxuyuan                                           
 112                                column=info:f15, timestamp=1498221834515, value=ap_112     
```

hdfs中
```
[root@hdp-node-02 ~]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /hbase/data/default/student/613ae6b498e5f8cc089c65b6857587a3/info
[root@hdp-node-02 ~]# 

```

刷新表之后，查看hdfs
```
#Hasee刷新
hbase(main):042:0> flush 'student'
0 row(s) in 0.5450 seconds

hbase(main):043:0> 

#再次去hdfs中查看
[root@hdp-node-02 ~]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /hbase/data/default/student/613ae6b498e5f8cc089c65b6857587a3/info
-rw-r--r--   3 root supergroup       5327 2017-06-23 20:46 /hbase/data/default/student/613ae6b498e5f8cc089c65b6857587a3/info/8b02bc6271bc45acb695a86d78eb503f
[root@hdp-node-02 ~]# 


```


该工具会为我们启动一个MapReduce任务，在map阶段，该任务会读取并解析指定输入目录下的tsv文件的行，然后根据列映射信息将这些行插入到hbase中，**他会在多台服务器上并行执行Read和Put操作**(因为数据是通过Put插入的，那么数据首先是写到HLog，然后是metastore，再然后是hfile中)，**也就是说，插入的时候，是写入的metastore中的，只有刷新表的之后，会将输入刷入hfile中**，所以从这里也是可以看出，如果没有走metastore，那么在HLog中也是不会留下记录的，所以直接生成hfile的方式不会走HLog


# 插入数据直接进入hfile


## 1.首先是将csv文件上传到hdfs上。 

上面的步骤是将simple.csv文件上传到hdfs中的/user/name中 (数据的间隔是制表符，若是逗号间隔符需要指定间隔符)

```
[root@hdp-node-03 ~]# /bigdata_installed/hadoop/bin/hdfs dfs -put simple.cvs /
#查看
[root@hdp-node-03 ~]# /bigdata_installed/hadoop/bin/hdfs dfs -cat /simple.cvs
111,chenyansong,21,man,chengxuyuan,ap_111
112,chenyansong,21,man,chengxuyuan,ap_112

```

## 2.通过hbase shell 界面创建相应的表

* 进入shell界面： hbase shell
* 创建表：create 'student','info'

## 3.查看hdfs变化
```
#此时是没有文件的
[root@hdp-node-02 ~]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /hbase/data/default/student/613ae6b498e5f8cc089c65b6857587a3/info
[root@hdp-node-02 ~]# 
```



## 3、通过mapreduce作业，生成Hfile文件


importtsv语法说明

```
importtsv -Dimporttsv.columns=a,b,c <tableName> <inputdir>

#<tableName>表名
#<inputdir>输入tsv文件的路径
```



importtsv参数说明
```
-Dimporttsv.columns参数所指定的TSV文件的字段序号与hbase表中的字段有对应关系，其中HBASE_ROW_KEY是一个常量，他指定了行健的字段名
-Dimporttsv.bulk.output
-Dimporttsv.separator
-importtsv.skip.bad.lines=false	当遇到无效行时是否失败
'-Dimporttsv.separator=!' 指定分隔符
-Dimporttsv.timestamp=currentTimeAsLong 本次导入使用指定的时间戳
-Dimporttsv.mapper.class=my.Mapper	使用用户自定义该的映射程序，不适用org.

```



执行：
```
hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.columns=HBASE_ROW_KEY,columnfamily -Dimporttsv.bulk.output=/hfile_tmp tablename /user/username/simple.csv 

#（默认间隔符是制表符）
```
或者 
```
hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.columns=HBASE_ROW_KEY,columnfamily -Dimporttsv.bulk.output=/hfile_tmp tablename -Dimporttsv.separator=, /user/username/simple.csv
```

```
./hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.columns=HBASE_ROW_KEY,info:f1,info:f2,info:f3,info:f4,info:f5 -Dimporttsv.bulk.output=hdfs://hdp-node-01:9000/storefile-outputdir '-Dimporttsv.separator=,' student  hdfs://hdp-node-01:9000/simple.cvs
```

此时在hdfs的目录中生成列族的描述(此时列族是info)
```
root@hdp-node-03 ~]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /storefile-outputdir/info/7a26fafeb7374403a958409e3c3210c3
-rw-r--r--   3 root supergroup       5344 2017-06-24 19:21 /storefile-outputdir/info/7a26fafeb7374403a958409e3c3210c3
```



## 4、此时mapreduce执行成功后，会在hdfs上生成一个目录，该目录就是上面的命令行指定的目录（如：-Dimporttsv.bulk.output=hdfs://hdp-node-01:9000/storefile-outputdir）。

但是该目录的权限是属于当前用户的，因此需要修改他的权限。(其实这一步在root下没有做)

sudo -u hdfs hdfs dfs -chown -R hbase:hbase /hfile_tmp



## 5、执行最后一步，批量导入

```
./hbase org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles hdfs://hdp-node-01:9000/storefile-outputdir student
```

## 6.观察结果

```
#在hbase shell中
hbase(main):004:0> scan 'student'
ROW                                 COLUMN+CELL                                                                                           
 111                                column=info:f1, timestamp=1498303258344, value=chenyansong                                            
 111                                column=info:f2, timestamp=1498303258344, value=21                                                     
 111                                column=info:f3, timestamp=1498303258344, value=man                                                    
 111                                column=info:f4, timestamp=1498303258344, value=chengxuyuan                                            
 111                                column=info:f5, timestamp=1498303258344, value=ap_111                                                 
 112                                column=info:f1, timestamp=1498303258344, value=chenyansong                                            
 112                                column=info:f2, timestamp=1498303258344, value=21                                                     
 112                                column=info:f3, timestamp=1498303258344, value=man                                                    
 112                                column=info:f4, timestamp=1498303258344, value=chengxuyuan                                            
 112                                column=info:f5, timestamp=1498303258344, value=ap_112      

#在hdfs中
[root@hdp-node-03 ~]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /hbase/data/default/student/4ef42410d6a80fcb24e0365f1c619fcf/info

-rw-r--r--   3 root supergroup       5344 2017-06-24 19:21 /hbase/data/default/student/4ef42410d6a80fcb24e0365f1c619fcf/info/d0491c38b37e489c9b905cd182fada41_SeqId_6_

```





注意：在导入的过程中会抛出一个异常，但是这个不会影响数据的导入

这个异常时Hbase的一个bug，参见
https://issues.apache.org/jira/browse/HBASE-14365

```
org.apache.hadoop.hbase.TableNotFoundException: hbase:labels
        at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation.locateRegionInMeta(ConnectionManager.java:1258)
        at org.apache.hadoop.hbase.client.ConnectionManager$HConnectionImplementation.locateRegion(ConnectionManager.java:1156)
        at org.apache.hadoop.hbase.client.RpcRetryingCallerWithReadReplicas.getRegionLocations(RpcRetryingCallerWithReadReplicas.java:300)
        at org.apache.hadoop.hbase.client.ScannerCallableWithReplicas.call(ScannerCallableWithReplicas.java:156)
        at org.apache.hadoop.hbase.client.ScannerCallableWithReplicas.call(ScannerCallableWithReplicas.java:60)
        at org.apache.hadoop.hbase.client.RpcRetryingCaller.callWithoutRetries(RpcRetryingCaller.java:210)
        at org.apache.hadoop.hbase.client.ClientScanner.call(ClientScanner.java:327)
        at org.apache.hadoop.hbase.client.ClientScanner.nextScanner(ClientScanner.java:302)
        at org.apache.hadoop.hbase.client.ClientScanner.initializeScannerInConstruction(ClientScanner.java:167)
        at org.apache.hadoop.hbase.client.ClientScanner.<init>(ClientScanner.java:162)
        at org.apache.hadoop.hbase.client.HTable.getScanner(HTable.java:797)
        at org.apache.hadoop.hbase.mapreduce.DefaultVisibilityExpressionResolver.init(DefaultVisibilityExpressionResolver.java:91)
        at org.apache.hadoop.hbase.mapreduce.CellCreator.<init>(CellCreator.java:48)
        at org.apache.hadoop.hbase.mapreduce.TsvImporterMapper.setup(TsvImporterMapper.java:108)
        at org.apache.hadoop.mapreduce.Mapper.run(Mapper.java:142)
        at org.apache.hadoop.mapred.MapTask.runNewMapper(MapTask.java:764)
        at org.apache.hadoop.mapred.MapTask.run(MapTask.java:340)
        at org.apache.hadoop.mapred.LocalJobRunner$Job$MapTaskRunnable.run(LocalJobRunner.java:243)
        at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:471)
        at java.util.concurrent.FutureTask.run(FutureTask.java:262)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
        at java.lang.Thread.run(Thread.java:745)
2017-06-23 19:55:10,772 INFO  [LocalJobRunner Map Task Executor #0] client.ConnectionManager$HConnectionImplementation: Closing zookeeper sessionid=0x25cd31142b9001d

```



