---
title: Linux基础命令之tree
categories: Linux   
toc: true  
tags: [Linux基础命令]
---
# 范例
``` shell
显示到指定的层级
[root@linux-study yum.repos.d]# tree -L 1 /
 
 
范例：只显示目录
[root@linux-study yum.repos.d]# tree -Ld 1 /

```

# 安装
``` shell
yum install tree
# CentOS上面默认是没有tree命令的，使用yum install tree安装tree命令，下面是tree的使用方法：

```
 
 
# 语法选项
``` shell
#常见的用法:
tree -a    #显示所有
tree -d     #仅显示目录
tree -L n     #n代表数字..表示要显示几层...
tree -f     #显示完整路径..
 
#当然tree支持重定向至文件...
tree -L 4 >dirce.doc即可生成UTF8格式的文档..我们也可以在windows 下查看..
#注意:生成的TXT或其他文件在win下面打开时也为乱码...这时我们要选择字符编码为UTF-8..当然..UTF-8是你Linux下的默认字符集才可以......


```
