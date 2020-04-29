---
title: MySQL主从复制
categories: mysql   
tags: [mysql]
---




# 1.目的
我们为什么要用主从复制？
 
* 可以做数据库的实时备份，保证数据的完整性；
* 可做读写分离，主服务器只管写，从服务器只管读，这样可以提升整体性能。

# 2.原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/replication_Master_slave/1.jpg)


　 　Mysql的 Replication 是一个异步的复制过程，从一个 Mysql instace(我们称之为 Master)复制到另一个 Mysql instance(我们称之 Slave)。在 Master 与 Slave 之间的实现整个复制过程主要由三个线程来完成，其中两个线程(Sql线程和IO线程)在 Slave 端，另外一个线程(IO线程)在 Master 端。
　　要实现 MySQL 的 Replication ，首先必须打开 Master 端的Binary Log(mysql-bin.xxxxxx)功能，否则无法实现。因为整个复制过程实际上就是Slave从Master端获取该日志然后再在自己身上完全 顺序的执行日志中所记录的各种操作。打开 MySQL 的 Binary Log 可以通过在启动 MySQL Server 的过程中使用 “—log-bin” 参数选项，或者在 my.cnf 配置文件中的 mysqld 参数组([mysqld]标识后的参数部分)增加 “log-bin” 参数项。
　　MySQL 复制的基本过程如下：
1. Slave 上面的IO线程连接上 Master，并请求从指定日志文件的指定位置(或者从最开始的日志)之后的日志内容;
2. Master 接收到来自 Slave 的 IO 线程的请求后，通过负责复制的 IO 线程根据请求信息读取指定日志指定位置之后的日志信息，返回给 Slave 端的 IO 线程。返回信息中除了日志所包含的信息之外，还包括本次返回的信息在 Master 端的 Binary Log 文件的名称以及在 Binary Log 中的位置;
3. Slave 的 IO 线程接收到信息后，将接收到的日志内容依次写入到 Slave 端的Relay Log文件(mysql-relay-bin.xxxxxx)的最末端，并将读取到的Master端的bin-log的文件名和位置记录到master- info文件中，以便在下一次读取的时候能够清楚的高速Master“我需要从某个bin-log的哪个位置开始往后的日志内容，请发给我”
4. Slave 的 SQL 线程检测到 Relay Log 中新增加了内容后，会马上解析该 Log 文件中的内容成为在 Master 端真实执行时候的那些可执行的 Query 语句，并在自身执行这些 Query。这样，实际上就是在 Master 端和 Slave 端执行了同样的 Query，所以两端的数据是完全一样的。

 

# 3.更改配置文件

```
# 3306和3307分别代表2台机器
# 打开log-bin,并使server-id不一样
#vim /data/3306/my.cnf
log-bin = /data/3306/mysql-bin
server-id = 1

#vim /data/3307/my.cnf
log-bin = /data/3307/mysql-bin
server-id = 3

#检查
#1、
[root@bogon ~]# egrep "log-bin|server-id" /data/3306/my.cnf
log-bin = /data/3306/mysql-bin
server-id = 1
[root@bogon ~]# egrep "log-bin|server-id" /data/3307/my.cnf
log-bin = /data/3307/mysql-bin
server-id = 3
#2、
[root@localhost ~]# mysql -uroot -p -S /data/3306/mysql.sock -e "show variables like 'log_bin';"
Enter password:
+--------+--------+
| Variable_name | Value |
+--------+--------+
| log_bin       | ON  |    # ON 为开始开启成功
+--------+--------+



```

# 4.建立用于从库复制的账号rep
通常会创建一个用于主从复制的专用账户，不要忘记授权。
```
# 主库授权，允许从库来连接我取日志
[root@localhost ~]# mysql -uroot -p -S /data/3306/mysql.sock
Enter password:
# 允许从库192.168.200网段连接，账号rep，密码nick。
mysql> grant replication slave on *.* to 'rep'@'192.168.200.%' identified by 'nick';
Query OK, 0 rows affected (0.00 sec)
mysql> flush privileges;
Query OK, 0 rows affected (0.00 sec)
# 检查创建的rep账号：
mysql> select user,host from mysql.user;
+-----+-------------+
| user | host              |
+-----+--------------+
| root | 127.0.0.1          |
| rep  | 192.168.200.%     |
| root | localhost           |
| root | localhost.localdomain |
+-----+------------------+
7    rows in set (0.00 sec)


```

