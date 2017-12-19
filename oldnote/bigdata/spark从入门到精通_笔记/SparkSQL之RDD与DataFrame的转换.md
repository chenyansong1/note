---
title: SparkSQL之RDD与DataFrame的转换
categories: spark  
tags: [spark]
---




为什么要将RDD转换为DataFrame,因为这样的话,我们就可以直接针对HDFS等任何可以构建为RDD的数据,使用spark sql进行sql查询了,这个功能是无比强大的,想象一下,针对HDFS中的数据,直接就可以使用sql进行查询

spark sql支持两种方式来将RDD转换为DataFrame
1.使用反射来推断包含了特定数据类型的RDD的元数据,这种基于反射的方式,代码比较简洁,当你已经知道你的RDD的元数据时,这是一种不错的方式
2.通过编程接口来创建DataFrame,你可以在程序运行时动态构建一份元数据,然后将其应用到已经存在的RDD上,这种方式的代码比较冗长,但是如果在编写程序时,还不知道RDD的元数据,只有在程序运行时,才能动态得知元数据,那么只能通过这种动态构建元数据的方式


**使用反射的方式推断元数据**
由于scala具有隐式转换的特点,所以spark sql的scala接口,是支持自动将包含了case class的RDD转换为DataFrame的,case class就定义了元数据,spark sql会通过反射读取传递给case class的参数的名称,然后将其作为列名,spark sql是支持将包含了嵌套数据结构的case class作为元数据的,比如包含了Array等

```
//要求Bean是可序列化的
case class Student(id:Int, name:String, age:Int) //extends Serializable

def RDD2DataFrameByReflection(): Unit ={
  val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
  val sc = new SparkContext(sparkConf)
  val sqlContext = new SQLContext(sc)

  // 在scala中使用反射方式,进行RDD到DataFrame的转换,需要手动导入一个隐式转换
  import sqlContext.implicits._

  // 对一行数据解析成Student对象返回
  def parseStudent(str: String): Student ={
    val fields = str.split(",")
    assert(fields.size == 3)
    Student(fields(0).trim.toInt, fields(1).toString, fields(2).trim.toInt)
  }

  // 因为前面已经导入了隐式的转换,所以这里可以将rdd转成DF
  val studentDF = sc.textFile("C:\\Users\\Administrator\\Desktop\\student.txt")
      .map(parseStudent)
      .toDF()

  // 将studentDF注册到成一张临时表
  studentDF.registerTempTable("students")

  // 操作这张临时表,返回的是一个DF
  val teenagerDF = sqlContext.sql("select * from students where age <= 18")
  // 将DF转回rdd
  val teenagerRdd = teenagerDF.rdd
  //teenagerRdd.foreach(println)
	/*因为打印的是数组,那么在row中取的时候,是去数组元素
	  [1,leo,17]
	  [2,marry,17]
	  [3,jack,18]
  	*/

  teenagerRdd.foreach{row=>println(Student(row(0).toString.toInt, row(1).toString, row(2).toString.toInt))}

  /*
  打印结果:
  Student(1,leo,17)
  Student(2,marry,17)
  Student(3,jack,18)
   */
}

```
上面的操作可以概括为下面的步骤:

1.将一个RDD通过反射的方式转换成为一个DataFrame(需要隐式转换);
2.将DataFrame注册成为一张表;
3.从表中查询数据,返回一个新的DataFrame;
4.将新的DataFrame转成rdd;
5.将RDD写会存储



**通过编程接口来创建DataFrame**

在编写程序时,还不知道RDD的元数据,只有在程序运行时,才能动态得知元数据,那么只能通过这种动态构建元数据的方式

```
 val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
  val sc = new SparkContext(sparkConf)
  val sqlContext = new SQLContext(sc)

  // 第一步:构造出元素为Row的普通RDD
  def parseStudent(str: String): Row ={
    val fields = str.split(",")
    assert(fields.size == 3)
    Row(fields(0).trim.toInt, fields(1).toString, fields(2).trim.toInt)
  }

  val studentRdd = sc.textFile("C:\\Users\\Administrator\\Desktop\\student.txt")
      .map(parseStudent)

  // 第二步:编程方式动态的构造元数据
  val structType = StructType(Array(
    StructField("id", IntegerType, true),
    StructField("name", StringType, true),
    StructField("age", IntegerType, true)
  ))

  // 第三步:进行RDD到DataFrame的转换
  val studentDF = sqlContext.createDataFrame(studentRdd, structType)

  // 使用DF
  studentDF.registerTempTable("students")

  //使用这张表
  val teenagerDF = sqlContext.sql("select * from students where age <=18")

  // 将DF转回RDD
  teenagerDF.rdd.foreach(println)

  /*
  打印结果:
  [1,leo,17]
  [2,marry,17]
  [3,jack,18]
   */

```

以上的操作主要是围绕着下面的方法进行的
```
createDataFrame(RDD[Row], StructType) :DataFrame = { /* compiled code */ }
```
1.首先将构造普通的RDD[Row]
2.构造StructType,定义元数据
3.调用createDataFrame方法返回DataFrame
4.将返回的DataFrame注册成为一张表
5.对表进行操作,返回新的DataFrame
6.将新的DataFrame转回RDD





