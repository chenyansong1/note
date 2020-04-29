---
title: spark内核源码六之DAGScheduler原理剖析与源码分析
categories: spark  
tags: [spark]
---


在前面的wordcount程序中,我们可以看到触发action后,会调用dagScheduler.runJob运行job,下面我们来看DAGScheduler的源码



![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/spark内核源码六之DAGScheduler原理剖析与源码分析.png)





