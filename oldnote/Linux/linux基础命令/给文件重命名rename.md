---
title: Linux基础命令之给文件重命名rename
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



# 语法
``` shell
#语法格式:
rename from to file

from    #需要替换或者是需要处理的字符(一般是文件名的一部分也包括扩展名)
to    #把前面from的部分替换成to部分的内容
file    #待处理的文件,可以用*处理所有文件

```

# 实例

```
[root@lamp01 tardir]# touch stu_{0..5}.jpg
[root@lamp01 tardir]# ll
总用量 0
-rw-r--r-- 1 root root 0 2月  13 15:01 stu_0.jpg
-rw-r--r-- 1 root root 0 2月  13 15:01 stu_1.jpg
-rw-r--r-- 1 root root 0 2月  13 15:01 stu_2.jpg
-rw-r--r-- 1 root root 0 2月  13 15:01 stu_3.jpg
-rw-r--r-- 1 root root 0 2月  13 15:01 stu_4.jpg


[root@lamp01 tardir]# rename "jpg" "html" ./*.jpg
[root@lamp01 tardir]# ll
总用量 0
-rw-r--r-- 1 root root 0 2月  13 15:01 stu_0.html
-rw-r--r-- 1 root root 0 2月  13 15:01 stu_1.html
-rw-r--r-- 1 root root 0 2月  13 15:01 stu_2.html
-rw-r--r-- 1 root root 0 2月  13 15:01 stu_3.html
-rw-r--r-- 1 root root 0 2月  13 15:01 stu_4.html


```

# 批量修改文件名
```
#方式1: 先拼接mv ，然后交给bash去执行
[root@lamp01 tardir]# ls aa_finished_*.jpg
aa_finished_0.jpg  aa_finished_2.jpg  aa_finished_4.jpg
aa_finished_1.jpg  aa_finished_3.jpg  aa_finished_5.jpg

[root@lamp01 tardir]# ls aa_finished_*.jpg|awk -F "_finished" '{print "mv "$0}'
mv aa_finished_0.jpg
mv aa_finished_1.jpg
mv aa_finished_2.jpg
mv aa_finished_3.jpg
mv aa_finished_4.jpg
mv aa_finished_5.jpg
[root@lamp01 tardir]# ls aa_finished_*.jpg|awk -F "_finished" '{print "mv "$0" " $1$2}'
mv aa_finished_0.jpg aa_0.jpg
mv aa_finished_1.jpg aa_1.jpg
mv aa_finished_2.jpg aa_2.jpg
mv aa_finished_3.jpg aa_3.jpg
mv aa_finished_4.jpg aa_4.jpg
mv aa_finished_5.jpg aa_5.jpg

[root@lamp01 tardir]# ls aa_finished_*.jpg|awk -F "_finished" '{print "mv "$0" " $1$2}'|bash
[root@lamp01 tardir]# ll
总用量 0
-rw-r--r-- 1 root root 0 2月  13 15:11 aa_0.jpg
-rw-r--r-- 1 root root 0 2月  13 15:11 aa_1.jpg
-rw-r--r-- 1 root root 0 2月  13 15:11 aa_2.jpg
-rw-r--r-- 1 root root 0 2月  13 15:11 aa_3.jpg
-rw-r--r-- 1 root root 0 2月  13 15:11 aa_4.jpg
-rw-r--r-- 1 root root 0 2月  13 15:11 aa_5.jpg


#方式2:使用for 循环
for file in `ls aa_finished_*.jpg`;do
    mv $file `echo $file|sed 's#_finished##g' `
done



#方式3:rename
rename "_finished" "" aa_finished_*.jpg

```


