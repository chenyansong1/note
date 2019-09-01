[TOC]



# IP地址分类

![image-20190812221156039](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190812221156039.png)

# 保留的私网地址

A 	10.0.0.0

B	  172.16.0.0-172.31.0.0

C		192.168.0.0-192.168.255.0



自动获取地址失败，之后，就会分配这个网段的地址给你 169.254.0.0（实际的案例：一个小区的用户使用拨号上网，这个小区有2000人，但是网络只有1500个地址，平时2000人也不一定同时上网，所以1500个地址够分，但是如果有一天小区同时有超过1500人上网，那么就会有人获取不到地址，此时如果我们dsl拨号通了，但是还是不能上网，那么查看自己的IP地址，看看是不是在169.254这个网段，是的话说明没有分配到地址，此时只要断开重连几次就行了）

广播地址：当一台主机向网络上所有的主机发送数据时，就产生了广播

直接广播：如果广播地址包含一个有效的网络ID和一个全1的主机ID，那么称之为直接广播，如：211.123.22.255

有限广播：32位全1的IP地址为255.255.255.255，用于本网广播，该地址叫做有限广播地址

本地回环地址(lookback)：127.0.0.1 ，用于网络软件测试，以及本地主机进程间通信







# 等长子网划分

等长子网，子网掩码向后移动1位，变成2个网段

![image-20190812223437549](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190812223437549.png)

路由器的地址默认使用该网段的地址一个地址如：192.168.0.1/24

子网掩码向后移动2位，变成四个网段

![image-20190812223832750](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190812223832750.png)



# 变长子网划分

直接将对应的台数放到对应的区域

![image-20190813204544463](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190813204544463.png)

![image-20190812221156039](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190812221156039.png)

# 子网合并

![image-20190813211203427](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190813211203427.png)