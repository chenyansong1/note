---
title: Linux基础命令之stat显示文件系统状态
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



```
[root@linux-study cys_test]# stat /cys_test/test.txt
  File: "/cys_test/test.txt"
  Size: 7               Blocks: 8          IO Block: 4096   普通文件
Device: 803h/2051d      Inode: 130085      Links: 2
Access: (0644/-rw-r--r--)  Uid: (    0/    root)   Gid: (    0/    root)
Access: 2016-07-16 07:32:51.752587018 +0800
Modify: 2016-07-16 07:32:47.362510531 +0800
Change: 2016-07-16 07:33:08.963445624 +0800
[root@linux-study cys_test]#


Access: 访问时间 find -atime
Modify: 修改时间,内容发生变化,find -mtime
Change:    变化时间,包括Modify,权限,属主,用户组, find -ctime
 

```

取文件的权限:如644
```
[root@lamp01 chenyansong]# stat /etc/hosts
  File: "/etc/hosts"
  Size: 223             Blocks: 8          IO Block: 4096   普通文件
Device: 803h/2051d      Inode: 130078      Links: 2
Access: (0644/-rw-r--r--)  Uid: (    0/    root)   Gid: (    0/    root)
Access: 2017-02-12 16:00:01.123897002 +0800
Modify: 2016-08-31 21:03:38.328210183 +0800
Change: 2016-08-31 21:03:38.354798349 +0800

[root@lamp01 chenyansong]# stat -c %a /etc/hosts
644

[root@lamp01 chenyansong]# stat -c %A /etc/hosts
-rw-r--r--

更多的格式化,参见:man stat
       %a     Access rights in octal
 
       %A     Access rights in human readable form
 
       %b     Number of blocks allocated (see %B)
 
       %B     The size in bytes of each block reported by %b
 
       %C     SELinux security context string
 
       %d     Device number in decimal
......

#反正前面打印的信息,使用-c %xxx 都可以获取到

```