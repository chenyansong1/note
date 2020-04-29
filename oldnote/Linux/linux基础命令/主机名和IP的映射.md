---
title: Linux基础命令之主机名和IP的映射
categories: Linux   
toc: true  
tags: [Linux基础命令]
---

规范步骤
> 方式一:

``` shell

#1
hostname oldboylinux   临时生效，重启失效
#2
vi /etc/sysconfig/network   主机名
#3
vi /etc/hosts 主机名和ip的映射

#注意：必须要有步骤一，因为要使2,3的修改生效，我们必须要重启服务器，而我们并不希望重启服务器，所以要使命令行生效，必须要有步骤1
```



