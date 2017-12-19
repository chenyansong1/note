---
title: mysql多实例配置
categories: mysql   
tags: [mysql]
toc: true
---



前提: 在mysql已经安装的完毕,可以参见: <<编译安装MySQL-5.5.32.md>>一文

# 1.多实例数据文件目录结构
&emsp;在该文档中，采用的是/data目录作为mysql多实例总的根目录，然后规则不同的数字（即Mysql实例端口号）作为/data下面的二级目录，不同的二级目录对应的数字就作为Mysql实例的端口号，以区别不同的实例，数字对应的二级目录下包含mysql数据文件、配置文件以及启动文件等。

```
mkdir -p /data/{3306,3307}/data

[root@lamp01 /]# tree /data
/data
|-- 3306
|   `-- data
`-- 3307
    `-- data

```



# 2.上传配置和启动文件
```
上传配置好的文件（F:\Linux_NOTE\MySQL数据库\data.zip）到 / 目录下
cd /
rz -y
unzip data.zip

#解压后的文件目录如下：
[root@lamp01 /]# tree /data
/data
|-- 3306
|   |-- data
|   |-- my.cnf
|   `-- mysql
`-- 3307
    |-- data
    |-- my.cnf
    `-- mysql


```

# 3.配置文件/data/3306/my.cnf

 

# 4.配置文件/data/3306/mysql
使用vim进去看，其实这个启动文件就是一个脚本，其中包含：启动、停止、重启的方法

 

# 5.每个实例初始化数据

```
[root@lamp01 /]# cd /application/mysql/scripts/
#指定数据生成的目录为: /data/3306/data 
[root@lamp01 scripts]# ./mysql_install_db --basedir=/application/mysql/ --datadir=/data/3306/data --user=mysql
[root@lamp01 scripts]# ./mysql_install_db --basedir=/application/mysql/ --datadir=/data/3307/data --user=mysql


[root@lamp01 data]# tree -L 3 /data
/data
|-- 3306
|   |-- data
|   |   |-- mysql
|   |   |-- performance_schema
|   |   `-- test
|   |-- my.cnf
|   |-- mysql
|   `-- mysql_oldboy3306.err
`-- 3307
    |-- data
    |   |-- mysql
    |   |-- performance_schema
    |   `-- test
    |-- my.cnf
    |-- mysql
    `-- mysql_oldboy3307.err



```

 
# 6.授权用户及目录
```
 
#授权用户及/tmp/临时文件目录
chown -R mysql.mysql /data

```

# 7.给执行脚本执行权限
```
[root@lamp01 3306]# chmod a+x /data/{3306,3307}/mysql

[root@lamp01 3306]# ll
total 16
drwxr-xr-x 5 mysql mysql 4096 Feb 16 22:33 data
-rw-r--r-- 1 mysql mysql 1899 Oct 29  2013 my.cnf
-rwxr-xr-x 1 mysql mysql 1307 Jul 15  2013 mysql
-rw-r----- 1 mysql mysql   77 Feb 16 22:31 mysql_oldboy3306.err
[root@lamp01 3306]#
 
```


# 5.启动多实例
```
[root@lamp01 3306]# /data/3306/mysql start
Starting MySQL...
[root@lamp01 3306]# /data/3307/mysql start
Starting MySQL...

#查看
[root@lamp01 3306]# netstat -lntup|grep mysql
tcp        0      0 0.0.0.0:3306                0.0.0.0:*                   LISTEN      2527/mysqld        
tcp        0      0 0.0.0.0:3307                0.0.0.0:*                   LISTEN      3240/mysqld    

```
# 6.初始化登录mysql
```
#要指定对应实例的sock文件
[root@lamp01 3306]# mysql -S /data/3306/mysql.sock 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 1
Server version: 5.5.51-log Source distribution
 
Copyright (c) 2000, 2016, Oracle and/or its affiliates. All rights reserved.
 
Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.
 
Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.
 
mysql>
 

```

# 7.配置密码和修改密码操作
```
//配置初始化密码
[root@MySQL ~]# mysqladmin -uroot password "oldboy123" -S /data/3309/mysql.sock
//用初始化密码登录
[root@MySQL ~]# mysql -uroot -poldboy123 -S /data/3309/mysql.sock
 
//修改密码
//非交互式的：直接写出了密码
mysqladmin -uroot -poldboy123 password "123" -S /data/3309/mysql.sock
//交互式的
mysqladmin -uroot -poldboy123 password -S /data/3309/mysql.sock 

```



# 8.去掉多余的登录用户和库

参见: mysql数据库安装之后的优化操作.md  一文



# 9.快速配置一个多实例
下面让我们来配置mysql 3308的多实例启动方法：

```
mkdir -p /data/3308/data
\cp /data/3306/my.cnf  /data/3308/
\cp /data/3306/mysql  /data/3308/
sed -i 's/3306/3308/g' /data/3308/my.cnf
sed -i 's/server-id = 1/server-id = 9/g' /data/3308/my.cnf
sed -i 's/3306/3308/g' /data/3308/mysql
chown -R mysql:mysql /data/3308
chmod 700 /data/3308/mysql
cd /application/mysql/scripts
./mysql_install_db --datadir=/data/3308/data --basedir=/application/mysql --user=mysql
chown -R mysql:mysql /data/3308
egrep "server-id|log-bin" /data/3308/my.cnf
/data/3308/mysql start
sleep 5
netstat -lnt|grep 3308
mysqladmin -u root password 'lx3308' -S /data/3308/mysql.sock  #初始化3308数据库密码



#查看mysql 3306 3307 3308各个服务是否开启
[root@lixiang scripts]# netstat -lntup|grep 3308
tcp        0      0 0.0.0.0:3308                0.0.0.0:*                   LISTEN      5251/mysqld


```





