---
title: Hive基本操作
categories: hive   
toc: true  
tag: [hive]
---

下面是对hive的DML和DDL操作

<!--more-->


# 1.DDL操作
## 1.1.创建表
### 1.1.1.建表语法
```
CREATE [EXTERNAL] TABLE [IF NOT EXISTS] table_name
   [(col_name data_type [COMMENT col_comment], ...)]
   [COMMENT table_comment]
   [PARTITIONED BY (col_name data_type [COMMENT col_comment], ...)]
   [CLUSTERED BY (col_name, col_name, ...)
   [SORTED BY (col_name [ASC|DESC], ...)] INTO num_buckets BUCKETS]
   [ROW FORMAT row_format]
   [STORED AS file_format]
   [LOCATION hdfs_path]

```
1. CREATE TABLE 创建一个指定名字的表。如果相同名字的表已经存在，则抛出异常；用户可以用 IF NOT EXISTS 选项来忽略这个异常。
3. LIKE 允许用户复制现有的表结构，但是不复制数据。
```
	ROW FORMAT DELIMITED
    [FIELDS TERMINATED BY char]
    [COLLECTION ITEMS TERMINATED BY char]
    [MAP KEYS TERMINATED BY char]
[LINES TERMINATED BY char]
   | SERDE serde_name [WITH SERDEPROPERTIES (property_name=property_value, property_name=property_value, ...)]

```
用户在建表的时候可以自定义 SerDe 或者使用自带的 SerDe。如果没有指定 ROW FORMAT 或者 ROW FORMAT DELIMITED，将会使用自带的 SerDe。在建表的时候，用户还需要为表指定列，用户在指定表的列的同时也会指定自定义的 SerDe，Hive通过 SerDe 确定表的具体的列的数据。
5. STORED AS  SEQUENCEFILE | TEXTFILE | RCFILE
如果文件数据是纯文本，可以使用 STORED AS TEXTFILE。如果数据需要压缩，使用 STORED AS SEQUENCEFILE。
6. CLUSTERED BY
对于每一个表（table）或者分区， Hive可以进一步组织成桶，也就是说桶是更为细粒度的数据范围划分。Hive也是 针对某一列进行桶的组织。Hive采用对列值哈希，然后除以桶的个数求余的方式决定该条记录存放在哪个桶当中。 
把表（或者分区）组织成桶（Bucket）有两个理由：
* 获得更高的查询处理效率。桶为表加上了额外的结构，Hive 在处理有些查询时能利用这个结构。具体而言，连接两个在（包含连接列的）相同列上划分了桶的表，可以使用 Map 端连接 （Map-side join）高效的实现。比如JOIN操作。对于JOIN操作两个表有一个相同的列，如果对这两个表都进行了桶操作。那么将保存相同列值的桶进行JOIN操作就可以，可以大大较少JOIN的数据量。
* 使取样（sampling）更高效。在处理大规模数据集时，在开发和修改查询的阶段，如果能在数据集的一小部分数据上试运行查询，会带来很多方便。


### 1.1.2.具体事例

创建内部表mytable
```
hive> create table if not exists mytable(sid int ,name string)
    > row format delimited field
    > row format delimited fields terminated by '\005'
    > stored as textfile ;
OK
Time taken: 1.016 seconds
hive> 

```


 创建外部表pageview
```
hive> create external table if not exists pageview(
    >          pageid int,
    >          page_url string comment 'the page url'
    > )
    > row format delimited fields terminated by ','
    > location 'hdfs://192.168.0.11:9000/user/hive/warehouse/' ;
OK
Time taken: 0.102 seconds
hive> 
```


 创建分区表invites
```
create table student_p(Sno int,Sname string,Sex string,Sage int,Sdept string) 
partitioned by(part string) 
row format delimited fields terminated by ','
stored as textfile;
```

 创建带桶的表student
