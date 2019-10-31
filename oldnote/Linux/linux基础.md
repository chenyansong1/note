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

* 文件查找

  ```shell
  find / -name filname #按文件名查找(支持通配符: hel*, hel?,)
  		 / -type f  #按文件类型查找(f:file， 目录d, b, c,p,  s)
  		 / -size +10k  (-10k 小于10k, 单位M)
  		 / -size +10M -size -100M  (>10M && <100M)
  		 
  ```

* 文件内容查找

  ```shell
  #找到对应的文件
  grep "xxx" -rn dir/
  ```

* 软件安装

  ```shell
  
  sudo apt-get install tree
  
  sudo apt-get remove tree
  
  #更新的是软件列表
  sudo apt-get update
  ```

  ![image-20191031194155378](/Users/chenyansong/Documents/note/images/linux/command/image-20191031194155378.png)

  ![image-20191031194217371](/Users/chenyansong/Documents/note/images/linux/command/image-20191031194217371.png)

  ```shell
  #删除下载的安装包
  sudo apt-get clean
  # /var/cache/apt/archives
  ```

  ```shell
  #deb包安装
  sudo dpkg -i xxx.deb #安装
  sudo dpkg -r xxx #卸载
  ```

  ```shell
  #源码安装
  ```

  ![](/Users/chenyansong/Documents/note/images/linux/command/image-20191031194815769.png)

* 挂载

  ```shell
  mount /dev/sdb1 /mnt
  umount /mnt
  ```

  

* 压缩解压缩

  ```shell
  gzip  # .gz 格式的压缩包
  bzip  # .bz2 格式的压缩包
  
  #tar 不适用z / j 参数，只能对文件或目录进行打包
  tar zcvf #c-- 创建
  				 #x -- 释放
  				 #v -- 显示详情
  				 #f --指定压缩文件的名字
  				 #z -- 使用gzip的方式压缩文件 -- .gz
  				 #j -- 使用bzip2的方式压缩文件 -- .bz2
  
  tar zcvf xxx.tar.gz dir/
  tar zcvf xxx.tar.gz *.txt
  
  tar jcvf xxx.tar.bz2 dir/
  
  #解压缩
  tar jxvf xxx.tar.bz2
  tar zxvf xxx.tar.gz
  
  #解压到指定的目录中
  tar zxvf xxx.tar.gz -C test-dir
  
  #rar, 需要安装 apt-get install rar
  ##参数
  a #压缩
  x #解压缩
  
  ##压缩
  rar a xxxx(tmp) 压缩的文件或目录 #最终生成的是xxxx.rar 
  
  ##解压缩
  rar x xxx.rar [解压缩目录]
  
  
  #zip
  ##参数
  -r #递归操作
  
  ##压缩
  zip 压缩包的名字  压缩的文件或目录
  
  ##解压缩
  unzip  压缩包的名字
  unzip  压缩包的名字 -d 解压的目录
  ```

  ![](/Users/chenyansong/Documents/note/images/linux/command/image-20191031205234421.png)

  ![](/Users/chenyansong/Documents/note/images/linux/command/image-20191031205438683.png)