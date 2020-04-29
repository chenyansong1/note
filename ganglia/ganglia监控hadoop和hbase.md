
前提：需要安装ganglia

参见：
* [ganglia安装（yum)](https://github.com/belongtocys/notebook/blob/master/ganglia/ganglia%E5%AE%89%E8%A3%85(yum).md)
* [ganglia安装（编译)](https://github.com/belongtocys/notebook/blob/master/ganglia/ganglia%E5%AE%89%E8%A3%85(%E7%BC%96%E8%AF%91).md)


# 1.添加hadoop监控


修改修改hadoop的文件


修改添加
```

# for Ganglia 3.1 support
*.sink.ganglia.class=org.apache.hadoop.metrics2.sink.ganglia.GangliaSink31

*.sink.ganglia.period=10

namenode.sink.ganglia.servers=hdp-node-01:8649
resourcemanager.sink.ganglia.servers=hdp-node-01:8649
mrappmaster.sink.ganglia.servers=hdp-node-01:8649
jobhistoryserver.sink.ganglia.servers=hdp-node-01:8649
datanode.sink.ganglia.servers=hdp-node-01:8649
nodemanager.sink.ganglia.servers=hdp-node-01:8649
```

完整的代码如下 ：

cat  /bigdata_installed/hadoop/etc/hadoop/hadoop-metrics2.properties 

```

# syntax: [prefix].[source|sink].[instance].[options]
# See javadoc of package-info.java for org.apache.hadoop.metrics2 for details

*.sink.file.class=org.apache.hadoop.metrics2.sink.FileSink
# default sampling period, in seconds
*.period=10

# The namenode-metrics.out will contain metrics from all context
#namenode.sink.file.filename=namenode-metrics.out
# Specifying a special sampling period for namenode:
#namenode.sink.*.period=8

#datanode.sink.file.filename=datanode-metrics.out

#resourcemanager.sink.file.filename=resourcemanager-metrics.out

#nodemanager.sink.file.filename=nodemanager-metrics.out

#mrappmaster.sink.file.filename=mrappmaster-metrics.out

#jobhistoryserver.sink.file.filename=jobhistoryserver-metrics.out

# the following example split metrics of different
# context to different sinks (in this case files)
#nodemanager.sink.file_jvm.class=org.apache.hadoop.metrics2.sink.FileSink
#nodemanager.sink.file_jvm.context=jvm
#nodemanager.sink.file_jvm.filename=nodemanager-jvm-metrics.out
#nodemanager.sink.file_mapred.class=org.apache.hadoop.metrics2.sink.FileSink
#nodemanager.sink.file_mapred.context=mapred
#nodemanager.sink.file_mapred.filename=nodemanager-mapred-metrics.out

#
# Below are for sending metrics to Ganglia
#
# for Ganglia 3.0 support
# *.sink.ganglia.class=org.apache.hadoop.metrics2.sink.ganglia.GangliaSink30
#
# for Ganglia 3.1 support
*.sink.ganglia.class=org.apache.hadoop.metrics2.sink.ganglia.GangliaSink31

*.sink.ganglia.period=10

# default for supportsparse is false
*.sink.ganglia.supportsparse=true

*.sink.ganglia.slope=jvm.metrics.gcCount=zero,jvm.metrics.memHeapUsedM=both
*.sink.ganglia.dmax=jvm.metrics.threadsBlocked=70,jvm.metrics.memHeapUsedM=40

# Tag values to use for the ganglia prefix. If not defined no tags are used.
# If '*' all tags are used. If specifiying multiple tags separate them with 
# commas. Note that the last segment of the property name is the context name.
#
#*.sink.ganglia.tagsForPrefix.jvm=ProcesName
#*.sink.ganglia.tagsForPrefix.dfs=
#*.sink.ganglia.tagsForPrefix.rpc=
#*.sink.ganglia.tagsForPrefix.mapred=

#namenode.sink.ganglia.servers=yourgangliahost_1:8649,yourgangliahost_2:8649

#datanode.sink.ganglia.servers=yourgangliahost_1:8649,yourgangliahost_2:8649

#resourcemanager.sink.ganglia.servers=yourgangliahost_1:8649,yourgangliahost_2:8649

#nodemanager.sink.ganglia.servers=yourgangliahost_1:8649,yourgangliahost_2:8649

#mrappmaster.sink.ganglia.servers=yourgangliahost_1:8649,yourgangliahost_2:8649

#jobhistoryserver.sink.ganglia.servers=yourgangliahost_1:8649,yourgangliahost_2:8649


namenode.sink.ganglia.servers=hdp-node-01:8649
resourcemanager.sink.ganglia.servers=hdp-node-01:8649
mrappmaster.sink.ganglia.servers=hdp-node-01:8649
jobhistoryserver.sink.ganglia.servers=hdp-node-01:8649
datanode.sink.ganglia.servers=hdp-node-01:8649
nodemanager.sink.ganglia.servers=hdp-node-01:8649

```

# 2.添加hbase的监控


需要添加如下的代码

```
# default sampling period
*.period=10


*.source.filter.class=org.apache.hadoop.metrics2.filter.RegexFilter  
#*.source.filter.class=org.apache.hadoop.metrics2.filter.GlobFilter  
*.record.filter.class=${*.source.filter.class}  
*.metric.filter.class=${*.source.filter.class}  
*.period=10  


*.sink.ganglia.class=org.apache.hadoop.metrics2.sink.ganglia.GangliaSink31  
*.sink.ganglia.period=10  

hbase.sink.ganglia.period=10  
hbase.sink.ganglia.servers=hdp-node-01:8649
#这里是过滤一些文件（使用正则过滤）
hbase.sink.ganglia.metric.filter.exclude=^(.*table.*)|(\\w+metric)|(\\w+region\\w+)|(Balancer\\w+)|(\\w+Assign\\w+)|(\\w+percentile)|(\\w+max)|(\\w+median)|(\\w+min)|(MetaHlog\\w+)|(\\w+WAL\\w+)$
```


完整的配置文件如下：

```
[root@hdp-node-01 libexec]# 
[root@hdp-node-01 libexec]# 
[root@hdp-node-01 libexec]# 
[root@hdp-node-01 libexec]# cat  /bigdata_installed/hbase/conf/hadoop-metrics2-hbase.properties 
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# syntax: [prefix].[source|sink].[instance].[options]
# See javadoc of package-info.java for org.apache.hadoop.metrics2 for details

*.sink.file*.class=org.apache.hadoop.metrics2.sink.FileSink
# default sampling period
*.period=10

# Below are some examples of sinks that could be used
# to monitor different hbase daemons.

# hbase.sink.file-all.class=org.apache.hadoop.metrics2.sink.FileSink
# hbase.sink.file-all.filename=all.metrics

# hbase.sink.file0.class=org.apache.hadoop.metrics2.sink.FileSink
# hbase.sink.file0.context=hmaster
# hbase.sink.file0.filename=master.metrics

# hbase.sink.file1.class=org.apache.hadoop.metrics2.sink.FileSink
# hbase.sink.file1.context=thrift-one
# hbase.sink.file1.filename=thrift-one.metrics

# hbase.sink.file2.class=org.apache.hadoop.metrics2.sink.FileSink
# hbase.sink.file2.context=thrift-two
# hbase.sink.file2.filename=thrift-one.metrics

# hbase.sink.file3.class=org.apache.hadoop.metrics2.sink.FileSink
# hbase.sink.file3.context=rest
# hbase.sink.file3.filename=rest.metrics


*.source.filter.class=org.apache.hadoop.metrics2.filter.RegexFilter  
#*.source.filter.class=org.apache.hadoop.metrics2.filter.GlobFilter  
*.record.filter.class=${*.source.filter.class}  
*.metric.filter.class=${*.source.filter.class}  
*.period=10  


*.sink.ganglia.class=org.apache.hadoop.metrics2.sink.ganglia.GangliaSink31  
*.sink.ganglia.period=10  

hbase.sink.ganglia.period=10  
hbase.sink.ganglia.servers=hdp-node-01:8649
hbase.sink.ganglia.metric.filter.exclude=^(.*table.*)|(\\w+metric)|(\\w+region\\w+)|(Balancer\\w+)|(\\w+Assign\\w+)|(\\w+percentile)|(\\w+max)|(\\w+median)|(\\w+min)|(MetaHlog\\w+)|(\\w+WAL\\w+)$

```







