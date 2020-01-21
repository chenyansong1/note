[TOC]

# 一个简单的“操作系统”源码

```assembly
  	org	07c00h			; 告诉编译器程序加载到7c00处
	mov	ax, cs
	mov	ds, ax
	mov	es, ax
	call	DispStr			; 调用显示字符串例程
	jmp	$			; 无限循环
DispStr:
	mov	ax, BootMessage
	mov	bp, ax			; ES:BP = 串地址
	mov	cx, 16			; CX = 串长度
	mov	ax, 01301h		; AH = 13,  AL = 01h
	mov	bx, 000ch		; 页号为0(BH = 0) 黑底红字(BL = 0Ch,高亮)
	mov	dl, 0
	int	10h			; 10h 号中断
	ret
BootMessage:		db	"Hello, OS world!"
times 	510-($-$$)	db	0	; 填充剩下的空间，使生成的二进制代码恰好为512字节
dw 	0xaa55				; 结束标志
```



显示服务中断0x10说明

```shell
(19)、功能13H
功能描述：在Teletype模式下显示字符串
入口参数：AH＝13H
BH＝页码
BL＝属性(若AL=00H或01H)
CX＝显示字符串长度
(DH、DL)＝坐标(行、列)
ES:BP＝显示字符串的地址 AL＝显示输出方式
0——字符串中只含显示字符，其显示属性在BL中。显示后，光标位置不变
1——字符串中只含显示字符，其显示属性在BL中。显示后，光标位置改变
2——字符串中含显示字符和显示属性。显示后，光标位置不变
3——字符串中含显示字符和显示属性。显示后，光标位置改变
出口参数：无
```

# 操作工具

nssm：编译器，编译汇编程序

dd 写命令：向硬盘写入数据

bochs：一个虚拟机，只不过有调试功能

# 编译生成机器码

```shell
#目录结构
./
├── bin
│   └── 1
│       └── boot.bin
└── code
    ├── 1
    │   └── boot.asm
    └── bochsrc

#编译
nasm code/1/boot.asm -o bin/1/boot.bin
```

# 写入磁盘并在虚拟机上启动

```shell
#使用dd，需要加上参数 notrunc 不要截断输出文件（不将文件长度缩短为0），不然of文件会被if文件覆盖;LEECHUNG.vhd只是一个镜像文件，下面只是覆盖了该镜像的512字节，启动的时候，bochs会以这个镜像启动
dd if=demo/mbr.bin of=/Users/chenyansong/Desktop/NASM/LEECHUNG.vhd bs=512 count=1 conv=notrunc

#然后在启动虚拟机的时候，选择以这个镜像来启动
```



```shell
#另外一种方式是，直接生成一个小的512字节的镜像
hadoop@oprator:~/operation/bochs$ tree
.
├── bin
│   └── 1
│       └── boot.bin
├── bochsrc
├── code
│   └── 1
│       └── boot.asm
└── img
    └── 1
        └── boot.img

############生成启动文件############
dd if=bin/1/boot.bin of=img/1/boot.img bs=512 count=1
 
############修改bochs的启动配置############
hadoop@oprator:~/operation/bochs$ cat bochsrc
###############################################################
# Configuration file for Bochs
###############################################################

# how much memory the emulated machine will have
megs: 32

# filename of ROM images
romimage: file=/usr/share/bochs/BIOS-bochs-latest
vgaromimage: file=/usr/share/vgabios/vgabios.bin

# what disk images will be used
floppya: 1_44=img/1/boot.img, status=inserted

# choose the boot disk.
boot: floppy

# where do we send log messages?
log: bochsout.txt

# disable the mouse
mouse: enabled=0

# enable key mapping, using US layout as default.
keyboard_mapping: enabled=1, map=/usr/share/bochs/keymaps/x11-pc-us.map

hadoop@oprator:~/operation/bochs$ 


############启动镜像############
bochs -f bochsrc
```

# 计算机启动流程简介

1. 当计算机电源被打开，他会进行加电自检（POST），
2. 然后寻找启动盘，如果是选择从软盘启动，计算机就会检查软盘的0面0磁道1扇区，如果发现他以0xAA55结束，则BIOS任务他是一个引导扇区，当然一个正确的引导扇区除了以0xAA55结束之外，还应该包含一段少于512字节的执行代码
3. BIOS加载引导扇区到内存的0000：7c00处
4. 跳转到内存0000：7c00处执行程序，将控制权交给引导代码



# 标号

```shell
$ : 表示当前行被汇编后的地址
$$: 表示程序被编译后的开始地址
$-$$表示本行距离程序开始处的相对距离
```

