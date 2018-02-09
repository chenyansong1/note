---
title: Spark运维管理之curl+REST API作业监控
categories: spark  
tags: [spark]
---




除了查看web ui上的统计来监控作业，还可以通过Spark提供的REST API来获取作业信息，并进行作业监控。REST API就给我们自己开发Spark的一些监控系统或平台提供了可能。REST API是通过http协议发送的，并给我们返回JSON格式的数据。因此无论你是用java，还是python，亦或是php，都可以获取Spark的监控信息。

<!--more-->

运行中的作业以及history server中的历史作业，都可以获取到信息

1、如果是要获取运行中的作业的信息，可以通过http://host:4040/api/v1/...的方式来获取
2、如果是要获取历史作业的信息，可以通过http://host:18080/api/v1/...的方式来获取

比如说，http://192.168.0.103:18080/api/v1/applications，就可以获取到所有历史作业的基本信息

以下是所有API的说明
```
/applications																	获取作业列表
/applications/[app-id]/jobs														指定作业的job列表
/applications/[app-id]/jobs/[job-id]											指定job的信息
/applications/[app-id]/stages													指定作业的stage列表
/applications/[app-id]/stages/[stage-id]										指定stage的所有attempt列表
/applications/[app-id]/stages/[stage-id]/[stage-attempt-id]						指定stage attempt的信息
/applications/[app-id]/stages/[stage-id]/[stage-attempt-id]/taskSummary			指定stage attempt所有task的metrics统计信息
/applications/[app-id]/stages/[stage-id]/[stage-attempt-id]/taskList			指定stage attempt的task列表
/applications/[app-id]/executors												指定作业的executor列表
/applications/[app-id]/storage/rdd												指定作业的持久化rdd列表
/applications/[app-id]/storage/rdd/[rdd-id]										指定持久化rdd的信息
/applications/[app-id]/logs														下载指定作业的所有日志的压缩包
/applications/[app-id]/[attempt-id]/logs										下载指定作业的某次attempt的所有日志的压缩包
```


当作业运行在yarn中时，每个作业都可能会尝试多次运行，所以上述的所有[app-id]都必须替换为[app-id]/[attempt-id]

这些API都非常便于让我们去基于它们开发各种监控系统或应用。特别是，spark保证以下几点: 

1、API永远不会因为版本的变更而更改
2、JSON中的字段用于不会被移除
3、新的API接口可能会被增加
4、已有API接口中可能会增加新的字段
5、API的新版本可能会作为新接口被添加进来。新版本的接口不要求向后兼容。
6、API版本可能会被删除掉，但是肯定是在一个相关的新API版本发布之后。

要注意的是，当查看运行中作业的UI时，applications/[app-id]还是需要提供的，尽管此时在那个4040端口上可能只有一个作业在运行。比如说，要查看正在运行的作业的job列表，可能需要使用以下API: http://host:4040/api/v1/applications/[app-id]/jobs
这主要是为了尽可能地复用API接口

实验

1、安装curl工具，来发送http请求: yum install -y curl
2、试一试以上的几个API，去获取历史作业的信息


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/rest_api_1.png)

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/rest_api_2.png)

standalone模式和yarn模式运行中的作业和历史作业的获取相同,也就是将http请求的端口换成作业的4040端口

```
spark-shell --master spark://192.168.0.103:7077

scala>val lines = sc.textFile("hdfs://192.168.0.103:9000/test/hello.txt")
scala>val words = lines.flatMap(_.split(" ")).map((_,1))
scala>val counts = words.reduceByKey(_+_)
scala>counts.collect

```
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/rest_api_3.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/rest_api_4.png)


