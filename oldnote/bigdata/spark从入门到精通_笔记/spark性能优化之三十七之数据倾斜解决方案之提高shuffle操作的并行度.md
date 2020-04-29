---
title: spark性能优化之三十七之数据倾斜解决方案之提高shuffle操作的并行度
categories: spark  
tags: [spark]
---

提高shuffle操作的并行度原理图

<!--more-->

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_data_skew_reduce_paralism.png)




提升shuffle reduce端并行度，怎么来操作？

很简单，主要给我们所有的shuffle算子，**比如groupByKey、countByKey、reduceByKey。在调用的时候，传入进去一个参数**。一个数字。那个数字，就代表了那个shuffle操作的reduce端的并行度。那么在进行shuffle操作的时候，就会对应着创建指定数量的reduce task。

这样的话，就可以让每个reduce task分配到更少的数据。基本可以缓解数据倾斜的问题。

比如说，原本某个task分配数据特别多，直接OOM，内存溢出了，程序没法运行，直接挂掉。按照log，找到发生数据倾斜的shuffle操作，给它传入一个并行度数字，这样的话，原先那个task分配到的数据，肯定会变少。就至少可以避免OOM的情况，程序至少是可以跑的。


提升shuffle reduce并行度的缺陷

治标不治本，因为，它没有从根本上改变数据倾斜的本质和问题。不像第一个和第二个方案（直接避免了数据倾斜的发生）。原理没有改变，只是说，尽可能地去缓解和减轻shuffle reduce task的数据压力，以及数据倾斜的问题。

实际生产环境中的经验。

1、如果最理想的情况下，提升并行度以后，减轻了数据倾斜的问题，或者甚至可以让数据倾斜的现象忽略不计，那么就最好。就不用做其他的数据倾斜解决方案了。

2、不太理想的情况下，就是比如之前某个task运行特别慢，要5个小时，现在稍微快了一点，变成了4个小时；或者是原先运行到某个task，直接OOM，现在至少不会OOM了，但是那个task运行特别慢，要5个小时才能跑完。

那么，如果出现第二种情况的话，各位，就立即放弃第三种方案(提高shuffle操作的并行度)，开始去尝试和选择后面的四种方案。



