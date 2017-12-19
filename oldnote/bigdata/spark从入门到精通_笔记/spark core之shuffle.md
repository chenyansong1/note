---
title: spark core之shuffle
categories: spark   
toc: true  
tag: [spark]
---


# shuffle原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/shuffle原理.png)

<!--more-->

# shuffle操作过程中进行数据排序
对每个分区中的数据进行排序,那么可以使用下面3种方式
1.使用mapPartitions算子处理每个partition,对每个partition中的数据进行排序(推荐)
2.使用reparationAndSortWithinPartitions,对Rdd进行重新分区,在重分区的过程中同时就进行分区内数据的排序
3.使用sortByKey对数据进行全局的排序


```
//下面是wordcount的部分代码

val rdd2 = rdd1.reduceBykey(_+_)
rdd2.mapPartitions{
  ite=>
    ite.toList.sortBy(_._2).toIterator//在分区内进行排序
}


```


# 触发shuffle操作的算子
1.reparation类的操作:比如:repartition,repartitionAndSortWithinPartitions,coalesce等
2.ByKey类的操作,比如:reduceByKey,groupByKey,sortByKey等
3.join类的操作,比如:join,cogroup等


# shuffle操作对性能消耗的原理讲解

shuffle操作是spark中唯一最消耗性能的地方,因此也就成立最需要进行性能调优的地方,最需要解决线上报错的地方,也是唯一可能出现数据倾斜的地方,因为shuffle过程中,会产生大量的磁盘IO,数据序列化和反序列化,网络IO等

为了实时shuffle操作,spark中才有了stage的概念,在发生shuffle操作的算子中,进行stage的拆分,shuffle操作的前半部分,是上一个stage来进行的,也称之为map task,shuffle操作的后半部分是下一个stage来进行的,也称之为reduce task,其中map task负责数据的组织,也就是将同一个key对应的value都写入同一个下游的task对应的分区文件汇总,其中reduce task负责数据的聚合,也就是将上一个stage的task所在的节点上,将属于自己的各个分区文件,都拉取过来聚合,这种类型,是参考和模拟了MapReduce的shuffle过程来的

map task会将数据先保存在本地内存中,如果内存不够时,就溢写到磁盘文件中去,reduce task会读取各个节点上属于自己的分区磁盘文件,到自己节点的内存中,并进行聚合

shuffle操作会消耗大量的内存,因为无论是网络传输数据之前,还是之后,都会使用大量的内存中的数据结构来进行聚合操作,比如reduceByKey和aggregateByKey操作,会在map side使用内存中的数据结构进行预先聚合,其他的ByKey类的操作,都是在reduce side,使用内存数据结构进行聚合,在聚合的过程中,如果内存不够,只能溢写到磁盘文件中去,此时就会发生大量的磁盘IO,降低性能

此外,shuffle过程中,还会产生大量的中间文件,也就是map side写入的大量分区文件,比如spark1.3版本中,这些中间文件会一直保留着,直到Rdd不再被使用,而且被垃圾回收掉了,才会去清理中间文件,但是这种情况下,如果我们的应用程序中,一直保留着对RDD的引用,导致很长时间以后才会进行RDD的垃圾回收操作,保存中间文件的目录,由spark.local.dir属性指定




# shuffle操作所有的相关参数以及性能调优



我们可以通过对一系列的参数进行调优，来优化shuffle的性能


```

spark 1.5.2版本

属性名称										默认值			属性说明
spark.reducer.maxSizeInFlight					48m				reduce task的buffer缓冲，代表了每个reduce task每次能够拉取的map side数据最大大小，如果内存充足，可以考虑加大大小，从而减少网络传输次数，提升性能
spark.shuffle.blockTransferService				netty			shuffle过程中，传输数据的方式，两种选项，netty或nio，spark 1.2开始，默认就是netty，比较简单而且性能较高，spark 1.5开始nio就是过期的了，而且spark 1.6中去除掉了
spark.shuffle.compress							true			是否对map side输出的文件进行压缩，默认是启用压缩的，压缩器是由spark.io.compression.codec属性指定的，默认是snappy压缩器，该压缩器强调的是压缩速度，而不是压缩率
spark.shuffle.consolidateFiles					false			默认为false，如果设置为true，那么就会合并map side输出文件，对于reduce task数量特别的情况下，可以极大减少磁盘IO开销，提升性能
spark.shuffle.file.buffer						32k				map side task的内存buffer大小，写数据到磁盘文件之前，会先保存在缓冲中，如果内存充足，可以适当加大大小，从而减少map side磁盘IO次数，提升性能
spark.shuffle.io.maxRetries						3				网络传输数据过程中(reduce side拉取数据的过程)，如果出现了网络IO异常，重试拉取数据的次数，默认是3次，对于耗时的shuffle操作，建议加大次数，以避免full gc或者网络不通常导致的数据拉取失败，进而导致task lost，增加shuffle操作的稳定性
spark.shuffle.io.retryWait						5s				每次重试拉取数据的等待间隔，默认是5s，建议加大时长，理由同上，保证shuffle操作的稳定性
spark.shuffle.io.numConnectionsPerPeer			1				机器之间的可以重用的网络连接，主要用于在大型集群中减小网络连接的建立开销，如果一个集群的机器并不多，可以考虑增加这个值
spark.shuffle.io.preferDirectBufs				true			启用堆外内存，可以避免shuffle过程的频繁gc，如果堆外内存非常紧张，则可以考虑关闭这个选项
spark.shuffle.manager							sort			ShuffleManager，Spark 1.5以后，有三种可选的，hash、sort和tungsten-sort，sort-based ShuffleManager会更高效实用内存，并且避免产生大量的map side磁盘文件，从Spark 1.2开始就是默认的选项，tungsten-sort与sort类似，但是内存性能更高(如果map side不需要排序,那么可以使用hash,然后配合上面讲解的一个参数在map side进行本地聚合操作也是可以的)
spark.shuffle.memoryFraction					0.2				(reduce side的内存大小)如果spark.shuffle.spill属性为true，那么该选项生效，代表了executor内存中，用于进行shuffle reduce side聚合的内存比例，默认是20%，如果内存充足，建议调高这个比例，给reduce聚合更多内存，避免内存不足频繁读写磁盘
spark.shuffle.service.enabled					false			启用外部shuffle服务，这个服务会安全地保存shuffle过程中，executor写的磁盘文件，因此executor即使挂掉也不要紧，必须配合spark.dynamicAllocation.enabled属性设置为true，才能生效，而且外部shuffle服务必须进行安装和启动，才能启用这个属性
spark.shuffle.service.port						7337			外部shuffle服务的端口号，具体解释同上
spark.shuffle.sort.bypassMergeThreshold			200				对于sort-based ShuffleManager，如果没有进行map side聚合，而且reduce task数量少于这个值，那么就不会进行排序，如果你使用sort ShuffleManager，而且不需要排序，那么可以考虑将这个值加大，直到比你指定的所有task数量都大，以避免进行额外的sort，从而提升性能
spark.shuffle.spill								true			当reduce side的聚合内存使用量超过了spark.shuffle.memoryFraction指定的比例时，就进行磁盘的溢写操作
spark.shuffle.spill.compress					true			同上，进行磁盘溢写时，是否进行文件压缩，使用spark.io.compression.codec属性指定的压缩器，默认是snappy，速度优先


```



