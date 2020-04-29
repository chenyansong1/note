---
title: redis setbit(bitmaps)的妙用实现节省存储，快速查询，操作，统计(转)
categories: redis   
toc: true  
tags: [redis]
---


redis提供了二进制的操作，分别是：
```
 
setbit
 
getbit
 
bitcount
 
bitop
 
```
看完具体的使用后，可以衍生出两个经典案例


# 1.存储布尔性质的值，节约内存，快速实现业务
如下需求，在用户系统中(user),ID往往是唯一且递增生成的，如果用redis实现一套用户系统，则可以这样
```
set user:1:name "begin man"     # 注意空格必须加引号以区分
set user:1:age 24
set user:1:sex 1    # 性别 1表示男，0表示女

```

这样如果有1w个用户则对应1w个user:ID:sex键值对，如果我们想要节省内存，何不尝试用bit呢，如下面二进制，2个字节(16位二进制组成)，其中是1的二进制位共9个

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/bitmap/1.png)

那么我们可以用user_id对应每个二进制位，这里设置键名为”users:sex”

比如我们总共有1亿个用户，userID从1到一亿，当然数字不一定是连贯性的，那么userID为0则表示offset(偏移量)为0， userID为15则表示offset为15,(如果你的uid不是从1开始的，比如从100000开始，实际上你也可以相应的用uid减去初始值来表示其位数，比如1000000用户对应到bitmap的第一位)如下命令我们创建了该键值：
```
127.0.0.1:6379> SETBIT users:sex 100000000 0
(integer) 0

```
需要注意的是redis的bitmap只支持2^32大小,即使用512M内存。结合你的业务规划好内存的分配。

接下来做个测试，这里初始化userID用户性别都是0，这里看下userID为0和15和1亿的性别

```
127.0.0.1:6379> GETBIT users:sex 0
(integer) 0
127.0.0.1:6379> GETBIT users:sex 15
(integer) 0
127.0.0.1:6379> GETBIT users:sex 100000000
(integer) 0

```

某一天UserID为15的哥们设置了自己的性别为男，则

```
127.0.0.1:6379> setbit users:sex 15 1
(integer) 0
127.0.0.1:6379> GETBIT users:sex 15
(integer) 1

```

 后台管理员为了统计男女比例，则可以：

```
127.0.0.1:6379> BITCOUNT users:sex
(integer) 1
```
哇，亿里才有1个男人啊，赶紧抢啊。。 别看有1亿多数据，其实整个操作耗时基本是毫秒级别的，时间复杂度为O(1).迅速吧。


![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/bitmap/2.png)


想到这里我们还可以扩展常见的需求：
* 对用户标识一条消息的已读未读
* 用户是否某某某等，只要涉及布尔形式的都可以用bitmaps


# 2.日活跃用户
参考了这篇译文: [使用Redis bitmaps进行快速、简单、实时统计](http://blog.csdn.net/gaoyingju/article/details/9671283)
为了统计今日登录的用户数，我们建立了一个bitmap,每一位标识一个用户ID。当某个用户访问我们的网页或执行了某个操作，就在bitmap中把标识此用户的位置为1。


![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/bitmap/3.png)

 
 这个简单的例子中，每次用户登录时会执行一次redis.setbit(daily_active_users, user_id, 1)。将bitmap中对应位置的位置为1，时间复杂度是O(1)。统计bitmap结果显示有今天有9个用户登录。Bitmap的key是daily_active_users，它的值是1011110100100101。

因为日活跃用户每天都变化，所以需要每天创建一个新的bitmap。我们简单地把日期添加到key后面，实现了这个功能。例如，要统计某一天有多少个用户至少听了一个音乐app中的一首歌曲，可以把这个bitmap的redis key设计为play:yyyy-mm-dd-hh。当用户听了一首歌曲，我们只是简单地在bitmap中把标识这个用户的位置为1，时间复杂度是O(1)。

setbit play:yyyy-mm-dd user_id 1  

今天听过歌曲的用户就是key是play:yyyy-mm-dd的bitmap的位图计数。如果要按周或月统计，只要对这周或这个月的所有bitmap求并集，得出新的bitmap，在对它做位图计数。


![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/bitmap/4.png)

 
利用这些bitmap做其它复杂的统计也非常容易。例如，统计11月听过歌曲的高级用户(premium user),只需通过bitop命令进行AND,OR,XOR,NOT操作即可，如下伪代码：

(play:2011-11-01 ∪ play:2011-11-02 ∪...∪play:2011-11-30) ∩ premium:2011-11

下面的表格显示了在1亿2千8百万用户上完成的时间粒度为1天，一周，一个月的用户统计的时间消耗比较。



![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/bitmap/5.png)
