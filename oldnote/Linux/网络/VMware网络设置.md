[TOC]

# 虚拟机创建网络

可以在vmware上添加网络，可以指定

1. 网络的类型
   1. Bridge
   2. NAT
   3. Host only
2. 是否需要在物理机上创建出对应的虚拟网卡(用于虚拟机和物理机通信)

![image-20190814210449254](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814210449254.png)

![image-20190814210420410](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814210420410.png)

# 虚拟机和物理机通信

要虚拟机和物理机进行通信，需要他们在同一个网络，比如虚拟机选择的网络是VMnet8(NAT)，那么物理机就需要使用VMnet8这个虚拟网卡和虚拟机进行通信(因为他们在同一个网络，所以可以进行通信)，此时就需要配置他们的IP在同一个网络中

![image-20190814211952991](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814211952991.png)

![image-20190814212018813](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814212018813.png)