---
title: hive交互式命令行中的一些常用操作
categories: hive  
tags: [hive]
---



# hive中常用的操作语句

```
create table student(id int, name string) 
ROW FORMAT delimited 
fields terminated by '\t'
lines terminatd by '\n'
stored as textfile;



ROW FORMAT 表示行格式化
delimited 表示行加上限制
fields terminated by '\t'
类似的限制还有:
lines terminated by '\n'



#加载数据
load data local inpath 'xx.txt' into table db_hive.student;

#通常在表的名称前面加上数据库名称,这样,避免我们忘记使用use db_hive

select *from student;



#查看表的结构

desc formatted student;

#创建数据库
show databases;

create database db_hive;

use db_hive;

desc test_table;



desc extended test_table;

#相对于上面一个,显示的格式更加让我们容易观察
desc formatted test_table;


#hive中提供的函数
show functions;

#看一个函数怎么使用,如upper函数
desc function upper

#可以看到函数的使用例子
desc function extended upper




```

# 在 hive cli命令窗口中如何查看HDFS文件系统

```

#可以在Hive命令行中直接操作HDFS
hive (default)> dfs -ls /user/hive/warehouse;
Found 5 items
drwxr-xr-x   - root supergroup          0 2016-11-27 23:34 /user/hive/warehouse/mytable1
drwxr-xr-x   - root supergroup          0 2016-11-28 13:49 /user/hive/warehouse/stu_buck
drwxr-xr-x   - root supergroup          0 2016-11-28 10:12 /user/hive/warehouse/student
drwxr-xr-x   - root supergroup          0 2017-04-23 16:05 /user/hive/warehouse/test_database.db
drwxr-xr-x   - root supergroup          0 2017-04-23 15:00 /user/hive/warehouse/test_table


hive (default)> dfs -text /user/hive/warehouse/test_table/student.txt;
11,zhangsan
22,lisi
33,wangwu

```

# 在hive cli命令窗口中如何查看本地(linux)文件系统

```

# 在hive shell中操作linux文件系统的命令(在命令前加一个!号)

使用上述命令就可以在hive shell中不用退出的情况下使用linux命令

hive (default)> ! ls /etc/hosts;
/etc/hosts
hive (default)> 

#注意不要使用命令的别名
hive (default)> ! ll /etc/hosts;
Exception raised from Shell command Failed to execute  ll /etc/hosts

hive (default)> ! cat /tmp/hivef.txt;
test_table.id   test_table.name
11      zhangsan
22      lisi
33      wangwu
11      zhangsan
22      lisi
33      wangwu


```


# 在hive cli中的所有的历史命令

```
#在当前用户的目录下有一个 .hivehistory 文件,其中就是我们在hive cli中的所有的操作

[root@hdp-node-01 ~]# pwd
/root
[root@hdp-node-01 ~]# cat .hivehistory 
show databases;
quit
;
create database hadoop_hive;
use hadoop_hive;
create table student(id int,name string);
show tables;
.....



```