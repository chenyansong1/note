---
title: redis命令——通用key操作命令
categories: redis   
toc: true  
tags: [redis]
---



# 1.官网
http://redis.io/commands#generic

# 2.实例
```
'连接客户端'
[root@originalOS bin]# redis-cli

' keys * ：查询所有的key 可以使用正则去匹配'
127.0.0.1:6379> keys *                                
1) "site"
127.0.0.1:6379> keys site
1) "site"
127.0.0.1:6379> keys s*                    #*去匹配所有字符
1) "site"
127.0.0.1:6379> keys sit[t|e]            #匹配【】中的字符
1) "site"
127.0.0.1:6379>
127.0.0.1:6379> keys si?e            #？匹配到一个字符
1) "site"



'randomkey：返回随机key'
127.0.0.1:6379> RANDOMKEY
"site"

  
'type'
127.0.0.1:6379> type site
string
127.0.0.1:6379> type ss
none

'exists：是否存在'
127.0.0.1:6379> EXISTS site
(integer) 1
127.0.0.1:6379> EXISTS aaa
(integer) 0
127.0.0.1:6379> 


'del'
127.0.0.1:6379> del site
(integer) 1
127.0.0.1:6379> EXISTS site
(integer) 0

'rename key'
127.0.0.1:6379> get name
"zhangsan"
127.0.0.1:6379> rename name newname
OK
127.0.0.1:6379> get newname
"zhangsan"
127.0.0.1:6379> 
 

'renamenx:如果修改后的名字已经存在，不会执行'
127.0.0.1:6379> KEYS *
1) "newname"
127.0.0.1:6379> set name lisi
OK
127.0.0.1:6379> renamenx newname name
(integer) 0                            #修改失败
127.0.0.1:6379> 

'rename newname name      #将会直接覆盖原来的名字'



'select：选择进入的数据库（数据库从0到n）'
127.0.0.1:6379> SELECT 1
OK
127.0.0.1:6379[1]> keys *
(empty list or set)
127.0.0.1:6379[1]> select 0
OK
127.0.0.1:6379> keys *
1) "name"
2) "newname"

'move：移动某一个key到指定的数据库'
127.0.0.1:6379> move name 1
(integer) 1
127.0.0.1:6379> select 1
OK
127.0.0.1:6379[1]> keys *
1) "name"
127.0.0.1:6379[1]>



'ttl：查看key的生命周期'
127.0.0.1:6379> keys *
1) "newname"
127.0.0.1:6379> ttl newname                
(integer) -1                        #不过期的key返回-1

127.0.0.1:6379> ttl cc
(integer) -2                        #不存在的key
127.0.0.1:6379> 


'expire：设置key的生命周期' 
127.0.0.1:6379> expire newname 10        #设置10s的生命周期
(integer) 1
127.0.0.1:6379> get newname
(nil)


'pexpire：设置毫秒失效'
127.0.0.1:6379> pexpire serache 9000
(integer) 1
127.0.0.1:6379> pttl serache


'persist：设置永久有效设置'
 127.0.0.1:6379> set age 11
OK
127.0.0.1:6379> expire age 22                #设置22s后失效
(integer) 1
127.0.0.1:6379> PERSIST age                #设置永久有效
(integer) 1
127.0.0.1:6379> 
127.0.0.1:6379> ttl age                #查看生命周期
(integer) -1                            #永久有效
127.0.0.1:6379> 



```



