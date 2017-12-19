---
title: Linux基础命令之查看命令所在的路径(locate,which,whereis)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



# 1.locate(查询文件所在路径)

```
#功能描述：在文件资料库中查找文件
语法：locate 文件名
 
#范例1：
[root@linux-study cys_test]# ll
total 4
-rw-r--r--. 1 root root 51 Jul 12 20:02 people.txt
[root@linux-study cys_test]# locate people.txt
/cys_test/people.txt
 
#范例2：
//用locate去查找本身
locate locate
//会发现这样的文件，用locate就是在该文件中进行搜索，该文件会定期更新
/var/lib/mlocate/mlocate.db
 
 
#范例3：
//创建一个文件，然后用locate去查询，但是查询不到，
/var/lib/mlocate/mlocate.db  The database searched by default.
这时就要升级文件资料库（mlocate.db），来帮助我们找到对应的文件。
[root@localhost /]# touch /home/lingzhiling/chenyansong.list
[root@localhost /]# locate chenyansong.list
[root@localhost /]# updatedb        #更新
[root@localhost /]# locate chenyansong.list
/home/lingzhiling/chenyansong.list
 
#范例4：不区分大小写，查找
//locate不区分大小写
[root@linux-study cys_test]# locate peoplE.txt
[root@linux-study cys_test]# locate -i peoplE.txt
/cys_test/people.txt
[root@linux-study cys_test]#

#注意：但是如果你要找的文件在/tmp 临时文件中，那么它不会去搜索的到的。

```

# 2.which(搜索命令所在的目录和别名)
```
#功能描述：搜索命令所在的目录及别名信息
语法：which 命令
 
#范例：
[root@linux-study cys_test]# which cp
alias cp='cp -i'
        /bin/cp
[root@linux-study cys_test]# 
```


# 3.whereis(搜索命令所在目录及帮助文档路径)
```
#功能描述：搜索命令所在目录及帮助文档路径
语法：whereis 命令名称
 
#范例：
[root@linux-study cys_test]# whereis cp
cp: /bin/cp /usr/share/man/man1p/cp.1p.gz /usr/share/man/man1/cp.1.gz
[root@linux-study cys_test]# 

```

