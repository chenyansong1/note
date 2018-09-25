---
title: shell之字符串操作
categories: shell   
toc: true  
tags: [shell]
---



[TOC]



# 1.判断读取字符串值

```
${var}	    #变量var的值, 与$var相同 
${var-DEFAULT}	    #如果var没有被声明, 那么就以$DEFAULT作为其值 * 
${var:-DEFAULT}    #如果var没有被声明, 或者其值为空, 那么就以$DEFAULT作为其值 * 
${var=DEFAULT}    #如果var没有被声明, 那么就以$DEFAULT作为其值 * 
${var:=DEFAULT}    #如果var没有被声明, 或者其值为空, 那么就以$DEFAULT作为其值 * 
${var+OTHER}    #如果var声明了, 那么其值就是$OTHER, 否则就为null字符串 
${var:+OTHER}    #如果var被设置了, 那么其值就是$OTHER, 否则就为null字符串 
${var?ERR_MSG}    #如果var没被声明, 那么就打印$ERR_MSG * 
${var:?ERR_MSG}    #如果var没被设置, 那么就打印$ERR_MSG * 
${!varprefix*}    #匹配之前所有以varprefix开头进行声明的变量 
${!varprefix@}    #匹配之前所有以varprefix开头进行声明的变量 
```

# 2.字符串操作（长度，读取，替换）

```
${    #string}    #$string的长度 
${string:position}    # 在$ string中,  从位置$ position开始提取子串 
${string:position:length}    
${string#substring} 	# 从变量$string的开头, 删除最短匹配$substring的子串 
${string##substring}    #从变量$string的开头, 删除最长匹配$substring的子串 
${string%substring}    #从变量$string的结尾, 删除最短匹配$substring的子串 
${string%%substring}    #从变量$string的结尾, 删除最长匹配$substring的子串 
${string/substring/replacement}    #使用$replacement, 来代替第一个匹配的$substring 
${string//substring/replacement}    #使用$replacement, 代替所有匹配的$substring 
${string/    #substring/replacement}    #如果$string的前缀匹配$substring, 那么就用$replacement来代替匹配到的$substring 
${string/%substring/replacement}    #如果$string的后缀匹配$substring, 那么就用$replacement来代替匹配到的$substring 

```

* 路径截取后面

```
chenyansongdeMacBook-Pro:shell chenyansong$ echo $file
/usr/local/src
chenyansongdeMacBook-Pro:shell chenyansong$ echo ${file#/}
usr/local/src
chenyansongdeMacBook-Pro:shell chenyansong$ echo ${file##/}
usr/local/src
chenyansongdeMacBook-Pro:shell chenyansong$ echo ${file##*/}
src
chenyansongdeMacBook-Pro:shell chenyansong$ 

```



* 路径截取前面

```
chenyansongdeMacBook-Pro:shell chenyansong$ echo $file
/usr/local/src
chenyansongdeMacBook-Pro:shell chenyansong$ echo ${file%/*}
/usr/local
chenyansongdeMacBook-Pro:shell chenyansong$ echo ${file%%/*}

chenyansongdeMacBook-Pro:shell chenyansong$ 

```



```

```

