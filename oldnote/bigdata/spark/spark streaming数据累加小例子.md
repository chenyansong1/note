---
title: spark streaming数据累加小例子
categories: spark  
tags: [spark]
---


# 1.结构图
使用nc命令向spark streaming 发送数据

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/spark_streaming/1.png)

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/spark_streaming/2.png)

 

# 2.spark streaming 接收程序
```
package cn.itcast.spark.day5
 
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}
 
/**
  * Created by root on 2016/5/21.
  */
object StateFulWordCount {
 
    /**
    * String : 单词 hello
    * Seq[Int] ：单词在当前批次出现的次数
    * Option[Int] ： 以前的结果
    * */
 
  //分好组的数据
  val updateFunc = (iter: Iterator[(String, Seq[Int], Option[Int])]) => {
    //iter.flatMap(it=>Some(it._2.sum + it._3.getOrElse(0)).map(x=>(it._1,x)))
    //iter.map{case(x,y,z)=>Some(y.sum + z.getOrElse(0)).map(m=>(x, m))}
    //iter.map(t => (t._1, t._2.sum + t._3.getOrElse(0)))
    iter.map{ //这是一个模式匹配
      case(word, current_count, history_count) => (word, current_count.sum + history_count.getOrElse(0))
    }
  }
 
  def main(args: Array[String]) {
    //设置日志的级别
    LoggerLevels.setStreamingLogLevels()
    //StreamingContext
    val conf = new SparkConf().setAppName("StateFulWordCount").setMaster("local[2]")
    val sc = new SparkContext(conf)
    //updateStateByKey必须设置setCheckpointDir
    sc.setCheckpointDir("c://ck")//实际场景是使用HDFS
    val ssc = new StreamingContext(sc, Seconds(5))
 
    val ds = ssc.socketTextStream("172.16.0.11", 8888)
    //DStream是一个特殊的RDD
    //hello tom hello jerry
    val result = ds.flatMap(_.split(" ")).map((_, 1)).updateStateByKey(updateFunc, new HashPartitioner(sc.defaultParallelism), true)
 
    result.print()
 
    ssc.start()
 
    ssc.awaitTermination()
 
  }
}

```
 








