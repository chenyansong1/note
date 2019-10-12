---
title: Linux一些重要的配置文件之开机自动挂载fstab
categories: Linux   
toc: true  
tags: [Linux重要配置文件]
---

[TOC]

&emsp;设置文件系统挂载信息的文件，开机能够自动挂载磁盘分区

# fstab各个字段的解释
```
[root@lamp01 chenyansong]# cat /etc/fstab
 
#
# /etc/fstab
# Created by anaconda on Sun Jul  3 20:57:31 2016
#
# Accessible filesystems, by reference, are maintained under '/dev/disk'
# See man pages fstab(5), findfs(8), mount(8) and/or blkid(8) for more info
#
UUID=ac0d7d40-5964-4813-b54c-7b9d2b967bc6 /                       ext4    defaults        1 1
UUID=f569bd10-8dda-42e9-83b0-78d172417788 /boot                   ext4    defaults        1 2
UUID=3eea297e-1107-415f-a34f-5aed0b17281c swap                    swap    defaults        0 0
tmpfs                   /dev/shm                tmpfs   defaults        0 0
devpts                  /dev/pts                devpts  gid=5,mode=620  0 0
sysfs                   /sys                    sysfs   defaults        0 0
proc                    /proc                   proc    defaults        0 0
#设备名称        挂载点(在哪儿)    文件系统类型    挂载选项    字段4    字段5
[root@lamp01 chenyansong]# 


/*
字段4：每多少天做一次备份（转储频率，0表示不备份，1每天都备份）
字段5：文件系统检测次序（0表示不检查，只有根可以为1，其他从2开始）
*/
```

# 挂载的两个方式
```
#方式1.mount命令
mount -t ext4 -o noexec /dev/sda1 /mnt    
# -t 是文件系统类型, -o是指选项

#方式2.vim /etc/fstab添加
/dev/sdb1               /mnt                    ext4    defaults        0 0
 #需要执行mount -a 加载/etc/fstab 文件使挂载生效

```



#  /etc/fstab文件出错,无法进入Linux系统

转自：https://blog.csdn.net/zjf280441589/article/details/39503479

## 问题描述

今天复习Linux文件系统管理，在Linux系统上挂载了一块新硬盘之后，然后分区，格式化，一步步走下来，为了能够使该硬盘在系统启动时自动挂载，于是将之写入了/etc/fstab文件，然而在reboot之后，Linux系统无法正常启动，系统显示的情况与下图类似(因为当时急于处理该故障,因此并未未截图,后来在网上找了几张图片,大体记录下自己的处理思路)

![img](E:\git-workspace\note\images\linux\command\fstab.png)

根据系统提示，可以看出是系统不能启动的真正原因是 /etc/fstab给写错了，系统启动报告Checking filesystems 失败,此时,根据系统提示,输入root密码进入repair filesystem模式

## 修复过程


尝试修改 /etc/fstab 发现系统是read-only模式

![img](E:\git-workspace\note\images\linux\command\fstab2.png)

```shell
#以可读写方式重新挂载文件系统
mount -o remount,rw /
```

重新修改/etc/fstab,修改出错处,如图[注意,最新的CentOS版本已经不再支持以该方式书写卷标了

![img](E:\git-workspace\note\images\linux\command\fstab3.png)

然后使用reboot命令重启系统