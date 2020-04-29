---
title: sqoop介绍及安装
categories: sqoop   
toc: true  
tag: [sqoop]
---


# 1.概述
sqoop是apache旗下一款“Hadoop和关系数据库服务器之间传送数据”的工具。
* 导入数据：MySQL，Oracle导入数据到Hadoop的HDFS、HIVE、HBASE等数据存储系统；
* 导出数据：从Hadoop的文件系统中导出数据到关系数据库

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/sqoop/structure.png)


<!--more-->

# 2.工作机制
&emsp;将导入或导出命令翻译成mapreduce程序来实现，在翻译出的mapreduce中主要是对inputformat和outputformat进行定制



# 3.sqoop安装
```
'解压'
tar -zxvf sqoop-1.4.6.bin__hadoop-2.0.4-alpha.tar.gz 

'修改配置文件'
$ cd $SQOOP_HOME/conf
$ mv sqoop-env-template.sh sqoop-env.sh
打开sqoop-env.sh并编辑下面几行：
export HADOOP_COMMON_HOME=/home/hadoop/apps/hadoop-2.6.1/
export HADOOP_MAPRED_HOME=/home/hadoop/apps/hadoop-2.6.1/
export HIVE_HOME=/home/hadoop/apps/hive-1.2.1


'加入mysql的jdbc驱动包'
cp  ~/app/hive/lib/mysql-connector-java-5.1.28.jar   $SQOOP_HOME/lib/


'验证启动'
$ cd $SQOOP_HOME/bin
$ sqoop-version
预期的输出：
15/12/17 14:52:32 INFO sqoop.Sqoop: Running Sqoop version: 1.4.6
Sqoop 1.4.6 git commit id 5b34accaca7de251fc91161733f906af2eddbe83
Compiled by abe on Fri Aug 1 11:19:26 PDT 2015
到这里，整个Sqoop安装工作完成。


```