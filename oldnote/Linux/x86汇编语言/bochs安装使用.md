[toc]

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

# mac下安装出现的问题

