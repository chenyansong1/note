---
title: spark性能优化之二十四之算子调优之MapPartitions提升Map操作性能
categories: spark  
tags: [spark]
---



spark中，最基本的原则，就是每个task处理一个RDD的partition

<!--more-->


MapPartitions操作的优点：

如果是普通的map，比如一个partition中有1万条数据；ok，那么你的function要执行和计算1万次,但是，使用MapPartitions操作之后，一个task仅仅会执行一次function，function一次接收所有的partition数据。只要执行一次就可以了，性能比较高。




MapPartitions的缺点：一定是有的。

如果是普通的map操作，一次function的执行就处理一条数据；那么如果内存不够用的情况下，比如处理了1千条数据了，那么这个时候内存不够了，那么就可以将已经处理完的1千条数据从内存里面垃圾回收掉，或者用其他方法，腾出空间来吧。所以说普通的map操作通常不会导致内存的OOM异常。但是MapPartitions操作，对于大量数据来说，比如甚至一个partition，100万数据，一次传入一个function以后，那么可能一下子内存不够，但是又没有办法去腾出内存空间来，可能就OOM，内存溢出。


什么时候比较适合用MapPartitions系列操作，就是说，数据量不是特别大的时候，都可以用这种MapPartitions系列操作，性能还是非常不错的，是有提升的。比如原来是15分钟，（曾经有一次性能调优），12分钟。10分钟->9分钟。但是也有过出问题的经验，MapPartitions只要一用，直接OOM，内存溢出，崩溃。在项目中，自己先去估算一下RDD的数据量，以及每个partition的量，还有自己分配给每个executor的内存资源。看看一下子内存容纳所有的partition数据，行不行。如果行，可以试一下，能跑通就好。性能肯定是有提升的。但是试了一下以后，发现，不行，OOM了，那就放弃吧。


