转自：http://wangying.sinaapp.com/archives/2336

[TOC]





对逻辑卷进行拉伸时，其实际就是向逻辑卷中增加PE的数量，而PE的数量是由VG中剩余PE的数量所决定的。下面我们通过处理以往遇到的多种情景来完成对逻辑卷的拉伸 



## 情景-：VG中PE数量能满足逻辑卷（lv）的拉伸要求

1. 目标需求：/home/www目录需要增加5G空间
2. 检查当前LVM分区容量

```
#查找目录对应挂载分区名
[root@localhost home]# df -alh
Filesystem               Size  Used   Avail   Use%    Mounted on
/dev/VolGroup/lvs_www    2.0G  35M    1.8G     2%      /home/www    #挂载生效，2G空间

#查找对应挂载分区所在的卷组(VG)
[root@localhost www]# lvs
  LV        VG       Attr       LSize Pool Origin Data%  Meta%  Move Log Cpy%Sync Convert
  lvs_www   VolGroup -wi-ao---- 2g 

#检查卷组VolGroup容量的
[root@localhost ~]# vgs
  VG       #PV #LV #SN  Attr   VSize VFree
  VolGroup   1   0   0  wz--n-  2g    10g   ##VG剩余10g
```

从上面可以看出，VG剩余10g足够满足lvs_www扩展5G空间。 



3. 空间扩展 

lvextend指令用于在线扩展逻辑卷的空间大小，而不中断应用程序对逻辑卷的访问，单位为“kKmMgGtT”字节。 

```
#给lvs_www增加5G空间
[root@localhost ~]# lvextend -L +5G /dev/VolGroup/lvs_www;
#或者设置lvs_www总容量为7G（2G+5G）
[root@localhost ~]# lvextend -L 7G /dev/VolGroup/lvs_www;
```





## 情景二：当卷组不够用的情况下，如何扩大卷组

方案一.检查当前硬盘是否还有未使用空间，并格式化新建分区。
方案二.新增硬盘并创建分区。
上面两个方案，都是新建分区并加入卷组VG中来扩大卷组,并再次通过卷组分配容量给逻辑卷。

1. 目标需求：/home/www目录需要增加5G空间
2. 新增硬盘(/dev/sdb)并创建分区



```
#检查新增硬盘(/dev/sdb)
[root@localhost ~]# fdisk -l
##只显示新增硬盘的相关信息，其他硬盘忽略！
Disk /dev/sdb: 10.7 GB, 10737418240 bytes
255 heads, 63 sectors/track, 1305 cylinders
Units = cylinders of 16065 * 512 = 8225280 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disk identifier: 0x00000000

#格式化硬盘
[root@localhost ~]# fdisk /dev/sdb
Command (m for help): N  ##新建分区命令:N
Command action
   e   extended
   p   primary partition (1-4)
P ##创建主分区:P
Partition number (1-4): 1
First cylinder (1-1305, default 1):
Using default value 1
Last cylinder, +cylinders or +size{K,M,G} (1-1305, default 1305): +5G  ##增加5G空间

Command (m for help): t  ##修改分区格式命令
Selected partition 1     ##修改上述操作创建的/dev/sdb1
Hex code (type L to list codes): 8e  #分区格式为8e
Changed system type of partition 1 to 8e (Linux LVM)

Command (m for help): w #创建完成后通过W命令写入磁盘分区表

#新分区sdb1创建完成
[root@localhost ~]# fdisk -l
##只显示新增硬盘的相关信息，其他硬盘忽略！
Disk /dev/sdb: 10.7 GB, 10737418240 bytes
~~~~~
   Device Boot      Start         End      Blocks   Id  System
/dev/sdb1               1         654     5253223+  8e  Linux LVM
```

重新加载磁盘

```shell
# 使用下面的命令可以看到当前内核已经识别了哪些分区
cat /proc/partitions

# 重读分区表
partprobe /dev/sdb
```



3. 分区sdb1格式化LVM并加入卷组 

```
#格式化物理卷(PV)-/dev/sdb1
[root@localhost ~]# pvcreate /dev/sdb1

#将sdb1加入VolGroup卷组
[root@localhost ~]# vgextend VolGroup /dev/sdb1
  Volume group "VolGroup" successfully extended
[root@localhost ~]# vgs
  VG       #PV #LV #SN Attr   VSize  VFree
  VolGroup   4   2   0 wz--n- 11.02g 5.11g
```

由上面操作可知，卷组新增5G容量。 

4. 空间扩展 

lvextend指令用于在线扩展逻辑卷的空间大小，而不中断应用程序对逻辑卷的访问，单位为“kKmMgGtT”字节。 

```
#给lvs_www增加5G空间
[root@localhost ~]# lvextend -L +5G /dev/VolGroup/lvs_www;
#或者设置lvs_www总容量为7G（2G+5G）
[root@localhost ~]# lvextend -L 7G /dev/VolGroup/lvs_www;
#resize2fs同步文件系统,通过df命令能看到容量改变
[root@localhost ~]# resize2fs /dev/VolGroup/lvs_www;
```



扩展的空间的时候，首先扩展的是物理边界（也就是逻辑卷），然后再扩展逻辑边界（resize2fs）

8、逻辑卷扩展后并不会马上生效，需要使用resize2fs命令重新加载逻辑卷的大小

eg：

```
centos6：
resize2fs /dev/mapper/vg_zgate192-LogVol01   
centos7：
xfs_growfs  /dev/mapper/vg_zgate192-LogVol01
```

9、查看磁盘分区：

```
fdisk -l
df -lh
```

10、可以看到扩容成功。

