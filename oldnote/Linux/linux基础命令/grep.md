---
title: Linux基础命令之grep
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



[TOC]

# 1.语法

``` shell
功能描述：在文件中搜索字符串匹配的行并输出
grep [options] PATTERN [FILE...]
语法：grep -vi [指定字符串] [文件]

-v 排除指定的字符串
-i 不区分大小写
-c 计算找到“搜索字符串”的次数
-o 仅显示匹配regexp的内容（用于统计出现在文中的次数）
-n 行首显示行号
-E 扩展的grep，简写：egrep

-A 显示匹配到的内容的后面的几行
-B 显示匹配到的内容的前面的几行
-C 显示匹配到的内容的前后的几行

```



# 文件内容搜索

```shell
#搜索当前目录下的所有的文件中有包含nodeip的文件
#r recursive 递归目录， n显示行号
d
```



# 基本正则说明

## 字符匹配



| 通配符 | 说明                                                     |
| ------ | :------------------------------------------------------- |
| .      | 匹配任意的单个字符;如果是 \. 这要使用\\. 转移            |
| [0-9]  | 匹配指定范围内的单个字符                                 |
| [^]    | 匹配指定范围外的任意单个字符                             |
|        | [:digit:] 数字； [:lower:] 小写字母；[:upper:] 大写字符  |
|        | [:space:] 空白字符；[:alpha:] 字母 ；[:alnum:] 字母+数字 |
|        | grep '[[:digit:]]$'  /etc/inittab 匹配以数字结尾的行     |
|        |                                                          |



## 次数匹配

| 通配符     | 说明    |
| ---------- | :------------- |
| *          | 匹配其前面的字符任意次   |
| .*         | 任意长度的任意字符  |
| ？         | 匹配其前面的字符1次或0次，可能需要转义: grep 'a\?b' text.log |
| \\{m,n\\}  | 匹配其前面的字符，至少m次，至多n次  |
|            | \\{1,\\}  至少1次      |
|            | grep  'a.\\{1,3\\}b'  aa.txt  ;  a和b之间有1到3个字符        |
|            | \\{,3\\} 至多3次   |


## 锚定

| 通配符     | 说明    |
| ---------- | :------------- |
| ^          | 锚定行首  |
| $          | 锚定行尾                       |
| ^$         | 空白行 |
| \\< or \\b | 其后面的任意字符必须作为单词首部出现                         |
| \\> or \\b | 其后面的任意字符必须作为单词的尾部出现                       |

```
# 以root作为词尾
grep "root\>" text.log

# 以root作为词首
grep "\<root" text.log

# 以root为一个单词的
grep "\<root\>" text.log

```



![image-20180826101559470](/Users/chenyansong/Documents/note/images/linux/command/grep.png)



## 分组

| 通配符 | 说明                                                 |
| ------ | ---------------------------------------------------- |
| \\(\\) |                                                      |
| \\1    | 引用第一个左括号以及与之对应的右括号所包含的所有内容 |
| \\n    | 表示第n个括号中的内容                                |

```

grep "\(ab\)*" test.txt 		// ab当做一组，可以出现0或多次,所以说 不出现也是可以的

grep '\(l..e\).*\1'  text3.txt


```



![image-20180826102206853](/Users/chenyansong/Documents/note/images/linux/command/grep2.png)



![image-20180826102206853](/Users/chenyansong/Documents/note/images/linux/command/grep3.png)





# 扩展正则

egrep==grep -E





## 字符匹配



| 通配符 | 说明                                                     |
| ------ | :------------------------------------------------------- |
| .      | 匹配任意的单个字符;如果是 \. 这要使用\\. 转移            |
| [0-9]  | 匹配指定范围内的单个字符                                 |
| [^]    | 匹配指定范围外的任意单个字符                             |
|        | [:digit:] 数字； [:lower:] 小写字母；[:upper:] 大写字符  |
|        | [:space:] 空白字符；[:alpha:] 字母 ；[:alnum:] 字母+数字 |
|        | grep '[[:digit:]]$'  /etc/inittab 匹配以数字结尾的行     |
|        |                                                          |



## 次数匹配



| 通配符     | 说明    |
| ---------- | :------------- |
| *          | 匹配其前面的字符任意次   |
| .*         | 任意长度的任意字符  |
| ？         | 匹配其前面的字符1次或0次，可能需要转义: grep 'a\?b' text.log |
| {m,n}  | 匹配其前面的字符，至少m次，至多n次  |
|            | {1,}  至少1次      |
|            | grep  'a.\\{1,3\\}b'  aa.txt  ;  a和b之间有1到3个字符        |
|            | \\{,3\\} 至多3次   |
| + | 匹配其前字符至少一次 |


