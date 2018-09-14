转自：http://wangying.sinaapp.com/archives/2301

[TOC]



Linux用户安装Linux操作系统时遇到的一个最常见的难以决定的问题就是如何正确地给评估各分区大小，以分配合适的硬盘空间。而遇到出现某个分区空间耗尽时，解决的方法通常是使用符号链接，或者使用调整分区大小的工具(比如PatitionMagic等)，但这都只是暂时解决办法，没有根本解决问题。因此完美的解决方法应该是在零停机前提下可以自如对文件系统的大小进行调整，可以方便实现文件系统跨越不同磁盘和分区。幸运的是Linux提供的逻辑盘卷管理(LVM，LogicalVolumeManager)机制就是一个完美的解决方案。 



## LVM简介

LVM是逻辑盘卷管理（Logical Volume Manager）的简称，它是Linux环境下对磁盘分区进行管理的一种机制，LVM是建立在硬盘和分区之上的一个逻辑层，来提高磁盘分区管理的灵活性。
LVM的工作原理其实很简单，它就是通过将底层的物理硬盘抽象的封装起来，然后以逻辑卷的方式呈现给上层应用。在传统的磁盘管理机制中，我们的上层应用是直接访问文件系统，从而对底层的物理硬盘进行读取，而在LVM中，其通过对底层的硬盘进行封装，当我们对底层的物理硬盘进行操作时，其不再是针对于分区进行操作，而是通过一个叫做逻辑卷的东西来对其进行底层的磁盘管理操作。比如说我增加一个物理硬盘，这个时候上层的服务是感觉不到的，因为呈现给上层服务的是以逻辑卷的方式。
LVM最大的特点就是可以对磁盘进行动态管理。因为逻辑卷的大小是可以动态调整的，而且不会丢失现有的数据。如果我们新增加了硬盘，其也不会改变现有上层的逻辑卷。作为一个动态磁盘管理机制，逻辑卷技术大大提高了磁盘管理的灵活性。



## LVM的原理

### 单一硬盘系统LVM图一

在一个硬盘上创建多个逻辑卷，对创建好的卷调整大小,然后将它们挂载在’/home,/var,/tmp’目录。

![](E:\note\images\linux\filesystem\lvm-yuanli.png)



PV（Physical Volume）- 物理卷 

物理卷在逻辑卷管理中处于最底层，它可以是实际物理硬盘上的分区，也可以是整个物理硬盘，也可以是raid设备,是LVM的基本存储逻辑块，但和基本的物理存储介质(如分区、磁盘等)比较，却包含有与LVM相关的管理参数。 

VG（Volumne Group）- 卷组 

卷组建立在物理卷之上，一个卷组中至少要包括一个物理卷，在卷组建立之后可动态添加物理卷到卷组中。一个逻辑卷管理系统工程中可以只有一个卷组，也可以拥有多个卷组。 

PE（physical extent）：每一个物理卷被划分为称为PE(Physical Extents)的基本单元，具有唯一编号的PE是可以被LVM寻址的最小单元。PE的大小是在VG过程中配置的，默认为4MB。 

LVM 默认使用4MB的PE区块，而LVM的LV最多仅能含有65534个PE (lvm1 的格式)，因此默认的LVM的LV最大容量为4M*65534/(1024M/G)=256G。PE是整个LVM 最小的储存区块，也就是说，其实我们的资料都是由写入PE 来处理的。简单的说，这个PE 就有点像文件系统里面的block 大小。所以调整PE 会影响到LVM 的最大容量！不过，**在 CentOS 6.x 以后，由于直接使用 lvm2 的各项格式功能，因此这个限制已经不存在了**。 

LV（Logical Volume）- 逻辑卷 逻辑卷建立在卷组之上，卷组中的未分配空间可以用于建立新的逻辑卷，逻辑卷建立后可以动态地扩展和缩小空间。系统中的多个逻辑卷可以属于同一个卷组，也可以属于不同的多个卷组。 简单来说就是： 

PV:是物理的磁盘分区 

VG:LVM中的物理的磁盘分区，也就是PV，必须加入VG，可以将VG理解为一个仓库或者是几个大的硬盘。 

LV：也就是从VG中划分的逻辑分区 



### 多硬盘系统LVM图二

从另外一块硬盘增加额外的卷到LVM中。 



![](E:\note\images\linux\filesystem\lvm-yuanli2.png)

![](E:\note\images\linux\filesystem\lvm-yuanli3.png)



## 系统环境

```
#实验环境：VMware
#系统平台：CentOS release 6.5 (Final)
#设备类型：分区、物理硬盘、raid 设备
##检查磁盘状况
[root@centos7 ~]#  fdisk -l
Disk /dev/sda: 21.5 GB, 21474836480 bytes, 41943040 sectors
Units = sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disk label type: dos
Disk identifier: 0x000297f0

   Device Boot      Start         End      Blocks   Id  System    #system表示分区类型
/dev/sda1   *        2048     1026047      512000   83  Linux     #正常分区
/dev/sda2         1026048    11266048    10240000   8e  Linux LVM #lvm格式化分区

Disk /dev/mapper/centos_centos7-root: 10 GB
Disk /dev/mapper/centos_centos7-swap: 2147 MB
##查看分区挂载
[root@centos7 ~]# df -alh
Filesystem                       Size  Used Avail Use% Mounted on
/dev/mapper/centos_centos7-root   10G  1.2G  8.8G   7% /  #挂载lvm逻辑卷
/dev/sda1                        497M  123M  374M  25% /boot
```



## 安装LVM



```
$ yum -y install lvm;
```

