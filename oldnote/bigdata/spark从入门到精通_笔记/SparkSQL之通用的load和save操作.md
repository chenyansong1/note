---
title: SparkSQL之通用的load和save操作
categories: spark  
tags: [spark]
---


# 通用的load和save操作

对于spark sql的DataFrame来说,无论是从什么数据源创建出来的DataFrame,都有一些共同的load和save操作,load操作主要用于加载数据,创建出来DataFrame;save操作,主要用于将DataFrame中的数据保存到文件中

```
// users.parquet是使用parquet面向列存储的文件,用文本打开是乱码
val df = sqlContext.read.load("users.parquet")

df.select("name", "favorite_color").write.save()
```

完整的代码如下
```

  val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
  val sc = new SparkContext(sparkConf)
  val sqlContext = new SQLContext(sc)

  val userDF = sqlContext.read.load("C:\\Users\\Administrator\\Desktop\\users.parquet")
  userDF.printSchema
  userDF.show
  /*
  打印结果:
  root
   |-- name: string (nullable = true)
   |-- favorite_color: string (nullable = true)
   |-- favorite_numbers: array (nullable = true)
   |    |-- element: integer (containsNull = true)

  +------+--------------+----------------+
  |  name|favorite_color|favorite_numbers|
  +------+--------------+----------------+
  |Alyssa|          null|  [3, 9, 15, 20]|
  |   Ben|           red|              []|
  +------+--------------+----------------+
   */

  userDF.select("name", "favorite_color").write.save("C:\\Users\\Administrator\\Desktop\\nameAndFavoriteColor.parquet")
// 可以看到在桌面生成了一个nameAndFavoriteColor.parquet文件夹

```


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/nameAndFavoriteColor.parquet.png)

验证上面的保存文件是否保存成功
```
  val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
  val sc = new SparkContext(sparkConf)
  val sqlContext = new SQLContext(sc)

// 使用上面的保存路径
  val userDF = sqlContext.read.load("C:\\Users\\Administrator\\Desktop\\nameAndFavoriteColor.parquet")
  userDF.printSchema
  userDF.show

/*
打印结果:
root
 |-- name: string (nullable = true)
 |-- favorite_color: string (nullable = true)

+------+--------------+
|  name|favorite_color|
+------+--------------+
|Alyssa|          null|
|   Ben|           red|
+------+--------------+
所以说明上面的保存是成功的
*/


```


# 手动指定数据源的类型

可以手动指定用来操作的数据源类型,数据源通常需要使用其全限定名来指定,比如parquet是org.apache.spark.sql.parquet,但是spark sql 内置了一些数据源类型,比如json,parquet,jdbc等等,实际上,通过这个功能,就可以在不同类型的数据源之间进行转换了,比如将json文件中的数据保存到parquet文件中,默认情况下,如果不指定数据源的类型,那么就是parquet

```
 val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
 val sc = new SparkContext(sparkConf)
 val sqlContext = new SQLContext(sc)

 val userDF = sqlContext.read.format("json").load("C:\\Users\\Administrator\\Desktop\\people.json")
//保存为parquet
 userDF.select("name").write.format("parquet").save("C:\\Users\\Administrator\\Desktop\\peopleName.parquet")
 

```


# save mode
spark sql对于save操作,提供了不同的savemode,主要用来处理,当目标位置,已经有数据时,应该如何处理,而且save操作并不会执行行锁操作,并且不是原子的,因此是有一定风险出现脏数据的


Save Mode|意义
:--------|:--------
SaveMode.ErrorIfExists(默认)|如果目标位置已经存在数据,那么就抛出一个异常
SaveMode.Append|如果目标位置已经存在数据,那么将数据追加进去
SaveMode.Overwrite|如果目标位置已经存在数据,那么就将已经存在的数据删除,用新数据进行覆盖
SaveMode.Ignore|如果目标位置已经存在数据,那么就忽略,不做任何操作
