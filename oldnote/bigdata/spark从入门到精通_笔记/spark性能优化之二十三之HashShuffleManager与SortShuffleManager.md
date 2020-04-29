---
title: spark性能优化之二十三之HashShuffleManager与SortShuffleManager
categories: spark  
tags: [spark]
---


几种spark shuffle manager

```
spark.shuffle.manager：hash、sort、tungsten-sort（自己实现内存管理）
2.0.x之后就没有了这个配置参数
```

在spark1.2.x之前shuffle Manager是HashShuffleManager；这种manager，实际上，从spark 1.2.x版本以后，就不再是默认的选择了。

<!--more-->




spark 1.2.x版本以后，默认的shuffle manager，是什么呢？SortShuffleManager。


下面是SortShuffleManager的原理图


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_shuffle_sort.png)



和shuffle manager相关的另外一个参数
```
spark.shuffle.sort.bypassMergeThreshold：200
```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_shuffle_sort2.png)



自己可以设定一个阈值，默认是200，当reduce task数量少于等于200；map task创建的输出文件小于等于200的；最后会将所有的输出文件合并为一份文件。

这样做的好处，就是避免了sort排序，节省了性能开销。而且还能将多个reduce task的文件合并成一份文件。节省了reduce task拉取数据的时候的磁盘IO的开销。




在spark 1.5.x以后，对于shuffle manager又出来了一种新的manager，tungsten-sort（钨丝），钨丝sort shuffle manager。官网上说，钨丝sort shuffle manager，效果跟sort shuffle manager是差不多的。

但是，唯一的不同之处在于，钨丝manager，是使用了自己实现的一套内存管理机制，性能上有很大的提升， 而且可以避免shuffle过程中产生的大量的OOM，GC，等等内存相关的异常。



总结:

对于hash、sort、tungsten-sort。如何来选择？

1、需不需要数据默认就让spark给你进行排序？就好像mapreduce，默认就是有按照key的排序。如果不需要的话，其实还是建议搭建就使用最基本的HashShuffleManager，因为最开始就是考虑的是不排序，换取高性能；

2、什么时候需要用sort shuffle manager？如果你需要你的那些数据按key排序了，那么就选择这种吧，而且要注意，reduce task的数量应该是超过200的，这样sort、merge（多个文件合并成一个）的机制，才能生效把。但是这里要注意，你一定要自己考量一下，有没有必要在shuffle的过程中，就做这个事情，毕竟对性能是有影响的。

3、如果你不需要排序，而且你希望你的每个task输出的文件最终是会合并成一份的，你自己认为可以减少性能开销；可以去调节bypassMergeThreshold这个阈值，比如你的reduce task数量是500，默认阈值是200，所以默认还是会进行sort和直接merge的；可以将阈值调节成550，不会进行sort，按照hash的做法，每个reduce task创建一份输出文件，最后合并成一份文件。（一定要提醒大家，这个参数，其实我们通常不会在生产环境里去使用，也没有经过验证说，这样的方式，到底有多少性能的提升）

4、如果你想选用sort based shuffle manager，而且你们公司的spark版本比较高，是1.5.x版本的，那么可以考虑去尝试使用tungsten-sort shuffle manager。看看性能的提升与稳定性怎么样。


```
spark.shuffle.manager：hash、sort、tungsten-sort

new SparkConf().set("spark.shuffle.manager", "hash")
new SparkConf().set("spark.shuffle.manager", "tungsten-sort")

// 默认就是，new SparkConf().set("spark.shuffle.manager", "sort")
new SparkConf().set("spark.shuffle.sort.bypassMergeThreshold", "550")


```


