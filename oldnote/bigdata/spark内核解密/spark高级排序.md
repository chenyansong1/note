---
title: spark高级排序
categories: spark   
toc: true  
tag: [spark]
---


# 基础排序算法

```
val rdd = sc.textFile("C:\\Users\\Administrator\\Desktop\\test.txt")

val result = rdd.flatMap(_.split(" "))
                .map((_,1)).reduceByKey(_+_)
                .map(pair=>(pair._2,pair._1))
               .sortByKey(false) //默认是升序的,false是降序
               .map(pair=>(pair._2,pair._1))
result.collect.foreach(println)
/*执行结果:
(chen,320)
(zhangsan,160)
(cheng,160)
(zlisi,160)
(chengyansong,131)
(chenyansong,131)
(chengyansongchenyansong,29)
(,1)
 */

```


# 二次排序算法
所谓二次排序就是排序的时候考虑2个维度(如指定2个排序列,当第一列的相同,比较第二列)

```
package org.dt.spark

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Administrator on 2017/3/24.
  */
class SecondarySortKey(val first:Int, val second: Int ) extends Ordered[SecondarySortKey] with Serializable{
  def compare(other: SecondarySortKey): Int = {
    if(this.first - other.first !=0){
      this.first - other.first
    }else{
      this.second - other.second
    }
  }
}

object SecondarySortKey{
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\Users\\Administrator\\Desktop\\hadoop\\")
    val sc = sparkContext("Transformation Operations")
    val line = sc.textFile("C:\\Users\\Administrator\\Desktop\\xx.txt")
    /*xx.txt
    1 11
    2 22
    3 33
    2 11
    1 22
     */
    val pairWithSortKey = line.map(line=>{
      val arr = line.split(" ")
      val first = arr(0).toInt
      val second = arr(1).toInt
      (new SecondarySortKey(first,second), line)  //指定Tuple的key为SecondarySortKey
    }).sortByKey(false).map(pair=>pair._2)//sortByKey排序的时候会以SecondarySortKey为key排序

    pairWithSortKey.collect.foreach(println)
    /*
    打印结果:
    3 33
    2 22
    2 11
    1 22
    1 11
     */
  }

  //在实际的生成中,我们是封装函数来进行逻辑的组织
  def sparkContext(name:String)={
    val conf = new SparkConf().setAppName(name).setMaster("local")

    //创建SparkContext,这是第一个RDD创建的唯一入口,是通往集群的唯一通道
    val sc = new SparkContext(conf)
    sc
  }
}



```


# Top N
```
 val rdd = sc.textFile("C:\\Users\\Administrator\\Desktop\\xx.txt")
/*
66
33
333
355
22
234
77
22
*/
 val pairs = rdd.map(line=>(line.toInt,line))//生成key-value方便sortByKey进行排序(为了方便排序,我们添加了key

),Int已经实现了比较排序的接口,所以我们不用自己写
 val rddSortedArr = pairs.sortByKey(false).map(_._1).take(5)
 rddSortedArr.foreach(println)
 /*
 打印结果:
 355
 333
 234
 77
 66
 */

```

# 分组排序
找出不同的类型的每组中的top N

```
 val rdd = sc.textFile("C:\\Users\\Administrator\\Desktop\\xx.txt")
 val lines=rdd.map{ line => (line.split(" ")(0),line.split(" ")(1).toInt) }

 //分组
 val groups=lines.groupByKey()
 //组内进行排序
 val groupsSort=groups.map(tu=>{
   val key=tu._1
   val values=tu._2
   val sortValues=values.toList.sortWith(_>_).take(4)//取top 4
   (key,sortValues)
 })

 //组与组之间进行排序
 groupsSort.sortBy(tu=>tu._1, false, 1).collect.foreach(value=>{
   print(value._1)
   value._2.foreach(v=>print("\t"+v))
   println()
 })

 /*
 打印结果:
 spark	100	99	94	88
 hadoop	88	56	35	33
  */

```


# 排序算法详解

RangePartitioner主要是依赖的RDD的数据划分成不同的范围,关键的地方是不同的范围是有序的,即Partition之间是有序的,但是在Partition内部是不保证有序的,和HashPartition相比(会存在数据倾斜的问题),RangePartitioner尽量保证每个Partition中的数据量是均匀的

乘3的目的保证数据量特别小的分区能够抽取到足够的数据,同时保证数据量特别大的分区能够二次采样
```
val (numItems, sketched) = RangePartitioner.sketch(rdd.map(_._1), sampleSizePerPartition) //对Tuple的key进行采样:_._1
def sketch[K : ClassTag](rdd: RDD[K],sampleSizePerPartition: Int): (Long, Array[(Int, Long, Array[K])])

//返回(Long, Array[(Int, Long, Array[K])]);其中Int为分区的编号,long为分区中总元素有多少个;Array[K]从父RDD中每次分区中采样到的数据


(reservoir, l) //L为一个分区中的数据总和

```



水塘抽样:



