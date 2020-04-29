---
title: sentinel运维监控
categories: redis   
toc: true  
tags: [redis]
---


![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/sentinel/monitor_1.png)



# 运行时手动修改master-slave

> 修改一台slave为master

1. 执行slaveof no one #执行该机不是一台slave了
redis> slaveof no one
2. 修改readonly no #因为要转为master，所以要改成可以写的
redis> config set slave-read-only no

> 其他的slave再指向这台机器

redis> slaveof IP PORT
 

# 自动切换

自动切换服务器sentinel.conf文件
```
sentinel monitor def_master 127.0.0.1 6379 2 
 
sentinel auth-pass def_master 012_345^678-90 
 
##master被当前sentinel实例认定为“失效”的间隔时间 
##如果当前sentinel与master直接的通讯中，在指定时间内没有响应或者响应错误代码，那么 
##当前sentinel就认为master失效(SDOWN，“主观”失效) 
##<mastername> <millseconds> 
##默认为30秒 
sentinel down-after-milliseconds def_master 30000 
 
##当前sentinel实例是否允许实施“failover”(故障转移) 
##no表示当前sentinel为“观察者”(只参与"投票".不参与实施failover)， 
##全局中至少有一个为yes 
sentinel can-failover def_master yes 
 
##sentinel notification-script mymaster /var/redis/notify.sh  
```
 

参看:
[redis第九步（sentinel监控主从服务器）](https://segmentfault.com/a/1190000003946562)