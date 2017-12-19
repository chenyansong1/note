---
title: Amoeba实现mysql读写分离(转)
categories: mysql   
toc: true  
tags: [mysql]
---


[转载自](http://lx.wxqrcode.com/index.php/post/86.html)


![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/amoeba/1.png)

# 机器配置说明
|机器|说明| 
|-|-|
|172.16.1.51|Amoeba|
|172.16.1.52|mysql多实例|
|172.16.1.10|测试机|

# 创建用户
首先在172.16.1.52服务器完成mysql主从复制，启用3306，3307多实例
```
# 1.在主库创建一个用户
        grant select,insert,update,delete on *.* to amoeba@'172.16.1.%' identifieentified by 'lx';
# 2.在从库回收权限
        REVOKE insert,update,delete on *.* FROM 'amoeba'@'172.16.1.%';
        flush privileges;

```

# 环境支持
Amoeba框架是基于Java SE1.5开发的，建议使用java  SE1.5以上的版本

```
#下载安装jdk
wet  http://download.oracle.com/otn-pub/java/jdk/7u80-b15/jdk-7u80-linux-x64.rpm
cd /server/toolsrpm -ivh jdk-7u80-linux-x64.rpm
ln -s /usr/java/jdk1.7.0_80/ /usr/java/r/java/jdk1.7

#加入环境变量
vim /etc/profile
export JAVA_HOME=/usr/java/jdk1.7
export PATH=$PATH_HOME/bin:$PATH_HOME/jre/bin:$PATH. /etc/profile


```

# amoeba安装
```


#下载amoeba-mysql-binary-2.2.0.tar.gz 
wget http://sourceforge.net/projects/amoeba/files/Amoeba%20for%20mysql/2.2.x/amoeba-mysql-binary-2.2.0.tar.gz/downloadtar xf amoeba-mysql-binary-2.2.0.tar.gz

#解压安装
mkdir /application/amoeba
tar -zxvf amoeba-mysql-binary-2.2.0.tar.gz -C /application/amoeba

cd /application/amoeba
[root@lamp01 amoeba]# ll
总用量 60
drwxr-xr-x 2 root root  4096 2月  18 09:59 benchmark
drwxr-xr-x 2 root root  4096 2月  29 2012 bin
-rw-r--r-- 1 root root  3976 8月  29 2012 changelogs.txt
drwxr-xr-x 2 root root  4096 2月  18 09:59 conf
drwxr-xr-x 3 root root  4096 2月  18 09:59 lib
-rw-r--r-- 1 root root 34520 8月  29 2012 LICENSE.txt
-rw-r--r-- 1 root root  2031 8月  29 2012 README.html


```


# 修改配置文件
amoeba的配置是基于XML的配置文件
```

#cd /application/amoeba/conf/
#vim amoeba.xml 

 <service name="Amoeba for Mysql" class="com.meidusa.amoeba.net.ServerableConnectionManager">
         <!-- port -->
         <property name="port">3306</property>        #修改amoeba启动端口
         <!-- bind ipAddress -->
         <property name="ipAddress">172.16.1.51</property>    #修改为amoeba服务器ip地址
         <property name="manager">${clientConnectioneManager}</property>
         <property name="connectionFactory">
                 <bean class="com.meidusa.amoeba.mysql.net.MysqlClientConnectionFactory">
                         <property name="sendBufferSize">128</property>
                         <property name="receiveBufferSize">64</property>
                 </bean>
         </property>
         <property name="authenticator">
                 <bean class="com.meidusa.amoeba.mysql.server.MysqlClientAuthenticator">
                         <property name="user">amoeba</property>    #定义用户
                         <property name="password">lx</property>    #定义密码
                         <property name="filter">
                                 <bean class="com.meidusa.amoeba.server.IPAccessController">
                                         <property name="ipFile">${amoeba.home}/conf/access_list.conf</property>
                                 </bean>
                         </property>
                 </bean>
         </property>
</service>
 
 <property name="defaultPool">master</property>            #修改amoeba指向后端节点主数据库
<property name="writePool">master</property>
<property name="readPool">slave</property>

```

编辑dbServers.xml文件,添加3306,3307多实例
vim dbServers.xml

```
<dbServer name="abstractServer1" abstractive="true">     将 abstractServer 修改为 abstractServer1
  <factoryConfig class="com.meidusa.amoeba.mysql.net.MysqlServerConnectionFactory">
    <property name="manager">${defaultManager}</property>
    <property name="sendBufferSize">64</property>
    <property name="receiveBufferSize">128</property>
    <!-- mysql port -->
    <property name="port">3306</property>        #3306端口
    <!-- mysql schema -->
    <property name="schema">test</property>            #注意查看主从数据库是否存在test数据库
    <!-- mysql user -->
    <property name="user">amoeba</property>        #用户名
    <!--  mysql password -->
    <property name="password">lx</property>        #密码
  </factoryConfig>
  <poolConfig class="com.meidusa.amoeba.net.poolable.PoolableObjectPool">
    <property name="maxActive">500</property>
    <property name="maxIdle">500</property>
    <property name="minIdle">10</property>
    <property name="minEvictableIdleTimeMillis">600000</property>
    <property name="timeBetweenEvictionRunsMillis">600000</property>
    <property name="testOnBorrow">true</property>
    <property name="testOnReturn">true</property>
    <property name="testWhileIdle">true</property>
  </poolConfig>
</dbServer>


<dbServer name="abstractServer2" abstractive="true">       将 abstractServer 修改为 abstractServer2
  <factoryConfig class="com.meidusa.amoeba.mysql.net.MysqlServerConnectionFactory">
    <property name="manager">${defaultManager}</property>
    <property name="sendBufferSize">64</property>
    <property name="receiveBufferSize">128</property>
    <!-- mysql port -->
    <property name="port">3307</property>        #数据库3307端口号
    <!-- mysql schema -->
    <property name="schema">test</property>    #注意查看主从数据库是否存在test数据库
    <!-- mysql user -->
    <property name="user">amoeba</property>      #用户帐号
    <!--  mysql password -->
    <property name="password">lx</property>        #用户密码
  </factoryConfig>
  <poolConfig class="com.meidusa.amoeba.net.poolable.PoolableObjectPool">
    <property name="maxActive">500</property>
    <property name="maxIdle">500</property>
    <property name="minIdle">10</property>
    <property name="minEvictableIdleTimeMillis">600000</property>
    <property name="timeBetweenEvictionRunsMillis">600000</property>
    <property name="testOnBorrow">true</property>
    <property name="testOnReturn">true</property>
    <property name="testWhileIdle">true</property>
  </poolConfig>
</dbServer>


```


![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/amoeba/2.png)


![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/amoeba/3.png)
 

修改bin目录的权限
```
chmod -R 700 /Application/amoeba/bin/
```


```

#vim /application/amoeba/bin/amoeba
#添加
DEFAULT_OPTS="-server -Xms256m -Xmx256m -Xss256k"

```

# 启动
```
/application/amoeba/bin/amoeba   start

#查看端口
[root@MySQL-master-01 conf]# lsof -i:3306
COMMAND   PID USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
java    19474 root   41u  IPv6 238156      0t0  TCP MySQL-master-01:39277->MySQL-master-02:mysql (ESTABLISHED)
java    19474 root   53u  IPv6 238162      0t0  TCP MySQL-master-01:mysql (LISTEN)

```

# 测试
```
#在web服务端172.16.1.10连接amoeba服务器测试
[root@web01 ~]# mysql -uamoeba -plx -h 172.16.1.51
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 126729963
Server version: 5.1.45-mysql-amoeba-proxy-2.2.0 Source distribution
Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.
Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.


#测试在172.16.1.52主库上面创建一个表
mysql> use database ruby;
mysql> use ruby;
Database changed
mysql> create table lx (id int(10),name varchar(10),address varchar(20));
Query OK, 0 rows affected (0.08 sec)


#停止从库
mysql> stop slave;
Query OK, 0 rows affected (0.03 sec)


#分别在主库和从库插入一条数据
#主库插入：
mysql> insert into lx values(1,'lx','master');
Query OK, 1 row affected (0.01 sec)
mysql> select * from lx;
+------+------+---------+
| id   | name | address |
+------+------+---------+
|    1 | lx   | master  |
+------+------+---------+
1 row in set (0.00 sec)


#从库插入：
mysql> insert into ruby.lx values(1,'lx','slave'); 
Query OK, 1 row affected (0.00 sec
mysql> select * from ruby.lx;
+------+------+---------+
| id   | name | address |
+------+------+---------+
|    1 | lx   | slave   |
+------+------+---------+
1 row in set (0.00 sec)


#在测试服务器172.16.1.10
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| bbs                |
| dedecms            |
| lixiang            |
| mysql              |
| performance_schema |
| ruby               |
| test               |
| wordpress          |
+--------------------+
9 rows in set (0.00 sec)
mysql> use ruby;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A
Database changed
mysql> show tables;
+----------------+
| Tables_in_ruby |
+----------------+
| lx             |
+----------------+
1 row in set (0.01 sec)
这个时候我们查询到的数据是从库创建的数据
mysql> select * from lx;
+------+------+---------+
| id   | name | address |
+------+------+---------+
|    1 | lx   | slave   |
+------+------+---------+
1 row in set (0.00 sec)
此时我们在插入一条数据
mysql> insert into ruby.lx values(33,'test33','test33');
Query OK, 1 row affected (0.01 sec)



#返回172.16.1.52服务器，在主库中查看是否有该条数据
mysql> select * from lx;                  
+------+--------+---------+
| id   | name   | address |
+------+--------+---------+
|    1 | lx     | master  |
|   33 | test33 | test33  |
+------+--------+---------+
2 rows in set (0.00 sec)
回到172.16.1.52服务器，在从库中查看是否有该条数据
mysql> select * from ruby.lx;             
+------+------+---------+
| id   | name | address |
+------+------+---------+
|    1 | lx   | slave   |
+------+------+---------+
1 row in set (0.00 sec)
mysql> start slave;                                                #从库开启主从同步
Query OK, 0 rows affected (0.00 sec)



#再次回到测试服务器172.16.1.10，查看数据情况
mysql> select * from ruby.lx;
+------+--------+---------+
| id   | name   | address |
+------+--------+---------+
|    1 | lx     | slave   |
|    1 | lx     | master  |
|   33 | test33 | test33  |
+------+--------+---------+
3 rows in set (0.01 sec)



```
至此，如果得到上面的结果则说明mysql数据读写分离完成，此时在回到从数据库开启主从同步 start slave



