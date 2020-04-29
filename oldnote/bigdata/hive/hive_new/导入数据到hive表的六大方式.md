---
title: 导入数据到hive表的六大方式
categories: hive  
tags: [hive]
---

# 加载文件到表中

```
load data [local] inpath 'filepath' [overwrite] into table tablename [partition (partition1=val1, partition2=val2...)]

#原始文件存储的位置
1.本地: 此时需要加上local
2.hdfs: 那么什么不用加

#对表中的数据是否覆盖
1.覆盖:overwrite
2.追加:不用overwrite

#分区表需要加载上:partition (partition1=val1, partition2=val2...)


```


# 加载本地文件到hive表

```
load data local inpath 'xx/emp.txt' into table default.emp ;
```

# 加载hdfs文件到hive表

```
load data inpath '/user/hive/xx/emp.txt' into table default.emp ;
```

# 加载数据覆盖表中已有的数据

```
load data inpath '/user/hive/xx/emp.txt' overwrite into table default.emp ;

```

# 通过as创建表的时候,加载数据

```
create table if not exists default.emp_as 
as
select * from default.emp ;

```

# 创建表的时通过insert加载

```
create table if not exists default.emp_cli like emp ;
insert into table default.emp_cli select * from default.emp ;

```

# 创建表的时候通过location指定加载

```
create external table if not exists default.emp_external
(
id int,
name string
)
comment 'external table'
row format delimited 
fields terminated by ','
location 'xx.txt'  ;

因为location中的数据已经存在,那么在我们创建表的时候就直接有了表的数据


```



# 对于hdfs上的数据,加载完之后就会被删除
```
#将数据上传到hdfs
hive (default)> dfs -put /home/hadoop/app/hive/script/employee2.txt /user/; 
hive (default)> dfs -ls /user/ ;
Found 3 items
-rw-r--r--   3 root supergroup        652 2017-04-24 00:59 /user/employee2.txt
drwxr-xr-x   - root supergroup          0 2016-11-27 23:34 /user/hive
drwxr-xr-x   - root supergroup          0 2017-04-23 23:23 /user/zhangsan

#加载数到hive表
hive (default)> load data inpath '/user/employee2.txt' into table emp ;

#再次查看hdfs中的数据(数据被删除了)
hive (default)> dfs -ls /user/ ;
Found 2 items
drwxr-xr-x   - root supergroup          0 2016-11-27 23:34 /user/hive
drwxr-xr-x   - root supergroup          0 2017-04-23 23:23 /user/zhangsan
hive (default)> 

```
