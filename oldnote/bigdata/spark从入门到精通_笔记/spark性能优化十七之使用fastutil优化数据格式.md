---
title: spark性能优化十七之使用fastutil优化数据格式
categories: spark  
tags: [spark]
---



# fastutil介绍

fastutil是扩展了Java标准集合框架（Map、List、Set；HashMap、ArrayList、HashSet）的类库，提供了特殊类型的map、set、list和queue；

<!--more-->

fastutil能够提供更小的内存占用，更快的存取速度；我们使用fastutil提供的集合类，来替代自己平时使用的JDK的原生的Map、List、Set，好处在于，fastutil集合类，可以减小内存的占用，并且在进行集合的遍历、根据索引（或者key）获取元素的值和设置元素的值的时候，提供更快的存取速度；

fastutil也提供了64位的array、set和list，以及高性能快速的，以及实用的IO类，来处理二进制和文本类型的文件；

fastutil最新版本要求Java 7以及以上版本；

fastutil的每一种集合类型，都实现了对应的Java中的标准接口（比如fastutil的map，实现了Java的Map接口），因此可以直接放入已有系统的任何代码中。
fastutil还提供了一些JDK标准类库中没有的额外功能（比如双向迭代器）。

fastutil除了对象和原始类型为元素的集合，fastutil也提供引用类型的支持，但是对引用类型是使用等于号（=）进行比较的，而不是equals()方法。

fastutil尽量提供了在任何场景下都是速度最快的集合类库。

# Spark中应用fastutil的场景

1、如果算子函数使用了外部变量
第一，你可以使用Broadcast广播变量优化；
第二，可以使用Kryo序列化类库，提升序列化性能和效率；
第三，如果外部变量是某种比较大的集合，那么可以考虑使用fastutil改写外部变量，首先从源头上就减少内存的占用，通过广播变量进一步减少内存占用，再通过Kryo序列化类库进一步减少内存占用。

2、在你的算子函数里，也就是task要执行的计算逻辑里面，如果有逻辑中，出现，要创建比较大的Map、List等集合，可能会占用较大的内存空间，而且可能涉及到消耗性能的遍历、存取等集合操作；那么此时，可以考虑将这些集合类型使用fastutil类库重写，使用了fastutil集合类以后，就可以在一定程度上，减少task创建出来的集合类型的内存占用。避免executor内存频繁占满，频繁唤起GC，导致性能下降。


# 关于fastutil调优的说明

fastutil其实没有你想象中的那么强大，也不会跟官网上说的效果那么一鸣惊人。广播变量、Kryo序列化类库、fastutil，都是之前所说的，对于性能来说，类似于一种调味品，烤鸡，本来就很好吃了，然后加了一点特质的孜然麻辣粉调料，就更加好吃了一点。分配资源、并行度、RDD架构与持久化，这三个就是烤鸡；broadcast、kryo、fastutil，类似于调料。

比如说，你的spark作业，经过之前一些调优以后，大概30分钟运行完，现在加上broadcast、kryo、fastutil，也许就是优化到29分钟运行完、或者更好一点，也许就是28分钟、25分钟。

shuffle调优，15分钟；groupByKey用reduceByKey改写，执行本地聚合，也许10分钟；跟公司申请更多的资源，比如资源更大的YARN队列，1分钟。


# fastutil的使用


第一步：在pom.xml中引用fastutil的包
```
<dependency>
    <groupId>fastutil</groupId>
    <artifactId>fastutil</artifactId>
    <version>5.0.9</version>
</dependency>

```

速度比较慢，可能是从国外的网去拉取jar包，可能要等待5分钟，甚至几十分钟，不等

List<Integer> => IntList

基本都是类似于IntList的格式，前缀就是集合的元素类型；特殊的就是Map，Int2IntMap，代表了key-value映射的元素类型。除此之外，刚才也看到了，还支持object、reference。
