---
title: flushall故障处理和rdb服务器间的迁移
categories: redis   
toc: true  
tags: [redis]
---



# 1.flushall故障处理

```
flushdb    #清空当前库所有键
flushall        #清空所有库所有键
```
如果不小心运行了flushall, 立即 shutdown nosave ,关闭服务器, 然后 手工编辑aof文件, 去掉文件中的 “flushall ”相关行, 然后开启服务器,就可以导入回原来数据. 
如果,flushall之后,系统恰好bgrewriteaof了,那么aof就清空了,数据丢失.
 
# 2.rdb服务器间的迁移

将rdb文件拿到，然后在配置文件中指定rdb的路径，redis会自动读入rdb文件，实现迁移
