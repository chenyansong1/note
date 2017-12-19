---
title: SparkSQL实战之统计每日top3热点搜索词
categories: spark  
tags: [spark]
---



数据格式
```
日期,用户,搜索词,程序,平台,版本
2015-10-01	leo	water	beijing	android 1.0
```
要求:
1.筛选出符合查询条件的数据
2.统计出每天搜索uv排名前3的搜索词
3.按照每天的top3搜索词的搜索uv总次数,倒序排序
4.将数据保存到hive表中


实现思路
1.针对原始数据(HDFS文件),获取输入的rdd
2.使用filter,去针对输入RDD中的数据进行过滤,过滤出符合查询条件的数据
2.1.普通的做法:直接在filter算子函数中,使用外部的查询条件(Map),但是这样做的话,会将查询条件Map发送给每个task一个副本
2.2.优化后的做法:将查询条件,封装为Broadcast广播变量,在filter算子中使用Broadcast广播变量
3.将数据转换为"(日期_搜索词, 用户)" ,然后对其进行分组,其次再对分组后的数据在组内进行去重操作,得到每天每个搜索词的uv,最后的数据格式为:(日期_搜索词, uv)
4.将3中得到的Rdd转成Rdd[Row],将该Rdd转换成DataFrame
5.将DataFrame注册成临时表,使用spark sql开窗函数,来统计每天的uv数量排名前3的搜索词,以及他的搜索uv,最后获取的是一个DataFrame
6.将DataFrame转换为rdd,继续操作,按照每天日期进行分组,并进行映射,计算出每天的top3搜索词的搜索uv总数,然后将uv总数作为key,将每天的top3搜索词以及搜索次数拼接为一个字符串
7.按照每天的top3搜索总uv,进行倒序排列
8.整理格式:日期_搜索词_uv
9.再次映射为DataFrame,并将数据保存到hive表中




```
  val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
  val sc = new SparkContext(sparkConf)
  val sqlContext = new HiveContext(sc)

  import sqlContext.implicits._

  // 构造一份数据:查询条件(在实际的开发过程中,通过JavaWeb将查询条件入库到mysql中
  // 然后这里从mysql中提取查询条件
  val queryParamMap = Map(
    "city"->Array("beijing","hubei"),
    "platform"->Array("android"),
    "version"->Array("1.0","1.2")
  )
  // 将查询条件广播
  val queryParamMapBroadCast = sc.broadcast(queryParamMap)

  //val lineRdd = sc.textFile("hdfs://spark1:9000/keyword.txt")
  //val lineRdd = sc.textFile("C:\\Users\\Administrator\\Desktop\\sousuo.txt")
  // 使用查询条件,进行filter
  val filterRdd = lineRdd.filter{
    line=>
      val arr = line.split("\t")
      /*
      日期,用户,搜索词,程序,平台,版本
      2015-10-01	leo	water	beijing	android 1.0
       */
      val city, platform, version = (arr(2),arr(3), arr(4))

      val map = queryParamMapBroadCast.value
      val cities = map.get("city")
      if(!cities.toList.contains(city)){
        false
      }

      val platforms = map.get("platform")
      if(!platforms.toList.contains(platform)){
        false
      }

      val versions = map.get("version")
      if(!versions.toList.contains(version)){
        false
      }

      true
  }

  // 改变格式:(日期_搜索词,用户)
  val dateKeywordUserRdd = filterRdd.map{
      /*
      日期,用户,搜索词,程序,平台,版本
      2015-10-01	leo	water	beijing	android 1.0
       */
    line=>
      val arr = line.split("\t")
      val (date,user,keyword) = (arr(0),arr(1), arr(2))
      // 返回格式:(日期_搜索词,用户)
      (date+"_"+keyword, user)
  }
  // 进行分组,获取每天每个搜索词,有哪些有用(没有对用户去重)
  val dateKeywordUsesRdd = dateKeywordUserRdd.groupByKey()
  // 对每天每个搜索词的搜索用户,执行去重,获取其uv
  val dateKeywordUvsRdd = dateKeywordUsesRdd.map{
    line=>
      val dateKeyword = line._1
      val iter = line._2.toIterator
      val distinctUsers = mutable.Set[String]()
      while(iter.hasNext){
        val user = iter.next
        distinctUsers+=user
      }

      // 返回:(日期_搜索词,uv)
      val uv = distinctUsers.size
      (dateKeyword, uv)
  }

  // 返回:Row(日期,搜索词,uv)
  val dateKeywordUsesRowRdd = dateKeywordUvsRdd.map(line=>Row(line._1.split("_")(0),line._1.split("_")(1),line._2.toString.toInt))

  // 将每天每个搜索词的uv数据转成DataFrame
  val structFields = StructType(Array(
    StructField("date", StringType, true),
    StructField("keyword", StringType, true),
    StructField("uv", IntegerType, true)
  ))

  val dateKeywordUvDF = sqlContext.createDataFrame(dateKeywordUsesRowRdd, structFields)

  // 使用spark sql的开窗函数,统计每天搜索uv排名前3的热点搜索词
  dateKeywordUvDF.registerTempTable("daily_keyword_uv")
  val dailyTop3KeywordDF = sqlContext.sql("" +
    "select date, keyword, uv" +
    "from (" +
    "   select " +
    "       date," +
    "       keyworkd," +
    "       uv," +
    "       row_number() over (partition by date order by uv desc) rank" +
    "   from daily_keyword_uv" +
    ") tmp" +
    "where rank <= 3")

  dailyTop3KeywordDF.show


```

