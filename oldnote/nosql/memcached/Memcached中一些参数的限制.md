---
title: Memcached中一些参数的限制
categories: memcached   
toc: true  
tags: [memcached]
---




# 1.Key的长度
```
#语法: 
add key flag expire length

#key 250个字节
```

# 2.Value的限制1M
一般都是存储一些文本，1M够了
# 3.内存限制
如果有30G的数据要缓存，一般也不会单实例装30G（不要把鸡蛋放在一个篮子里），一般建议，开启多个实例（可以在不同的机器或同一台机器上的不同端口中启多个memcached）

# 4.启动多个实例
```
#可以启动多个实例（指定不同的端口即可）
[root@originalOS memcached]# ./bin/memcached -p 11212 -u nobody -d
[root@originalOS memcached]# ./bin/memcached -p 11213 -u nobody -d

#查看启动的实例
[root@originalOS memcached]# ps -ef|grep memcached               
nobody     8695      1  0 19:19 ?        00:00:00 ./bin/memcached -m 64 -p 11211 -u nobody -d
nobody     8711      1  1 19:29 ?        00:00:00 ./bin/memcached -p 11212 -u nobody -d
nobody     8721      1  1 19:29 ?        00:00:00 ./bin/memcached -p 11213 -u nobody -d
root       8729   1022  3 19:29 pts/0    00:00:00 grep memcached

```
 



