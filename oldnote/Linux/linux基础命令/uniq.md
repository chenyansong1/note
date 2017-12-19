---
title: Linux基础命令之uniq
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



# 语法
```
       -c, --count    #计数
              prefix lines by the number of occurrences
 
       -d, --repeated    #只打印重复的行,2次或2次以上的行,默认的去重包含1次
              only print duplicate lines
         #1.仅显示相邻重复出现的行
         #2.在1的基础上去重包含1次 

       -i, --ignore-case    #在判断重复行时忽略大小写
              ignore differences in case when comparing
 
       -u, --unique    #仅显示出现一次的行
              only print unique lines

```

# 实例
```
#只对相邻的行去重
[root@lamp01 chenyansong]cat test_cc.txt
10.0.0.1.txt
10.0.0.4.txt
10.0.0.2.txt
10.0.0.1.txt
10.0.0.1.txt
10.0.0.1.txt
10.0.0.3.txt
10.0.0.4.txt
10.0.0.5.txt
[root@lamp01 chenyansong]uniq test_cc.txt
10.0.0.1.txt
10.0.0.4.txt
10.0.0.2.txt
10.0.0.1.txt
10.0.0.3.txt
10.0.0.4.txt
10.0.0.5.txt


#所以要使用sort之后,再uniq
[root@lamp01 chenyansong]sort test_cc.txt|uniq -c
      4 10.0.0.1.txt
      1 10.0.0.2.txt
      1 10.0.0.3.txt
      2 10.0.0.4.txt
      1 10.0.0.5.txt


#仅显示重复的行
[root@lamp01 chenyansong]cat test_cc.txt
10.0.0.1.txt
10.0.0.4.txt
10.0.0.2.txt
10.0.0.1.txt
10.0.0.1.txt
10.0.0.1.txt
10.0.0.3.txt
10.0.0.4.txt
10.0.0.5.txt
[root@lamp01 chenyansong]uniq -d test_cc.txt
10.0.0.1.txt
[root@lamp01 chenyansong]


#仅显示出现一次的行
[root@lamp01 chenyansong]cat test_cc.txt    
10.0.0.1.txt
10.0.0.4.txt
10.0.0.2.txt
10.0.0.1.txt
10.0.0.1.txt
10.0.0.1.txt
10.0.0.3.txt
10.0.0.4.txt
10.0.0.5.txt
[root@lamp01 chenyansong]uniq -u test_cc.txt 
10.0.0.1.txt
10.0.0.4.txt
10.0.0.2.txt
10.0.0.3.txt
10.0.0.4.txt
10.0.0.5.txt

```

通常uniq和sort配合使用