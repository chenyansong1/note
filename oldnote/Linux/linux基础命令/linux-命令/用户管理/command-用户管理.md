[TOC]



```shell
## command options...	arguments...

选项：有些选项是可以带参数的
	短选项
	长选项
	
参数

```



# 常用命令



## su



su : switch user : 转换用户

su	 [-l] 	用户名

exit 退出上面的登录（其实ctrl+d也退出来了）



## passwd



passwd 修改当前用户的密码；管理员可以修改其他用户的密码，可以设定任意的密码；但是普通用户设定的密码必须符合密码复杂性规则

> 数字
>
> 大写字母
>
> 小写字母
>
> 特殊字符
>
> 长度足够长



普通用户修改密码：passwd 回车两次，修改自己的密码





## ls

ls 列出指定路径下的目录和文件，如果没有指定路径，默认是当前目录



ls -l ： 长格式，显示完整信息
ls -h: 列出人类能够识别的格式(human readable)
ls -a:列出所有的文件(包括隐藏文件，通常隐藏文件是以 点开头的)
	. ： 当前目录
	..: 当前目录的上层目录

ls -d : 显示目录自身属性

ls -i : 显示文件的索引节点号(inode : index node)

ls -r :逆序显示文件

ls -R : 递归显示（recursive)



## cd

change directory:切换目录

cd ~ : 进入到该用户的家目录

cd ~username : 管理员进入到username的家目录下

cd - ： 进入到前一次目录和当前目录之间切换



## type

type：显示指定属于那种类型

type cd



命令类型

- 内置命令（shell内置： builtin）
- 外部命令：在文件系统的某个路径下有一个与命令名称相应的可执行文件，这样在shell中才能调用到，


事实上，当我们登录的时候，我们就进入了shell的客户端中，[root@mylab ~]##这个就是命令提示符，我们在这里输入命令，然后客户端给我们及时的反馈，而在shell中能够输入的命令就是内置命令（如：cd）



既然外部的命令对应一个可执行的文件，但是我们在调用的时候只是调用了命令的简写形式（如：ls 对应的文件在 ：/bin/ls ），那么系统是怎么通过ls 就知道，我们实际输入的是 /bin/ls ，这就要引入环境变量



## printenv

查看变量

![image-20180817233252184](/Users/chenyansong/Documents/note/images/linux/command/env.png)



环境变量：第一次会从PATH中寻找输入的命令，如我们第一次输入ls的时候，会将PATH下的路径遍历一遍，找到 ls 对应的文件路径，然后将该路径缓存，我们查看缓存使用 hash



## hash

查看命令的缓存，这里是一个hash列表：<ls， /bin/ls>



![image-20180817234022599](/Users/chenyansong/Documents/note/images/linux/command/hash.png)



hits:表示缓存命中了多少次



## pwd



pwd : printing Working directory

show  working directory；current directory



## date

硬件时钟：clock

系统时钟:   date

date:时间管理(系统时间)

ntp:时间服务器：network time procol 



date 

86400



![image-20180818113727953](/Users/chenyansong/Documents/note/images/linux/command/date.png)



## clock

显示硬件时间

hwclock -w : 修改硬件时间为 系统时间

hwclock -s : 读取硬件时间 到 系统时间



![image-20180818111922896](/Users/chenyansong/Documents/note/images/linux/command/hwclock.png)



## cal

显示日志

cal 2018   : 显示2018年的日历

cal 12 2018 : 显示2018年12月的日志

## man帮助

manual 手册

内部命令：

​	help command : help cd

外部命令：

​	command  - -help

man ls : 查看ls的帮助命令

分章节：

1：用户命令（/bin, /usr/bin, /usr/local/bin 下的命令）

2：系统调用： man 2 read, 使用whatis command 查看命令在哪些章节

3：库调用

4：特殊文件（设备文件）

5：文件格式（解释配置文件的语法）：man 5 passwd 可以看到生成的/etc/passwd的文件格式说明

6：游戏

7：杂项：Miscellaneous

8：管理命令（/sbin, /usr/sbin, /usr/local/sbin 下的命令）



<> : 必选

[] : 可选

… : 可以出现多次

| : 多选一



MAN: 

​	NAME:命令名称

​	SYNOPSIS: 用法说明，包括可用的选项

​	DESCRIPTION: 命令功能的详尽说明，可能包括每一个选项的意义

​	OPTIONS:说明每一个选项的意义

​	FILES: 此命令相关的配置文件

​	EXAMPELS:使用示例

​	

向前翻一屏：space

向前翻一屏：b

上一行：j

下一行：k

退出：q

查找：/keyword (向后)  or  ?keyword （向前），n(next), N（前一个）  显示下一个



## echo

echo : 打印一行（默认是带有换行的）

打印多行，需要使用-e 开启转义; \n是换行； \t 制表符

echo -e "The year is 2018, \nTody is 26."



-n : 取消 行末的换行



## Printf

格式化并打印

printf   "this is good day"   	默认是不换行的

printf   "this is good day。\nMoney is good day too.\n"



## whatis

查看命令出现在哪些章节



whatis ls