---
title: shell之算术运算
categories: shell   
toc: true  
tags: [shell]
---



[TOC]



# 1.(())
```
#语法:
（表达式1,表达式2…））


((a+1,b++,c++))

/*
特点：
1、在双括号结构中，所有表达式可以像c语言一样，如：a++,b--等。
2、在双括号结构中，所有变量可以不加入：“$”符号前缀。
3、双括号可以进行逻辑运算，四则运算
4、双括号结构 扩展了for，while,if条件测试运算
5、支持多个表达式运算，各个表达式之间用“，”分开，在for中使用分号：for((i=0;i<5;i++))


*/
[root@lnmp02 shell]# ((a=1+1))
[root@lnmp02 shell]# echo $a
2

[root@lnmp02 shell]# ((a=1 + 1)) #+号两边有空格
[root@lnmp02 shell]# echo $a   
2
[root@lnmp02 shell]# ((a = 21 + 1)) #+和=之间都有空格
[root@lnmp02 shell]# echo $a      
22
[root@lnmp02 shell]# (( a = 21 + 12 ))  #空号内有空格
[root@lnmp02 shell]# echo $a         
33

[root@lamp01 chenyansong]# echo "`seq -s '+' 10`"="$((`seq -s '+' 10`))"
1+2+3+4+5+6+7+8+9+10=55

```



# 2.let
```
#语法
let  expression

#使用let 引用变量无需在添加$前缀
a=4;
b=5;
let result=a+b
echo $result
let a++
echo $a
let a--
echo $a
let a+=100
echo $a
let "x=x/(y+1)"
#注意双引号被用来忽略括号的特殊含义。同样如果你希望使用空格来分隔操作符和操作符的时候，就必须使用双引号，当使用逻辑和关系操作符，(!,<=,>=,<,>,++,~=),的时候，shell会返回一个代码变量，?会反映结果是真还是假，再一次说明，必须使用双引号来防止shell将大于和小于运算符当作I/O重定向。


[root@lnmp02 shell]# let a=1+1  
[root@lnmp02 shell]# echo $a
2
[root@lnmp02 shell]# let a=1 + 1    #不能有空格
-bash: let: +: syntax error: operand expected (error token is "+")
[root@lnmp02 shell]# let a= 1+1
-bash: let: a=: syntax error: operand expected (error token is "=")
[root@lnmp02 shell]# let a = 1+1
-bash: let: =: syntax error: operand expected (error token is "=")
[root@lnmp02 shell]#
 
[root@lnmp02 shell]# let "a = 1 + 13"  #如果有空格，需要加上双引号
[root@lnmp02 shell]# echo $a        
14
[root@lnmp02 shell]#
 

```

# 3.expr
如表达式中和运算符号之间的空格及一些运算符号需要转义，还有一点需要记住，expr只适用于整数之间的运算

## 3.1.整数间的四则运算（+、-、*、/、%）
```
$expr 9 + 8 - 7 \* 6 / 5 + \( 4 - 3 \) \* 2
11

#注意其中的反引号：思想就是：拼接字符串，然后将拼接之后的字符串交给命令执行
[root@MySQL shell]# echo `seq -s "+" 10`=`seq -s " + " 10|xargs expr`
1+2+3+4+5+6+7+8+9+10=55
[root@MySQL shell]#


#判断输入是否为整数
expr $1 + 1 &>/dev/null
$? -ne 0

```

## 3.2.字符串匹配
```
STRING : REGEXP   anchored pattern match of REGEXP in STRING
match STRING REGEXP same as STRING : REGEXP
 
[root@MySQL shell]# expr match "aaabbbcc" "aa*"
3
[root@MySQL shell]# expr match "aaabbbcc" "aad"
0
[root@MySQL shell]#
[root@MySQL shell]# expr "aaabbbcc" : "aa*"
3
[root@MySQL shell]# expr "aaabbbcc" : "aad"
0
[root@MySQL shell]#
 
```



## 3.3.取字符串的子串
```
substr STRING POS LENGTH   substring of STRING, POS counted from 1
 
[root@MySQL shell]# expr substr "aaabbbccc" 2 4 
aabb

```


## 3.4.取字符的下标
```
index  STRING  CHARS  index in STRING where any CHARS is found, or 0
 
[root@MySQL shell]# expr index "aaabbbccc" a
1

```

## 3.5.字符串的长度
```
length STRING   length of STRING
 
[root@MySQL shell]# expr length "aaabbbccc"
9

```


# 4.计算小数bc
在shell命令行直接输入bc及能进入bc语言的交互模式。
bc也可以进行非交互式的运算，方法是与echo一起使用。

```
[root@MySQL shell]# echo "4 * 0.56"|bc
2.24

```



# declare



```Shell
#!/bin/bash

# 申明SUM为integer
declare -i SUM=0

for I in {0..10} ;do
	let SUM=$[$SUM+$I]
done

echo "The sum is :$SUM"
```

