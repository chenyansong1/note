---
title: SparkSQL数据源之Parquet数据源
categories: spark  
tags: [spark]
---

# 数据源Parquet的介绍
Parquet是面向分析性业务的列式存储格式,由Twitter和Cloudera合作开发,2015年5月从Apache的孵化器里毕业成为Apache顶级项目

列式存储和行式存储相比有哪些优势呢?
1.可以跳过不符合条件的数据,制只读取需要的数据,降低IO数据量
2.压缩编码可以降低磁盘存储空间,由于同一列的数据类型是一样的,可以使用更高效的压缩编码(例如:Run Length Encoding和Delta Encoding)进一步节约存储空间
3.只读取需要的列,支持向量运算,能够获取更好的扫描性能


# 数据源Parquet的编程方式加载数据
下面介绍的是Parquet数据源,使用编程的方式加载Parquet文件中的数据

案例:查询用户数据中的用户名
```
 val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
 val sc = new SparkContext(sparkConf)
 val sqlContext = new SQLContext(sc)

 // 使用format("parquet").load()是通用的方式
 //val userDF = sqlContext.read.format("parquet").load("C:\\Users\\Administrator\\Desktop\\users.parquet")
 // parquet()是针对parquet文件具体的读取,例如对于json文件,就有sqlContext.read.json();cvs,jdbc等是一样的
 val userDF = sqlContext.read.parquet("C:\\Users\\Administrator\\Desktop\\users.parquet")

 // 将DataFrame注册为临时表,然后使用sql查询需要的数据
 userDF.registerTempTable("users")
 val userNameDF = sqlContext.sql("select name from users")

 //对查询出来的DataFrame进行Transformation操作,然后打印
 // 在进行DF到rdd的转换的时候,一行数据转成rdd就是一个Array,所以用()去取数组元素
 val userName = userNameDF.rdd.map(row=>row(0).toString+"-xxx")
 userName.foreach(println)

 /*打印结果:
   Alyssa-xxx
   Ben-xxx
  */

```

# Parquet数据源的自动分区推断

表分区是一种常见的优化方式,比如hive中就提供了表分区的特性,在一个分区表中,不同分区的数据通常存储在不同的目录中,分区列的值通常就包含在了分区目录的目录名中,spark sql中的Parquet数据源支持自动根据目录名推断出分区信息,例如,如果将入口数据存储在分区表中,并且使用性别和国家作为分区列,那么目录结构可能如下所以:

```
tableName
	|--gender=male
		|--country=US
			....
		|--country=CN
			....

	|--gender=female
		|--country=US
			....
		|--country=CN
			....

```

如果将/tableName传入SQLContext.read.Parquet()或者SQLContext.read.load()方法,那么spark sql就会自动根据目录结构,推断出分区信息,是gender和country,即使数据文件中只包含了两列值:name和age,但是spark sql返回的DataFrame,调用printSchema()方法时,会打印四个列的值:name,age,country,gender,这就是自动分区推断你的功能

此外,分区列的数据类型,也是自动被推断出来的,目前,spark sql仅支持自动推断出数字类型和字符串类型,有时,用户也许不希望spark sql自动推断分区列的数据类型,此时只要设置一个配置即可,spark.sql.source.partitionColumnTypeInference.enabled,默认为true,即:自动推断分区列的类型,设置为false,即不糊自动推断类型,禁止自动推断分区列的类型时,所有分区列的类型就统一默认都是String

案例:自动推断用户数据的性别和国家

创建目录并上传Parquet文件
```
hadoop fs -mkdir /spark-study/users
hadoop fs -mkdir /spark-study/users/gender=male
hadoop fs -mkdir /spark-study/users/gender=male/country=US
hadoop fs -put users.parquet /spark-study/users/gender=male/country=US/users.parquet


```

测试
```
 val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
 val sc = new SparkContext(sparkConf)
 val sqlContext = new SQLContext(sc)

 val userDF = sqlContext.read.parquet("C:\\Users\\Administrator\\Desktop\\users\\gender=male\\country=US\\users.parquet")
 userDF.printSchema()
 userDF.show

```


# Parquet数据源之合并元数据

如同ProtocolBuffer,Avro,Thrift一样,Parquet也是支持元数据的合并的,用户可以在一开始就定义一个简单的元数据,然后随着业务需要,逐渐往元数据中添加更多的列,在这种情况下,用户可能会创建多个Parquet文件,有着多个不同的但是却相互兼容的元数据,Parquet数据源支持自动推断出这种情况,并且进行多个Parquet文件的元数据的合并

因为元数据合并是一种相对耗时的操作,而且在大多数情况下不是一种必要的特性,从spark 1.5.0版本开始,默认是关闭Parquet文件的自动合并元数据的特性的,可以通过以下的两种方式开启Parquet数据源的自动合并元数据的特性:
1.读取parquet文件时,将数据源的选项,mergeSchema设置为true
2.使用SQLContext.setConf()方法,将"spark.sql.parquet.mergeSchema"参数设置为true


案例:合并学生的基本信息和成绩信息的元数据
```
 val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
 val sc = new SparkContext(sparkConf)
 val sqlContext = new SQLContext(sc)

 import sqlContext.implicits._

 // 首先手动创建一个DataFrame,作为学生的基本信息数据,并将其写入到一个Parquet文件中
 val studentWithNameAge = Array(("leo",23),("jack",25))
 val studentWithNameAgeDF = sc.parallelize(studentWithNameAge).toDF("name", "age")
 studentWithNameAgeDF.write.save("C:\\Users\\Administrator\\Desktop\\student", SaveMode.Append)

 //创建第二个DataFrame,作为学生的成绩信息,并写入一个Parquet文件中
 val studentWithNameGrade = Array(("marry","A"),("tom","B"))
 val studentWithNameGradeDF = sc.parallelize(studentWithNameGrade).toDF("name", "grade")
 studentWithNameGradeDF.write.save("C:\\Users\\Administrator\\Desktop\\student", SaveMode.Append)

 /*
 第一个DataFrame的元数据和第二个DataFrame的元数据是不相同的,
 第一个包含了name和age两个列,第二个包含了name和grade两个列
 所以,这里期望的是,读取出来的表数据,自动合并两个问价你的元数据,
 出现3个列:name,age,grade
  */
 val studentDF = sqlContext.read.option("mergeSchema", true).parquet("C:\\Users\\Administrator\\Desktop\\student")
 studentDF.printSchema()
 studentDF.show


// 注意在spark1.5.0上是可以的,在spark2.1.0上不行,编译就报错

```
