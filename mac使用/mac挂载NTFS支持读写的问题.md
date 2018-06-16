mac挂载NTFS支持读写的问题



参照此方法在自己的Mac上试验成功，现记录此方法如下(略有改动)：

本机环境： 
macOS Sierra version 10.12.6 
2017 款 MacBook Pro 
2017.9.10

此方法对10.6以前版本的OS理论上是无效的 
苹果理论上是支持NTFS的，修改下原生配置文件就可以实现读写ntfs

**流程简介**

1. 挂载上你的NTFS硬盘，查看硬盘名称
2. 编辑/etc/fstab文件，使其支持NTFS写入
3. 将/Volumes中的NTFS磁盘快捷方式到Finder

**详细流程**

1. 插上硬盘后，查看你的硬盘名称，这里假设名称是AngleDisk，牢记之（你的可不是这个呀！！）
2. 打开Applications的Terminal, 你也可以直接spotlight输入terminal打开
3. 在终端输入`sudo nano /etc/fstab` 敲击回车
4. 现在你看到了一个编辑界面，输入`LABEL=AngleDisk none ntfs rw,auto,nobrowse`后，敲击回车，再Ctrl+X，再敲击Y，再敲击回车
5. 此时，退出你的移动硬盘，再重新插入，你会发现磁盘没有显示再桌面或是Finder之前出现的地方，别慌
6. 打开Finder，Command+Shift+G，输入框中输入`/Volumes`，回车，你就可以看到你的磁盘啦！是可以读写的哟，Enjoy
7. 方便起见，你可以直接把磁盘拖到Finder侧边栏中，这样下次使用就不用进入到/Volumes目录打开了

Enjoy !!



参见：

https://blog.csdn.net/u013247765/article/details/77932144

https://www.zhihu.com/question/19571334/answer/224291746