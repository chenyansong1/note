---
title: redis安装
categories: redis   
toc: true  
tags: [redis]
---





另一种方式的安装：https://blog.csdn.net/hello_junz/article/details/78135718

# 1.下载

```
wget  http://download.redis.io/releases/redis-3.2.4.tar.gz
或者到官网下载：http://redis.io/download
```

# 2.编译
```
tar -zxvf redis-3.2.4.tar.gz
cd redis-3.2.4/
>make && make install
'这时Redis 的可执行文件被放到了/usr/local/bin 目录下'
```


# 3.下载配置文件和启动脚本
```
wget https://github.com/ijonas/dotfiles/raw/master/etc/init.d/redis-server
wget https://github.com/ijonas/dotfiles/raw/master/etc/redis.conf


#下载这2个配置文件是为了能更好的启动和关闭redis。
#在/usr/local/bin 目录下'，自带的redis-server也可以用默认的(二进制编译)不能打开，下载来的同名文件是一个shell脚本（自己写的一个启动脚本），调用启动/usr/local/bin 目录下'的redis-server，可以打开查看修改

#启动需要加上配置文件
./src/redis-server  redis.conf
```

# 4.把配置文件放到合适的位置
```
cp redis.conf /etc/                            #配置文件
cp redis-server /etc/init.d/                #启动脚本文件
chmod +x /etc/init.d/redis-server                #添加执行权限

```

# 5.第一次启动Redis前，做一些准备工作
1 建立redis专用的用户：useradd redis。
2 建立数据目录：mkdir -p /var/lib/redis
3 建立日志目录：mkdir -p /var/log/redis
4 设置这些目录的权限：
chown redis.redis /var/lib/redis
chown redis.redis /var/log/redis
上面的这些目录的创建位子是根据配置文件的设置来确认的


# 6.redis加入开机自启动
```
[root@originalOS redis-3.2.4]# chkconfig redis-server on
[root@originalOS redis-3.2.4]# chkconfig redis-server --list
redis-server    0:off   1:off   2:on    3:on    4:on    5:on    6:off

```

# 7.重启
```
/etc/init.d/redis-server start
/etc/init.d/redis-server stop
/etc/init.d/redis-server restart

启动脚本已经指定了配置文件，所以在启动的时候不需要再显性的指定配置文件了。要是用自己的配置文件，不用下载来的，则需要：
/usr/local/bin/redis-server  /etc/redis.conf  #指定配置文件
```
# 8.加入环境变量
```
cp /usr/local/bin/redis*  /usr/bin/                                #因为/usr/bin默认是在环境变量的PATH中的，所以将/usr/local/bin/redis*拷贝到/usr/bin下也是可以找到的

#或者是将/usr/local/bin/ 的路径加入环境变量：
vim  /etc/profile
export PATH = /usr/local/bin:$PATH

```

# 8.客户端连接

```
$ redis-cli -h host -p port -a password
$ redis-cli -h 127.0.0.1 -p 6379 -a "mypass"
redis 127.0.0.1:6379>
redis 127.0.0.1:6379> PING
```
