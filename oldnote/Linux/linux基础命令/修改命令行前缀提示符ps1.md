---
title: Linux基础命令之修改命令行前缀提示符ps1
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


ps1 修改 [root@linux-study /]# 显示

```
[root@lamp01 tardir]# set|grep -i ps1
PS1='[\u@\h \W]\$ '
_=ps1
[root@lamp01 tardir]# 


[root@lamp01 tardir]# PS1='[\u@\h \W \t]'
[root@lamp01 tardir 15:24:33]

```