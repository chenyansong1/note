---
title: 从环境搭建到编写WordCount程序并提交到集群
categories: spark  
tags: [spark]
---



&emsp;spark shell仅在测试和验证我们的程序时使用的较多，在生产环境中，通常会在IDE中编制程序，然后打成jar包，然后提交到集群，最常用的是创建一个Maven项目，利用Maven来管理jar包的依赖。

<!--more-->


# 1.创建一个项目

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/word_count/1.jpg)

# 2.选择Maven项目，然后点击next

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/word_count/2.png)
 


# 3.填写maven的GAV，然后点击next
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/word_count/3.png)
  
# 4.填写项目名称，然后点击finish

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/word_count/4.png)
 
 
# 5.创建好maven项目后，点击Enable Auto-Import

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/word_count/5.png)
 

# 6.配置Maven的pom.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>cn.itcast.spark</groupId>
    <artifactId>spark-mvn</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>1.7</maven.compiler.source>
        <maven.compiler.target>1.7</maven.compiler.target>
        <encoding>UTF-8</encoding>
        <scala.version>2.10.6</scala.version>
        <scala.compat.version>2.10</scala.compat.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-library</artifactId>
            <version>${scala.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_2.10</artifactId>
            <version>1.5.2</version>
        </dependency>

        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-streaming_2.10</artifactId>
            <version>1.5.2</version>
        </dependency>

        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>2.6.2</version>
        </dependency>
    </dependencies>

    <build>
        <sourceDirectory>src/main/scala</sourceDirectory>
        <testSourceDirectory>src/test/scala</testSourceDirectory>
        <plugins>
            <plugin>
                <groupId>net.alchim31.maven</groupId>
                <artifactId>scala-maven-plugin</artifactId>
                <version>3.2.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>testCompile</goal>
                        </goals>
                        <configuration>
                            <args>
                                <arg>-make:transitive</arg>
                                <arg>-dependencyfile</arg>
                                <arg>${project.build.directory}/.scala_dependencies</arg>
                            </args>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.18.1</version>
                <configuration>
                    <useFile>false</useFile>
                    <disableXmlReport>true</disableXmlReport>
                    <includes>
                        <include>**/*Test.*</include>
                        <include>**/*Suite.*</include>
                    </includes>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>2.3</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <filters>
                                <filter>
                                    <artifact>*:*</artifact>
                                    <excludes>
                                        <exclude>META-INF/*.SF</exclude>
                                        <exclude>META-INF/*.DSA</exclude>
                                        <exclude>META-INF/*.RSA</exclude>
                                    </excludes>
                                </filter>
                            </filters>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>cn.itcast.spark.WordCount</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>

```

# 7.将src/main/java和src/test/java分别修改成src/main/scala和src/test/scala，与pom.xml中的配置保持一致


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/word_count/6.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/word_count/7.png)


# 8.新建一个scala class，类型为Object

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/word_count/8.png)

# 9.编写spark程序
```
package cn.itcast.spark

import org.apache.spark.{SparkContext, SparkConf}

object WordCount {
  def main(args: Array[String]) {
    //创建SparkConf()并设置App名称
    val conf = new SparkConf().setAppName("WC")
    //创建SparkContext，该对象是提交spark App的入口
    val sc = new SparkContext(conf)
    //使用sc创建RDD并执行相应的transformation和action
    sc.textFile(args(0)).flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_, 1).sortBy(_._2, false).saveAsTextFile(args(1))
    //停止sc，结束该任务
    sc.stop()
  }
}

```

 

# 10.使用Maven打包

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/word_count/9.png)



# 11.选择编译成功的jar包，并将该jar上传到Spark集群中的某个节点上

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/word_count/10.png)

# 12.首先启动hdfs和Spark集群
```
启动hdfs
/usr/local/hadoop-2.6.1/sbin/start-dfs.sh
启动spark
/usr/local/spark-1.5.2-bin-hadoop2.6/sbin/start-all.sh

```

# 13.使用spark-submit命令提交Spark应用（注意参数的顺序）
```
/usr/local/spark-1.5.2-bin-hadoop2.6/bin/spark-submit \
--class cn.itcast.spark.WordCount \
--master spark://node1.itcast.cn:7077 \
--executor-memory 2G \
--total-executor-cores 4 \
/root/spark-mvn-1.0-SNAPSHOT.jar \
hdfs://node1.itcast.cn:9000/words.txt \
hdfs://node1.itcast.cn:9000/out
 
/*
--class cn.itcast.spark.WordCount \         //是启动类
/root/spark-mvn-1.0-SNAPSHOT.jar \        //jar包
hdfs://node1.itcast.cn:9000/words.txt \        //输入参数1 ,要读取的文件
hdfs://node1.itcast.cn:9000/out        //输入参数2 , 内容输出的文件

*/
```

# 14.查看程序执行结果
```
hdfs dfs -cat hdfs://node1.itcast.cn:9000/out/part-00000
(hello,6)
(tom,3)
(kitty,2)
(jerry,1)


```
