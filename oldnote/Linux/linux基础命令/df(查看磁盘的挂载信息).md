---
title: Linux基础命令之df(查看磁盘的挂载信息)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


```
#查看磁盘挂载inode情况
[root@linux-study cys_test]# df -i
文件系统              Inode  已用(I)  可用(I) 已用(I)%% 挂载点
/dev/sda3             479552   51486  428066   11% /
tmpfs                 128621       1  128620    1% /dev/shm
/dev/sda1              51200      38   51162    1% /boot


#查看磁盘使用情况
[root@linux-study cys_test]# df -h
文件系统              容量  已用  可用 已用%% 挂载点
/dev/sda3             7.2G  1.3G  5.6G  18% /
tmpfs                 503M     0  503M   0% /dev/shm
/dev/sda1             194M   26M  158M  15% /boot

```








