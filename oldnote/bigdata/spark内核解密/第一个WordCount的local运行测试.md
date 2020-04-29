---
title: 第一个wordcount的local运行测试
categories: spark   
toc: true  
tag: [spark]
---


```
package org.dt.spark

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    /**
      * 之所以要这样写,因为在Windows本地执行的时候,会有null指针异常,
      * 网上说的解决方法:下载winutils.exe,然后将其放在一个目录下,如:C:\Users\Administrator\Desktop\hadoop\bin\winutils.exe
      * 然后指定bin\winutils.exe所在的目录,如下
      */
    System.setProperty("hadoop.home.dir", "C:\\Users\\Administrator\\Desktop\\hadoop\\")

    /**
      * 1.创建spark的配置对象SparkConf,设置Spark程序的运行时的配置信息
      * 例如:通过setMaster来设置程序要连接的spark集群的Master的URL,
      *   如果设置为local,则代表spark程序在本地运行
      */
    val conf = new SparkConf()  //创建sparkConf对象
    conf.setAppName("helloSpark") //设置应用程序的名称,在程序运行的监控界面可以看到名称
    conf.setMaster("local") //此时程序在本地运行,不需要安装spark集群

    /**
      * 2.创建SparkContext对象
      *   sparkContext是spark程序所有功能的唯一入口,无论是采用scala,java,python,R等都必须有一个sparkContext实例
      *   SparkContext的核心作用,初始化spark应用程序运行所需要的核心组件,包括:DAGScheduler,TaskScheduler,SchedulerBankend
      *   同时还会负责Spark程序往Master注册程序等
      *   Sparkcontext是整个Spark应用程序中最为至关重要的一个对象
      */
    val sc = new SparkContext(conf) //创建SparkContext对象,通过传入Sparkconf实例来定制Spark运行的具体参数和配置信息

    /**
      * 3.根据具体的数据来源(HDFS,HBase,Local FS, DB,S3等),通过sparkContext来创建ＲＤＤ
      *   RDD的创建有三种方式:
      *       根据外部的数据来源(例如HDFS)
      *       根据scala集合
      *       根据其他的RDD操作产生
      *   数据会被RDD划分称为一系列的Partitions,分配到每个Partition的数据属于一个Task的处理范畴
      */
    val rdd = sc.textFile("C://Users//Administrator//Desktop//test.txt", 1)
    /**
      * 4.对初始的RDD进行Transformation级别的处理,例如:Map,filter等高阶函数的编程
      *   进行具体的数据计算
      */
    val rdd2 = rdd.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).sortBy(_._2)
    rdd2.coalesce(1).saveAsTextFile("C://Users//Administrator//Desktop//test_re2223.txt")
    sc.stop()
  }
}


/*
为什么不能直接在IDEA中直接发布spark程序到spark集群中?
如果在IDEA中提交程序的话,那么IDEA机器就相当于Driver,此时的IDEA的内存就必须非常的强大
Driver要指挥集群中worker的运行,如果IDEA和spark的集群在不同的网络环境中,运行就会出现缓慢,可能出现任务丢失
同时也是不安全的,因为外部可以进入集群

一般实际情况下:会有专门的会和spark集群在同一个网络环境下的一台机器,远程连上这台机器,在上面做开发和提交程序
这台机器的配置和集群的worker的配置基本上是一致的
 */

```