---
title: spark的wordcount的执行流程图解
categories: spark  
tags: [spark]
---


# 代码
```
package cn.itcast.spark.day1

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by root on 2016/5/14.
  */
object WordCount {
  def main(args: Array[String]) {
    //非常重要，是通向Spark集群的入口
    val conf = new SparkConf().setAppName("WC")
      .setJars(Array("C:\\HelloSpark\\target\\hello-spark-1.0.jar"))
      .setMaster("spark://node-1.itcast.cn:7077")
    val sc = new SparkContext(conf)

    //textFile会产生两个RDD：HadoopRDD  -> MapPartitinsRDD
    sc.textFile(args(0)).cache()
      // 产生一个RDD ：MapPartitinsRDD
      .flatMap(_.split(" "))
      //产生一个RDD MapPartitionsRDD
      .map((_, 1))
      //产生一个RDD ShuffledRDD
      .reduceByKey(_+_)
      //产生一个RDD: mapPartitions
      .saveAsTextFile(args(1))
    sc.stop()
  }
}

```

# 流程图


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/wc_liucheng/1.png)
