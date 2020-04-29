---
title: Linux基础命令之Linux中rz和sz命令用法详解
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


# 介绍

rz，sz是Linux/Unix同Windows进行ZModem文件传输的命令行工具。

优点就是不用再开一个sftp工具登录上去上传下载文件。
sz：将选定的文件发送（send）到本地机器
rz：运行该命令会弹出一个文件选择窗口，从本地选择文件上传到Linux服务器


# 安装命令
```
yum install lrzsz
```


从服务端发送文件到客户端：
```
sz filename
```

从客户端上传文件到服务端：
```
rz
#在弹出的框中选择文件，上传文件的用户和组是当前登录的用户
```


# SecureCRT设置默认路径：
Options -> Session Options -> Terminal -> Xmodem/Zmodem ->Directories
Xshell设置默认路径：
右键会话 -> 属性 -> ZMODEM -> 接收文件夹
测试：
开发板接收文件：
1. 进入开发板要接收文件的目录
2. 开发板执行命令# rz
3. 在minicom下，按住Ctrl+A键不放，按下Z键
4. 按下S键选择发送文件
5. 选择zmodem，用回车键确认
6. 用空格选择主机要发送的文件，用回车键确认
7. 传输完成后按任意键返回
开发板发送文件：
1. 进入开发板要发送文件的目录
2. 进入主机要接收文件的目录
2. 主机执行命令# rz
3. 开发板执行命令# sz filename
