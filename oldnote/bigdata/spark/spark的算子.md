---
title: spark的算子
categories: spark  
tags: [spark]
---




# 1.什么是RDD
&emsp;RDD（Resilient Distributed Dataset）叫做<font color=red>分布式数据集</font>，是Spark中最基本的数据抽象，它代表一个不可变、可分区、里面的元素可并行计算的集合。RDD具有数据流模型的特点：自动容错、位置感知性调度和可伸缩性。RDD允许用户在执行多个查询时显式地<font color=red>将工作集缓存在内存中</font>，后续的查询能够重用工作集，这极大地提升了查询速度。

<!--more-->


# 2.RDD的属性

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/rdd/1.png)
 
1. 一组分片（Partition），即数据集的基本组成单位。对于RDD来说，每个分片都会被一个计算任务处理，并决定并行计算的粒度。用户可以在创建RDD时指定RDD的分片个数，如果没有指定，那么就会采用默认值。默认值就是程序所分配到的CPU Core的数目。
2. 一个计算每个分区的函数。Spark中RDD的计算是以分片为单位的，每个RDD都会实现compute函数以达到这个目的。compute函数会对迭代器进行复合，不需要保存每次计算的结果。
3. RDD之间的依赖关系。RDD的每次转换都会生成一个新的RDD，所以RDD之间就会形成类似于流水线一样的前后依赖关系。在部分分区数据丢失时，Spark可以通过这个依赖关系重新计算丢失的分区数据，而不是对RDD的所有分区进行重新计算。
4. 一个Partitioner，即RDD的分片函数。当前Spark中实现了两种类型的分片函数，一个是基于哈希的HashPartitioner，另外一个是基于范围的RangePartitioner。只有对于于key-value的RDD，才会有Partitioner，非key-value的RDD的Parititioner的值是None。Partitioner函数不但决定了RDD本身的分片数量，也决定了parent RDD Shuffle输出时的分片数量。
5. 一个列表，存储存取每个Partition的优先位置（preferred location）。对于一个HDFS文件来说，这个列表保存的就是每个Partition所在的块的位置。按照“移动数据不如移动计算”的理念，Spark在进行任务调度的时候，会尽可能地将计算任务分配到其所要处理数据块的存储位置。


# 3.创建RDD
创建RDD的两种方式:
1.由一个已经存在的Scala集合创建(通过Scala集合或数组以并行化的方式创建RDD)
```
 val rdd1 = sc.parallelize(Array(1,2,3,4,5,6,7,8))
```
2.由外部存储系统的数据集创建，包括本地的文件系统，还有所有Hadoop支持的数据集，比如HDFS、Cassandra、HBase等
```
 val rdd2 = sc.textFile("hdfs://node1.itcast.cn:9000/words.txt")
```

# 4.RDD编程API
spark的算子分为两类
1. Transformation (转换)
2. Action (动作)

## 4.1.Transformation
&emsp;RDD中的所有转换都是延迟加载的，也就是说，它们并不会直接计算结果。相反的，它们只是记住这些应用到基础数据集（例如一个文件）上的转换动作。只有当发生一个要求返回结果给Driver的动作时，这些转换才会真正运行。这种设计让Spark更加有效率地运行。
>常用的Transformation：

转换													|含义
:----------                                             |:----------   
map(func)												|返回一个新的RDD，该RDD由每一个输入元素经过func函数转换后组成
filter(func)											|返回一个新的RDD，该RDD由经过func函数计算后返回值为true的输入元素组成
flatMap(func)											|类似于map，但是每一个输入元素可以被映射为0或多个输出元素（所以func应该返回一个序列，而不是单一元素）
mapPartitions(func)										|类似于map，但独立地在RDD的每一个分片上运行，因此在类型为T的RDD上运行时，func的函数类型必须是Iterator[T] => Iterator[U]
mapPartitionsWithIndex(func)							|类似于mapPartitions，但func带有一个整数参数表示分片的索引值，因此在类型为T的RDD上运行时，func的函数类型必须是(Int, Interator[T]) => Iterator[U]
sample(withReplacement, fraction, seed)					|根据fraction指定的比例对数据进行采样，可以选择是否使用随机数进行替换，seed用于指定随机数生成器种子
union(otherDataset)										|对源RDD和参数RDD求并集后返回一个新的RDD
intersection(otherDataset)								|对源RDD和参数RDD求交集后返回一个新的RDD
distinct([numTasks]))									|对源RDD进行去重后返回一个新的RDD
groupByKey([numTasks])									|在一个(K,V)的RDD上调用，返回一个(K, Iterator[V])的RDD
reduceByKey(func, [numTasks])							|在一个(K,V)的RDD上调用，返回一个(K,V)的RDD，使用指定的reduce函数，将相同key的值聚合到一起，与groupByKey类似，reduce任务的个数可以通过第二个可选的参数来设置
aggregateByKey(zeroValue)(seqOp, combOp, [numTasks])	| 
sortByKey([ascending], [numTasks])						|在一个(K,V)的RDD上调用，K必须实现Ordered接口，返回一个按照key进行排序的(K,V)的RDD
sortBy(func,[ascending], [numTasks])					|与sortByKey类似，但是更灵活
join(otherDataset, [numTasks])							|在类型为(K,V)和(K,W)的RDD上调用，返回一个相同key对应的所有元素对在一起的(K,(V,W))的RDD
cogroup(otherDataset, [numTasks])						|在类型为(K,V)和(K,W)的RDD上调用，返回一个(K,(Iterable<V>,Iterable<W>))类型的RDD
cartesian(otherDataset)									|笛卡尔积
pipe(command, [envVars])	                           |
coalesce(numPartitions)		                           |
repartition(numPartitions)	                           |
repartitionAndSortWithinPartitions(partitioner)	       |

