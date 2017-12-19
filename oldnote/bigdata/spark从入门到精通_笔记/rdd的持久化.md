---
title: rdd的持久化
categories: spark   
toc: true  
tag: [spark]
---


为什么要有rdd的持久化?

不进行持久化的情况
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/rdd_persisit.png)


有持久化的情况
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/rdd_persisit2.png)



RDD持久化的原理
spark非常重要的一个功能特性就是可以将RDD持久化到内存中,当对RDD执行持久化操作时,每个节点都会将自己操作的RDD持久化到内存中,并且在之后对该RDD额反复使用中,直接使用内存缓存的partition,这样的话,对于针对一个RDD反复执行多个操作的场景,就只要对RDD计算一次即可,后面直接使用该RDD,而不需要反复计算多次该RDD

要持久化一个RDD,只要调用其cache()或者persist()方法即可,在该RDD第一次被计算出来时,就会直接缓存在每个节点中,而且spark的持久化机制还是自动容错的,如果持久化的RDD的任何partition丢失了,那么spark会自动通过其源RDD,使用Transformation操作重新计算该partition

cache()和persist()的区别在于,cache()是persist()的一种简化方式,cache()的底层就是调用的persist()的无参版本,同时就是调用persist(MEMORY_ONLY),将数据持久化到内存中,如果需要从内存中清除缓存,那么可以使用unpersist()方法

spark自己在也会在shuffle操作时,进行数据的持久化,比如写入磁盘,主要是为了在节点失败时,避免要重复计算整个过程


RDD持久化实例
```
    val linesRdd = sc.textFile("C:\\Users\\Administrator\\Desktop\\xx.txt").cache
    val beginTime = System.currentTimeMillis()
    val count = linesRdd.count()
    val endTime = System.currentTimeMillis()
    println("cost: " + count +":time" + (endTime-beginTime))


    val beginTime2 = System.currentTimeMillis()
    val count2 = linesRdd.count()
    val endTime2 = System.currentTimeMillis()

    println("cost2: " + count2 +":time"  + (endTime2-beginTime2))

/*
可以连续两次执行上面的程序,然后观察结果,可以看出有cache和注释掉.cache的时间差是很明显的
*/

```
cache或者persist的使用规则
1.必须在Transformation或者textFile等创建了一个RDD之后,直接连续调用cache或persist才可以
2.如果你先创建了一个rdd,然后单独另起一行执行cache或者persist方法,是没有用的,而且会报错(大量文件会丢失)



如何选择RDD的持久化策略?
spark提供的多种持久化级别,主要是为了在CPU和内存消耗之间进行取舍,下面是一些通用的持久化级别的选择建议:
1.优先选择MEMORY_ONLY,如果可以缓存所有的数据的话,那么就使用这种策略,因为纯内存速度最快,而且没有序列化,不需要消耗CPU进行反序列化操作
2.如果MEMORY_ONLY策略,无法存储的下所有的数据,那么使用MEMORY_ONLY_SER,将数据进行序列徐进行存储,纯内存操作还是非常快,只是消耗CPU进行反序列化
3.如果需要进行快速的失效恢复的话,那么就选择带后缀为_2的策略,进行数据的备份,这样在失败时,就不需要重新计算了
4.能不使用DISK相关的策略,就不使用,有的时候,从磁盘读取数据,还不如重新计算,除非计算你的数据集的方法开销很大，或者他们会过滤大量的数据，就不要溢写到磁盘中。不然,重新进计算一个分区会和从磁盘中读取的速度一样慢。



