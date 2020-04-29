---
title: 配置mysql从库级联复制和主主复制(转)
categories: mysql   
toc: true  
tags: [mysql]
---




# 1.配置mysql从库级联复制



环境是：3306主库 3307从库 3308从库
由于已经做了主库3306到从库3307，所以现在我们要实现的需求是，当主库3306产生bin_log，发给从库，从库3307产生的bin_log文件发送给其他从库3308。相当于下图中第三个图




![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/mmm/1.png)

 

> 开启从库3307的log-bin日志文件

```
sed -i 's@#log-bin = /data/3307/mysql-bin@log-bin = /data/3307/mysql-bin@g' /data/3307/my.cnf
```

> 在3307从库配置文件my.cnf，[mysqld]模块添加 如下内容

```
log-bin = /data/3307/mysql-bin
log-slave-updates = 1
expire_logs_days = 7

```
重启数据库3307
```
[root@lixiang data]# /data/3307/mysql stop                        
Stoping MySQL...
[root@lixiang data]# /data/3307/mysql start
Starting MySQL...
```
如果现下面的错误的时候
```
[root@lixiang data]# /data/3307/mysql stop
Stoping MySQL...
/application/mysql/bin/mysqladmin: connect to server at 'localhost' failed
error: 'Access denied for user 'root'@'localhost' (using password: YES)'
```
那是因为我们在做单台主从复制的时候，是将主服务器整个包导入到从库3307的，所以修改从库3307的启动文件mysqld
```
sed -i 's/lx3307/lx3306/g' /data/3307/mysql
```

> 查看log_slave_updates状态是否开启

```
mysql> show variables like "log_slave_updates";
+-------------------+-------+
| Variable_name     | Value |
+-------------------+-------+
| log_slave_updates | ON    |
+-------------------+-------+
1 row in set (0.00 sec)
```



> 通过mysqldump导出从库3307数据文件

```
mysqldump -uroot -plx3306 -S /data/3307/mysql.sock -A --events -B -F -x --master-data=1|gzip > /opt/lx.sql.gz  
#--master-data=1,表示在lx.sql文件中将取消注释“CHANGE MASTER TO MASTER_LOG_FILE='mysql-bin.000003', MASTER_LOG_POS=107;”
[root@lixiang opt]# cat lx.sql |grep "mysql-bin.000003"
CHANGE MASTER TO MASTER_LOG_FILE='mysql-bin.000003', MASTER_LOG_POS=107;
```


> 解压数据库，并导入从库3308


```
cd /opt/3308
gzip -d lx.sql.gz
mysql -uroot -plx3308 -S /data/3308/mysql.sock <lx.sql
```


> 登录从数据库3308

```
mysql -uroot -plx3308 -S /data/3308/mysql.sock
mysql> CHANGE MASTER TO  MASTER_HOST='192.168.10.102',  MASTER_PORT=3307, MASTER_USER='rep',  MASTER_PASSWORD='lx123';
#此时就不用指定binlog的日志文件和pos,因为在mysqldump的时候,指定了--master-data=1

mysql> start slave;                                                #开启从库3307到从库3308同步开关
mysql> show slave status\G;                               #查看从库3308状态
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 192.168.10.102
                  Master_User: rep
                  Master_Port: 3307
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000003
          Read_Master_Log_Pos: 107
               Relay_Log_File: relay-bin.000005
                Relay_Log_Pos: 253
        Relay_Master_Log_File: mysql-bin.000003
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB:
          Replicate_Ignore_DB: mysql
           Replicate_Do_Table:
       Replicate_Ignore_Table:
      Replicate_Wild_Do_Table:
  Replicate_Wild_Ignore_Table:
                   Last_Errno: 0
                   Last_Error:
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 107
              Relay_Log_Space: 446
              Until_Condition: None
               Until_Log_File:
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File:
           Master_SSL_CA_Path:
              Master_SSL_Cert:
            Master_SSL_Cipher:
               Master_SSL_Key:
        Seconds_Behind_Master: 0
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error:
               Last_SQL_Errno: 0
               Last_SQL_Error:
  Replicate_Ignore_Server_Ids:
             Master_Server_Id: 3
1 row in set (0.00 sec)
ERROR:
No query specified


mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| lx                 |
| mysql              |
| performance_schema |
| test               |
+--------------------+
5 rows in set (0.00 sec)
 
#使用此种方法也能快速查看从库mysql的状态
[root@lixiang data]# mysql -uroot -p'lx3306' -S /data/3307/mysql.sock -e "show slave status\G;"|egrep -i "_running|_Behind"        
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
        Seconds_Behind_Master: 0

```
这个时候我们看见从库3308已经多了一个数据库名为 “lx”的数据库，出现了这个则表示创建成功

