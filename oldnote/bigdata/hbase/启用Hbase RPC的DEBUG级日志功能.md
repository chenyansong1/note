
# 启用Hbase RPC的DEBUG级日志功能

在生产环境中，日志级别通常设置为INFO级别，这种设置能够适合很多种情况，然而，在某些情况下，你可能希望查看一下某些特殊Hadoop/hbase守护程序的调试信息，此时就需要在线改变日志级别，守护进程的日志级别可在Hbase的web界面中修改，而且无需重启守护进程

这种情况就适用于在生产环境中通过改变日志级别对Hbase集群进行故障排查


## 操作步骤

在集群的web界面选择“Log Level”，出现下面的界面

![](/images/bigdata/hbase/log_level.jpg)

这里可以：
* 获取当前的日志级别
* 设置新的日志级别

查看指定包或类的当前日志记录级别

![](/images/bigdata/hbase/log_level_2.jpg)


设置新的日志级别，输入包名和日志记录级别，然后点击Set Log Level按钮设置该包的日志记录级别(例如：想看org.apache.hadoop.ipc这个包的日志级别为DEBUG)

![](/images/bigdata/hbase/log_level_3.jpg)

在日志中就可以看到该信息

```
[root@hdp-node-01 logs]# tail -f /bigdata_installed/hbase/logs/hbase-root-master-hdp-node-01.log                       
2017-06-30 14:52:18,496 DEBUG [IPC Client (13402762) connection to hdp-node-01/192.168.153.201:9000 from root] ipc.Client: IPC Client (13402762) connection to hdp-node-01/192.168.153.201:9000 from root: starting, having connections 1
2017-06-30 14:52:18,510 DEBUG [IPC Parameter Sending Thread #0] ipc.Client: IPC Client (13402762) connection to hdp-node-01/192.168.153.201:9000 from root sending #227
2017-06-30 14:52:18,512 DEBUG [IPC Client (13402762) connection to hdp-node-01/192.168.153.201:9000 from root] ipc.Client: IPC Client (13402762) connection to hdp-node-01/192.168.153.201:9000 from root got value #227
2017-06-30 14:52:18,512 DEBUG [hdp-node-01,16000,1498803901664_ChoreService_1] ipc.ProtobufRpcEngine: Call: getListing took 40ms
2017-06-30 14:52:18,596 DEBUG [IPC Parameter Sending Thread #0] ipc.Client: IPC Client (13402762) connection to hdp-node-01/192.168.153.201:9000 from root sending #228

```



上面的演示把Hbase IPC（org.apache.hadoop.ipc）包的日志级别设为DEBUG级别，这样在HRegionServer守护进程中就可以看到其IPC调试信息写入日志文件中


注意：Hadoop/Hbase守护进程会在很短的时间内产生出数量非常巨大的调试日志，因此在收集到足够调试信息之后，应立即将日志级别调回INFO级别


## 使用hadoop daemonlog 设置/获取日志级别

如下面这条命令可以获取本机所运行的HMaster守护进程的IPC日志记录级别


```
[root@hdp-node-01 logs]# /bigdata_installed/hadoop/bin/hadoop daemonlog -getlevel localhost:26010 org.apache.hadoop.ipc
Connecting to http://localhost:26010/logLevel?log=org.apache.hadoop.ipc
Submitted Log Name: org.apache.hadoop.ipc
Log Class: org.apache.commons.logging.impl.Log4JLogger
Effective level: INFO


```


查看使用帮助

```

[root@hdp-node-01 logs]# /bigdata_installed/hadoop/bin/hadoop daemonlog 

Usage: General options are:
        [-getlevel <host:httpPort> <name>]
        [-setlevel <host:httpPort> <name> <level>]

[root@hdp-node-01 logs]# 

```