```
hive> create table student(id int ,age int, name string)
    > partitioned by(stat_data string)
    > clustered by(id) sorted by(age) into 2 buckets
    > row format delimited fields terminated by ',' ;
OK
Time taken: 0.13 seconds
hive> 


#加载本地数据到指定分区
hive> load data local inpath '/home/buckets.txt' overwrite into table student partition(stat_data='20131230');
Loading data to table default.student partition (stat_data=20131230)
Partition default.student{stat_data=20131230} stats: [numFiles=1, numRows=0, totalSize=91, rawDataSize=0]
OK
Time taken: 3.756 seconds

```



## 1.2.修改表

### 1.2.1.增加/删除分区
```
'语法结构'
#增加
ALTER TABLE table_name ADD [IF NOT EXISTS] partition_spec [ LOCATION 'location1' ] partition_spec [ LOCATION 'location2' ] ...
partition_spec:
: PARTITION (partition_col = partition_col_value, partition_col = partiton_col_value, ...)


#删除
ALTER TABLE table_name DROP partition_spec, partition_spec,...

```

实例
```
alter table student_p add partition(part='a')
 partition(part='b');

#添加分区
hive> alter table student 
add partition(stat_data='20140101') location '/user/hive/warehouse/student'                         #添加的同时指定分区的指向
   partition(stat_data='20140102');
OK
Time taken: 0.23 seconds
hive> 


hive> show partitions student;
OK
stat_data=20131230
stat_data=20140101
stat_data=20140102
Time taken: 0.145 seconds, Fetched: 3 row(s)
hive>
 

#删除分区
hive> alter table student drop partition(stat_data='20140101'),partition(stat_data='20140102') ;
Dropped the partition stat_data=20140101
Dropped the partition stat_data=20140102
OK
Time taken: 0.852 seconds


hive> show partitions student;
OK
stat_data=20131230
Time taken: 0.145 seconds, Fetched: 1 row(s)
hive> 
```


### 1.2.2.重命名表
 语法结构
```
ALTER TABLE table_name RENAME TO new_table_name
```

 具体实例
```
hive> show tables;
OK
mytable
pageview
student
Time taken: 0.077 seconds, Fetched: 3 row(s)

hive> alter table mytable rename to mytable1 ;                  #rename to 重命名
OK
Time taken: 0.34 seconds

hive> show tables;
OK
mytable1
pageview
student
Time taken: 0.049 seconds, Fetched: 3 row(s)
hive> 
```


### 1.2.3.增加/更新列
 语法结构
```

ALTER TABLE table_name ADD|REPLACE COLUMNS (col_name data_type [COMMENT col_comment], ...)
 
注：ADD是代表新增一字段，字段位置在所有列后面(partition列前)，REPLACE则是表示替换表中所有字段。
 
ALTER TABLE table_name CHANGE [COLUMN] col_old_name col_new_name column_type [COMMENT col_comment] [FIRST|AFTER column_name]


```

 具体实例
```
 
hive> desc student;
OK
id                      int                                        
age                     int                                        
name                    string                                     
stat_data               string                                     
 
# Partition Information         
# col_name              data_type               comment             
stat_data               string                                     
Time taken: 0.15 seconds, Fetched: 9 row(s)


#添加字段
hive> alter table student add columns(gender string);
OK
Time taken: 0.22 seconds


hive> desc student;
OK
id                      int                                        
age                     int                                        
name                    string                                     
gender                  string                                     
stat_data               string                                     
 
# Partition Information         
# col_name              data_type               comment            
stat_data               string                                     
Time taken: 0.18 seconds, Fetched: 10 row(s)
hive> alter
 
 
#修改
hive> alter table student replace columns (id int ,age int ,name string);
OK
Time taken: 0.192 seconds
hive> desc student;
OK
id                      int                                        
age                     int                                        
name                    string                                     
stat_data               string                                     
 
# Partition Information         
# col_name              data_type               comment            
stat_data               string                                     
Time taken: 0.153 seconds, Fetched: 9 row(s)
hive> 
```


## 1.3显示命令
```
show tables
show databases
show partitions
show functions
desc extended t_name;
desc formatted table_name;
```


# 2.DML操作