> 登录主库3306，删除测试数据库

```
[root@lixiang opt]# mysql -uroot -plx3306 -S /data/3306/mysql.sock
mysql> drop database test;                                    #删除主库名称为 "test"数据库
Query OK, 0 rows affected (0.13 sec)
mysql> show databases;                                        #查看数据库
+--------------------+
| Database           |
+--------------------+
| information_schema |
| lx                 |
| mysql              |
| performance_schema |
+--------------------+
4 rows in set (0.00 sec)

```

> 分别登录从库3306和从库3307查看

```
[root@lixiang opt]# mysql -uroot -plx3307 -S /data/3307/mysql.sock
mysql> show databases;                                        #查看数据库
+--------------------+
| Database           |
+--------------------+
| information_schema |
| lx                 |
| mysql              |
| performance_schema |
+--------------------+
4 rows in set (0.00 sec)
[root@lixiang opt]# mysql -uroot -plx3308 -S /data/3308/mysql.sock
mysql> show databases;                                        #查看数据库
+--------------------+
| Database           |
+--------------------+
| information_schema |
| lx                 |
| mysql              |
| performance_schema |
+--------------------+
4 rows in set (0.00 sec)
```

发现test数据库都被删除了，至此mysql级联复制配置完毕



# 2.mysql主主复制

* 应用场景：高并发场景，使用双主双写，慎用！
* 注意： ID会冲突
* 解决 ID 冲突问题
 方法一： 表的id自增，让主A 写1，3，5；主B 写2，4，6；
 方法二：表的id不自增，通过web端程序去seq取id，写入双主


环境：主库3306 ，从库 3307
由于我们已经做了主库3306到从库3307，现在我们需要将从库3307变为主库，将3306作为从库

> 编辑数据库配置文件

```
##3306
[root@lixiang 3306]# cd /data/3306
[root@lixiang 3306]# vim my.cnf           
#……省略……
[mysqld]                # 以下内容加在[mysqld]下面
#________m-m m1 start________
auto_increment_increment    = 2        #自增ID的间隔
auto_increment_offset           = 1        #ID的初始位置
log-slave-updates   = 1
log-bin = /data/3306/mysql-bin
expire_logs_days = 7
#________m-m m1 end________
……省略……


#重启mysql
[root@lixiang 3306]# ./mysql stop
Stoping MySQL...
[root@lixiang 3306]# ./mysql start
Starting MySQL...




##3307
[root@lixiang 3306]# cd /data/3307
[root@lixiang 3307]# vim my.cnf    
……省略……
[mysqld]                # 以下内容加在[mysqld]下面
#________m-m m1 start________
auto_increment_increment    = 2        #自增ID的间隔
auto_increment_offset           = 2        #ID的初始位置
log-slave-updates   = 1
log-bin = /data/3307/mysql-bin
expire_logs_days = 7
#________m-m m1 end________
……省略……
 
#重启mysql
[root@lixiang 3307]# ./mysql stop
Stoping MySQL...
[root@lixiang 3307]# ./mysql start
Starting MySQL...

```

