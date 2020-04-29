---
title: namenode工作机制
categories: hadoop   
toc: true  
tag: [hadoop]
---




# 1.namenode 的职责
1. 负责客户端请求的响应
2. 元数据的管理（查询，修改）



# 2.namenode对数据的管理形式
namenode对数据的管理采用了三种存储形式:
1.内存元数据(NameSystem)
2.磁盘元数据镜像文件(Image)
3.数据操作日志文件（可通过日志运算出元数据）(edits)

<!--more-->

# 3.元数据存储机制
A、内存中有一份完整的元数据(<font color=red>内存meta data</font>)
B、磁盘有一个“准完整”的元数据镜像（fsimage）文件(<font color=red>在namenode的工作目录中</font>)
C、用于衔接内存metadata和持久化元数据镜像fsimage之间的操作日志（<font color=red>edits文件</font>）
注：当客户端对hdfs中的文件进行新增或者修改操作，操作记录首先被记入edits日志文件中，当客户端操作成功后，相应的元数据会更新到内存meta.data中


# 4.元数据手动查看
可以通过hdfs的一个工具来查看edits中的信息
```
bin/hdfs oev -i edits -o edits.xml
bin/hdfs oiv -i fsimage_0000000000000000087 -p XML -o fsimage.xml

```

# 5.元数据的checkpoint
每隔一段时间，会由secondary namenode将namenode上积累的所有edits和一个最新的fsimage下载到本地(fsimage只会下载一次)，并加载到内存进行merge（这个过程称为checkpoint）

## 5.1.checkpoint的详细过程

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/namenode/secondarynamenode_checkpoint.png)
 

## 5.2.checkpoint操作的触发条件配置参数

```
dfs.namenode.checkpoint.check.period=60  #检查触发条件是否满足的频率，60秒
dfs.namenode.checkpoint.dir=file://${hadoop.tmp.dir}/dfs/namesecondary      #以上两个参数做checkpoint操作时，secondary namenode的本地工作目录
dfs.namenode.checkpoint.edits.dir=${dfs.namenode.checkpoint.dir}
 
dfs.namenode.checkpoint.max-retries=3  #最大重试次数
dfs.namenode.checkpoint.period=3600  #两次checkpoint之间的时间间隔3600秒
dfs.namenode.checkpoint.txns=1000000 #两次checkpoint之间最大的操作记录

```

## 5.3.checkpoint的附带作用（元数据恢复）
&emsp;namenode和secondary namenode的工作目录存储结构完全相同，所以，当namenode故障退出需要重新恢复时，可以从secondary namenode的工作目录中将fsimage拷贝到namenode的工作目录，以恢复namenode的元数据

参见: namenode目录故障时解决方案.md

# 6.元数据目录说明
## 6.1.总体结构
在第一次部署好Hadoop集群的时候，我们需要在NameNode（NN）节点上格式化磁盘：
```
$HADOOP_HOME/bin/hdfs namenode -format
```

格式化完成之后，将会在$dfs.namenode.name.dir/current目录下如下的文件结构
```
current/
|-- VERSION
|-- edits_*
|-- fsimage_0000000000008547077
|-- fsimage_0000000000008547077.md5
`-- seen_txid

```
其中的dfs.namenode.dir是在hdfs-site.xml文件中配置的，默认值如下：
```
<property>
  <name>dfs.namenode.name.dir</name>
  <value>file://${hadoop.tmp.dir}/dfs/name</value>
</property>
 
hadoop.tmp.dir是在core-site.xml中配置的，默认值如下
<property>
  <name>hadoop.tmp.dir</name>
  <value>/tmp/hadoop-${user.name}</value>
  <description>A base for other temporary directories.</description>
</property>
```

dfs.namenode.name.dir属性可以配置多个目录:
```
/*
如/data1/dfs/name,/data2/dfs/name,/data3/dfs/name,....
各个目录存储的文件结构和内容都完全一样，相当于备份，这样做的好处是当其中一个目录损坏了，也不会影响到Hadoop的元数据，
特别是当其中一个目录是NFS（网络文件系统Network File System，NFS）之上，即使你这台机器损坏了，元数据也得到保存。
*/
```
## 6.2.VERSION文件
VERSION文件是Java属性文件，内容大致如下：

```
#Fri Nov 15 19:47:46 CST 2013
namespaceID=934548976                #namespaceID是文件系统的唯一标识符，在文件系统首次格式化之后生成的；
clusterID=CID-cdff7d73-93cd-4783-9399-0a22e6dce196
cTime=0        #cTime表示NameNode存储时间的创建时间，由于我的NameNode没有更新过，所以这里的记录值为0，以后对NameNode升级之后，cTime将会记录更新时间戳；
storageType=NAME_NODE        #storageType说明这个文件存储的是什么进程的数据结构信息（如果是DataNode，storageType=DATA_NODE）；
blockpoolID=BP-893790215-192.168.24.72-1383809616115
layoutVersion=-47    #layoutVersion表示HDFS永久性数据结构的版本信息， 只要数据结构变更，版本号也要递减，此时的HDFS也需要升级，否则磁盘仍旧是使用旧版本的数据结构，这会导致新版本的NameNode无法使用；

```
clusterID是系统生成或手动指定的集群ID，在-clusterid选项中可以使用它；如下说明
1. 使用如下命令格式化一个Namenode：$HADOOP_HOME/bin/hdfs namenode -format [-clusterId <cluster_id>]
&emsp;选择一个唯一的cluster_id，并且这个cluster_id不能与环境中其他集群有冲突。如果没有提供cluster_id，则会自动生成一个唯一的ClusterID。
2. 使用如下命令格式化其他Namenode：$HADOOP_HOME/bin/hdfs namenode -format -clusterId <cluster_id>
3. 升级集群至最新版本。在升级过程中需要提供一个ClusterID，例如：\$HADOOP_PREFIX_HOME/bin/hdfs start namenode --config $HADOOP_CONF_DIR  -upgrade -clusterId <cluster_ID>,如果没有提供ClusterID，则会自动生成一个ClusterID。



**blockpoolID**

是针对每一个Namespace所对应的blockpool的ID，上面的这个BP-893790215-192.168.24.72-1383809616115就是在我的ns1的namespace下的存储块池的ID，这个ID包括了其对应的NameNode节点的ip地址。

## 6.3.seen_txid文件

&emsp;非常重要，是存放transactionId的文件，format之后是0，它代表的是namenode里面的edits_*文件的尾数，namenode重启的时候，会按照seen_txid的数字，循序从头跑edits_0000001~到seen_txid的数字。所以当你的hdfs发生异常重启的时候，一定要比对seen_txid内的数字是不是你edits最后的尾数，不然会发生建置namenode时metaData的资料有缺少，导致误删Datanode上多余Block的资讯
&emsp;文件中记录的是edits滚动的序号，每次重启namenode时，namenode就知道要将哪些edits进行加载edits












