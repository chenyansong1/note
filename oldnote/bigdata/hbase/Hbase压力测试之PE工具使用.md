# Hbase压力测试之PE工具使用

PerformanceEvaluation是HBase自带的性能测试工具，该工具提供了顺序读写、随机读写、扫描等性能测试功能。本文简要介绍HBase PerformanceEvaluation的使用方法。

这里不需要指定表和列族的名字，因为Hbase的PE工具在其代码中会自动创建一张名为TestTable的表，该表带有一个名为info的列族

**注意：使用PE运行读测试之前，需要先执行写测试，因为读测试要使用写测试插入的那些数据**

## PE使用帮助

```
[root@hdp-node-01 ~]# /bigdata_installed/hbase/bin/hbase org.apache.hadoop.hbase.PerformanceEvaluation
Usage: java org.apache.hadoop.hbase.PerformanceEvaluation \
  <OPTIONS> [-D<property=value>]* <command> <nclients>

Options:
 nomapred        Run multiple clients using threads (rather than use mapreduce) #使用多线程代替MapReduce
 rows            Rows each client runs. Default: One million #每个客户端跑多少行，默认是100w
 size            Total size in GiB. Mutually exclusive with --rows. Default: 1.0.
 sampleRate      Execute test on a sample of total rows. Only supported by randomRead. Default: 1.0
 traceRate       Enable HTrace spans. Initiate tracing every N rows. Default: 0
 table           Alternate table name. Default: 'TestTable'
 multiGet        If >0, when doing RandomRead, perform multiple gets instead of single gets. Default: 0
 compress        Compression type to use (GZ, LZO, ...). Default: 'NONE' #默认的压缩方式
 flushCommits    Used to determine if the test should flush the table. Default: false
 writeToWAL      Set writeToWAL on puts. Default: True
 autoFlush       Set autoFlush on htable. Default: False
 oneCon          all the threads share the same connection. Default: False
 presplit        Create presplit table. Recommended for accurate perf analysis (see guide).  Default: disabled
 inmemory        Tries to keep the HFiles of the CF inmemory as far as possible. Not guaranteed that reads are always served from memory.  Default: false
 usetags         Writes tags along with KVs. Use with HFile V3. Default: false
 numoftags       Specify the no of tags that would be needed. This works only if usetags is true.
 filterAll       Helps to filter out all the rows on the server side there by not returning any thing back to the client.  Helps to check the server side performance.  Uses FilterAllFilter internally. 
 latency         Set to report operation latencies. Default: False
 bloomFilter      Bloom filter type, one of [NONE, ROW, ROWCOL]
 valueSize       Pass value size to use: Default: 1024
 valueRandom     Set if we should vary value size between 0 and 'valueSize'; set on read for stats on size: Default: Not set.
 valueZipf       Set if we should vary value size between 0 and 'valueSize' in zipf form: Default: Not set.
 period          Report every 'period' rows: Default: opts.perClientRunRows / 10
 multiGet        Batch gets together into groups of N. Only supported by randomRead. Default: disabled
 addColumns      Adds columns to scans/gets explicitly. Default: true
 replicas        Enable region replica testing. Defaults: 1.
 splitPolicy     Specify a custom RegionSplitPolicy for the table.
 randomSleep     Do a random sleep before each get between 0 and entered value. Defaults: 0
 columns         Columns to write per row. Default: 1
 caching         Scan caching to use. Default: 30

 Note: -D properties will be applied to the conf used. 
  For example: 
   -Dmapreduce.output.fileoutputformat.compress=true
   -Dmapreduce.task.timeout=60000

Command:
 append          Append on each row; clients overlap on keyspace so some concurrent operations
 checkAndDelete  CheckAndDelete on each row; clients overlap on keyspace so some concurrent operations
 checkAndMutate  CheckAndMutate on each row; clients overlap on keyspace so some concurrent operations
 checkAndPut     CheckAndPut on each row; clients overlap on keyspace so some concurrent operations
 filterScan      Run scan test using a filter to find a specific row based on it's value (make sure to use --rows=20)
 increment       Increment on each row; clients overlap on keyspace so some concurrent operations
 randomRead      Run random read test	#随机读
 randomSeekScan  Run random seek and scan 100 test
 randomWrite     Run random write test	#随机写
 scan            Run scan test (read every row)
 scanRange10     Run random seek scan with both start and stop row (max 10 rows)
 scanRange100    Run random seek scan with both start and stop row (max 100 rows)
 scanRange1000   Run random seek scan with both start and stop row (max 1000 rows)
 scanRange10000  Run random seek scan with both start and stop row (max 10000 rows)
 sequentialRead  Run sequential read test	#顺序读
 sequentialWrite Run sequential write test	#顺序写

Args:
 nclients        Integer. Required. Total number of clients (and HRegionServers)
                 running: 1 <= value <= 500 #指定客户端的个数
Examples:
 To run a single client doing the default 1M sequentialWrites:
 $ bin/hbase org.apache.hadoop.hbase.PerformanceEvaluation sequentialWrite 1
 To run 10 clients doing increments over ten rows:
 $ bin/hbase org.apache.hadoop.hbase.PerformanceEvaluation --rows=10 --nomapred increment 10
[root@hdp-node-01 ~]# 
```



## 写操作

1）  顺序写：sequentialWrite
例如，预分区100 regions，100线程并发，顺序写1亿条数据：
```
hbase org.apache.hadoop.hbase.PerformanceEvaluation--nomapred --rows=1000000 --presplit=100 sequentialWrite 100
```

注意事项：
a.  hbase PE默认使用mapreduce作业进行读写扫描数据，如果使用多线程/客户端并发来代替mapreduce作业，需要加上选项 --nomapred
b.  设置每个客户端处理多少行记录，使用参数 --rows，例如 --rows=1000000,即每个线程/客户端处理1000000行记录；
c.  --presplit参数，只有在写数据时使用，读表时使用参数--presplit，会导致之前写的表数据被删除；
d.  最后一个参数100即为100个线程/客户端并发。


2）  随机写：randomWrite
例如，随机写1亿条数据：
```
hbase org.apache.hadoop.hbase.PerformanceEvaluation--nomapred --rows=1000000 --presplit=100 randomWrite 100
```

## 读操作

1）  顺序读：sequentialRead
例如，顺序读1亿条数据：
```
hbase org.apache.hadoop.hbase.PerformanceEvaluation--nomapred --rows=1000000 sequentialRead 100
```

2）  随机读：
例如，随机读1亿条数据：
```
hbase org.apache.hadoop.hbase.PerformanceEvaluation--nomapred --rows=100000 randomRead 100
```


## 扫描操作

扫描：scan\\ scanRange10\\ scanRange100\\ scanRange1000\\scanRange10000

```
hbase org.apache.hadoop.hbase.PerformanceEvaluation--nomapred --rows=1000000 scan 100
hbase org.apache.hadoop.hbase.PerformanceEvaluation--nomapred --rows=100000 scanRange10 100
hbase org.apache.hadoop.hbase.PerformanceEvaluation--nomapred --rows=10000 scanRange100 100
hbase org.apache.hadoop.hbase.PerformanceEvaluation--nomapred --rows=1000 scanRange1000 100
hbase org.apache.hadoop.hbase.PerformanceEvaluation--nomapred --rows=100 scanRange10000 100
```

## 注意事项

Hbase读\\扫描表数据，会优先读取内存数据，所以在写表操作结束后，可手动对表进行一次flush操作，以此清空内存中memstore数据；并且每次读\\扫描测试前，重启hbase，把缓存清掉。













