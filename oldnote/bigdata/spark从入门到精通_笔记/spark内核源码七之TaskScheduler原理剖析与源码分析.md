---
title: spark内核源码七之TaskScheduler原理剖析与源码分析
categories: spark  
tags: [spark]
---



在**spark内核源码六之DAGScheduler原理剖析与源码分析**一文中,最后dagScheduler将stage分成taskSet,使用taskScheduler.submitTasks去提交
```
taskScheduler.submitTasks(new TaskSet())
```


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/spark内核源码七之TaskScheduler原理剖析与源码分析.png)




