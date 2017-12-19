---
title: datanode的工作机制
categories: hadoop   
toc: true  
tag: [hadoop]
---



# 1.问题场景
1. 集群容量不够，怎么扩容？
2. 如果有一些datanode宕机，该怎么办？
3. datanode明明已启动，但是集群中的可用datanode列表中就是没有，怎么办？

<!--more-->


# 2.Datanode工作职责
1. 存储管理用户的文件块数据
2. 定期向namenode汇报自身所持有的block信息（通过心跳信息上报）（这点很重要，因为，当集群中发生某些block副本失效时，集群如何恢复block初始副本数量的问题）

```
<property>
   <name>dfs.blockreport.intervalMsec</name>
   <value>3600000</value>
   <description>Determines block reporting interval in milliseconds.</description>
</property>

```

# 3.Datanode掉线判断时限参数
datanode进程死亡或者网络故障造成datanode无法与namenode通信，namenode不会立即把该节点判定为死亡，要经过一段时间，这段时间暂称作超时时长。HDFS默认的超时时长为10分钟+30秒。如果定义超时时间为timeout，则超时时长的计算公式为：
```
timeout  = 2 * heartbeat.recheck.interval + 10 * dfs.heartbeat.interval
```
而默认的heartbeat.recheck.interval 大小为5分钟，dfs.heartbeat.interval默认为3秒,需要注意的是hdfs-site.xml 配置文件中的heartbeat.recheck.interval的单位为毫秒，dfs.heartbeat.interval的单位为秒。所以，举个例子，如果heartbeat.recheck.interval设置为5000（毫秒），dfs.heartbeat.interval设置为3（秒，默认），则总的超时时间为40秒
```
<property>
        <name>heartbeat.recheck.interval</name>
        <value>2000</value>
</property>
<property>
        <name>dfs.heartbeat.interval</name>
        <value>1</value>
</property>
```


**HDFS冗余数据块的自动删除**

在日常维护hadoop集群的过程中发现这样一种情况：
某个节点由于网络故障或者DataNode进程死亡，被NameNode判定为死亡，HDFS马上自动开始数据块的容错拷贝；当该节点重新添加到集群中时，由于该节点上的数据其实并没有损坏，所以造成了HDFS上某些block的备份数超过了设定的备份数。通过观察发现，这些多余的数据块经过很长的一段时间才会被完全删除掉，那么这个时间取决于什么呢？
该时间的长短跟数据块报告的间隔时间有关。Datanode会定期将当前该结点上所有的BLOCK信息报告给Namenode，参数dfs.blockreport.intervalMsec就是控制这个报告间隔的参数。
hdfs-site.xml文件中有一个参数：
```
<property>
	<name>dfs.blockreport.intervalMsec</name>
	<value>3600000</value>
	<description>Determines block reporting interval in milliseconds.</description>
</property>
```
其中3600000为默认设置，3600000毫秒，即1个小时，也就是说，块报告的时间间隔为1个小时，所以经过了很长时间这些多余的块才被删除掉。通过实际测试发现，当把该参数调整的稍小一点的时候（60秒），多余的数据块确实很快就被删除了。




# 4.datanode不被namenode识别的问题
namenode在format初始化的时候会形成两个标识：blockPoolId、clusterId, 新的datanode加入的时候，会获取这两个标识作为自己的工作目录中你的标识，一旦namenode重新format后，namenode的身份标识已变，而datanode持有原来的id，就不会被namenode识别
 
>修改了name node 的存储目录

&emsp;如果修改了name node的存储目录，那么需要重新格式化name node ，此时和原来关联的data node的工作目录中的数据将不能使用了，因为原来data node中的数据version是和name node 关联的，所以此时需要将data node中的数据删除，因为name node是记录data node中的数据的，所以如果name node 重新初始化了，那么data node留着就没有意义了
