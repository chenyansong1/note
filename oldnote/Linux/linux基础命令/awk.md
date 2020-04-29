---
title: Linux基础命令之awk
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



[TOC]

报告生成工具：会将每一行都分解为\$1,\$2,\$3...这样的列



# 1 语法

* 定义：一门语言，过滤内容（取列）
* 语法：# awk ‘条件1{动作1} 条件2{动作2} ...’ 文件名
* 条件：一般使用关系表达式作为条件，如：x>10 or x<=10 or x>=10
* 动作：格式化输出 or 流程控制语句

```
awk 'pattern{action}' file

#拿到pattern匹配的行，然后执行action的动作
action：
	print
	
echo "aa bb"|awk '{print $1}'
echo "aa bb cc"|awk '{print $1,$3}'
#$0表示一整行中的所有字段
echo "aa bb cc"|awk '{print $0}'

NF 表示分隔出来字段个数；而$NF表示最后一个字段，比如分隔出来的字段个数为4，那么NF=4，而$4就表示的最后一个字段

输入的分隔符，默认是空白
-F可以指定，awk -F: '{print $1,$3}' /etc/passwd

输出分隔符，默认是空白

```



example

```
chenyansongdeMacBook-Pro:note chenyansong$ echo  "aa bb"|awk '{print $1}'
aa
chenyansongdeMacBook-Pro:note chenyansong$ echo  "aa bb"|awk '{print $2}'
bb
chenyansongdeMacBook-Pro:note chenyansong$ echo  "aa bb"|awk '{print $3}'

chenyansongdeMacBook-Pro:note chenyansong$ echo "aa bb cc"|awk '{print $1,$3}'
aa cc
chenyansongdeMacBook-Pro:note chenyansong$ echo "aa bb cc"|awk '{print $0}'
aa bb cc
chenyansongdeMacBook-Pro:note chenyansong$ 
```




# 2. 指定分隔符
``` shell
[root@linux-study cys_test]# echo "name@@age@@gender" > new
[root@linux-study cys_test]# cat new
name@@age@@gender
[root@linux-study cys_test]# awk -F "@@" '{print $1}' new   
name
[root@linux-study cys_test]#

#注意：-F 表示分隔符，$1 第一列，$2第二列...... $NF最后一列，$(NF-1) 倒数第二列 
```

# 3. 指定多个分隔符
```
[root@lamp01 chenyansong]# echo "I am oldboy,myqq is 122344" > test.txt

[root@lamp01 chenyansong]# cat test.txt
I am oldboy,myqq is 122344

#指定多个分隔符
[root@lamp01 chenyansong]# awk -F "[, ]" '{print $3 ":::" $6}' test.txt
oldboy:::122344
```

# 4.在开头或者是结尾处添加字符串
``` shell
[root@lamp01 chenyansong]# echo "chenyansong@@123.com" > test2.txt
[root@lamp01 chenyansong]# cat test2.txt
chenyansong@@123.com

[root@lamp01 chenyansong]# awk -F "@@" 'BEGIN{printf "hello-"}{print $1}' test2.txt
hello-chenyansong

[root@lamp01 chenyansong]# awk -F "@@" 'END{print "--world"}{printf $1}' test2.txt
chenyansong--world

#注意print和printf是有换行的区别的
```



# 5.添加条件表达式
``` shell
[root@linux-study cys_test]# seq 55 >seq_n
[root@linux-study cys_test]# awk '{if(NR>33) print $1}' seq_n
 
#其中：NR 表示行号
```


# 6.NR
``` shell
#获取文件的访问权限(此时是644)
[root@lamp01 chenyansong]# stat /etc/hosts
  File: "/etc/hosts"
  Size: 223             Blocks: 8          IO Block: 4096   普通文件
Device: 803h/2051d      Inode: 130078      Links: 2
Access: (0644/-rw-r--r--)  Uid: (    0/    root)   Gid: (    0/    root)
Access: 2017-02-11 15:56:14.414759618 +0800
Modify: 2016-08-31 21:03:38.328210183 +0800
Change: 2016-08-31 21:03:38.354798349 +0800
[root@lamp01 chenyansong]# stat /etc/hosts
 
 
[root@lamp01 chenyansong]# stat /etc/hosts|awk -F '[0/]' 'NR==4 {print $2}'
644
#NR==4表示取第4行,然后对第4行用0或者/分割,取第2段

#注意：“{}”和‘{}’的区别，因为{} 里面是命令块，所以不需要解析，所以用‘’单引号
```

# 7.awk相加各列
```
#求一个文件的数字访问权限(如644)
[root@lamp01 chenyansong]# ls -l test2.txt|cut -c 2-10|tr rwx- 4210|awk -F "" '{print $1+$2+$3 $4+$5+$6 $7+$8+$9}'
644

/* 
1.首先使用cut将权限字符取到
2.使用tr去掉rwx-,换成4210
3.使用awk取列,然后相加各列
*/
```

# 8.awk数组
``` shell
$ ps aux | awk 'NR!=1{a[$1]+=$6;} END { for(i in a) print i ", " a[i]"KB";}'
dbus, 540KB
mysql, 99928KB
www, 3264924KB
root, 63644KB
hchen, 6020KB
```



# 9.循环遍历行

```shell
[root@VM_0_84_centos ~]# cat sshd.txt
1 2 3
4 5 6
7 8 9
#循环打印上述文本

#for 循环的固定格式   i=1设置i的初始变量  i<=NF i变量小于等于 NF变量的值（每行的字段数） i++ 表示i递增+1，
[root@VM_0_84_centos ~]# cat sshd.txt |awk '{for(i=1;i<=NF;i++){print $i}}'
1
2
3
4
5
6
7
8
9
```