> 导出3307数据库数据

```
mysqldump -uroot -plx3306 -S /data/3307/mysql.sock -A --events -B -F -x --master-data=1|gzip > /opt/$(date +%F).sql.gz

```

> 解压并将数据导入到3306

```
gzip -d 2015-07-27.sql.gz
mysql -uroot -plx3306 -S /data/3306/mysql.sock < 2015-07-27.sql
```


> 登录主数据库3306

```
mysql -uroot -plx3306 -S /data/3306/mysql.sock
mysql> CHANGE MASTER TO  MASTER_HOST='192.168.10.102', MASTER_USER='rep', MASTER_PORT=3307, MASTER_PASSWORD='lx123';
mysql> start slave;
查看从库3306状态
mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 192.168.10.102
                  Master_User: rep
                  Master_Port: 3307
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000008
          Read_Master_Log_Pos: 1561
               Relay_Log_File: relay-bin.000011
                Relay_Log_Pos: 253
        Relay_Master_Log_File: mysql-bin.000008
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB:
          Replicate_Ignore_DB: mysql
           Replicate_Do_Table:
       Replicate_Ignore_Table:
      Replicate_Wild_Do_Table:
  Replicate_Wild_Ignore_Table:
                   Last_Errno: 0
                   Last_Error:
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 1561
              Relay_Log_Space: 446
              Until_Condition: None
               Until_Log_File:
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File:
           Master_SSL_CA_Path:
              Master_SSL_Cert:
            Master_SSL_Cipher:
               Master_SSL_Key:
        Seconds_Behind_Master: 0
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error:
               Last_SQL_Errno: 0
               Last_SQL_Error:
  Replicate_Ignore_Server_Ids:
             Master_Server_Id: 3
1 row in set (0.00 sec)
ERROR:
No query specified


```

> 在数据库3306创建数据库students

```
mysql -uroot -plx3306 -S /data/3306/mysql.sock
mysql> create database students;
mysql> use students;
创建表t1，并插入内容
mysql> CREATE TABLE `t1` (   `id` bigint(12) NOT NULL auto_increment,   `name` varchar(12) NOT NULL,   PRIMARY KEY  (`id`) );
mysql> insert into t1(name) values("oldgirl");
mysql> insert into t1(name) values("oldboy");
mysql> select * from t1;                     
+----+---------+
| id | name    |
+----+---------+
|  1 | oldgirl |
|  3 | oldboy  |
+----+---------+
#结果查看到内容是按照ID号，1 3 ……进行增长
```

> 登录到3307数据库

```
[root@lixiang opt]# mysql -uroot -plx3306 -S /data/3307/mysql.sock
mysql> use students;
mysql> select * from t1;
+----+---------+
| id | name    |
+----+---------+
|  1 | oldgirl |
|  3 | oldboy  |
+----+---------+
mysql> insert into t1(name) values("lx");
mysql> insert into t1(name) values("swj");
mysql> select * from t1;                  
+----+---------+
| id | name    |
+----+---------+
|  1 | oldgirl |
|  3 | oldboy  |
|  4 | lx      |
|  6 | swj     |
+----+---------+
4 rows in set (0.00 sec)

#查看到数据库3307的ID是按照偶数进行递增的
```



# 主从复制的故障处理
&emsp;当从库复制遇到错误时，比如报错“要创建的数据库已存在”

解决方案： 让从库跳过这一步操作，继续执行其它的操作
* 方法一： 命令行实现，跳过这一步；
```
mysql> stop slave;
mysql> set global sql_slave_skip_counter =1;
mysql> start slave;
```

* 方法二： 配置文件中，指定忽略的错误；
```
[root@MySQL opt]# grep slave-skip /data/3308/my.cnf
slave-skip-errors = 1032,1062
```


[整理自](http://lx.wxqrcode.com/index.php/post/68.html)
