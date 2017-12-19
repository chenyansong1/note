---
title: Linux一些重要的配置文件之系统运行级别文件etc/inittab
categories: Linux   
toc: true  
tags: [Linux重要配置文件]
---


设定系统启动时init进程将把系统设置成什么样的runlevel运行级别及加载相关的级别对应启动文件设置
```
[root@lamp01 chenyansong]# cat /etc/inittab |grep -vE "^#"
id:3:initdefault:
```


