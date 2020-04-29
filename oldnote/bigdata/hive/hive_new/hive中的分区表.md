---
title: hive中的分区表
categories: hive  
tags: [hive]
---


分区表实际上就是对应一个hdfs文件系统上的独立的文件夹,该文件夹下是该分区所有的数据文件,hive中的分区就是分目录,把一个大的数据集根据业务需求要分割成更小的数据集

<!--more-->

在查询时通过where子句中的表达式来选择查询所需要的指定的分区,这样的查询效率会提高很多

```
create table dept_partition
(
deptno int,
dname string,
loc string
)
comment 'partition table'
partitioned by (event_month string)
row format delimited 
fields terminated by ',' ;


create external table if not exists default.emp_partition
(
empno int,
ename string,
job string,
mgr int,
hiredate string,
sal double,
comm double,
deptno int
)
comment 'emp partition table'
partitioned by (month string)
row format delimited
fields terminated by ','  ;



#查看分区的信息
hive (default)> desc formatted emp_partition;
OK
col_name        data_type       comment
# col_name              data_type               comment             
                 
empno                   int                                         
ename                   string                                      
job                     string                                      
mgr                     int                                         
hiredate                string                                      
sal                     double                                      
comm                    double                                      
deptno                  int                                         

#这里可以看到分区的字段名和字段类型                 
# Partition Information          
# col_name              data_type               comment             
                 
month                   string                                      
                 
.....
hive (default)> 


```


# 加载数据到分区表

```
load data local inpath '/home/hadoop/app/hive/script/employee2.txt' into table emp_partition partition (month='201509')


hive (default)> dfs -ls /user/hive/warehouse/emp_partition/ ;
Found 1 items
drwxr-xr-x   - root supergroup          0 2017-04-24 00:10 /user/hive/warehouse/emp_partition/month=201509


```

# 插叙分区表中的数据

```
select * from emp_partition where month='201509' ;


```

# 指定多个分区字段
```
create external table if not exists default.emp_partition
(
empno int,
ename string,
job string,
mgr int,
hiredate string,
sal double,
comm double,
deptno int
)
comment 'emp partition table'
partitioned by (month string, day string)
row format delimited
fields terminated by ','  ;

#加载数据
load data local inpath '/home/hadoop/app/hive/script/employee2.txt' into table emp_partition partition (month='201509', day='13')


#查询数据
select * from emp_partition where month='201509' and day='13' ;

```



# 注意事项

```

create table if not exists default.dept_nopartition
(
deptno int,
dname string,
loc string
)
comment 'dept no partition table'
row format delimited
fields terminated by ','  ;

hive>dfs -put /xx/dept.txt /user/hive/warehouse/dept_nopartition ;

select * from dept_nopartition
#此时是有数据的

----------------------------------

create table if not exists default.dept_partition
(
deptno int,
dname string,
loc string
)
comment 'dept no partition table'
partitioned by (day string)
row format delimited
fields terminated by ','  ;


hive>dfs -mkdir -p /user/hive/warehouse/dept_partition/day=20150913 ;

hive>dfs -put /xx/dept.txt /user/hive/dept_partition/day=20150913 ;

hive>select * from dept_partition
#此时没有数据

登录到mysql中去看,表的元数据信息,
mysql>select * from paritions;
#可以看到其中并没有表dept_partition对应的分区信息,所以当我们select查询的时候,根本不知道分区的存在


既然数据我们已经手动放入了我们创建的分区目录中,那么我们又要查询到数据,怎么解决呢?

#方式一:
hive>msck repair table dept_partition
OK
Partitions not in metastore: dept_part:day=20150913
Repair: Added partition to metastore dept_part:day=20150913

此时再去mysql中查看表的分区信息
mysql>select * from paritions;
#此时就可以看到分区信息了


#方式二:
hive>dfs -mkdir -p /user/hive/warehouse/dept_partition/day=20150914 ;
hive>dfs -put /xx/dept.txt /user/hive/dept_partition/day=20150914 ;

解决:
alter table dept_partition add partition(day='20150914') ;

此时查看mysql中的分区信息
mysql>select * from paritions;
#此时就可以看到分区信息了



```
# 查看表的所有的分区
```
hive (default)> show partitions emp_partition;
OK
partition
month=201509
month=201510
month=201511
Time taken: 0.137 seconds, Fetched: 3 row(s)
hive (default)> 


```
