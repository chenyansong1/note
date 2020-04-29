---
title: Nginx状态码
categories: nginx   
toc: true  
tags: [nginx]
---


nginx 403 forbidden

引起nginx 403 forbidden有二种原因，一是缺少索引文件，二权限问题。
> 缺少index.html或者index.php文件

```
server { 
 listen       80; 
 server_name  localhost; 
 index  index.php index.html; 
 root  /home/zhangy/www; 

#如果在/home/zhang/www下面没有index.php,index.html的时候，直接访问域名，找不到文件，会报403 forbidden。例如：你访问www.test.com而这个域名，对应的root指定的索引文件不存在。

```

> 权限问题

因为权限问题引起的403，个人觉得比较难查找，因为一时想不起
```
server { 
 listen       80; 
 server_name  localhost; 
 index  index.php index.html; 
 root  /home/zhangy/www; 

#我把web目录放在用户的所属目录下面，nginx的启动用户默认是nginx的，所以对目录根本没有读的权限，这样就会报403错误了。这个时候，把web目录的权限改大，或者是把nginx的启动用户改成目录的所属用户，重起一下就能解决。
```




