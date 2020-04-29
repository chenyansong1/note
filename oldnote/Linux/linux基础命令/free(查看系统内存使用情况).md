---
title: Linux基础命令之free(查看系统内存使用情况)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



```
[root@lamp01 chenyansong]# free -m
             total       used       free     shared    buffers     cached
Mem:          1004        155        849          0         53         49
-/+ buffers/cache:         52        951
Swap:          511          0        511

/*
实际空闲的内存是951,他会将部分内存当做缓存使用,951是去掉缓存的部分
-/+ buffers/cache意思就是：buffers和cache占用了部分内存
（849+53+49 就是实际空闲的内存951是去掉了buffers和cached之后的可用内存）


buffers为写入缓冲区
cache为读取数据的缓冲区
硬盘是机械的,无论是写入还是读取都太慢了,所以读取和写入都是使用了缓存技术
*/

```