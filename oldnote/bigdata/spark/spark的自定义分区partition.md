---
title: spark的自定义分区partition
categories: spark  
tags: [spark]
---



```
package cn.itcast.spark.day3

import java.net.URL

import org.apache.spark.{HashPartitioner, Partitioner, SparkConf, SparkContext}

import scala.collection.mutable

/**
  * Created by root on 2016/5/18.
  */
object UrlCountPartition {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("UrlCountPartition").setMaster("local[2]")
    val sc = new SparkContext(conf)

    //rdd1将数据切分，元组中放的是（URL， 1）
    val rdd1 = sc.textFile("c://itcast.log").map(line => {
      val f = line.split("\t")
      (f(1), 1)
    })
    val rdd2 = rdd1.reduceByKey(_ + _)

    val rdd3 = rdd2.map(t => {
      val url = t._1
      val host = new URL(url).getHost
      (host, (url, t._2))
    })

    val ints = rdd3.map(_._1).distinct().collect()

    val hostParitioner = new HostParitioner(ints)

//    val rdd4 = rdd3.partitionBy(new HashPartitioner(ints.length))

    val rdd4 = rdd3.partitionBy(hostParitioner).mapPartitions(it => {//partitionBy通过什么进行分区,mapPartitions遍历每一个分区
      it.toList.sortBy(_._2._2).reverse.take(2).iterator //在每一个分区中进行排序,取top2
    })

    rdd4.saveAsTextFile("c://out4")


    //println(rdd4.collect().toBuffer)
    sc.stop()

  }
}

/**
  * 决定了数据到哪个分区里面
  * @param ins
  */
class HostParitioner(ins: Array[String]) extends Partitioner {

  val parMap = new mutable.HashMap[String, Int]()
  var count = 0
  for(i <- ins){
    parMap += (i -> count)
    count += 1
  }

  override def numPartitions: Int = ins.length

  override def getPartition(key: Any): Int = {
    parMap.getOrElse(key.toString, 0)
  }
}


```


