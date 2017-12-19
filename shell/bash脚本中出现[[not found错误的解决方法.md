转自：http://www.cnblogs.com/njuzhoubing/p/4186190.html

今天在写脚本的时候，发生了一个奇怪的问题：在脚本中使用[[的时候报错“[[: not found”。遇到问题自然是解决问题。

# 使用的bash版本太低？
```
bash --version查看bash版本信息如下

lee@lee:~$bash --version

GNU bash, version 3.2.39(1)-release (i486-pc-linux-gnu)

Copyright (C) 2007 Free Software Foundation, Inc.
```
在google bash手册，3.2.39已经不算低了，完全支持[[这样的扩展。看来不是版本问题。

# 是脚本中[[使用错误？

写测试脚本进行测试。test.sh测试脚本内容如下
```
#!/bin/bash

[[ 1 ]] && echo "successful"
```
执行后仍然是“[[: not found”。但是，在bash交互模式下执行[[ 1 ]] && echo "successful"命令，却是成功的，执行结果如下
```
lee@lee:~$ [[ 1 ]] && echo "successful"

successful

lee@lee:~$
```
看来bash是支持[[扩展的，那么，问题应该就是出在脚本上。

# 脚本里的问题存在于哪里呢？

显然，那条孤零零的命令是没问题的，因为已经在交互模式下验证过了。脚本里还有一行#!/bin/bash

用来指定运行该脚本所使用的shell类型。显然，我们这里就是要使用bash，所以这一行也没有问题。


# 既然脚本的内容没有问题了，那问题究竟在哪里呢？

从编写和运行等几个环节仔细思考，脚本既然没问题，那问题是不是出在 运行环节上？出于习惯，我经常喜欢$ sh test.sh这样的运行脚本的 方式，那么，换一种运行方式是不是能解决问题呢？在终端下用./test.sh运行，果然，运行成功！至此，问题的症结找到。

## 下面的问题是，为什么sh test.sh与./test.sh有着不同的运行结果。

通过查看(ls -l /bin)得知，sh只是一个符号链接，最终指向是一个叫做dash的程序
```
lee@lee:~$ ls -hl /bin | grep sh

-rwxr-xr-x 1 root root 686K 2008-05-13 02:33 bash

-rwxr-xr-x 1 root root  79K 2009-03-09 21:03 dash

lrwxrwxrwx 1 root root    4 2010-03-03 01:52 rbash -> bash

lrwxrwxrwx 1 root root    4 2010-03-03 01:53 sh -> dash

lrwxrwxrwx 1 root root    4 2010-03-03 01:53 sh.distrib -> bash
```
 

在运行sh test.sh时，首先调用sh命令，而sh指向dash，因此，sh test.sh相当于/bin/dash test.sh。而dash不管是名称还是程序大小，都与bash不同。那么，sh test.sh与./test.sh两种命令有了不同的执行结果也就不足为奇。

在执行./test.sh命令时，bash会自动生成一个subshell来执行该命令，即执行filename arguments等 同于执行bash filename arguments。

## 还剩下的一个问题是，dash与bash究竟有什么区别？

Ubuntu wiki上给出了答案。自Ubuntu 6.10以后，系统的默认shell /bin/sh被改成了dash。dash(the Debian Almquist shell)是一个比bash小很多但仍兼容POSIX标准的shell，它占用的磁盘空间更少，执行shell脚本比bash更快，依赖的库文件更少，当然，在功能上无法与bash相比。dash来自于NetBSD版本的Almquist Shell(ash)。

Ubuntu中将默认shell改为dash的主要原因是效率。由于Ubuntu启动过程中需要启动大量的shell脚本，为了优化启动速度和资源使用情况，Ubuntu做了这样的改动。

 

## 如何避免dash引起的问题？

1.如果dash仅仅只影响到几个shell脚本，则最方便的解决方法是修改脚本，在脚本中指定正确的shell解释器

#!/bin/sh 改为  #!/bin/bash

2.如果dash影响到的是Makefile文件，则需要在文件开始指定以下变量

SHELL = /bin/bash
3.如果被影响的范围较广，以至于修改单独的几个文件无法解决问题，此时 可以终止将dash安装为/bin/sh

sudo dpkg-reconfigure dash
需要注意的是，这样修改将会影响到系统的启动速度，甚至会影响到一些 依赖于dash独有特性的脚本（这些特性bash没有提供）。

## 如何在编写脚本时避免这些影响？

首先，可以使用checkbashisms命令检查脚本是否是“bash化”(bashism)的。要使用checkbashisms，需先安装devscripts包

aptitude install devscripts

checkbashisms test.sh

其次，在编写脚本时需要注意使用符合POSIX标准的语法，从而使脚本成为"POSIX shell"。
