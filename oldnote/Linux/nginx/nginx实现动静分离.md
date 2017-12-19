---
title: nginx实现动静分离
categories: nginx   
toc: true  
tags: [nginx]
---



# 1.应用场景

&emsp;在企业中，有事希望只用一个域名对外提供服务，不希望使用多个域名对应同一个产品业务，此时就需要在代理服务器上通过配置规则，使得匹配不同规则的请求会交给不同的服务器池处理，这类业务有：

1. 业务的域名没有拆分或者不希望拆分，但是希望实现动静分离，多业务分离。
2. 不同的客户端设备（例如：手机和PC端）使用同一个域名访问同一个业务网站，就需要根据规则将不同设备的用户请求交给后端不同的服务器处理，以便得到最佳的用户体验。


# 2.拆分

&emsp;将文件上传程序、动态web程序和静态数据请求程序分别放在不同的服务器中，这样实现业务拆分上传服务器：将上传文件的地址放到数据库中，将文件存储到NFS中

* 动态web服务器：直接和数据库打交道
* 静态数据请求服务器：直接请求NFS数据

![图片来自老男孩](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/static-dam/1.png)

 
 
# 3.程序没有拆分
&emsp;如果：文件上传程序、动态web程序和静态数据请求程序没有拆分，那么将所有的程序在下面三种服务器中都存放一份，然后，对于负载均衡器中的请求，要根据条件过滤是找静态数据请求服务器，还是找动态程序服务器（此时上传程序和动态web程序一起了）

# 4.实战1
## 4.1.先进行企业案例需求梳理
1. 当用户请求www.etiantian.org/upload/xx地址时实现由upload上传服务器池处理请求
2. 当用户请求www.etiantian.org/static/xx地址时实现由静态服务器池处理请求
3. 除此之外，对于其他访问请求，全都由默认的动态服务器池处理请求


## 4.2配置说明
```
#static_pools 为静态服务器池，有一个服务器，地址为10.0.0.9，端口为80
upload static_pools {
    server 10.0.0.9:80 weight=1
}

#upload_pools为上传服务器池，有一个服务器，地址为10.0.0.10，端口为80
upload upload_pools {
    server 10.0.0.10:80 weight=1
}

#default_pools为动态服务器池，有一个服务器，地址为10.0.0.9，端口为8000
upload default_pools {
    server 10.0.0.9:8000 weight=1
}

```
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/static-dam/2.png)

 
## 4.3.location正则匹配

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/static-dam/3.png)


## 4.4.if判断

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/static-dam/4.png)
 

# 5.实战2

## 5.1根据PC端浏览器的不同设置对应的匹配规则
```
location / {
   if ($http_user_agent ~* “MSIE”){   #如果请求的浏览器为IE（MSIE），请求有static_pools池处理
         proxy_pass http://static_pools;
      }
   if ($http_user_agent ~* “Chrome”){    #如果请求的浏览器为谷歌，请求有upload_pools池处理
         proxy_pass http://upload_pools;
      }
}
 
#$http_user_agent  是客户端代理，即客户端是使用的什么来访问的

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/static-dam/5.png)
 

## 5.2根据移动端设备拆分

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/nginx/static-dam/6.png)
