---
title: nginx安装
categories: nginx   
toc: true  
tags: [nginx]
---



官网：www.nginx.org

# 1.检查版本
&emsp;在安装之前，要先检查机器的版本，否则安装的时候可能出现系统不兼容的情况

```

[root@lamp01 ~]# cat /etc/redhat-release
CentOS release 6.3 (Final)
[root@lamp01 ~]# uname -r
2.6.32-279.el6.i686
[root@lamp01 ~]# uname -m
i686

```

# 2.安装pcre pcre-devel

```
#Pcre (Perl Compatible Regular Expressions) perl兼容的正则表达式，官网：www.pcre.org
#HTTP rewrite modul requires the PCRE library (rewrite伪静态需要pcre的支持)

rpm -qa pcre pcre-devel
yum install pcre-devel -y

```

# 3.安装openssl
```
/*
ssh的加密软件
SSL module require the OpenSSL library
 */
yum install openssl-devel -y
```


# 4.安装nginx

http://nginx.org/download/nginx-1.6.3.tar.gz


## 4.1.下载
```
mkdir /root/oldboy/tools -p
cd /root/oldboy/tools
wget -q http://nginx.org/download/nginx-1.6.3.tar.gz
/*
其中：-q是取消输出
下载地址， 可以去nginx的官网download页面，然后右键复制地址
*/

#解压  
tar -zxvf nginx-1.6.3.tar.gz 

```

## 4.2.编译
```
./configure --prefix=/application/nginx-1.6.3 --user=nginx --group=nginx --with-http_ssl_module --with-http_stub_status_module

# prefix : 表示软件安装在哪
#user,  group 是以什么用户和组来安装软件
#http_ssl_module 是安全模块;      http_stub_status_module 是监控模块,状态页面

#一些特殊的模块
--http-client-body-temp-path=path  #客户端的通过post上传的文件的临时目录(如果文件很大的话，不可能都放在内存中)
defines a directory for storing temporary files that hold client request bodies. After installation, the directory can always be changed in the nginx.conf configuration file using the client_body_temp_path directive. By default the directory is named prefix/client_body_temp.

--http-proxy-temp-path=path #作为代理时需要的临时目录

--error-log-path=path
sets the name of the primary error, warnings, and diagnostic file. After installation, the file name can always be changed in the nginx.conf configuration file using the error_log directive. By default the file is named prefix/logs/error.log.

--http-log-path=path
sets the name of the primary request log file of the HTTP server. After installation, the file name can always be changed in the nginx.conf configuration file using the access_log directive. By default the file is named prefix/logs/access.log.

--pid-path=path
sets the name of an nginx.pid file that will store the process ID of the main process. After installation, the file name can always be changed in the nginx.conf configuration file using the pid directive. By default the file is named prefix/logs/nginx.pid.

--lock-path=path
sets a prefix for the names of lock files. After installation, the value can always be changed in the nginx.conf configuration file using the lock_file directive. By default the value is prefix/logs/nginx.lock.

--with-http_gunzip_module
enables building the ngx_http_gunzip_module module that decompresses responses with “Content-Encoding: gzip” for clients that do not support “gzip” encoding method. This module is not built by default.

--with-http_gzip_static_module
enables building the ngx_http_gzip_static_module module that enables sending precompressed files with the “.gz” filename extension instead of regular files. This module is not built by default.

--with-http_flv_module	#流媒体
enables building the ngx_http_flv_module module that provides pseudo-streaming server-side support for Flash Video (FLV) files. This module is not built by default.

--with-http_mp4_module
enables building the ngx_http_mp4_module module that provides pseudo-streaming server-side support for MP4 files. This module is not built by default.

#之所以用编译的方式，不用yum的方式，是我们可以定制，哪些模块需要我们就安装，哪些模块不需要我们就不编译安装
#with表示默认是启用的
#without表示默认是没有启用的
#我们将：--without-pcre 我们就可以取消对pcre模块的加入,对于陌生的软件，我们需要看他的readme：
#查看编译的参数：如果软件不是我们安装的，我们要知道其中安装了哪些模块，使用:-V 来查看，如下：

```


## 4.3.安装
```
make && make install
```

## 4.4.创建需要的用户
```
useradd nginx -s /sbin/nologin -M
```

## 4.5.创建软链接
```
ln -s /application/nginx-1.6.3/ /application/nginx
```

# 5.启动测试
```
#1.启动
/application/nginx/sbin/nginx

#2.检查端口
lsof -i :80     ##查看服务和端口（默认端口是80）
ps -ef |grep nginx

#3.使用浏览器访问（安装nginx的主机ip为：http://192.168.0.106/）默认端口是：80
http://192.168.0.106/
 
#这里有点相当于tomcat


```

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/install/1.png)


&emsp;如果使用浏览器访问不了：1.先ping ip ，2然后Telnet ip port 3.查看防火墙是否关闭，或者是selinux打开了，使用setgotten 0将其临时关闭即可


# 6.启动参数说明
```
[root@lamp01 ~]# cd /application/nginx/
[root@lamp01 nginx]# ./sbin/nginx -h
nginx version: nginx/1.6.3
Usage: nginx [-?hvVtq] [-s signal] [-c filename] [-p prefix] [-g directives]
 
Options:
  -?,-h         : this help
  -v            : show version and exit
  -V            : show version and configure options then exit        #显示编译的时候加入的模块
  -t            : test configuration and exit    #相当于语法检查
  -q            : suppress non-error messages during configuration testing
  -s signal     : send signal to a master process: stop, quit, reopen, reload        #启动参数
  -p prefix     : set prefix path (default: /application/nginx-1.6.3/)
  -c filename   : set configuration file (default: conf/nginx.conf)
  -g directives : set global directives out of configuration file

```




# Nginx目录结构说明
```
[root@lamp01 nginx]# ll /application/nginx/
总用量 36
drwx------ 2 nginx root 4096 8月   1 2016 client_body_temp
drwxr-xr-x 3 root  root 4096 2月  15 18:21 conf
drwx------ 2 nginx root 4096 8月   1 2016 fastcgi_temp
drwxr-xr-x 4 root  root 4096 8月   1 2016 html
drwxr-xr-x 2 root  root 4096 8月   3 2016 logs
drwx------ 2 nginx root 4096 8月   1 2016 proxy_temp
drwxr-xr-x 2 root  root 4096 8月   1 2016 sbin
drwx------ 2 nginx root 4096 8月   1 2016 scgi_temp
drwx------ 2 nginx root 4096 8月   1 2016 uwsgi_temp

#其中temp都是一些临时文件

[root@lamp01 nginx]# ll|grep -v temp      
总用量 36
drwxr-xr-x 3 root  root 4096 2月  15 18:21 conf    #配置文件目录
drwxr-xr-x 4 root  root 4096 8月   1 2016 html    #站点目录
drwxr-xr-x 2 root  root 4096 8月   3 2016 logs    #日志目录
drwxr-xr-x 2 root  root 4096 8月   1 2016 sbin    #启动目录


```




# 7.错误排查


没有安装pcre-devel
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/install/2.png)

安装操作系统的时候没有安装gcc

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/install/3.png)


需要安装下面的软件包
```
yum groupinstall "Development tools"

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/install/4.png)

 

查看log日志

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/install/5.png)















