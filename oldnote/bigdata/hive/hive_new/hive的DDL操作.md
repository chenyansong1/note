---
title: hive的DDL操作
categories: hive  
tags: [hive]
---


# 创建数据库及数据库相关操作

```

CREATE (DATABASE|SCHEMA) [IF NOT EXISTS] database_name
  [COMMENT database_comment]
  [LOCATION hdfs_path]
  [WITH DBPROPERTIES (property_name=property_value, ...)];



#注意一定要使用if如果存在就创建,这样更加的专业
create database if not exists db_hive_01;

create database if not exists db_hive_02
location '/user/study/hive/warehouse/db_hive_02.db'
#如果不指定数据库在HDFS中的位置,默认是在HDFS中的/user/hive/warehouse/以数据库名.db命名的目录


#在数据库下创建表,那么将在数据库的目录下创建一个以表名为名字的目录
create table if not exist db_hive_02.user


#查看数据库
show databases;

#查看以db_hive开头的数据库(有时候我们只是记得数据库的大致的名称的时候,这个命令有用)
show databases like 'db_hive*';


#查看数据库的相关信息
hive (test_database)> desc database test_database;
OK
db_name comment location        owner_name      owner_type      parameters
test_database           hdfs://hdp-node-01:9000/user/hive/warehouse/test_database.db    root    USER
Time taken: 0.052 seconds, Fetched: 1 row(s)

#查看数据库的扩展信息
hive (test_database)> desc database extended test_database;
OK
db_name comment location        owner_name      owner_type      parameters
test_database           hdfs://hdp-node-01:9000/user/hive/warehouse/test_database.db    root    USER
Time taken: 0.068 seconds, Fetched: 1 row(s)
hive (test_database)> 



#删除数据库(如果已经存在的话)
drop database if exists test_database;



hive (test_database)> drop database test_database;
FAILED: Execution Error, return code 1 from org.apache.hadoop.hive.ql.exec.DDLTask. InvalidOperationException(message:Database test_database is not empty. One or more tables exist.)
#数据库中有表存在的数据库,是不能删除的


#可以使用级联删除的方式,进行删除,删除的时候,数据库对应的在HDFS中的目录也是会被删除的
drop database test_database cascad;


```


# hive官网完整的建表语句

```
CREATE [TEMPORARY] [EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name    -- (Note: TEMPORARY available in Hive 0.14.0 and later)
  [(col_name data_type [COMMENT col_comment], ... [constraint_specification])]
  [COMMENT table_comment]
  [PARTITIONED BY (col_name data_type [COMMENT col_comment], ...)]
  [CLUSTERED BY (col_name, col_name, ...) [SORTED BY (col_name [ASC|DESC], ...)] INTO num_buckets BUCKETS]
  [SKEWED BY (col_name, col_name, ...)                  -- (Note: Available in Hive 0.10.0 and later)]
     ON ((col_value, col_value, ...), (col_value, col_value, ...), ...)
     [STORED AS DIRECTORIES]
  [
   [ROW FORMAT row_format] 
   [STORED AS file_format]
     | STORED BY 'storage.handler.class.name' [WITH SERDEPROPERTIES (...)]  -- (Note: Available in Hive 0.6.0 and later)
  ]
  [LOCATION hdfs_path]
  [TBLPROPERTIES (property_name=property_value, ...)]   -- (Note: Available in Hive 0.6.0 and later)
  [AS select_statement];   -- (Note: Available in Hive 0.5.0 and later; not supported for external tables)
 
CREATE [TEMPORARY] [EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name
  LIKE existing_table_or_view_name
  [LOCATION hdfs_path];
 
data_type
  : primitive_type
  | array_type
  | map_type
  | struct_type
  | union_type  -- (Note: Available in Hive 0.7.0 and later)
 
primitive_type
  : TINYINT
  | SMALLINT
  | INT
  | BIGINT
  | BOOLEAN
  | FLOAT
  | DOUBLE
  | DOUBLE PRECISION -- (Note: Available in Hive 2.2.0 and later)
  | STRING
  | BINARY      -- (Note: Available in Hive 0.8.0 and later)
  | TIMESTAMP   -- (Note: Available in Hive 0.8.0 and later)
  | DECIMAL     -- (Note: Available in Hive 0.11.0 and later)
  | DECIMAL(precision, scale)  -- (Note: Available in Hive 0.13.0 and later)
  | DATE        -- (Note: Available in Hive 0.12.0 and later)
  | VARCHAR     -- (Note: Available in Hive 0.12.0 and later)
  | CHAR        -- (Note: Available in Hive 0.13.0 and later)
 
array_type
  : ARRAY < data_type >
 
map_type
  : MAP < primitive_type, data_type >
 
struct_type
  : STRUCT < col_name : data_type [COMMENT col_comment], ...>
 
union_type
   : UNIONTYPE < data_type, data_type, ... >  -- (Note: Available in Hive 0.7.0 and later)
 
row_format
  : DELIMITED [FIELDS TERMINATED BY char [ESCAPED BY char]] [COLLECTION ITEMS TERMINATED BY char]
        [MAP KEYS TERMINATED BY char] [LINES TERMINATED BY char]
        [NULL DEFINED AS char]   -- (Note: Available in Hive 0.13 and later)
  | SERDE serde_name [WITH SERDEPROPERTIES (property_name=property_value, property_name=property_value, ...)]
 
file_format:
  : SEQUENCEFILE
  | TEXTFILE    -- (Default, depending on hive.default.fileformat configuration)
  | RCFILE      -- (Note: Available in Hive 0.6.0 and later)
  | ORC         -- (Note: Available in Hive 0.11.0 and later)
  | PARQUET     -- (Note: Available in Hive 0.13.0 and later)
  | AVRO        -- (Note: Available in Hive 0.14.0 and later)
  | INPUTFORMAT input_format_classname OUTPUTFORMAT output_format_classname
 
constraint_specification:
  : [, PRIMARY KEY (col_name, ...) DISABLE NOVALIDATE ]
    [, CONSTRAINT constraint_name FOREIGN KEY (col_name, ...) REFERENCES table_name(col_name, ...) DISABLE NOVALIDATE 


```