## 2.1.Load
### 2.1.1.语法结构
```

LOAD DATA [LOCAL] INPATH 'filepath' [OVERWRITE] INTO
TABLE tablename [PARTITION (partcol1=val1, partcol2=val2 ...)]

/*
说明：
1、Load 操作只是单纯的复制/移动操作，将数据文件移动到 Hive 表对应的位置。
2、filepath：
相对路径，例如：project/data1
绝对路径，例如：/user/hive/project/data1
包含模式的完整 URI，列如：
hdfs://namenode:9000/user/hive/project/data1
3、LOCAL关键字
如果指定了 LOCAL， load 命令会去查找本地文件系统中的 filepath。
如果没有指定 LOCAL 关键字，则根据inpath中的uri[如果指定了 LOCAL，那么：
load 命令会去查找本地文件系统中的 filepath。如果发现是相对路径，则路径会被解释为相对于当前用户的当前路径。
load 命令会将 filepath中的文件复制到目标文件系统中。目标文件系统由表的位置属性决定。被复制的数据文件移动到表的数据对应的位置。
 
如果没有指定 LOCAL 关键字，如果 filepath 指向的是一个完整的 URI，hive 会直接使用这个 URI。 否则：如果没有指定 schema 或者 authority，Hive 会使用在 hadoop 配置文件中定义的 schema 和 authority，fs.default.name 指定了 Namenode 的 URI。
如果路径不是绝对的，Hive 相对于/user/进行解释。
Hive 会将 filepath 中指定的文件内容移动到 table （或者 partition）所指定的路径中。]查找文件
 
 
4、OVERWRITE 关键字
如果使用了 OVERWRITE 关键字，则目标表（或者分区）中的内容会被删除，然后再将 filepath 指向的文件/目录中的内容添加到表/分区中。
如果目标表（分区）已经有一个文件，并且文件名和 filepath 中的文件名冲突，那么现有的文件会被新文件所替代。

*/

```

### 2.1.2.具体实例
load
```
#加载绝对路径数据
hive> load data local inpath '/home/buckets.txt' overwrite into table student partition(stat_data='20131230');

#加载包含模式数据
[root@hdp-node-01 home]# hdfs dfs -ls /
Found 3 items
-rw-r--r--   3 root supergroup         91 2016-11-28 09:43 /buckets.txt
drwx-wx-wx   - root supergroup          0 2016-11-27 23:31 /tmp
drwxr-xr-x   - root supergroup          0 2016-11-27 23:34 /user

hive> load data inpath 'hdfs://hdp-node-01:9000/buckets.txt' into table student partition(stat_data='20140202');

[root@hdp-node-01 home]# hdfs dfs -ls /                                #会将文件移动
Found 2 items
drwx-wx-wx   - root supergroup          0 2016-11-27 23:31 /tmp
drwxr-xr-x   - root supergroup          0 2016-11-27 23:34 /user
[root@hdp-node-01 home]#

 
#覆盖：overwrite 
load data inpath 'hdfs://hdp-node-01:9000/buckets.txt' overwrite into table student partition(stat_data='20140202');

```



## 2.2.Insert
### 2.2.1.语法结构
```
INSERT OVERWRITE TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2 ...)] select_statement1 FROM from_statement


Multiple inserts:
FROM from_statement
INSERT OVERWRITE TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2 ...)] select_statement1
[INSERT OVERWRITE TABLE tablename2 [PARTITION ...] select_statement2] ...


Dynamic partition inserts:
INSERT OVERWRITE TABLE tablename PARTITION (partcol1[=val1], partcol2[=val2] ...) select_statement FROM from_statement


```

