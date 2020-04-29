---
title: redis的list结构
categories: redis   
toc: true  
tags: [redis]
---



# lpush/rpush
RPUSH key value [value ...]         #从右侧 插入数据
LPUSH key value [value ...]            #从左侧插入数据
```
127.0.0.1:6379> lpush str_list "aa" "bb"
(integer) 2
127.0.0.1:6379> lrange str_list 0 11
1) "bb"
2) "aa"
127.0.0.1:6379> rpush str_list "r_cc"
(integer) 3
127.0.0.1:6379> lrange str_list 0 11
1) "bb"
2) "aa"
3) "r_cc"
127.0.0.1:6379> rpush str_list "r_dd"
(integer) 4
127.0.0.1:6379> lrange str_list 0 11
1) "bb"
2) "aa"
3) "r_cc"
4) "r_dd"
127.0.0.1:6379> lpush str_list "r_ee"
(integer) 5
127.0.0.1:6379> lrange str_list 0 11
1) "r_ee"
2) "bb"
3) "aa"
4) "r_cc"
5) "r_dd"
127.0.0.1:6379> 
```
# lrange
LRANGE key start stop
0表示第一个元素，1表示第二个元素。。。。
-1表示最后一个元素，-2表示倒数第二个元素。。。。。。。
```
redis> RPUSH mylist "one"
(integer) 1
redis> RPUSH mylist "two"
(integer) 2
redis> RPUSH mylist "three"
(integer) 3
redis> LRANGE mylist 0 0
1) "one"
redis> LRANGE mylist -3 2
1) "one"
2) "two"
3) "three"
redis> LRANGE mylist -100 100
1) "one"
2) "two"
3) "three"
redis> LRANGE mylist 5 10
(empty list or set)
redis>

'取所有的元素，0表示第一个，-1表示最后一个'
127.0.0.1:6379> lrange str_list 0 -1
 
```

# rpop/lpop
RPOP key
删除link中的元素

```
redis> RPUSH mylist "one"
(integer) 1
redis> RPUSH mylist "two"
(integer) 2
redis> RPUSH mylist "three"
(integer) 3
redis> RPOP mylist                #从最右侧删除
"three"
redis> LRANGE mylist 0 -1        #查询所有
1) "one"
2) "two"
redis> 
```

# lrem
LREM key count value
count > 0:从开头到结尾移除值为value的元素count
count < 0: 从结尾到开头移除值为value的元素count
count = 0: 移除所有的value元素

```
redis> RPUSH mylist "hello"
(integer) 1
redis> RPUSH mylist "hello"
(integer) 2
redis> RPUSH mylist "foo"
(integer) 3
redis> RPUSH mylist "hello"
(integer) 4
redis> LREM mylist -2 "hello"        #从结尾开始移除，移除两个
(integer) 2
redis> LRANGE mylist 0 -1
1) "hello"
2) "foo"
redis> 
```


# ltrim
LTRIM key start stop
剪切key对应的链接，切【start , stop】一段，并将该段重新赋值给key

```
redis> RPUSH mylist "one"
(integer) 1
redis> RPUSH mylist "two"
(integer) 2
redis> RPUSH mylist "three"
(integer) 3
redis> LTRIM mylist 1 -1            #从第二个元素开始截取，到最后一个元素
OK
redis> LRANGE mylist 0 -1
1) "two"
2) "three"
redis>
```


# lindex
LINDEX key index
取某个索引的值

```
redis> LPUSH mylist "World"
(integer) 1
redis> LPUSH mylist "Hello"
(integer) 2
redis> LINDEX mylist 0                 #取第一个索引的值
"Hello"        
redis> LINDEX mylist -1                #取最后一个索引的值
"World"
redis> LINDEX mylist 3            #如果超出range，返回nil
(nil)
redis> 

```

# llen
返回链表的元素个数
```
redis> LPUSH mylist "World"
(integer) 1
redis> LPUSH mylist "Hello"
(integer) 2
redis> LLEN mylist
(integer) 2
redis> 
```


# linsert
LINSERT key BEFORE|AFTER pivot value
在指定的值前/后添加

```
redis> RPUSH mylist "Hello"
(integer) 1
redis> RPUSH mylist "World"
(integer) 2
redis> LINSERT mylist BEFORE "World" "There"        #在“World” 前面插入“There”
(integer) 3
redis> LRANGE mylist 0 -1
1) "Hello"
2) "There"
3) "World"
redis> 

'在 a b c d a e d  中的链表中，在a后面插入f'
'则：a f b c d a e d '  #只会在找到的第一个元素后插入，不会插入多个
```


# rpoplpush
RPOPLPUSH source destination
source右侧弹出元素，同时在destination 左侧添加

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/list/1.png)



场景：task+bak双向链表完成安全队列

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/list/2.png)
 
业务逻辑：
1. rpoplpush task bak
2. 接受返回值，并做业务处理
3. 如果业务处理成功，那么rpop bak 清除任务，如果不成功下次从bak表中执行任务（因为该任务没有执行成功）
 

```
'
source = [a,b,c]
destination=[ x,y,z]
RPOPLPUSH source destination            # results in source holding a,b and destination holding c,x,y,z.
source = [a,b]
destination=[ c,x,y,z]

'

redis> RPUSH mylist "one"
(integer) 1
redis> RPUSH mylist "two"
(integer) 2
redis> RPUSH mylist "three"
(integer) 3
redis> RPOPLPUSH mylist myotherlist
"three"
redis> LRANGE mylist 0 -1
1) "one"
2) "two"
redis> LRANGE myotherlist 0 -1
1) "three"
redis>
```


# brpop/blpop
BRPOP key [key ...] timeout
brpop其实和rpop类似，只是当链表中没有元素的时候，他会等待timeout(指定的时间) ，0表示一直等待

```
127.0.0.1:6379> brpop waitstr 0
1) "waitstr"                                        #从key
2) "aaa"                                             #pop出的元素
(27.33s)

```


