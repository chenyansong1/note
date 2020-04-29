---
title: 模拟datanode实现大文件分割的原理
categories: hadoop
toc: true
tag: [hadoop]
---




![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/moni_datanode.png)


* 将一个200M的文件分成两段(0-128,129-200)
* 开启两个socket去并发的读取文件,然后写入不同的机器