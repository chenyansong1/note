---
title: Linux基础命令之dumpe2fs(查看block和inode大小)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



``` shell
dumpe2fs
 
[root@linux-study cys_test]# dumpe2fs /dev/sda1|grep -iE "block size|inode size"
dumpe2fs 1.41.12 (17-May-2010)
Block size:               1024
Inode size:               128
```






