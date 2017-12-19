---
title: Linux基础命令之cut
categories: Linux   
toc: true  
tags: [Linux基础命令]
---

# 1.语法
``` shell

       -c, --characters=LIST
              select only these characters
 
       -d, --delimiter=DELIM
              use DELIM instead of TAB for field delimiter
 
       -f, --fields=LIST
              select  only  these fields;  also print any line that contains no delimiter
              character, unless the -s option is specified

```

# 2.举例
```
[root@linux-study cys_test]# echo "my name is chenyansong" >> name.txt
 
#d 分隔符，f字段
[root@linux-study cys_test]# cut -d " " -f2,4 name.txt
name chenyansong
 
#c 字符,13- 表示第13个字符到最后
[root@linux-study cys_test]# cut -c 1-11,13- name.txt
my name is
 
```
 


