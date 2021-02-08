[toc]

# 创建GPT分区以支持大于2T的容量



**GPT是支持多个分区的，没有4个分区的限制**

分区，格式化

```shell
[root@zcwyou ~]# parted /dev/sdb


GNU Parted 3.1
使用 /dev/sdb
Welcome to GNU Parted! Type ‘help’ to view a list of commands.
创建gpt标签
(parted) mklabel gpt    #如果是一块新的磁盘需要第一步需要这个
创建10000M的分区，即10G
(parted) mkpart primary 0 10000     #创建一个主分区，指定起始和结束位置
警告: The resulting partition is not properly aligned for best performance.
忽略/Ignore/放弃/Cancel? I
查看分区表
(parted) print             #打印分区

Model: VMware, VMware Virtual S (scsi)
Disk /dev/sdb: 42.9GB
Sector size (logical/physical): 512B/512B
Partition Table: gpt
Disk Flags:


#重读分区表
[root@zcwyou ~]# partprobe   

#格式化GPT分区
[root@zcwyou ~]# mkfs -t ext4 /dev/sdb1


#查看分区信息
[root@zcwyou ~]# parted -l


#挂载文件系统
[root@centos7 ~]# mkdir /mnt/sdb1
[root@centos7 ~]# mount /dev/sdb1 /mnt/sdb1

#实现开机自动挂载文件系统
[root@zcwyou ~]# vi /etc/fstab 
```



# 报错

```shell
Warning: Not all of the space available to /dev/vdb appears to be used, you can fix the GPT to use all of the        ## 提示该磁盘GPT分区不是所有空间可用，建议修复 全部选择Fix
space (an extra 4194304000 blocks) or continue with the current setting? 
```



参考：

https://www.linuxrumen.com/rmxx/790.html

https://www.cnblogs.com/zh-dream/p/12685098.html

http://blog.sciencenet.cn/home.php?mod=space&uid=656335&do=blog&id=940537

