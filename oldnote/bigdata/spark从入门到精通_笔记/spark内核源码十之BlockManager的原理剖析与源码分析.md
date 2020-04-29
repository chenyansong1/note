---
title: spark内核源码十之BlockManager的原理剖析与源码分析
categories: spark  
tags: [spark]
---





BlockManager的原理剖析

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/BlockManager的原理剖析.png)


BlockManager的源码分析

1.BlockManagerMaster
2.BlockManagerMasterEndpoint
3.BlockManager 
	initialize()
获取数据:
	doGetLocal()
	doGetRemote()
写入数据:
	doPut()


4.DiskStore
	getBytes()
	getValues()

5.MemoryStore
	putBytes()
	putIterator()
	tryToPut()





