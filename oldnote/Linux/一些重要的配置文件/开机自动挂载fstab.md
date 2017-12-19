---
title: Linux一些重要的配置文件之开机自动挂载fstab
categories: Linux   
toc: true  
tags: [Linux重要配置文件]
---


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

