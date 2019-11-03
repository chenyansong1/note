[toc]

# C库函数

![](/Users/chenyansong/Documents/note/images/c_languge/C库IO函数工作流程.png)

> C库函数是在内部封装了一个I/O缓冲区，但是如果是系统的I/O函数是没有这样的缓冲区的



# 虚拟地址空间

![](/Users/chenyansong/Documents/note/images/c_languge/pcb和文件描述符.png)

![](/Users/chenyansong/Documents/note/images/c_languge/虚拟地址空间.png)

查看文件的格式

```shell
chenyansongdeMacBook-Pro:c_language chenyansong$ file a.out 
a.out: Mach-O 64-bit executable x86_64
chenyansongdeMacBook-Pro:c_language chenyansong$ file /tmp//sunlogin_helper.log 
/tmp//sunlogin_helper.log: ASCII text
chenyansongdeMacBook-Pro:c_language chenyansong$ 
```

![image-20191103111757079](/Users/chenyansong/Documents/note/images/c_languge/image-20191103111757079.png)



>  cpu 为什么要使用虚拟地址空间与物理地址空间映射？解决了什么样的问题？

1. 方便编译器和操作系统安排程序的地址分布。
   		程序可以使用一系列相邻的虚拟地址来访问物理内存中不相邻的大内存缓冲区。
   
2. 方便进程之间隔离
   不同进程使用的虚拟地址彼此隔离。一个进程中的代码无法更改正在由另一进程使用的物理内存。
   
3. 方便OS使用你那可怜的内存。
   程序可以使用一系列虚拟地址来访问大于可用物理内存的内存缓冲区。当物理内存的供应量变小时，
   内存管理器会将物理内存页（通常大小为 4 KB）保存到磁盘文件。数据或代码页会根据需要在物理内存与磁盘之间移动。
      		
   
      		

![](/Users/chenyansong/Documents/note/images/c_languge/image-20191103114054898.png)



# C库函数与系统函数的关系

![7_库函数与系统函数的关系](/Users/chenyansong/Documents/note/images/c_languge/库函数与系统函数的关系.png)