---
title: Linux基础命令之tr转换或删除字符
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


&emsp;从标准输入中替换、缩减和/或删除字符，并将结果写到标准输出。

```Shell
[root@lamp01 chenyansong]ll
总用量 4
drwxr-xr-x. 3 root root 4096 2月  13 16:00 tardir
[root@lamp01 chenyansong]ls -l|cut -c 2-10|tr rwx- 4210
用量 4
421401401


#将ab换成AB
tr ab AB 
>输入内容

#
tr 'ab' 'AB' < /etc/passwd
#连续替换
tr 'a-z' 'A-Z' </etc/passwd

#删除'a' or 'b'
tr -d 'ab' < /etc/passwd


```



