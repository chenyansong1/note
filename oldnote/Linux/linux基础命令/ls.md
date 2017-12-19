---
title: Linux基础命令之ls
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



# 1.语法
```
#功能描述：显示目录文件
语法：ls 选项[-aldh] [文件或目录]
   -a all显示所有文件，包括隐藏文件
   -l long详细信息显示，长格式
   -d 查看目录属性
    -h  human  
```



# 2.ll所查看的文件各个字段的属性含义

![分区表示意图](http://ols7leonh.bkt.clouddn.com//assert/img/linux/基础命令/ls.png)


* -表示的是一个文件，d 表示的是一个目录，l表示的是一个软连接
* 文件将用户分成三类：user 所有者，group 说数组，other 其他人
* 和selinux 相关：当selinux开着，有点的存在



# 3.显示文件的权限

![分区表示意图](http://ols7leonh.bkt.clouddn.com//assert/img/linux/基础命令/ls_2.png)
![分区表示意图](http://ols7leonh.bkt.clouddn.com//assert/img/linux/基础命令/ls_3.png)
 

# 4.只显示目录本身的信息
```
[root@localhost boot]# ls -ld /etc
drwxr-xr-x. 102 root root 12288 10月 31 05:50 /etc
```

# 5.ll -F 给目录文件添加后缀
```

[root@lamp01 tardir]ll
总用量 4
-rw-r--r-- 1 root root    0 2月  13 15:11 aa_0.jpg
-rw-r--r-- 1 root root    0 2月  13 15:11 aa_1.jpg
drwxr-xr-x 2 root root 4096 2月  13 15:59 test

[root@lamp01 tardir]ll -F
总用量 4
-rw-r--r-- 1 root root    0 2月  13 15:11 aa_0.jpg
-rw-r--r-- 1 root root    0 2月  13 15:11 aa_1.jpg
drwxr-xr-x 2 root root 4096 2月  13 15:59 test/

```

# 6.-p给目录加上/
```
[root@lamp01 tardir]ll
总用量 4
-rw-r--r-- 1 root root    0 2月  13 15:11 aa_0.jpg
-rw-r--r-- 1 root root    0 2月  13 15:11 aa_1.jpg
drwxr-xr-x 2 root root 4096 2月  13 15:59 test
[root@lamp01 tardir]ll -lp
总用量 4
-rw-r--r-- 1 root root    0 2月  13 15:11 aa_0.jpg
-rw-r--r-- 1 root root    0 2月  13 15:11 aa_1.jpg
drwxr-xr-x 2 root root 4096 2月  13 15:59 test/
[root@lamp01 tardir]

```

# 7.按修改时间倒序
```
[root@lamp01 tardir]ll
总用量 8
-rw-r--r-- 1 root root    0 2月  13 15:11 aa_0.jpg
-rw-r--r-- 1 root root    4 2月  13 16:02 aa_1.jpg
drwxr-xr-x 2 root root 4096 2月  13 16:01 test


[root@lamp01 tardir]ll -rt ./
总用量 8
-rw-r--r-- 1 root root    0 2月  13 15:11 aa_0.jpg
drwxr-xr-x 2 root root 4096 2月  13 16:01 test
-rw-r--r-- 1 root root    4 2月  13 16:02 aa_1.jpg

#-r reverse 反转排序, -t按修改时间排序
```

# 8.显示长格式的修改时间
```
[root@lamp01 tardir]ll --time-style=long-iso
总用量 8
-rw-r--r-- 1 root root    0 2017-02-13 15:11 aa_0.jpg
-rw-r--r-- 1 root root    4 2017-02-13 16:02 aa_1.jpg
drwxr-xr-x 2 root root 4096 2017-02-13 16:01 test

```

# 9.查看文件的inode编号
inode：index node 索引节点编号，他可以是文件或者是目录，
在磁盘里的唯一标识，Linux读取文件首先要读取到这个索引节点。书的目录
 
```
[root@lamp01 tardir]ls -li ./
总用量 0
 7198 -rw-r--r-- 1 root root 0 2月  13 15:11 aa_0.jpg    # inode号7198
22672 -rw-r--r-- 1 root root 0 2月  13 15:11 aa_1.jpg
22677 -rw-r--r-- 1 root root 0 2月  13 15:11 aa_2.jpg
 
```


