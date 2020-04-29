# hbase的几个脚本命令

## 常用脚本

```
1、$HBASE_HOME/bin/start-hbase.sh 
    启动整个集群
2、$HBASE_HOME/bin/stop-hbase.sh 
    停止整个集群
3、$HBASE_HOME/bin/hbase-daemons.sh
    启动或停止，所有的regionserver或zookeeper或backup-master
4、$HBASE_HOME/bin/hbase-daemon.sh
    启动或停止，单个master或regionserver或zookeeper
5、$HBASE_HOME/bin/hbase
    最终启动的实现由这个脚本执行
```

## start-hbase.sh启动脚本说明

一般通过start-hbase.sh启动脚本说明来启动HBase集群，脚本执行流程如下：

```
#!/usr/bin/env bash
# $? 最后运行的命令的结束代码
# $# 传shell给脚本的参数个数
# $0 shell脚本本身的名字
# $1 shell脚本的第一个参数
# $2 shell脚本的第二个参数
# $@ shell脚本的所有参数的列表
  
# Start hadoop hbase daemons.
# Run this on master node.
usage="Usage: start-hbase.sh"
  
bin=`dirname "${BASH_SOURCE-$0}"`
bin=`cd "$bin">/dev/null; pwd`
  
# 1、装载相关配置
. "$bin"/hbase-config.sh
  
# start hbase daemons
errCode=$? # 最后运行的命令的结束代码
if [ $errCode -ne 0 ] # 
then
  exit $errCode
fi
  
# 2、解析参数（0.96版本及以后才可以带唯一参数autorestart，作用就是重启） 
if [ "$1" = "autorestart" ] # 获取start-hbase.sh的参数，调用时未提供参数
then
  commandToRun="autorestart"
else 
  commandToRun="start"
fi
  
# HBASE-6504 - only take the first line of the output in case verbose gc is on
distMode=`$bin/hbase --config "$HBASE_CONF_DIR" org.apache.hadoop.hbase.util.HBaseConfTool 
hbase.cluster.distributed | head -n 1`
# 判定hbase是否为分布式模式，hbase-site.xml中配置的
  
# 3、调用相应的启动脚本
if [ "$distMode" == 'false' ] 
then
  "$bin"/hbase-daemon.sh --config "${HBASE_CONF_DIR}" $commandToRun master $@
else
  "$bin"/hbase-daemons.sh --config "${HBASE_CONF_DIR}" $commandToRun zookeeper
  "$bin"/hbase-daemon.sh --config "${HBASE_CONF_DIR}" $commandToRun master 
  "$bin"/hbase-daemons.sh --config "${HBASE_CONF_DIR}" --hosts "${HBASE_REGIONSERVERS}" $commandToRun regionserver
  "$bin"/hbase-daemons.sh --config "${HBASE_CONF_DIR}" --hosts "${HBASE_BACKUP_MASTERS}" $commandToRun master-backup
fi

```

## hbase-config.sh的作用


装载相关配置，如HBASE_HOME目录、conf目录(HBASE_CONF_DIR)、regionserver机器列表(HBASE_REGIONSERVERS)、JAVA_HOME目录以及HBASE_BACKUP_MASTERS机器列表它会调用$HBASE_HOME/conf/hbase-env.sh。


## hbase-env.sh的作用

主要是配置JVM及其GC参数，还可以配置log目录及参数，配置是否需要hbase管理ZK，配置进程id目录等。

## hbase-daemons.sh的作用

根据需要启动的进程。

```
# Run a hbase command on all slave hosts.
# Modelled after $HADOOP_HOME/bin/hadoop-daemons.sh
  
usage="Usage: hbase-daemons.sh [--config <hbase-confdir>] \
 [--hosts regionserversfile] [start|stop] command args..."
  
# if no args specified, show usage
if [ $# -le 1 ]; then
  echo $usage
  exit 1
fi
  
bin=`dirname "${BASH_SOURCE-$0}"`
bin=`cd "$bin">/dev/null; pwd`
  
. $bin/hbase-config.sh
  
remote_cmd="cd ${HBASE_HOME}; $bin/hbase-daemon.sh --config ${HBASE_CONF_DIR} $@"
args="--hosts ${HBASE_REGIONSERVERS} --config ${HBASE_CONF_DIR} $remote_cmd"
  
command=$2
case $command in
  (zookeeper)
exec "$bin/zookeepers.sh" $args
;;
  (master-backup)
exec "$bin/master-backup.sh" $args
;;
  (*)
exec "$bin/regionservers.sh" $args
;;
esac
```



## zookeepers.sh的作用

如果hbase-env.sh中的HBASE_MANAGES_ZK" = "true"，那么通过ZKServerTool这个类解析xml配置文件，获取ZK节点列表（即hbase.zookeeper.quorum的配置值），然后通过SSH向这些节点发送远程命令

```
cd ${HBASE_HOME};
$bin/hbase-daemon.sh --config ${HBASE_CONF_DIR} start/stop zookeeper 
if [ "$HBASE_MANAGES_ZK" = "true" ]; then
  hosts=`"$bin"/hbase org.apache.hadoop.hbase.zookeeper.ZKServerTool | grep '^ZK host:' | sed 's,^ZK host:,,'`
  cmd=$"${@// /\\ }"
  for zookeeper in $hosts; do
   ssh $HBASE_SSH_OPTS $zookeeper $cmd 2>&1 | sed "s/^/$zookeeper: /" &
   if [ "$HBASE_SLAVE_SLEEP" != "" ]; then
 sleep $HBASE_SLAVE_SLEEP
   fi
  done
fi
```

