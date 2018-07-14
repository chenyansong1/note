---
title: zk的几个基本的概念介绍
categories: hadoop   
tags: [zookeeper]
---



# 1.集群角色（Leader、Follower、Observer）
1. Leader：为客户端提供读写服务，提供事物请求（增删改），只有leader会提供同步的服务
2. Follower：仅提供读服务，所有写服务都需要转交给Leader角色，另外参与选举
3. Observer：仅提供读服务，不参与选举，一般是为了增强zk集群的读请求并发能力

# 2.会话（session）
1. zk的客户端与zk的服务端之间的连接
2. 通过心跳检测保持客户端连接的存活
3. 接收来自服务端的watch事件通知
4. 可以设置超时时间



会话状态

![image-20180709200937276](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/session-status.png )






# 3.数据节点（znode)
1. 不是机器节点的意思
2. zk树形结构中的数据节点，用于存储数据
3. 持久节点：一旦创建，除非主动调用删除，否则一直存储在zk上
4. 临时节点：与客户端的会话绑定，一旦客户端会话失效，这个客户端创建的所有临时节点都会被移除
5. sequential znode（顺序节点）：创建节点时，如果设置属性sequential，则会自动在节点名后面追加一个整形的数字


# 4.版本
1. Version：当前znode的版本
2. Cversion：当前znode的子节点的版本
3. Aversion：当前znode的ACL（访问控制）版本


# 5.Watcher
1. 作用于znode的节点上
2. 多种事件通知，数据更新、子节点状态等会触发





# 6.ACL（访问控制列表）
1. Access Control Lists
2. 类似于Linux的权限控制
3. CREATE：创建子节点的权限
4. READ：获取子节点的权限
5. WRITE：更新节点数据的权限
6. DELETE：删除子节点的权限
7. ADMIN：设置acl的权限

>CREATE和DELETE是针对子节点的权限控制

