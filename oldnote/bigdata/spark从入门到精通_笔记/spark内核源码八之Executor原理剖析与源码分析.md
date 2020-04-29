---
title: spark内核源码八之Executor和Task原理剖析与源码分析
categories: spark  
tags: [spark]
---





## Executor的注册机制

在worker上启动Executor的时候,会在worker上启动一个进程:CoarseGrainedExecutorBackend,然后调用其初始化的方法
在其初始化的方法中会向driver去注册,在driver接收到注册消息之后,driver返回

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/executor的注册机制.png)




## 启动task机制

在**spark内核源码七之TaskScheduler原理剖析与源码分析**中最后发送的消息是:LaunchTask消息


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/spark内核源码八之Executor和Task原理剖析与源码分析.png)




## task原理图示
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/task原理剖析.png)

