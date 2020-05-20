[toc]

如下图显示了Linux系统启动时程序或模块在内存中的位置

![](../images/os/image-20200520094406115.png)





如下图是各个模块在磁盘中的位置

![](../images/os/image-20200520095008243.png)

bootsect.S程序的功能描述

1. PC机加电后，ROM BIOS自检后，BOM BIOS会把引导扇区代码bootsect加载到内存地址0x7c00开始处，并跳到这里执行
2. bootsect将自己移动到0x90000开始处
3. 将setup模块加载到0x90200处
4. 利用BIOS中断0x13取磁盘参数表中当前引导盘的参数
5. 在屏幕上显示“Loading system...”
6. 把磁盘setup模块后面的system模块加载到内存0x10000开始处
7. 确定根文件系统的设备号，保存于root_dev中
8. 最后长跳转到setup程序开始处（0x90200）去执行setup程序

