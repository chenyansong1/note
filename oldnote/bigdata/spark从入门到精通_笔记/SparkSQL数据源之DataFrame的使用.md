---
title: SparkSQL数据源之DataFrame的使用
categories: spark  
tags: [spark]
---

DataFrame介绍

DataFrame可以理解为是以列的形式组织的,分布式的数据集合,他其实和关系型数据库中的表非常类似,但是底层做了很多的优化,DataFrame可以通过很多来源进行构建,包括:结构胡的数据文件,hive中的表,外部的关系型数据库,以及RDD


SQLContext
要使用Spark SQL,首先就得创建一个SQLContext对象,或者是他的子类的对象,比如HiveContext对象

```
val sc:SparkContext = ...
val sqlContext = new SQLContext(sc)
import sqlContext.implicits._
```

HiveContext
除了基本你的SQLContext以外,还可以使用它的子类-----HiveContext,HiveContext的功能除了包含SQLContext提供的所有功能之外,还包含了额外的专门针对Hive的一些功能,这些额外的功能包括:使用hiveSQL语法来编写和执行sql;使用hive中的UDF函数;从hive表中读取数据

要使用hiveContext,就必须预先安装好hive,SQLContext支持的数据源,hiveContext也同样支持-----而不只是支持hive,对于spark1.3.x以上的版本,都推荐使用hiveContext,因为其功能更加丰富和完善

spark sql还支持用spark.sql.dialect参数来设置sql的方言,使用SQLContext的setConf()即可进行设置,对于SQLContext,他只支持"sql"一种方言,对于hiveContext,他默认的方言是"hiveql"


创建DataFrame
使用SQLContext,可以从RDD,hive表或者其他数据源,来创建一个DataFrame,以下是一个使用json文件创建DataFrame的例子
```
val sc:SparkContext = ...
val sqlContext = new SQLContext(sc)
val df = sqlContext.read.json("hdfs://spark1:9000/students.json")
df.show()




=======students.json===========
{"id":1, "name":"leo", "age":18}
{"id":2, "name":"jack", "age":19}
{"id":3, "name":"merry", "age":17}

```


下面的代码是在windows本地的完整测试代码
```


object TopNBasic {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\Users\\Administrator\\Desktop\\hadoop\\")

    val sc = sparkContext("Transformation Operations")
    testMethod(sc)
    sc.stop()//停止SparkContext,销毁相关的Driver对象,释放资源
  }

  //在实际的生成中,我们是封装函数来进行逻辑的组织
  def sparkContext(name:String)={
    val conf = new SparkConf().setAppName(name).setMaster("local")
    //创建SparkContext,这是第一个RDD创建的唯一入口,是通往集群的唯一通道
    val sc = new SparkContext(conf)
    sc
  }

  def testMethod(sc: SparkContext): Unit ={
    val sqlContext = new SQLContext(sc)
    val df = sqlContext.read.json("C:\\Users\\Administrator\\Desktop\\xx.txt")
    df.show()

    /*
    打印结果:
    +---+---+-----+
    |age| id| name|
    +---+---+-----+
    | 18|  1|  leo|
    | 19|  2| jack|
    | 17|  3|merry|
    +---+---+-----+
     */
  }
}
```


提交到spark集群的shell
```
/usr/local/spark/bin/spark-submit \
--class cn.spark.study.sql.DataFrameCreate \
--num-executors 3 \
--driver-memory 100m \
--executor-cores 3 \
--files /usr/local/hive/conf/hive-site.xml \
--driver-class-path /usr/local/hive/lib/mysql-connctor-java-5.1.17.jar \
/root/spark-test-0.0.1-SNAPSHOT-jar-with-dependencies.jar \


```
其中会用到了hive的conf文件,和mysql的驱动jar包



DataFrame的常用操作
```
 val sqlContext = new SQLContext(sc)
 val df = sqlContext.read.json("C:\\Users\\Administrator\\Desktop\\xx.txt")
 // 打印DataFrame中的所有的数据
 df.show()
 // 打印DataFrame中的元数据信息(Schema)
 df.printSchema
 // 查询某一列所有的数据
 df.select("name").show
 // 查询某几列所有的数据,并对列进行计算(将age列加1)
 df.select(df.col("name"), df.col("age").plus(1)).show
 //df.select(df("name"), df("age") + 1).show

 // 对于某一列的值进行过滤
 df.filter(df.col("age").gt(18)).show
 //df.filter(df("age") > 18).show

 // 根据某一列进行分组,然后进行聚合
 df.groupBy(df.col("age")).count.show
 //df.groupBy("age").count.show


 /*
 打印结果:
 // 打印DataFrame中的所有的数据
 df.show()
   +---+---+-----+
   |age| id| name|
   +---+---+-----+
   | 18|  1|  leo|
   | 19|  2| jack|
   | 17|  3|merry|
   +---+---+-----+
   
   // 打印DataFrame中的元数据信息(Schema)
  df.printSchema
   root
    |-- age: long (nullable = true)
    |-- id: long (nullable = true)
    |-- name: string (nullable = true)
   
 // 查询某一列所有的数据
 df.select("name").show
   +-----+
   | name|
   +-----+
   |  leo|
   | jack|
   |merry|
   +-----+
   
 // 查询某几列所有的数据,并对列进行计算(将age列加1)
 df.select(df.col("name"), df.col("age").plus(1)).show
   +-----+---------+
   | name|(age + 1)|
   +-----+---------+
   |  leo|       19|
   | jack|       20|
   |merry|       18|
   +-----+---------+
   
 // 对于某一列的值进行过滤
 df.filter(df.col("age").gt(18)).show
   +---+---+----+
   |age| id|name|
   +---+---+----+
   | 19|  2|jack|
   +---+---+----+
   
 // 根据某一列进行分组,然后进行聚合
 df.groupBy(df.col("age")).count.show
   +---+-----+
   |age|count|
   +---+-----+
   | 19|    1|
   | 17|    1|
   | 18|    1|
   +---+-----+
  */

```