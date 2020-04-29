# 概念介绍

我们将在集群的每个结点上都安装一个Ganglia监控守护进程（Gmond Ganglia monitoring Daemon),用它来收集该节点上的服务器指标和Hbase指标，然后这些指标会被发送给Ganglia元守护进程（Gmetad, Ganglia Meta Daemon）服务器，该服务器会使用RRDtool（round-robin database too,)来计算这些指标并将其保存在轮替型时间序列数据库中，本例将只安装一个Gmetad节点，但我们可以让其向外伸缩，成为一个含多个Gmetad节点的监控系统，**其中每个Gmetad节点只收集他自己负责的那部分Gmond节点的结果**


Ganglia其核心由3部分组成：

* gmond：运行在每个节点上监视并收集节点信息，可以同时收发统计信息，它可以运行在广播模式和单播模式中。
* gmetad：从gmond以poll的方式收集和存储原数据。
* ganglia-web：部署在gmetad机器上，访问gmetad存储的元数据并由Apache Web提高用户访问接口。

　　下面，我们来看看Ganglia的架构图，如下图所示：


![](/images/ganglia/jiagou.png)

![](/images/ganglia/ganglia_flow.jpg)


# 安装


本次安装的Ganglia工具是基于Apache的Hadoop-2.6.0，若是未安装Hadoop集群，可以参考我写的《配置高可用的Hadoop平台》。另外系统环境是CentOS 6.6。首先，我们下载Ganglia软件包，步骤如下所示：

* 第一步：安装yum epel源

```
[hadoop@nna ~]$ rpm -Uvh http://dl.fedoraproject.org/pub/epel/6/i386/epel-release-6-8.noarch.rpm
```

* 第二步：安装依赖包

```
[hadoop@nna ~]$ yum -y install httpd-devel automake autoconf libtool ncurses-devel libxslt groff pcre-devel pkgconfig
```

* 第三步：查看Ganglia安装包

```
[hadoop@nna ~]$ yum search ganglia

```

　　然后，我为了简便，把Ganglia安装全部安装，安装命令如下所示：

* 第四步：安装Ganglia

```
[hadoop@nna ~]$ yum -y install ganglia*
```

　　最后等待安装完成，由于这里资源有限，我将Ganglia Web也安装在NNA节点上，另外，其他节点也需要安装Ganglia的Gmond服务，该服务用来发送数据到Gmetad，安装方式参考上面的步骤。

# 部署
　　在安装Ganglia时，我这里将Ganglia Web部署在NNA节点，其他节点部署Gmond服务，下表为各个节点的部署角色：

|节点	|Host			|角色						|
|-------|---------------|---------------------------|
|NNA	|10.211.55.26	|Gmetad、Gmond、Ganglia-Web  |
|NNS	|10.211.55.27	|Gmond                      |
|DN1	|10.211.55.16	|Gmond                      |
|DN2	|10.211.55.17	|Gmond                      |
|DN3	|10.211.55.18	|Gmond                      |


　　Ganglia部署在Hadoop集群的分布图，如下所示：


![](/images/ganglia/ganglia_in_hadoop.png)


# 配置

在安装好Ganglia后，我们需要对Ganglia工具进行配置，在由Ganglia-Web服务的节点上，我们需要配置Web服务。

vi /etc/httpd/conf.d/ganglia.conf 

```
#
# Ganglia monitoring system php web frontend
#

Alias /ganglia /usr/share/ganglia

<Location /ganglia>
  Order deny,allow
  # Deny from all
  Allow from all
  # Allow from 127.0.0.1
  # Allow from ::1
  # Allow from .example.com
</Location>
```

vi /etc/ganglia/gmetad.conf 

```
data_source "hadoop" nna nns dn1 dn2 dn3
```

这里“hadoop”表示集群名，nna nns dn1 dn2 dn3表示节点域名或IP。

vi /etc/ganglia/gmond.conf 