### 2.2.2.实例
```
'基本模式插入'
hive> insert overwrite table student partition(stat_data='20150202') 
       select id,age,name from student where stat_data='20150101';

hive> select * from student;
OK
1       21      zhangsan        20150101
2       22      lisi    20150101
3       33      wangwu  20150101
4       44      zhouliu 20150101
1       21      zhangsan        20150202
2       22      lisi    20150202
3       33      wangwu  20150202
4       44      zhouliu 20150202


'多插入模式'

hive> from student
    > insert overwrite table student partition(stat_data='20160101')
    > select id,age,name where stat_data='20150101'
    > insert overwrite table student partition(stat_data='20170101')
    > select id,age,name where stat_data='20150101';

hive> select *from student;
OK
1       21      zhangsan        20150101
2       22      lisi    20150101
3       33      wangwu  20150101
4       44      zhouliu 20150101
1       21      zhangsan        20150202
2       22      lisi    20150202
3       33      wangwu  20150202
4       44      zhouliu 20150202
1       21      zhangsan        20160101
2       22      lisi    20160101
3       33      wangwu  20160101
4       44      zhouliu 20160101
1       21      zhangsan        20170101
2       22      lisi    20170101
3       33      wangwu  20170101
4       44      zhouliu 20170101
Time taken: 0.147 seconds, Fetched: 16 row(s)
hive>

 
'自动分区模式'
hive> set hive.exec.dynamic.partition.mode=nonstrict;
hive> insert overwrite table student partition(stat_data)
    > select id,age,name,stat_data from student where stat_data='20150101';


```

 


## 2.3.导出表数据
### 2.3.1.语法结构
```
INSERT OVERWRITE [LOCAL] DIRECTORY directory1 SELECT ... FROM ...
 
 
multiple inserts:
FROM from_statement
INSERT OVERWRITE [LOCAL] DIRECTORY directory1 select_statement1
[INSERT OVERWRITE [LOCAL] DIRECTORY directory2 select_statement2] ...

```

### 2.3.2.实例

```
'导出文件到本地'
hive> insert overwrite local directory '/home/student' select *from student;

[root@hdp-node-01 home]# cd /home/student/
[root@hdp-node-01 student]# ll
total 4
-rw-r--r-- 1 root root 340 Nov 28 10:16 000000_0
-rw-r--r-- 1 root root   0 Nov 28 10:16 000001_0
[root@hdp-node-01 student]# cat 000000_0
121zhangsan20150101
222lisi20150101
333wangwu20150101
444zhouliu20150101
121zhangsan20150202
222lisi20150202
333wangwu20150202
444zhouliu20150202
121zhangsan20160101
222lisi20160101
333wangwu20160101
444zhouliu20160101
121zhangsan20170101
222lisi20170101
333wangwu20170101
444zhouliu20170101
[root@hdp-node-01 student]#




'导出数据到HDFS'
hive> insert overwrite directory 'hdfs://hdp-node-01:9000/student' select *from student;

[root@hdp-node-01 student]# hdfs dfs -ls /student
Found 1 items
-rwxr-xr-x   3 root supergroup        340 2016-11-28 10:20 /student/000000_0
[root@hdp-node-01 student]# hdfs dfs -cat  /student/000000_0
121zhangsan20150101
222lisi20150101
333wangwu20150101
444zhouliu20150101
121zhangsan20150202
222lisi20150202
333wangwu20150202
444zhouliu20150202
121zhangsan20160101
222lisi20160101
//..........

```


## 2.4.SELECT
### 2.4.1.语法结构
```
SELECT [ALL | DISTINCT] select_expr, select_expr, ...
FROM table_reference
[WHERE where_condition]
[GROUP BY col_list [HAVING condition]]
[CLUSTER BY col_list
  | [DISTRIBUTE BY col_list] [SORT BY| ORDER BY col_list]
]
[LIMIT number]


/*
注：1、order by 会对输入做全局排序，因此强行设置reduce为1，会导致当输入规模较大时，需要较长的计算时间。
2、sort by不是全局排序，其在数据进入reducer前完成排序。因此，如果用sort by进行排序，并且设置mapred.reduce.tasks>1，则sort by只保证每个reducer的输出有序，不保证全局有序。
3、distribute by(字段)根据指定的字段将数据分到不同的reducer，且分发算法是hash散列。
4、Cluster by(字段) 除了具有Distribute by的功能外，还会对该字段进行排序。
 
 
因此，如果分桶和sort字段是同一个时，此时，cluster by = distribute by + sort by
 
分桶表的作用：最大的作用是用来提高join操作的效率；
（思考这个问题：
select a.id,a.name,b.addr from a join b on a.id = b.id;
如果a表和b表已经是分桶表，而且分桶的字段是id字段
做这个join操作时，还需要全表做笛卡尔积吗？）
*/

```


