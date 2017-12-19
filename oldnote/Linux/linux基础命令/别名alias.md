---
title: Linux基础命令之别名alias
categories: Linux   
toc: true  
tags: [Linux基础命令]
---




1. alias ddd=’df -Th’ 取一个别名
2. unalias ddd  取消别名
3. alias 看系统中的所有的别名
4. vim ~/.bashrc 永久的修改别名


别名生效的位置:
* 针对root用户,/root/.bashrc
* 所有用户生效:    /etc/bashrc 或 /etc/profile定义
* 生效: source /etc/profile


> 练习

```
/*
这样在Linux下输入cp命令实际上运行的是cp -i，加上一个 \ 符号或者写cp全路径/bin/cp就是让此次的cp命令不使用别名(cp -i)运行。
 
练习：将一个同名的文件拷贝到另外一个文件中，如何不提示是否覆盖的方法？因为如果直接使用cp，那么会调用上图中的方式“cp -i” -i表示提示，而下面两种方式是绕过别名
*/

方式一：
\cp  chenyansong.txt  cys/
 
方式二：
/bin/cp  chenyansong.txt  cys/

```

