---
title: SparkSQL函数之UDAF自定义聚合函数
categories: spark  
tags: [spark]
---



UDF(自定义函数),针对的是单行输入,返回一个输出
UDAF(自定义聚合函数),针对的是多行输入,进行聚合计算,返回一个输出


下面是一个自定义的分组统计字符串的个数的函数

```
package org.dt.spark

import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}

/**
  */
class StringCount extends UserDefinedAggregateFunction{
  // inputSchema:指的是输入数据的类型
  override def inputSchema: StructType = {
    StructType(Array(StructField("str", StringType, true)))
  }

  // bufferSchema:中间进行聚合时,所处理的数据的类型
  override def bufferSchema: StructType = {
    StructType(Array(StructField("count", IntegerType, true)))
  }

  // dataType:是函数返回值的类型
  override def dataType: DataType = {
    IntegerType
  }

  override def deterministic: Boolean = true

  // 为每个分组的数据执行初始化操作
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0
  }

  // 每个分组有新的值进来的时候,如何进行分组对应的聚合值的计算
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = buffer.getAs[Int](0) + 1
  }

  // 由于spark是分布式的,所以一个分组的数据,可能会在不同的节点上进行局部聚合,这个过程就是update
  // 但是,最后,在各个节点上的聚合值,要进行merge,也就是合并
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getAs[Int](0) + buffer2.getAs[Int](0)
  }

  // 个分组的聚合值,如何通过中间的缓存聚合值,返回一个最终的聚合值
  override def evaluate(buffer: Row): Any = {
    // 这里是直接将数据返回,没有做任何的处理
    buffer.getAs[Int](0)
  }
}


```

测试自定义聚合函数
```
 val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
 val sc = new SparkContext(sparkConf)
 val sqlContext = new SQLContext(sc)

 // 构造模拟数据rdd:
 val names = Array("leo", "marry", "jack", "marry", "jack", "marry", "jack")
 val namesRdd = sc.parallelize(names)

 // 将RDD转成DataFrame
 val namesRowRdd = namesRdd.map(Row(_))
 val structType = StructType(Array(StructField("name",StringType,true)))
 val namesDF = sqlContext.createDataFrame(namesRowRdd, structType)

 // 注册一张表
 namesDF.registerTempTable("names")

 // 定义和注册自定义函数
 // 函数名: strCount
 // 函数体: 是一个自定义的函数
 sqlContext.udf.register("strCount", new StringCount)

 // 使用自定义函数:统计相同名字出现的次数
 sqlContext.sql("select name, strCount(name) from names group by name")
   .collect
   .foreach(println)
 /* 打印结果
   [jack,3]
   [leo,1]
   [marry,3]
  */

```




