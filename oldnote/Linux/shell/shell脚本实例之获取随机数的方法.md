---
title: shell脚本实例之获取随机数的方法
categories: shell   
toc: true  
tags: [shell]
---


# 1.获取随机数的方法

## 1.1.RANDOM
```
#RANDOM是bash中的内置变量
[root@lnmp02 shell]# man bash
RANDOM Each time this parameter is referenced, a random integer between 0 and 32767 is generated. 
 
[root@lnmp02 shell]# echo $RANDOM
6421

#使用md5去加密
[root@lnmp02 shell]# echo $RANDOM|md5sum    
0ee03cc589c2d6c87a55dfb12837992d  -

#取md5加密之后的5-10
[root@lnmp02 shell]# echo $RANDOM|md5sum|cut -c 5-10  
f61bc1

 
#因为$RANDOM的范围是：between 0 and 32767,数量还是有限的,所以通过遍历32767次,然后md5sum加密,还是能够算出密码的, 所以一般($RANDOM str)一起使用(因为str是不确定的)
[root@lnmp02 shell]# echo $RANDOM oldboy
18449 oldboy
[root@lnmp02 shell]# echo $RANDOM oldboy|md5sum|cut -c 5-10
96fe97

```

## 1.2.openssl rand产生随机数

&emsp;openssl rand 用于产生指定长度个bytes的随机字符。-base64或-hex对随机字符串进行base64编码或用hex格式显示。
```
[root@lnmp02 shell]# openssl rand -base64 8  #八位字母和数字的组合
NHKIT0Q4Els=
[root@lnmp02 shell]#
 
openssl rand -base64 8 | md5sum | cut -c1-8      #openssl rand 之后再使用md5加密 ，cut 截取

```

## 1.3.时间随机数
```
date +%s%N       #生成19位数字，1287764807051101270 ，%s 是秒数，%N是纳秒数
 
date +%s%N | cut -c 6-13   #取八位数字，21793709
date +%s%N | md5sum | head -c 8   #八位字母和数字的组合，87022fda ，-c是字节
 
#生成一个m-n之间的随机数
function rand(){
    min=$1
    max=$(($2-$min+1))
    num=$(date +%s%N)
    echo $(($num%$max+$min))
}

```



## 1.4.UUID
```
#UUID码全称是通用唯一识别码 (Universally Unique Identifier, UUID)，UUID格式是：包含32个16进制数字，以“-”连接号分为五段，形式为8-4-4-4-12的32个字符。linux的uuid码也是有内核提供的，在/proc/sys/kernel/random/uuid这个文件内。cat /proc/sys/kernel/random/uuid每次获取到的数据都会不同。
 
[root@lnmp02 shell]# cat /proc/sys/kernel/random/uuid
1f309506-c862-4b94-9d1a-bcb0f3486b4b

```


## 1.5.expect中的mkpasswd
```
yum install expect -y

#mkpasswd语法
usage: mkpasswd [args] [user]
  where arguments are:
    -l #      (length of password, default = 9)
    -d #      (min # of digits, default = 2)
    -c #      (min # of lowercase chars, default = 2)
    -C #      (min # of uppercase chars, default = 2)
    -s #      (min # of special chars, default = 1)
    -v        (verbose, show passwd interaction)
    -p prog   (program to set password, default = passwd)

#使用
[root@lamp01 ~]# mkpasswd -l 8
B8ir8^qG
[root@lamp01 ~]# mkpasswd -l 8
uVD,49rm


```



# 2.测试随机数的唯一性
```
#通过对产生的随机数统计排序,查看是否唯一性
for n in `seq 20`;do date +%s%N|md5sum|cut -c 1-9;done|sort|uniq -c|sort -rn -k1

for n in `seq 20`;do echo $RANDOM|md5sum|cut -c 1-9;done|sort|uniq -c|sort -rn -k1

# sort -rn -k1    # 其中k1是以第一个字段进行排序
```


# 3.组合使用
```
[root@lamp01 ~]# echo $RANDOM `mkpasswd -l 8`
14081 F~1zeBw6

[root@lamp01 ~]# echo $RANDOM `mkpasswd -l 8`|md5sum
9570c8be65dbfafb2eb644593b2d3b38  -

```


