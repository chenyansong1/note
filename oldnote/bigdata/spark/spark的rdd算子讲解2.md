---
title: spark的rdd算子讲解2
categories: spark  
tags: [spark]
---



# reduceByKey

```
#Example
sc.textFile(args(0)).flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_).

#源码
  def reduceByKey(partitioner: Partitioner, func: (V, V) => V): RDD[(K, V)] = self.withScope {
    combineByKeyWithClassTag[V]((v: V) => v, func, func, partitioner)
  }
/*
在reduceByKey的内部是调用的combineByKeyWithClassTag, 由上面的源码知道combineByKeyWithClassTag的第一个参数是对value原样输出,第二个,第三个参数是调用reduceByKey中指定的函数
第二个参数的功能:是在partition内进行操作
第三个参数的功能是对各个partition的所有的结果进行操作
*/
```

<!--more-->

# collect

```
package spark.examples.rddapi

import org.apache.spark.{SparkContext, SparkConf}

object CollectTest_07 {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("CoGroupTest_05")
    val sc = new SparkContext(conf);
    val z1 = sc.parallelize(List((3, "A"), (6, "B1"), (7, "Z1"), (9, "E"), (7, "F"), (9, "Y"), (77, "Z"), (31, "X")), 3)

    /**
     * Return an array that contains all of the elements in this RDD.
     */
    //这是一个行动算子
    z1.collect().foreach(println)

    /**
     * Return an RDD that contains all matching values by applying `f`.
     */
    //    def collect[U: ClassTag](f: PartialFunction[T, U]): RDD[U] = {
    //      filter(f.isDefinedAt).map(f)
    //    }

//    val f  = {
//      case x: (Int, String) => x
//    }
//    val z2 = z1.collect(f)
//    println(z2)
  }
}

```

# mapPartitionsWithIndex 
```
map是对每个元素操作, mapPartitions是对其中的每个partition操作
 
-------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------
mapPartitionsWithIndex : 把每个partition中的分区号和对应的值拿出来, 看源码
val func = (index: Int, iter: Iterator[(Int)]) => { //index是分区的索引,Iterator是一个分区中的数据,可以迭代
  iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
}
val rdd1 = sc.parallelize(List(1,2,3,4,5,6,7,8,9), 2)
rdd1.mapPartitionsWithIndex(func).collect


```

# aggregate
```
aggregate(参数1)(参数2,参数3)   //参数1是初始化的值,参数2是一个函数,对每一个partition的数据聚合,参数3 是对所有partition的结果进行聚合
 
def func1(index: Int, iter: Iterator[(Int)]) : Iterator[String] = {
  iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
}
val rdd1 = sc.parallelize(List(1,2,3,4,5,6,7,8,9), 2)
rdd1.mapPartitionsWithIndex(func1).collect
###是action操作, 第一个参数是初始值, 二:是2个函数[每个函数都是2个参数(第一个参数:先对个个分区进行合并, 第二个:对个个分区合并后的结果再进行合并), 输出一个参数]
###0 + (0+1+2+3+4/*局部求和*/   +   0+5+6+7+8+9/*局部求和*/)
rdd1.aggregate(0)(_+_, _+_/*全局求和*/)
rdd1.aggregate(0)(math.max(_, _), _ + _) //传给第一个参数的是:itera ,所以使用math.max(_, _) 去迭代
###5和1比, 得5再和234比得5 --> 5和6789比,得9 --> 5 + (5+9)
rdd1.aggregate(5)(math.max(_, _), _ + _)
 
add1.reduce(math.max(_,_))//第一个下划线是上一次求max的值,第二个下划线是循环add1中的一个元素
 
 
aggregate:先进行局部的(partition)操作(循环迭代所有的局部元素),然后进行全局的操作(循环迭代所有的分区)
 
 
val rdd2 = sc.parallelize(List("a","b","c","d","e","f"),2)
def func2(index: Int, iter: Iterator[(String)]) : Iterator[String] = {
  iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
}
rdd2.aggregate("")(_ + _, _ + _)  //abcdef
rdd2.aggregate("=")(_ + _, _ + _)  //==abc=def  :局部求和为 =abc 和 =def 最后整体求和:==abc=def
 
val rdd3 = sc.parallelize(List("12","23","345","4567"),2)
rdd3.aggregate("")((x,y) => math.max(x.length, y.length).toString, (x,y) => x + y)//24 或者是42 因为是并行的任务,所以可能先返回2,也有可能先返回4
 
val rdd4 = sc.parallelize(List("12","23","345",""),2)
rdd4.aggregate("")((x,y) => math.min(x.length, y.length).toString, (x,y) => x + y)//返回10 或者01
/*因为有初始值(空字符串)的存在,所以如下过程:
分区0:
 math.min("".length,"12".length)  ==>0.toString  "0"
 math.min("0".length,"23".length) ==>1.toString  "1" 最终结果===>1
 
分区1:
 math.min("".length,"345".length) ==>0.toString  "0"
 math.min("0".length,"".length)  ==>0.toString  "0" 最终结果===>0
 
分区全局聚合:""+"1"+"0"  或者   ""+"0"+"1"
*/
 
val rdd5 = sc.parallelize(List("12","23","","345"),2)
rdd5.aggregate("")((x,y) => math.min(x.length, y.length).toString, (x,y) => x + y) //结果: "11"
 
 

```

