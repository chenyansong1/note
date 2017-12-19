---
title: RDD-API之groupBy
categories: spark  
tags: [spark]
---




```
package spark.examples.rddapi

import org.apache.spark.{Partitioner, SparkContext, SparkConf}

object GroupByTest_06 {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("CoGroupTest_05")
    val sc = new SparkContext(conf);
    val z1 = sc.parallelize(List((3, "A"), (6, "B1"), (7, "Z1"), (9, "E"), (7, "F"), (9, "Y"), (77, "Z"), (31, "X")), 3)
    /**
     * Return an RDD of grouped items. Each group consists of a key and a sequence of elements
     * mapping to that key. The ordering of elements within each group is not guaranteed, and
     * may even differ each time the resulting RDD is evaluated.
     *
     * Note: This operation may be very expensive. If you are grouping in order to perform an
     * aggregation (such as a sum or average) over each key, using [[PairRDDFunctions.aggregateByKey]]
     * or [[PairRDDFunctions.reduceByKey]] will provide much better performance.
     */
    //  def groupBy[K](f: T => K)(implicit kt: ClassTag[K]): RDD[(K, Iterable[T])] =  groupBy[K](f, defaultPartitioner(this))
    //根据指定的函数进行分组,分组得到的集合的元素类型是(K,V),K是分组函数的返回值，V是组内元素列表
    val r = z1.groupBy(x => if (x._1 % 2 == 0) "even" else "odd")
    r.collect().foreach(println)
    //结果：
    /*
    (even,CompactBuffer((6,B1)))
   (odd,CompactBuffer((3,A), (7,Z1), (9,E), (7,F), (9,Y), (77,Z), (31,X)))
     */

    //Partitioner是HashPartitioner
    val r2 = z1.groupBy(_._1 % 2)
    r2.collect().foreach(println)
    //结果：
    /*
    (0,CompactBuffer((6,B1)))
    (1,CompactBuffer((3,A), (7,Z1), (9,E), (7,F), (9,Y), (77,Z), (31,X)))
    */

    class MyPartitioner extends Partitioner {
      override def numPartitions = 3

      def getPartition(key: Any): Int = {
        key match {
          case null => 0
          case key: Int => key % numPartitions
          case _ => key.hashCode % numPartitions
        }
      }

      override def equals(other: Any): Boolean = {
        other match {
          case h: MyPartitioner => true
          case _ => false
        }
      }
    }
    println("=======================GroupBy with Partitioner====================")
    //分组的同时进行分区；分区的key是分组函数的计算结果？
    val r3 = z1.groupBy((x:(Int, String)) => x._1, new MyPartitioner())
    r3.collect().foreach(println)
    /*
    //6,3,9一个分区，7,31一个分区，77一个分区
    (6,CompactBuffer((6,B1)))
    (3,CompactBuffer((3,A)))
    (9,CompactBuffer((9,E), (9,Y)))
    (7,CompactBuffer((7,Z1), (7,F)))
    (31,CompactBuffer((31,X)))
    (77,CompactBuffer((77,Z)))
    */

  }


}


```
