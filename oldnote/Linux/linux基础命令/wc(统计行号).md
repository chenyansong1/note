---
title: Linux基础命令之wc(统计行号)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



# 语法

```
       -c, --bytes #字节数
              print the byte counts
 
       -m, --chars #字符数
              print the character counts
 
       -l, --lines    #所有的行数
              print the newline counts
 
       -L, --max-line-length    #最长行的长度
              print the length of the longest line
     
       -w, --words    #单词数
              print the word counts


```

# 实例

```
[root@lamp01 chenyansong]cat wc_test.txt
aaa
bbb
ccccc
ee

#字节数
[root@lamp01 chenyansong]wc -c wc_test.txt
17 wc_test.txt

#字符数
[root@lamp01 chenyansong]wc -m wc_test.txt 
17 wc_test.txt

#所有的行数
[root@lamp01 chenyansong]wc -l wc_test.txt 
4 wc_test.txt

#最长行的长度
[root@lamp01 chenyansong]wc -L wc_test.txt 
5 wc_test.txt

```