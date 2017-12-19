---
title: SparkSQL函数之UDF
categories: spark  
tags: [spark]
---


```
  val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
  val sc = new SparkContext(sparkConf)
  val sqlContext = new SQLContext(sc)

  // 构造模拟数据rdd
  val names = Array("leo", "marry", "jack")
  val namesRdd = sc.parallelize(names)

  // 将RDD转成DataFrame
  val namesRowRdd = namesRdd.map(Row(_))
  val structType = StructType(Array(StructField("name",StringType,true)))
  val namesDF = sqlContext.createDataFrame(namesRowRdd, structType)

  // 注册一张表
  namesDF.registerTempTable("names")

  // 定义和注册自定义函数
  // 函数名: strLen
  // 函数体:这里是一个匿名函数:(str:String)=> str.length
  sqlContext.udf.register("strLen", (str:String)=> str.length)

  // 使用自定义函数
  sqlContext.sql("select name, strLen(name) from names")
    .collect
    .foreach(println)
  /* 打印结果
  [leo,3]
  [marry,5]
  [jack,4]
   */

```



