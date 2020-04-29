---
title: Centos 安装MongoDB 详细教程(转).md
categories: mongodb   
toc: true  
tags: [mongodb]
---




# 1.环境准备
```
Centos 6.7
MongoDB 3.2.7
 
#官网: https://www.mongodb.com

```


# 2.安装

&emsp;这里我们在官网下载源码进行安装. 下载地址: https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-rhel62-3.2.7.tgz
```
cd /usr/local
wget https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-rhel62-3.2.7.tgz
tar -xvf mongodb-linux-x86_64-rhel62-3.2.7.tgz
mv mongodb-linux-x86_64-rhel62-3.2.7 mongodb

```

配置环境变量
 
```
#vim /etc/profile
export MONGODB_HOME=/usr/local/mongodb
export PATH=$MONGODB_HOME/bin:$PATH

source /etc/profile
```

查看mongodb版本信息 mongod -v
```
mongod -v
 
2016-07-09T22:01:18.546+0800 I CONTROL  [initandlisten] MongoDB starting : pid=1314 port=27017 dbpath=/data/db 64-bit host=iZ28lgwrrtqZ
2016-07-09T22:01:18.553+0800 I CONTROL  [initandlisten] db version v3.2.7
2016-07-09T22:01:18.553+0800 I CONTROL  [initandlisten] git version: 4249c1d2b5999ebbf1fdf3bc0e0e3b3ff5c0aaf2
2016-07-09T22:01:18.553+0800 I CONTROL  [initandlisten] OpenSSL version: OpenSSL 1.0.1e-fips 11 Feb 2013
2016-07-09T22:01:18.553+0800 I CONTROL  [initandlisten] allocator: tcmalloc
2016-07-09T22:01:18.553+0800 I CONTROL  [initandlisten] modules: none
2016-07-09T22:01:18.553+0800 I CONTROL  [initandlisten] build environment:
2016-07-09T22:01:18.553+0800 I CONTROL  [initandlisten]     distmod: rhel62
2016-07-09T22:01:18.553+0800 I CONTROL  [initandlisten]     distarch: x86_64
2016-07-09T22:01:18.553+0800 I CONTROL  [initandlisten]     target_arch: x86_64
2016-07-09T22:01:18.555+0800 I CONTROL  [initandlisten] options: { systemLog: { verbosity: 1 } }
2016-07-09T22:01:18.555+0800 D NETWORK  [initandlisten] fd limit hard:65535 soft:65535 max conn: 52428
2016-07-09T22:01:18.616+0800 D -        [initandlisten] User Assertion: 29:Data directory /data/db not found.
2016-07-09T22:01:18.635+0800 I STORAGE  [initandlisten] exception in initAndListen: 29 Data directory /data/db not found., terminating
2016-07-09T22:01:18.635+0800 I CONTROL  [initandlisten] dbexit:  rc: 100


```

# 3.添加配置文件
创建数据库目录
MongoDB需要自建数据库文件夹.

```
mkdir -p /data/mongodb
mkdir -p /data/mongodb/log
touch /data/mongodb/log/mongodb.log

```

添加配置文件 
新建mongodb.conf配置文件, 通过这个配置文件进行启动.

```
#vim /etc/mongodb.conf

dbpath=/data/mongodb
logpath=/data/mongodb/log/mongodb.log
logappend=true
port=27017
fork=true
##auth = true # 先关闭, 创建好用户在启动


```

配置文件参数说明
```
mongodb的参数说明：
--dbpath 数据库路径(数据文件)
--logpath 日志文件路径
--master 指定为主机器
--slave 指定为从机器
--source 指定主机器的IP地址
--pologSize 指定日志文件大小不超过64M.因为resync是非常操作量大且耗时，最好通过设置一个足够大的oplogSize来避免resync(默认的 oplog大小是空闲磁盘大小的5%)。
--logappend 日志文件末尾添加
--port 启用端口号
--fork 在后台运行
--only 指定只复制哪一个数据库
--slavedelay 指从复制检测的时间间隔
--auth 是否需要验证权限登录(用户名和密码)
 
注：mongodb配置文件里面的参数很多，定制特定的需求，请参考官方文档

```


# 4.启动
通过配置文件启动
```
mongod -f /etc/mongodb.conf
about to fork child process, waiting until server is ready for connections.
forked process: 2814
child process started successfully, parent exiting

```
出现successfully表示启动成功了.


# 5.客户端测试
进入 MongoDB后台管理 Shell
```
cd /usr/local/mongodb/bin
./mongo

#创建数据库
use test
switched to db test


#创建用户, 设置权限
db.createUser(
    {
        user: "test",
        pwd: "test",
        roles: [ { role: "readWrite", db: "test" } ]
    }
)

#详细权限配置参考网址: [MongoDB 3.0 用户创建](http://www.cnblogs.com/zhoujinyi/p/4610050.html)

```

# 配置防火墙
 
将27017端口添加到防火墙中
```
vi /etc/sysconfig/iptables
    -A INPUT -m state --state NEW -m tcp -p tcp --dport 27017 -j ACCEPT
/etc/init.d/iptables reload

```

# 注意
 
* 我们创建了用户, 这个时候要开启权限启动, 在配置文件中添加auth=true, 然后重启一下
* MongoDB 默认没有用户权限的, 建议大家一定要设置, 这样数据才安全.


# BIN目录说明
```
bsondump 导出BSON结构
mongo 客户端
mongod 服务端
mongodump 整体数据库二进制导出
mongoexport 导出易识别的json文档或csv文档
mongorestore 数据库整体导入
mongos 路由器(分片用)
mongofiles GridFS工具，内建的分布式文件系统
mongoimport 数据导入程序
mongotop 运维监控
mongooplog
mongoperf
mongostat

```

