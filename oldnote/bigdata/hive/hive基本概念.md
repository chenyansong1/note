---
title: hive基本概念
categories: hive   
toc: true  
tag: [hive]
---



# 1.架构图


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hive/structure/1.jpg)


Jobtracker是hadoop1.x中的组件，它的功能相当于： Resourcemanager+AppMaster，TaskTracker 相当于：  Nodemanager  +  yarnchild

<!--more-->

# 2.基本组成
* 用户接口：包括 CLI、JDBC/ODBC、WebGUI。
* 元数据存储：通常是存储在关系数据库如 mysql , derby中。
* 解释器、编译器、优化器、执行器。




# 3.各组件的基本功能
* 用户接口主要由三个：CLI、JDBC/ODBC和WebGUI。其中，CLI为shell命令行；JDBC/ODBC是Hive的JAVA实现，与传统数据库JDBC类似；WebGUI是通过浏览器访问Hive。
* 元数据存储：Hive 将元数据存储在数据库中。Hive 中的元数据包括表的名字，表的列和分区及其属性，表的属性（是否为外部表等），表的数据所在目录等。
* 解释器、编译器、优化器完成 HQL 查询语句从词法分析、语法分析、编译、优化以及查询计划的生成。生成的查询计划存储在 HDFS 中，并在随后有 MapReduce 调用执行。


# 4.Hive与Hadoop的关系
&emsp;Hive利用HDFS存储数据，利用MapReduce查询数据


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hive/structure/2.jpg)


# 4.Hive与传统数据库对比


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hive/structure/3.jpg)

 hive具有sql数据库的外表，但应用场景完全不同，hive只适合用来做批量数据统计分析


# 5.Hive的数据存储

1. Hive中所有的数据都存储在 HDFS 中，没有专门的数据存储格式（可支持Text，SequenceFile，ParquetFile，RCFILE等）
2. 只需要在创建表的时候告诉 Hive 数据中的列分隔符和行分隔符，Hive 就可以解析数据。
3. Hive 中包含以下数据模型：DB、Table，External Table，Partition，Bucket。
* db：在hdfs中表现为${hive.metastore.warehouse.dir}目录下一个文件夹
* table：在hdfs中表现所属db目录下一个文件夹
* external table：与table类似，不过其数据存放位置可以在任意指定路径
* partition：在hdfs中表现为table目录下的子目录
* bucket：在hdfs中表现为同一个表目录下根据hash散列之后的多个文件


