---
title: redis的set结构
categories: redis   
toc: true  
tags: [redis]
---



# 官网
http://redis.io/commands#set

# 集合的特点
1. 无序性
2. 唯一性

# sadd 添加
SADD key member [member ...]

```
redis> SADD myset "Hello"
(integer) 1
redis> SADD myset "World"
(integer) 1
redis> SADD myset "World"        #不能重复添加
(integer) 0
redis> SMEMBERS myset
1) "Hello"
2) "World"
redis>
```

# smemberes 查看 
SMEMBERS key
Return value        all elements of the set.

```
redis> SADD myset "Hello"
(integer) 1
redis> SADD myset "World"
(integer) 1
redis> SMEMBERS myset
1) "Hello"
2) "World"
redis>
 
```




# srem 删除
SREM key member [member ...]
Return value：
            the number of members that were removed from the set, not including non existing members
```
 redis> SADD myset "one"
(integer) 1
redis> SADD myset "two"
(integer) 1
redis> SADD myset "three"
(integer) 1
redis> SREM myset "one"
(integer) 1
redis> SREM myset "four"            #没有就返回0
(integer) 0
redis> SMEMBERS myset
1) "three"
2) "two"
redis>
```





# spop 随机删除元素
SPOP key [count]
Removes and returns one or more random elements from the set value store at key.
Return value： the removed element, or nil when key does not exist.
 
```
 redis> SADD myset "one"
(integer) 1
redis> SADD myset "two"
(integer) 1
redis> SADD myset "three"
(integer) 1
redis> SPOP myset            #随机删除一个
"three"
redis> SMEMBERS myset
1) "two"
2) "one"
redis> SADD myset "four"
(integer) 1
redis> SADD myset "five"
(integer) 1
redis> SPOP myset 3        #随机删除3个
1) "five"
2) "four"
3) "two"
redis> SMEMBERS myset
1) "one"
```






# srandommember 随机得到一个或多个元素
SRANDMEMBER key [count]
count>0 返回一个元素不重复的数组
count<0 返回的数组中元素可能重复
如果不指定count，那么随机的得到一个元素
 
```
 redis> SADD myset one two three
(integer) 3
redis> SRANDMEMBER myset
"one"
redis> SRANDMEMBER myset 2
1) "two"
2) "three"
redis> SRANDMEMBER myset -5        #返回了随机的重复元素
1) "two"
2) "three"
3) "one"
4) "two"
5) "one"
redis> 
```



# sismember 是否在集合中
SISMEMBER key member
存在返回1，否返回0 
```
 redis> SADD myset "one"
(integer) 1
redis> SISMEMBER myset "one"
(integer) 1
redis> SISMEMBER myset "two"
(integer) 0
redis> 
```


# scard 集合元素的个数
SCARD key
返回集合中元素的个数

```
redis> SADD myset "Hello"
(integer) 1
redis> SADD myset "World"
(integer) 1
redis> SCARD myset
(integer) 2
redis> 
```




# smove 移动集合中的某个元素
SMOVE source destination member
 从源集合中移动元素到目标集合中，这个操作是原子性的
如果源集合不存在或者是源集合中没有指定的元素，什么也不操作，返回0
如果元素在目标集合中存在，那么只是删除源集合中的元素，目标集合不动

```
redis> SADD myset "one"
(integer) 1
redis> SADD myset "two"
(integer) 1
redis> SADD myotherset "three"
(integer) 1
redis> SMOVE myset myotherset "two"
(integer) 1
redis> SMEMBERS myset
1) "one"
redis> SMEMBERS myotherset
1) "two"
2) "three"
redis> 
 
```


# sinter 求交集
SINTER key [key ...]
如果一个集合的为空，或者改集合不存在（会被当做空集合），那么求的并集为空（因为任何集合和一个空集合求交集都是空集合）
 
```
key1 = {a,b,c,d}
key2 = {c}
key3 = {a,c,e}
SINTER key1 key2 key3 = {c}


redis> SADD key1 "a"
(integer) 1
redis> SADD key1 "b"
(integer) 1
redis> SADD key1 "c"
(integer) 1
redis> SADD key2 "c"
(integer) 1
redis> SADD key2 "d"
(integer) 1
redis> SADD key2 "e"
(integer) 1
redis> SINTER key1 key2
1) "c"
redis>
 
```



# sunion 求并集
SUNION key [key ...]
 return : list with members of the resulting set.
```
 key1 = {a,b,c,d}
key2 = {c}
key3 = {a,c,e}
SUNION key1 key2 key3 = {a,b,c,d,e}

'Keys that do not exist are considered to be empty sets.'


redis> SADD key1 "a"
(integer) 1
redis> SADD key1 "b"
(integer) 1
redis> SADD key1 "c"
(integer) 1
redis> SADD key2 "c"
(integer) 1
redis> SADD key2 "d"
(integer) 1
redis> SADD key2 "e"
(integer) 1
redis> SUNION key1 key2
1) "b"
2) "a"
3) "c"
4) "e"
5) "d"
redis>
 
```



# sdiff  求集合的差
SDIFF key [key ...]
返回：list with members of the resulting set.
 
```
 key1 = {a,b,c,d}
key2 = {c}
key3 = {a,c,e}
SDIFF key1 key2 key3 = {b,d}


redis> SADD key1 "a"
(integer) 1
redis> SADD key1 "b"
(integer) 1
redis> SADD key1 "c"
(integer) 1
redis> SADD key2 "c"
(integer) 1
redis> SADD key2 "d"
(integer) 1
redis> SADD key2 "e"
(integer) 1
redis> SDIFF key1 key2
1) "b"
2) "a"
redis>

```


# sinterstore/sunionstore/sdiffstore
SINTERSTORE destination key [key ...]
SUNIONSTORE destination key [key ...]
SDIFFSTORE destination key [key ...]
 只是将结果存储在了destination  中，使用方法见上面

