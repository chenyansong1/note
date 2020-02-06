

整理自

[慕课中国-哈工大-李治军-操作系统](https://www.icourse163.org/course/HIT-1002531008)



[TOC]



# 什么是操作系统

* 计算机硬件和应用之间的一层软件

  应用软件，操作系统，计算机硬件

  方便我们使用硬件，如：显存

  高效的使用硬件，如开多个终端

* 管理哪些硬件

  CPU管理

  内存管理

  终端管理

  磁盘管理

  文件管理

  网络管理

  电源管理

  多核管理



# 学习操作系统的层次

* 从应用层软件出发“探到操作系统”

  * 几种在使用计算机的接口上

  * 使用显示器：printf,使用CPU，fork，使用文件：open,read

    ![image-20200206122647922](/Users/chenyansong/Documents/note/images/os/image-20200206122647922.png)

* 从应用软件出发“进入操作系统”

  * 一段文字是如何写到磁盘上的

    ![image-20200206122731414](/Users/chenyansong/Documents/note/images/os/image-20200206122731414.png)

* 从硬件出发“设计到实现操作系统”

  * 给你一个板子，配一个操作系统	







# 操作系统基础

## 揭开钢琴的盒子

![image-20190212230251866](/Users/chenyansong/Documents/note/images/os/1.png)



将人计算3+2=5的过程，通过上图的机器进行模拟，也就是出来了最早期的图灵机





# BIOS(x86 PC)启动

1. x86 PC刚开机时CPU处于实模式
2. 开机时，CS=0xFFFF;IP=0x0000
3. 寻址0xFFFF0(ROM BIOS映射区)
4. 检查RAM，键盘，显示器，软硬磁盘
5. 将磁盘0磁道0扇区读入0x7c00处
6. 设置CS=0x07c0;IP=0x0000

![](/Users/chenyansong/Documents/note/images/os/image-20200206180129553.png)

实模式的寻址CS:IP (CS 左移4位+IP)



# 引导扇区

## 加载引导扇区代码

![](/Users/chenyansong/Documents/note/images/os/image-20200206181204450.png)

## 加载setup代码

![](/Users/chenyansong/Documents/note/images/os/image-20200206182652616.png)


![](/Users/chenyansong/Documents/note/images/os/image-20200206183641986.png)

