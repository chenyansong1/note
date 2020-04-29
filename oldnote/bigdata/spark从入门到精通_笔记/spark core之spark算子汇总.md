---
title: spark core之spark算子汇总
categories: spark   
toc: true  
tag: [spark]
---


# mapPartitions

```
val sparkConf = new SparkConf()
      .setAppName("Rdd")
      .setMaster("local")
val sc = new SparkContext(sparkConf)

val studentNames = Array("张三", "李四", "王五")
val studentNamesRdd = sc.parallelize(studentNames, 2)


//mapPartitions类似于map
// 不同之处在于map算子一次处理一个partition中的一条数据,
// mapPartitions算子,一次处理一个partition中的所有的数据

// 推荐使用场景
// 如果你的Rdd的数据量不是特别大,那么建议采用mapPartitions算子代替map算子,这样可以加快处理速度
// 但是如果你的rdd的数据量特别大,比如10条数据,不建议使用mapPartitions,因为可能会内存溢出

studentNamesRdd.mapPartitions{
  ite=>
    var arr = mutable.ArrayBuffer<String>()
    while(ite.hasNext){
      val studentName = ite.next
      val studentScore = studentScoresMap.get(studentName)

    }
    null
}

```

# mapPartitionsWithIndex

```
map是对每个元素操作, mapPartitions是对其中的每个partition操作
------------------------------------------------------------
mapPartitionsWithIndex : 把每个partition中的分区号和对应的值拿出来, 看源码
val func = (index: Int, iter: Iterator[(Int)]) => { //index是分区的索引,Iterator是一个分区中的数据,可以迭代
  iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
}
val rdd1 = sc.parallelize(List(1,2,3,4,5,6,7,8,9), 2)
rdd1.mapPartitionsWithIndex(func).collect

```


# groupByKey算子原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/groupByKey算子原理.png)




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

reduceByKey算子原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/reduceByKey算子原理.png)


# distinct算子原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/distinct算子原理.png)



# cogroup算子原理


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/cogroup算子原理.png)



# intersection

```
val rdd6 = sc.parallelize(List(5,6,4,7))
val rdd7 = sc.parallelize(List(1,2,3,4))
#intersection求交集
val rdd9 = rdd6.intersection(rdd7)
```


```
//对rdd中的数据进行去重
distinct([numTasks]))      
//Return a new dataset that contains the distinct elements of the source dataset.
```
intersection算子原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/intersection算子原理.png)



# join算子原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/join算子原理.png)


# sortByKey原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/sortByKey原理.png)


# aggregate
```
aggregate(参数1)(参数2,参数3)   //参数1是初始化的值,参数2是一个函数,对每一个partition的数据聚合,参数3 是对所有partition的结果进行聚合

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

# sample

```
val rand = studentNamesRdd.sample(false,0.1,9)

/*
  /**
   * Return a sampled subset of this RDD.
   *
   * @param withReplacement can elements be sampled multiple times (replaced when sampled out)
   * @param fraction expected size of the sample as a fraction of this RDD's size
   *  without replacement: probability that each element is chosen; fraction must be [0, 1]
   *  with replacement: expected number of times each element is chosen; fraction must be >= 0
   * @param seed seed for the random number generator
   */
  def sample(
      withReplacement: Boolean,
      fraction: Double,
      seed: Long = Utils.random.nextLong): RDD[T] = withScope {
    require(fraction >= 0.0, "Negative fraction value: " + fraction)
    if (withReplacement) {
      new PartitionwiseSampledRDD[T, T](this, new PoissonSampler[T](fraction), true, seed)
    } else {
      new PartitionwiseSampledRDD[T, T](this, new BernoulliSampler[T](fraction), true, seed)
    }
  }
*/

```


# union
```
#union求并集，注意类型要一致
val rdd6 = sc.parallelize(List(5,6,4,7))
val rdd7 = sc.parallelize(List(1,2,3,4))
val rdd8 = rdd6.union(rdd7)
rdd8.distinct.sortBy(x=>x).collect

```


union算子原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/union算子原理.png)







# aggregateByKey

```

 val pairRDD = sc.parallelize(List( ("cat",2), ("cat", 5), ("mouse", 4),("cat", 12), ("dog", 12), ("mouse", 2)), 2)

 def func2(index: Int, iter: Iterator[(String, Int)]) : Iterator[String] = {
   iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
 }
 pairRDD.mapPartitionsWithIndex(func2).foreach(println)     //查看分区的结果

 pairRDD.aggregateByKey(0)(math.max(_, _), _ + _).foreach(println)  //在局部可以将key相同的放在一起迭代,math.max(_, _) 就是取一个分区中key相同的元素中的最大的值
 pairRDD.aggregateByKey(100)(math.max(_, _), _ + _).foreach(println)//会在每个分区中有一个初始化的值:100

