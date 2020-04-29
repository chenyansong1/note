---
title: redis主从同步及切换主从配置示例(转)
categories: redis   
toc: true  
tags: [redis]
---



redis主从原理：当一个从数据库启动时，会向主数据库发送SYNC命令，主数据库收到命令后会开始在后台保存快照（即RDB持久化过程），并将保存快照期间接收到的命令缓存起来。当快照完成后，Redis会将快照文件和缓存的命令发给从数据库，从数据库收到数据后，会载入快照文件并执行缓存的命令。以上过程称为复制初始化。复制初始化之结束后，主数据库每收到写命令时就会将命令同步给从数据库，从而保证主从数据库数据一致，这一过程称为复制同步阶段。


![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/replication/1.png)

> 注意：redis主从同步版本必须一致，不一致的话同步过程中会出现各种奇葩问题！

操作系统环境如下：
```
[root@a47b0619d2ad ~]# cat /etc/issue
CentOS release 6.7 (Final)
Kernel \r on an \m
[root@a47b0619d2ad ~]# getconf LONG_BIT
64
```

# 1.主从规划如下


|redis|port|dir|config|
|:-:|:-:|:-:|:-:|
|master|7001|/opt/7001|redis.conf|
|slave|7002|/opt/7002|redis.conf|

# 2.主从同步

> 1：拷贝redis编译安装后的配置文件以及redis-server

```
[root@a47b0619d2ad ~]# cp /usr/local/src/redis-3.0.6/redis.conf /usr/local/src/redis-3.0.6/src/redis-server  /opt/7001/
[root@a47b0619d2ad ~]# cp /usr/local/src/redis-3.0.6/redis.conf /usr/local/src/redis-3.0.6/src/redis-server  /opt/7002/

#目录结构
[root@a47b0619d2ad ~]# tree /opt/700*
/opt/7001
├── redis.conf
└── redis-server
/opt/7002
├── redis.conf
└── redis-server
0 directories, 4 files

```

> 2：redis 主服务配置文件

```
[root@a47b0619d2ad ~]# vim /opt/7001/redis.conf
pidfile /var/run/redis-7001.pid
port 7001
logfile "/var/log/redis/redis-7001.log"
dir /opt/7001/

#注：以上只是修改redis主配置文件，其他默认


```


启动redis master主服务

```
[root@a47b0619d2ad ~]# /opt/7001/redis-server /opt/7001/redis.conf
[root@a47b0619d2ad ~]# netstat -ntpl|grep 7001
tcp        0      0 0.0.0.0:7001                0.0.0.0:*                   LISTEN      5595/redis-server *
tcp        0      0 :::7001                     :::*                        LISTEN      5595/redis-server *

```



> 3：redis 从服务配置文件

```
[root@a47b0619d2ad ~]# vim /opt/7002/redis.conf
pidfile /var/run/redis-7002.pid
port 7002
logfile "/var/log/redis/redis-7002.log"
dir /opt/7002/
slaveof 127.0.0.1 7001 //redis主服务的ip地址以及端口号
注：如果redis master设置了验证密码，还需配置masterauth xxx，指redis master上认证密码即可。

```

启动redis slave从服务

```
[root@a47b0619d2ad ~]# /opt/7002/redis-server /opt/7002/redis.conf
[root@a47b0619d2ad ~]# netstat -ntpl|grep 7002
tcp        0      0 0.0.0.0:7002                0.0.0.0:*                   LISTEN      5605/redis-server *
tcp        0      0 :::7002                     :::*                        LISTEN      5605/redis-server *

```


> 4：登录redis master

```
[root@a47b0619d2ad ~]# redis-cli -p 7001
127.0.0.1:7001> info
............
............
# Replication
role:master
connected_slaves:1
slave0:ip=127.0.0.1,port=7002,state=online,offset=71,lag=1
master_repl_offset:71
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:2
repl_backlog_histlen:70
注：通过info命令，可以查看当然redis服务的角色是master，以及slave相关信息等。

```

设置一些数据....

```
127.0.0.1:7001> set name zxl
OK
127.0.0.1:7001> set age 33
OK
127.0.0.1:7001> get name
"zxl"
127.0.0.1:7001> get age
"33"
127.0.0.1:7001>

```

> 5：登录redis slave服务器

```
[root@a47b0619d2ad ~]# redis-cli -p 7002
127.0.0.1:7002> info
...........
...........
# Replication
role:slave
master_host:127.0.0.1
master_port:7001
master_link_status:up
master_last_io_seconds_ago:6
master_sync_in_progress:0
slave_repl_offset:212
slave_priority:100
slave_read_only:1
connected_slaves:0
master_repl_offset:0
repl_backlog_active:0
repl_backlog_size:1048576
repl_backlog_first_byte_offset:0
repl_backlog_histlen:0
# Keyspace
db0:keys=2,expires=0,avg_ttl=0
#注：通过info命令，可以查看当然redis服务的角色是slave，以及master相关信息等。


```

redis slave查看数据

```
127.0.0.1:7002> KEYS *
1) "age"
2) "name"
127.0.0.1:7002> get age
"33"
127.0.0.1:7002> get name
"zxl"
#注：redis slave上可以查看到来自redis master上同步的数据。

127.0.0.1:7002> set ox xxoo
(error) READONLY You can't write against a read only slave.
127.0.0.1:7002>

```
注：redis slave只是只读数据库，而不能插入数据。如需要写，把redis slave配置文件中slave-read-only yes改为slave-read-only no，重启服务即可。

如果不想通过配置文件来更改的话，也可以使用config更改，实例如下：
```
127.0.0.1:7002> CONFIG GET slave-read-only
1) "slave-read-only"
2) "yes"
127.0.0.1:7002> CONFIG set slave-read-only no
OK
127.0.0.1:7002> CONFIG GET slave-read-only
1) "slave-read-only"
2) "no"
```


# 3.主从在线切换

把redis的7002端口切换为主服务
```
127.0.0.1:7002> CONFIG GET slaveof //查看
1) "slaveof"
2) "127.0.0.1 7001"
127.0.0.1:7002> SLAVEOF no one //设置为主服务
OK

```

插入数据

```
127.0.0.1:7002> KEYS *
1) "age"
2) "name"
127.0.0.1:7002> get age
"33"
127.0.0.1:7002> set a 11
OK
127.0.0.1:7002> KEYS *
1) "age"
2) "name"
3) "a"

```

原来redis主节点端口为7001设置为从服务节点

```
127.0.0.1:7001> SLAVEOF 127.0.0.1 7002
OK

```

注：以上就是主从在线切换redis主节点@，主从在线切换后可以通过info命令查看相关信息


转自:
[redis主从同步及切换主从配置示例](http://noodle.blog.51cto.com/2925423/1731484)