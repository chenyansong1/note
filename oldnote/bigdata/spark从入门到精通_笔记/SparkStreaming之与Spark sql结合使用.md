---
title: SparkStreaming之与Spark sql结合使用
categories: spark  
tags: [spark]
---


spark Streaming 最强大的地方在于,可以与spark core, spark sql整合使用的,下面来看看,如何将DStream中的RDD与spark sql结合起来使用



案例:每隔10秒,统计最近60秒的,每个种类的每个商品的点击次数,然后统计出每个种类的top3热门商品

实现:
1.每隔10秒,统计最近60秒的,每个种类的每个商品的点击次数:用到的是reduceByKeyAndWindow
2.统计出每个种类的top3热门商品:用到的是DStream.foreachRDD然后对每次生成的窗口中的RDD进行sql查询,取top3

```
 val conf = new SparkConf()
   .setAppName("Streaming")
   .setMaster("local[2]")

 // 每收集多长时间的数据就划分为一个batch进行处理,这里设置为1秒:Seconds(1)
 val ssc = new StreamingContext(conf,Seconds(5))

 // 输入日志的格式:username product1 catatory
 // zhangsan iphone  mobile_phone
 val productClickLogsDStream = ssc.socketTextStream("spark1", 9999)

 // 要统计:每个种类的每个商品的点击次数,所以我们将数据格式转成:(category_product,1)
 // 然后使用window操作,对窗口中的数据进行reduceByKey
 // 从而统计出一个窗口中的每个种类的每个商品的点击次数
 val productClickLogsPairs = productClickLogsDStream.map{
   line=>
     val arr = line.split(" ")
     (arr(2)+"_"+arr(1),1)
 }

 // 计算60s内每个种类的每个商品的点击次数统计
 val categoryProductCountsDStream = productClickLogsPairs.reduceByKeyAndWindow((x:Int,y:Int)=>x+y, Durations.seconds(60),Durations.seconds(10))
 //
 val categoryProductCountRowRdd = categoryProductCountsDStream.foreachRDD{
   categoryProductCountsRdd=>
     val rowRdd = categoryProductCountsRdd.map{
       categoryProductCount=>
         val (category,product) = categoryProductCount._1.split("_").toVector
         val count = categoryProductCount._2
         Row(category,product,count)
     }

     val structType = StructType(Array(
       StructField("category", StringType, true),
       StructField("product", StringType, true),
       StructField("click_count", IntegerType, true)
     ))
     val sqlContext = new HiveContext(rowRdd.sparkContext)
     val categoryProductCountsDF = sqlContext.createDataFrame(rowRdd,structType)
     categoryProductCountsDF.registerTempTable("product_click_log")

     // 使用spark sql执行top3热门商品的统计
     val top3ProductDF = sqlContext.sql("" +
       "select category, product, click_count" +
       "from (" +
       "   select " +
       "       category," +
       "       product," +
       "       click_count," +
       "       row_number() over (partition by category order by click_count desc) rank" +
       "    from product_click_log" +
       ") tmp" +
       "where rank <= 3")

     // 将数据保存到redis或者db中,然后在web中对数据进行展示
     // 但是这里只是测试,所以用show
     top3ProductDF.show
 }

 ssc.start()
 ssc.awaitTermination()
 ssc.stop()

```


spark-submit
```
/usr/local/spark/bin/spark-submit \
--class cn.xxx.Top3HotProduct
--num-executor 3 \
--driver-memory 100m \
--execuotr-memory 100m \
--executor-cores 3 \
--files /usr/local/hive/conf/hive-site.xml
--driver-class-path /usr/local/hive/lib/mysql-connector-java-5.1.17.jar \
/user/xx/spark-study-test.jar \


```



