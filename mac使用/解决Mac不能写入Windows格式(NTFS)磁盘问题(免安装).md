

解决Mac不能写入Windows格式(NTFS)磁盘问题(免安装)



使用Mac的同学都知道Mac默认不能在NTFS格式的磁盘中写入内容。下面介绍一个简单的方法，简单几行命令解决所有问题。

大概的思路就是：Mac自动挂载的时候没有开放写权限，我们自己手动挂载一遍，把写权限加上。仅此而已！！！

### 第一步：查看磁盘设备文件名

这一步需要看一下，目标磁盘叫什么名字

```
diskutil list
```



![](/Users/chenyansong/Documents/note/images/mac/mount.png)

 可以看到我移动硬盘被挂载了disk2的位置上，其中Windows那个磁盘设备文件名为disk2s4

以上信息告诉我们：1. 在/dev目录下； 2. 设备名称为disk2s4

### 第二步：新建挂载点

其实他的意思也就是要告诉电脑，你这张盘要放在那里，就好像Windows电脑在你点击我的电脑之后可以看到所有的盘一样。
这里选择放在桌面。
其实是在桌面上新建一个叫Windows的文件夹：

```
mkdir ~/Desktop/Windows
```

### 第三步：推出磁盘（重新挂载）

Mac默认挂载的时候不可写磁盘，这里我们需要重新挂载一次，但是在此之前，需要先取消挂载（等同于鼠标右键菜单中的推出，但是不要选择推出全部）

```
sudo umount /dev/disk2s4
```

![](/Users/chenyansong/Documents/note/images/mac/moun2.png)

### 第四部：重新挂载

手动挂载

```
sudo mount_ntfs -o rw,nobrowse /dev/disk2s4 ~/Desktop/Windows
```

成功，磁盘可以正常读写了！！！