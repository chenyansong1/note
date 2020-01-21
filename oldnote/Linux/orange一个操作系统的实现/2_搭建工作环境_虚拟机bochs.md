[TOC]

# 虚拟机bochs



参见： [bochs安装使用](https://github.com/chenyansong1/note/blob/master/oldnote/Linux/x86汇编语言/bochs安装使用.md)



# kvm

bochs可以进行调试，但是如果我们想要直接启动镜像，那么qemu是一个不错的选择

```shell
qemu -fda a.img
```



# 编译器

C代码选择是GCC

汇编代码选择的是nasm



# 自动化工具

GNU Make



# 编写操作系统的步骤

1. 使用编辑器emacs或者vim
2. 使用Make调用GCC，NASM及其他Linux下的工具来生成内核并写入磁盘映像
3. 用bochs来运行你的操作系统
4. 如果有问题的话
   1. 用各种方式来调试，比如用bochs
   2. 返回第1步





