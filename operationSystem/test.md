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

