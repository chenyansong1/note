[TOC]

# mysql的官网说明

* MySQL Server（msyqld, mysql) ： MySQL客户端，服务器端
* MySQL Cluster：集群（一般至少4个节点）
* MySQL Proxy：代理，实现路由，实现读写分离
* MySQL Adminnitrator : 
* Mysql Query Browser：
* MySQL Workbench ： 以上三个整合在一起，是MySQL的管理工具
* MySQL Migration Toolkit :工具箱，数据移植用
* MySQL Embeddded Server：嵌入式
* MySQL Drivers and Connectors :连接器 （驱动）



Community Edition  :  社区版

Enterprise Edition : 企业版



- [MySQL on Windows](https://dev.mysql.com/downloads/windows/) #windows
- [MySQL Yum Repository](https://dev.mysql.com/downloads/repo/yum/) #yum仓库
- [MySQL APT Repository](https://dev.mysql.com/downloads/repo/apt/)
- [MySQL SUSE Repository](https://dev.mysql.com/downloads/repo/suse/)
- [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)  #服务器
- [MySQL Cluster](https://dev.mysql.com/downloads/cluster/)  # 集群
- [MySQL Router](https://dev.mysql.com/downloads/router/)
- [MySQL Shell](https://dev.mysql.com/downloads/shell/)
- [MySQL Workbench](https://dev.mysql.com/downloads/workbench/)
- MySQL Connectors  # MySQL的连接器（就是驱动）
  - [Connector/ODBC](https://dev.mysql.com/downloads/connector/odbc/)
  - [Connector/NET](https://dev.mysql.com/downloads/connector/net/)
  - [Connector/J](https://dev.mysql.com/downloads/connector/j/) #Java的驱动
  - [Connector/Node.js](https://dev.mysql.com/downloads/connector/nodejs/)
  - [Connector/Python](https://dev.mysql.com/downloads/connector/python/)
  - [Connector/C++](https://dev.mysql.com/downloads/connector/cpp/)
  - [Connector/C (libmysqlclient)](https://dev.mysql.com/downloads/connector/c/)
  - [MySQL Native Driver for PHP](https://dev.mysql.com/downloads/connector/php-mysqlnd/)



一般我们是下载的是通用的格式包

![image-20181024225055416](/Users/chenyansong/Documents/note/images/mysql/server-download.png)



# MySQL安装

在Linux系统中自带的MySQL，有如下的包：

* mysql 这是客户端
* mysql-server 这个是服务器端



mysql官方提供的安装方式，我们可以进入对应的链接进行安装

| Type                             | Setup Method                                                 | Additional Information                                       |
| -------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Apt                              | Enable the [MySQL Apt repository](https://dev.mysql.com/downloads/repo/apt/) | [Documentation](https://dev.mysql.com/doc/refman/5.7/en/linux-installation-apt-repo.html) |
| Yum                              | Enable the [MySQL Yum repository](https://dev.mysql.com/downloads/repo/yum/) | [Documentation](https://dev.mysql.com/doc/refman/5.7/en/linux-installation-yum-repo.html) |
| Zypper                           | Enable the [MySQL SLES repository](https://dev.mysql.com/downloads/repo/suse/) | [Documentation](https://dev.mysql.com/doc/refman/5.7/en/linux-installation-sles-repo.html) |
| RPM                              | [Download](https://dev.mysql.com/downloads/mysql/) a specific package | [Documentation](https://dev.mysql.com/doc/refman/5.7/en/linux-installation-rpm.html) |
| DEB                              | [Download](https://dev.mysql.com/downloads/mysql/) a specific package | [Documentation](https://dev.mysql.com/doc/refman/5.7/en/linux-installation-debian.html) |
| Generic                          | [Download](https://dev.mysql.com/downloads/mysql/) a generic package | [Documentation](https://dev.mysql.com/doc/refman/5.7/en/binary-installation.html) |
| Source                           | Compile from [source](https://dev.mysql.com/downloads/mysql/) | [Documentation](https://dev.mysql.com/doc/refman/5.7/en/source-installation.html) |
| Docker                           | Use Docker Hub, Docker Store, or Oracle Container Registry   | [Documentation](https://dev.mysql.com/doc/refman/5.7/en/linux-installation-docker.html) |
| Oracle Unbreakable Linux Network | Use ULN channels                                             | [Documentation](https://dev.mysql.com/doc/refman/5.7/en/uln-installation.html) |

 

## rpm的安装方式



下面是rpm安装中需要用到的包的说明

![image-20181024230613807](/Users/chenyansong/Documents/note/images/mysql/mysql-install-rpm.png)



具体的安装，参见官网 ： https://dev.mysql.com/doc/refman/5.7/en/linux-installation-rpm.html



## 通用(Generic)二进制安装



已经编译好的二进制，直接就能用的

参见：https://dev.mysql.com/doc/refman/5.7/en/binary-installation.html



## 源码安装



## 安装之后的说明



mysql的元数据的定义是存放在mysql这个数据库中的，而安装完成之后的**初始化就是 生成mysl 库的过程**

```
#在启动MySQL的时候，会自动的初始化
service mysqld start 
```

![image-20181024233211573](/Users/chenyansong/Documents/note/images/mysql/mysql-start.png)



如果是二进制安装，则我们需要手动初始化，然后启动MySQL服务



```
shell> groupadd mysql
shell> useradd -r -g mysql -s /bin/false mysql
shell> cd /usr/local
shell> tar zxvf /path/to/mysql-VERSION-OS.tar.gz
shell> ln -s full-path-to-mysql-VERSION-OS mysql
shell> cd mysql
shell> mkdir mysql-files
shell> chown mysql:mysql mysql-files
shell> chmod 750 mysql-files
shell> bin/mysqld --initialize --user=mysql 		#手动初始化
shell> bin/mysql_ssl_rsa_setup              
shell> bin/mysqld_safe --user=mysql &		#启动MySQL服务
```



MySQL安装之后的密码

```
shell> sudo grep 'temporary password' /var/log/mysql/mysqld.log 
#这个密码是root的密码
```



MySQL的初始化数据库

```
information_schema	#MySQL运行时数据
performance_schema #MYSQL状态信息
mysql	#MySQL的表的元数据信息
test	#测试数据库

ls /var/lib/mysql 有对应数据库的文件目录
	test	#对应数据库
	mysql	#对应数据库，而information_schema是内存信息，不属于锁文件
	#如果我们在该目录下新建一个 mydb，那么使用客户端去查询的时候，可以看到 show databases; 
```



# 客户端连接



```
mysql 
	-u  username
	-p 回车
	-h mysql-server-host
	-e 'commands'
	
mysql -uroot -p -h localhost

#执行MySQL命令
shell>mysql -uroot -p -h localhost -e 'CRERAE DATABASE test_db;'
```

mysql的用户：username@host 放在一起限定的

```
#如：我们授权一个用户时，需要同时指定：username , host
mysql> GRANT ALL ON menagerie.* TO 'your_mysql_name'@'your_client_host';
```



如果在Linux上，MySQL的客户端和服务器端在同一台主机，那么使用的是socket连接，如果是远程的客户端连接服务器上的MySQL，使用的是tcp/IP, 可以查看文件：/var/lib/mysql/mysql.sock 这个文件



# 关系型数据库对象

* 库

* 表
* 索引
* 视图
* 约束（键）
* 存储函数
* 存储过程
* 触发器
* 游标
* 用户
* 权限
* 事务



# 数据类型

字段类型

​	字符

​		CHAR(N)		#固定长度

​		VARCHAR(N)  #可变长度

​		BINARY(n)	#固定长度，区分大小写

​		VARBINARY(n)	#可变长度，区分大小写

​		TEXT(n)			#大文本，不区分大小写

​		BLOB(n)			#大文本，区分大小写 （binary large object）

​		ENUM, SET		# 如Set('m','f')  我们可以组合 ： m, f, mf, fm

​	数值

​		精确数值

​			整形

​				TINYINT  (一个字节，-128-127 或者0-255)  TINYINT(1) 还是1个字节存储，但是显示的时候只是显示1位，如：如果存储111 ，那么显示的时候只是显示1

​				SMALLINT（2个字节）

​				MEDIUMINT（3个字节）

​				INT	（4个字节）
​				BIGINT(8个字节)

​				修饰符：UNSIGNED 无符号

​			十进制

​				DECIMAL 可以精确的表示一个浮点数

​		近似数值

​			浮点数

​				FLOAT	#4字节

​				DOUBLE	#8字节

​	日期

​		DATE	#日期	2018-11-11

​		TIME	#时间	11:11:11

​		DATETIME	#日期时间 2018-11-11 11:11:11

​		TIMESTAMP	#时间戳	毫秒值

​		YEAR	

​	布尔

​		0 or 1

​	内置类型：ENUM, SET

| Type       | Storage Required            | Max length                 |
| ---------- | --------------------------- | -------------------------- |
| CHAR(M)    | M 个字符                    | 255 characters         2^8 |
| VARCHAR(M) | m+ 1 or 2 bytes(表示结束符) | 65535 characters           |
| TINYTEXT   | L character + 1 byte        | 255 character              |
| TEXT       | L character + 2 byte        | 65535 character            |
| MEDIUMTEXT | L character + 3 byte        | 16777215 character         |
| LONGTEXT   | L character + 4 byte        | 4294967295 character       |
| TINYBLOB   | 255byte                     |                            |
| BLOB       | 64Kb                        |                            |
| LONGBLOB   | 4Gb                         |                            |
| MEDIUMBLOB | 16Mb                        |                            |

> char和tinytext都可以表示255个字符，但是当需要索引的时候，char可以对整个字段进行索引，但是tinytext不能



* 字符串类型的修饰属性
  * NOT NULL
  * NULL
  * DEFAULT
  * CHARACTER SET      #字符集
  * COLLATION              #排序规则，默认从表继承，表没有，从数据库继承，数据库服务器继承，这样一层一层的继承关系

* 数值类型的修饰符
  * AUTO_INCREMENT        #自动增长
    * 该字段一定不能为空 NOT NULL
    * 该列一定要创建索引，要么是主键索引，要么是唯一键索引
    * 只能是正数，所以一般声明为 UNSIGNED
    * MySQL内部有一个函数 ： LAST_INSERT_ID() 最近一次生成 auto_increment的值： SELECT LAST_INSERT_ID();
    * eg: 

```
#创建auto_increment的列
CREATE TABLE test(
	id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY
	name CHAR(100)
);

#查看最后一次生成的auto_increment的值
SELECT LAST_INSERT_ID();
```

* 浮点数
  * DOUBLE(g.f)			# DOUBLE(3.2)      # 1.36
  * FLOAT(g.f)                      
* 布尔值  相当于 TINYINT(1) 0-255   
  * BOOLEAN
  * BOOL
* 日期时间型

|           |                                                |        |              |
| --------- | ---------------------------------------------- | ------ | ------------ |
| DATE      | '1000-01-01' TO '9999-12-31'                   | 3 byte | '0000-00-00' |
| DATETIME  | '1000-01-01 00:00:01' TO '9999-12-31 23:59:59' | 8 byte |              |
| TIMESTAMP | '1971-01-01 00:00:01' TO '2038-01-18 23:59:59' | 4 byte |              |
| TIME      | '-838:59:59' TO '838:59:58'                    | 3 byte |              |
| YEAR(2)   | 00-99                                          | 1      |              |
| YEAR(4)   | 1901-2155                                      |        |              |

* 日期时间型的修饰符
  * NOT NULL
  * NULL
  * DEFAULT

* ENUM        可以表示65535钟变化，即：枚举的值可以是65535钟个选择

```
rtype ENUM('A','PRT','CNAME')
```



* SET      可以表示1-64个字符串，表中存储的是字符的下标索引，所以这个也没有必要创建索引

```
#查看服务器支持的字符集
SHOW CHARACTER SET;

#显示各个字符集下的排序规则
SHOW COLLATION;
```



1. 值类型
2. 占据多大的存储空间
3. 定长还是变长
4. 如何比较及排序法则
5. 是否能够索引





# SQL模型定义

* ANSI QUOTES  : 双引号和反引号只能用来引用字段名称，表名等，而单引号只能用来引用字符串
* IGNORE SPACE ： 忽略多余的空白字符
* STRICT_ALL_TABLES: 所有的非法的数值都是不允许的
* STRICT_TRANS_TABLES : 向支持事物的表中插入非法数据的时候是不允许的
* TRADITIONAL : 



```
#查看SQL 模型
SHOW GLOBAL VARIABLES LIKE 'sql_mode';
```





# MySQL的服务器变量

按照作用域，分成两类：

* 全局变量

  SHOW GOLBAL VARIABLES;  #只有

* 会话变量

  SHOW [SESSION] VARIABLES;  



按照生效时间划分：

* 动态调整：立即生效
  * 全局：对当前会话无效，只对新建立的会话有效
  * 会话：及时生效，但只对当前会话有效
* 静态调整：需要服务器重启
  * 写在配置文件中
  * 通过参数传递给 mysqld进程

```
#查看服务器变量
SELECT @@global.sql_mode
SELECT @@session.sql_mode
```

* 设定变量的值

```
SET GLOBAL|SESSION 变量名='value'

SET GLOBAL sql_mode='strict_all_tables';

SET SESSION sql_mode='strict_trans_tables';
```





# SQL语句

## 数据库



```
#创建数据库
mysql> HELP CREATE DATABASE;
Name: 'CREATE DATABASE'
Description:
Syntax:
CREATE {DATABASE | SCHEMA} [IF NOT EXISTS] db_name
    [create_specification] ...

create_specification:
    [DEFAULT] CHARACTER SET [=] charset_name
  | [DEFAULT] COLLATE [=] collation_name

#创建数据库
CREATE DATABASE [IF NOT EXISTS] db_name [CHARACTER SET][=]charset_name COLLATE [=] collation_name

mysql>  CREATE DATABASE IF NOT EXISTS students CHARACTER SET = 'utf8' COLLATE = 'utf8_general_ci';
Query OK, 1 row affected (0.00 sec)


#我们进入到student库对应的目录文件下
[root@localhost mysql]# cd /var/lib/mysql/students/
[root@localhost students]# ll
total 4
-rw-rw----. 1 mysql mysql 61 Nov  1 00:38 db.opt
[root@localhost students]# cat db.opt	#这里就可以看到定义的变量 
default-character-set=utf8			
default-collation=utf8_general_ci
[root@localhost students]# 

#修改数据库
我们对数据库修改，只能修改 ”字符集和排序规则“
mysql> HELP ALTER DATABASE;
Name: 'ALTER DATABASE'
Description:
Syntax:
ALTER {DATABASE | SCHEMA} [db_name]
    alter_specification ...
ALTER {DATABASE | SCHEMA} db_name
    UPGRADE DATA DIRECTORY NAME

alter_specification:
    [DEFAULT] CHARACTER SET [=] charset_name
  | [DEFAULT] COLLATE [=] collation_name

#删除数据库
mysql> HELP DROP DATABASE;
Name: 'DROP DATABASE'
Description:
Syntax:
DROP {DATABASE | SCHEMA} [IF EXISTS] db_name

#数据库重命名：一般我们并不会重新修改数据库的名称
```



## 表

### 创建表

```
#创建表
mysql> HELP CREATE TABLE;
Name: 'CREATE TABLE'
Description:
Syntax:
#方式1：直接定义一张空表
CREATE [TEMPORARY] TABLE [IF NOT EXISTS] tbl_name
    (create_definition,...)
    [table_options]
    [partition_options]

#方式2：从其他表中查询到数据，以这些数据创建新的表
CREATE [TEMPORARY] TABLE [IF NOT EXISTS] tbl_name
    [(create_definition,...)]
    [table_options]
    [partition_options]
    [IGNORE | REPLACE]
    [AS] query_expression		#查询表达式

#方式3：以其他表为模板创建空表
CREATE [TEMPORARY] TABLE [IF NOT EXISTS] tbl_name
    { LIKE old_tbl_name | (LIKE old_tbl_name) }

create_definition:
    col_name column_definition
  | [CONSTRAINT [symbol]] PRIMARY KEY [index_type] (index_col_name,...)
      [index_option] ...
  | {INDEX|KEY} [index_name] [index_type] (index_col_name,...)
      [index_option] ...
  | [CONSTRAINT [symbol]] UNIQUE [INDEX|KEY]
      [index_name] [index_type] (index_col_name,...)
      [index_option] ...
  | {FULLTEXT|SPATIAL} [INDEX|KEY] [index_name] (index_col_name,...)
      [index_option] ...
  | [CONSTRAINT [symbol]] FOREIGN KEY
      [index_name] (index_col_name,...) reference_definition
  | CHECK (expr)

column_definition:
    data_type [NOT NULL | NULL] [DEFAULT default_value]
      [AUTO_INCREMENT] [UNIQUE [KEY]] [[PRIMARY] KEY]
      [COMMENT 'string']
      [COLUMN_FORMAT {FIXED|DYNAMIC|DEFAULT}]
      [STORAGE {DISK|MEMORY|DEFAULT}]
      [reference_definition]

data_type:
    BIT[(length)]
  | TINYINT[(length)] [UNSIGNED] [ZEROFILL]
  | SMALLINT[(length)] [UNSIGNED] [ZEROFILL]
  | MEDIUMINT[(length)] [UNSIGNED] [ZEROFILL]
  | INT[(length)] [UNSIGNED] [ZEROFILL]
  | INTEGER[(length)] [UNSIGNED] [ZEROFILL]
  | BIGINT[(length)] [UNSIGNED] [ZEROFILL]
  | REAL[(length,decimals)] [UNSIGNED] [ZEROFILL]
  | DOUBLE[(length,decimals)] [UNSIGNED] [ZEROFILL]
  | FLOAT[(length,decimals)] [UNSIGNED] [ZEROFILL]
  | DECIMAL[(length[,decimals])] [UNSIGNED] [ZEROFILL]
  | NUMERIC[(length[,decimals])] [UNSIGNED] [ZEROFILL]
  | DATE
  | TIME[(fsp)]
  | TIMESTAMP[(fsp)]
  | DATETIME[(fsp)]
  | YEAR
  | CHAR[(length)]
      [CHARACTER SET charset_name] [COLLATE collation_name]
  | VARCHAR(length)
      [CHARACTER SET charset_name] [COLLATE collation_name]
  | BINARY[(length)]
  | VARBINARY(length)
  | TINYBLOB
  | BLOB[(length)]
  | MEDIUMBLOB
  | LONGBLOB
  | TINYTEXT
      [CHARACTER SET charset_name] [COLLATE collation_name]
  | TEXT[(length)]
      [CHARACTER SET charset_name] [COLLATE collation_name]
  | MEDIUMTEXT
      [CHARACTER SET charset_name] [COLLATE collation_name]
  | LONGTEXT
      [CHARACTER SET charset_name] [COLLATE collation_name]
  | ENUM(value1,value2,value3,...)
      [CHARACTER SET charset_name] [COLLATE collation_name]
  | SET(value1,value2,value3,...)
      [CHARACTER SET charset_name] [COLLATE collation_name]
  | spatial_type

index_col_name:
    col_name [(length)] [ASC | DESC]

index_type:
    USING {BTREE | HASH}

index_option:
    KEY_BLOCK_SIZE [=] value
  | index_type
  | WITH PARSER parser_name
  | COMMENT 'string'

reference_definition:
    REFERENCES tbl_name (index_col_name,...)
      [MATCH FULL | MATCH PARTIAL | MATCH SIMPLE]
      [ON DELETE reference_option]
      [ON UPDATE reference_option]

reference_option:
    RESTRICT | CASCADE | SET NULL | NO ACTION | SET DEFAULT

table_options:
    table_option [[,] table_option] ...

table_option:
    AUTO_INCREMENT [=] value
  | AVG_ROW_LENGTH [=] value
  | [DEFAULT] CHARACTER SET [=] charset_name
  | CHECKSUM [=] {0 | 1}
  | [DEFAULT] COLLATE [=] collation_name
  | COMMENT [=] 'string'
  | CONNECTION [=] 'connect_string'
  | {DATA|INDEX} DIRECTORY [=] 'absolute path to directory'
  | DELAY_KEY_WRITE [=] {0 | 1}
  | ENGINE [=] engine_name
  | INSERT_METHOD [=] { NO | FIRST | LAST }
  | KEY_BLOCK_SIZE [=] value
  | MAX_ROWS [=] value
  | MIN_ROWS [=] value
  | PACK_KEYS [=] {0 | 1 | DEFAULT}
  | PASSWORD [=] 'string'
  | ROW_FORMAT [=] 	 # 行格式{DEFAULT|DYNAMIC|FIXED|COMPRESSED|REDUNDANT|COMPACT}
  | STATS_AUTO_RECALC [=] {DEFAULT|0|1}
  | STATS_PERSISTENT [=] {DEFAULT|0|1}
  | STATS_SAMPLE_PAGES [=] value
  | TABLESPACE tablespace_name [STORAGE {DISK|MEMORY|DEFAULT}] #指定表空间
  | UNION [=] (tbl_name[,tbl_name]...)

partition_options:
    PARTITION BY
        { [LINEAR] HASH(expr)
        | [LINEAR] KEY [ALGORITHM={1|2}] (column_list)
        | RANGE{(expr) | COLUMNS(column_list)}
        | LIST{(expr) | COLUMNS(column_list)} }
    [PARTITIONS num]
    [SUBPARTITION BY
        { [LINEAR] HASH(expr)
        | [LINEAR] KEY [ALGORITHM={1|2}] (column_list) }
      [SUBPARTITIONS num]
    ]
    [(partition_definition [, partition_definition] ...)]

partition_definition:
    PARTITION partition_name
        [VALUES
            {LESS THAN {(expr | value_list) | MAXVALUE}
            |
            IN (value_list)}]
        [[STORAGE] ENGINE [=] engine_name]
        [COMMENT [=] 'string' ]
        [DATA DIRECTORY [=] 'data_dir']
        [INDEX DIRECTORY [=] 'index_dir']
        [MAX_ROWS [=] max_number_of_rows]
        [MIN_ROWS [=] min_number_of_rows]
        [TABLESPACE [=] tablespace_name]
        [NODEGROUP [=] node_group_id]
        [(subpartition_definition [, subpartition_definition] ...)]

subpartition_definition:
    SUBPARTITION logical_name
        [[STORAGE] ENGINE [=] engine_name]
        [COMMENT [=] 'string' ]
        [DATA DIRECTORY [=] 'data_dir']
        [INDEX DIRECTORY [=] 'index_dir']
        [MAX_ROWS [=] max_number_of_rows]
        [MIN_ROWS [=] min_number_of_rows]
        [TABLESPACE [=] tablespace_name]
        [NODEGROUP [=] node_group_id]

query_expression:
    SELECT ...   (Some valid select or union statement)

CREATE TABLE creates a table with the given name. You must have the
CREATE privilege for the table.

By default, tables are created in the default database, using the
InnoDB storage engine. An error occurs if the table exists, if there is no default database, or if the database does not exist.


#方式1
CREATE TABLE [IF NOT EXISTS] (col_name data_type 修饰符，索引)
CREATE TABLE tb1(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name CHAR(20) NOT NULL,
    age TINYINT NOT NULL);
    
CREATE TABLE tb1(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	name CHAR(20) NOT NULL,
    age TINYINT NOT NULL,
    PRIMARY KEY(id), #单独定义索引，有时候，我们需要将多个字段放在一起作为主键，此时就可以像这样单独定义： PRIMARY KEY(id,name) #将id,name作为联合主键
    UNIQUE KEY(name), #单独定义唯一键，也是可以在定义字段的后面写
    INDEX KEY(id),   #单独定义索引，也是可以直接在字段上加这个来标明是索引
    ...);
    
    
mysql> CREATE TABLE courses(cid TINYINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    -> course VARCHAR(50) NOT NULL,
    -> other CHAR(1)
    -> );
    
#查看表的格式
mysql> SHOW TABLE STATUS LIKE 'courses' \G
*************************** 1. row ***************************
           Name: courses
         Engine: InnoDB
        Version: 10
     Row_format: Compact
           Rows: 0
 Avg_row_length: 0
    Data_length: 16384
Max_data_length: 0
   Index_length: 0
      Data_free: 0
 Auto_increment: 1
    Create_time: 2018-11-01 01:53:24
    Update_time: NULL
     Check_time: NULL
      Collation: utf8_general_ci
       Checksum: NULL
 Create_options: 
        Comment: 
        
#删除table
DROP TABLE courses;
  
#创建表的时候指定存储引擎    
mysql> CREATE TABLE courses(cid TINYINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, course VARCHAR(50) NOT NULL, other CHAR(1) ) ENGINE=MYISAM;
Query OK, 0 rows affected (0.01 sec)

mysql> SHOW TABLE STATUS LIKE 'courses' \G                                              *************************** 1. row ***************************
           Name: courses
         Engine: MyISAM
        Version: 10
     Row_format: Dynamic
           Rows: 0
 Avg_row_length: 0
    Data_length: 0
Max_data_length: 281474976710655
   Index_length: 1024
      Data_free: 0
 Auto_increment: 1
    Create_time: 2018-11-01 01:58:46
    Update_time: 2018-11-01 01:58:46
     Check_time: NULL
      Collation: utf8_general_ci
       Checksum: NULL
 Create_options: 
        Comment: 
1 row in set (0.00 sec);


#查看表的字段定义
mysql> 
mysql> desc courses;
+--------+---------------------+------+-----+---------+----------------+
| Field  | Type                | Null | Key | Default | Extra          |
+--------+---------------------+------+-----+---------+----------------+
| cid    | tinyint(3) unsigned | NO   | PRI | NULL    | auto_increment |
| course | varchar(50)         | NO   |     | NULL    |                |
| other  | char(1)             | YES  |     | NULL    |                |
+--------+---------------------+------+-----+---------+----------------+
3 rows in set (0.00 sec)


#查看表的索引
mysql> SHOW INDEXES FROM courses;  #从这里可以知道，主键是有创建索引的
+---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
| Table   | Non_unique 是否为“非唯一” | Key_name | Seq_in_index(在表中的第几个索引) | Column_name(索引名称) | Collation(排序方式)| Cardinality | Sub_part | Packed | Null | Index_type(索引类型) | Comment | Index_comment |
+---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
| courses |          0 | PRIMARY  |            1 | cid         | A         |           0 |     NULL | NULL   |      | BTREE      |         |               |
+---------+------------+----------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
1 row in set (0.00 sec)


#通过select创建表
CREATE TABLE test_courses 
SELECT * FROM courses WHERE cid<=2;

#显示表的结构,注意：这样创建出来的新表字段是相同的，但是字段的修饰符将不会存在了
desc test_courses;
desc courses;


#以其他表为模板创建新的表
CREATE TABLE test LIKE courses;
#这时候显示表结构是一样的，字段的修饰符也是一样的
desc test_courses;
desc courses;


#单字段
PRIMARY KEY
UNIQUE KEY

#单字段或多字段
PRIMARY KEY (col,...)
UNIQUE KEY(col,...)
INDEX (col,...)
```



### 修改表

```
#修改表帮助 
mysql> HELP ALTER TABLE;
Name: 'ALTER TABLE'
Description:
Syntax:
ALTER [ONLINE|OFFLINE] [IGNORE] TABLE tbl_name
    [alter_specification [, alter_specification] ...]
    [partition_options]

alter_specification:
    table_options
  | ADD [COLUMN] col_name column_definition
        [FIRST | AFTER col_name]
  | ADD [COLUMN] (col_name column_definition,...)
  | ADD {INDEX|KEY} [index_name]
        [index_type] (index_col_name,...) [index_option] ...
  | ADD [CONSTRAINT [symbol]] PRIMARY KEY
        [index_type] (index_col_name,...) [index_option] ...
  | ADD [CONSTRAINT [symbol]]
        UNIQUE [INDEX|KEY] [index_name]
        [index_type] (index_col_name,...) [index_option] ...
  | ADD FULLTEXT [INDEX|KEY] [index_name]
        (index_col_name,...) [index_option] ...
  | ADD SPATIAL [INDEX|KEY] [index_name]
        (index_col_name,...) [index_option] ...
  | ADD [CONSTRAINT [symbol]]
        FOREIGN KEY [index_name] (index_col_name,...)
        reference_definition
  | ALGORITHM [=] {DEFAULT|INPLACE|COPY}
  | ALTER [COLUMN] col_name {SET DEFAULT literal | DROP DEFAULT}
  | CHANGE [COLUMN] old_col_name new_col_name column_definition
        [FIRST|AFTER col_name]
  | [DEFAULT] CHARACTER SET [=] charset_name [COLLATE [=] collation_name]
  | CONVERT TO CHARACTER SET charset_name [COLLATE collation_name]
  | {DISABLE|ENABLE} KEYS
  | {DISCARD|IMPORT} TABLESPACE
  | DROP [COLUMN] col_name
  | DROP {INDEX|KEY} index_name
  | DROP PRIMARY KEY
  | DROP FOREIGN KEY fk_symbol
  | FORCE
  | LOCK [=] {DEFAULT|NONE|SHARED|EXCLUSIVE}
  | MODIFY [COLUMN] col_name column_definition
        [FIRST | AFTER col_name]
  | ORDER BY col_name [, col_name] ...
  | RENAME [TO|AS] new_tbl_name
  | ADD PARTITION (partition_definition)
  | DROP PARTITION partition_names
  | TRUNCATE PARTITION {partition_names | ALL}
  | COALESCE PARTITION number
  | REORGANIZE PARTITION partition_names INTO (partition_definitions)
  | EXCHANGE PARTITION partition_name WITH TABLE tbl_name
  | ANALYZE PARTITION {partition_names | ALL}
  | CHECK PARTITION {partition_names | ALL}
  | OPTIMIZE PARTITION {partition_names | ALL}
  | REBUILD PARTITION {partition_names | ALL}
  | REPAIR PARTITION {partition_names | ALL}
  | REMOVE PARTITIONING

index_col_name:
    col_name [(length)] [ASC | DESC]

index_type:
    USING {BTREE | HASH}

index_option:
    KEY_BLOCK_SIZE [=] value
  | index_type
  | WITH PARSER parser_name
  | COMMENT 'string'

table_options:
    table_option [[,] table_option] ...

table_option:
    AUTO_INCREMENT [=] value
  | AVG_ROW_LENGTH [=] value
  | [DEFAULT] CHARACTER SET [=] charset_name
  | CHECKSUM [=] {0 | 1}
  | [DEFAULT] COLLATE [=] collation_name
  | COMMENT [=] 'string'
  | CONNECTION [=] 'connect_string'
  | {DATA|INDEX} DIRECTORY [=] 'absolute path to directory'
  | DELAY_KEY_WRITE [=] {0 | 1}
  | ENGINE [=] engine_name
  | INSERT_METHOD [=] { NO | FIRST | LAST }
  | KEY_BLOCK_SIZE [=] value
  | MAX_ROWS [=] value
  | MIN_ROWS [=] value
  | PACK_KEYS [=] {0 | 1 | DEFAULT}
  | PASSWORD [=] 'string'
  | ROW_FORMAT [=] {DEFAULT|DYNAMIC|FIXED|COMPRESSED|REDUNDANT|COMPACT}
  | STATS_AUTO_RECALC [=] {DEFAULT|0|1}
  | STATS_PERSISTENT [=] {DEFAULT|0|1}
  | STATS_SAMPLE_PAGES [=] value
  | TABLESPACE tablespace_name [STORAGE {DISK|MEMORY|DEFAULT}]
  | UNION [=] (tbl_name[,tbl_name]...)
  
  
# 添加，删除，修改字段
## 修改字段名：change既可以修改字段名称，又可以修改字段的修饰符；但是MODIFY只能修改字段的修饰符
ALTER TABLE test CHANGE name iname VARCHAR(100);

## 新增表字段
ALTER TABLE test ADD start_time DATE NOT NULL;

## 删除表
DROP TABLE test;

# 添加，删除，修改索引
ALTER TABLE test ADD UNIQUE KEY (name);

# 修改表名
ALTER TABLE test RENAME TO test_newname;
RENAME TABLE test TO test_newname;


#修改表属性

#添加外键
ALTER TABLE student ADD FOREIGN KEY (cid) FEFERENCES courses(cid);

```





唯一键，主键，索引的区别：

键：是特殊的索引，也称为约束，可用作索引，属于特殊的索引，B+Tree的索引结构



## 索引

### 创建索引

```
mysql> HELP CREATE INDEX;
Name: 'CREATE INDEX'
Description:
Syntax:
CREATE [ONLINE|OFFLINE] [UNIQUE|FULLTEXT|SPATIAL] INDEX index_name
    [index_type]
    ON tbl_name (index_col_name,...)
    [index_option]
    [algorithm_option | lock_option] ...

index_col_name:
    col_name [(length)] [ASC | DESC]	#length表示只是比较多长的字符

index_option:
    KEY_BLOCK_SIZE [=] value
  | index_type
  | WITH PARSER parser_name
  | COMMENT 'string'

index_type:
    USING {BTREE | HASH}

algorithm_option:
    ALGORITHM [=] {DEFAULT|INPLACE|COPY}

lock_option:
    LOCK [=] {DEFAULT|NONE|SHARED|EXCLUSIVE}


#创建索引
CREATE INDEX name_on_student ON student (name);

#查看表的索引
SHOW INDEXES FROM student;


```



### 删除索引

```

DROP INDEX index_name ON tb1_name;
```



## DML

### select

```
SELECT selecct-list FROM tb WHERE qualification;

#简单查询
SELECT * FROM tb_name;
SELECT f1,f2.f3 FROM tb_name;				#投影
SELECT * FROM tb_name where qualification;	#选择
SELECT DISTINCT gender FROM tb_name ;		#相同的词只显示一次

#正则
SELECT name FROM students WHERE name RLIKE '^[MNY].*'; #查询以 MNY开头的行

#多表查询
##连接方式
	交叉连接：笛卡尔乘积，一张表的一行和另一张表的所有行进行一次连接
		SELECT * FROM tb1,tb2;
	自然连接：等值连接
		SELECT * FROM tb1,tb2 WHERE tb1.f1=tb2.f2
	外连接：
		左外连接:保持左表不变
		右外连接:
	自连接：SELECT * FROM tb1 as t1, tb2 as t2 where t1.cid=t2.sid;
		

#子查询
##年龄大于平均年龄的人:比较操作符中使用子查询，子查询只能返回单个值
SELECT name FROM students WHERE age > (SELECT AVG(age) FROM students);

##在from中使用子查询
SELECT a.name FROM
(
	SELECT name,age FROM students
) a;


```

### insert 

```

#方式1
INSERT INTO tb_name(col1,col2,col3,..) VALUES(val1,val2...)[,(),...]

#方式2
INSERT INTO tb_name SET name='tom';


#方式3
INSERT INTO tb_name SELECT f1,f2 FROM tb_2;
```







## view视图

```
mysql> HELP CREATE VIEW;
Name: 'CREATE VIEW'
Description:
Syntax:
CREATE
    [OR REPLACE]
    [ALGORITHM = {UNDEFINED | MERGE | TEMPTABLE}]
    [DEFINER = { user | CURRENT_USER }]
    [SQL SECURITY { DEFINER | INVOKER }]
    VIEW view_name [(column_list)]
    AS select_statement
    [WITH [CASCADED | LOCAL] CHECK OPTION]


#创建视图
CREATE VIEW sct AS (SELECT name FROM students);

#视图会被当做表
SHOW TABLES;

#使用视图
SELECT * FROM sct;

#删除视图
DROP VIEW sct;

#view没有update,直接删除原来，然后新创建一个视图


#查看视图的创建语句
SHOW CREATE TABLE students;
SHOW CREATE VIEW sct;


```







# 约束(constraint)

* 外键约束：引用完整性

* 主键约束：唯一，不能为空，一张表中只能有一个主键

* 唯一性约束：每一行的某个字段不允许出现相同的值，但是可以为空，一张表可以有多个

* 检查性约束：age TINYINT  && 0<age<200 (年龄的常理值)




# 数据库操作

* DML
* DCL
* DDL

```
#CREATE 
	##创建库
	CREATE DATABASE db_name;
    CREATE DATABASE IF NOT EXISTS db_name;	#如果存在就创建
    
    ##创建表
    CREATE TABLE tb_name(col1 ,col2, col3...);
    USE my_db_name;
    CREATE TABLE students(name CHAR(20), age TINYINT UNSIGNED, gender CHAR(1) NOT NULL);
    #查看表
    SHOW TABLES [FROM db_name];
    #查看表结构
    DESC tb_name;
    DESC student; #注意表名是区分大小写的，因为表是要对应单个文件的，而文件的名称在不同的文件系统上是不同的(有的文件系统区分大小写，有的不区分)
    #删除表
    DROP TABLE [IF EXISTS] tb_name;
    
    #修改表(help alter table 这个很重要，你不可能记住所有的定义)
    ALTER TABLE tb_name #还可以指定字段的位置
		ADD [COLUMN] col_name column_definition [FIRST | AFTER col_name]
		CHANGE [COLUMN] old_name new_col_name column_definition [FIRST | AFTER col_name]
		
    ALTER TABLE tb_name;
    	MODIFY:修改字段的属性
    	CHANGE：修改字段名称
    	ADD：添加一个字段
    	DROP:删除一个字段
	
	ALTER TABLE student ADD course VARCHAR(100)  #会在所有的字段后面添加上这个字段
	ALTER TABLE student CAHNGE course course_new VARCHAR(101) 
	

#DROP
	DROP DATABASE db_name;

#INSERT 
	INSERT INTO tb_name(col1, col2,...) VALUES ('','',...);
	#插入一批数据
	INSERT INTO tb_name(col1, col2,...) VALUES ('','',...),('','',...) ..;

#UPDATE
	UPDATE tb_name set column_name=value WHERE... #如果不指定条件的话，会改变所有的行，这样很危险
	
#DELETE
	DELETE FROM tb_name where 
	
#TRUCATE 清空表
	TRUCATE TABLE tb_name
	
#SELECT 
	SELECT column,... FROM tb_name WHERE CONDITIONS
	
	

#创建用户:通过'USERNAME'@'HOST' 唯一的确定一个用户
	CREATE USER 'USERNAME'@'HOST' IDENTIFIED BY 'PASSWD'
	DROP USER 'USERNAME'@'HOST' 
	#jerry@localhost  , jerry@127.0.0.1不是同一个用户
	
	HOST:
		IP
		HOSTNAME
		NETWORK
		通配符：
			_ : 匹配任意单个字符； 172.16.0._   表示 172.16.0.1 - 172.16.0.9
			% ：匹配任意长度的任意字符， jerry@'%' 表示从所有的主机上jerry上可以登录


#授权用户
	GRANT pri1, pri2,...  ON db_name.tb_name TO 'USERNAME'@'HOST' [IDENTIFIED BY 'PWD'] #修改密码是可选的
	#如果用户不存在，那么直接创建用户，并授权
	
	GRANT ALL PRIVILEGES ON mydb.* TO 'jerry'@'%' #授权所有的权限给jerry
	
#取消授权
	REVOKE pri1,pri2.. ON db_name.tb_name FROM 'USERNAME'@'HOST' 

#查看用户的权限
	SHOW GRANTS FOR 'USERNAME'@'HOST' 
	

```



# MySQL的mysql库

```shell
USE mysql;
#可以查看用户可以从哪些host登录
SELECT User, Host, Password FROM user;


```





# mysql通用二进制格式安装



官网安装文档：

https://dev.mysql.com/doc/refman/5.6/en/binary-installation.html

## install

```
#前置依赖
shell> yum search libaio  # search for info
shell> yum install libaio # install library


#安装
##创建用户和组
shell> groupadd mysql
#-r创建的是系统用户，为系统的某些程序使用，如这里的mysql,-r不会创建家目录
shell> useradd -r -g mysql -s /bin/false mysql		
##解压文件在 /usr/local目录下(建议是安装在这个目录下的，也可以改)
shell> cd /usr/local
shell> tar zxvf /path/to/mysql-VERSION-OS.tar.gz
##创建软连接
shell> ln -s full-path-to-mysql-VERSION-OS mysql
shell> cd mysql
##初始化数据库
shell> scripts/mysql_install_db --user=mysql
##启动
shell> bin/mysqld_safe --user=mysql &
# Next command is optional
##可选，加入到服务进程中
shell> cp support-files/mysql.server /etc/init.d/mysql.server  #这样启动的时候就可以使用 systemctl start msyql.server  (centos7)
##可选，加入环境变量中
shell> export PATH=$PATH:/usr/local/mysql/bin
```



## 初始化日志，启动日志说明

```shell
#mysql初始化的时候，有下面的日志
2018-10-28 10:04:08 1401 [Note] InnoDB: 5.6.40 started; log sequence number 1625977
2018-10-28 10:04:08 1401 [Note] Binlog end
2018-10-28 10:04:08 1401 [Note] InnoDB: FTS optimize thread exiting.
2018-10-28 10:04:08 1401 [Note] InnoDB: Starting shutdown...
2018-10-28 10:04:10 1401 [Note] InnoDB: Shutdown completed; log sequence number 1625987
OK

To start mysqld at boot time you have to copy
##cp support-files/mysql.server /etc/init.d/mysql.server 设置开机自启动
support-files/mysql.server to the right place for your system

#记得为root用户设置密码
PLEASE REMEMBER TO SET A PASSWORD FOR THE MySQL root USER !
To do so, start the server, then issue the following commands:

  ./bin/mysqladmin -u root password 'new-password'
  ./bin/mysqladmin -u root -h localhost.localdomain password 'new-password'

Alternatively you can run:

  ./bin/mysql_secure_installation

which will also give you the option of removing the test
databases and anonymous user created by default.  This is
strongly recommended for production servers.

See the manual for more instructions.

#启动MySQL进程的命令
You can start the MySQL daemon with:

  cd . ; ./bin/mysqld_safe &

You can test the MySQL daemon with mysql-test-run.pl

  cd mysql-test ; perl mysql-test-run.pl

Please report any problems at http://bugs.mysql.com/

The latest information about MySQL is available on the web at

  http://www.mysql.com

Support MySQL by buying support/licenses at http://shop.mysql.com

WARNING: Found existing config file ./my.cnf on the system.
Because this file might be in use, it was not replaced,
but was used in bootstrap (unless you used --defaults-file)
and when you later start the server.
The new default config file was created as ./my-new.cnf,
please compare it with your file and take the changes you need.

#启动MySQL进程的时候，默认是读取的是/etc/my.cnf文件，可以--defaults-file指定配置文件
WARNING: Default config file /etc/my.cnf exists on the system
This file will be read by default by the MySQL server
If you do not want to use this, either remove it, or use the
--defaults-file argument to mysqld_safe when starting the server

[root@localhost mysql]# echo $?

#mysql进程启动的时候，有下面的日志
[root@localhost mysql]# bin/mysqld_safe --user=mysql &
[1] 2077
[root@localhost mysql]# 181028 10:28:10 mysqld_safe Logging to '/var/log/mysqld.log'.
181028 10:28:10 mysqld_safe Starting mysqld daemon with databases from /var/lib/mysql

#查看是否存在mysql进程
[root@localhost mysql]# ps -ef|grep mysql
root      1705  1683  0 10:23 pts/2    00:00:00 tail -f mysqld.log
root      2077  1245  0 10:28 pts/0    00:00:00 /bin/sh bin/mysqld_safe --user=mysql
mysql     2234  2077  0 10:28 pts/0    00:00:00 /usr/local/mysql/bin/mysqld --basedir=/usr/local/mysql --datadir=/var/lib/mysql --plugin-dir=/usr/local/mysql/lib/plugin --user=mysql --log-error=/var/log/mysqld.log --pid-file=/var/run/mysqld/mysqld.pid --socket=/var/lib/mysql/mysql.sock
root      2257  1245  0 10:28 pts/0    00:00:00 grep mysql
```

## mysql的配置文件

mysql默认的是读取 ： /etc/my.cnf这个配置文件，如果我们想要改变这个配置文件，在启动mysql的时候，可以指定 --defaults-file 配置文件 (bin/mysqld_safe --user=mysql --defaults-file config-file)

```
[root@localhost mysql]# cat /etc/my.cnf 
[mysqld]
datadir=/var/lib/mysql		#数据目录,建立将数据目录放在一个独立的大磁盘上，最好是逻辑卷，这样可以扩展
socket=/var/lib/mysql/mysql.sock	#sock文件目录，客户端访问服务器端的mysql是通过tcp进行的，但是如果是本地客户端访问，则通过socket进行，这样更快
user=mysql
# Disabling symbolic-links is recommended to prevent assorted security risks
symbolic-links=0

[mysqld_safe]
log-error=/var/log/mysqld.log			#日志文件，这里可以看到启动报错的日志信息
pid-file=/var/run/mysqld/mysqld.pid		#pid文件


#可以看到 /var/lib/mysql 目录下有sock文件，和数据库对应的目录，如test这个数据库
[root@localhost mysql]# ll /var/lib/mysql
total 110608
-rw-rw----. 1 mysql mysql       56 Oct 28 10:11 auto.cnf
-rw-rw----. 1 mysql mysql 12582912 Oct 28 10:28 ibdata1
-rw-rw----. 1 mysql mysql 50331648 Oct 28 10:28 ib_logfile0
-rw-rw----. 1 mysql mysql 50331648 Oct 28 10:04 ib_logfile1
drwx------. 2 mysql mysql     4096 Oct 28 10:04 mysql
srwxrwxrwx. 1 mysql mysql        0 Oct 28 10:28 mysql.sock
drwx------. 2 mysql mysql     4096 Oct 28 10:04 performance_schema
drwx------. 2 mysql mysql     4096 Oct 28 10:04 test
[root@localhost mysql]# 
```



配置文件格式，集中式配置文件，可以为多个程序提供配置



```
/etc/my.cnf --> /etc/mysql/my.cnf --> $BASEDIR(安装)/my.cnf -->data-dir/my.cnf--> ~(mysql的home)/.my.cnf 依次覆盖

#客户端配置
[mysql]

#所有的客户端的通用配置
[client]
socket  = /var/lib/mysql/mysql.sock	#这个需要指定，不然有可能连接不上

#服务器端配置
[mysqld]
socket  = /var/lib/mysql/mysql.sock
# try number of cpu's * 2 for thread_concurrency : cpu的个数乘以2
thread_concurrency = 8	#线程并发量
datadir = /mydata/data	#数据存放的目录


```



## 生产环境初始化数据目录的过程

1. 为MySQL的数据存放在一个逻辑卷中，如 /mydata/mysql
2. 为上面的MySQL存放目录授权 chown -R mysql:mysql /mydata/mysql
3. scripts/mysql_install_db  --basedir=/mydata/mysql  --user=mysql 初始化mysql，这样在该目录下就有一些初始化的库文件
4. 修改配置文件 /etc/my.cnf



## 初始化文件

scripts/mysql_install_db --help 在初始化的时候会生成一个/etc/my.cnf文件，这个文件会指定数据目录，sock的位置，pid文件的位置，日志文件的位置

```
[root@localhost mysql]# scripts/mysql_install_db --help
Usage: scripts/mysql_install_db [OPTIONS]
  --basedir=path       The path to the MySQL installation directory.
  --builddir=path      If using --srcdir with out-of-directory builds, you
                       will need to set this to the location of the build
                       directory where built files reside.
  --cross-bootstrap    For internal use.  Used when building the MySQL system
                       tables on a different host than the target.
  --datadir=path       The path to the MySQL data directory.
                       If missing, the directory will be created, but its
                       parent directory must already exist and be writable.
  --defaults-extra-file=name
                       Read this file after the global files are read.
  --defaults-file=name Only read default options from the given file name.
  --force              Causes mysql_install_db to run even if DNS does not
                       work.  In that case, grant table entries that
                       normally use hostnames will use IP addresses.
  --help               Display this help and exit.                     
  --ldata=path         The path to the MySQL data directory. Same as --datadir.
  --no-defaults        Don't read default options from any option file.
  --keep-my-cnf        Don't try to create my.cnf based on template. 
                       Useful for systems with working, updated my.cnf.
                       Deprecated, will be removed in future version.
  --random-passwords   Create and set a random password for all root accounts
                       and set the "password expired" flag,
                       also remove the anonymous accounts.
  --rpm                For internal use.  This option is used by RPM files
                       during the MySQL installation process.
  --skip-name-resolve  Use IP addresses rather than hostnames when creating
                       grant table entries.  This option can be useful if
                       your DNS does not work.
  --srcdir=path        The path to the MySQL source directory.  This option
                       uses the compiled binaries and support files within the
                       source tree, useful for if you don't want to install
                       MySQL yet and just want to create the system tables.
  --user=user_name     The login username to use for running mysqld.  Files
                       and directories created by mysqld will be owned by this
                       user.  You must be root to use this option.  By default
                       mysqld runs using your current login name and files and
                       directories that it creates will be owned by you.
Any other options are passed to the mysqld program.

```



## 启动报错解决

```
2018-10-28 10:24:50 1866 [ERROR] Can't start server: can't check PID filepath: No such file or directory
2018-10-28 10:26:37 2050 [ERROR] /usr/local/mysql/bin/mysqld: Can't create/write to file '/var/run/mysqld/mysqld.pid' (Errcode: 13 - Permission denied)
2018-10-28 10:26:37 2050 [ERROR] Can't start server: can't create PID file: Permission denied


##错误的原因
mysql的pid文件不能创建，没有这个目录var/run/mysqld/，所以我们创建这个目录
mkdir var/run/mysqld/
##但是还是有写权限的问题，因为我们启动MySQL进程的时候，是以mysql用户去启动的，所以需要给这个目录授权
 chown -R mysql:mysql /var/run/mysqld
 
```



# MySQL安装完成之后有哪些变化

## 生成的用户

```
#会生成3个用户
root@127.0.0.1
root@localhost
root@hostname #当前主机名

#两个匿名用户		#建议将这些匿名用户删除
''@localhost
''@hostname

DROP USER 'USERNAME'@'HOST' 
#或者删除 mysql.user的表中的对应的记录

```



## root用户密码设置

```
#设置用户密码
##1使用客户端命令
mysql>SET PASSWORD FOR 'USERNAME'@'HOST' = PASSWORD('123456')


##2在shell中使用命令行
shell>mysqladmin -uUSERNAME -hHOST -p password 'new-passwd' #回车指定老密码，才能改新密码

##3在客户端改表:mysql.user的字段
mysql>UPDATE mysql.user SET Password=PASSWORD('new-passwd') where User='hostname' AND Host='host'

##这里修改了密码并不会立即生效，需要加载到内存中，所以需要flush，让MySQL重读授权表；
##FLUSH PRIVILEGES;
```



# mysql的两类变量

* 服务器变量
  * 定义MySQL服务器运行属性 （数据目录，log定义,初始化的设定都可以在这里看到）
    * SHOW GLOBAL VARIABLES 

```

mysql> SHOW GLOBAL VARIABLES;

| pid_file | /var/run/mysqld/mysqld.pid  
| plugin_dir | /usr/local/mysql/lib/plugin/     
| skip_external_locking | ON  
| skip_name_resolve  | OFF   

#模糊查询某个我们需要的变量
mysql> SHOW GLOBAL VARIABLES LIKE '%data%';
+-------------------------------+------------------------+
| Variable_name                 | Value                  |
+-------------------------------+------------------------+
| character_set_database        | latin1                 |
| collation_database            | latin1_swedish_ci      |
| datadir                       | /var/lib/mysql/        |
| innodb_data_file_path         | ibdata1:12M:autoextend |
| innodb_data_home_dir          |                        |
| innodb_stats_on_metadata      | OFF                    |
| max_length_for_sort_data      | 1024                   |
| metadata_locks_cache_size     | 1024                   |
| metadata_locks_hash_instances | 8                      |
| myisam_data_pointer_size      | 6                      |
| skip_show_database            | OFF                    |
| updatable_views_with_limit    | YES                    |
+-------------------------------+------------------------+
12 rows in set (0.00 sec)

mysql> 

```



* 状态变量
  * 保存的MySQL服务器运行的统计数据
    * SHOW GLOBAL STATUS

```
mysql> SHOW GLOBAL STATUS;
+-----------------------------------------------+-------------+
| Variable_name                                 | Value       |
+-----------------------------------------------+-------------+
| Aborted_clients                               | 0           |
| Aborted_connects                              | 0           |
| Binlog_cache_disk_use                         | 0           |
| Binlog_cache_use                              | 0           |
| Binlog_stmt_cache_disk_use                    | 0           |
| Binlog_stmt_cache_use                         | 0           |



mysql> SHOW GLOBAL STATUS LIKE '%select%';
+--------------------------+-------+
| Variable_name            | Value |
+--------------------------+-------+
| Com_insert_select        | 0     |
| Com_replace_select       | 0     |
| Com_select               | 6     |	#SELECT命令执行了6次
| Connection_errors_select | 0     |
| Select_full_join         | 0     |
| Select_full_range_join   | 0     |
| Select_range             | 0     |
| Select_range_check       | 0     |
| Select_scan              | 20    |
+--------------------------+-------+
9 rows in set (0.00 sec)


```







# MySQL的默认函数



```
#查询MySQL的版本
mysql> SELECT VERSION();
+-----------+
| VERSION() |
+-----------+
| 5.6.40    |
+-----------+
1 row in set (0.01 sec)



#查看客户端当前使用的数据库版本
mysql> SELECT DATABASE();
+------------+
| DATABASE() |
+------------+
| NULL       |
+------------+
1 row in set (0.00 sec)

mysql> use mysql;
Database changed
mysql> SELECT DATABASE();
+------------+
| DATABASE() |
+------------+
| mysql      |
+------------+
1 row in set (0.00 sec)

mysql> 

#查看当前登录的用户
mysql> SELECT USER();
+----------------+
| USER()         |
+----------------+
| root@localhost |
+----------------+
1 row in set (0.00 sec)

#查看使用的默认存储引擎
mysql> SHOW ENGINES;
+--------------------+---------+----------------------------------------------------------------+--------------+------+------------+
| Engine             | Support | Comment                                                        | Transactions | XA   | Savepoints |
+--------------------+---------+----------------------------------------------------------------+--------------+------+------------+
| InnoDB             | DEFAULT | Supports transactions, row-level locking, and foreign keys     | YES          | YES  | YES        |
| CSV                | YES     | CSV storage engine                                             | NO           | NO   | NO         |
| MRG_MYISAM         | YES     | Collection of identical MyISAM tables                          | NO           | NO   | NO         |
| BLACKHOLE          | YES     | /dev/null storage engine (anything you write to it disappears) | NO           | NO   | NO         |
| MyISAM             | YES     | MyISAM storage engine                                          | NO           | NO   | NO         |
| MEMORY             | YES     | Hash based, stored in memory, useful for temporary tables      | NO           | NO   | NO         |
| ARCHIVE            | YES     | Archive storage engine                                         | NO           | NO   | NO         |
| FEDERATED          | NO      | Federated MySQL storage engine                                 | NULL         | NULL | NULL       |
| PERFORMANCE_SCHEMA | YES     | Performance Schema                                             | NO           | NO   | NO         |
+--------------------+---------+----------------------------------------------------------------+--------------+------+------------+
9 rows in set (0.00 sec)

```

动态SQL：程序设计语言使用函数（mysql_connect())或者方法与RDBMS服务器建立连接，并进行交互，通过建立连接向SQL服务器发送查询语句，并将结果保存至变量中而后进行处理

嵌入式SQL：与动态SQL类似，但是其语句必须在程序编译时完全确定下来，并由预处理器进行处理



# MySQL的内部结构



- 存储管理器
  - 权限及完整性管理器
  - 事物管理器
  - 文件管理器
  - 缓冲区管理器
- 查询管理器
  - DML解释器
  - DDL解释器
  - DCL解释器
  - 查询执行引擎



MySQL为每一个用户单独创建一个线程，而这个用户发起的所有的请求都在这个线程内部完成，



MySQL是单进程，多线程的

- 守护线程 
- 应用线程



![image-20181028235542875](/Users/chenyansong/Documents/note/images/mysql/struct.png)





![image-20181029201013622](/Users/chenyansong/Documents/note/images/mysql/struct2.png)



![struc-detail](/Users/chenyansong/Documents/note/images/mysql/struct-detail.png)

* 表管理器：负责创建，读取，修改 **表结构定义文件**，维护 “表描述符高速缓存”（会将表的结构缓存在内存中），管理表锁

* 表修改(定义)模块：表创建，删除，重命名，移除，更新，插入之类的操作
* 表维护模块：检查，修改，备份，恢复，优化(碎片整理)及解析



* 磁盘块，数据块

  由4个磁盘块组成一个数据块，然后这些数据块是由存储引擎来管理

* 文件中记录组织
  * 堆文件组织：一条记录可以放在文件中的任务地方
  * 顺序文件组织：以某个字段的顺序(搜索码，即索引字段)来存放
    * 使用链表的结构存储，每一行有一个指针指向自己前面的行，有一个指针指向自己后面的行
  * 散列文件组织：人为的将表中的行分为n个子容器，查询的时候进入到对应的子模块中进行查询，对某一个行中的某一个或者某一个字段做hash，hash之后的结果对应于一个子容器（bucket），类似于HBASE

* 表结构定义文件，表数据文件

* 表空间

  * 一个文件防止多个表的数据

* 数据字典：data dictionary，如MySQL的初始化后，mysql这个库就是MySQL的数据字典

  * 各种关系的元数据（表，视图）
    * 关系的名称
    * 字段的名称
    * 字段的类型和长度
    * 视图
    * 完整性约束

* 缓冲区管理器

  我们读取数据的时候，需要将磁盘中的行数据加载到内存中，但是如果我们一次读取的数据很多的时候，内存放不下了，此时需要将内存中最近最少使用的地址置换出内存，这里就有一个 **缓存置换策略**

  什么时候开始置换(例如内存中还剩95%开始置换)，有些快不允许置换(被钉住的块)




# MySQL客户端和服务器通信的方式



* Unix
  * mysql --> mysql.sock --> mysqld
* Windows
  * mysql --> memory(pipe) --> mysqld
* 不在同一台主机上，基于TCP/IP协议
  * mysql -uroot -h172.16.11.11 -p



# mysql客户端和非客户端工具

> 客户端工具

* mysql
* mysqlimport
* mysqldump
* mysqladmin
* mysqlcheck  ： 检查数据库完整性工具

> 非客户端工具

* myisamchk ： 检查isam表
* myisampack ： 压缩 isam表



这些客户端工具都会读取 [client] 配置



# mysql本机不要密码连接



```
#在用户的家目录下
vim ~/.my.cnf
[client]
user=root
password=123456
host=localhost


#然后客户端连接,就可以连接了
shell>mysql

```



# mysql表文件，数据文件

```
进入MySQL的数据目录
[root@localhost mysql]# ll /var/lib/mysql/mysql  #查看mysql库的目录
-rw-rw----. 1 mysql mysql  10684 Oct 28 10:04 user.frm	#user表的表结构定义文件
-rw-rw----. 1 mysql mysql    468 Oct 28 10:04 user.MYD	#user表的表数据文件
-rw-rw----. 1 mysql mysql   2048 Oct 28 10:04 user.MYI	#user表的索引文件

MyISAM
	.frm 表结构定义文件
	.MYD 表数据文件
	.MYI 表的索引文件

InnoDB
	db.opt	当前数据库的默认字符集和字符集的排序规则定义，一些数据库的选项
	.frm  表结构(每个表有一个)
	.idb  表空间(表数据和表索引)(每个表有一个)
	所有表共享一个表空间文件
	建议：每一个表独立的一个表空间文件,进行如下的操作
	
	
SHOW VARIABLES LIKE '%innoDB%';
innodb_file_per_table  	OFF

vim /etc/my.cnf
[mysqld]
innodb_file_per_table=1  #需要重启mysql

#进行了上面的设置之后，这样每一个表都有一个表空间和表结构数据
```





# mysql客户端命令



```
mysql 
	-u root
	-p 3306
	-D my_db	#默认连接的数据库
	-h 192.168.1.11
	
交互式
批处理模式(脚本模式)

#登录客户端执行脚本
mysql>source /root/test.sql

#直接在shell命令行中
shell>msyql -uroot -proot </root/test.sql

#mysql的帮助命令
mysql>\? 		#打印帮助命令

#语句结束符
mysql>delimiter //		#设置语句结束符为 //
mysql>\d ;				#设置语句结束符为 ;

#重要的客户端命令
mysql>\c 				#提前终止语句

mysql>command \g		#无论语句结束符是什么，直接将此语句送至服务器端执行
mysql>command \G		#无论语句结束符是什么，直接将此语句送至服务器端执行，而且以纵向显示

mysql>quit				#退出
mysql>\! ls /root		#执行shell命令
mysql>\W				#显示警告信息
mysql>\w				#不显示警告信息


#mysql中提示符的含义
mysql>			#准备输入命令
	->			#等待结束符
	'>			#等待另一个 单引号
	">			#等待另一个 双引号
	`>			#等待另一个反引号
	/*>			#等待另一个结束注释符		/*   注释 */


#MYSQL的输出格式
shell>mysql -uroot -p --html		#则输出为HTML
shell>mysql -uroot -p --xml			#则输出为xml格式


```



# mysqladmin命令



```
help command
mysql>help SELECT;		#查看select如何使用


mysqladmin 
	-u
	-p
	-h
	create  my_db
	drop	my_db
	ping	#查看数据库是否在线
	processlist		#查看进程列表
	status			#显示mysql的服务器状态
		--sleep	2	#2s显示一次
		--count	3	#只显示2次
	extended-status	#显示状态变量(统计变量，全局的 show global status)
	flush-status		#重置统计status
	
	variables		#服务器的变量(状态开关)
	flush-privileges	#重读授权表
	reload				#同上
	
	flush-logs			#二进制，中继日志滚动
	flush-hosts			#清除主机内部信息(DNS缓存，主机名多次连接失败被封)
	refresh			#关闭所有打开的表，并且滚动日志，类似于(flush-logs && flush-hosts)
	
	shutdown	#关闭mysqld
	version		#版本号，及状态信息
	
	kill		#杀死一个内部线程的
	password	#设置用户密码
	
	start-slave		#启动从服务器的复制线程，主从复制的时候用到
				这两个线程为：SQL thread ， IO thread
	stop-slave		#停止
	
	
#创建数据库
mysqladmin -uroot -h 127.0.0.1 -p create my_db

#删除数据库
mysqladmin -uroot -h 127.0.0.1 -p drop my_db


#ping MySQL服务器端是否在线
[root@localhost bin]# mysqladmin -uroot -h 127.0.0.1 -p ping
Enter password: 
mysqld is alive

#查看进程列表
[root@localhost bin]# mysqladmin -uroot -h 127.0.0.1 -p processlist
Enter password: 
+----+------+-----------------+----+---------+------+-------+------------------+
| Id | User | Host            | db | Command | Time | State | Info             |
+----+------+-----------------+----+---------+------+-------+------------------+
| 27 | root | localhost:43996 |    | Query   | 0    | init  | show processlist |
+----+------+-----------------+----+---------+------+-------+------------------+
[root@localhost bin]# 

#mysql的status
[root@localhost bin]# mysqladmin -uroot -h 127.0.0.1 -p status --sleep 2 --count 4
Enter password: 
Uptime: 3927  Threads: 1  Questions: 159  Slow queries: 0  Opens: 115  Flush tables: 2  Open tables: 28  Queries per second avg: 0.040
Uptime: 3929  Threads: 1  Questions: 160  Slow queries: 0  Opens: 115  Flush tables: 2  Open tables: 28  Queries per second avg: 0.040
Uptime: 3931  Threads: 1  Questions: 161  Slow queries: 0  Opens: 115  Flush tables: 2  Open tables: 28  Queries per second avg: 0.040
Uptime: 3933  Threads: 1  Questions: 162  Slow queries: 0  Opens: 115  Flush tables: 2  Open tables: 28  Queries per second avg: 0.041

```



# 存储引擎

```

#查看使用的默认存储引擎
mysql> SHOW ENGINES;
+--------------------+---------+----------------------------------------------------------------+--------------+------+------------+
| Engine             | Support | Comment                                                        | Transactions | XA   | Savepoints |
+--------------------+---------+----------------------------------------------------------------+--------------+------+------------+
| InnoDB             | DEFAULT(默认使用的引擎) | Supports transactions, row-level locking, and foreign keys     | YES          | YES  | YES        |
| CSV                | YES     | CSV storage engine                                             | NO           | NO   | NO         |
| MRG_MYISAM         | YES     | Collection of identical MyISAM tables                          | NO           | NO   | NO         |
| BLACKHOLE          | YES     | /dev/null storage engine (anything you write to it disappears) | NO           | NO   | NO         |
| MyISAM             | YES     | MyISAM storage engine                                          | NO           | NO   | NO         |
| MEMORY             | YES     | Hash based, stored in memory, useful for temporary tables      | NO           | NO   | NO         |
| ARCHIVE            | YES     | Archive storage engine                                         | NO           | NO   | NO         |
| FEDERATED          | NO      | Federated MySQL storage engine                                 | NULL         | NULL | NULL       |
| PERFORMANCE_SCHEMA | YES     | Performance Schema                                             | NO           | NO   | NO         |
+--------------------+---------+----------------------------------------------------------------+--------------+------+------------+
9 rows in set (0.00 sec)

#查看某个表的属性信息
mysql> SHOW TABLE STATUS LIKE 'user'\G
*************************** 1. row ***************************
           Name: user
         Engine: MyISAM
        Version: 10
     Row_format: Dynamic
           Rows: 2
 Avg_row_length: 152
    Data_length: 468
Max_data_length: 281474976710655
   Index_length: 2048
      Data_free: 164
 Auto_increment: NULL
    Create_time: 2018-10-28 10:04:06
    Update_time: 2018-10-31 20:43:56
     Check_time: NULL
      Collation: utf8_bin
       Checksum: NULL
 Create_options: 
        Comment: Users and global privileges
1 row in set (0.00 sec)

mysql> 
```





# 事物

```
#查看是否自动提交,1或者ON表示启用；0或者OFF表示关闭
mysql>SHOW VARIABLES LIKE 'AUTOCOMMIT';

#
```



```
#查看一些启动时的设置的参数 (类似于 客户端执行： show variables)
[root@localhost bin]# mysqld --help --verbose


```



# 锁



* 锁粒度：从大到小，MySQL服务器仅支持表级别锁，行锁需要由存储引擎完成
  * 表锁
  * 页锁：一个数据块可能存放多条数据
  * 行锁