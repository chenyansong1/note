---
title: Linux下编译安装memcached
categories: memcached   
toc: true  
tags: [memcached]
---


# 1.准备编译环境
```
yum install gcc make cmake autoconf libtool                        #gcc make 都是编译器
```

# 2.安装依赖：libevent

```
去http://libevent.org/   下载稳定版本

tar zxvf libevent-2.0.21-stable.tar.gz
cd libevent-2.0.21-stable
./configure --prefix=/usr/local/libevent                        #指定libevent的安装位置
make && make install

```


# 3.编译memcached
```
cd memcached-1.4.5
./configure--prefix=/usr/local/memcached --with-libevent=/usr/local/libevent      #指定上面安装libevent的安装位置，因为memcached依赖libevent
make && make install

```


# 4.启动，测试
```shell
#查看帮助
./bin/memcached -h


#启动（不指定用户）
不允许使用root去启动，因为一般不用root，安全问题
[root@originalOS memcached]# ./bin/memcached -m 64 -p 11211 -vv
can\'t run as root without the -u switch
[root@originalOS memcached]#


#启动（指定用户：nobody用户）
[root@originalOS memcached]# ./bin/memcached -m 64 -u nobody -p 11211 -vv       #-vv表示前台直接启动，会打印详细信息


#以守护进程的方式运行(-d)
[root@originalOS memcached]# ./bin/memcached -m 64 -p 11211 -u nobody -d
[root@originalOS memcached]# ps -ef|grep memcached
nobody     8695      1  3 19:19 ?        00:00:00 ./bin/memcached -m 64 -p 11211 -u nobody -d


```


# 5.编译出现问题，解决
注意: 在虚拟机下练习编译,一个容易碰到的问题---虚拟机的时间不对,导致的 gcc 编译过程中,检测时间通不过,一直处于编译过程，错误的原因在于系统时间比文件修改时间早（可能是虚拟机长时间没有用了，没有设置时间同步）
```
# date -s 'yyyy-mm-dd hh:mm:ss'
# clock -w # 把时间写入 cmos

```



# 6.启动多个实例
```
#可以启动多个实例（指定不同的端口即可）
[root@originalOS memcached]# ./bin/memcached -p 11212 -u nobody -d
[root@originalOS memcached]# ./bin/memcached -p 11213 -u nobody -d
 
#查看启动的实例
[root@originalOS memcached]# ps -ef|grep memcached              
nobody     8695      1  0 19:19 ?        00:00:00 ./bin/memcached -m 64 -p 11211 -u nobody -d
nobody     8711      1  1 19:29 ?        00:00:00 ./bin/memcached -p 11212 -u nobody -d
nobody     8721      1  1 19:29 ?        00:00:00 ./bin/memcached -p 11213 -u nobody -d
root       8729   1022  3 19:29 pts/0    00:00:00 grep memcached
 
```








