---
title: Linux基础命令之find文件搜索
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



[TOC]

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
[root@localhost /]# updatedb        #更新,会很慢
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

# 1.find语法

```
find [搜索范围] [匹配条件]

find [查找路径] [查找标准] [处理动作]

查找路径：默认是当前目录
查找标准：默认指定路径下的所有文件
	-name 'filename' ：对文件名做精确匹配
		文件名通配：
			* ： 任意长度的任意字符
			？：
			[]
	-iname 'filename' :表示文件名匹配不区分大小写
	-user hadoop  :查找用户为hadoop的文件,根据属主查找
	-group groupname:根据数组查找
	-uid uid:根据uid查找
	-gid gid:根据gid查找
	-nouser:没有属主的用户
	-nogroup:没有属组的文件
	
	-type filetype
		f: 普通文件
		d:目录
		c:字符设备
		b：块设备
		l:符号链接
		p:管道设备
		s:套接字文件
		
	-size:根据文件大小查找，默认单位是字节
		[+|-]#k: eg： [+]10k 大于10k；10k 精确为10k
		[+|-]#M
		[+|-]#G
		
		eg:find /etc -size 10k  ;//会显示9-10k之间的数据
		   find /etc -size -10k ;//显示所有小于10k的数据
		   find /etc -size +10k ;//显示所有大于10k的数据
	
	组合条件：
		-a :两个条件同时满足
		-o :or #两个条件的默认形式
		-not :非；eg:查找非目录的文件--> find /etc -not -type d
		
处理动作：默认为显示
	-print 显示(默认)
	-ls:类似于ls -l 的显示每一个文件的详细信息
	-ok command {} \;   :一定要“\;"
	-exec command {} \; :一定要“\;","{}"表示找到的文件
	
	// -ok和-exec的区别在于，每一次操作都要确认，而exec不需要确认
	
	#给找到的文件添加w权限
	eg:find ./ -perm -006 -exec chmod o-w {} \;  
	// {}表示找到的文件，即：chmod o-w filename \;
	
	#给找到的文件，修改文件名
	find ./ -perm -006 -exec mv {} {}.new \;
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



# 查找文件内容

```shell
find . -name “*.in” | xargs grep “thermcontact”
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
find /etc -cmin -5 //小于5分钟，就是过去5分钟到现在，这段时间内

find /etc -cmin +5 //大于5min钟的
 
/*
其中：
-amin [+|-]#  访问时间 access
-cmin [+|-]#   文件属性 change （就是定义文件的元数据）
-mmin [+|-]#  文件内容 modify

-atime [+|-]# :这个单位是：天
-ctime [+|-]#
-mtime [+|-]#
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

# 11.正则查找

```Shell

# 找到以 .txt结尾的文件
#find  /Users/chenyansong/Desktop/test-dir  -type f -regex  ".*\.\(txt\)" 
/Users/chenyansong/Desktop/test-dir/1.txt


```



# 12.找到指定文件并拷贝到指定的目录下



```shell

chenyansongdeMacBook-Pro:test-dir chenyansong$ pwd
/Users/chenyansong/Desktop/test-dir

# 找到以 .txt结尾的文件
#find  /Users/chenyansong/Desktop/test-dir  -type f -regex  ".*\.\(txt\)" 
/Users/chenyansong/Desktop/test-dir/1.txt

# 将以 .log 结尾的文件 copy到 /Users/chenyansong/Desktop/test-dir/1/ 目录下
#find  /Users/chenyansong/Desktop/test-dir  -type f -regex  ".*\.\(txt\)" -exec cp -rp {} /Users/chenyansong/Desktop/test-dir/1/  \;

```



# 权限查找



```
-perm mode : 精确匹配
	  /mode:只要有一位一个匹配就行；-perm /644 ,只要644中有一为可以匹配就行
	  -mode:所有的位都要匹配；-perm -644 ;那么755也是可以匹配的，因为644有的755都有
```



```
find ./ -perm 644



```



# 13.执行额外的命令

```shell
#给找到的文件添加w权限
eg:find ./ -perm -006 -exec chmod o-w {} \;  
// {}表示找到的文件，即：chmod o-w filename \;

#给找到的文件，修改文件名
find ./ -perm -006 -exec mv {} {}.new \;
	
find / -name "*test.log" -exec ls -l {} \;
```



![](E:\note\images\linux\command\find.png)



该范例中特殊的地方有 {} 以及 \; 还有 -exec 这个关键字，这些东西的意义为： 

{} 代表的是“由 find 找到的内容”，如上图所示，find 的结果会被放置到 {} 位置中； 

-exec 一直到 \; 是关键字，代表 find 额外动作的开始 （-exec） 到结束 （\;） ，在这中 间的就是 find 指令内的额外动作。 在本例中就是“ ls -l {} ”啰！ 

因为“ ; ”在 bash 环境下是有特殊意义的，因此利用反斜线来跳脱。    

参见:

https://javawind.net/p132



# xargs

> build and execute command lines from standard input
>
> 执行来自标准输入的命令



```
find /etc -size +1M -exec echo {} >> /tmp/etc.largefiles \;

#or

find /etc -size +1M | xargs echo >> /tmp/etc.largefiles \;

```

