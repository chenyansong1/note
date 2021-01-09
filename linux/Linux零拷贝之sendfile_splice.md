[toc]

转自：https://www.pianshen.com/article/37311621790/

## Linux零拷贝之sendfile/splice

sendfile/splice实现了真正意义上的零拷贝：CPU完全不需要参与数据的拷贝，应用层Java NIO为开发人员提供了FileChannel.transferFrom()/transferTo()方法使用零拷贝。

### sendfile

#### 传输原理

1. 用户进程通过 sendfile() 函数向内核（kernel）发起系统调用，上下文从用户态（user space）切换为内核态（kernel space）；
2. CPU 利用 DMA 控制器将数据从主存或硬盘拷贝到内核空间（kernel space）的读缓冲区（read buffer）；
3. CPU 把读缓冲区（read buffer）的文件描述符（file descriptor）和数据长度拷贝到网络缓冲区（socket buffer）；
4. 基于已拷贝的文件描述符（file descriptor）和数据长度，CPU 利用 DMA 控制器的 gather/scatter 操作直接批量地将数据从内核的读缓冲区（read buffer）拷贝到网卡进行数据传输；
5. 上下文从内核态（kernel space）切换回用户态（user space），Sendfile 系统调用执行返回；
   ![在这里插入图片描述](https://www.pianshen.com/images/797/8132e113854d19d90dd0ace80f854b95.png)

#### 适用场景

  **适用于文件数据到网卡的传输过程，并且用户程序对数据没有修改的场景；**

### splice

#### 传输原理

1. 用户进程通过 splice() 函数向内核（kernel）发起系统调用，上下文从用户态（user space）切换为内核态（kernel space）；
2. CPU 利用 DMA 控制器将数据从主存或硬盘拷贝到内核空间（kernel space）的读缓冲区（read buffer）；
3. CPU 在内核空间的读缓冲区（read buffer）和网络缓冲区（socket buffer）之间建立管道（pipeline）；
4. CPU 利用 DMA 控制器将数据从网络缓冲区（socket buffer）拷贝到网卡进行数据传输；
5. 上下文从内核态（kernel space）切换回用户态（user space），Splice 系统调用执行返回；
   ![在这里插入图片描述](https://www.pianshen.com/images/628/dcdd43a4e0289d21ebc696eff7fa4a2c.png)

#### 适用场景

  splice适用于任意两个文件描述符中传输数据（两个文件描述符参数中有一个必须是管道设备），并且用户程序对数据没有修改的场景；

### Java应用

  Java NIO的FileChannel.transferFrom()/transferTo()底层基于sendfile/splice，不仅可以进行网络文件传输，还可以对本地文件实现零拷贝操作，示意图如下所示：
![在这里插入图片描述](https://www.pianshen.com/images/497/25dafbf6fc6fda72474c1c482d137039.png)

参考：

1. [支撑百万并发的“零拷贝”技术，你了解吗？](https://mp.weixin.qq.com/s/mZujKx1bKl1T6gEI1s400Q)
