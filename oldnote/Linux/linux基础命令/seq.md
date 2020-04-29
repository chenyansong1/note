---
title: Linux基础命令之seq
categories: Linux   
toc: true  
tags: [Linux基础命令]
---

# 语法

``` shell
seq [OPTION] ... LAST
seq [OPTION] ... FIRST LAST
seq [OPTION] ... FIRST INCREMENT LAST
```

# 实例
>  生成指定的范围的序列

``` shell
[root@linux-study ~]# seq 5
1
2
3
4
5
 
[root@linux-study ~]# seq 3 5    #指定开始和结束
3
4
5
 
[root@linux-study ~]# seq 1 2 5    #2是指定的增量
1
3
5
```
> 指定分隔符

``` shell
       -s, --separator=STRING
              use STRING to separate numbers (default: \n)    #默认的分隔符是\n(换行)

#测试
[root@linux-study ~]# seq -s '@' 5
1@2@3@4@5

```

> 生成的序列添加到指定的文件中

``` shell
 [root@linux-study cys_test]# seq 55 >seq_n
```
> 生成指定宽度的序列

``` shell
        -w, --equal-width
              equalize width by padding with leading zeroes

#测试
[root@lamp01 ~]# seq -w 10
01
02
03
04
05
06
07
08
09
10

```