## 4.2.Action

动作										|含义
:-------------------------------------------|:---------------
reduce(func)								|通过func函数聚集RDD中的所有元素，这个功能必须是课交换且可并联的
collect()									|在驱动程序中，以<font color=red>数组的形式</font>返回数据集的所有元素
count()										|返回RDD的元素个数
first()										|返回RDD的第一个元素（类似于take(1)）
take(n)										|返回一个由数据集的前n个元素组成的数组
takeSample(withReplacement,num, [seed])		|返回一个数组，该数组由从数据集中随机采样的num个元素组成，可以选择是否用随机数替换不足的部分，seed用于指定随机数生成器种子
takeOrdered(n, [ordering])	                |
saveAsTextFile(path)						|将数据集的元素以textfile的形式保存到HDFS文件系统或者其他支持的文件系统，对于每个元素，Spark将会调用toString方法，将它装换为文件中的文本
saveAsSequenceFile(path) 					|将数据集中的元素以Hadoop sequencefile的格式保存到指定的目录下，可以使HDFS或者其他Hadoop支持的文件系统。
saveAsObjectFile(path) 	                    |
countByKey()								|针对(K,V)类型的RDD，返回一个(K,Int)的map，表示每一个key对应的元素个数。
foreach(func)								|在数据集的每一个元素上，运行函数func进行更新。



## 4.3.练习


