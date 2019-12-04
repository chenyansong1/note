[TOC]

# GPT类型分区

查看服务器磁盘信息：vdc是新增的大于2T的未分区的磁盘

```shell
[root@0026 ~]# lsblk
NAME          MAJ:MIN RM  SIZE RO TYPE MOUNTPOINT
vda           253:0    0   40G  0 disk 
├─vda1        253:1    0    4G  0 part [SWAP]
└─vda2        253:2    0   36G  0 part /
vdb           253:16   0   60G  0 disk 
└─vdb1        253:17   0   60G  0 part 
  ├─is-boot 252:0    0  528M  0 lvm  /boot
  ├─is-opt  252:1    0   16G  0 lvm  /opt
  ├─is-log  252:2    0   16G  0 lvm  /srv/ftpd/log
  └─is-data 252:3    0   25G  0 lvm  /opt/mysql/data
vdc           253:32   0    3T  0 disk
```

分区

```shell

[root@0026 dev]# parted /dev/vdc     ##使用parted命令进行分区，进入parted分区工具。
GNU Parted 3.1
Using /dev/vdc
Welcome to GNU Parted! Type 'help' to view a list of commands.
(parted) mklabel gpt       ##输入mklable gpt，把vdc改成gpt大分区格式。                                                  
(parted) print          ##查看sdb分区状态。可以看到已经打上了gpt的标签。                                                  
Model: Virtio Block Device (virtblk)
Disk /dev/vdc: 3221GB
Sector size (logical/physical): 512B/512B
Partition Table: gpt
Disk Flags: 
 
Number  Start  End  Size  File system  Name  Flags
 
(parted) mkpart primary 0 3221gb  ##创建一个主分区，容量从0GB开始到3221GB的全部空间。                                        
Warning: The resulting partition is not properly aligned for best performance.
Ignore/Cancel? i                                                          
(parted) print    ##可查看分区                                                          
Model: Virtio Block Device (virtblk)
Disk /dev/vdc: 3221GB
Sector size (logical/physical): 512B/512B
Partition Table: gpt
Disk Flags: 
 
Number  Start   End     Size    File system  Name     Flags
 1      17.4kB  3221GB  3221GB               primary
 
(parted) quit   ##退出parted分区工具。                                                           
Information: You may need to update /etc/fstab

```

查看磁盘

```shell
[root@0026 dev]# lsblk                                                    
NAME          MAJ:MIN RM  SIZE RO TYPE MOUNTPOINT
vda           253:0    0   40G  0 disk 
├─vda1        253:1    0    4G  0 part [SWAP]
└─vda2        253:2    0   36G  0 part /
vdb           253:16   0   60G  0 disk 
└─vdb1        253:17   0   60G  0 part 
  ├─is-boot 252:0    0  528M  0 lvm  /boot
  ├─is-opt  252:1    0   16G  0 lvm  /opt
  ├─is-log  252:2    0   16G  0 lvm  /srv/ftpd/log
  └─is-data 252:3    0   25G  0 lvm  /opt/mysql/data
vdc           253:32   0    3T  0 disk 
└─vdc1        253:33   0    3T  0 part 
```



# gpt对齐

**解决parted分区时The resulting partition is not properly aligned for best performance报警**



How to align partitions for best performance using parted

```shell

There are two common problems when creating partitions in Linux on big storage arrays. The first is easy, and the warning message from fdisk is a bit of a giveaway:

WARNING: The size of this disk is 8.0 TB (7970004230144 bytes).
DOS partition table format can not be used on drives for volumes
larger than (2199023255040 bytes) for 512-byte sectors. Use parted(1) and GUID
partition table format (GPT).
The answer: use parted. Don’t have it? Install it!

The second problem is this warning from parted:

(parted) mklabel gpt
(parted) mkpart primary 0 100%
Warning: The resulting partition is not properly aligned for best performance.
Ignore/Cancel?
…and no matter what combination of numbers you use, the message just keeps coming back. It’s tempting to ignore it, but don’t.

There are a few posts on the subject, but this one from HP really gets to the guts of the problem.

Here’s a quick step-by-step guide to aligning partitions properly. It’s just an abstraction of the HP post, but hopefully easier to follow. This will work for most arrays (in fact it works for all the arrays that I’ve seen); there are more options in HP’s post, but I’ve included the most common configuration here.

1. Get the alignment parameters for your array (remember to replace sdb with the name of your device as seen by the kernel).

    # cat /sys/block/sdb/queue/optimal_io_size
    1048576
    # cat /sys/block/sdb/queue/minimum_io_size
    262144
    # cat /sys/block/sdb/alignment_offset
    0
    # cat /sys/block/sdb/queue/physical_block_size
    512
2. Add optimal_io_size to alignment_offset and divide the result by physical_block_size. In my case this was (1048576 + 0) / 512 = 2048.
3. This number is the sector at which the partition should start. Your new parted command should look like

    mkpart primary 2048s 100%
The trailing ‘s’ is important: it tells parted that you’re talking about sectors, not bytes or megabytes.

4. If all went well, the partition will have been created with no warnings. You can check the alignment thusly (replacing ’1′ with the partition number if necessary):

    (parted) align-check optimal 1                                           
    1 aligned
As I alluded to before, there are cases where this won’t work: if optimal_io_size is zero, for example, there are other rules to follow. Of course it would be nice if parted could do this—the values are all available as ioctls, after all—but then what would I write about? :)

#末尾的最后一条评论更加实用：
Apparently, using % causes parted to automatically align the sectors for best performance:

(parted) mkpart primary ext4 0% 100%
```



