[TOC]

转自：https://www.cnblogs.com/gaochaoweino/p/10277172.html

新安装的Ubuntu18.04出现无法启动的现象,于是乎查阅度娘,重新安装grub2就可以;我用外置移动硬盘安装的Ubuntu,这样我可以将移动硬盘插在其他电脑上,同样可以启动.



挂载的移动硬盘分区为sda;系统分区为sda1;

![img](https://img2018.cnblogs.com/blog/1371693/201901/1371693-20190116145105291-32310621.png)

## 步骤如下:

### 1.将/home / /boot　分区均挂载在/mnt上;我这里都是在/dev/sda1上,于是:

```
sudo mount /dev/sda1 /mnt
```

### 2.接下来将主系统的配置文件与当前系统bind起来

```
mount –bind /dev /mnt/dev 
mount –bind /proc /mnt/proc 
mount –bind /sys /mnt/sys 
mount –bind /usr/ /mnt/usr 
```

### 3.将/mnt作为当前系统的根目录

```
sudo chroot /mnt
```

### 4.安装grub

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
卸载掉旧的grub

apt-get purge grub-pc

安装新的

apt-get install grub-pc

grub-mkconfig

grub-install /dev/sda

exit

umount /proc

umount /dev

umount /sys

umount /dev/sda1

umount /dev/sda7

umount /dev/sda6

reboot
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

### 5.最后一步,更新grub

```
update grub
```

