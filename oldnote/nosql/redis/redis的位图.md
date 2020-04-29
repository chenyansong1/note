---
title: redis的位图
categories: redis   
toc: true  
tags: [redis]
---



# setbit
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


# getbit
对 key 所储存的字符串值，获取指定偏移量上的位(bit)。 
当 offset 比字符串值的长度大，或者 key 不存在时，返回 0
```
返回值：
字符串值指定偏移量上的位(bit)。
# 对不存在的 key 或者不存在的 offset 进行 GETBIT， 返回 0
 
redis> EXISTS bit
(integer) 0
 
redis> GETBIT bit 10086
(integer) 0
 
 
# 对已存在的 offset 进行 GETBIT
 
redis> SETBIT bit 10086 1
(integer) 0
 
redis> GETBIT bit 10086
(integer) 1
```
 

# bitcount
计算给定字符串中，被设置为 1 的比特位的数量。
一般情况下，给定的整个字符串都会被进行计数，通过指定额外的 start 或 end 参数，可以让计数只在特定的位上进行。
start 和 end 参数的设置和 GETRANGE 命令类似，都可以使用负数值： 比如 -1 表示最后一个字节， -2 表示倒数第二个字节，以此类推。 
不存在的 key 被当成是空字符串来处理，因此对一个不存在的 key 进行 BITCOUNT 操作，结果为 0 。
```
redis> BITCOUNT bits
(integer) 0
 
redis> SETBIT bits 0 1          # 0001
(integer) 0
 
redis> BITCOUNT bits
(integer) 1
 
redis> SETBIT bits 3 1          # 1001
(integer) 0
 
redis> BITCOUNT bits
(integer) 2
```


 
# bitop
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



# 案例
参见：redis setbit(bitmaps)的妙用实现节省存储，快速查询，操作，统计(转)

