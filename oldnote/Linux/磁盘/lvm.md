[TOC]



# DM

Device Mapper 

linear

mirror

snapshot

multipath

动态扩展逻辑空间

http://wangying.sinaapp.com/archives/2310



http://wangying.sinaapp.com/archives/2336





![image-20180912231105035](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm.png)



![image-20180912231838528](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm2.png)



PV ： 可以是：物理磁盘，分区，raid

VG ：卷组

Logical Volume：卷组本身是没有文件系统的，所以在卷组的基础上有了 Logical Volume （逻辑卷），逻辑卷相当于一个分区



PE: Physical Extend 盘区，这就是将物理的存储设备分成块（chunk），而这些chunk在 Logical Volume 上叫做LE



```
pv
pvcreate , pvremove, pvscan, pvdisplay, pvmove

vg
vgcreate, vgremove, vgextend, vgreduce, vgdisplay |vgs

vgcreate VG_name /path/to/pv
	-s # : PE大小，默认是4M
	


lv
lvcreate, lvremove, lvextend, lvreduce, lvresize, lvs or lvdisplay


lvcreate -n Lv_name -L #G|M VG_name

lvcreate -L 50M -n testlv myvg

lvs
lvdisplay /dev/myvg/testlv


#格式化
mke2fs -j /dev/myvg/testlv
#挂载
mount /dev/myvg/testlv /mnt

#其实是/dev/mapper/myvg-testlv 挂载到了 /mnt下

```

![image-20180913213039205](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm3.png)

 

![ima](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm4.png)









![image-20180913213201865](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm5.png)



移除lv

![image-20180913213403026](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm6.png)



因此先卸载

umount /mnt

lvremove  /dev/myvg/testlv

lvs



# 减小vg



```

#减少vg
#先将数据移走
pvmove /dev/sda11
#将pv从vg中移除
vgreduce myvg /dev/sda11
#查看vg中是否移除
vgs
#查看pvs中是否移除
pvs

#最后pv移除
pvremove /dev/sda11
```





![image-20180913211351341](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm7.png)

![image-20180913211441498](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm8.png)

![image-20180913211535939](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm9.png)



# 扩展VG



```
#首先创建一个pv
pvcreate /dev/sda12

#扩展vg
vgextend myvg /dev/sda12

#查看vg
vgs

```

![image-20180913211828813](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm10.png)





# 扩展逻辑卷



先扩展 物理边界， 再扩展 逻辑边界

```
resize2fs - ext2/ext3/ext4 file system resizer

#至5G
lvextend -L 5G /dev/myvg/testlv

#扩展物理边界,增加3G
lvextend -L +3G /dev/myvg/testlv

#扩展逻辑边界
resize2fs -p /dev/myvg/testlv

```



# 缩减逻辑卷

1. 不能在线缩减：得先卸载
2. 确保缩减后的空间，依然能够存储原有的数据
3. 在缩减之前应该强行检查文件系统，以确保文件系统处于一致性状态



```
#缩减逻辑边界
resize2fs /path/to/pv 3G

#缩减物理边界
lvreduce -L [-]# /path/to/lv

#重新挂载逻辑卷


#1.df -lh 查看逻辑卷的使用大小和剩余大小

#2.umount /path/to/lv

#3.e2fsck -f /dev/myvg/testlv 强制文件系统检查

#4.resize2fs /dev/myvg/testlv 3G 缩减至3G

#5.lvreduce -L 3G /dev/myvg/testlv

#6.mount -a 重新读取 /etc/fstab重新挂载

```







# 快照卷

1. 主要是用来备份的
2. 生命周期为整个数据时长，在这段时长内，数据的增长量不能超出快照卷大小
3. 快照卷应该是只读的
4. 跟原卷在同一卷组内



```
lvcreate 
	-n :指定快照的名称
	-s :表示快照
	-p r|w :r只读；w只写

# 指定 对/path/to/lv逻辑卷创建快照
lvcreate -L # -n SLv_name -p r /path/to/lv


#对 /dev/myvg/testlv 这个逻辑卷创建名称为 testlv-snap 的快照
lvcreate -L 50M -n testlv-snap -s -p r /dev/myvg/testlv

```

![image-20180913230328597](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm11.png)



挂载快照卷

![image-20180913230406579](/Users/chenyansong/Documents/note/images/linux/filesystem/lvm12.png)



备份快照卷

```
tar -zcf  xx.tar.gz  /mnt/inittab issue
```



卸载快照卷

umount   /mnt



移除逻辑卷



lvremove /dev/myvg/testlv-snap