```

#常用Transformation(即转换，延迟加载)
#通过并行化scala集合创建RDD
val rdd1 = sc.parallelize(Array(1,2,3,4,5,6,7,8))
 
 
#查看该rdd的分区数量
rdd1.partitions.length
val rdd1 = sc.parallelize(List(5,6,4,7,3,8,2,9,1,10),5)//可以手动指定分区的大小
 
val rdd1 = sc.parallelize(List(5,6,4,7,3,8,2,9,1,10))
val rdd2 = sc.parallelize(List(5,6,4,7,3,8,2,9,1,10)).map(_*2).sortBy(x=>x,true)
val rdd3 = rdd2.filter(_>10) //取出大于10的数据
 
//转成字符串之后,排序就是字典顺序
val rdd2 = sc.parallelize(List(5,6,4,7,3,8,2,9,1,10)).map(_*2).sortBy(x=>x+"",true)
val rdd2 = sc.parallelize(List(5,6,4,7,3,8,2,9,1,10)).map(_*2).sortBy(x=>x.toString,true)
 
 
val rdd4 = sc.parallelize(Array("a b c", "d e f", "h i j"))
rdd4.flatMap(_.split(' ')).collect
 
val rdd5 = sc.parallelize(List(List("a b c", "a b b"),List("e f g", "a f g"), List("h i j", "a a b")))
 
 
List("a b c", "a b b") =List("a","b",))
 
 
 
 
 
 
rdd5.flatMap(_.flatMap(_.split(" "))).collect
 
#union求并集，注意类型要一致
val rdd6 = sc.parallelize(List(5,6,4,7))
val rdd7 = sc.parallelize(List(1,2,3,4))
val rdd8 = rdd6.union(rdd7)
rdd8.distinct.sortBy(x=>x).collect
 
#intersection求交集
val rdd9 = rdd6.intersection(rdd7)
 
 
#join
val rdd1 = sc.parallelize(List(("tom", 1), ("jerry", 2), ("kitty", 3)))
val rdd2 = sc.parallelize(List(("jerry", 9), ("tom", 8), ("shuke", 7)))
 
 
val rdd3 = rdd1.join(rdd2)
val rdd3 = rdd1.leftOuterJoin(rdd2)
val rdd3 = rdd1.rightOuterJoin(rdd2)
 
 
#groupByKey
val rdd3 = rdd1 union rdd2
rdd3.groupByKey
rdd3.groupByKey.map(x=>(x._1,x._2.sum))
rdd3.groupByKey.mapValues(_.sum).collect
 
#WordCount, 第二个效率低
sc.textFile("/root/words.txt").flatMap(x=>x.split(" ")).map((_,1)).reduceByKey(_+_).sortBy(_._2,false).collect
sc.textFile("/root/words.txt").flatMap(x=>x.split(" ")).map((_,1)).groupByKey.map(t=>(t._1, t._2.sum)).collect
 
#cogroup
val rdd1 = sc.parallelize(List(("tom", 1), ("tom", 2), ("jerry", 3), ("kitty", 2)))
val rdd2 = sc.parallelize(List(("jerry", 2), ("tom", 1), ("shuke", 2)))
val rdd3 = rdd1.cogroup(rdd2)
val rdd4 = rdd3.map(t=>(t._1, t._2._1.sum + t._2._2.sum))
 
#cartesian笛卡尔积
val rdd1 = sc.parallelize(List("tom", "jerry"))
val rdd2 = sc.parallelize(List("tom", "kitty", "shuke"))
val rdd3 = rdd1.cartesian(rdd2)
 
###################################################################################################
 
#spark action
val rdd1 = sc.parallelize(List(1,2,3,4,5), 2)
 
#collect
rdd1.collect
 
#reduce
val rdd2 = rdd1.reduce(_+_)
 
#count
rdd1.count
 
#top
rdd1.top(2)
 
#take
rdd1.take(2)
 
#first(similer to take(1))
rdd1.first
 
#takeOrdered
rdd1.takeOrdered(3)
 
#
 
map(func)                                 Return a new distributed dataset formed by passing each element of the source through a function func.
filter(func)                             Return a new dataset formed by selecting those elements of the source on which func returns true.
flatMap(func)                             Similar to map, but each input item can be mapped to 0 or more output items (so func should return a Seq rather than a single item).
mapPartitions(func)                         Similar to map, but runs separately on each partition (block) of the RDD, so func must be of type Iterator<T> => Iterator<U> when running on an RDD of type T.
mapPartitionsWithIndex(func)             Similar to mapPartitions, but also provides func with an integer value representing the index of the partition, so func must be of type (Int, Iterator<T>) => Iterator<U> when running on an RDD of type T.
sample(withReplacement, fraction, seed)     Sample a fraction fraction of the data, with or without replacement, using a given random number generator seed.
union(otherDataset)          Return a new dataset that contains the union of the elements in the source dataset and the argument.
intersection(otherDataset)        Return a new RDD that contains the intersection of elements in the source dataset and the argument.
distinct([numTasks]))         Return a new dataset that contains the distinct elements of the source dataset.
groupByKey([numTasks])         When called on a dataset of (K, V) pairs, returns a dataset of (K, Iterable<V>) pairs.
reduceByKey(func, [numTasks])       When called on a dataset of (K, V) pairs, returns a dataset of (K, V) pairs where the values for each key are aggregated using the given reduce function func, which must be of type (V,V) => V. Like in groupByKey, the number of reduce tasks is configurable through an optional second argument.
aggregateByKey(zeroValue)(seqOp, combOp, [numTasks]) When called on a dataset of (K, V) pairs, returns a dataset of (K, U) pairs where the values for each key are aggregated using the given combine functions and a neutral "zero" value. Allows an aggregated value type that is different than the input value type, while avoiding unnecessary allocations. Like in groupByKey, the number of reduce tasks is configurable through an optional second argument.
sortByKey([ascending], [numTasks])      When called on a dataset of (K, V) pairs where K implements Ordered, returns a dataset of (K, V) pairs sorted by keys in ascending or descending order, as specified in the boolean ascending argument.
join(otherDataset, [numTasks])       When called on datasets of type (K, V) and (K, W), returns a dataset of (K, (V, W)) pairs with all pairs of elements for each key. Outer joins are supported through leftOuterJoin, rightOuterJoin, and fullOuterJoin.
cogroup(otherDataset, [numTasks])      When called on datasets of type (K, V) and (K, W), returns a dataset of (K, (Iterable<V>, Iterable<W>)) tuples. This operation is also called groupWith.
cartesian(otherDataset)         When called on datasets of types T and U, returns a dataset of (T, U) pairs (all pairs of elements).
pipe(command, [envVars])        Pipe each partition of the RDD through a shell command, e.g. a Perl or bash script. RDD elements are written to the process's stdin and lines output to its stdout are returned as an RDD of strings.
coalesce(numPartitions)         Decrease the number of partitions in the RDD to numPartitions. Useful for running operations more efficiently after filtering down a large dataset.
repartition(numPartitions)        Reshuffle the data in the RDD randomly to create either more or fewer partitions and balance it across them. This always shuffles all data over the network.
repartitionAndSortWithinPartitions(partitioner)   Repartition the RDD according to the given partitioner and, within each resulting partition, sort records by their keys. This is more efficient than calling repartition and then sorting within each partition because it can push the sorting down into the shuffle machinery.
 
(K,(Iterable<V>,Iterable<W>))
 


```


# 5.相关网站推荐
比spark官网的例子多:    http://homepage.cs.latrobe.edu.au/zhe/ZhenHeSparkRDDAPIExamples.html









