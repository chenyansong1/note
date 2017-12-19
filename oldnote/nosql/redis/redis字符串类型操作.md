---
title: redis字符串类型操作
categories: redis   
toc: true  
tags: [redis]
---



# 1.官网
http://redis.io/commands#string


# 2.常用操作
## 2.1.set
SET key value [EX seconds] | [PX milliseconds] [NX|XX]

* EX seconds -- Set the specified expire time, in seconds.
* PX milliseconds -- Set the specified expire time, in milliseconds.
* NX -- Only set the key if it does not already exist.    key 不存在就设置
* XX -- Only set the key if it already exist            key存在就设置

```
127.0.0.1:6379> set address beijing ex 22
OK
127.0.0.1:6379> ttl address            #ttl会返回离过期的时间还有多少秒
(integer) 6
127.0.0.1:6379> ttl address
(integer) 3
127.0.0.1:6379> ttl address
(integer) 2
127.0.0.1:6379> ttl address
(integer) 2
127.0.0.1:6379> ttl address
(integer) 0
127.0.0.1:6379> ttl address
(integer) -2                                #已经过期


```

## 2.2.mset
MSET key value [key value ...]
一次性设置多个键值

```
127.0.0.1:6379> mset name zhangsan age 55 address guangshui
OK
127.0.0.1:6379> keys *
1) "address"
2) "age"
3) "name"

```


## 2.3.get
GET key
 
```
redis> SET key1 "Hello"
OK
```

## 2.4.mget
MGET key [key ...]
```
127.0.0.1:6379> mget name age address
1) "zhangsan"
2) "55"
3) "guangshui"
127.0.0.1:6379> 
```
## 2.5.setrange
SETRANGE key offset value
将指定的key从offset（从0开始数）的位置替换成value
```
127.0.0.1:6379> set key1 "hello world"
OK
127.0.0.1:6379> setrange key1 6 "redis"
(integer) 11
127.0.0.1:6379> get key1
"hello redis"
127.0.0.1:6379> setrange key2 6 "redis"                #从6的offset开始替换key2
(integer) 11
127.0.0.1:6379> get key2                #因为key2不存在，所以前面使用\x00填充，一共填充6次
"\x00\x00\x00\x00\x00\x00redis"
127.0.0.1:6379> 

127.0.0.1:6379> set key3 "hello world"
OK
127.0.0.1:6379> get key3
"hello world"
127.0.0.1:6379> setrange key3 13 "redis"
(integer) 18
127.0.0.1:6379> get key3    
"hello world\x00\x00redis"                #不存在的位置会用\x00进行填充
127.0.0.1:6379>


 
```


##  2.6.APPEND
APPEND key value
如果key存在并且是一个字符串，则在字符串的末尾添加value
如果key不存在，将会创建一个空的字符串，然后在末尾添加
```
redis> EXISTS mykey
(integer) 0
redis> APPEND mykey "Hello"
(integer) 5
redis> APPEND mykey " World"
(integer) 11
redis> GET mykey
"Hello World"
redis> 


```



## 2.7.getrange
GETRANGE key start end
```
redis> SET mykey "This is a string"
OK
redis> GETRANGE mykey 0 3    
"This"
redis> GETRANGE mykey -3 -1            #从后往前获取：-1表示最后一个字符
"ing"
redis> GETRANGE mykey 0 -1                    #所有
"This is a string"    
redis> GETRANGE mykey 10 100            #100超过str的长度，那么将获取指定的位置后的所有
"string"
redis> 
```


## 2.8.getset
GETSET key value
获取并返回旧值，并设置新值

```
redis> SET mykey "Hello"
OK
redis> GETSET mykey "World"
"Hello"
redis> GET mykey
"World"
redis>

'如果key不存在就返回nil，并在该key上设置新的值' 
127.0.0.1:6379> EXISTS run
(integer) 0
127.0.0.1:6379> getset run "running..."
(nil)
127.0.0.1:6379> get run
"running..."
127.0.0.1:6379> exists run
(integer) 1
127.0.0.1:6379> 


```


## 2.9.ince/dec
INCR key
自增操作
如果key不存在，就创建key=0，然后再自增
如果key的type不是number，那么将会抛出异常

```
redis> SET mykey "10"
OK
redis> INCR mykey
(integer) 11
redis> GET mykey
"11"
redis>


'不存在就创建'
127.0.0.1:6379> exists number
(integer) 0
127.0.0.1:6379> incr number
(integer) 1
127.0.0.1:6379> get number
"1"
127.0.0.1:6379> decr number
(integer) 0
127.0.0.1:6379> decr number
(integer) -1                                    #可以是一个负数的


redis> SET mykey "10"
OK
redis> DECR mykey
(integer) 9
redis> SET mykey "234293482390480948029348230948"
OK
redis> DECR mykey
ERR value is not an integer or out of range
redis> 


```



## 2.10.incrby/decrby
INCRBY key increment

```
redis> SET mykey "10"
OK
redis> INCRBY mykey 5
(integer) 15
redis>

redis> SET mykey "10"
OK
redis> DECRBY mykey 3
(integer) 7
redis>


```



## 2.11.incrbyfloat
INCRBYFLOAT key increment
添加浮点数

```
redis> SET mykey 10.50
OK
redis> INCRBYFLOAT mykey 0.1
"10.6"
redis> INCRBYFLOAT mykey -5
"5.6"
redis> SET mykey 5.0e3
OK
redis> INCRBYFLOAT mykey 2.0e2
"5200"
redis> 
```



## 2.12.setbit
SETBIT key offset value
对某一个位（bit）进行设置

```
'
A         65        0100    0001
                                                +32
---------------------------------------------
a         97        0110    0001
'
127.0.0.1:6379> set char "a"
OK
127.0.0.1:6379> setbit char 2 0                #将第二bit设置为0
(integer) 1
127.0.0.1:6379> get char
"A"

 

redis> SETBIT mykey 7 1
(integer) 0
redis> SETBIT mykey 7 0
(integer) 1
redis> GET mykey
"\u0000"
redis> 


'如果offset过大，则会在中间填充0'
127.0.0.1:6379> get char
"A"
127.0.0.1:6379> setbit char 20 1
(integer) 0
127.0.0.1:6379> get char
"A\x00\b"

```


## 2.13.bitop
BITOP operation destkey key [key ...]
对key1,key2,keyN作operation，并将结果保存到destkey上
operation可以是AND , OR , NOT , XOR 
```
redis> SET key1 "foobar"
OK
redis> SET key2 "abcdef"
OK
redis> BITOP AND dest key1 key2
(integer) 6
redis> GET dest
"`bc`ab"
redis>

```