```
/*
 * The cluster attributes specified will be used as part of the <CLUSTER>
 * tag that will wrap all hosts collected by this instance.
 */
cluster {
  name = "hadoop"
  owner = "unspecified"
  latlong = "unspecified"
  url = "unspecified"
}

/* Feel free to specify as many udp_send_channels as you like.  Gmond
   used to only support having a single channel */
udp_send_channel {
  #bind_hostname = yes # Highly recommended, soon to be default.
                       # This option tells gmond to use a source address
                       # that resolves to the machine's hostname.  Without
                       # this, the metrics may appear to come from any
                       # interface and the DNS names associated with
                       # those IPs will be used to create the RRDs.
  # mcast_join = 239.2.11.71
  host = 10.211.55.26
  port = 8649
  ttl = 1
}

/* You can specify as many udp_recv_channels as you like as well. */
udp_recv_channel {
  # mcast_join = 239.2.11.71
  port = 8649
  #bind = 10.211.55.26
  retry_bind = true
  # Size of the UDP buffer. If you are handling lots of metrics you really
  # should bump it up to e.g. 10MB or even higher.
  # buffer = 10485760
}
```


# Hadoop集群配置Ganglia

vi /hadoop-2.6.0/etc/hadoop/hadoop-metrics2.properties

```
namenode.sink.ganglia.servers=nna:8649

#datanode.sink.ganglia.servers=yourgangliahost_1:8649,yourgangliahost_2:8649

resourcemanager.sink.ganglia.servers=nna:8649

#nodemanager.sink.ganglia.servers=yourgangliahost_1:8649,yourgangliahost_2:8649

mrappmaster.sink.ganglia.servers=nna:8649

jobhistoryserver.sink.ganglia.servers=nna:8649

```

这里修改的是NameNode节点的内容，若是修改DataNode节点信息，内容如下所示：

```
#namenode.sink.ganglia.servers=nna:8649

datanode.sink.ganglia.servers=dn1:8649

#resourcemanager.sink.ganglia.servers=nna:8649

nodemanager.sink.ganglia.servers=dn1:8649

#mrappmaster.sink.ganglia.servers=nna:8649

#jobhistoryserver.sink.ganglia.servers=nna:8649

```


　　其他DN节点可以以此作为参考来进行修改。

　　另外，在配置完成后，若之前Hadoop集群是运行的，这里需要重启集群服务。

# hbase  metrics 配置

以 hbase-0.98为例，需要配置 hadoop-metrics2-hbase.properties

```
# syntax: [prefix].[source|sink].[instance].[options]  
# See javadoc of package-info.java for org.apache.hadoop.metrics2 for details  
  
#*.sink.file*.class=org.apache.hadoop.metrics2.sink.FileSink  
default sampling period  
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
  
*.sink.ganglia.class=org.apache.hadoop.metrics2.sink.ganglia.GangliaSink31    
*.sink.ganglia.period=10    
  
hbase.sink.ganglia.period=10    
hbase.sink.ganglia.servers=172.18.144.198:8648

```

ganglia 3.1及以上版本需要用这个类：org.apache.hadoop.metrics2.sink.ganglia.GangliaSink31





# 启动、预览Ganglia


Ganglia的启动命令有start、restart以及stop，这里我们分别在各个节点启动相应的服务，各个节点需要启动的服务如下：

* NNA节点：

```
[hadoop@nna ~]$ service gmetad start
[hadoop@nna ~]$ service gmond start
[hadoop@nna ~]$ service httpd start

```


* NNS节点：

```
[hadoop@nns ~]$ service gmond start
```

* DN1节点：

```
[hadoop@dn1 ~]$ service gmond start
```

* DN2节点：

```
[hadoop@dn2 ~]$ service gmond start
```

* DN3节点：

```
[hadoop@dn3 ~]$ service gmond start
```


# web界面说明


![](/images/ganglia/web_cluster.jpg)

选择了集群之后

![](/images/ganglia/web_cluster2.jpg)


查看对应的机器

![](/images/ganglia/web_cluster3.jpg)



![](/images/ganglia/web_cluster4.jpg)



参看：

http://blog.csdn.net/K_James/article/details/38117873
https://my.oschina.net/vieky/blog/98770
http://www.cnblogs.com/wukenaihe/archive/2013/03/21/2972837.html
http://www.cnblogs.com/smartloli/p/4519914.html


# 注意

对于Hbase，你应该更多的关注的是下列这些图表

* CPU及内存使用情况
* JVM GC计数和时间
* Hbase RegionServer合并队列的长度
* HBase RegionServer写磁盘队列的长度

例如：合并队列的长度可以说明在regionServer服务器中有多少store正在排队等待合并，该值通常应当很低（每台RegionServer最多为几十个），当服务器过载或有出现I/O问题时，该图就会出现一个突发的峰值






