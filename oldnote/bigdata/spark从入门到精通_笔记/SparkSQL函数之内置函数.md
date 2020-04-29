---
title: SparkSQL函数之内置函数
categories: spark  
tags: [spark]
---



在spark1.5.x版本中,增加了一系列内置函数到DataFrame API中,并且实现了code generation的优化,与普通的函数不同,DataFrame的函数并不会执行后立即返回一个值,而是返回一个Column对象,用于在并行作业中进行求值,Column可以用在DataFrame的操作之中,比如select,filter,groupBy等,函数的输入值,也可以是Column


种类|函数
:---|:----
聚合函数|approxCountDistinct,avg,count,countDistinct,first,last,max,mean,min,sum,sumDistinct
集合函数|array_contains,explode,size,sort_array
日期/时间转换函数|日期时间转换unix_timestamp,from_unixtime,to_date,quarter,day,dayofyear,weekofyear,from_utc_timestamp,to_utc_timestamp;(从日期时间中提取字段:year,month,dayofmonth,hour,minute,second;(日期/时间计算)datediff,date_add,date_sub,add_months,last_day,next_day,months_between;(获取当前时间)current_date,current_timestamp,trunc,date_format
混合函数|array,isNaN,isnotnull,isnull,not,when,if,rand
字符串函数|concat,decode,encode,format_number,format_string,get_json_object,length,lpad,ltrim,lower,rpad,upper
窗口函数|cumeDist,denseRank,lag,lead,ntile,percentRank,rank,rowNumber



案例:根据每天的用户访问日志和购买日志,统计每日的uv和销售额

统计每日的UV

```
  val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
  val sc = new SparkContext(sparkConf)
  val sqlContext = new SQLContext(sc)

  // 导入隐式转换
  import sqlContext.implicits._

  // 构造用户访问日志数据,创建DataFrame
  val userAccessLog = Array(//date , userid
    "2015-10-01,1122",
    "2015-10-01,1123",
    "2015-10-01,1124",
    "2015-10-02,1122",
    "2015-10-02,1123",
    "2015-10-02,1122"
  )

  val userAccessLogRDD = sc.parallelize(userAccessLog)

  // 将RDD转成DataFrame,需要处理RDD的元素变成Row
  val userAccessLogRowRdd = userAccessLogRDD.map{
    line=>
      val arr = line.split(",")
      Row(arr(0), arr(1).toInt)
  }

  val structType = StructType(Array(
   StructField("date", StringType, true),
   StructField("userid", IntegerType, true)
  ))

  // 构建DataFrame
  val userAccessLogRowDF = sqlContext.createDataFrame(userAccessLogRowRdd, structType)

  // 内置函数的使用
  // 1.统计uv
  userAccessLogRowDF.groupBy("date")
    // 按照date聚合,在每组中对userid进行去重统计
    //.agg('date, functions.countDistinct('userid)).show
    /* 打印结果
    +----------+----------+----------------------+
    |      date|      date|count(DISTINCT userid)|
    +----------+----------+----------------------+
    |2015-10-02|2015-10-02|                     2|
    |2015-10-01|2015-10-01|                     3|
    +----------+----------+----------------------+
     */
  // 如果我们要取date,userid列
    .agg('date, functions.countDistinct('userid)).rdd
    .map{row=> (row(1),row(2))}
    .collect
    .foreach(println)
  /* 打印结果
  (2015-10-02,2)
  (2015-10-01,3)
   */

  /*
  聚合函数总结:
  1.对DataFrame调用groupBy()方法,对某一列进行分组
  2.调用agg()方法,第一个参数,必须是之前groupBy()方法中出现的字段
    agg()方法的第一个参数是用单引号开头的
  3.第二个参数,传入countDistinct,sum,first等,spark提供的内置函数
    内置函数中传入的参数也是用:单引号作为参数的
  4.所有的统计函数在:org.apache.spark.sql.functions中,所以要指定functions.xx
   */

```


统计销售额
```
 val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
 val sc = new SparkContext(sparkConf)
 val sqlContext = new SQLContext(sc)

 // 导入隐式转换
 import sqlContext.implicits._

 val userSaleLog = Array(
   "2015-10-01,55.55",
   "2015-10-01,66.55",
   "2015-10-02,77.55",
   "2015-10-02,55.55",
   "2015-10-03,55.55",
   "2015-10-03,55.55"
 )

 val userSaleLogRdd = sc.parallelize(userSaleLog)
 val userSaleLogRowRdd = userSaleLogRdd.map{
   line=>
     val arr = line.split(",")
     Row(arr(0), arr(1).toDouble)
 }

 val structType = StructType(Array(
   StructField("date", StringType, true),
   StructField("sale_amount", DoubleType, true)
 ))

 val userSaleLogDF = sqlContext.createDataFrame(userSaleLogRowRdd, structType)

 // 每日销售额的统计
 userSaleLogDF.groupBy("date")
   .agg('date, functions.sum('sale_amount)).rdd
   .map(row=> (row(1),row(2).toString.toDouble))
   .collect
   .foreach(println)
 /* 打印结果
 (2015-10-02,133.1)
 (2015-10-01,122.1)
 (2015-10-03,111.1)
  */

```