/*
第一个参数:每个key的初始值
第二个参数:如何进行shuffle map-side的本地聚合
第三个参数:如何进行shuffle reduce-side的全局聚合
*/

/*
//这里是分区信息
[partID:0, val: (cat,2)]
[partID:0, val: (cat,5)]
[partID:0, val: (mouse,4)]

[partID:1, val: (cat,12)]
[partID:1, val: (dog,12)]
[partID:1, val: (mouse,2)]

//这里是aggregate的结果
(dog,12)
(cat,17)

(mouse,6)

(dog,100)
(cat,200)
(mouse,200)
*/
```

# cartesian笛卡尔积

```

#cartesian笛卡尔积

val rdd1 = sc.parallelize(List("tom", "jerry"))
val rdd2 = sc.parallelize(List("tom", "kitty", "shuke"))
rdd1.cartesian(rdd2).foreach(println)

/*
(tom,tom)
(tom,kitty)
(tom,shuke)
(jerry,tom)
(jerry,kitty)
(jerry,shuke)
*/

```


cartesian算子原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/cartesian算子原理.png)





# coalesce
```
/*
coalesce算子,功能:将RDD的partition缩减
将一定量的数据缩减到更少的partition中去

使用场景,配合filter算子使用
使用filter算子过滤掉很多数据以后,比如30%的数据,出现很多partition中的数据不均匀的情况
此时建议使用coalesce算子,压缩rdd的partition的数量,从而让各个partition中的数据更加紧促
*/


 val pairRDD = sc.parallelize(List( ("cat",2), ("cat", 5), ("mouse", 4),("cat", 12), ("dog", 12), ("mouse", 2)), 3)

 def func2(index: Int, iter: Iterator[(String, Int)]) : Iterator[String] = {
   iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
 }
 pairRDD.mapPartitionsWithIndex(func2).foreach(println)     //查看分区的结果

 pairRDD.coalesce(2).mapPartitionsWithIndex(func2).foreach(println)



/*
------------第一次查看分区的结果-----------------------
[partID:0, val: (cat,2)]
[partID:0, val: (cat,5)]

[partID:1, val: (mouse,4)]
[partID:1, val: (cat,12)]

[partID:2, val: (dog,12)]
[partID:2, val: (mouse,2)]

------------第二次查看分区的结果-----------------------
[partID:0, val: (cat,2)]
[partID:0, val: (cat,5)]

[partID:1, val: (mouse,4)]
[partID:1, val: (cat,12)]
[partID:1, val: (dog,12)]
[partID:1, val: (mouse,2)]
*/

```


coalesce算子原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/coalesce算子原理.png)






# repartition

```
/*
repartition可以将RDD的partition增多或者减少
而coalesce仅仅能将rdd的partition减少

reparation的使用场景
使用spark sql从hive中查询数据时,spark sql会根据hive对应的HDFS文件的block数量来决定加载出来的数据rdd有多少个partition,这里的partition数量,是我们根本无法设置的,有些时候产生的partition数量少了,此时就可以在spark sql加载hive数据到rdd中以后,立即使用reparation算在,将rdd的partition数量变多
*/

 val pairRDD = sc.parallelize(List( ("cat",2), ("cat", 5), ("mouse", 4),("cat", 12), ("dog", 12), ("mouse", 2)), 1)

 def func2(index: Int, iter: Iterator[(String, Int)]) : Iterator[String] = {
   iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
 }
 pairRDD.mapPartitionsWithIndex(func2).foreach(println)     //查看分区的结果

 pairRDD.repartition(3).mapPartitionsWithIndex(func2).foreach(println)



/*
------------第一次查看分区的结果-----------------------
[partID:0, val: (cat,2)]
[partID:0, val: (cat,5)]
[partID:0, val: (mouse,4)]
[partID:0, val: (cat,12)]
[partID:0, val: (dog,12)]
[partID:0, val: (mouse,2)]

------------第二次查看分区的结果-----------------------
[partID:0, val: (mouse,4)]
[partID:0, val: (mouse,2)]

[partID:1, val: (cat,2)]
[partID:1, val: (cat,12)]

[partID:2, val: (cat,5)]
[partID:2, val: (dog,12)]
*/

```















