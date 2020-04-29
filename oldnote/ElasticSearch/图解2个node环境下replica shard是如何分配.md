---
title: 图解2个node环境下replica shard是如何分配
categories: elasticsearch   
toc: true  
tag: [elasticsearch]
---


如果只有一个节点,那么只会在节点上分配primary shard,当我们加入一个节点的时候,会在新增的节点上加入replica shard,此时会将primary shard的数据拷贝到replica shard上,由primary shard和replica shard共同承担读请求


（1）replica shard分配：3个primary shard，3个replica shard，1 node
（2）primary ---> replica同步
（3）读请求：primary/replica共同承担

![](/assert/img/ES/two_node_primary_shard_replica_shard.png)

