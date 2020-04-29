---
title: Mongodb简介
categories: mongodb   
toc: true  
tags: [mongodb]
---




* mongodb文档数据库，存储的是文档（Bson->json的二进制化）
* 内部执行引擎为JS解释器，把文档存储成bson结构，在查询时，转换为JS对象，并可以通过熟悉的js语法来操作

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/mongodb/jianjie/1.png)


和传统数据库的比较：表下的每篇文档都可以有自己的结构（json对象可以有自己独特的属性和值）
 
![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/mongodb/jianjie/2.png)


一部电影，有影评，影评下面有回复，这样的多表的关系（在传统数据库中肯定是几张表），mongodb可以使用多个层级的json来存储

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/mongodb/jianjie/3.png)
 
这样的存储结构，就像一棵树，也像一个文档（JavaScript的document）

 
![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/mongodb/jianjie/4.png)
 




