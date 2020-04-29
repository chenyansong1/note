---
title: Linux基础命令之cat
categories: Linux   
toc: true  
tags: [Linux基础命令]
---

# 1.作用
```shell
#1.显示文件内容，
cat filename

#2.将几个文件连接起来显示
cat > filename 只能创建新文件,不能编辑已有文件.
cat file1 file2 > file

#3.从标准输入读取内容并显示
//输出到一个文件中
  [root@localhost test]# cat >log.txt <<EOF
> Hello
> World
> Linux
> EOF
//直接打印
[root@MySQL shell]# cat <<EOF
> A
> B
> EOF
A
B
```

# 2.语法
```shell
cat  [选项]   [文件]...
```

# 3.参数选项
|参数|含义|
|:-|:-|
|-A, --show-all |等价于 -vET|
|-b, --number-nonblank |<font color=red>对非空输出行编号</font>|
|-e|等价于 -vE|
|-E, --show-ends|在每行结束处显示 $|
|-n, --number|<font color=red>对输出的所有行编号,由1开始对所有输出的行数编号</font>|
|-s, --squeeze-blank|有连续两行以上的空白行，就代换为一行的空白行|
|-t|与 -vT 等价|
|-T, --show-tabs| 将跳格字符显示为 ^I|
|-u|(被忽略)|
|-v, --show-nonprinting|使用 ^ 和 M- 引用，除了 LFD 和 TAB 之外|


# 4.使用实例
## 4.1.<<EOF输出到文件
```shell
[root@web setup]# cat >2.txt <<EOF
> Hello
> Bash
> Linux
> PWD=$(pwd)
> EOF
 
[root@web setup]# ls -l 2.txt
-rw-r--r-- 1 root root 33 11-02 21:35 2.txt
[root@web setup]# cat 2.txt
Hello
Bash
Linux
PWD=/root/setup 
[root@web setup]# 
```

## 4.2.在shell中使用cat<<EOF
```shell
#!/bin/bash
main(){
cat << AA
==============================
monday;
FDSFSDFSDFSF
=============================
AA
}
main;
 

```


## 4.3.输出行号
```shell
[root@MySQL shell]# cat -n test.sh
     1  [ "$1" -eq "$2" ]&&echo "="
     2  [ "$1" -gt "$2" ]&&echo ">"
     3  [ "$1" -lt "$2" ]&&echo "<"
     4
     5
     6
[root@MySQL shell]# 
```

## 4.4.空行不显示行号,用-b
```shell
[root@MySQL shell]# cat -b test.sh
     1  [ "$1" -eq "$2" ]&&echo "="
     2  [ "$1" -gt "$2" ]&&echo ">"
     3  [ "$1" -lt "$2" ]&&echo "<"
 
 
[root@MySQL shell]# 
```shell
## 4.5.连续两行以上的空白行,就代换为一行的空白行
```shell
[root@MySQL shell]# cat -s test.sh
[ "$1" -eq "$2" ]&&echo "="
[ "$1" -gt "$2" ]&&echo ">"
[ "$1" -lt "$2" ]&&echo "<"
 
[root@MySQL shell]# 
```

## 4.6.打印两个文件的内容
```shell
[root@MySQL shell]# cat cat.txt cat1.txt
this is cat.txt
this is cat1.txt
[root@MySQL shell]#
 
[root@MySQL shell]# cat -n cat.txt cat1.txt
     1  this is cat.txt
     2  this is cat1.txt
 
 
 
 
[root@MySQL shell]# cat -n cat.txt cat1.txt >cat3.txt
[root@MySQL shell]# cat cat3.txt
     1  this is cat.txt
     2  this is cat1.txt
[root@MySQL shell]# 
```










