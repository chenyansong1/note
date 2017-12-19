---
title: Linux基础命令之tr转换或删除字符
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


&emsp;从标准输入中替换、缩减和/或删除字符，并将结果写到标准输出。

```
[root@lamp01 chenyansong]ll
总用量 4
drwxr-xr-x. 3 root root 4096 2月  13 16:00 tardir
[root@lamp01 chenyansong]ls -l|cut -c 2-10|tr rwx- 4210
用量 4
421401401

```


