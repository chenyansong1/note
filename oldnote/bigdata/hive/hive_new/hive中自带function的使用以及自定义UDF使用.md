---
title: hive中自带function的使用以及自定义UDF使用
categories: hive  
tags: [hive]
---


# hive自带的函数

```
#查看所有的
show functions;


#如果一个函数的名称记不全了,可以使用like去模糊匹配
hive (test_database)> show functions like 'spli*';
OK
tab_name
split
Time taken: 0.03 seconds, Fetched: 1 row(s)
hive (test_database)> 


#查看一个具体的函数的使用
desc function split;

desc function extended split;

hive (test_database)> desc function extended split;
OK
tab_name
split(str, regex) - Splits str around occurances that match regex
#我们可以参看这里的例子
Example:
  > SELECT split('oneAtwoBthreeC', '[ABC]') FROM src LIMIT 1;
  ["one", "two", "three"]
Time taken: 0.02 seconds, Fetched: 4 row(s)
hive (test_database)>


```

# 用户自定义函数

```

package com.example.hive.udf;
 
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
 
public final class Lower extends UDF {
  public Text evaluate(final Text s) {
    if (s == null) { return null; }
    return new Text(s.toString().toLowerCase());
  }
}

```

步骤:
1.继承org.apache.hadoop.hive.sl.exec.UDF
2.需要实现evaluate函数,evaluate函数支持**重载**


注意事项:
1.UDF必须要有返回值类型,可以返回null,但是返回类型不能为void
2.UDF中常用的Text/LongWritable等类型,不推荐使用java类型



# UDF开发实例
```
'1.先开发一个java类，继承UDF，并重载evaluate方法'
##############################################################
package cn.itcast.bigdata.udf
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
 
public final class Lower extends UDF{
public Text evaluate(final Text s){
  if(s==null){return null;}
  return new Text(s.toString().toLowerCase());
}
}
##############################################################

'2.打成jar包上传到服务器'

'3.将jar包添加到hive的classpath'
hive>add JAR /home/hadoop/udf.jar;

'4.创建临时函数与开发好的java class关联'
Hive>create temporary function tolowercase as 'cn.itcast.bigdata.udf.Lower';        #tolowercase 是临时函数的名字，as后面指定的是临时函数对应的java类

'5.即可在hql中使用自定义的函数tolowercase '
hive>select   tolowercase(name)   from student ;


#在0.13.x之后,另外一种方式是
1.将jar文件放入hdfs中
hive>dfs -mkdir -p /user/study/hive/jars/ ;
hive>dfs -put /opt/datas/udf.jar /user/study/hive/jars/ ;
2.将add jar 和创建函数的的步骤放在一起了
CREATE FUNCTION myfunc AS 'cn.itcast.bigdata.udf.Lower' USING JAR 'hdfs://hadoop-node-01:9000/user/study/hive/jars/udf.jar';

hive>show functions like 'myfunc';

```

 Json数据解析UDF开发
```
public class JsonParser extends UDF {
 
public String evaluate(String jsonLine) {
 
  ObjectMapper objectMapper = new ObjectMapper();
 
  try {
   MovieRateBean bean = objectMapper.readValue(jsonLine, MovieRateBean.class);
   return bean.toString();
  } catch (Exception e) {
 
  }
  return "";
}
 
}

```

 电话号码转换函数
```

public class ToProvince extends UDF{
 
static HashMap<String, String> provinceMap = new HashMap<String, String>();
static{
 
  provinceMap.put("138", "beijing");
  provinceMap.put("139", "shanghai");
  provinceMap.put("137", "dongjing");
  provinceMap.put("156", "huoxing");
}
 
//我们需要重载这个方法，来适应我们的业务逻辑
public String evaluate(String phonenbr){
  String res = provinceMap.get(phonenbr.substring(0, 3));
  return res==null?"wukong":res;
}
}
```



