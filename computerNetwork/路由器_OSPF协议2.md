[TOC]

# OSPF的验证配置

OSPF的验证分为区域验证和接口验证两种

区域验证：在OSPF路由进程下启用的，一旦启用，这台路由器所有属于这个区域的接口都将启用

接口验证：是在接口下启用，所以只影响路由器的一个接口

验证方法：明文验证和MD5加密验证

![image-20190908090653186](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908090653186.png)





# OSPF的末梢区域和完全末梢区域

* OSPF区域类型

  当网络中包含多个区域时，OSPF协议有特殊的规定，即其中必须有一个Area 0，通常也叫做骨干区域（Backbone Area），也称之为主干区域，当设计OSPF网络时，一个很好的方法就是从骨干区域开始，然后再扩展到其他区域，骨干区域在所有其他区域的中心，即所有区域都必须与骨干区域物理或逻辑上相连，这种设计思想的原因是OSPF协议要把所有区域的路由信息引入骨干区域，然后再依次将路由信息从骨干区域分发到其他区域中

  在OSPF的各种区域中，主要有五种链路状态公告LSA，分别是类型1（Router LSA)，类型2（Network LSA），类型3（ABR汇总LSA），类型4（ASBR汇总LSA），类型5（AS扩展LSA）

  ![image-20190908092151077](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908092151077.png)

  ![image-20190908092317482](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908092317482.png)



* OSPF路由来源分类

  ![image-20190908092612869](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908092612869.png)

![image-20190908092911672](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908092911672.png)

配置末梢区域：**外部路由，由一条缺省路由替代**

![image-20190908093135477](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908093135477.png)



配置成完全末梢区域：此时只有区域内的路由，没有区域外，和自治系统外的路由信息

![image-20190908093426841](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908093426841.png)



# OSPF虚连接的配置

OSPF规定必须有一个骨干区域0，其他所有区域都必须与骨干区域物理相连，在实际的网络情况下，往往出现如下所示情况

![image-20190908093824285](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908093824285.png)



没有配置虚连接之前，区域2由于没有直连和骨干区域0相连接，因此区域2中的路由器C2811A无法通过骨干区域学习到区域1和区域0中的路由信息，而其他区域的路由器也无法学习到区域2中的192.168.1.0/24网络的路由信息

![image-20190908094202284](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908094202284.png)

配置虚连接之后

![image-20190908094410127](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908094410127.png)

![image-20190908094536252](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908094536252.png)

![image-20190908094626054](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908094626054.png)