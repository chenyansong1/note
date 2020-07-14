[toc]

# Linux中的mmap映射 [一]

转自：https://zhuanlan.zhihu.com/p/67894878



除了传统的read()和write()系统调用，Linux还提供了另一种读写文件数据的方式，那就是mmap()。

先来看下用户进程调用read()在Linux中是怎样实现的。比如要读取磁盘上某个文件的8192个字节数据，那么这8192个字节会首先拷贝到内存中作为[page cache](https://zhuanlan.zhihu.com/p/68071761)（方便以后快速读取），然后再从page cache拷贝到用户指定的buffer中，也就是说，在数据已经加载到page cache后，还需要一次内存拷贝操作和一次系统调用。

![img](https://pic2.zhimg.com/80/v2-133afde38ea7205e52bfbdafc1348d0b_720w.jpg)

如果使用mmap()，则在磁盘数据加载到page cache后，用户进程可以通过指针操作直接读写page cache，不再需要系统调用和内存拷贝。看起来mmap()好像完胜read()有没有？

其实，mmap()在数据加载到page cache的过程中，会触发大量的page fault和建立页表映射的操作，开销并不小。另一方面，随着硬件性能的发展，内存拷贝消耗的时间已经大大降低了。所以啊，很多情况下，mmap()的性能反倒是比不过read()和write()的。

![img](https://pic1.zhimg.com/80/v2-302ff3afccdff6841f18b5eadd437c76_720w.jpg)

mmap()其实只是将一个文件的一部分内容映射到了进程虚拟地址空间中的一个[VMA区域](https://zhuanlan.zhihu.com/p/67936075)，并没有真正分配物理内存，只有等到进程真正访问这个VMA的时候，才会触发page fault，将这部分文件内容从磁盘拷贝到内存中。

![img](https://pic4.zhimg.com/80/v2-823536c62751ea3d0276c44a03478724_720w.jpg)

Linux中的文件是一个抽象的概念，并不是所有类型的文件都可以被mmap映射，比如目录和管道就不可以。一个文件可能被多个进程通过mmap映射后访问并修改，根据所做的修改是否对其他进程可见，mmap可分为共享映射和私有映射两种。

对于共享映射，修改对所有进程可见，也就是说，如果进程A修改了其中某个page上的数据，进程B之后读取这个page得到的就是修改后的内容。有共享就有竞态（race condition），mmap本身并没有提供互斥机制，需要调用者在使用的过程中自己加锁。**共享文件映射**的一个应用场景就是两个进程共同读写一个文本文件，比如你用vim编辑保存后，再用cat命令查看。

![img](https://pic3.zhimg.com/80/v2-140f5ba06608c1c73e5d5b3ba2ee7ecf_720w.jpg)

对于私有映射，进程A的修改对进程B是不可见的，都是同一份数据，这是如何做到的呢？这里利用的是 Copy On Write(COW)机制。

当进程A试图修改某个page上的数据时，内核会将这个page的内容拷贝一份，之后A的写操作实际是在这个拷贝的page上进行的（进程A中对应这个page的页表项也需要被修改，以指向新拷贝的page），这样进程B看到的这个page还是原来未经改动的。这种修改只会存在于内存中，不会同步到外部的磁盘文件上（事实上也没法同步，因为不同进程所做的修改是不同的）。**私有文件映射**最典型的应用就是进程对动态链接库（比如libc.so）的使用。

![img](https://pic4.zhimg.com/80/v2-65d8a834c49848e44f95e458edb801c7_720w.jpg)

以上介绍了的是mmap基于文件的映射（file-backed），其实mmap还可以用于对没有文件关联的内容进行映射，也就是[匿名映射](https://zhuanlan.zhihu.com/p/70964551)。同文件映射一样，匿名映射也有共享映射和私有映射两种。

**共享匿名映射**的一个典型应用是作为进程间通信机制的POSIX[共享内存](https://link.zhihu.com/?target=http%3A//hustcat.github.io/shared-memory-tmpfs/)。在Linux中，POSIX共享内存是通过挂载在/dev/shm下的tmpfs内存文件系统实现的，创建的每一个共享内存都对应tmpfs中的一个文件，因此POSIX共享内存也可视为共享文件映射。

而**私有匿名映射**可以用来实现glibc中的malloc()。传统的malloc()实现靠的是brk，通常brk用于分配小块内存，mmap则用于分配大块内存，这个分界的阈值默认是128KB（可通过mallopt函数调整）。

那mmap()到底该如何使用呢？请看[下文](https://zhuanlan.zhihu.com/p/71517406)分解。