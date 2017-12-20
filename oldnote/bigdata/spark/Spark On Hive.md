---
title: Spark On Hive
categories: spark  
tags: [spark]
---


# 1.编译spark源码
Spark On Hive，通过spark sql模块访问和使用Hive，默认Spark预编译(pre-built)版不包含hive相关依赖，并不支持此功能，因此需要对spark源码进行重新编译，并进行相关的配置，
具体操作步骤参见: **spark源码编译.md**

# 2.安装hive

```
CREATE USER 'hive'@'%' IDENTIFIED BY '123456';
GRANT all privileges ON hive.* TO 'hive'@'%';
flush privileges;
```
 
# 3.将配置好的hive-site.xml放入$SPARK-HOME/conf目录下
 hive-site.xml文件:其实就是写:连接的数据库/jdbc驱动/用户名/密码
 
```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->
<configuration>
<property>
   <name>javax.jdo.option.ConnectionURL</name>
   <value>jdbc:mysql://172.16.0.1:3306/hive?createDatabaseIfNotExist=true</value>
   <description>JDBC connect string for a JDBC metastore</description>
</property>
 
<property>
   <name>javax.jdo.option.ConnectionDriverName</name>
   <value>com.mysql.jdbc.Driver</value>
   <description>Driver class name for a JDBC metastore</description>
</property>
 
<property>
   <name>javax.jdo.option.ConnectionUserName</name>
   <value>hive</value>
   <description>username to use against metastore database</description>
</property>
 
<property>
   <name>javax.jdo.option.ConnectionPassword</name>
   <value>123456</value>
   <description>password to use against metastore database</description>
</property>
</configuration>

```

# 4.写测试sql

```

#方式一:启动spark-shell时指定mysql连接驱动位置
bin/spark-shell \
--master spark://node1.itcast.cn:7077 \
--executor-memory 1g \
--total-executor-cores 2 \
--driver-class-path /usr/local/apache-hive-0.13.1-bin/lib/mysql-connector-java-5.1.35-bin.jar

sqlContext.sql("select * from spark.person limit 2")



#方式二:API 的方式
org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.hive.HiveContext
val hiveContext = new HiveContext(sc)
hiveContext.sql("select * from spark.person")


#方式三
bin/spark-sql \
--master spark://node1.itcast.cn:7077 \
--executor-memory 1g \
--total-executor-cores 2 \
--driver-class-path /usr/local/apache-hive-0.13.1-bin/lib/mysql-connector-java-5.1.35-bin.jar

#直接写sql就可以了


/*
因为连接mysql的时候需要指定驱动类,所以不管是在spark-shell还是在spark-sql中的时候,都要指定mysql驱动类,两种方式:
1.将驱动类放在spark的lib下
2.手动指定驱动类的位置:--driver-class-path        (上面 的方式都是手动指定的驱动的位置)
*/

```
 




