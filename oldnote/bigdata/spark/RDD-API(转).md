---
title: RDD-API(转)
categories: spark  
tags: [spark]
---


# aggregate

```
package spark.examples.rddapi  
  
import org.apache.spark.{SparkConf, SparkContext}  
  
//测试RDD的aggregate方法  
object AggregateTest {  
  def main(args: Array[String]) {  
    val conf = new SparkConf().setMaster("local").setAppName("AggregateTest_00")  
    val sc = new SparkContext(conf);  
    val z1 = sc.parallelize(List(1, 3, 5, 7, 7, 5, 3, 3, 79), 2)  
    /** 
     * Aggregate the elements of each partition, and then the results for all the partitions, using 
     * given combine functions and a neutral "zero value". This function can return a different result 
     * type, U, than the type of this RDD, T. Thus, we need one operation for merging a T into an U 
     * and one operation for merging two U's, as in scala.TraversableOnce. Both of these functions are 
     * allowed to modify and return their first argument instead of creating a new U to avoid memory 
     * allocation. 
     */  
  
    // def aggregate[U: ClassTag](zeroValue: U)(seqOp: (U, T) => U, combOp: (U, U) => U): U  
    //T是RDD中的元素类型，U是aggregate方法自定义的泛型参数，aggregate返回U(而不一定是T)  
    //两个分区取最大值，然后相加  
    //math.max(_, _)表示针对每个partition实施的操作, _ + _表示combiner  
  
    val r1 = z1.aggregate(0)(math.max(_, _), _ + _)  
    println(r1) //86  
  
    //RDD元素类型字符串，aggregate的返回类型同样为String  
    val z2 = sc.parallelize(List("a", "b", "c", "d", "e", "f"), 2)  
    val r2 = z2.aggregate("xx")(_ + _, _ + _)  
    println(r2) //连接操作，结果xxxxabcxxdef，每个分区计算时，加上xx，最后两个分区计算时，继续把xx加上  
  
    //_ + _的道理也是(x,y) => x + y  
    //(x,y)=>math.max是做两两比较吗？  
    val z3 = sc.parallelize(List("12", "23", "345", "4567"), 2)  
    val r3 = z3.aggregate("")((x, y) => math.max(x.length, y.length).toString, (x, y) => x + y)  
    println(r3)   ///结果24，表示两个分区的字符串长度最长的长度转成String后，做拼接  
  
    //结果11
    val r4 = sc.parallelize(List("12", "23", "345", "4567"), 2).aggregate("")((x, y) => math.min(x.length, y.length).toString, (x, y) => x + y)  
    println(r4)  
  }  
}  

```


# cartesian

```
package spark.examples.rddapi  
  
import org.apache.spark.rdd.{CartesianRDD, RDD}  
import org.apache.spark.{SparkContext, SparkConf}  
  
  
object CartesianTest_01 {  
  def main(args: Array[String]) {  
    val conf = new SparkConf().setMaster("local").setAppName("AggregateTest_00")  
    val sc = new SparkContext(conf);  
    val z1 = sc.parallelize(List(2, 3, 4, 5, 6), 2)  
    val z2 = sc.parallelize(List("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"), 3)  
  
    /** 
     * Return the Cartesian product of this RDD and another one, that is, the RDD of all pairs of 
     * elements (a, b) where a is in `this` and b is in `other`. 
     */  
  
    //def cartesian[U: ClassTag](other: RDD[U]): RDD[(T, U)] = new CartesianRDD(sc, this, other)  
    //z1 和 z2集合的元素类型可以不同，并且cartesian是个转换算子，  
    //调用z.collect触发作业  
    val z = z1.cartesian(z2)  
    println("Number of partitions: " + z.partitions.length) //6  
    var count = 0  
  
    z.collect().foreach(x  => {println(x._1 + "," + x._2); count = count + 1}) //  
  
   println("count =" + count) //50  


```


# Repartition

```

package spark.examples.rddapi  
  
import org.apache.spark.{SparkContext, SparkConf}  
  
object RepartitionTest_04 {  
  def main(args: Array[String]) {  
    val conf = new SparkConf().setMaster("local").setAppName("RepartitionTest_04")  
    val sc = new SparkContext(conf);  
    val z1 = sc.parallelize(List(3, 9, 18, 22, 11, 9, 8), 3)  
    //z1.coalesce(5, true)的效果一样，开启shuffle  
    /** 
     * Return a new RDD that has exactly numPartitions partitions. 
     * 
     * Can increase or decrease the level of parallelism in this RDD. Internally, this uses 
     * a shuffle to redistribute data. 
     * 
     * If you are decreasing the number of partitions in this RDD, consider using `coalesce`, 
     * which can avoid performing a shuffle. 
     */  
    val r1 = z1.repartition(5)  
     r1.collect().foreach(println)  
  }  
}  

```



# coalesce

```
package spark.examples.rddapi  
  
import org.apache.spark.{SparkContext, SparkConf}  
  
//coalesce：合并  
object CoalesceTest_03 {  
  def main(args: Array[String]) {  
    val conf = new SparkConf().setMaster("local").setAppName("CoalesceTest_03")  
    val sc = new SparkContext(conf);  
    val z = sc.parallelize(List(3, 9, 18, 22, 11, 9, 8), 3)  
  
    /** 
     * Return a new RDD that is reduced into `numPartitions` partitions. 
     * 
     * This results in a narrow dependency, e.g. if you go from 1000 partitions 
     * to 100 partitions, there will not be a shuffle, instead each of the 100 
     * new partitions will claim 10 of the current partitions. 
     * 
     * However, if you're doing a drastic coalesce, e.g. to numPartitions = 1, 
     * this may result in your computation taking place on fewer nodes than 
     * you like (e.g. one node in the case of numPartitions = 1). To avoid this, 
     * you can pass shuffle = true. This will add a shuffle step, but means the 
     * current upstream partitions will be executed in parallel (per whatever 
     * the current partitioning is). 
     * 
     * Note: With shuffle = true, you can actually coalesce to a larger number 
     * of partitions. This is useful if you have a small number of partitions, 
     * say 100, potentially with a few partitions being abnormally large. Calling 
     * coalesce(1000, shuffle = true) will result in 1000 partitions with the 
     * data distributed using a hash partitioner. 
     */  
    //shuffle默认为false  
    //将分区数由3变成2，大变小使用narrow dependency  
    val zz = z.coalesce(2, false)  
    println("Partitions length: " + zz.partitions.length) //2  
    println(zz.collect()) //结果是[I@100498c？  
    zz.collect().foreach(println)  
  
    //将分区数由3变成6，少变多必须使用shuffle=true  
    //在单机上没有发现有问题  
    //在cluster环境下，为了保证新的分区分布到不同的节点，应该使用shuffle为true  
    //也就是说，少变多也可以使用shuffle为false，但是达不到分区数据进行重新分布的目的  
    val z2 = z.coalesce(6, false)  
    z2.collect().foreach(println)  
  
    //分区扩大，同时设置shuffle为true  
    val z3 = z.coalesce(6, true)  
    z3.collect().foreach(println)  
  
  }  
}  

```