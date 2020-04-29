---
title: SparkSQL之Hive On Spark
categories: spark  
tags: [spark]
---



hive是目前大数据领域,事实上的sql标准,底层默认是基于MapReduce实现的,但是由于MapReduce速度实在比较慢,因此,陆续出现了新的sql查询引擎,包括spark sql,hive on Taz,hive on spark等

spark sql与hive on spark是不一样的,spark sql是spark自己研发出来的针对各种数据源,包括hive,json,Parquet,jdbc,rdd等都可以执行查询的,一套基于spark计算引擎的查询引擎,因此它是spark的一个项目,只不过提供了针对hive执行查询的功能而已,适合在一些使用spark技术栈的大数据应用类系统中使用

而hive on spark,是hive的一个项目,它是指,不通过MapReduce作为唯一的查询引擎,而是将spark作为底层的查询引擎,hive on spark,只适用于hive,在可预见的未来,很有可能hive的默认的底层引擎就从MapReduce切换为spark了,适合于原有的hive数据仓库以及数据统计分析替换为spark引擎



hive on spark 环境搭建


1.安装hive
参见:"hive安装"一文


2.使用

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/hive_on_spark_1.png)


