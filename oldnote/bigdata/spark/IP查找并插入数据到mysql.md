---
title: IP查找并插入数据到mysql
categories: spark  
tags: [spark]
---



# 1.需求

&emsp;根据网关日志,查询用户的地址, 并在此基础上统计所有的地址的用户数量


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/ip_address/1.png)

<!--more-->
 


# 2.数据格式
 用户上网的网关日志
```
#下面是一条网关日志
20090121000132095572000|125.213.100.123|show.51.com|/shoplist.php?phpfile=shoplist2.php&style=1&sex=137|Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; Mozilla/4.0(Compatible Mozilla/4.0(Compatible-EmbeddedWB 14.59 http://bsalsa.com/ EmbeddedWB- 14.59  from: http://bsalsa.com/ )|http://show.51.com/main.php|

#125.213.100.123就是出网的IP,根据该IP就可以知道对应的归属地
```


 IP和所属地映射文件
```
#(起始IP,    结束IP,    起始IP的十进制,    结束IP的十进制,    所属地,    ....)
1.0.1.0|1.0.3.255|16777472|16778239|亚洲|中国|福建|福州||电信|350100|China|CN|119.306239|26.075302
1.0.8.0|1.0.15.255|16779264|16781311|亚洲|中国|广东|广州||电信|440100|China|CN|113.280637|23.125178
1.0.32.0|1.0.63.255|16785408|16793599|亚洲|中国|广东|广州||电信|440100|China|CN|113.280637|23.125178
1.1.0.0|1.1.0.255|16842752|16843007|亚洲|中国|福建|福州||电信|350100|China|CN|119.306239|26.075302
1.1.2.0|1.1.7.255|16843264|16844799|亚洲|中国|福建|福州||电信|350100|China|CN|119.306239|26.075302

```



# 3.代码实现

```
package cn.itcast.spark.day3

import java.sql.{Connection, Date, DriverManager, PreparedStatement}

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by root on 2016/5/18.
  */
object IPLocation {

  val data2MySQL = (iterator: Iterator[(String, Int)]) => {//遍历每个分区中的iteration
    var conn: Connection = null
    var ps : PreparedStatement = null
    val sql = "INSERT INTO location_info (location, counts, accesse_date) VALUES (?, ?, ?)"
    try {
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bigdata", "root", "123456")//拿到一个连接
      iterator.foreach(line => {//遍历每个分区中的元素
        ps = conn.prepareStatement(sql)
        ps.setString(1, line._1)
        ps.setInt(2, line._2)
        ps.setDate(3, new Date(System.currentTimeMillis()))
        ps.executeUpdate()//保存数据
      })
    } catch {
      case e: Exception => println("Mysql Exception")
    } finally {
      if (ps != null)
        ps.close()
      if (conn != null)
        conn.close()
    }
  }

  /**
    * 将IP(127.0.0.1)转成long
    */
  def ip2Long(ip: String): Long = {
    val fragments = ip.split("[.]")
    var ipNum = 0L
    for (i <- 0 until fragments.length){
      ipNum =  fragments(i).toLong | ipNum << 8L
    }
    ipNum
  }

  /*
  * 二分法查找
  * */
  def binarySearch(lines: Array[(String, String, String)], ip: Long) : Int = {
    var low = 0
    var high = lines.length - 1
    while (low <= high) {
      val middle = (low + high) / 2
      //lines(middle) 为(start_num_ip, end_num_ip, province)
      if ((ip >= lines(middle)._1.toLong) && (ip <= lines(middle)._2.toLong))
        return middle
      if (ip < lines(middle)._1.toLong)
        high = middle - 1
      else {
        low = middle + 1
      }
    }
    -1
  }

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local[2]").setAppName("IpLocation")
    val sc = new SparkContext(conf)
    //所有的IP映射表
    // 1.0.1.0|1.0.3.255|16777472|16778239|亚洲|中国|福建|福州||电信|350100|China|CN|119.306239|26.075302
    val ipRulesRdd = sc.textFile("c://ip.txt").map(line =>{
      val fields = line.split("\\|")//需要转义,因为|是正则的关键字
      val start_num = fields(2) //起始IP(long类型)
      val end_num = fields(3)//结束IP(long类型)
      val province = fields(6)//IP所属的省份
      (start_num, end_num, province)

    })
    //全部的ip映射规则
    val ipRulesArrary = ipRulesRdd.collect()//将iteration转成listbuffer

    //广播规则(将ip映射规则数据 发送到所有的worker)
    val ipRulesBroadcast = sc.broadcast(ipRulesArrary)

    //加载要处理的数据
    val ipsRDD = sc.textFile("c://access_log").map(line => {
      //数据格式: 20090121000132095572000|125.213.100.123|show.51.com|/shoplist.php?phpfile=shoplist2.php&style=1&sex=137|Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; Mozilla/4.0(Compatible Mozilla/4.0(Compatible-EmbeddedWB 14.59 http://bsalsa.com/ EmbeddedWB- 14.59  from: http://bsalsa.com/ )|http://show.51.com/main.php|
      val fields = line.split("\\|")
      fields(1)//取IP字段: 125.213.100.123
    })

    val result = ipsRDD.map(ip => {
      val ipNum = ip2Long(ip)
      //返回一个数组的下标
      val index = binarySearch(ipRulesBroadcast.value, ipNum) //ipRulesBroadcast.value 是一个Array
      val info = ipRulesBroadcast.value(index)//取数组中的某一个下标的元素
      //(ip的起始Num， ip的结束Num，省份名)
      info
    }).map(t => (t._3, 1)).reduceByKey(_+_)//对相同的省份进行统计计数

    //向MySQL写入数据
    result.foreachPartition(data2MySQL(_))//每个分区拿到一个数据库连接
    //println(result.collect().toBuffer)

    sc.stop()
  }
}
```

# 需要注意的一些坑
按照Java程序员使用JDBC的习惯，首先通过Class.forName("com.mysql.jdbc.Driver ")注册MySQL的JDBC驱动，但是在Scala中却不需要这么做，这么做还出错，包ClassNotFoundExeception（但是com.mysql.jdbc.Driver明明在classpath上）










