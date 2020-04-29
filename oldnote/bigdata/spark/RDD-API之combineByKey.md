---
title: RDD-API之combineByKey
categories: spark  
tags: [spark]
---


# 1.combineByKey函数的运行机制
 
&emsp;RDD提供了很多针对元素类型为(K,V)的API，这些API封装在PairRDDFunctions类中，通过Scala隐式转换使用。这些API实现上是借助于combineByKey实现的。combineByKey函数本身也是RDD开放给Spark开发人员使用的API之一
 首先看一下combineByKey的方法说明：

```
/**
   * Generic function to combine the elements for each key using a custom set of aggregation
   * functions. Turns an RDD[(K, V)] into a result of type RDD[(K, C)], for a "combined type" C
   * Note that V and C can be different -- for example, one might group an RDD of type
   * (Int, Int) into an RDD of type (Int, Seq[Int]). Users provide three functions:
   *
   * - `createCombiner`, which turns a V into a C (e.g., creates a one-element list)
   * - `mergeValue`, to merge a V into a C (e.g., adds it to the end of a list)
   * - `mergeCombiners`, to combine two C's into a single one.
   *
   * In addition, users can control the partitioning of the output RDD, and whether to perform
   * map-side aggregation (if a mapper can produce multiple items with the same key).
   */
  def combineByKey[C](createCombiner: V => C,
      mergeValue: (C, V) => C,
      mergeCombiners: (C, C) => C,
      partitioner: Partitioner,
      mapSideCombine: Boolean = true,
      serializer: Serializer = null): RDD[(K, C)] = {
    require(mergeCombiners != null, "mergeCombiners must be defined") // required as of Spark 0.9.0
    if (keyClass.isArray) {
      if (mapSideCombine) {
        throw new SparkException("Cannot use map-side combining with array keys.")
      }
      if (partitioner.isInstanceOf[HashPartitioner]) {
        throw new SparkException("Default partitioner cannot partition array keys.")
      }
    }
    val aggregator = new Aggregator[K, V, C](
      self.context.clean(createCombiner),
      self.context.clean(mergeValue),
      self.context.clean(mergeCombiners))
    if (self.partitioner == Some(partitioner)) {
      self.mapPartitions(iter => {
        val context = TaskContext.get()
        new InterruptibleIterator(context, aggregator.combineValuesByKey(iter, context))
      }, preservesPartitioning = true)
    } else {
      new ShuffledRDD[K, V, C](self, partitioner)
        .setSerializer(serializer)
        .setAggregator(aggregator)
        .setMapSideCombine(mapSideCombine)
    }
  }


/* 主要看:
      createCombiner: V => C,
      mergeValue: (C, V) => C,
      mergeCombiners: (C, C) => C,

combineByKey的功能是对RDD中的数据集按照Key进行聚合(想象下Hadoop MapReduce的Combiner，用于Map端做Reduce)。聚合的逻辑是通过自定义函数提供给combineByKey。
从上面的源代码中可以看到，combineByKey是把(K,V)类型的RDD转换为(K,C)类型的RDD，C和V可以不一样。
combineByKey函数需要三个重要的函数作为参数

createCombiner：在遍历RDD的数据集合过程中，对于遍历到的(k,v)，如果combineByKey第一次遇到值为k的Key（类型K），那么将对这个(k,v)调用combineCombiner函数，它的作用是将v转换为c(类型是C，聚合对象的类型，c作为局和对象的初始值)

mergeValue：在遍历RDD的数据集合过程中，对于遍历到的(k,v)，如果combineByKey不是第一次(或者第二次，第三次...)遇到值为k的Key（类型K），那么将对这个(k,v)调用mergeValue函数，它的作用是将v累加到聚合对象（类型C）中，mergeValue的类型是(C,V)=>C,参数中的C遍历到此处的聚合对象，然后对v进行聚合得到新的聚合对象值

mergeCombiners：因为combineByKey是在分布式环境下执行，RDD的每个分区单独进行combineByKey操作，最后需要对各个分区的结果进行最后的聚合，它的函数类型是(C,C)=>C，每个参数是分区聚合得到的聚合对象。
 

*/

```


 
 combineByKey的流程是：

假设**一组具有相同 K 的 <K, V> records 正在一个个流向 combineByKey()**，createCombiner 将第一个 record 的value 初始化为 c （比如，c = value），然后从第二个 record 开始，来一个 record 就使用 mergeValue(c,record.value) 来更新 c，比如想要对这些 records 的所有 values 做 sum，那么使用 c = c + record.value。等到records 全部被 mergeValue()，得到结果 c。假设还有一组 records（key 与前面那组的 key 均相同）一个个到来，combineByKey() 使用前面的方法不断计算得到 c'。现在如果要求这两组 records 总的 combineByKey() 后的结果，那么可以使用 final c = mergeCombiners(c, c') 来计算。



# 举例


```
 
combineByKey : 和reduceByKey是相同的效果
###第一个参数x:原封不动取出来, 第二个参数:是函数, 局部运算, 第三个:是函数, 对局部运算后的结果再做运算
###每个分区中每个key中value中的第一个值, (hello,1)(hello,1)(good,1)-->(hello(1,1),good(1))-->x就相当于hello的第一个1, good中的1
val rdd1 = sc.textFile("hdfs://master:9000/wordcount/input/").flatMap(_.split(" ")).map((_, 1))
val rdd2 = rdd1.combineByKey(x => x, (a: Int, b: Int) => a + b, (m: Int, n: Int) => m + n)
rdd1.collect
rdd2.collect
 
###当input下有3个文件时(有3个block块, 不是有3个文件就有3个block, ), 每个会多加3个10
val rdd3 = rdd1.combineByKey(x => x + 10, (a: Int, b: Int) => a + b, (m: Int, n: Int) => m + n) //x => x + 10 只是将分区的第一个元素作为x,然后加10 ,将x+10作为局部计算的初始值
rdd3.collect
 
 
val rdd4 = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"), 3)
val rdd5 = sc.parallelize(List(1,1,2,2,2,1,2,2,2), 3)
val rdd6 = rdd5.zip(rdd4) //List((1,dog),(1,cat),(2,gnu),(2,salmon),(2,rabbit),(1,turkey),(2,wolf),(2,bear),(2,bee))
val rdd7 = rdd6.combineByKey(List(_), (x: List[String], y: String) => x :+ y, (m: List[String], n: List[String]) => m ++ n)        //(1,list("dog","cat","turkey") ) ,  (2, list("gnu","salmon","rabbit","wolf","bear","bee")) )
 
 

```



