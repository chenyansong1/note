---
title: nginx负载均衡
categories: nginx   
toc: true  
tags: [nginx]
---



# 1.官网中的模块

http://nginx.org/en/docs/http/ngx_http_upstream_module.html


# 2.环境准备

2台lamp（都安装好nginx）
1台负载均衡（安装好nginx）


# 3.配置一个简单的负载均衡配置文件

创建一个最简的配置文件(去掉注释的部分)
```
egrep -v "#|^$" /application/nginx/conf/nginx.conf.default > /application/nginx/conf/nginx.conf

```
在负载均衡服务器上的：
```
[root@lb01 nginx]# cat /application/nginx/conf/nginx.conf
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    upstream www_server_pools {
        server 192.168.0.4:80;
        server 192.168.0.3:80;
    }
    server {
        listen  80;
        server_name     www.etiantian.org;
        location / {
                proxy_pass      http://www_server_pools;
        }
    }
}

```


# 4.upstream中server后的参数
```
    upstream www_server_pools {
        server 192.168.0.4:80 weight=1 max_fails=3;
        server 192.168.0.3:80 backup;
    }

/*
可用的参数说明:
server 10.0.10.8:80      可以是ip或域名，端口不写，默认是80 ，高并发场景IP可换成域名，通过DNS做负载均衡
weight=1                代表服务器的权重，默认是1，权重数字越大代表接受的请求越多
max_fails=1         Nginx尝试连接后端主机失败的此时，这个值配合：proxy_next_upstream,fastcgi_netx_upstream  and    memcached_next_upstream 这三个参数使用，当Nginx接收到后端服务器返回这三个参数定义的状态码时，会将这个请求转发给正常工作的后端服务器，例如：404,502,503。默认值1，企业场景：建议2-3次，根据业务去配置
backup            就是在所有的server都不可用时，才使用backup服务器，就相当于备胎, 当算法为ip_hash的时候，后端算法不能是weight和backup，因为他直接根据ip就决定访问哪台服务器了，就不用weight和backup来决定访问
fail_timeout=10s      在max_fails定义的失败次数后,距离下次检查的间隔时间,默认是10s,如果max_fails是5,他就检查5次,如果5次都是502,那么,他就会根据fail_timeout的值,等待10s再去检查,还是只检查一次,如果持续502,在不重新加载nginx配置的情况下,每隔10s都只检测一次,常规业务2-3秒比较合理,比如京东3秒,蓝汛3秒,可根据业务需求去配置
down      这标志着服务器永远不可用,这个参数可配合ip_hash使用
max_conns=number      单个RS最大并发连接数限制,防止请求过载,保护节点服务器
route      设置server路由的名字
slow_start=time      宕机的服务器从恢复开始,多长时间内被认为是健康的


*/

```

 
# 5.upstream调度算法
&emsp;调度算法一般分为两类:第一类为静态调度算法,即负载均衡器更具自身设定的规则进行分配,不需要考虑后端节点服务器的情况,例如:rr, wrr, ip_hash等都属于静态调度算法
&emsp;第二类为动态调度算法,即:负载均衡器会根据后端节点的当前状态来决定是否分发请求,例如:连接数少的优先获得请求,相应时间短的获得请求,例如:least_conn,    fair等都属于动态调度算法


## 5.1.rr轮询(默认调度算法,静态调度算法)
&emsp;rr ；你一个，我一个，你一个，我一个
&emsp;按客户端请求顺序把客户端的请求逐一分配到不同的后端节点服务器,这相当于LVS中的rr算法,如果侯丹节点服务器宕机(默认情况下nginx只检测80端口),宕机的服务器会被自动从节点服务器池中剔除,以使客户端用户访问不受影响,新的请求会分配给正常的服务器

## 5.2.wrr(权重轮询,静态调度算法)
&emsp;在rr轮询的基础上加上权重,即为权重轮询算法,当使用该算法时,权重和用户访问成正比,权重值越大,被访问的请求也就越多,可以根据服务器的配置和性能指定权重值大小,可以有效解决新旧服务器性能不均带来的请求分配问题
&emsp;举个例子:
```
后端服务器:192.168.1.2的配置为: E5520*2CPU, 8GB内存
后端服务器:192.168.1.3的配置为: Xeon(TM)2.80GHz*2, 4GB内存
假设希望在有30个请求到达前端时,其中20个请求交给192.168.1.3处理,剩余10个交给192.168.1.2
```
 

## 5.3.ip_hash(静态调度算法)

&emsp;每个请求按客户端IP的hash结果分配,当新的请求到达时,先将其客户端ip通过哈希算法哈希出一个值,在随后的客户端请求中,客户IP的哈希值只要相同,机会被分配至同一台服务器,该调度算法可以解决动态网页的session共享问题,但有时会导致请求分配不均,即无法保证1:1的负载均衡,因为在国内大多数公司都是NAT上网模式,多个客户端会对应一个外部IP,所以这些客户端都会被分配到同一节点服务器,从而导致请求分配不均

 
## 5.4.fair(动态调度算法)
fair：谁响应快就给谁发
&emsp;此算法会根据后端节点服务器的相应时间来分配请求,响应时间短的优先分配
&emsp;这是更加智能的调度算法,此种算法可以依据页面大小和加载时间长短智能的进行负载均衡,也就是根据后端服务器的响应时间来分配请求,响应时间短的优先分配,Nginx本身不支持fair调度算法,如果需要使用这种调度算法,必须下载Nginx的相关模块upstream_fair 

## 5.5.least_conn
least_conn ：谁最少连接数，就给谁发
&emsp;least_conn算法会根据后端节点的连接数来决定分配情况,哪个机器连接数少就分发,此外还有一些第三方的调度算法,例如:url_hash, 一致性HASH算法等


## 5.6.url_hash算法
&emsp;和ip_hash类似,这里是根据访问url的hash结果来分配请求的,让每个url定向到同一个后端服务器,后端服务器为缓存服务器时效果显著,在upstream中加入hash语句,server语句中不能写入weight等其他的参数,hash_methodshiyo使用的是hash算法
&emsp;url_hash按访问url的hash结果来分配请求,使每个url定向到同一个后端服务器,可以进一步提高后端缓存服务器的效率命中率,Nginx本身不支持url_hash的,如果需要使用这种调度算法,必须安装Nginx的hash软件包

url_hash的问题
&emsp;当新增服务器或者是一个缓存服务器挂了，那么url_hash将重新排列，此时原先的缓存服务器中的缓存的数据就无效了，因为可能重新排过的url，将不会找原来的服务器了，因为根据服务器的数据来取模，然后找对应的服务器，如果数量变了，那么取模之后找的服务器就会变，所以原来的缓存将会失效



## 5.7.一致性HASH算法

&emsp;一致性HASH算法一般用于代理后端业务为缓存服务的场景,通过将用户请求的URI或者指定字符串进行计算,然后调度到后端的服务器上,此后任何用户查找同一个URI都会被调度到这一台服务器上,因此后端的每个节点缓存的内容都是不同的,一致性HASH算法可以让后端某个或几个节点宕机后,缓存的数据动荡的最小




