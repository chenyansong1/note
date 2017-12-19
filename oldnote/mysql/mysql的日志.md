---
title: mysql的日志
categories: mysql   
tags: [mysql]
---



# 1.错误日志
## 1.1.配置错误日志
```
#1.在配置文件中指定
vim /data/3306/my.cnf
[mysqld_safe]
log-error=/data/3309/mysql_oldboy3309.err

#2.在启动命令中加入
 mysql_safe --defaults-file=/data/3306/my.cnf --log-error=/data/3306/mysql_oldboy.err
 


#3.在mysql客户端查看error log 的位置
mysql> show variables like 'log_error%';
+---------------+---------------------------------+
| Variable_name | Value                           |
+---------------+---------------------------------+
| log_error     | /data/3309/mysql_oldboy3309.err |
+---------------+---------------------------------+

```




# 2.普通日志
记录客户端连接的信息和执行的sql语句的信息
```
 
mysql> show variables like 'general_log%';
+------------------+---------------------------+
| Variable_name    | Value                     |
+------------------+---------------------------+
| general_log      | OFF                       |
| general_log_file | /data/3309/data/MySQL.log |
+------------------+---------------------------+


#在生产环境下，我们是关闭的，因为mysql的瓶颈就是磁盘IO，而这样的日志很多，将会产生IO，所以我们是关闭的，开启的方法如下：
mysql>set global general_log = on

#在配置文件中修改
general_log = on
general_log_file = /data/3306/data/MySQL_oldboy.log

```


# 3.慢查询日志
 执行时间超过指定时间的sql语句,利用慢查询来的优化sql

## 3.1.开启慢查询日志
```
#慢查询的设置对数据的sql优化非常重要
#vim /data/3306/my.cnf
long_query_time=1        //超过1s 的查询语句
log-slow-queries=/data/3306/slow.log
log_queries_not_using_indexs         //没有使用索引的语句


```

## 3.2.慢查询日志切割
```

cd /server/scripts/
vim cut_slow_log.sh
cd /data/3309/ &&\
/bin/mv slow.log slow.log.$(date +%F) &&\
mysqladmin -uroot -poldboy123 -S /data/3309/mysql.sock  flush-log

```


## 3.3.使用工具mysqlsla分析慢查询，定时发送邮件给相关人员

参见文档：mysqlsla日志分析工具.md


# 4.二进制日志

## 4.1.设置开启
```
[root@lamp01 data]# grep log-bin /data/3306/my.cnf
log-bin = /data/3306/mysql-bin


```




## 4.2.作用
log-bin的作用:
* 记录更改的SQL语句
* 主从复制
* 增量备份


## 4.3.清除binlog日志

mysql>reset master




# 5.记录日志的三种模式
## 5.1.行模式(Row Level)
```
日志中会记录每一行数据被修改
举例：update teacher set name=”zhangsan”;
上述语句在binlog就会被解析成

update teacher set name=”zhangsan” where id=1
update teacher set name=”zhangsan” where id=2
update teacher set name=”zhangsan” where id=3
//..................
 
#缺点：会产生大量的binlog日志

```

## 5.2.语句级别（Statement Level）
```
#mysql的默认级别
#举例：update teacher set name=”zhangsan”;
#上述语句在binlog就会被解析成

update teacher set name=”zhangsan”;

```


## 5.3.mixed
是上述两种模式的结合，智能选择其中的一种


# 6.设置binlog的记录模式
```
#在配置文件中修改
log-bin=mysql-bin
#binlog_format="STATEMENT"
#binlog_format="ROW"
#binlog_format="MIXED"


#运行时在线修改
mysql> SET SESSION binlog_format = 'STATEMENT';
mysql> SET SESSION binlog_format = 'ROW';
mysql> SET SESSION binlog_format = 'MIXED';


#查看
mysql> show variables like "binlog_format%";
+---------------+-----------+
| Variable_name | Value     |
+---------------+-----------+
| binlog_format | STATEMENT |
+---------------+-----------+


```


# 7.使用mysqlbinlog提取二进制日志

## 7.1.提取指定的binlog日志 
```
[root@lamp01 3306]# mysqlbinlog /data/3306/mysql-bin.000002|grep insert
/*!40019 SET @@session.max_insert_delayed_threads=0*/;
insert into student values(1,"zhangsan2222222")
insert into student values(1,"lisi")
insert into student values(2,"lisi2")
[root@lamp01 3306]#
 
```

## 7.2.提取指定position位置的binlog日志
```
mysqlbinlog --start-position="120" --stop-position="332" /opt/data/APP01bin.000001  
```

