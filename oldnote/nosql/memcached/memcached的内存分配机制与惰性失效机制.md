---
title: memcached的内存分配机制与惰性失效机制
categories: memcached   
toc: true  
tags: [memcached]
---



# 1.内存的碎片化
如果用C语言直接 malloc free来向操作系统申请和释放内存时，在不断的申请和释放过程中，形成了一些很小的内存片段，无法再利用，这就是内存碎片


![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/memcached/LRU/1.png)


                                                    
 
# 2.memcached的内存分配机制 
## 2.1.Slab Allocator分配机制


memcached用slab allocator来管理内存的，<font color=red> 其基本原理：把内存划分成数个slab仓库，各个仓库切分不同的尺寸的小块（chunk)，需要存储内容时，判断内存的大小，为其选取合理的仓库 </font>，但是如果有100byte的内容要存储，但距离100byte最近的122大小的仓库中你的chunk满了，此时并不会寻找更大的chunk，而是将122中不常用的数据踢掉

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/memcached/LRU/2.jpg)

## 2.2.Slab Allocator缓存原理

 memcached根据收到的数据的大小， 选择最适合数据大小的slab。memcached中保存着slab内空闲chunk的列表， 根据该列表选择chunk， 然后将数据缓存于其中。

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/memcached/LRU/3.jpg)

## 2.3.lab Allocator的缺点

chunks为固定大小,造成浪费. 这个问题 不能克服,只能缓解

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/memcached/LRU/4.jpg)

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/memcached/LRU/5.png)
 
## 2.4.grow factor(增长因子)
memcached在启动时指定Growth Factor因子（通过­f选项）， 就可以在某种程度上控制slab之间的差异。默认值为1.25。 但是，在该选项出现之前，这个因子曾经固定为2，称为“powers of 2”策略
```
#如下图：112/88=1.25 也就是增长因子，一般而言，观察缓存数据大小的变化规律，设置合理的生长因子，可以根据自己网站的缓存数据的大小来调整增长因子
```

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/memcached/LRU/6.png)![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/memcached/LRU/7.png)
<br/>

# 3.惰性失效机制（Lazy Expiration）
memcached内部不会监视记录是否过期，而是在get时会查看记录的时间戳（和设定的时间比较），检查记录是否过期。这种技术被称为lazy（惰性）expiration。 因此，<font color=red>memcached不会在过期监视上耗费CPU时间</font>
1. 当某个值过期后，并没有从内存删除，因此stats统计的时候，curr_item有其信息
2. 当某个新值去占用他的位置的时候，当成空chunk来占用
3. 当get值时，判断是否过期，如果过期返回空，并且清空，此时在stats，curr_item就减少了
4. 即：这个过期只是让用户看不到这个数据而已并没有真正删除数据

为什么说节省了CPU？
实际上设置5s之后失效，如果是主动的使数据失效的话，就会在5s的时候去主动的触发数据失效，这样系统本身还要去检查，5s到了吗，哦，没到，那等一下再去检查，系统会一直这样检查，这样就会消耗CPU的时间，但是惰性机制就是，在get的时候，直接取检查，当前时间是否和数据建立的时间之间的时间差，然后，超过了我设定的过期时间间隔，此时才正式的将数据删除，新增数据的时候也是这样去检查，



# 4.memcached的删除机制
Least Recently Used（LRU）——最近最少使用，memcached会优先使用已超时的记录的空间，但即使如此，也会发生追加新记录时空间不 足的情况，此时就要使用名为 Least Recently Used（LRU）机制来分配空间
如果chunk满了，又有新的加入，旧数据会被踢掉。踢掉可以设置两种FIFO（先进先出）、LRU(最近最少使用)。下面是一个LRU的demo:

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/memcached/LRU/8.png)

<font color=green>注意：get、inrc、decr等可以使数据变成最近使用</font>

 


 








