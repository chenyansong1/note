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
# 设置虚拟机内存为32MB
megs: 32

# 设置BIOS镜像
romimage: file=$BXSHARE/BIOS-bochs-latest 

# 设置VGA BIOS镜像
vgaromimage: file=$BXSHARE/VGABIOS-lgpl-latest

# 设置从硬盘启动
boot: disk

# 设置日志文件
log: bochsout.txt

# 关闭鼠标
mouse: enabled=0

# 打开键盘
keyboard: type=mf, serial_delay=250

# 设置硬盘
ata0: enabled=1, ioaddr1=0x1f0, ioaddr2=0x3f0, irq=14
ata0-master: type=disk, path="c.img", mode=flat

# 添加gdb远程调试支持
gdbstub: enabled=1, port=1234, text_base=0, data_base=0, bss_base=0

```

如果是virtual PC 

```shell
chenyansongdeMacBook-Pro:bochs chenyansong$ cat bochsrc.bxrc
# 设置虚拟机内存为32MB
megs: 32

# 设置BIOS镜像
romimage: file=/Users/chenyansong/Desktop/NASM/install/bochs/share/bochs/BIOS-bochs-latest 

# 设置VGA BIOS镜像
vgaromimage: file=/Users/chenyansong/Desktop/NASM/install/bochs/share/bochs/VGABIOS-lgpl-latest

# 设置从硬盘启动
boot: disk

# 设置日志文件
log: bochsout.txt

# 关闭鼠标
mouse: enabled=0

# 打开键盘
keyboard: type=mf, serial_delay=250

# 设置硬盘
ata0: enabled=1, ioaddr1=0x1f0, ioaddr2=0x3f0, irq=14
#这里的类型是vpc，path指定硬盘的路径
ata0-master: type=disk, path="/Users/chenyansong/Desktop/NASM/LEECHUNG-bochs.vhd", mode=vpc
# 添加gdb远程调试支持
#gdbstub: enabled=1, port=1234, text_base=0, data_base=0, bss_base=0
chenyansongdeMacBook-Pro:bochs chenyansong$ 
```



# 创建一块磁盘镜像

![image-20191223200244120](/Users/chenyansong/Documents/note/images/linux/tixijiegou/image-20191223200244120.png)



参见：http://imushan.com/2018/07/11/os/Bochs学习-安装配置篇/



# nasm编译源文件

```shell
#编译生成二进制机器码
nasm demo/first.asm -o demo/mbr.bin
```





# 将MBR拷贝到磁盘的0扇区

```shell
#使用dd，需要加上参数 notrunc 不要截断输出文件（不将文件长度缩短为0），不然of文件会被if文件覆盖
dd if=demo/mbr.bin of=/Users/chenyansong/Desktop/NASM/LEECHUNG.vhd bs=512 count=1 conv=notrunc
```





# 使用

bochs下有两个程序

*  bochs.exe：作为类似于 VirtualBox 的虚拟机来使用，
*  bochsdbg.exe： 来调试程序



https://www.jianshu.com/p/742a61ce3e58



https://www.okcode.net/article/71934



## bochs调试

| 命令          | 说明                              |
| :------------ | :-------------------------------- |
| blist         | 显示所有断点信息                  |
| pb [物理地址] | **设置断点，以物理地址方式**      |
| vb [虚拟地址] | 设置断点，以虚拟地址方式          |
| lb [线性地址] | 设置断点，以线性地址方式          |
| d [断点号]    | **删除断点 ,断点号根据blist查询** |
| c             | **继续执行，跳到下一个断点**      |
| s [N]         | **单步执行**                      |
| n             | **单步执行(跳过call函数内部 )**   |
| q             | **退出**                          |

## 显示信息

| 命令          | 说明                                            |
| :------------ | :---------------------------------------------- |
| show mode     | 显示模式切换                                    |
| show int      | 显示中断                                        |
| show call     | 显示call调用                                    |
| trace on      | 显示指令反编译                                  |
| info ivt      | 显示ivt（中断向量表）信息                       |
| info idt      | 显示idt（中断描述符表）信息                     |
| info gdt      | 显示gdt信息                                     |
| info ldt      | 显示ldt信息                                     |
| info tss      | 显示tss信息                                     |
| info tab      | 页表映射                                        |
| reg           | **通用寄存器信息 + 标志寄存器 + eip寄存器信息** |
| sreg          | **段寄存器信息**                                |
| creg          | 控制寄存器信息                                  |
| dreg          | 调试寄存器信息                                  |
| print-stack N | 堆栈信息                                        |

## 内存信息

| 命令               | 说明                                                         |
| :----------------- | :----------------------------------------------------------- |
| xp /nuf [物理地址] | **显示物理地址处内容，例如：xp /16 0xa0000**,显示指定物理内存地址处的内容，xp每次只显示一个双字 ， / 后面可以指定数量 |
| x /nuf [线性地址]  | 显示线性地址处内容                                           |
| setpmem            |                                                              |
| page               |                                                              |



# mac下安装出现的问题

```shell
但是就当我make的时候，去突然报错了，报错如下：

cdrom_osx.cc:194:18: error: assigning to 'char ' from incompatible type 'const ch
于是在网上查了一下，这个报错有个补丁[https://raw.githubusercontent.com/Homebrew/formula-patches/e9b520dd4c/bochs/xcode9.patch]

vim  .//iodev/hdimage/cdrom_osx.cc

也就是把cdrom_osx.cc中出错的这一行
if ((devname = strrchr(devpath, '/')) != NULL) {
改为：
if ((devname = (char *) strrchr(devpath, '/')) != NULL) {
即可make和make install成功

```