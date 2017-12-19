---
title: Storm编程模型（各组件：dataSource、spout、bolt、Tuple）
categories: storm   
toc: true  
tag: [storm]
---




一条数据在storm中是如何流动的呢?一张图说明:

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/component/component.png)


* DataSource：外部数据源
* Spout：接受外部数据源的组件，将外部数据源转化成Storm内部的数据，以Tuple为基本的传输单元下发给Bolt
* Bolt:接受Spout发送的数据，或上游的bolt的发送的数据。根据业务逻辑进行处理。发送给下一个Bolt或者是存储到某种介质上。介质可以是* Redis可以是mysql，或者其他。
* Tuple：Storm内部中数据传输的基本单元，里面封装了一个List对象，用来保存数据。
* StreamGrouping:数据分组策略
* 7种：shuffleGrouping(Random函数),Non Grouping(Random函数),FieldGrouping(Hash取模)、Local or ShuffleGrouping 本地或随机，优先本地。