# CentOS的GPT分区+LVM挂载

为突破MBR分区限制（最大卷：2T，最多4个主分区或3个主分区加一个扩展分区）常常以GPT分区方式（突破MBR 4个主分区限制，每个磁盘最多支持128个分区，支持大于2T的分区，最大卷可达18EB）新建分区并挂载，下面记录自己常用的GPT+LVM的方式挂载新的硬盘的方式。

## 1.查看硬盘标签

使用fdisk -l 也可查看新的硬盘盘符，此处直接使用新的parted命令进行操作

```
> sudo parted // 进入parted交互模式
(parted) help // 打印帮助命令文档
...
(parted) print all // 打印所有存储设备
...
(parted) select /dev/sdb // 选择存储设备，此处以/dev/sdb为例
```

## 2. 创建一个GPT分区

```
(parted) mklable gpt // 设置gpt分区方式的磁盘标签
(parted) mkpart // 创建一个分区
...
(start) 0%
(end) 100%
...
(parted) quit
```

创建分区时，除上述列出的外，其它可使用默认参数，如分区格式ext2，后续可再进行格式化。

- 关于4k对齐

  Get the alignment parameters for your array (remember to replace sdb with the name of your device as seen by the kernel).

  ```
  # cat /sys/block/sdb/queue/optimal_io_size
  1048576
  # cat /sys/block/sdb/queue/minimum_io_size
  262144
  # cat /sys/block/sdb/alignment_offset
  0
  # cat /sys/block/sdb/queue/physical_block_size
  512
  ```

  Add optimal_io_size to alignment_offset and divide the result by physical_block_size. In my case this was (1048576 + 0) / 512 = 2048.This number is the sector at which the partition should start. Your new parted command should look like

  ```
  mkpart primary 2048s 100%
  ```

  The trailing ‘s’ is important: it tells parted that you’re talking about sectors, not bytes or megabytes. If all went well, the partition will have been created with no warnings. You can check the alignment thusly (replacing ‘1’ with the partition number if necessary):

  ```
  (parted) align-check optimal 1                                            
  1 aligned
  ```

  磁盘分区4k对齐问题，开始位置设置为“0%”，结束位置设置为“100%”，可以保证使用最大空间的前提下4k对齐。
  参考文章：

  1. https://rainbow.chard.org/2013/01/30/how-to-align-partitions-for-best-performance-using-parted/

  2. https://blog.csdn.net/open_data/article/details/44828741

     

     ## 3. 格式化新的分区

     服务器数据盘一般格式化xfs，一般桌面的系统可以用ext4，xfs单个分区的容量比ext4同等条件下大。

     ```
     > sudo mkfs.xfs /dev/sdb1 // xfs硬盘格式化
     或
     > sudo mkfs.ext4 /dev/sdb1 // ext4硬盘格式化
     ```

     ## 4. LVM划分加载硬盘

     创建物理卷

     ```
     # pvcreate /dev/sdb1
     ```

     将物理设备加入卷组

     ```
     # vgcreate vg_data /dev/sdb1
     ```

     *说明：可以将多个分区和到一起，如下*

     ```
     # vgcreate vg_data /dev/sdb1 /dev/sdc1
     ```

     使用整个卷组空间创建逻辑卷

     ```
     # lvcreate -l 100%FREE -n lv_data vg_data
     ```

     ## 5. 挂载逻辑卷设备

     ```
     # mount /vol/data/ /dev/vg_data/lv_data
     ```

## 6. 开机挂载

```
# vim /etc/fstab

// 新增一行
/dev/vg_dada/lv_data /vol/data    xfs               default,_netdev  0 0
```

说明：

|       磁盘分区       | 挂载目录  | 文件格式 |     访问方式     | fs_dump | fs_pass |
| :------------------: | :-------: | :------: | :--------------: | :-----: | :-----: |
| /dev/vg_data/lv_data | /vol/data |   xfs    | defaults,_netdev |    0    |    0    |

- 访问方式选项（多个选项间使用逗号分隔）有：
  - async：异步I/O
  - sync：同步I/O
  - auto: 是否能够自动挂载
  - dev/nodev：是否能创建设备文件
  - nouser（只有根用户可以装载）
  - exec/noexec：是否允许执行二进制程序
  - _netdev: 网络设备
  - acl： 文件访问控制列表
- 转储频率（fs_dump）：
  - 0：从不备份
  - 1：每日备份
  - 2：每隔一天备份
- fs_pass，该字段被fsck命令用来决定在启动时需要被扫描的文件系统的顺序（自检顺序）：根文件系统"/"对应该字段的值应该为1，其他文件系统应该为2-9顺序。若该文件系统无需在启动时扫描则设置该字段为0