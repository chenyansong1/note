---
title: SparkSQL数据源之json数据源
categories: spark  
tags: [spark]
---


spark sql可以自动推断json文件的元数据,并且加载其数据,创建一个DataFrame,可以使用SQLContext.read.json()方法,针对一个元素类型为String的RDD,或者是一个json文件

但是要主要的是,这里使用的json文件与传统意义上的json文件是不一样的,每行都必须,也只能包含一个,单独的,自包含的,有效的json对象,不能让一个json对象分散在多行,否则会报错

案例:查询成绩为80分一身的学生的基本信息与成绩信息

```
//json文件
{"name":"Michael","score":88}
{"name":"Andy", "score":99}
{"name":"Justin", "score":77}



```

完整的代码
```
  val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
  val sc = new SparkContext(sparkConf)
  val sqlContext = new SQLContext(sc)

  // 针对json文件,创建DataFrame
  val studentScoresDF = sqlContext.read.json("C:\\Users\\Administrator\\Desktop\\studentscores.json")

  // 针对学生成绩的DataFrame,注册临时表,查询分数大于80的学生的姓名数组
  studentScoresDF.registerTempTable("student_scores")
  val goodStudentScoresDF = sqlContext.sql("select name,score from student_scores where score >= 80")
  val goodStudentNames = goodStudentScoresDF.rdd.map(row=>row(0).toString).collect

  // 构造学生信息数据
  val studentInfoJson = Array(
    "{\"name\":\"Michael\",\"age\":18}",
    "{\"name\":\"Andy\",\"age\":17}",
    "{\"name\":\"Justin\",\"age\":19}")
  val studentInfoJsonRDD = sc.parallelize(studentInfoJson)
  val studentInfoJsonDF = sqlContext.read.json(studentInfoJsonRDD)

  // 查询学生成绩大于80分的学生信息
  studentInfoJsonDF.registerTempTable("stduent_infos")

  // 构造sql语句
  var sql = "select name, age from stduent_infos where name in ("
  for(i <- 0 until goodStudentNames.length){
    sql += "'"+goodStudentNames(i)+"'"
    if(i < goodStudentNames.length-1){
      sql += ","
    }
  }
  sql += ")"

  val goodStudentInfoDF = sqlContext.sql(sql)
  // 将DataFrame转换为RDD[Tuple], 这样在进行join的时候,可以按照Tuple的key进行join
  val goodStudentInfoRdd = goodStudentInfoDF.rdd.map{row => (row(0).toString, row(1).toString.toInt)}

  // 将分数大于80分的学生的成绩信息和基本基本信息进行join
  val goodStudentsRdd = goodStudentScoresDF.rdd.map(row=>(row(0).toString, row(1).toString.toInt))
      .join(goodStudentInfoRdd)

  // 将RDD转换为DataFrame,并保存成json格式
  val goodStudents = goodStudentsRdd.map(t=>Row(t._1,t._2._1.toInt,t._2._2.toInt))//将RDD转成RDD[Row]
  val meta = StructType(Array(
    StructField("name", StringType, true),
    StructField("score", IntegerType, true),
    StructField("age", IntegerType, true)
  ))

  val goodStudentsDF = sqlContext.createDataFrame(goodStudents,meta)
  goodStudentsDF.write.format("json").save("C:\\Users\\Administrator\\Desktop\\studentscores.json1")

```

总结:
1.要实现两张表的join
2.而join的过程是RDD的join,所以要将两个DataFrame转成RDD(这里涉及到Row)
3.join之后是一个RDD,此时要将RDD转成DataFrame,保存成json格式(这里涉及到RDD转DataFrame的时候,要将RDD抓成RDD[Row]