## 锚定

| 通配符     | 说明    |
| ---------- | :------------- |
| ^          | 锚定行首  |
| $          | 锚定行尾                       |
| ^$         | 空白行 |
| \\< or \\b | 其后面的任意字符必须作为单词首部出现                         |
| \\> or \\b | 其后面的任意字符必须作为单词的尾部出现                       |



## 分组

| 通配符 | 说明                                                 |
| ------ | ---------------------------------------------------- |
| ( ) |                                                      |
| \\1    | 引用第一个左括号以及与之对应的右括号所包含的所有内容 |
| \\n    | 表示第n个括号中的内容                                |

## 或者



| 通配符 | 说明 |
| ------ | ---- |
| \|     | or   |

```Shell
# 匹配 C or cat
grep -E 'C|cat'  test.log

# 匹配cat or Cat
grep -E '(C|c)at'  test.log

# 找出 /etc/profile中 1-255之间的整数
grep -E "\<([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\>"  /etc/profile
```



# fgrep

不支持正则表达式






# 2.指定开头（^）,指定结尾（$）

``` shell
#去掉以#开头的行，其中^ 表示以。。。开头 ，$ 以。。结尾的
[root@lamp01 chenyansong]# grep -v "^#" /etc/inittab
id:3:initdefault:


```

# 3.grep -A -B -C
``` shell
# 第一个30是表示匹配到字符串30所在的行，然后向前取10行
[root@linux-study cys_test]# grep 30 -B 10 seq_n
20
21
22
23
24
25
26
27
28
29
30

/* 
注意：-B 表示除了显示匹配的一行之外，并显示该行之前的num行
-A 表示除了显示匹配的一行之外，并显示该行之后的num行
-C 表示除了显示匹配的一行之外，并显示该行之前的各num行
*/
```

# 4.-0只是打印匹配到的字符串
``` shell
#-o 表示只是打印匹配到的字符串，而不是默认的整行
[root@linux-study cys_test]# grep -o "chen.*" people.txt
chenyansong 22 man
[root@linux-study cys_test]# grep -o "chen" people.txt  
chen

```

![image-20180925214719093](/Users/chenyansong/Documents/note/images/linux/command/grep-o.png)

# 5. -E指定多个字符串进行匹配

``` shell
#-E 表示拓展的grep，简写：egrep，可以不用转义

[root@linux-study cys_test]# grep -E "chenyansong|zhangsan" people.txt
chenyansong 22 man
zhangsan 33 man

#找出端口为3306或者是1521的服务
[root@linux-study cys_test]# grep -E "3306|1521" /etc/services
mysql           3306/tcp                        # MySQL
mysql           3306/udp                        # MySQL
ncube-lm        1521/tcp                # nCube License Manager
ncube-lm        1521/udp                # nCube License Manager

# 获取数字开头的行
grep "^[0-9]" test.log


```

# 6.改匹配到的字符串添加颜色标识
``` shell
#给匹配到的字符串添加颜色
[root@lamp01 chenyansong]# grep --color=auto 3306 /etc/services
mysql           3306/tcp                        # MySQL
mysql           3306/udp                        # MySQL


```

# 7. 显示行号
``` shell
[root@linux-study cys_test]# grep -n ':' people.txt    #对匹配到的行在原有行中的位置
2:chenyansong 22 man
3:zhangsan 33 man
4:lisi 44 women
6:aaaa

```

# 8. 统计匹配到的行：-c
``` shell
[root@lamp01 chenyansong]# cat test3.txt
aaaa
 
aa cc dd
[root@lamp01 chenyansong]# grep "a" test3.txt
aaaa
aa cc dd
[root@lamp01 chenyansong]# grep -c "a" test3.txt
2

```

# 8.匹配取反:-v
``` shell
#匹配非空的行（空字符串不是空行）
[root@lamp01 chenyansong]# grep -v "^$" test3.txt
aaaa
aa cc dd

```

# 9忽略大小写匹配：-i
``` shell
[root@lamp01 chenyansong]# cat test3.txt
aaaa
 
aa cc dd
AAA
[root@lamp01 chenyansong]# grep -i "aa" test3.txt
aaaa
aa cc dd
AAA

```


# 10.取ip
``` shell

[root@lamp01 chenyansong]# cat /etc/sysconfig/network-scripts/ifcfg-eth0|grep "IPADDR"
IPADDR=192.168.0.3

[root@lamp01 chenyansong]# cat /etc/sysconfig/network-scripts/ifcfg-eth0|grep "IPADDR"|cut -d= -f2
192.168.0.3

```



