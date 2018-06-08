---
title: 条件测试,控制流及for循环
categories: shell   
toc: true  
tags: [shell]
---

[TOC]


# 1.条件测试
```
查看帮助：man test

```
## 1.1.语法
```
test condition
#或 
[ condition ]     #注意中括号（［］）、参数之间必须有一个空格

```


## 1.2.逻辑运算符
|符号|描述|
|-|-|
|-a|逻辑与|
|-o|逻辑或|
|!|逻辑否|
|&&, \|\|||

```
#1.      测试两文件是否均可读
$[ -w result.txt –a –w scores.txt ]
$echo $?

#2.      测试两文件中其中一个是否可执行
$[ -x dream –o –x dream2 ]

#3.      判断是否可写可执行
$[ -w dream –a –x dream ]
$echo $?

#4.      判断文件是非可执行文件
$[ ! –x dream ]

#5.   [条件判断表达式1]&& [条件判断表达式2]|| [条件判断表达式3]

#6. 在[[]] 中，只能用&&、||，而在[]中只能用-a、-o
[root@MySQL ~]# [ -f /etc/rc.local && -f /etc/hosts ]&&echo 1||echo 0
-bash: [: missing `]'
0
[root@MySQL ~]# [ -f /etc/rc.local -a -f /etc/hosts ]&&echo 1||echo 0  
1
 
[root@MySQL ~]# [[ -f /etc/rc.local -a -f /etc/hosts ]]&&echo 1||echo 0    
-bash: syntax error in conditional expression
-bash: syntax error near `-a'
[root@MySQL ~]# [[ -f /etc/rc.local && -f /etc/hosts ]]&&echo 1||echo 0 
1
[root@MySQL ~]#
```

## 1.3.文件条件测试

|符号|描述|
|-|-|
|-d|目录|
|-f|普通文件（Regular file）|
|-L|符号链接|
|-r|Readable（文件、目录可读）|
|-b|块专用文件|
|-e|文件存在|
|-g|如果文件的set-group-id位被设置则结果为真|
|-s|文件长度大于0，非空|
|-z|文件长度=0|
|-w|Writable（文件、目录可写）|
|-u|文件有suid位设置|
|-x|Executable（文件可执行、目录可浏览）|
|-c|字符专用文件|
|-L|符号链接|

```

测试文件是否可写
$test –w dream
$echo $?
或者
$[ -w dream ]

```


## 1.4.字符串测试
```
#5种语法格式:

test “str”
test str_operator “str”
test “str1” str_operator “str2”
[ string_operator str1 ]     
[ string string_operator string2 ]    #操作符两边有空格：[ “aaa” = “cccc” ]   不要： [ “aaa”=“cccc” ]

#string_operator有如下的形式
=    #两字符串相等
!=    #两字符串不等
-z    #空串 [zero]
-n    #非空串 [nozero]

```
实例
```
#1.测试环境变量是否为空
$[ -z $EDITOR ]
$echo $?

#2. 测试是否为某字符串
$[ $EDITOR = “vi” ]
$echo $?

#3.如果操作符两边没有空格的结果
[root@MySQL shell]# [ "a" = "a" ]&&echo 1||echo 0
1
[root@MySQL shell]# [ "a" = "aa" ]&&echo 1||echo 0
0
[root@MySQL shell]# [ "a"="a" ]&&echo 1||echo 0  
1
[root@MySQL shell]# [ "a"="aa" ]&&echo 1||echo 0
1
[root@MySQL shell]#
 
```



## 1.5.数值测试
```
#2种格式
“number” numberic_operator “number”
# 或    
[ “number” numberic_operator “number”]

#Numberic_operator 算术比较
-eq    #数值相等（equal）
-ne    #不等（not equal）
-gt    #A>B（greater than）
-lt    #A<B（less than）
-le   #A<=B（less、equal）
-ge    #A>=B（greater、equal）


#例子
$SOURCE=13
$DEST=15
$[ “$SOURCE” –gt “$DEST”]

```

## 1.6.比较两个文件

```
FILE1 -ef FILE2  //比较两个文件是否是同一个
              #FILE1 and FILE2 have the same device and inode numbers
FILE1 -nt FILE2 //比较两个文件的修改日期
              #FILE1 is newer (modification date) than FILE2
FILE1 -ot FILE2
              #FILE1 is older than FILE2
```



# 2.控制流

## 2.1.if

### 2.1.1语法

> 单分支结构

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/1.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/2.png)


> 双分支结构

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/3.png)


> 多分支结构


![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/4.png)




### 2.1.2.书写格式

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/5.png)



### 2.1.3.example
```
# 判断字符串是否为空
if [ $log_dir"x"="x" ];then
	echo "true"
fi
```


## 2.2.case

### 2.2.1.语法

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/6.png)


### 2.2.2.书写格式

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/7.png)


## 2.3.while

### 2.3.1.语法

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/8.png)


### 2.3.2.书写格式

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/9.png)


### 2.3.3.通过while读文件的方式


```
#方式1(效率最低)
cat data.dat | while read line
do
    echo "File:${line}"
done

#方式2
while read line
do
    echo "File:${line}"
done < data.dat

#方式3(效率最高)
for line in $(cat data.dat)
do
    echo "File:${line}"
done
 
for line in `cat data.dat`
do
    echo "File:${line}"
done

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/10.png)





# 3.for循环

## 3.1.语法

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/11.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/12.png)




## 3.2.书写格式

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/13.png)

# 退出break、continue、exit、return


![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/14.png)



