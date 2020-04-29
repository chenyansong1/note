---
title: hbase1.x之后的架构之META和ROOT
categories: hbase   
toc: true  
tag: [hbase]
---


原来是通过-ROOT-table找到.META. ，然后通过.META.表找到Register

现在：
直接向存放register的信息存放在hbase:meta （以前叫.META.） 而hbase:meta 表在zookeeper中存放着
The hbase:meta table (previously called .META.) keeps a list of all regions ，now stored in ZooKeeper.

The hbase:meta table structure is as follows:

* Key
	* Region key of the format ([table],[region start key],[region id])

* Values
	* info:regioninfo (serialized HRegionInfo instance for this region)
	* info:server (server:port of the RegionServer containing this region)
	* info:serverstartcode (start-time of the RegionServer process containing this region)


client
1. 通过找hbase:meta table 找到RegionServers 
2. After locating the required region(s), the client contacts the RegionServer serving that region, rather than going through the master
3. 找到的信心将被缓存在客户端，以便后续请求不需要经过查找过程。
4. 如果RegionServers 重新分配，或者是宕机，那么才需要重新查找










