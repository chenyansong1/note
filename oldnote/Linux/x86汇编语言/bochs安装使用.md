[TOC]

* 面向处理器的调试工具

* bochs本省就是一个虚拟机，类似于VirtualBox

* 作用：跟踪硬盘的启动过程，查看寄存器的内容和机器的状态



# 安装bochs


```shell
tar zxvf bochs-2.4.5.tar.gz 
cd bochs-2.4.5/
./configure --enable-debugger --enable-disasm -with-nogui --prefix=xxx/xxx
make
make install
which bximage 

```



# bochsrc配置文件

bochsrc

```shell
hadoop@oprator:~$ cat ~/operation/bochs/code/bochsrc
###############################################################
# Configuration file for Bochs
###############################################################

# how much memory the emulated machine will have
megs: 32

# filename of ROM images
romimage: file=/usr/share/bochs/BIOS-bochs-latest
vgaromimage: file=/usr/share/vgabios/vgabios.bin

# what disk images will be used
floppya: 1_44=a.img, status=inserted

# choose the boot disk.
boot: floppy

# where do we send log messages?
log: bochsout.txt

# disable the mouse
mouse: enabled=0

# enable key mapping, using US layout as default.
keyboard_mapping: enabled=1, map=/usr/share/bochs/keymaps/x11-pc-us.map

hadoop@oprator:~$ 

```



# 使用

bochs下有两个程序

*  bochs.exe：作为类似于 VirtualBox 的虚拟机来使用，
* bochsdbg.exe： 来调试程序



https://www.jianshu.com/p/742a61ce3e58



https://www.okcode.net/article/71934

# mac下安装出现的问题

