[TOC]

![image-20180817224045500](/Users/chenyansong/Documents/note/images/linux/filesystem/filesyem1.png)

* 文件类型

```properties
- : 普通文件
d : 目录文件
b : 块设备文件(block)
c : 字符设备文件(character)
l : 符号链接文件(symbolic link file 软连接)
p : 命令管道(pipe)
s : 套接字文件(socket)
```

* 文件权限：9位，每3位一组（每组 rwx ：读 写 执行；没有就用-表示）

* 文件硬链接的次数

* 文件的属主（owner）

* 文件的属组(group)

* 文件大小（size：单位是字节）

* 文件的时间戳（timestamp :最近一次被访问的时间；最近一次被修改的时间（默认显示）；最近一次被改变的时间)

  * 访问：access
  * 修改：modify，改变的是文件的内容
  * 改变：change，meta data 元数据，这个就是改变的是文件的元数据

* 文件名








