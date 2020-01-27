[TOC]

# nasm安装

```shell
#download
https://sourceforge.net/projects/nasm/

#编译安装，需要gcc
##解压
tar zxvf nasm-2.07.tar.gz
cd nasm-2.07
#该脚本会找到最好的C编译器来构造NASM，并据此建立Makefiles
./configure --prefix="/Users/chenyansong/Desktop/NASM/install"
make
make install

```

# nasm简单使用

```assembly
#要汇编一个文件，你可以以下面的格式执行一个命令：
nasm -f <format> <filename> [-o <output>]
#比如
nasm -f elf myfile.asm ;会把文件'myfile.asm'汇编成'ELF'格式 的文件'myfile.o'

nasm -f bin myfile.asm -o myfile.com ;会把文件'myfile.asm'汇编成纯二进制格式的文件'myfile.com'。

;想要以十六进制代码的形式产生列表文件输出，并让代码显示在源代码的左侧，使用'-l'选项并给出列表文件名，比如：
nasm -f coff myfile.asm -l myfile.lst

#######################
chenyanongdeMBP:bin chenyansong$ cat myfile.lst
     1 00000000 B83F00                mov ax, 0x3f
     2 00000004 01C3                  add bx, ax
     3 00000007 01C1                  add cx, ax
chenyanongdeMBP:bin chenyansong$ 
#######################


;想要获取更多的关于NASM的使用信息，请输入：
nasm -h

nasm -hf ;列出可以生成的文件格式

valid output formats for -f are (`*' denotes default):
  * bin       flat-form binary files (e.g. DOS .COM, .SYS);默认是这个
    aout      Linux a.out object files
    aoutb     NetBSD/FreeBSD a.out object files
    coff      COFF (i386) object files (e.g. DJGPP for DOS)
    elf       ELF32 (i386) object files (e.g. Linux)
    as86      Linux as86 (bin86 version 0.3) object files
    obj       MS-DOS 16-bit/32-bit OMF object files
    win32     Microsoft Win32 (i386) object files
    rdf       Relocatable Dynamic Object File Format v2.0
    ieee      IEEE-695 (LADsoft variant) object file format
    macho     NeXTstep/OpenStep/Rhapsody/Darwin/MacOS X object files
;默认情况下，nasm将生成一个纯二进制文件(也称为Raw Binary File或Flat-form Binary File),也就是说，生成的二进制中除了你写的源代码之外不包含其他任何东西，这意味着，程序执行时的内存映像和二进制文件映像是一样的
```



# 使用dd对拷mbr

```shell
#编译生成二进制机器码
nasm demo/first.asm -o demo/mbr.bin

#使用dd，需要加上参数 notrunc 不要截断输出文件（不将文件长度缩短为0），不然of文件会被if文件覆盖
dd if=demo/mbr.bin of=/Users/chenyansong/Desktop/NASM/LEECHUNG.vhd bs=512 count=1 conv=notrunc
```



# 反编译

```shell
############源码##########
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


#目录结构如下
hadoop@oprator:~/operation/bochs$ tree
├── bin
│   └── 1
│       └── boot.bin #编译之后的文件
├── bochsout.txt
├── bochsrc
├── bochsrc.bak.org
├── code
│   └── 1
│       └── boot.asm
└── img
    └── 1
        └── boot.img

#反编译；-o是指定一个地址
hadoop@oprator:~/operation/bochs$ ndisasm -o 0x7c00 bin/1/boot.bin 
#内存地址  机器码            汇编语言
00007C00  8CC8              mov ax,cs
00007C02  8ED8              mov ds,ax
00007C04  8EC0              mov es,ax
00007C06  E80200            call 0x7c0b
00007C09  EBFE              jmp short 0x7c09  #从这里对照源码，可以看到标号的意义
00007C0B  B81E7C            mov ax,0x7c1e
00007C0E  89C5              mov bp,ax
00007C10  B91000            mov cx,0x10
00007C13  B80113            mov ax,0x1301
00007C16  BB0C00            mov bx,0xc
00007C19  B200              mov dl,0x0
00007C1B  CD10              int 0x10
00007C1D  C3                ret
00007C1E  48                dec ax
00007C1F  656C              gs insb
00007C21  6C                insb
00007C22  6F                outsw
00007C23  2C20              sub al,0x20
00007C25  4F                dec di
00007C26  53                push bx
00007C27  20776F            and [bx+0x6f],dh
00007C2A  726C              jc 0x7c98
00007C2C  642100            and [fs:bx+si],ax
00007C2F  0000              add [bx+si],al
00007C31  0000              add [bx+si],al
00007C33  0000              add [bx+si],al
#......
```





