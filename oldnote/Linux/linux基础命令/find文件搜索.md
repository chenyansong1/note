---
title: Linux基础命令之find文件搜索
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


# 1.find语法
```
find [搜索范围] [匹配条件]
```


# 2.根据文件名搜索(-name)
``` shell
[root@linux-study ~]# find /cys_test/ -name "err.a"
/cys_test/err.a
/cys_test/cy1/err.a
 
 
#模糊匹配文件
[root@linux-study ~]# find /cys_test/ -name "er*"
/cys_test/err.a
/cys_test/cy1/err.a
/cys_test/cy1/cy1/error.txt
```

# 3.根据文件大小搜索
``` shell
在Linux中是以数据块来区分文件大小的，一个数据块是512 字节，即0.5k
find /cys_test/ -size +n    #+n 大于；-n小于；n等于

[root@linux-study ~]# find /cys_test/ -size +6
/cys_test/
/cys_test/cy1
/cys_test/cy1/changename
/cys_test/cy1/cy1
[root@linux-study ~]# 

```

# 4 根据文件所有者或者所属组搜索
``` shell
[root@linux-study ~]# find /cys_test/ -user root
[root@linux-study ~]# find /cys_test/ -group root

```


# 5 根据时间属性来查找
``` shell

#在/etc下查找5分钟内被修改过属性的文件和目录
find /etc -cmin -5
 
/*
其中：-amin   访问时间 access
-cmin   文件属性 change （就是定义文件的元数据）
-mmin  文件内容 modify
*/


```

# 6.查询n天前被修改的文件
``` shell
       -mtime n
              File’s  data  was last modified n*24 hours ago.  See the comments for -atime to understand how rounding
              affects the interpretation of file modification times.

#+n表示n天之前被修改过的文件，-n：反之，n表示前n天的当天
[root@linux-study cys_test]# find ./ -type f -mtime -1
./people.txt
[root@linux-study cys_test]#

-atime #n为数字, 意义为在n天之前的[一天之内]被access过的档案 
-ctime #n为数字, 意义为在n天之前的[一天之内]被change过的档案 
-mtime #n为数字, 意义为在n天之前的[一天之内]被modify过的档案 
-newer file #file为一个存在的档案,意思是说:只要档案比file还要新,就会被列出来
```

![find 参数 -mtime 解析](http://ols7leonh.bkt.clouddn.com//assert/img/linux/基础命令/find.png "find 参数 -mtime 解析")



# 7.连接选项：-a 或者是 -o    表示 ：and     or
``` shell
[root@localhost tmp]# find /etc -name init* -a -type d    #表示名字以init开头的并且是目录
/etc/init
/etc/rc.d/init.d

```

# 8.根据文件类型查找
``` shell
find -type f
#f 文件；d 目录；l 软链接文件
 

[root@linux-study cys_test]# find /cys_test/ -type f

```


# 9.根据路径层级查找（maxdepth）
``` shell
#查找层级深度为1的目录文件
[root@linux-study cys_test]# find ./ -maxdepth 1 -type d   
./
./ee
./aa
[root@linux-study cys_test]# 

```

# 10.一个实例
``` shell
#删除一个目录下的所有的文件，但是保留指定文件
#假设这个目录是/xx/，里面有file1,file2,file3..file10  十个文件

[root@oldboy xx]# touch file{1..10}
[root@oldboy xx]# ls
file1  file10  file2  file3  file4  file5  file6  file7  file8  file9

[root@oldboy xx]# ls
file1  file10  file2  file3  file4  file5  file6  file7  file8  file9


[root@oldboy xx]# find /xx -type f ! -name "file10"|xargs rm -f
[root@oldboy xx]# ls
file10

#或者
[root@oldboy xx]# find /xx -type f ! -name "file10" -exec rm -f {} \;    
[root@oldboy xx]# ls
file10

```

另外,下面三个的区别:

```

-amin n　　查找系统中最后N分钟访问的文件
-atime n　　查找系统中最后n*24小时访问的文件
-cmin n　　查找系统中最后N分钟被改变文件状态的文件
-ctime n　　查找系统中最后n*24小时被改变文件状态的文件
-mmin n　　查找系统中最后N分钟被改变文件数据的文件
-mtime n　　查找系统中最后n*24小时被改变文件数据的文件

```

参见:

https://javawind.net/p132



