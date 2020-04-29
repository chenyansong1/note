---
title: Linux基础命令之cd
categories: Linux   
toc: true  
tags: [Linux基础命令]
---

# cd  （切换路径）

``` shell
命令名称：cd
命令英文原意：change directory
命令所在路径：shell 内置命令
执行权限：所有用户
语法：cd [目录名]
功能描述：切换目录
```
 
&emsp;范例：

``` shell
cd  /tmp/Japan/boduo 切换到指定的目录
cd ..    回到上一级的目录

[root@linux-study cys_test]# cd /tmp
[root@linux-study tmp]# env | grep -i OLDPWD
OLDPWD=/cys_test
[root@linux-study tmp]# cd -        #直接切换到上一次目录下
/cys_test
[root@linux-study cys_test]# 
```

