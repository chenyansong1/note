---
title: zookeeper的Znode数据模型
categories: hadoop   
tags: [zookeeper]
---


# 1.zkdatabase、datatree、datanode的关系



![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/znode_structure/1.png)


## 1.1.DataTree
整个zk的数据就靠datatree维护，包括数据、目录、权限, DataTree是**内存数据**存储的核心，是一个树结构，代表了内存中一份完整的数据。DataTree不包含任何与网络、客户端连接及请求处理相关的业务逻辑，是一个独立的组件, 默认初始化三目录


## 1.2.DataNode

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/znode_structure/2.jpg)

1. 树形结构中的每个节点叫做做Znode
2. 使用路径来引用一个节点，节点的路径是绝对的，没有相对路径，如：/app1是一个节点，/app1/p_1是一个节点
3. DataNode是数据存储的最小单元，其内部除了保存了结点的数据内容、ACL列表、节点状态之外，还记录了父节点的引用和子节点列表两个属性，其也提供了对子节点列表进行操作的接口。
 

## 1.3. ZKDatabase
Zookeeper的内存数据库，管理Zookeeper的所有会话、DataTree存储和事务日志。ZKDatabase会定时向磁盘dump快照数据，同时在Zookeeper启动时，会通过磁盘的事务日志和快照文件恢复成一个完整的内存数据库。
　　