# 5.备份主库，及恢复到从库
把主库现有数据备份下来，再恢复到从库，此时两个主机的数据一致
如果事先有数据的话，这不不能忘。
```
#1)在主库上加锁，使只有只读权限。
mysql> flush table with read lock;
Query OK, 0 rows affected (0.00 sec)
#5.1、5.5锁表命令略有不同。
# 5.1锁表：flush tables with read lock;
# 5.5锁表：flush table with read lock;


#2)记住就是这个点备份的。
mysql> show master status;
+-------+------+--------+---------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB |
+-------+------+--------+---------+
| mysql-bin.000013  |   410 |             |               |
+-------+------+--------+---------+
1 row in set (0.00 sec)


#3)克隆窗口，备份数据。
[root@bogon ~]# mysqldump -uroot -p -S /data/3306/mysql.sock -A -B --events --master-data=2|gzip >/opt/rep.sql.gz
Enter password:
参数：    -A：备份所有的
#看rep.sql.gz参数
vim /opt/rep.sql.gz
-- CHANGE MASTER TO MASTER_LOG_FILE='mysql-bin.000013', MASTER_LOG_POS=410;


#4)查看master status；数值是否正常。
mysql> show master status;
+------+------+---------+-------+
| File            | Position | Binlog_Do_DB | Binlog_Ignore_DB |
+-------+-----+---------+--------+
| mysql-bin.000013 |    410 |            |                |
+--------+----+---------+--------+
1 row in set (0.00 sec)



#5)解锁库
mysql> unlock tables;
Query OK, 0 rows affected (0.00 sec)


#6)恢复到从库
[root@bogon ~]# gunzip < /opt/rep.sql.gz | mysql -uroot -p -S /data/3307/mysql.sock
Enter password:



```


# 6.配置从库及生效
更改从库和主库的连接参数，配置生效。检查就成功了！
```
#1)进入从库。
[root@bogon ~]# mysql -uroot -p -S /data/3307/mysql.sock
Enter password:


#2)更改从属服务器用于与主服务器进行连接和通讯的参数。
mysql> CHANGE MASTER TO
      MASTER_HOST='192.168.200.98',
      MASTER_PORT=3306,
      MASTER_USER='rep',
      MASTER_PASSWORD='nick',
      MASTER_LOG_FILE='mysql-bin.000013',
      MASTER_LOG_POS=410;
Query OK, 0 rows affected (0.01 sec)



#3)查看更改的参数。
[root@localhost ~]# cd /data/3307/data/
[root@localhost data]# cat master.info
18
mysql-bin.000013
410
192.168.200.98
REP
nick
3306
60
0
0
1800.000
0



#4)生效！
mysql> start slave;
Query OK, 0 rows affected (0.01 sec)


#5)检查下列参数，符合则正常！
mysql> show slave status\G
Relay_Master_Log_File: mysql-bin.000013
             Slave_IO_Running: Yes        #取logo。
            Slave_SQL_Running: Yes        #读relay-bin、logo,写数据。
Seconds_Behind_Master: 0        #落后主库的秒数。




#6)查看relay-bin.logo(因为同步的过程中会有中继日志,即relay-bin.xxx日志的生成,所以查看是否已经生成)
[root@localhost 3307]# cd /data/3307
[root@localhost 3307]# ll
总用量 48
drwxr-xr-x. 9 mysql mysql  4096 10月 29 18:52 data
-rw-r--r--. 1 mysql mysql  1900 10月 29 11:45 my.cnf
-rwx------. 1 root  root   1307 10月 20 17:06 mysql
-rw-rw----. 1 mysql mysql     6 10月 29 11:00 mysqld.pid
-rw-r-----. 1 mysql mysql 15090 10月 29 18:49 mysql_nick3307.err
srwxrwxrwx. 1 mysql mysql     0 10月 29 11:00 mysql.sock
-rw-rw----. 1 mysql mysql   150 10月 29 18:49 relay-bin.000001
-rw-rw----. 1 mysql mysql   340 10月 29 18:52 relay-bin.000002
-rw-rw----. 1 mysql mysql    56 10月 29 18:49 relay-bin.index
-rw-rw----. 1 mysql mysql    53 10月 29 18:52 relay-log.info



#7)查看relay-log.info。
[root@localhost 3307]# cat relay-log.info
/data/3307/relay-bin.000002
340
mysql-bin.000013
497



#8)查看master.info。
[root@localhost 3307]# cat data/master.info
18
mysql-bin.000013
497
192.168.200.98
rep
nick
3306
60
0
0
1800.000
0

```


# 7.测试
在主库添加数据,在从库看是否同步过来

```
#进入3306(Master)
mysql> use test;
Database changed
mysql>
mysql> show tables;
Empty set (0.00 sec)
 
mysql> create table student(id int, name varchar(30));
Query OK, 0 rows affected (0.03 sec)
 
mysql> show tables;
+----------------+
| Tables_in_test |
+----------------+
| student        |
+----------------+
1 row in set (0.00 sec)
 
#插入一条记录
mysql> insert into student values(1,"zhangsan2222222");
Query OK, 1 row affected (0.04 sec)
 


#进入3307
mysql> use test;
Database changed
mysql> show tables;
+----------------+
| Tables_in_test |
+----------------+
| student        |
+----------------+
1 row in set (0.00 sec)
 

#查询数据
mysql> select *from student;
+------+-----------------+
| id   | name            |
+------+-----------------+
|    1 | zhangsan2222222 |        #同步成功
+------+-----------------+
1 row in set (0.00 sec)
 
mysql> 
```


