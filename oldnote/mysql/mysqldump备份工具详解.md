---
title: mysqldump备份工具详解
categories: mysql   
tags: [mysql]
---




# 1.逻辑备份和物理备份
* 逻辑备份: 小于50G的数据量
&emsp;&emsp;原理: 将数据库的数据以逻辑的SQL语句的方式导出
* 物理备份:
&emsp;&emsp;1.scp /application/mysql/data/   拷贝到独立数据库上就可以
&emsp;&emsp;2.xtrabackup开源的物理备份工具

# 2.三种方式来调用mysqldump
```
#Usage: 
mysqldump [OPTIONS] database [tables]
#OR     
mysqldump [OPTIONS] --databases [OPTIONS] DB1 [DB2 DB3...]
#OR     
mysqldump [OPTIONS] --all-databases [OPTIONS]
 
#如果没有指定任何表或使用了--database或--all--database选项，则转储整个数据库。
#要想获得你的版本的mysqldump支持的选项，执行mysqldump --help


```

# 3.mysqldump参数详解
```
--help，-？
   #显示帮助消息并退出
    
--add-drop--database
   #在每个CREATE DATABASE语句前添加DROP DATABASE语句
     
--add-drop-tables
   #在每个CREATE TABLE语句前添加DROP TABLE语句
     
--add-locking
   #用LOCK TABLES和UNLOCK TABLES语句引用每个表转储。重载转储文件时插入得更快
   

--all--database，-A
   #转储所有数据库中的所有表。与使用---database选项相同，在命令行中命名所有数据库

--allow-keywords
   #允许创建关键字列名。应在每个列名前面加上表名前缀

--comments[={0|1}]
   #如果设置为 0，禁止转储文件中的其它信息，例如程序版本、服务器版本和主机。--skip—comments与---comments=0的结果相同。 默认值为1，即包括额外信息

--compact
   #产生少量输出。该选项禁用注释并启用--skip-add-drop-tables、--no-set-names、--skip-disable-keys和--skip-add-locking选项。适合调试输出，生产不适用

--compatible=name
   #产生与其它数据库系统或旧的MySQL服务器更兼容的输出。值可以为ansi、mysql323、mysql40、postgresql、oracle、mssql、db2、maxdb、no_key_options、no_tables_options或者no_field_options。要使用几个值，用逗号将它们隔开。这些值与设置服务器SQL模式的相应选项有相同的含义

--compress，-C
   #压缩在客户端和服务器之间发送的所有信息（如果二者均支持压缩）

--database，-B
   #转储几个数据库。通常情况，mysqldump将命令行中的第1个名字参量看作数据库名，后面的名看作表名。使用该选项，它将所有名字参量看作数据库名。CREATE DATABASE IF NOT EXISTS db_name和USE db_name语句包含在每个新数据库前的输出中

--default-character-set=charset
   #使用charsetas默认字符集。如果没有指定，mysqldump使用utf8
    mysqldump -uroot -p'chenyansong'    --default-character-set=utf8 student >/opt/mysql_bak.sql
--flush-logs，-F
   #开始转储前刷新MySQL服务器日志文件。该选项要求RELOAD权限。请注意如果结合--all--database(或-A)选项使用该选项，根据每个转储的数据库刷新日志。例外情况是当使用--lock-all-tables或--master-data的时候：在这种情况下，日志只刷新一次，在所有 表被锁定后刷新。如果你想要同时转储和刷新日志，应使用--flush-logs连同--lock-all-tables或--master-data

--host=host_name，-h host_name
   #从给定主机的MySQL服务器转储数据。默认主机是localhost

--lock-all-tables，-x
   #所有数据库中的所有表加锁。在整体转储过程中通过全局读锁定来实现。该选项自动关闭--single-transaction和--lock-tables

--master-data[=value]
   #该选项将二进制日志的位置和文件名写入到输出中。该选项要求有RELOAD权限，并且必须启用二进制日志。如果该选项值等于1，位置和文件名被写入CHANGE MASTER语句形式的转储输出，如果你使用该SQL转储主服务器以设置从服务器，从服务器从主服务器二进制日志的正确位置开始。如果选项值等于2，CHANGE MASTER语句被写成SQL注释。如果value被省略，这是默认动作
    #--master-data选项启用--lock-all-tables，除非还指定--single-transaction(在这种情况下，只在刚开始转储时短时间获得全局读锁定。又见--single-transaction。在任何一种情况下，日志相关动作发生在转储时。该选项自动关闭--lock-tables


--no-create-info，-t
   #只分表数据-t,不写重新创建每个转储表的CREATE TABLE语句

--no-data，-d
   #备份表结构-d,不写表的任何行信息。如果你只想转储表的结构这很有用

--password[=password]，-p[password]
   #连接服务器时使用的密码。如果你使用短选项形式(-p)，不能在选项和密码之间有一个空格。如果在命令行中，忽略了--password或-p选项后面的 密码值，将提示你输入一个


--port=port_num，-P port_num
   #用于连接的TCP/IP端口号

--quick，-q
   #该选项用于转储大的表。它强制mysqldump从服务器一次一行地检索表中的行而不是检索所有行并在输出前将它缓存到内存中

--where='where-condition', -w 'where-condition'
   #只转储给定的WHERE条件选择的记录。请注意如果条件包含命令解释符专用空格或字符，一定要将条件引用起来。
   #例如：
"--where=user='jimf'" "-w userid>1" "-wuserid<1"


```



#### Example



```
1.导出所有数据库

该命令会导出包括系统数据库在内的所有数据库

mysqldump -uroot -proot --all-databases >/tmp/all.sql
2.导出db1、db2两个数据库的所有数据

    mysqldump -uroot -proot --databases db1 db2 >/tmp/user.sql

```



