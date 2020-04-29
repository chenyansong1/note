---
title: Linux基础命令之关闭SELinux
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


&emsp;SELinux(Security-Enhanced Linux)是美国国家安全局对于强制访问控制的实现,这个功能让系统管理员又爱又恨,这里考虑还是将其关闭了,至于安全问题,后面通过其他手段来解决

```
[root@lamp01 chenyansong]# cat /etc/selinux/config
 
# This file controls the state of SELinux on the system.
# SELINUX= can take one of these three values:
#     enforcing - SELinux security policy is enforced.
#     permissive - SELinux prints warnings instead of enforcing.
#     disabled - No SELinux policy is loaded.
SELINUX=disabled
# SELINUXTYPE= can take one of these two values:
#     targeted - Targeted processes are protected,
#     mls - Multi Level Security protection.
SELINUXTYPE=targeted

#SELinux有几种模式: enforcing , permissive , disabled 
#我们需要将其改为disabled 


```

使之生效
``` 
[root@lamp01 chenyansong]# getenforce
Disabled
[root@lamp01 chenyansong]# setenforce 0
setenforce: SELinux is disabled
[root@lamp01 chenyansong]# getenforce 
Disabled
[root@lamp01 chenyansong]#
```