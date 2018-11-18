---
title: Nginx日志
categories: nginx   
toc: true  
tags: [nginx]
---




# 1.错误日志

## 1.1.语法
```
error_log  file   level;
#关键字   日志文件 错误日志级别

```

## 1.2.常见的错误日遏制级别

&emsp;有【debug|info|notice|warn|error|crit|alert|emerg】从左到右，级别越高，记录的信息越少，一般情况下是warn|error| crit这三个级别之一，注意在生产环境中不要配置info等低级别，会带来大的磁盘I/O消耗

## 1.3.默认值
```
#default：error_log logs/error.log error;
```

## 1.4.可以放置的标签段为
```
#context：main , http, server,  location
```

# 2.访问日志

## 2.1.日志格式log_format
```
#他属于http_log_module模块
http://nginx.org/en/docs/http/ngx_http_log_module.html
```

### 2.1.1.格式及各个字段的含义

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/log/1.png)
 
```
/*
$remote_addr   与$http_x_forwarded_for 用以记录客户端的ip地址；
$remote_user   ：用来记录客户端用户名称；
$time_local  ： 用来记录访问时间与时区； 
$request  ：  用来记录请求的url与http协议；
$status     ：  用来记录请求状态；成功是200，
$body_bytes_s ent  ：记录发送给客户端文件主体内容大小；
$http_referer  ：用来记录从那个页面链接访问过来的；
$http_user_agent  ：记录客户毒啊浏览器的相关信息；
 
-：空白，用一个“-”占位符替代，历史原因导致还存在。
 
$http_x_forwarded_for  当前端有代理服务器时，设置Web节点记录客户端地址的配置，此参数生效的前提是代理服务器上也要有进行相关的x_forwarded_for设置。

*/

```


### 2.1.2.一条日志的例子
```
192.168.1.102 - scq2099yt [18/Mar/2013:23:30:42 +0800] "GET /stats/awstats.pl?config=scq2099yt HTTP/1.1" 200 899 "http://192.168.1.1/pv/" "Mozilla/4.0 (compatible; MSIE 6.0; Windows XXX; Maxthon)"
```



### 2.1.3.注意事项

需要注意的是：log_format配置必须放在http内，否则会出现如下警告信息：
```
nginx: [warn] the "log_format" directive may be used only on "http" level in /etc/nginx/nginx.conf:97

```

## 2.2.access_log
```
Syntax: access_log path [format [buffer=size] [gzip[=level]] [flush=time] [if=condition]];
access_log off;     #表示不记录访问日志

#默认配置:  access_log logs/access.log combined;

#可以放置的位置: http, server, location, if in location, limit_except
#参数说明：buffer=size 为存放访问日志的缓冲区大小，flush=time为将缓冲区的日志刷到磁盘的时间，gzip[=level]表示压缩的级别，
#一般将访问日志放在对应的虚拟主机下，这样不同的站点有不同的访问日志，我们可以更好的统计

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/log/2.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/log/3.png)
 

# 3.日志轮询shell脚本
&emsp;思想：就是写一个shell脚本，然后将对应虚拟主机的访问日志，改名，改成带时间戳的文件名，然后重启Nginx，这样就又会生成一个对应虚拟主机的访问日志文件

## 3.1.写shell脚本
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/log/4.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/log/5.png)

 
## 3.2.测试


![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/log/6.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/log/7.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/log/8.png)
 

## 3.3.定时任务

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/log/9.png)

 




