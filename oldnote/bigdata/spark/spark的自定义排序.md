---
title: spark的自定义排序
categories: spark  
tags: [spark]
---



```
package cn.itcast.spark.day3

import org.apache.spark.{SparkConf, SparkContext}


object OrderContext {
  implicit val girlOrdering  = new Ordering[Girl] {
    override def compare(x: Girl, y: Girl): Int = {
      if(x.faceValue > y.faceValue) 1
      else if (x.faceValue == y.faceValue) {
        if(x.age > y.age) -1 else 1
      } else -1
    }
  }
}


/**
  * Created by root on 2016/5/18.
  */
//sort =>规则 先按faveValue，比较年龄
//name,faveValue,age


object CustomSort {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("CustomSort").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val rdd1 = sc.parallelize(List(("yuihatano", 90, 28, 1), ("angelababy", 90, 27, 2),("JuJingYi", 95, 22, 3)))
    import OrderContext._
    val rdd2 = rdd1.sortBy(x => Girl(x._2, x._3), false)//false是降序
    println(rdd2.collect().toBuffer)
    sc.stop()
  }

}

/**
  * 第一种方式

case class Girl(val faceValue: Int, val age: Int) extends Ordered[Girl] with Serializable {//Serializable 是因为要走网络,所以需要实现序列化
  //比较规则
  override def compare(that: Girl): Int = {
    if(this.faceValue == that.faceValue) {
      that.age - this.age
    } else {
      this.faceValue -that.faceValue
    }
  }
}
  */

/**
  * 第二种，通过隐式转换完成排序
  */
case class Girl(faceValue: Int, age: Int) extends Serializable

```