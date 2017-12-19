---
title: 编译安装MySQL-5.5.32
categories: mysql   
tags: [mysql]
---



# 卸载掉原有mysql
因为mysql数据库在Linux上实在是太流行了，所以目前下载的主流Linux系统版本基本上都集成了mysql数据库在里面，我们可以通过如下命令来查看我们的操作系统上是否已经安装了mysql数据库
 
 
```
[root@xiaoluo ~]# rpm -qa | grep mysql　　// 这个命令就会查看该操作系统上是否已经安装了mysql数据库
 
[root@xiaoluo ~]# rpm -e mysql　　// 普通删除模式
[root@xiaoluo ~]# rpm -e --nodeps mysql　　// 强力删除模式，如果使用上面命令删除时，提示有依赖的其它文件，则用该命令可以对其进行强力删除
 
```


# 1.依赖包
## 1.1.ncurses-devel libaio-devel
```
yum install ncurses-devel libaio-devel -y
//检查
[root@MySQL data]# rpm -qa ncurses-devel libaio-devel
ncurses-devel-5.7-4.20090207.el6.i686
libaio-devel-0.3.107-10.el6.i686
[root@MySQL data]#
 
```

## 1.2.安装cmake
```
cd /home/oldboy/tools/
tar zxf cmake-2.8.8.tar.gz
cd cmake-2.8.8
./configure
gmake
gmake install
//检查
[root@MySQL data]# which cmake
/usr/local/bin/cmake

```

# 2.创建用户和组
```
 useradd mysql -s /sbin/nologin -M
id mysql

```

# 3.解压编译mysql
```
cd /home/oldboy/tools/
tar zxf mysql-5.5.32.tar.gz
cd mysql-5.5.32
cmake . -DCMAKE_INSTALL_PREFIX=/application/mysql-5.5.32 \
-DMYSQL_DATADIR=/application/mysql-5.5.32/data \
-DMYSQL_UNIX_ADDR=/application/mysql-5.5.32/tmp/mysql.sock \
-DDEFAULT_CHARSET=utf8 \
-DDEFAULT_COLLATION=utf8_general_ci \
-DEXTRA_CHARSETS=gbk,gb2312,utf8,ascii \
-DENABLED_LOCAL_INFILE=ON \
-DWITH_INNOBASE_STORAGE_ENGINE=1 \
-DWITH_FEDERATED_STORAGE_ENGINE=1 \
-DWITH_BLACKHOLE_STORAGE_ENGINE=1 \
-DWITHOUT_EXAMPLE_STORAGE_ENGINE=1 \
-DWITHOUT_PARTITION_STORAGE_ENGINE=1 \
-DWITH_FAST_MUTEXES=1 \
-DWITH_ZLIB=bundled \
-DENABLED_LOCAL_INFILE=1 \
-DWITH_READLINE=1 \
-DWITH_EMBEDDED_SERVER=1 \
-DWITH_DEBUG=0

#-- Build files have been written to: /home/oldboy/tools/mysql-5.5.32
提示，编译时可配置的选项很多，具体可参考结尾附录或官方文档：

make && make install

#[100%] Built target my_safe_process


```

# 4.创建软链接
```
ln -s /application/mysql-5.5.32/ /application/mysql
#如果上述操作未出现错误，则MySQL5.5.32软件cmake方式的安装就算成功了。

```

# 5.加入环境变量
```
 
echo  “export PATH=/application/mysql/bin/:$PATH”>>/etc/profile
[root@MySQL 3306]# source /etc/profile
 
 
[root@MySQL 3306]# which mysql
/application/mysql/bin/mysql

```

# 6.初始化数据库
```
#初始化数据库,初始化系统表
cd /application/mysql/scripts/
./mysql_install_db --basedir=/application/mysql/ --datadir=/application/mysql/data/ --user=mysql
cd ../

```


# 7.授权用户及目录
```
 
#授权用户及/tmp/临时文件目录
chown -R mysql.mysql /application/mysql/data/
chmod -R 1777 /tmp/

```

# 8.拷贝配置文件

```

#拷贝配置文件,在support-files下有默认的配置文件
cp mysql-5.5.32/support-files/my-small.cnf /etc/my.cnf
 
```

 
# 9.启动数据库及测试
```

cp support-files/mysql.server /etc/init.d/mysqld
#添加执行权限
chmod +x /etc/init.d/mysqld
/etc/init.d/mysqld  start
 
#检查端口
netstat -lntup|grep 3306


# 客户端登录
[root@lamp01 mysql]# mysql        #因为是刚刚安装,所以不需要密码
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 1
Server version: 5.5.51 Source distribution
 
Copyright (c) 2000, 2016, Oracle and/or its affiliates. All rights reserved.
 
Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.
 
Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.
 
mysql>
 

#关闭mysql
[root@lamp01 mysql]# /etc/init.d/mysqld stop
Shutting down MySQL. SUCCESS!
[root@lamp01 mysql]# lsof -i:3306   
[root@lamp01 mysql]# 
```