## 7.3.提取指定position位置的binlog日志并输出到压缩文件 
```
mysqlbinlog --start-position="120" --stop-position="332" /opt/data/APP01bin.000001 |gzip >extra_01.sql.gz  
```


## 7.4.提取指定position位置的binlog日志导入数据库 
```
mysqlbinlog --start-position="120" --stop-position="332" /opt/data/APP01bin.000001 | mysql -uroot -p  
```


## 7.5.提取指定开始时间的binlog并输出到sql文件 
```
mysqlbinlog --start-datetime="2014-12-15 20:15:23" /opt/data/APP01bin.000002 --result-file=extra02.sql  
```


## 7.6.提取指定位置的多个binlog日志文件 
```
mysqlbinlog --start-position="120" --stop-position="332" /opt/data/APP01bin.000001 /opt/data/APP01bin.000002|more 
 
```




## 7.7.提取指定数据库binlog并转换字符集到UTF8 
```
 mysqlbinlog --database=test --set-charset=utf8 /opt/data/APP01bin.000001 /opt/data/APP01bin.000002 >test.sql  

#>test.sql 也是输出到一个sql文件中
```


## 7.8.远程提取日志，指定结束时间 
```
mysqlbinlog -urobin -p -h192.168.1.116 -P3306 --stop-datetime="2014-12-15 20:30:23" --read-from-remote-server mysql-bin.000033 |more 
```

## 7.9.远程提取使用row格式的binlog日志并输出到本地文件 
```
mysqlbinlog -urobin -p -P3606 -h192.168.1.177 --read-from-remote-server -vv inst3606bin.000005 >row.sql  
```

# 8.binlog命令的参数
## 8.1.--base64-output
&emsp;对于不同的日志模式，生成的binlog有不同的记录方式。对于MIXED(部分SQL语句)和ROW模式是以base-64方式记录，会以BINLOG开头，是一段伪SQL，我们可以用使用base64-output参数来抑制其显示


![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/binlog/1.png)

 
![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/binlog/2.png)


![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/binlog/3.png)


 
 

## 8.2.--verbose(-v)
选项可以获取更多的可读信息，但是并不是一个原始的SQL语句
```
[root@MySQL 3309]# mysqlbinlog --base64-output="decode-row" -v mysql-bin.000001
 
```

## 8.3.-d 指定数据库

```
[root@MySQL 3309]# mysqlbinlog mysql-bin.000002|egrep -v "^$|^#|^\/\*|^SET|BEGIN|COMMIT|DELIMIT|ROLL|'"          
gAyxVw8BAAAAZwAAAGsAAAABAAQANS41LjUxLWxvZwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAACADLFXEzgNAAgAEgAEBAQEEgAAVAAEGggAAAAICAgCAA==
use `teacher`/*!*/;
DROP TABLE `t1` /* generated by server */
DROP TABLE `t2` /* generated by server */
insert into t3(id,name) values(33,"chenyansong")
insert into t3(id,name,age) values(333,"chenyansong",33)
drop database tst_t1
drop database tst_t2
 
#指定数据库
[root@MySQL 3309]# mysqlbinlog -d teacher mysql-bin.000002|egrep -v "^$|^#|^\/\*|^SET|BEGIN|COMMIT|DELIMIT|ROLL|'"
gAyxVw8BAAAAZwAAAGsAAAABAAQANS41LjUxLWxvZwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAACADLFXEzgNAAgAEgAEBAQEEgAAVAAEGggAAAAICAgCAA==
use `teacher`/*!*/;
DROP TABLE `t1` /* generated by server */
DROP TABLE `t2` /* generated by server */
insert into t3(id,name) values(33,"chenyansong")
insert into t3(id,name,age) values(333,"chenyansong",33)
 
insert into teacher.t3(id,name,age) values(8,"lisi_4",22)
#上面的语句就不会计入分库binlog，只有我们使用use的时候，才会在-d的时候计入分库

```

# 9.删除二进制日志
&emsp;随着时间的推移，二进制日志也会变得很多很大，因此，有必要执行删除操作，我们会在配置文件中加入下面的参数来实现自动清理二进制日志的工作

## 9.1.reset master

删除所有的binlog日志，新日志编号从头开始

## 9.2.purge master logs to "mysql-bin.000002"
```
#删除mysql-bin.000002之前所有日志，不包含000002自身
mysql> purge master logs to "mysql-bin.000002";   
 
```

## 9.3.purge master logg before “2015-09-11 24:45:56”;

删除指定时间之前的binlog

## 9.4.配置文件中定义删除指定日期的日志

```
 grep expire /data/3309/my.cnf
expire_logs_days = 7

```




