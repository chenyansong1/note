[TOC]



# DM

Device Mapper 

linear

mirror

snapshot

multipath

动态扩展逻辑空间





![image-20180912231105035](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm.png)



![image-20180912231838528](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm2.png)



PV ： 可以是：物理磁盘，分区，raid

VG ：卷组

Logical Volume：卷组本身是没有文件系统的，所以在卷组的基础上有了 Logical Volume （逻辑卷），逻辑卷相当于一个分区



PE: Physical Extend 盘区，这就是将物理的存储设备分成块（chunk），而这些chunk在 Logical Volume 上叫做LE

