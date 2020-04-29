---
title: hive中的优化
categories: hive  
tags: [hive]
---



# hive.fetch.task.conversion
有些情况不走MapReduce,这样可以更快

```
vim conf/hive-site.xml

  <property>
    <name>hive.fetch.task.conversion</name>
    <value>more</value>
    <description>
	对于当前查询只是单表,没有子查询和聚合以及distinct和join下面的情况不会走mapreduce
	  Some select queries can be converted to single FETCH task minimizing latency.
      Currently the query should be single sourced not having any subquery and should not have
      any aggregations or distincts (which incurs RS), lateral views and joins.
      0. none : disable hive.fetch.task.conversion
      1. minimal : SELECT STAR, FILTER on partition columns, LIMIT only
      2. more    : SELECT, FILTER, LIMIT only (support TABLESAMPLE and virtual columns)
    </description>
  </property>



1.minimal 对于select* 和where条件是分区字段以及limit等不会走MapReduce

select * from test;
select * from test where monday='201509' ; (此时test表有monday='201509'这个分区)
select * from test limit 5;

2.more    : SELECT, FILTER, LIMIT only (support TABLESAMPLE and virtual columns)
	SELECT,filter,limit不走MapReduce

select job, ename from emp ;
select job, ename from emp_partition where ename='SMITH';

```


# 大表的拆分

将大表(字段很多的表),我们只需要对其中的某些字段进行操作,那么我们可以创建子表(取我们需要的字段)

```
create table if not exists test_sub
row format delimited fields terminated by ','
stored as orc
as 
select id,name from test

```


# 外部表和分区表

通常这两个表是结合使用的

```
create external table if not exists test_sub
(
id int,
name string
)
comment 'xx'
partitioned by (month string, day string)
row format delimited fields tereminated by ','

location 'xx.txt' ;


```


# 数据的存储格式和数据的压缩

* 存储格式:orcfitle,parquet
* 数据压缩:snappy


# sql优化

* 优化sql语句


# join

* Reduce join
大表对大表:每个表的数据都是从文件中读取的

![](/assert/img/bigdata/hive/hive_new/hive_optimer.png)





* Map join

```
#如果设置了这个参数,那么程序会识别map join
set hive.auto.convert.join=true ;

```

小表对大表;大表的数据是从文件从读取的,小表的数据放入到内存中
DistributedCache 类将小表缓存起来


* SMB join

Sort-Merge-bucket join 是对上面的大表对大表的优化策略

![](/assert/img/bigdata/hive/hive_new/hive_optimer_bucket.png)


```
set hive.auto.convert.sortmerge.join=true;
set hive.optimize.bucketmapjoin = true;
set hive.optimize.bucketmapjoin.sortedmerge=true;
```



group by出现数据倾斜的问题:
对于group by (col_name) ,如果进行group的col_name字段很多为空,那么会出现数据倾斜的问题,此时最好将该字段变成非空,然后就不会数据倾斜


count(distinct xx)也是会出现数据倾斜的问题


# 执行计划

```
explain select * from emp ;

explain select deptno, avg(sal) avg_sal from emp group by deptno;

explain extended select deptno, avg(sal) avg_sal from emp group by deptno;

```


# 并行执行

```
job1 a join b ==> aa

job2 c join d ==> cc

job3 aa join cc

#因为job1和job2没有依赖关系,所以可以并行执行

```

可以设置如下的参数进行配置

```
  <property>
    <name>hive.exec.parallel</name>
    <value>false</value>
    <description>Whether to execute jobs in parallel</description>
  </property>
  <property>
    <name>hive.exec.parallel.thread.number</name>
    <value>8</value>
    <description>How many jobs at most can be executed in parallel</description>
  </property>
```


# jvm重用

```
hive>set mapreduce.job.jvm.numtasks;

#可以设置jvm中跑多少个MapReduce

```

# reduce数目

```

set mapreduce.job.reduces;

```


# 推测执行

关掉推测执行,不然有的时候,对同一个任务会启动两个MapReduce
```
0: jdbc:hive2://hdp-node-01:10000> set hive.mapred.reduce.tasks.speculative.execution=false;

0: jdbc:hive2://hdp-node-01:10000> set mapreduce.reduce.speculative=false;
0: jdbc:hive2://hdp-node-01:10000> set mapreduce.map.speculative=false;


```


# 动态分区

# Strict Mode

在分区表进行查询,在where子句中没有加分区过滤的话,将禁止提交任务(默认是nostrict)

```
0: jdbc:hive2://hdp-node-01:10000> set hive.mapred.mode ;
+-----------------------------+--+
|             set             |
+-----------------------------+--+
| hive.mapred.mode=nonstrict  |
+-----------------------------+--+
1 row selected (0.017 seconds)
0: jdbc:hive2://hdp-node-01:10000> set hive.mapred.mode=strict;
No rows affected (0.016 seconds)
0: jdbc:hive2://hdp-node-01:10000> set hive.mapred.mode ;
+--------------------------+--+
|           set            |
+--------------------------+--+
| hive.mapred.mode=strict  |
+--------------------------+--+
1 row selected (0.01 seconds)

#对分区表执行查询数据
0: jdbc:hive2://hdp-node-01:10000> select * from emp_partition;
Error: Error while compiling statement: FAILED: SemanticException [Error 10041]: No partition predicate found for Alias "emp_partition" Table "emp_partition" (state=42000,code=10041)
0: jdbc:hive2://hdp-node-01:10000> 



0: jdbc:hive2://hdp-node-01:10000> show partitions emp_partition;
+---------------+--+
|   partition   |
+---------------+--+
| month=201509  |
| month=201510  |
| month=201511  |
+---------------+--+
3 rows selected (0.165 seconds)
0: jdbc:hive2://hdp-node-01:10000> select * from emp_partition where month='201509' limit 3;
+----------------------+----------------------+--------------------+--------------------+-------------------------+--------------------+---------------------+-----------------------+----------------------+--+
| emp_partition.empno  | emp_partition.ename  | emp_partition.job  | emp_partition.mgr  | emp_partition.hiredate  | emp_partition.sal  | emp_partition.comm  | emp_partition.deptno  | emp_partition.month  |
+----------------------+----------------------+--------------------+--------------------+-------------------------+--------------------+---------------------+-----------------------+----------------------+--+
| 7369                 | SMITH                | CLERK              | 7902               | 1980-12-17              | 800.0              | NULL                | 20                    | 201509               |
| 7499                 | ALLEN                | SALESMAN           | 7698               | 1981-2-20               | 1600.0             | 300.0               | 30                    | 201509               |
| 7521                 | WARD                 | SALESMAN           | 7698               | 1981-2-22               | 1250.0             | 500.0               | 30                    | 201509               |
+----------------------+----------------------+--------------------+--------------------+-------------------------+--------------------+---------------------+-----------------------+----------------------+--+
3 rows selected (0.19 seconds)
0: jdbc:hive2://hdp-node-01:10000>


```

使用严格模式可以禁止3种类型的查询:
* 对于分区表,不加分区字段过滤条件的不能执行
* 对于order by, 没有使用limit的不能执行
* 对于笛卡尔积的查询,使用where但是没有使用on的不能进行查询




