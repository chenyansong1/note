[TOC]



# tmp



## basename

```shell
[root@soc30 bvs]# basename /tmp/me.txt 
me.txt

[root@soc30 bvs]# basename /tmp/me.txt .txt
me

```



## dirname

```shell
# 这里还是拿到的是上一层目录，并没有智能的拿到 该拿到的目录
[root@soc30 bvs]# dirname /usr/local/workspace/component/kafka/
/usr/local/workspace/component

[root@soc30 bvs]# dirname /usr/local/workspace/component/kafka/kafka_2.11-0.10.2.0.tar.gz 
/usr/local/workspace/component/kafka
```

## locate

```
[root@study ~]# locate [-ir] keyword
选项与参数：
-i ：忽略大小写的差异；
-c ：不输出文件名，仅计算找到的文件数量
-l ：仅输出几行的意思，例如输出五行则是 -l 5
-S ：输出 locate 所使用的数据库文件的相关信息，包括该数据库纪录的文件/目录数量等
-r ：后面可接正则表达式的显示方式

范例一：找出系统中所有与 passwd 相关的文件名，且只列出 5 个
[root@study ~]# locate -l 5 passwd
/etc/passwd
/etc/passwd-
/etc/pam.d/passwd
/etc/security/opasswd
/usr/bin/gpasswd

范例二：列出 locate 查询所使用的数据库文件之文件名与各数据数量
[root@study ~]# locate -S
Database /var/lib/mlocate/mlocate.db:
8,086 directories # 总纪录目录数
109,605 files # 总纪录文件数
5,190,295 Bytes in file names
2,349,150 Bytes used to store database

# updatedb
他是经由数据库来搜寻的，而数据库的创建默认是在每天执行一次 （每个 distribution 都不同，CentOS 7.x 是每天更新数据库一次！） ，所以当你新创建起来的文件， 却还在数据库更新之前搜寻该文件，那么 locate 会告诉你“找不到！”呵呵！因为必须要更新数据库呀！

# updatedb原理
updatedb：根据 /etc/updatedb.conf 的设置去搜寻系统硬盘内的文件名，并更新/var/lib/mlocate 内的数据库文件；
locate：依据 /var/lib/mlocate 内的数据库记载，找出使用者输入的关键字文件名

```





## 通配符

```properties
* : 表示任意字符，任意长度
? : 任意单个字符 ;ls ?y*  // 任意一个字符+y+xxxx
[]:匹配指定范围内的任意单个字符； [abc], [a-z], [a-zA-Z], [0-9] ;
[^]:匹配指定范围之外的任意单个字符
[0-9a-zA-Z]:数字，字母，开头的

文件名中有空白的文件名
[:space:] 表示空白字符，那么[[:space:]] 表示任意的一个空白字符
[:lower:] 表示所有的小写字母
[:upper:] 表示所有的大写字母
[:alpha:] 表示所有的大小写字母
[:digit:] 表示数字
[:alnum:] 表示数字和大小写字母 ： alpha + number
[:punct:] 表示所有的标点符号


字母开头，字母结尾，中间有空格字符
[[:alpha:]]*[[:space:]]*[[:alpha:]
```

