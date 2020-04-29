---
title: wordcount的运行原理及源码解读
categories: spark   
toc: true  
tag: [spark]
---

下面是分析wordcount过程中产生的RDD及stage

<!--more-->

```
package org.dt.spark
import org.apache.spark.{SparkConf, SparkContext}

object WordCountOrdered {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\Users\\Administrator\\Desktop\\hadoop\\")

    val conf = new SparkConf()  //创建sparkConf对象
    conf.setAppName("helloSpark") //设置应用程序的名称,在程序运行的监控界面可以看到名称
    conf.setMaster("local") //此时程序在本地运行,不需要安装spark集群

    val sc = new SparkContext(conf) //创建SparkContext对象,通过传入Sparkconf实例来定制Spark运行的具体参数和配置信息

    val rdd = sc.textFile("C://Users//Administrator//Desktop//test.txt", 1)
    val rdd2 = rdd.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).sortBy(_._2)
    rdd2.saveAsTextFile("C://Users//Administrator//Desktop//test_result")
    sc.stop()
  }

}


```


![](/assert/img/bigdata/spark内核解密/wordcount的运行原理及源码解读.png)


一个很大的文件存在HDFS上,1T吧,然后要对这个文件进行wordcount,
现在有四台机器,那么会将数据发送到四台机器,具体每台机器多少数据,看具体的情况


