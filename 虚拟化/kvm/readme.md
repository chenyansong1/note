kvm:Kernel-based Virtual Machine,基于内核的虚拟化技术

以模块的形式，被嵌入在Linux内核中

> lsmod|grep kvm



2. 硬件辅助的虚拟化技术
   1. CPU去支持虚拟化技术

3. 宿主型的Hypervisor

   1. 物理机Hypervisor（host)：支持虚拟化的那个服务器或者host

   2. 虚拟机（guest）

      1. 对于硬件的驱动和管理是由我们的OS来管理的，宿主型的Hypervisor，所以需要在host上安装一个操作系统(OS)
      2. 裸机型Hypervisor：不需要额外的安装操作系统，对于硬件的驱动和管理是由Hypervisor



虚拟化技术：

1. 纯软件 VMWare Workstations

​	qemu Linux开源：

​	1.对于硬件的要求低

​	2.速度慢

​	qemu	模拟磁盘，模拟网卡等等

​	qemu/kvm  , kvm 有个特点，不进行设备的模拟，设备的模拟依赖于qemu来完成的



2. 硬件辅助类型的虚拟化技术

   现在的虚拟化技术，基本上都要CPU的支持

3. 完全虚拟化技术

   KVM，不需要去修改guest的OS，直接运行在这种虚拟化上

4. 半虚拟化技术

   guest必须要修改内核：XEN



宿主机：Host Hypervisor

1. 宿主型
2. 裸机型
3. 混合型



KVM：

1. 基于内核的虚拟化技术
> lsmod |grep kvm 

2. 硬件辅助的虚拟化技术

> cat /proc/cpuinfo	|egrep "svm|vmx"
>
> amd:svm;  intel : vmx

2. 宿主型的虚拟化技术

   安装一个OS，用操作系统来管理和驱动硬件

3. 完全虚拟化技术

   对于kvm上的guest 不需要去修改内核

4. KVM不进行的设备的模拟，使用 qemu去模拟



1. 由于是宿主型的，所以host需要安装一个操作系统（RHEL6/CentOS)

2. 由于kvm是一个基于内核的虚拟化技术，所以在安装完成操作系统之后，kvm的模块就已经安装完成了（当然可以去源码编译kvm模块）

3. 安装管理软件（管理kvm)

   1. 图形化的管理软件：

      1. virt-manager
      2. virsh
      3. virt-install

      > yum install virt-manager virsh virt-install libvirt*

   2. 平台

      1. RHEV———Ovirt
      2. OpenStack

4. cpu是支持虚拟化功能



```
1. 安装管理工具
yum install virt-manager virsh virt-install libvirt*
   
2. 启动一个服务
systemctl status libvirtd
systemctl start libvirtd
如果启动失败，需要看 /usr/sbin/libvirtd 前台启动，看报错，一定是哪些依赖包没有安装，此时需要yum安装即可解决报错

3.安装虚拟机
3.1.采用图形化安装
	1.使用本地的ISO
	2.network安装
	3.pxe：网络引导安装
	4.导入一个虚拟机(导入磁盘)
3.2.vir-install安装(命令行)

```



https://ke.qq.com/course/335149



![image-20190104225154232](/Users/chenyansong/Documents/note/images/kvm/virt-manager1.png)