### 2.4.2.实例
```
'获取年龄大的3个学生'
hive> select id,age,name from student where stat_data='20150101' order by age desc limit 3;

Total MapReduce CPU Time Spent: 7 seconds 280 msec
OK
4       44      zhouliu
3       33      wangwu
2       22      lisi
Time taken: 75.821 seconds, Fetched: 3 row(s)



'查询学生信息按年龄，降序排序
select id,age,name from student sort by age desc;


select id,age,name from student order by age desc;


 select id,age,name from student distribute by age;



'按学生名称汇总学生年龄'

 select id,age,name from student   group by name;




```



## 2.5.Hive Join
### 2.5.1.语法结构
```
join_table:
  table_reference JOIN table_factor [join_condition]  | table_reference {LEFT|RIGHT|FULL} [OUTER] JOIN table_reference join_condition  | table_reference LEFT SEMI JOIN table_reference join_condition

/*
Hive 支持等值连接（equality joins）、外连接（outer joins）和（left/right joins）。Hive 不支持非等值的连接，因为非等值连接非常难转化到 map/reduce 任务。
另外，Hive 支持多于 2 个表的连接。
*/

```
### 2.5.2.举例
```
'只支持等值join'

例如：
  SELECT a.* FROM a JOIN b ON (a.id = b.id)
  SELECT a.* FROM a JOIN b  ON (a.id = b.id AND a.department = b.department)      #是正确的

  SELECT a.* FROM a JOIN b ON (a.id>b.id)            #是错误的。

'可以 join 多于 2 个表'

  SELECT a.val, b.val, c.val FROM a JOIN b   ON (a.key = b.key1) JOIN c ON (c.key = b.key2)
#如果join中多个表的 join key 是同一个，则 join 会被转化为单个 map/reduce 任务，因为 join 中只使用了 b.key1 作为 join key，例如：
  SELECT a.val, b.val, c.val FROM a JOIN b   ON (a.key = b.key1) JOIN c   ON (c.key = b.key1)

#而这一 join 被转化为 2 个 map/reduce 任务。因为 b.key1 用于第一次 join 条件，而 b.key2 用于第二次 join
SELECT a.val, b.val, c.val FROM a JOIN b ON (a.key = b.key1)   JOIN c ON (c.key = b.key2)


'join 时，每次 map/reduce 任务的逻辑'

#reducer 会缓存 join 序列中除了最后一个表的所有表的记录，再通过最后一个表将结果序列化到文件系统。这一实现有助于在 reduce 端减少内存的使用量。实践中，应该把最大的那个表写在最后（否则会因为缓存浪费大量内存）。例如：
SELECT a.val, b.val, c.val FROM a
    JOIN b ON (a.key = b.key1) JOIN c ON (c.key = b.key1)

#所有表都使用同一个 join key（使用 1 次 map/reduce 任务计算）。Reduce 端会缓存 a 表和 b 表的记录，然后每次取得一个 c 表的记录就计算一次 join 结果，类似的还有：
  SELECT a.val, b.val, c.val FROM a
    JOIN b ON (a.key = b.key1) JOIN c ON (c.key = b.key2)
#这里用了 2 次 map/reduce 任务。第一次缓存 a 表，用 b 表序列化；第二次缓存第一次 map/reduce 任务的结果，然后用 c 表序列化。


'LEFT，RIGHT 和 FULL OUTER 关键字用于处理 join 中空记录的情况'
SELECT a.val, b.val FROM
a LEFT OUTER  JOIN b ON (a.key=b.key)        #对应所有 a 表中的记录都有一条记录输出。输出的结果应该是 a.val, b.val，当 a.key=b.key 时，而当 b.key 中找不到等值的 a.key 记录时也会输出:a.val, NULL
#所以 a 表中的所有记录都被保留了；“a RIGHT OUTER JOIN b”会保留所有 b 表的记录。


'LEFT  SEMI  JOIN是IN/EXISTS的高效实现'
select id,name,age from student a left semi join class b on(a.name=b.std_name);            #相当于内连接，但是只显示左表


```

