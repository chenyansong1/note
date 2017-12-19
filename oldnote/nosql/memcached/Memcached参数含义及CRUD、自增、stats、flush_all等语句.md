---
title: Memcached参数含义及CRUD、自增、stats、flush_all等语句
categories: memcached   
toc: true  
tags: [memcached]
---




# 1.参数含义
```
add   <key>        <flag>         <expires>        <byte>
```
* key 每个缓存有一个独特的名字和存储空间. <font color=red>key是操作数据的唯一标识</font>，key可以250个字节以内,(不能有空格和控制字符) 注:在新版开发计划中提到key可能会扩充到65535个字节
* flag 用来表示存入的是字符串、数组、还是对象等
* expires 缓存的有效期，一种方式是秒数，另一种是使用Unix时间戳，0为有效期无限
* byte 保留值的字节数


# 2.参数expires 举例
```
#设置秒数，从设定开始数第N秒后失效
set web 0 10 5                #设置key=web，然后10s之后数据失效，存储数据的大小为5个字节
zixue

#时间戳，到指定的时间戳后失效，比如在团购网站缓存的某团到中午12:00失效
set web2 0 1379209940 5                        #1379209940 就是一个时间戳（就是某一个时间点）
abcde


#设置为0，表示不自动失效
set web3 0 0 5
abcde

##有种误会，设为0，永久有效，是错误的
#1.编译memcached时，指定一个最长的有效期，默认是30天，所以即使设置为0,30天后也会失效
#2.可能等不到30天，就会被新数据挤出去
#3.memcached重启）

```

# 3.增删改查语句
```
#增加（add)
add class 00 0 4
erqi
STORED

add class 0 0 4
eri9
NOT_STORED                     #为何会添加失败？因为在add只能是新增一个内存中没有的key，如果想要重复的对一个key进行操作，只能用replace



#删除（delete）
add class 00 0 4
erqi
STORED

delete class
DELETED

#delete key [time seconds]      #加秒之后，是指被删除的key，N秒内不能再用，目的是让网站上的页面缓存也代谢完毕

#替换（replace改）
get class
VALUE class 0 4
eqi3
END

replace class 0 0 4                        #重新设置
eri8
STORED

get class
VALUE class 0 4
eri8
END


#replace只能修改存在的key
get abd
END

replace abd 0 0 4                                #因为abd是不存在的，所以用replace是不能修改的
eqi9
NOT_STORED


 

#查找（get）
get class
VALUE class 0 4
eri8
END

#set
#其实set是add和replace的结合，如果有对应的key就修改，如果没有对应的key就添加
set aa 0 0 4                                        #第一次set
eqi5
STORED

get aa
VALUE aa 0 4
eqi5
END

set aa 0 0 4                                #对同样的key，第二次set
eqi0
STORED

get aa
VALUE aa 0 4
eqi0
END

```

# 4.自增、自减操作
```
incr/decr     key      num
```

```
set age 00 0 2
24
STORED

incr age 1                        #增加1
25

get age
VALUE age 0 2
25
END

decr age 1                    #减少1
24

get age
VALUE age 0 2
24
END

#注意：incre/decr操作是将值当做32位无符号来操作的，值的范围：【0 - 2的32次-1】
set num 0 0 1
2
STORED

decr num 1
1

decr num 1                                #因为是无符号的，所以一直减下去，还会是0
0
decr num 1
0
decr num 1
0

get num
VALUE num 0 1
0
END


/**
应用场景：秒杀功能
每个人的抢单主要在内存中操作，速度非常的快，这里的秒杀只是每人发一个订单号，标明你能够购买了，下面就是自己去购买页面下单即可，而秒杀只是负责前面的抢单部分
*/
```


# 5.统计命令
```
stats
STAT pid 8816                                                 #进程号                               
STAT uptime 3054547022                       #服务器自运行以来的秒数
STAT time 234797177                            #当前服务器上的UNIX时间
STAT version 1.4.4-14-g9c660c0               #服务器的版本字符串
STAT pointer_size 64
STAT curr_connections 10                            #当前存在的连接数
STAT total_connections 11                            #历史上的总的连接数
STAT connection_structures 11
STAT cmd_get 21                            #get了多少次
STAT cmd_set 14
STAT cmd_flush 0
STAT get_hits 12                            #get成功返回(关键字获取命中的次数)
STAT get_misses 9                         #get没有取到值    ，这里可以计算出缓存命中率
STAT delete_misses 0
STAT delete_hits 1
STAT incr_misses 0
STAT incr_hits 1
STAT decr_misses 1
STAT decr_hits 5
STAT cas_misses 0
STAT cas_hits 0
STAT cas_badval 0
STAT auth_cmds 0
STAT auth_errors 0
STAT bytes_read 702
STAT bytes_written 669
STAT limit_maxbytes 20971520
STAT accepting_conns 1
STAT listen_disabled_num 0
STAT threads 4
STAT conn_yields 0
STAT bytes 437
STAT curr_items 6                                                            #当前存在的key的数量
STAT total_items 9                                                            #历史上存在的总的数量
STAT evictions 0
END

```

# 6.flush_all
>将删除所有的数据，但是这里是惰性删除的
全删: flush_all [time] time参数是指是所有缓存失效,并在time秒内限制使用删除的key









