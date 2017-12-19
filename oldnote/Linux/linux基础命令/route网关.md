---
title: Linux基础命令之route网关
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


#1.命令行配置路由
```
'添加'
add    
-host        #主机路由
-net        #网关路由

route add    -net 10.0.0.0/8    gw 192.168.10.1        #到达10.0.0.0/8，走网关：192.168.10.1    
route   add default   gw 192.168.0.1      #设置默认路由

'删除路由'
 del    
-host        
-net
route    del -net 10.0.0.0/8
route    del  -net 0.0.0.0        #删除默认路由

'显示'
-n    
route -n             #以数字显示主机和网关


'在指定设备上添加'
route add -net 224.0.0.0 netmask 240.0.0.0 dev eth0

route add -net 224.0.0.0/8 gw 192.168.0.1 dev eth0


```



