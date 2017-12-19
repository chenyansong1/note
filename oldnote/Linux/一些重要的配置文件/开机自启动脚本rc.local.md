---
title: Linux一些重要的配置文件之开机自启动脚本/etc/rc.local
categories: Linux   
toc: true  
tags: [Linux重要配置文件]
---



在所有的初始化脚本执行完成之后,会执行该脚本文件,一般放一些启动的命令,挂载命令等

```
[root@lamp01 ~]# cat /etc/rc.local
#!/bin/sh
#
# This script will be executed *after* all the other init scripts.
# You can put your own initialization stuff in here if you don't
# want to do the full Sys V style init stuff.
 
touch /var/lock/subsys/local
 
#### NFS ######
/etc/init.d/rpcbind start        #启动rpcbind服务
mount -t nfs 192.168.1.102:/data /mnt        #挂载分区


```


