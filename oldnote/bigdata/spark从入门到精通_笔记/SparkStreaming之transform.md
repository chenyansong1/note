---
title: SparkStreaming之transform
categories: spark  
tags: [spark]
---


transform操作,应用在DStream上时,可以用于执行任意的RDD到RDD的转换操作,他可以用于实现,DStream API中所没有的操作,比如说,DStream API中,并没有提供将一个DStream中的每个batch,与一个特定的RDD进行join的操作,但是我们自己就可以使用transform操作来实现该功能

DStream.join(),只能join其他DStream,在DStream每个batch的RDD计算出来之后,回去跟其他DStream的RDD进行join


案例:实时黑名单过滤

```

    val conf = new SparkConf()
      .setAppName("Streaming")
      .setMaster("local[2]")

    // 每收集多长时间的数据就划分为一个batch进行处理,这里设置为1秒:Seconds(1)
    val ssc = new StreamingContext(conf,Seconds(1))

    // 构造模拟数据:黑名单RDD
    val blackListData = Array(
      ("tom", true)
    )
    val blckListRdd = ssc.sparkContext.parallelize(blackListData)

    // 日志的格式为:date  username
    val adsChickLogDStream = ssc.socketTextStream("spark1", 9999)
    val userAdsClickLogDStream = adsChickLogDStream.map{
      line=>
        // (username, line)
        (line.split(" ")(1), line)
    }

    // 执行transform操作,将每个batch的RDD,与黑名单RDD进行join,filter,map等操作
    // 实时黑名单过滤
    val validAdsClickLogDstream = userAdsClickLogDStream.transform(
      userAdsClickLogRdd=>{
        // (leo, ("20150101 leo", None))
        // (tom, ("20150101 tom", Some(true))
        // leftOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (V, Option[W]))]
        val joinedRdd = userAdsClickLogRdd.leftOuterJoin(blckListRdd)

        val filterRdd = joinedRdd.filter(
//          tuple=>{
//            if(tuple._2._2.getOrElse(false)){
//              false
//            }else{
//              true
//            }
//          }
          !_._2._2.getOrElse(false)
        )

        // 将:"20150101 leo"这一条数据返回
        filterRdd.map(_._2._1)
      }
    )

    // 写入kafka等消息中间件,
    // 然后再开发一个专门的后台服务,作为广告计费服务,执行实时的广告计费,这里就拿到了有效的广告点击
    validAdsClickLogDstream.print

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()

    /*
    其实使用transform,会将DStream中batch的每个RDD与指定的Rdd进行操作
     */


```