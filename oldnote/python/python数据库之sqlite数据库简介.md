---
title:  python数据库之sqlite数据库简介
categories: python   
toc: true  
tags: [python]
---


# 1.简介
SQLite是一种<font color=red>嵌入式数据库</font>，<font color=red>它的数据库就是一个文件</font>。由于SQLite本身是C写的，而且体积很小，所以，经常被集成到各种应用程序中，甚至在iOS和Android的App中都可以集成。

# 2.特点

* <font color=green>不需要一个单独的服务器进程或操作的系统（无服务器的）</font>。
* <font color=green>SQLite 不需要配置，这意味着不需要安装或管理</font>。
* 一个完整的 SQLite 数据库是存储在一个单一的跨平台的<font color=green>磁盘文件</font>。
* SQLite 是非常小的，是轻量级的，完全配置时小于 400KiB，省略可选功能配置时小于250KiB。
* SQLite 是自给自足的，这意味着不需要任何外部的依赖。
* SQLite 事务是完全兼容 ACID 的，允许从多个进程或线程安全访问。
* SQLite 支持 SQL92（SQL2）标准的大多数查询语言的功能。
* SQLite 使用 ANSI-C 编写的，并提供了简单和易于使用的 API。
* SQLite 可在 UNIX（Linux, Mac OS-X, Android, iOS）和 Windows（Win32, WinCE, WinRT）中运行。


# 3.参考教程
http://www.runoob.com/sqlite/sqlite-tutorial.html