# aggregateByKey
```
//参见浏览器
 
 
val pairRDD = sc.parallelize(List( ("cat",2), ("cat", 5), ("mouse", 4),("cat", 12), ("dog", 12), ("mouse", 2)), 2)

def func2(index: Int, iter: Iterator[(String, Int)]) : Iterator[String] = {
  iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
}
pairRDD.mapPartitionsWithIndex(func2).collect    //查看分区的结果


pairRDD.aggregateByKey(0)(math.max(_, _), _ + _).collect   //在局部可以将key相同的放在一起迭代,math.max(_, _) 就是取一个分区中key相同的元素中的最大的值
pairRDD.aggregateByKey(100)(math.max(_, _), _ + _).collect

```

# coalesce  合并, repartition

```

重新分区之后,要走网络

coalesce  合并, repartition
val rdd1 = sc.parallelize(1 to 10, 10)
val rdd2 = rdd1.coalesce(2, false)  //false表示不进行shuffle
rdd2.partitions.length

```

# combineByKey 
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

# collectAsMap 

```
#可以将list转成Map
collectAsMap : Map(b -> 2, a -> 1)
val rdd = sc.parallelize(List(("a", 1), ("b", 2)))
rdd.collectAsMap

```



# countByKey
```

val rdd1 = sc.parallelize(List(("a", 1), ("b", 2), ("b", 2), ("c", 2), ("c", 1)))
rdd1.countByKey        // Map(a->1, b->2, c->2)

rdd1.countByValue    //Map((c,2)->1, (a,1)->1, (b,2)->2, (c,1)->1)  //将元组当做一个value
```



# filterByRange
```

val rdd1 = sc.parallelize(List(("e", 5), ("c", 3), ("d", 4), ("c", 2), ("a", 1), (b,6)))
val rdd2 = rdd1.filterByRange("b", "d")  //取指定key范围内的元组
rdd2.collect

```

# flatMapValues  
```
      
flatMapValues  :  Array((a,1), (a,2), (b,3), (b,4))
val rdd3 = sc.parallelize(List(("a", "1 2"), ("b", "3 4")))
val rdd4 = rdd3.flatMapValues(_.split(" "))
rdd4.collect

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/rdd/2.png)
 

# foldByKey
```
 
val rdd1 = sc.parallelize(List("dog", "wolf", "cat", "bear"), 2)
val rdd2 = rdd1.map(x => (x.length, x))
val rdd3 = rdd2.foldByKey("")(_+_)
 
val rdd = sc.textFile("hdfs://node-1.itcast.cn:9000/wc").flatMap(_.split(" ")).map((_, 1))
rdd.foldByKey(0)(_+_)
```



![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/rdd/3.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/rdd/4.png)
 
# foreachPartition
```
val rdd1 = sc.parallelize(List(1, 2, 3, 4, 5, 6, 7, 8, 9), 3)
rdd1.foreachPartition(x => println(x.reduce(_ + _)))

//map会遍历rdd中的每一个元素,然后返回生成一个新的rdd
//foreach 会遍历rdd中的每一个元素,但是不会生成新的rdd
//foreachPartition 会对一个partition进行操作
例子;
想要存储rdd中的每一个元素,那么使用map和foreach在遍历每一个元素的时候都去哪一个数据库连接池中的连接,
而使用foreachPartition将只是针对每一个分区,拿一个数据库连接,然后使用这一个连接,遍历一个分区中的所有的元素,存入数据库

 


```




![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/rdd/5.png)




# keyBy 
```
keyBy : 以传入的参数做key
val rdd1 = sc.parallelize(List("dog", "salmon", "salmon", "rat", "elephant"), 3)
val rdd2 = rdd1.keyBy(_.length)        //遍历每一个元素,并为每一个元素指定key
rdd2.collect
```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/rdd/6.png)
 

# keys values
```

val rdd1 = sc.parallelize(List("dog", "tiger", "lion", "cat", "panther", "eagle"), 2)
val rdd2 = rdd1.map(x => (x.length, x))
rdd2.keys.collect
rdd2.values.collect
```


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/rdd/7.png)

# checkpoint
```

sc.setCheckpointDir("hdfs://node-1.itcast.cn:9000/ck")
val rdd = sc.textFile("hdfs://node-1.itcast.cn:9000/wc").flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_)
rdd.checkpoint
rdd.isCheckpointed
rdd.count
rdd.isCheckpointed
rdd.getCheckpointFile

```
