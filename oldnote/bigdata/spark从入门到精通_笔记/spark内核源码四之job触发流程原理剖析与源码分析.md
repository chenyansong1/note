---
title: spark内核源码四之wordcount的job触发流程原理剖析与源码分析
categories: spark  
tags: [spark]
---


wordcount的执行源码查看

```
/*
首先调用hadoopFile方法,会创建一个HadoopRDD,其中的元素就是:(key,value) pair
key是HDFS或文本文件的每一行的offset,value是文本行
然后对HadoopRDD调用map方法,会剔除ky,只保留value,map方法返回的是MapPartitionsRDD
MapPartitionsRDD内部的元素,其实就是一行一行的文本行
*/
val lines = sc.textFile("xx.txt") //最终返回的是MapPartitionsRDD

val words = lines.flatMap(_.split(" "))//返回的是MapPartitionsRDD
val pair = words.map((_,1))//返回的是MapPartitionsRDD

//pair是一个MapPartitionsRDD,但是MapPartitionsRDD是继承了RDD的,但是在MapPartitionsRDD和RDD中都没有reduceByKey方法,为什么呢?其实在RDD中有一个隐式转换:implicit def rddToPairRDDFunctions会将rdd转成PairRDDFunctions,我们观察PairRDDFunctions中就有reduceByKey方法
val counts = pairs.reduceByKey(_+_)//返回的是ShuffledRDD
count.foreach(println)


============================
在执行foreach时代码如下:因为foreach是一个action,所以会触发job
def foreach(f: T => Unit): Unit = withScope {
  val cleanF = sc.clean(f)
  sc.runJob(this, (iter: Iterator[T]) => iter.foreach(cleanF))
}

在sc中runJob方法中如下:

dagScheduler.runJob(rdd, cleanedFunc, partitions, callSite, resultHandler, localProperties.get)


// 看是否是一个action的标志是:是否会触发runJob方法

```
