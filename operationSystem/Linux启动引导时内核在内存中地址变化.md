[toc]

转自：https://blog.csdn.net/Xsos77/article/details/18324441?utm_source=copy

以下描述为linux在i386类型CPU中的启动流程

![](/Users/chenyansong/Documents/note/images/os/SouthEast.png)



1、BIOS将可启动设备的第一个扇区(磁盘引导扇区，512字节)的代码（汇编语言编写的boot/**bootsect.s**）读入内存绝对地址0x7C00处，并跳转到0x7C00

2、于是这段代码开始执行，它把自己（这512字节代码）移到绝对地址0x90000处，并把启动设备中后2KB代码读入到内存0x90200处，而内核其它部分(system模块)则被读入到从地址0x10000开始处，**因为当时system模块长度不会超过0x80000字节大小（即512KB），所以不会覆盖0x90000处开始的bootsect和setup模块**

3、然后CPU运行到setup模块，setup模块将把system模块移动到内存起始处，这样system模块中代码的地址就等于实际的物理地址，便于对内核代码和数据的操作，然后进入保护模式并跳转到0x00000。

4、此时所有32位运行方式的设置启动都完成：IDT、GDT以及LDT被加载，处理器和协处理器也已确认，分页工作也设置好了，最终调用init/main.c中的main()程序。

——节选自《Linux内核完全注释》——赵烱