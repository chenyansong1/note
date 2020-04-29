---
title: spark计算用户在小区停留时间最长的两个小区
categories: spark  
tags: [spark]
---



统计一个用户经常出现的2个位置

<!--more-->

# 1.数据格式
 手机访问基站的数据格式

```
18611132889,20160327081100,CC0710CC94ECC657A8561DE549D940E0,1
18688888888,20160327081200,CC0710CC94ECC657A8561DE549D940E0,1
18688888888,20160327081900,CC0710CC94ECC657A8561DE549D940E0,0
18611132889,20160327082000,CC0710CC94ECC657A8561DE549D940E0,0
18688888888,20160327171000,CC0710CC94ECC657A8561DE549D940E0,1
18688888888,20160327171600,CC0710CC94ECC657A8561DE549D940E0,0
18611132889,20160327180500,CC0710CC94ECC657A8561DE549D940E0,1
18611132889,20160327181500,CC0710CC94ECC657A8561DE549D940E0,0
/*手机号,        时间,        基站code,        (1是进入基站,0是出基站)*/

```

 基站信息:local_info.txt
```
9F36407EAD0629FC166F14DDE7970F68,116.304864,40.050645,6
CC0710CC94ECC657A8561DE549D940E0,116.303955,40.041935,6
16030401EAFB68F1E3CDF819735E1C66,116.296302,40.032296,6
/*基站code,    经度,    纬度*/
```

# 2.实现方式一
```

package cn.itcast.spark.day2
 
import org.apache.spark.{SparkConf, SparkContext}
 
/**
  * 根据日志统计出每个用户在站点所呆时间最长的前2个的信息
  *   1, 先根据"手机号_站点"为唯一标识, 算一次进站出站的时间, 返回(手机号_站点, 时间间隔)
  *   2, 以"手机号_站点"为key, 统计每个站点的时间总和, ("手机号_站点", 时间总和)
  *   3, ("手机号_站点", 时间总和) --> (手机号, 站点, 时间总和)
  *   4, (手机号, 站点, 时间总和) --> groupBy().mapValues(以时间排序,取出前2个) --> (手机->((m,s,t)(m,s,t)))
  */
object UserLocation {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("ForeachDemo").setMaster("local[2]")    //local为本地运行
    val sc = new SparkContext(conf)
    //sc.textFile("c://bs_log").map(_.split(",")).map(x => (x(0), x(1), x(2), x(3)))
    val mbt = sc.textFile("c://bs_log").map( line => {
      val fields = line.split(",")
      val eventType = fields(3)
      val time = fields(1)
      val timeLong = if(eventType == "1")  -time.toLong else time.toLong
      (fields(0) + "_"  + fields(2), timeLong)
    })
    //println(mbt.collect().toBuffer)
    //(18611132889_9F36407EAD0629FC166F14DDE7970F68,54000)
    val rdd1 = mbt.groupBy(_._1).mapValues(_.foldLeft(0L)(_ + _._2))
 
    //(18611132889,9F36407EAD0629FC166F14DDE7970F68,54000)
    val rdd2 = rdd1.map( t => {
      val mobile_bs = t._1
      val mobile = mobile_bs.split("_")(0)
      val lac = mobile_bs.split("_")(1)
      val time = t._2
      (mobile, lac, time)
    })
    val rdd3 = rdd2.groupBy(_._1)
    //ArrayBuffer((18688888888,List((18688888888,16030401EAFB68F1E3CDF819735E1C66,87600), (18688888888,9F36407EAD0629FC166F14DDE7970F68,51200))), (18611132889,List((18611132889,16030401EAFB68F1E3CDF819735E1C66,97500), (18611132889,9F36407EAD0629FC166F14DDE7970F68,54000))))
    val rdd4 = rdd3.mapValues(it => {//it是一个itera
      //List((18688888888,16030401EAFB68F1E3CDF819735E1C66,87600), (18688888888,9F36407EAD0629FC166F14DDE7970F68,51200)))
      it.toList.sortBy(_._3).reverse.take(2)//对时间差进行排序,然后取top2
    })
    println(rdd4.collect().toBuffer)
    sc.stop()
  }
}

```

# 3.实现方式二
```
package cn.itcast.spark.day2

import org.apache.spark.{SparkConf, SparkContext}

/**
  * ((fields(0),fields(2)), timeLong) -->reduceByKey(_+_).map --> (lac, (mobile, time))
  *     -->rdd1.join(rdd2).map-->(mobile, lac, time, x, y)
  *     --> groupBy().mapValues(以时间排序,取出前2个) --> (手机->((m,s,t)(m,s,t)))
  * Created by root on 2016/5/16.
  */
object AdvUserLocation {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("AdvUserLocation").setMaster("local[2]")  //local为本地运行
    val sc = new SparkContext(conf)
    val rdd0 = sc.textFile("c://bs_log").map( line => {
      val fields = line.split(",")
      val eventType = fields(3)
      val time = fields(1)
      val mobile = fields(0)
      val location_area_code = fields(2)
      val timeLong = if(eventType == "1")  -time.toLong else time.toLong
      //将(mobile, location_area_code) 作为key
      ((mobile, location_area_code), timeLong)
    })
    val rdd1 = rdd0.reduceByKey(_+_).map(t => {
      val mobile = t._1._1
      val lac = t._1._2 //location_area_code
      val time = t._2
      (lac, (mobile, time))
    })

    val rdd2 = sc.textFile("c://lac_info.txt").map(line => {//基站信息表
      val f = line.split(",")
      //(基站ID， （经度，纬度）)
      (f(0), (f(1), f(2)))
    })

    //rdd1.join(rdd2)-->(CC0710CC94ECC657A8561DE549D940E0,((18688888888,1300),(116.303955,40.041935)))
    val rdd3 = rdd1.join(rdd2).map(t => {
      val lac = t._1
      val mobile = t._2._1._1
      val time = t._2._1._2
      val x = t._2._2._1
      val y = t._2._2._2
      (mobile, lac, time, x, y)
    })
    //rdd4分组后的
    val rdd4 = rdd3.groupBy(_._1)//  (mobile, List((mobile, lac, time, x, y), (mobile, lac, time, x, y)) )
    val rdd5 = rdd4.mapValues(it => {
      it.toList.sortBy(_._3).reverse.take(2)
    })

    println(rdd1.join(rdd2).collect().toBuffer)
    //    println(rdd5.collect().toBuffer)
    rdd5.saveAsTextFile("c://out")
    sc.stop()
  }
}

```










