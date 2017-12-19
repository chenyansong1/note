---
title: redis的hash结构及命令详解
categories: redis   
toc: true  
tags: [redis]
---



redis的hash结构相当于map的key-value的结构

# hset设置新值
HSET key field value
1. 设置 key 指定的哈希集中指定字段的值。
2. 如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key 关联。
3. 如果字段在哈希集中存在，它将被重写。

```
 redis> HSET myhash field1 "Hello"
(integer) 1
redis> HGET myhash field1
"Hello"
redis> 

```

 
# hget/hgetall 获取值
HGET key field
HGETALL key
 
```
 redis> HSET myhash field1 "foo"
(integer) 1
redis> HGET myhash field1
"foo"
redis> HGET myhash field2
(nil)



redis> HSET myhash field1 "Hello"
(integer) 1
redis> HSET myhash field2 "World"
(integer) 1
redis> HGETALL myhash
1) "field1"
2) "Hello"
3) "field2"
4) "World"
redis>
```



# hmset同时设置多个值
HMSET key field value [field value ...]

```
redis> HMSET myhash field1 "Hello" field2 "World"
OK
redis> HGET myhash field1
"Hello"
redis> HGET myhash field2
"World"
redis> 
```



# 同时获取多个值
HMGET key field [field ...]
 
```
 redis> HSET myhash field1 "Hello"
(integer) 1
redis> HSET myhash field2 "World"
(integer) 1
redis> HMGET myhash field1 field2 nofield
1) "Hello"
2) "World"
3) (nil)
redis> 
```



# 删除hdel
HDEL key field [field ...]
 
```
redis> HSET myhash field1 "foo"
(integer) 1
redis> HDEL myhash field1
(integer) 1
redis> HDEL myhash field2
(integer) 0

 
```



# hlen指定key的长度
HLEN key
 
```
redis> HSET myhash field1 "Hello"
(integer) 1
redis> HSET myhash field2 "World"
(integer) 1
redis> HLEN myhash
(integer) 2
redis> 
 
```

# 判断指定key中是否存在指定的域
HEXISTS key field
1 if the hash contains field.
0 if the hash does not contain field, or key does not exist.
```
redis> HSET myhash field1 "foo"
(integer) 1
redis> HEXISTS myhash field1
(integer) 1
redis> HEXISTS myhash field2
(integer) 0
```


 
# 对指定的域进行增长hincrby
HINCRBY key field increment
 
```
 redis> HSET myhash field 5
(integer) 1
redis> HINCRBY myhash field 1
(integer) 6
redis> HINCRBY myhash field -1
(integer) 5
redis> HINCRBY myhash field -10
(integer) -5
redis>
 
```


# 增长浮点数：hincrbyfloat
HINCRBYFLOAT key field increment
 
```
 redis> HSET mykey field 10.50
(integer) 1
redis> HINCRBYFLOAT mykey field 0.1
"10.60000000000000001"
redis> HINCRBYFLOAT mykey field -5
"5.59999999999999964"
redis> HSET mykey field 5.0e3
(integer) 0
redis> HINCRBYFLOAT mykey field 2.0e2
"5200"
redis>
 
```



# 返回所有的域:hkeys
HKEYS key
 
```
 redis> HSET myhash field1 "Hello"
(integer) 1
redis> HSET myhash field2 "World"
(integer) 1
redis> HKEYS myhash
1) "field1"
2) "field2"
redis> 
```



# 返回所有域对应的值：hvals
HVALS key
 
```
 redis> HSET myhash field1 "Hello"
(integer) 1
redis> HSET myhash field2 "World"
(integer) 1
redis> HVALS myhash
1) "Hello"
2) "World"
redis>
```