## regionservers.sh的作用

与zookeepers.sh类似，通过${HBASE_CONF_DIR}/regionservers配置文件，获取regionserver机器列表，然后SSH向这些机器发送远程命令

```
cd ${HBASE_HOME};
$bin/hbase-daemon.sh --config ${HBASE_CONF_DIR} start/stop regionserver

```

## master-backup.sh的作用： 


通过${HBASE_CONF_DIR}/backup-masters这个配置文件，获取backup-masters机器列表（默认配置中，这个配置文件并不存在，所以不会启动backup-master）,然后SSH向这些机器发送远程命令

```
cd ${HBASE_HOME};
$bin/hbase-daemon.sh --config ${HBASE_CONF_DIR} start/stop master --backup
```


## hbase-daemon.sh的作用

无论是zookeepers.sh还是regionservers.sh或是master-backup.sh，最终都会调用本地的hbase-daemon.sh，其执行过程如下： 

1.运行hbase-config.sh，装载各种配置（java环境、log配置、进程ID目录等）；
2.指定文件的执行及日志输出路径；


## $HBASE_HOME/bin/hbase的作用

最终启动的实现由这个脚本执行。
1.可以通过敲入$HBASE_HOME/bin/hbase查看其usage

```
[mvtech2@cu-dmz3 bin]$ hbase
Usage: hbase [<options>] <command> [<args>]
Options:
  --config DIR    Configuration direction to use. Default: ./conf
  --hosts HOSTS   Override the list in 'regionservers' file
  
Commands:
Some commands take arguments. Pass no args or -h for usage.
  shell           Run the HBase shell
  hbck            Run the hbase 'fsck' tool
  hlog            Write-ahead-log analyzer
  hfile           Store file analyzer
  zkcli           Run the ZooKeeper shell
  upgrade         Upgrade hbase
  master          Run an HBase HMaster node
  regionserver    Run an HBase HRegionServer node
  zookeeper       Run a Zookeeper server
  rest            Run an HBase REST server
  thrift          Run the HBase Thrift server
  thrift2         Run the HBase Thrift2 server
  clean           Run the HBase clean up script
  classpath       Dump hbase CLASSPATH
  mapredcp        Dump CLASSPATH entries required by mapreduce
  version         Print the version
  CLASSNAME       Run the class named CLASSNAME
```

2.bin/hbase shell,这个就是常用的shell工具，运维常用的DDL和DML都会通过此进行，其具体实现（对hbase的调用）是用ruby写的。
```
[mvtech2@cu-dmz3 bin]$ hbase shell
HBase Shell; enter 'help<RETURN>' for list of supported commands.
Type "exit<RETURN>" to leave the HBase Shell
Version 0.98.1-hadoop2, r1583035, Sat Mar 29 17:19:25 PDT 2014
  
hbase(main):001:0>
```

3.bin/hbase hbck
    运维常用工具，检查集群的数据一致性状态，其执行是直接调org.apache.hadoop.hbase.util.HBaseFsck中的main函数。
4.bin/hbase hlog
    log分析工具，其执行是直接调org.apache.hadoop.hbase.wal.WALPrettyPrinter中的main函数。    
5.bin/hbase hfile
    hfile分析工具，其执行是直接调org.apache.hadoop.hbase.io.hfile.HFilePrettyPrinter中的main函数。
6.bin/hbase zkcli
    查看/管理ZK的shell工具，其调用了org.apache.zookeeper.ZooKeeperMain的main函数。
7.bin/hbase master、regionserver、zookeeper


```
$HBASE_HOME/bin/hbase start master/regionserver/zookeeper
其执行则直接调用
org.apache.hadoop.hbase.master.HMaster
org.apache.hadoop.hbase.regionserver.HRegionServer
org.apache.hadoop.hbase.zookeeper.HQuorumPeer
的main函数，而这些main函数就是了new一个了Runnable的HMaster/HRegionServer/QuorumPeer，在不停的Running...
```

8.bin/hbase classpath 打印classpath
9.bin/hbase version 打印hbase版本信息
10.bin/hbase CLASSNAME

所有实现了main函数的类都可以通过这个脚本来运行，比如前面的hlog hfile hbck工具，实质是对这个接口的一个快捷调用，而其他未提供快捷方式的class我们也可以用这个接口调用，如Region merge 调用：$HBASE_HOME/bin/hbase/org.apache.hadoop.hbase.util.Merge。


## 脚本使用小结：

1.开启集群，start-hbase.sh
2.关闭集群，stop-hbase.sh
3.开启/关闭所有的regionserver、zookeeper
hbase-daemons.sh start/stop regionserver/zookeeper
4.开启/关闭单个regionserver、zookeeper
hbase-daemon.sh start/stop regionserver/zookeeper
5.开启/关闭master 
hbase-daemon.sh start/stop master,是否成为active master取决于当前是否有active master。


