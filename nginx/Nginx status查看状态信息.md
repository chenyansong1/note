---
title: Nginx status查看状态信息
categories: nginx   
toc: true  
tags: [nginx]
---



&emsp;Nginx软件的功能模块中有一个ngx-http_stub_status_module 模块，这个模块能记录Nginx的基本访问状态信息，让使用者能够记录Nginx的基本访问状态信息。
# 1.检查是否有http_stub_status_module模块
```
[root@lamp01 ~]# /application/nginx/sbin/nginx -V  
nginx version: nginx/1.6.3
built by gcc 4.4.6 20120305 (Red Hat 4.4.6-4) (GCC)
TLS SNI support enabled
configure arguments: --prefix=/application/nginx-1.6.3 --user=nginx --group=nginx --with-http_ssl_module --with-http_stub_status_module

```

# 2.新建一个虚拟主机，用于查看状态用
```
[root@lamp01 extra]# cat /application/nginx/conf/extra/status.conf
    server {
        listen      80;
        server_name  status.etiantian.org;
        location / {
            stub_status on;    #将状态打开
            access_log off;
        }
    }
```

# 3.添加到主配置文件中
```
[root@lamp01 conf]# cat ./nginx.conf
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    include extra/www.conf;
    include extra/bbs.conf;
    include extra/status.conf;        #添加到主配置文件中
 
}
```
# 4.测试
```
#修改/etc/hosts,添加
192.168.0.3 status.etiantian.org
#192.168.0.3是我的机器地址

#语法检查
[root@lamp01 conf]# ../sbin/nginx -t       
nginx: the configuration file /application/nginx-1.6.3/conf/nginx.conf syntax is ok
nginx: configuration file /application/nginx-1.6.3/conf/nginx.conf test is successful
#重新加载
[root@lamp01 conf]# ../sbin/nginx -s reload

#访问
[root@lamp01 conf]# curl status.etiantian.org
Active connections: 1
server accepts handled requests
1 1 1
Reading: 0 Writing: 1 Waiting: 0
 
```

状态参数说明
```
Active connections: 1
#表示Nginx正在处理的活动连接数1个

server accepts handled requests
1 1 1
#第一个server表示Nginx启动到现在共处理了1个连接
#第二个accepts表示Nginx启动到现在成功创建1次握手,请求丢失数=(握手数-连接数), 可以看出,本次状态显示美誉丢失请求
#第三个handled requests ,表示总共处理了1次请求

Reading: 0 Writing: 1 Waiting: 0

Reading:    Nginx读取客户端的Header信息数
Writing:    Nginx返回给客户端的Header信息数
Waiting:   Nginx已经处理完正在等候下一次请求指令的驻留连接,开启keep-alive的情况下,这个值等于active-(reading+writing)

```
 

