---
title: Linux基础命令之sort排序.md
categories: Linux   
toc: true  
tags: [Linux基础命令]
---




# 1.语法
```

#以行为单位对文件进行排序

sort [参数] [<文件>...]

#常用参数:
-b    #忽略前导的空格
-d    #只考虑空格,字母和数字
-f    #忽略字母的大小写
-i    #只考虑可打印字符
-M    #排序月份
-n    #根据字符串的数值进行排序
-r    #逆向排序
-u    #对相同的行只输出一行
+n    #n为数字,对指定的列进行排序, +0表示第1列,以空格或制表符作为列的间隔符
-t    #指定分隔符
-kn    #指定分隔符之后的第n列(从1开始)作为排序列


```


# 2.实例
```
# -u 取出唯一的行
[root@lamp01 chenyansong]# cat test_sort.txt
10.0.0.7
10.0.0.8
10.0.0.7
10.0.0.7
10.0.0.9
10.0.0.7
10.0.0.7
10.0.0.7
10.0.0.7
[root@lamp01 chenyansong]# sort -u test_sort.txt
10.0.0.7
10.0.0.8
10.0.0.9


#-r    按数字倒序
[root@lamp01 chenyansong]# cat test_sort.txt
10.0.0.7
10.0.0.8
10.0.0.7
10.0.0.7
10.0.0.9
10.0.0.7
10.0.0.7
10.0.0.7
10.0.0.7
[root@lamp01 chenyansong]# sort -r test_sort.txt
10.0.0.9
10.0.0.8
10.0.0.7
10.0.0.7
10.0.0.7
10.0.0.7
10.0.0.7
10.0.0.7
10.0.0.7


#按数字排序
[root@lamp01 chenyansong]# cat test_sort.txt    
10.0.0.7
10.0.0.8
10.0.0.7
10.0.0.7
10.0.0.9
10.0.0.7
10.0.0.7
10.0.0.7
10.0.0.7
[root@lamp01 chenyansong]# sort -n test_sort.txt 
10.0.0.7
10.0.0.7
10.0.0.7
10.0.0.7
10.0.0.7
10.0.0.7
10.0.0.7
10.0.0.8
10.0.0.9


#-t 指定分隔符排序, -k1 取分隔符之后的第一列排序
[root@lamp01 chenyansong]# cat test_sort.txt
10.9.0.7
10.1.0.8
10.2.0.7
10.3.0.7
10.11.0.9
10.0.0.7
10.8.0.7
10.5.0.7
10.25.0.7
[root@lamp01 chenyansong]# sort -t "." -k2 test_sort.txt
10.0.0.7
10.1.0.8
10.11.0.9
10.2.0.7
10.25.0.7
10.3.0.7
10.5.0.7
10.8.0.7
10.9.0.7
[root@lamp01 chenyansong]# sort -r -t "." -k2 test_sort.txt
10.9.0.7
10.8.0.7
10.5.0.7
10.3.0.7
10.25.0.7
10.2.0.7
10.11.0.9
10.1.0.8
10.0.0.7
[root@lamp01 chenyansong]# sort -rn -t "." -k2 test_sort.txt
10.25.0.7
10.11.0.9
10.9.0.7
10.8.0.7
10.5.0.7
10.3.0.7
10.2.0.7
10.1.0.8
10.0.0.7

#表示第2个字段的第一个字符开始排序到第3个字段的第1个字符结束
[root@lamp01 chenyansong]# sort -t "." -k 2.1,3.1 test_sort.txt
10.11.0.9
10.11.2.8
10.11.8.7
10.2.0.7
10.25.0.7
10.3.0.7
10.5.0.7
10.8.0.7
10.9.0.7

# -t "." -k 1,1   用点来做分隔符,表示第一个字段开始排序到第一个字段结束
```

> 面试题:

```
#将文件test_sort_2.log中的域名取出并更具域名进行计数排序处理

[root@lamp01 chenyansong]# awk -F / '{print $3}' test_sort_2.log
www.baidu.com
www.baidu.com
post.baidu.com
mp3.baidu.com
www.baidu.com
post.baidu.com

[root@lamp01 chenyansong]# awk -F / '{print $3}' test_sort_2.log|sort
mp3.baidu.com
post.baidu.com
post.baidu.com
www.baidu.com
www.baidu.com
www.baidu.com

[root@lamp01 chenyansong]# awk -F / '{print $3}' test_sort_2.log|sort|uniq -c
      1 mp3.baidu.com
      2 post.baidu.com
      3 www.baidu.com

[root@lamp01 chenyansong]# awk -F / '{print $3}' test_sort_2.log|sort|uniq -c|sort -r
      3 www.baidu.com
      2 post.baidu.com
      1 mp3.baidu.com



```
