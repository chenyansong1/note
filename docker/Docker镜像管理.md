[TOC]

存储镜像的文件系统：

当我们将镜像从remote pull 到本地的时候，我们需要将这个镜像存储下来，我们使用一种特殊的文件系统进行存储（overlay2）



Docker码头工人



# docker镜像

Docker镜像含有启动容器所需要的文件系统及其内容，因此，其用于创建并启动docker容器

* 采用分层构建机制，最底层为bootfs, 其之上为rootfs

  * bootfs：用于**系统引导**的文件系统，包括bootloader 和kernel，**容器启动完成后会被卸载**以节约内存资源

  * rootfs：位于bootfs之上，**表现为**docker容器的根文件系统

    * 传统模式中，系统启动之时，内核关在rootfs时会首先将其挂载为只读模式，完整性自检完成后将其重新挂载为读写模式
    * docker中，rootfs由内核挂载为“只读”模式，而后通过“联合挂载”技术**额外挂载一个可写层**

    ![1562296416294](E:\git-workspace\note\images\docker\im1.png)

    * 位于下层的镜像称为父镜像（parent image），最底层的称为基础镜像（base image)

    * 最上层为“可读写”层，其下的均为“只读”层

      ![1562296623114](E:\git-workspace\note\images\docker\im2.png)

* Aufs

  * advanced multi-layered unification filesystem(高级多层联合文件系统)
  * 用于为Linux文件系统实现联合
  * aufs是之前的UnionFS的重新实现
  * Docker最初使用aufs作为容器文件系统层，他目前仍作为存储后端之一来支持
  * aufs的竞争产品是overlayfs，后者从3.18版本开始被合并到Linux内核
  * docker的分层镜像，除了aufs，docker还支持btrfs,devicemapper和vfs等
    * 在Ubuntu系统下，docker默认Ubuntu的aufs，而在CentOS7上，用的是devicemapper(dm)

  

  

  启动容器时，docker daemon会试图从本地获取相关的镜像，本地不存在时，其将从Registry中下载该镜像并保存到本地

  ![1562306554851](E:\git-workspace\note\images\docker\im3.png)
  * Registry用于保存docker镜像，包括镜像的层次结构和元数据
  * 用户可自建Registry，也可以使用官方的Docker Hub
  * 分类
    * Sponsor Registry：第三方registry，供客户和Docker社区使用
    * Mirror Registry：第三方的registry，只让客户使用
    * Vendor Registry：由发布Docker镜像的供应商提供的registry
    * Private Registry：通过设有防火墙和额外的安全层的私有实体提供的registry

  * Registry（repository and index)
    * Repository
      * 由某个特定的docker镜像的所有迭代版本组成的镜像仓库

















