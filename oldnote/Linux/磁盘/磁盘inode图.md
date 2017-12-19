---
title: 磁盘inode原理
categories: linux   
tags: [linux]
---


# 1.低级格式化
&emsp;&emsp;低级格式化（磁盘厂商来做的），用来划分磁道、扇区，但是并没有任何的分区，需要我们自己来分区


# 2.磁盘分区(partition)
&emsp;&emsp;将磁盘划分成多个逻辑的组成结构，可以在分区的基础上创建独立的文件系统


![分区表示意图](http://ols7leonh.bkt.clouddn.com//assert/img/linux/磁盘/inode.png)



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

![分区表示意图](http://ols7leonh.bkt.clouddn.com//assert/img/linux/磁盘/inode_2.bmp)



## 4.2.创建文件的过程
创建一个文件的过程：touch   /backup/test.txt   大小10k (而每个block是2k)
1. 首先在inode 的bitmap上找一个空闲的inode，然后将该inode号(112)占用
2. 找到：/backup目录对应的块，在目录对应的块上写文件名(test.txt)和对应的inode号(112)
3. 找到块位图，找空闲的块，将其分配给inode，更新inode中块的位置

存储连续块的好处：这样磁头可以不用跳跃，直接访问，所以当我们删除文件的时候，会有磁盘碎片的产生，这样访问的时候，会有磁头的跳跃，然后会很慢


## 4.3.文件查找的过程

![分区表示意图](http://ols7leonh.bkt.clouddn.com//assert/img/linux/磁盘/inode_3.bmp)




## 4.4.文件删除的过程
 
删除一个文件的过程：mv /backup/test.txt
1. 将：/back中的 文件名和inode号（112）删除
2. 将inode的位图中该inode（112）标记为0
3. block的位图中对应的块标记为0（未使用）
 
以上删除并没有删除block中的数据，只是标记了数据被删除，那么只有在下一次进行写满对应的block的时候，原来的数据才会
被彻底覆盖



## 4.5.文件复制的过程
复制文件的过程：
就是在创建了一个文件的时候，然后将需要复制的文件填充到新创建的文件中
 
## 4.6.文件剪切
剪切的过程（如果是在同一个文件系统下）
那么只是改变的目录中文件的路径，


> 对于经常访问的文件，我们可以将其放入到缓存中，如：/var/log/messages  --->对应的inode号，这样下次将可以直接来访问了


# 5.超级块和块组
最上面的是超级块，然后就是块组，每一个块组中包含（inode，inode 的位图，block位图）

![分区表示意图](http://ols7leonh.bkt.clouddn.com//assert/img/linux/磁盘/inode_4.png)








