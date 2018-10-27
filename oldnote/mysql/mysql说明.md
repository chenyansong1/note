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
mysql	#MySQL的表的元数据信息
test	#测试数据库

ls /var/lib/mysql 有对应数据库的文件目录
	test	#对应数据库
	mysql	#对应数据库，而information_schema是内存信息，不属于锁文件
	#如果我们在该目录下新建一个 mydb，那么使用客户端去查询的时候，可以看到 show databases; 
```

![image-20181025224303969](/Users/chenyansong/Documents/note/images/mysql/database-new.png)



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



关系型数据库对象：

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

DDL

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
    
    #修改表(help alter table)
    ALTER TABLE tb_name;
    	MODIFY:修改字段的属性
    	CHANGE：修改字段名称
    	ADD：添加一个字段
    	DROP:删除一个字段
#ALTER


#DROP
	DROP DATABASE db_name;


```





DML

​	INSERT

​	UPDATE

​	DELETE



DCL

​	GRANT

​	REVOKE





