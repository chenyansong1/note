---
title: Linux基础命令之查看命令所在的路径(locate,which,whereis)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



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

