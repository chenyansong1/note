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

