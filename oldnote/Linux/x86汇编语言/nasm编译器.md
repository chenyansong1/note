[toc]

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

```



# 使用dd对拷mbr

```shell
#编译生成二进制机器码
nasm demo/first.asm -o demo/mbr.bin

#使用dd，需要加上参数 notrunc 不要截断输出文件（不将文件长度缩短为0），不然of文件会被if文件覆盖
dd if=demo/mbr.bin of=/Users/chenyansong/Desktop/NASM/LEECHUNG.vhd bs=512 count=1 conv=notrunc
```