# 3.Hive Shell参数


## 3.1.Hive命令行
### 3.1.1.语法结构
```
hive [-hiveconf x=y]* [<-i filename>]* [<-f filename>|<-e query-string>] [-S]

/*
说明：
1、-i 从文件初始化HQL。
2、-e从命令行执行指定的HQL
3、-f 执行HQL脚本
4、-v 输出执行的HQL语句到控制台
5、-p <port> connect to Hive Server on port number
6、-hiveconf x=y Use this to set hive/hadoop configuration variables.
7. -S  是取消OK和时间的显示
*/
```

### 3.1.2.实例
```
'运行一个查询'
[root@hdp-node-01 hive]# ./bin/hive -S -e 'select *from student;';              #-S 是取消OK和时间的显示
1       21      zhangsan        20150101
2       22      lisi    20150101
3       33      wangwu  20150101
4       44      zhouliu 20150101
1       21      zhangsan        20150202
2       22      lisi    20150202
3       33      wangwu  20150202
4       44      zhouliu 20150202
1       21      zhangsan        20160101
2       22      lisi    20160101
3       33      wangwu  20160101
4       44      zhouliu 20160101
1       21      zhangsan        20170101
2       22      lisi    20170101
3       33      wangwu  20170101
4       44      zhouliu 20170101
[root@hdp-node-01 hive]#


'运行一个文件'
[root@hdp-node-01 hive]# cat /home/query.hql
select *from student;
 
[root@hdp-node-01 hive]# ./bin/hive -f /home/query.hql
 
Logging initialized using configuration in jar:file:/home/hadoop/app/apache-hive-1.2.1-bin/lib/hive-common-1.2.1.jar!/hive-log4j.properties
OK
1       21      zhangsan        20150101
2       22      lisi    20150101
3       33      wangwu  20150101
4       44      zhouliu 20150101
1       21      zhangsan        20150202
2       22      lisi    20150202
3       33      wangwu  20150202
4       44      zhouliu 20150202
1       21      zhangsan        20160101
2       22      lisi    20160101
3       33      wangwu  20160101
4       44      zhouliu 20160101
1       21      zhangsan        20170101
2       22      lisi    20170101
3       33      wangwu  20170101
4       44      zhouliu 20170101
Time taken: 2.514 seconds, Fetched: 16 row(s)
[root@hdp-node-01 hive]#


'运行参数文件'
[root@hdp-node-01 hive]# cat /home/initHQL.conf
set mapred.reduce.tasks=4
[root@hdp-node-01 hive]# hive -i /home/initHQL.conf

 
```


## 3.2.Hive参数配置方式
Hive参数大全：
https://cwiki.apache.org/confluence/display/Hive/Configuration+Properties

对于一般参数，有以下三种设定方式：
* 配置文件
* 命令行参数
* 参数声明
 
 配置文件

* 用户自定义配置文件：$HIVE_CONF_DIR/hive-site.xml
* 默认配置文件：$HIVE_CONF_DIR/hive-default.xml
用户自定义配置会覆盖默认配置。另外，Hive也会读入Hadoop的配置，因为Hive是作为Hadoop的客户端启动的，Hive的配置会覆盖Hadoop的配置。配置文件的设定对本机启动的所有Hive进程都有效
 

 命令行参数

启动Hive（客户端或Server方式）时，可以在命令行添加-hiveconf param=value来设定参数，例如：
```
bin/hive -hiveconf hive.root.logger=INFO,console    #这一设定对本次启动的Session（对于Server方式启动，则是所有请求的Sessions）有效
```

参数声明

可以在HQL中使用SET关键字设定参数，例如：
```
set mapred.reduce.tasks=100;
```
这一设定的作用域也是session级的。

**上述三种设定方式的优先级依次递增。即参数声明覆盖命令行参数，命令行参数覆盖配置文件设定。注意某些系统级的参数，例如log4j相关的设定，必须用前两种方式设定，因为那些参数的读取在Session建立以前已经完成了。**