# 几种创建表的方法

## 自己指定字段建表

```

create table if not exists default.test_log_2015_0913
(
	ip string comment 'remote ip address',
	user string ,
	request_url string comment 'user request url'
)
#对表进行comment
COMMENT 'WEB Access Logs'
#指定格式化
ROW FORMAT DELIMITED
#  指定字段的分割符
fields terminated by ','
#文件的存储格式(默认的是textFile的格式,但是在实际生产环境中,我们使用的是一般是Parquet或者是ORC
STORED AS textFile  
#指定数据在HDFS中存储的位置,默认是在对应的数据库下面创建一个以表名的目录,但是我们可以指定这个目录,就是下面这样的操作
LOCATION '/user/hive/warehouse/test_log_2015_0913'



```



## 根据另外一张表的查询结果创建一张表

这种建表的方式将查询表中查询的数据导入到了新建的表中

```
create table if not exists default.test_log_2015_0913
(
	ip string comment 'remote ip address',
	user string ,
	request_url string comment 'user request url'
)
#对表进行comment
COMMENT 'WEB Access Logs'
#指定格式化
ROW FORMAT DELIMITED
#  指定字段的分割符
fields terminated by ','
#文件的存储格式(默认的是textFile的格式,但是在实际生产环境中,我们使用的是一般是Parquet或者是ORC
STORED AS textFile  
#指定数据在HDFS中存储的位置,默认是在对应的数据库下面创建一个以表名的目录,但是我们可以指定这个目录,就是下面这样的操作
LOCATION '/user/hive/warehouse/test_log_2015_0913'
as select * from xxx



# 使用查询的结果创建表,此时新创建的表中有数据
hive (default)> create table if not exists default.test_log 
              > as select id, name from default.test_table;


hive (default)> select * from default.test_log;
OK
test_log.id     test_log.name
11      zhangsan
22      lisi
33      wangwu
11      zhangsan
22      lisi
33      wangwu
Time taken: 0.294 seconds, Fetched: 6 row(s)
hive (default)> 

```

## 使用其他的表或者视图创建表结构

```
CREATE [TEMPORARY] [EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name
  LIKE existing_table_or_view_name
  [LOCATION hdfs_path];
 
         
#查询test_log2的数据
hive (default)> select * from test_log2;
OK
test_log2.id    test_log2.name
11      zhangsan
22      lisi
33      wangwu
11      zhangsan
22      lisi
33      wangwu
Time taken: 0.108 seconds, Fetched: 6 row(s)

#使用test_log2去创建新的表,这里只是构建新表的结构
hive (default)> create table if not exists default.test_log6
              > like test_log2;
OK
Time taken: 0.16 seconds

#新表中没有数据
hive (default)> select * from test_log6;
OK
test_log6.id    test_log6.name
Time taken: 0.106 seconds
hive (default)> 

```


# 对表的增删改操作

```
# 清除表的数据
truncate table dept_cats;


# 修改表的名称
alter table dept_like rename to dept_like_rename;

#删除表
drop table if exists dept_like_rename;


```

