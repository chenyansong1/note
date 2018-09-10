

[TOC]





# 1.虚拟地址的由来

虚拟地址，线性地址

![image-20180909224428689](/Users/chenyansong/Documents/note/images/linux/filesystem/vaddr.png)

如上图，如果我们的进程直接访问内存上的地址，那么如果我们有一个恶意进程，这个恶意进程会寻址到其他不属于自己的内存地址，然后对这写不属于自己的内存进行恶意的操作，那么系统就会出现问题了，**这个时候就有了虚拟地址的出场	**



![image-20180909224428689](/Users/chenyansong/Documents/note/images/linux/filesystem/vaddr2.png)



> 每个进程都会假设拿到的是内存地址的映射，这样每个进程都会认为自己拿到的是真个内存



# 2.交换分区的产生

因为每个进程都会认为自己拿到了系统的所有的内存，那么当多个进程同时使用内存的时候，内存总有一个时刻会被用完，此时，我们还要向内存空间中写数据，怎么办？我们想到了一个折中的办法，将内存中最近最少使用的数据移动到磁盘上，这样内存中就空出来了一部分空间，这样就可以继续使用了，而写入到磁盘的数据空间就是交换分区

![image-20180909230131261](/Users/chenyansong/Documents/note/images/linux/filesystem/vaddr3.png)

> 当我们的进程想要读取存在交换分区中的数据的时候，系统会将交换分区中的数据先读入到内存中，然后进程从内存中读取数据，这个过程我们称之为换进换出(page out, page in)



**交换分区的存在，允许了内存的过载使用**, 所以如果我们观察到系统的swap分区存在频繁的换进换出操作，那么就说明我们的内存太小了，不能满足当前的需求



# free 查看系统的物理内存和交换分区的使用情况



```
free -g
free -m


# 缓存(cache) 
可以重复使用，例如：读取数据的时候，会先读取缓存

# 缓冲(buffer)
避免快的设备和慢的设备速度不匹配的问题，例如：写数据到磁盘，会先写缓冲

[root@rac01 ~]# free -g
            	total      used      free    shared    buffers    cached
Mem:            15          8        7          0          0          8
-/+ buffers/cache:          0        15		# 这里是：如果去掉buffer/cache之后的已用内存和可用内存
Swap:           15          0        15

```



# dd复制文件，拷贝的是底层的数据流

## 使用



cp复制文件的逻辑：

1. 将文件通过vfs读取到内存中
2. 将内存中的文件写入到另外一个地址

dd复制文件的逻辑：

1. 直接复制原始文件在磁盘的字节，写入到磁盘的另一个位置，这样dd可以只复制某个文件的一部分，如：只复制文件的几个字节

```
dd if=/etc/inittab of=/root/inittab
if=input file
of=output file
bs=1	#以一个字节为单位，可以使用单位如：bs=1M; bs=1k
count=2	#只是复制2个单位的数据
seek=1023

# 前面的1023的单位跳过，之后最后1个单位是使用0填充的，文件总大小还是1G
dd if=/dev/zero of=/var/swapfile seek=1023 bs=1M count=1
# 实际上这个文件的大小只有1M



#备份MBR,将/dev/sda硬盘的前512字节备份到Usb上
dd if=/dev/sda  of=/mnt/usb/mbr.backup bs=512 count=1
#从USB还原MBR
dd if=/mnt/usb/mbr.backup  of=/dev/sda bs=512 count=1


# 创建1G大小的文件
dd if=/dev/zero of=/var/swapfile bs=1M count=1024

# /dev/zero 是一个冒泡设备，不停的向外吐0，
# /dev/null 是一个吸收设备，黑洞

```



## 用一个文件创建swap分区



1. 使用dd创建一个1G大小的文件
2. 使用mkswap使文件变成swap类型
3. 启用swapon



![image-20180910194454517](/Users/chenyansong/Documents/note/images/linux/filesystem/dd_swap.png)





