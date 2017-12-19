# Hbase Regioner Server内存大小

待整理：

翻译：http://hadoop-hbase.blogspot.com/2013/01/hbase-region-server-memory-sizing.html

与仅针对磁盘大小和吞吐量进行优化的纯存储机器不同，HBase RegionServer还是一个计算节点。

RegionSize / MemstoreSize *
ReplicationFactor * HeapFractionForMemstores

参见：
http://hbasefly.com/2016/08/22/hbase-practise-cluster-planning/
