---
title: 磁盘inode原理
categories: linux   
tags: [linux]
---



[TOC]



# 1.低级格式化

&emsp;&emsp;低级格式化（磁盘厂商来做的），用来划分磁道、扇区，但是并没有任何的分区，需要我们自己来分区


# 2.磁盘分区(partition)
&emsp;&emsp;将磁盘划分成多个逻辑的组成结构，可以在分区的基础上创建独立的文件系统



![分区表示意图](https://github.com/chenyansong1/note/blob/master/img/linux/磁盘/inode.png?raw=true)



# 3.MBR引导程序
1. Main Boot Record 主引导记录
2. 位置：0盘面0磁道1扇区，512bytes
3. 组成：
   446bytes：BootLoader 引导加载器（一段引导程序）
   64bytes：每16byte：标识一个分区，所以之后4个主分区
   2bytes：Magic Number  标记MBR是否有效


# 4.inode
> 磁盘分区之后，要进行文件系统的选择，为了方便我们寻找到数据，文件系统将一个分区划分为两块：
一个是存储元数据存储区，用来标示数据存储在哪里
另一个就是数据存储区


根的inode是自引用的，已知的
目录也是以块来存储的，块中有目录下的文件名和文件名对应的inode号


## 4.1.inode逻辑结构示意图

![分区表示意图](https://github.com/chenyansong1/note/blob/master/img/linux/磁盘/inode_2.bmp?raw=true)





如果是目录的话，块中的数据应该如下：

![image-20180909103324945](https://github.com/chenyansong1/note/blob/master/images/linux/filesystem/block_file.png?raw=true)







iNode的结构如下：

![image-20180909103535620](/var/folders/bx/_yxq3xys79d7tk10_qp5qk5w0000gn/T/abnerworks.Typora/image-20180909103535620.png)

直接引用磁盘块，间接引用，二级引用

Mode：权限

Owner info:属主，属组

Size：大小

一级间接指针；二级间接指针(Double Indirect)；三级间接指针（Triple Indirect）；直接指针



因为一个iNode中的指针的个数是有限的，所以一个iNode指向的block的个数也是有限的，而一个block的大小也是固定的，那么一个iNode代表的文件的大小是可以确定的，这就是文件的最大大小限制的由来






## 4.2.文件查找的过程

![分区表示意图](https://github.com/chenyansong1/note/blob/master/img/linux/磁盘/inode_3.bmp?raw=true)



## 4.3.创建文件的过程

创建一个文件的过程：touch   /backup/test.txt   大小10k (而每个block是2k)

1. 首先在inode 的bitmap上找一个空闲的inode，然后将该inode号(112)占用
2. 找到：/backup目录对应的块，在目录对应的块上写文件名(test.txt)和对应的inode号(112)
3. 找到块位图，找空闲的块，将其分配给inode，更新inode中块的位置

存储连续块的好处：这样磁头可以不用跳跃，直接访问，所以当我们删除文件的时候，会有磁盘碎片的产生，这样访问的时候，会有磁头的跳跃，然后会很慢

## 


## 4.4.文件删除的过程(rm)

删除一个文件的过程：rm -f /backup/test.txt
1. 将：/back中的 文件名和inode号（112）删除
2. 将inode的位图中该inode（112）标记为0
3. block的位图中对应的块标记为0（未使用）

以上删除并没有删除block中的数据，只是标记了数据被删除，那么只有在下一次进行写满对应的block的时候，原来的数据才会
被彻底覆盖



## 4.5.文件复制的过程(cp)
复制文件的过程：
就是在创建了一个文件的时候，然后将需要复制的文件填充到新创建的文件中

## 4.6.文件剪切(mv)
剪切的过程（如果是在同一个文件系统下）那么只是改变的目录中文件的路径，文件的iNode号和文件的内容并没有发生改变，但是如果是跨分区的话，是需要将该分区中的文件复制到指定的分区，然后将原分区中的文件删除






> 对于经常访问的文件，我们可以将其放入到缓存中，如：/var/log/messages  --->对应的inode号，这样下次将可以直接来访问了




# 5.超级块和块组
最上面的是超级块，然后就是块组，每一个块组中包含（inode，inode 的位图，block位图）

![分区表示意图](https://github.com/chenyansong1/note/blob/master/img/linux/磁盘/inode_4.png?raw=true)





![image-20180909102812957](https://github.com/chenyansong1/note/blob/master/images/linux/filesystem/super_block.png?raw=true)



* BootBlock ： 每个分区的第0个块，多文件系统的时候回用到，这个块是不会存储数据的
* SuperBlock：存放的是：有多少个块组；每个块组中包含多少个块，块大小，空闲磁盘块，已用iNode；早期的SuperBlock会在每个BlockGroup中存一个，但是现在不是了，只是在某些BlockGroup中存就行了，做备份使用，默认情况下，我们是读取的第0个块组中的SuperBlock

* GDT：Group Description Table 块组描述表：每个块组从第几个块开始，到第几个块结束；每个块组的块组名称；





# 链接文件



* 硬链接

  /backup/a/m.txt, 和 /backup/b/n.txt是**指向的同一个iNode**，这样的文件彼此之间称之为硬链接，如果删除一个指向，还有一个文件拥有该iNode的引用，因此我们还是可以访问文件的

```
[webuser@VM_0_4_centos ~]$ ll -i /etc/rc.local 
459086 lrwxrwxrwx 1 root root 13 3月  19 11:35 /etc/rc.local -> rc.d/rc.local

# 459086 是/etc/rc.local 的iNode号
# 1表示硬链接数

```





* 符号链接

  比如有一个文件的本来存放的是文件数据，但是这个iNode中的block中存放的是另外一个文件的路径名称（**相当于快捷方式的形式**），这样符号连接文件的大小就是这个文件指向的文件的文件名的长度

```
lrwxrwxrwx 1 root root 13 3月  19 11:35 /etc/rc.local -> rc.d/rc.local

# 1表示硬链接的次数
# 1.我们可以看到文件的大小就是他执行的文件的文件名的大小
# 2.链接文件的权限是777，这说明我们每个人都可以看到这个链接文件，但是我们是否可以使用链接文件指向的文件，这就是另外一回事了
```



* 创建链接

```
ln  [-s  -v] src dest


硬链接
# ln -s 创建硬链接
# 只能对文件创建硬链接
# 不能跨文件系统

软连接
# ln -s 是创建软连接
# 可应用于目录
# 可以跨文件系统
# 不会增加被链接文件的链接次数
# 其大小为指向文件的路径字符个数


```





#  设备文件



设备文件会作为设备的访问入口



* 按块为单位，随机访问的设备，如：硬盘

* 按字符为单位，线性设备，如：键盘



/dev

​	主设备号(major numer)：标识设备类型

​	次设备号（minor number)：标识同一种类型中不同设备

* 创建设备文件名

```markdown
man mknod

NAME
       mknod - make block or character special files

SYNOPSIS
       mknod [OPTION]... NAME TYPE [MAJOR MINOR]
      
option
	-m mode #指定权限
mknod mydev b 66 0   # name=mydev;type=b;major=66;minor=0
mknod -m 640 mydev b 66 0    # -m 指定文件的权限640
```



* 硬盘设备的设备文件名

IDE,ATA: hd

SATA: sd

SCSI: sd

USB: sd

​	a,b,c...来区别同一种类型下的不同设备

IDE:

​	第一个IDE口：主，从

​		/dev/hda, /dev/hdb

​	第二个IDE口：主，从

​		/dev/hdc, /dev/hdd



hda:

​	hda1:第一个主分区

​	hda2:

​	hda3

​	hda4

​	hda5:逻辑分区一定是从5开始的



* 查看当前系统识别了几块硬盘

```
# 查看所有的设备
fdisk -l 

# 查看指定设备
fdisk -l /dev/sda


```



* VFS(Virtual FileSystem)



![image-20180908221648460](https://github.com/chenyansong1/note/blob/master/images/linux/filesystem/vfs.png?raw=true)





为了兼容不同文件系统，Linux在命令和文件系统之间加了一层叫做虚拟文件系统



每一个分区都可以有不同的文件系统

其他的分区，可以通过挂载根下的目录来关联，从而我们可以通过目录来访问分区，所以根所在的分区是至关重要的，因为我们总是从根出发找到分区



# ext3文件系统



journal file system:日志文件系统，能够加快文件系统的修复



在写元数据的时候，会首先将元数据写在日志区中，然后待文件写完成再将日志区中的元数据移动到左侧的元数据区中

![image-20180908221648460](https://github.com/chenyansong1/note/blob/master/images/linux/filesystem/journal.png?raw=true)




