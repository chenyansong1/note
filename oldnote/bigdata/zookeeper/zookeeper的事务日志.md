---
title: zookeeper的事务日志
categories: hadoop   
tags: [zookeeper]
---




# 日志文件的存储路径

* 存储于datalog或者是dataLogDir配置目录
* 对应目录下的version-2代表的是日志格式版本号
* 日志文件命名
	* 文件大小都是64m
	* 后缀都是16进制格式数字,逐渐增大,其本质是本日志文件的第一条zxid号


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/log/1.png)



# 日志格式
* zk提供了工具类org.apache.zookeeper.server. LogFormatter解析日志的内容

* 第一行是日志格式信息

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/log/2.png)




# 日志写入
* Zk通过类org.apache.zookeeper.server.persistence. FileTxnLog实现对事务日志的管理
	* 通过append方法来添加事务日志
* 写入过程
	* 确定是否有事务日志文件可写,当第一次创建事务日志文件或者上一个事务日志文件写满后都会关闭这个文件流
	* 确定事务日志是否需要扩容,当文件剩余空间不足4KB时,把文件新增64MB(新增一个日志文件),用0填充剩余的空间
	* 事务序列化
	* 生成checksum
	* 写入事务日志文件流
	* 事务日志刷入磁盘,本质是调用系统的fsync接口


# 数据快照
* zk某个时刻的完整数据
* 快照文件的后缀为服务器最新的zxid
* 通过工具类SnapshotFormatter可以查看快照文件的文件内容

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/log/3.png)


# 快照流程
* 确定是否需要进行数据快照
	* snapCount默认是100000,表示达到这个数量的日志才开始进行快照
	* 为了避免集群节点同时进行快照,按照如下公式触发快照:
	logCount >(snapCount/2+randRoll) //randRoll是为1---snapCount/2之间的随机数
* 切换事务日志文件
	* 创建新的事务日志文件
* 创建数据快照异步线程
* 获取全量数据和会话信息
* 生成快照数据文件
* 把数据刷入快照文件


















