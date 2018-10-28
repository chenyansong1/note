[TOC]

# mysql的官网说明



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
	
mysql -uroot -p -h localhost
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

​	数值

​		精确数值

​			整形

​				TINYINT  (一个字节，-128-127 或者0-255) 

​				SMALLINT（2个字节）

​				MEDIUMINT（3个字节）

​				INT	（4个字节）
​				BIGINT(8个字节)

​				修饰符：UNSIGNED 无符号

​			十进制

​				DECIMAL 可以精确的表示一个浮点数

​		近似数值

​			浮点数

​				FLOAT

​				DOUBLE

​	日期

​		DATE	#日期

​		TIME	#时间

​		DATETIME	#日期时间

​		STAMP	#时间戳

​	布尔

​		0 or 1

​	内置类型：ENUM, SET



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
	
#设置用户密码
##1使用客户端命令
mysql>SET PASSWORD FOR 'USERNAME'@'HOST' = PASSWORD('123456')


##2在shell中使用命令行
shell>mysqladmin -uUSERNAME -hHOST -p password 'new-passwd'

##3在客户端改表:mysql.user的字段
mysql>UPDATE mysql.user SET Password=PASSWORD('new-passwd') where User='hostname' AND Host='host'

##这里修改了密码并不会立即生效，需要加载到内存中，所以需要flush，让MySQL重读授权表；
##FLUSH PRIVILEGES;
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
shell> cp support-files/mysql.server /etc/init.d/mysql.server
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

mysql> 


```

