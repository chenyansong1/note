[toc]

# 1.总体结构

下图是网口结构简图.网口由CPU、MAC和PHY三部分组成。DMA控制器通常属于CPU的一部分，用虚线放在这里是为了表示DMA控制器可能会参与到网口数据传输中。

MAC（Media Access Control或者Medium Access Control）地址，意译为媒体访问控制，或称为物理地址、硬件地址，用来定义网络设备的位置。

PHY(Physical Layer) 物理层。以太网芯片。

以太网芯片：是一款微小的控制器，把太网媒体接入控制器(MAC)和物理接口收发器(PHY)整合进同一芯片。

**很多网卡将MAC和PHY集成在一起，他们之间的关系是pci总线接mac总线，mac接phy，phy接网线。**

 

![](../../images/linux/hardware/17161251-19a6239a240f4aaaaf540c1a6e7b1c04.jpg)

![3](../../images/linux/hardware/v2-c83ec5221f60aee7a18a3cb8201a1b7c_r.jpg)

对于上述的三部分，并不一定都是独立的芯片，根据组合形式，可分为下列几种类型：

1. CPU集成MAC与PHY；
2. CPU集成MAC，PHY采用独立芯片；
3. CPU不集成MAC与PHY，MAC与PHY采用集成芯片；

本例中选用方案二做进一步说明，因为CPU总线接口很常见，通常都会做成可以像访问内存一样去访问，没必要拿出来说，而Mac与PHY之间的MII接口则需要多做些说明。

下图是采用方案二的网口结构图。虚框表示CPU，MAC集成在CPU中。PHY芯片通过MII接口与CPU上的Mac连接。

![](../../images/linux/hardware/17161446-3c65a40ac61a47cd8d277b143b9f55d4.jpg)

在软件上对网口的操作通常分为下面几步:

1. 为数据收发分配内存；
2. 初始化MAC寄存器；
3. 初始化PHY寄存器（通过MIIM）；
4. 启动收发；

# 2. MII

MII接口是MAC与PHY连接的标准接口。因为各厂家采用了同样的接口，用户可以根据所需的性能、价格，采用不同型号，甚至不同公司的phy芯片。

![img](../../images/linux/hardware/Center.png)

需要发送的数据通过MII接口中的收发两组总线实现。而对PHY芯片寄存器的配置信息，则通过MII总线的一组串口总线实现，即MIIM（MII Management）

下表列出了MII总线中主要的一些引脚。

| **PIN Name** | **Direction** | **Description**       |
| ------------ | ------------- | --------------------- |
| TXD[0:3]     | Mac to Phy    | Transmit Data         |
| TXEN         | Mac to Phy    | Transmit Enable       |
| TXCLK        | Mac to Phy    | Transmit Clock        |
| RXD[0:3]     | Phy to Mac    | Receive Data          |
| RXEN         | Phy to Mac    | Receive Enable        |
| RXCLK        | Phy to Mac    | Receive Clock         |
| MDC          | Mac to Phy    | Management Data Clock |
| MDIO         | Bidirection   | Management Data I/O   |


MIIM只有两个线， 时钟信号MDC与数据线MDIO。读写命令均由Mac发起，PHY不能通过MIIM主动向Mac发送信息。由于MIIM只能有Mac发起，我们可以操作的也就只有MAC上的寄存器。



[MII与RMII接口的区别](https://blog.csdn.net/fun_tion/article/details/70270632)




# 3. DMA

收发数据总是件费时费力的事，尤其对于网络设备来说更是如此。CPU做这些事情显然不合适。既然是数据搬移， 最简单的办法当然是让DMA来做。毕竟专业的才是最好的。

这样CPU要做的事情就简单了。**只需要告诉DMA起始地址与长度，剩下的事情就会自动完成。**

通常在MAC中会有一组寄存器专门用于记录数据地址，tbase与rbase，cpu按MAC要的格式把数据放好后，启动MAC的数据发送就可以了。启动过程常会用到寄存器tstate。

# 4. MAC

![](../../images/linux/hardware/17161911-e44129c5736e4bc58194498812093d4d.jpg)

CPU上有两组寄存器用于MAC。一组用户数据的收发，对应上面的DMA；一组用户MIIM，用户对PHY进行配置。两组寄存器由于都在CPU上，配置方式与其他CPU上寄存器一样，直接读写即可。数据的转发通过DMA完成。 

# 5. PHY

![](../../images/linux/hardware/17162040-91e934638b1649d3819d41aa5079605b.jpg)

该芯片是一个10M/100M Ethernet网口芯片

PHY芯片有一组寄存器用户保存配置，并更新状态。CPU不能直接访问这组寄存器，只能通过MAC上的MIIM寄存器组实现间接访问。同时PHY芯片负责完成MII总线的数据与Media Interface上数据的转发。该转发根据寄存器配置自动完成，不需要外接干预。

本文转自：https://www.cnblogs.com/jason-lu/p/3196096.html



其他：[网口扫盲三:以太网芯片MAC和PHY的关系](https://www.cnblogs.com/try-again/p/9179101.html)

