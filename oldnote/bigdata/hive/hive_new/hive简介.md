---
title: hive简介
categories: hive  
tags: [hive]
---



# 什么是Hive？

* Hive是基于Hadoop的一个数据仓库工具，可以将结构化的数据文件映射成一张表，并提供类SQL查询功能；

<!--more-->

* 构建在Hadoop之上的数据仓库
	* 使用HQL作为查询接口
	* 使用HDFS存储
	* 使用MapReduce计算

* 本质是： 将HQL转化成MapReduce程序
* 适合离线数据处理

# Hive 架构

![](/assert/img/bigdata/hive/hive_new/hive_jiagou.png)



* 用户接口: Client
CLI(hive shell)、 JDBC/ODBC(java访问hive)， WEBUI(浏览器访问hive)

* 元数据: Metastore
元数据包括：表名、表所属的数据库（默认是default）、表的拥有者、列/分区字段、表的类型（是否是外部表）、表的数据所在目录等；

默认存储在自带的derby数据库中，推荐使用采用MySQL存储Metastore；

* Hadoop

使用HDFS进行存储，使用MapReduce进行计算；

* 驱动器: Driver

包含：解析器、编译器、优化器、执行器；

解析器：将SQL字符串转换成抽象语法树AST，这一步一般都用第三方工具库完成，比如antlr；对AST进行语法分析，比如表是否存在、字段是否存在、 SQL语义是否有误(比如select中被判定为聚合的字段在group by中是否有出现)

编译器：将AST编译生成逻辑执行计划

优化器：对逻辑执行计划进行优化；

执行器：把逻辑执行计划转换成可以运行的物理计划。对于Hive来说，就是MR/TEZ/Spark
