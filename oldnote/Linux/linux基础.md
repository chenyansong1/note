[TOC]



* 命令解释器

  ```shell
  shell--unix操作系统
  bash--linux操作系统  伯恩 again shell
  ```

本质：根据命令的名字，调用对应的执行程序




* linux快捷键

```shell
#历史
history


#比方向键高级
#历史命令向上遍历
ctrl+p

#历史命令向下遍历
ctrl+n

#光标移动
ctrl+b #光标向前移动：back
ctrl+f #光标向后移动：forward
ctrl+a #移动光标到头
ctrl+e #移动光标到行尾

#删除光标前的一个字符
ctrl+h == backspace

#删除光标后面的一个字符
ctrl+d  

#删除光标前的所有字符
ctrl+u

```



* Linux目录结构

  ```shell
  /lib : #动态链接库
  
  /mnt ： #可以让用户临时挂载的文件目录
  
  /proc ： #对进程的映射
  
  /sbin: #系统命令
  
  /usr #user soft resource 当前用户安装的soft目录
  
  /usr/bin  #用户使用的应用程序
  /usr/sbin #超级用户使用的比较高级的管理程序和系统守护程序
  
  /usr/src #内核源代码的默认放置目录
  
  /var #这个目录中存放着不断扩充的东西，包括各种日志文件 /var： 这是一个非常重要的目录，系统上跑了很多程序，那么每个程序都会有相应的日志产生，而这些日志就被记录到这个目录下，具体在/var/log 目录下，另外mail的预设放置也是在这里。
  ```

* 用户目录

  ```shell
  . - > #当前目录
  ..  ->  #当前的上一级目录
  -   -> #两个临近目录的直接切换
  cd ~ #进入家目录
  
  hadoop@tomcat:/home$
  #hadoop是登录用户
  #tomcat是hostname
  #/home当前用户的工作目录
  # $ 表示是普通用户
  # # 表示超级用户
  
  ```

* 查看文件内容

  ```shell
  #cat 查看文件所有内容
  
  #more 一页一页查看,回车跳一行，空格每次翻一页，不能向前翻页，q退出（或者ctrl+c）
  
  #less 回车跳一行，空格每次翻一页，ctrl+p（往前一行），ctrl+n(向后一行), ctrl+b（向前翻页），ctrl+f(向后翻页)
  
  #head ： head -5 filename :显示前5行，默认是10行
  
  #tail ：tail -20 filename 显示尾部20行，tail -f 监听文件变化
  ```

* 软链接

  ```shell
  ln -s hello.c new_hello.soft
  
  #我们也可以给目录创建快捷方式
  ln -s ~/dir-test dir.soft
  ll dir.soft
  ```
  
* 硬链接

  ```shell
  ln hello.c hello.hard
  #增加的是硬链接计数
  #硬链接，并不占用实际的存储空间，硬链接之间使用的是同一个inode
  #不能给目录创建硬链接
  ```

  ![1572396898157](E:\git-workspace\note\images\linux\command\1572396898157.png)

  ![1572397158970](E:\git-workspace\note\images\linux\command\1572397158970.png)

```shell
#行统计
wc -l

#
wc filename

7 19 83
#7row
#19 words
#83 byte

#二进制文件
od
```

![image-20191030224557720](/Users/chenyansong/Documents/note/images/linux/command/image-20191030224557720.png)

![image-20191030224635680](/Users/chenyansong/Documents/note/images/linux/command/image-20191030224635680.png)