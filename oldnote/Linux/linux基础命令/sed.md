---
title: Linux基础命令之sed
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


# 1.语法
``` shell
功能描述：过滤输出文件的内容（取行）
sed -n ‘/过滤的内容/处理的命令’ 文件
-n　表示取消sed的默认输出
-p print 打印
-d delete 删除
-i 改变文件的内容(重点)
-r, --regexp-extended (可以使用正则，不用转义)  use extended regular expressions in the script.
```


# 2.-d 删除不显示
``` shell
 [root@linux-study cys_test]# cat err.a
-bash: ech: command not found
dfsdfsfsd
 
[root@linux-study cys_test]# sed '/dfs*/d' err.a
-bash: ech: command not found
 
[root@linux-study cys_test]# sed '/com*/d' err.a
dfsdfsfsd
 
[root@linux-study cys_test]# cat err.a
-bash: ech: command not found
dfsdfsfsd
```

# 3.取行
``` shell
[root@linux-study cys_test]# cat sed_tst.txt
chenyansong2
chenyansong3
chenyansong4
chenyansong5
chenyansong6
 
// -n表示取消默认的输出
[root@linux-study cys_test]# sed -n '3p' sed_tst.txt
chenyansong4
 
[root@linux-study cys_test]# sed -n '1,3p' sed_tst.txt
chenyansong2
chenyansong3
chenyansong4
[root@linux-study cys_test]# 
```


# 4.全局查找和替换
``` shell
[root@linux-study cys_test]# cat sed_tst.txt
chenyansong2
chenyansong3
chenyansong4
chenyansong5
chenyansong6
 
// -i 表示改变文件内容，s 表示search，g表示global
[root@linux-study cys_test]# sed -i 's#song#he#g' sed_tst.txt
[root@linux-study cys_test]# cat sed_tst.txt
chenyanhe2
chenyanhe3
chenyanhe4
chenyanhe5
chenyanhe6
 
[root@linux-study cys_test]# sed -ri 's#(chen)yanhe*#\1#g' sed_tst.txt  #查找到的是chenyanhe,然后取组: (chen),替换掉查找到的内容
[root@linux-study cys_test]# cat sed_tst.txt
chen2
chen3
chen4
chen5
chen6
 
/*
注意： s 是查找和替换，用一个字符串去替换匹配到的另一个字符串，
#是分隔符，可以使用@ /  等替换
-r 表示不用对需要转义的字符进行转义，如这里的括号等
\1 是取到的第一个分组
*/
```



# 5.增加
* a 追加文本到指定行后
* i插入文本到指定行前
* -i是修改文件内容
``` shell
[root@lamp01 chenyansong]# sed -i '2a testadd' test3.txt
[root@lamp01 chenyansong]# cat test3.txt                
aaaa
 
testadd
aa cc dd
AAA


[root@lamp01 chenyansong]# sed '2a testadd2222' test3.txt  
aaaa
 
testadd2222
testadd
aa cc dd
AAA


[root@lamp01 chenyansong]# cat test3.txt
aaaa
 
testadd
aa cc dd
AAA


[root@lamp01 chenyansong]# sed '2i testadd2222' test3.txt
aaaa
testadd2222
 
testadd
aa cc dd
AAA

[root@lamp01 chenyansong]# cat test3.txt
aaaa
 
testadd
aa cc dd
AAA
```






