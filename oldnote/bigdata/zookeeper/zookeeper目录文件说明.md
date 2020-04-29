---
title: zookeeper目录文件说明
categories: hadoop   
tags: [zookeeper]
---


# 1.bin目录文件

```
/*
.sh为linux的脚本文件
.cmd为Windows的脚本文件
*/
[root@data-1-1 zookeeper0]# cd bin/
[root@data-1-1 bin]# ll
total 48
-rwxr-xr-x 1 1000 1000  238 Feb 20  2014 README.txt
-rwxr-xr-x 1 1000 1000 1937 Feb 20  2014 zkCleanup.sh
-rwxr-xr-x 1 1000 1000 1049 Feb 20  2014 zkCli.cmd                        
-rwxr-xr-x 1 1000 1000 1534 Feb 20  2014 zkCli.sh            #客户端启动脚本
-rwxr-xr-x 1 1000 1000 1333 Feb 20  2014 zkEnv.cmd
-rwxr-xr-x 1 1000 1000 2697 Nov  5 16:53 zkEnv.sh            #环境变量启动脚本
-rwxr-xr-x 1 root root 2696 Nov  5 16:44 zkEnv.sh.bak
-rwxr-xr-x 1 1000 1000 1084 Feb 20  2014 zkServer.cmd
-rwxr-xr-x 1 1000 1000 5797 Nov  6 19:35 zkServer.sh            #服务端启动脚本
-rw-r--r-- 1 root root 5980 Nov  5 16:45 zookeeper.out            #日志文件
[root@data-1-1 bin]# 
```

# 2.conf目录
1. zoo_sample.cfg为样例配置文件，需要修改为自己的名称，一般为zoo.cfg
2. Log4j.properties为日志配置文件
```
[root@data-1-1 zookeeper0]# cd conf/
[root@data-1-1 conf]# ll
total 48
-rw-rw-r-- 1 1000 1000   535 Feb 20  2014 configuration.xsl
-rw-rw-r-- 1 1000 1000  2161 Nov  5 16:55 log4j.properties                #log4j日志
-rw-r--r-- 1 root root  1085 Nov  6 19:20 zoo.cfg            #启动的配置文件
-rw-r--r-- 1 root root 21487 Nov  6 23:11 zookeeper.out        #日志文件
-rw-rw-r-- 1 1000 1000   922 Feb 20  2014 zoo_sample.cfg.bak          #启动的配置文件（原）
[root@data-1-1 conf]# 

```

# 3.contrib目录
一些用于操作zk的工具包
```
[root@data-1-1 zookeeper0]# cd contrib/
[root@data-1-1 contrib]# ll
total 32
drwxr-xr-x 4 1000 1000 4096 Feb 20  2014 fatjar
drwxr-xr-x 3 1000 1000 4096 Feb 20  2014 loggraph
drwxr-xr-x 2 1000 1000 4096 Feb 20  2014 rest
drwxr-xr-x 3 1000 1000 4096 Feb 20  2014 zkfuse
drwxr-xr-x 4 1000 1000 4096 Feb 20  2014 zkperl
drwxr-xr-x 3 1000 1000 4096 Feb 20  2014 zkpython
drwxr-xr-x 4 1000 1000 4096 Feb 20  2014 zktreeutil
drwxr-xr-x 7 1000 1000 4096 Feb 20  2014 ZooInspector
[root@data-1-1 contrib]# 

```
# 4.lib目录
zk依赖的某些jar包

# 5.recipes
zk某些用法的代码示例

# 6.dist-maven 目录
maven编译后的发布目录


# 7.dataDir目录
在zoo.cfg中的dataDir 选项配置中是数据的配置目录
如，默认是：/tmp/zookeeper/
那么在zk服务启动额时候，在/tmp/zookeeper/zookeeper_server.pid  是server的进程pid
如果将服务停止，那么该文件将会被自动删除



