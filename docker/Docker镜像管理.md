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

      * 一个Registry中可以存在多个Repository

        * Repository可分为“顶层仓库”和“用户仓库”
        * 用户仓库名称格式为“用户名/仓库名"
        * 每个仓库可以包含多个Tag（标签），每个标签对应一个镜像

      * Index

        * 维护用户账户，镜像的校验以及公共命名空间的信息
        * 相当于为Registry提供了一个用户认证等功能的检索接口

      * docker registry中的镜像通常由开发人员制作，而后推送给“公共”或”私有“ Registry上保存，工其他人员使用

        ![1562308013592](E:\git-workspace\note\images\docker\im4.png)

  * Dockerfile文件自动构建

    在github上创建一个仓库用于写Dockerfile文件，如果这个github中的Dockerfile文件被改变会通过**Webhooks**通知**Automated Builds（他在Docker Hub)上**，然后docker hub拉取github上的Dockerfile实现自动构建镜像

    ![1562309001891](E:\git-workspace\note\images\docker\im5.png)



* 获取镜像

  ```shell
  docker pull <registry>[:<port>]/[<namespace/]<name>:<tag>
  
  #docker pull quay.io/coreos/flanrel
  
  ```

  ![1562309355954](E:\git-workspace\note\images\docker\im6.png)

  ![1562309490734](E:\git-workspace\note\images\docker\im7.png)



* 镜像的制作途径

  * Dockerfile
  * 基于容器制作
  * Docker Hub automated builds

  ![1562309665480](E:\git-workspace\note\images\docker\im8.png)



# 基于容器制作镜像

* Create a new image from a container's changes

  ```shell
  docker commit [options] container [repository[:port]]
  
  --author,-a  #作者信息（用户名，邮箱等）
  --change, -c #修改原有的基础镜像的指令
  --message, -m #
  --pause, -p #
  
  
  [root@spark01 ~]# docker commit -h
  Flag shorthand -h has been deprecated, please use --help
  
  Usage:  docker commit [OPTIONS] CONTAINER [REPOSITORY[:TAG]]
  
  Create a new image from a container's changes
  
  Options:
    -a, --author string    Author (e.g., "John Hannibal Smith
                           <hannibal@a-team.com>")
    -c, --change list      Apply Dockerfile instruction to the created image
    -m, --message string   Commit message
    -p, --pause            Pause container during commit (default true)
  [root@spark01 ~]# 
  
  #做镜像的过程尽量让其暂停
  docker commit -p comtainer_name
  
  docker commit -
  
  #标签管理
  [root@spark01 ~]# docker tag --help
  
  Usage:  docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]
  
  Create a tag TARGET_IMAGE that refers to SOURCE_IMAGE
  
  #可以在已有的标签上打另一个标签
  
  docker tag container_id chenyansong/httpd:v0.1-1
  
  #查看标签
  docker image ls
  
  docker tag chenyansong/httpd:v0.1-1 chenyansong/httpd:latest
  
  #删除一个镜像
  docker image rm chenyansong/httpd:v0.1-1
  #这里只是删除了镜像的tag而已，并没有真正的删除镜像，因为该镜像还有一个引用
  
  #在制作镜像时打标签
  #镜像中定义了启动该容器时，默认运行的程序
  docker inspect image_name|Id
  #其中有一个cmd，如nginx的cmd
              "Cmd": [
                  "nginx",
                  "-g",
                  "daemon off;"#因为容器中只有一个进程，所以容器中的程序必须运行在前端，如果运行在后台，那么容器会立即结束
              ],
   #可以看到，启动镜像时默认运行的程序
  
  
  #改变容器的默认启动程序
  docker commit -a "chenyansong <chenyansong@gmail.com>" -c 'CMD ["/bin/httpd", "-f","-h", "/data/html"]' -p containername chenyansong/httpd:v0.2
  
  docker image ls
  
  #docker run --name t2 chenyansong/httpd:v0.2
  
  #另外一个终端
  curl 
  
  #docker inspect t2
  ```

  

