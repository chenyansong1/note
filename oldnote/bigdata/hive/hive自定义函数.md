---
title: hive自定义函数
categories: hive   
toc: true  
tag: [hive]
---



当Hive提供的内置函数无法满足你的业务处理需要时，此时就可以考虑使用用户自定义函数（UDF：user-defined function）。
# 自定义函数类别
* UDF  作用于单个数据行，产生一个数据行作为输出。（数学函数，字符串函数）
* UDAF（用户定义聚集函数）：接收多个输入数据行，并产生一个输出数据行。（count，max）

<!--more-->

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



# Transform实现调用脚本

&emsp;Hive的 TRANSFORM 关键字提供了在SQL中调用自写脚本的功能，适合实现Hive中没有的功能又不想写UDF的情况
下面这句sql就是借用了weekday_mapper.py对数据进行了处理.
```
CREATE TABLE u_data_new (
  movieid INT,
  rating INT,
  weekday INT,
  userid INT)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t';
 
add FILE weekday_mapper.py;
 
INSERT OVERWRITE TABLE u_data_new
SELECT
  TRANSFORM (movieid , rate, timestring,uid)        #传递给脚本的字段
  USING 'python weekday_mapper.py'            #调用的脚本
  AS (movieid, rating, weekday,userid)        #select 显示的字段名称
FROM t_rating;

```

其中weekday_mapper.py内容如下
```
#!/bin/python
import sys
import datetime
 
for line in sys.stdin:
  line = line.strip()
  movieid, rating, unixtime,userid = line.split('\t')
  weekday = datetime.datetime.fromtimestamp(float(unixtime)).isoweekday()
  print '\t'.join([movieid, rating, str(weekday),userid])

```

