docker存储卷

[TOC]



* Docker镜像由多个只读层叠加而成，启动容器时，Docker会加载只读镜像层并在镜像栈顶添加一个读写层
* 如果运行中的容器修改了现有的一个已经存在的文件，那该文件将会从读写层下面的只读层复制到读写层，该文件的只读版本仍然存在，只是已经被读写层中该文件的副本所隐藏，此即“写时复制（cow)"机制
* 这样的写时复制，在修改和删除时效率非常的低，这就引出了存储卷的概念

![image-20190716203009596](/Users/chenyansong/Documents/note/images/docker/image-20190716203009596.png)



在容器内部有一个目录和宿主机的文件系统中有一个目录一一对应，然后就是在容器内操作该目录时，就是操作的宿主机的一个目录，**实现部分文件系统的共享**



Why Data Volume?

* 关闭并重启容器，其数据不受影响，但删除Docker容器，则其更改将会全部丢失

* 存在问题

  * 存储于联合文件系统中，不易于宿主机访问
  * 容器间数据共享不便
  * 删除容器其数据会丢失

* 解决方案

  * ”卷“是容器上的一个或多个目录，此类目录可绕过联合文件系统，与宿主机上的某目录”绑定(关联)“

    ![image-20190716205902440](/Users/chenyansong/Documents/note/images/docker/image-20190716205902440.png)

* Volume的初衷是独立于容器的生命周期实现数据持久化，因此删除容器之时既不会删除卷，也不会对哪怕未被引用的卷做垃圾回收操作



* Volume的类型
  * Bind-mount Volume: 在容器和宿主机中各指定一个路径进行对应绑定，这两个目录都需要绑定
  * docker管理的卷（Docker-managed volume）：只创建容器的目录，宿主机上的目录是Docker-daemon自动进行选择的（会以卷ID创建）



```shell
#Docker管理的卷，只指定Docker的目录
docker run -it --name bbox1 -v /data busybox


#Bind-mount Volume
docker run -it -v host_dir:volume_dir --name bbox2 busybox

```

![image-20190716211444466](/Users/chenyansong/Documents/note/images/docker/image-20190716211444466.png)

我们查看容器的详情，`docker inspect b2`

![image-20190716211618632](/Users/chenyansong/Documents/note/images/docker/image-20190716211618632.png)

​	![image-20190716211719035](/Users/chenyansong/Documents/note/images/docker/image-20190716211719035.png)



我们指定两个目录

![image-20190716212059386](/Users/chenyansong/Documents/note/images/docker/image-20190716212059386.png)

![image-20190716212133556](/Users/chenyansong/Documents/note/images/docker/image-20190716212133556.png)

在宿主机上也是会自动创建对应的目录





容器间的存储卷共享

![image-20190716214306720](/Users/chenyansong/Documents/note/images/docker/image-20190716214306720.png)

我们可以做一个基础的卷(这个卷并不一定要启动，该容器称作基础支撑架构容器)，我们可以在这个卷上做存储卷的共享，做网络名称空间的共享