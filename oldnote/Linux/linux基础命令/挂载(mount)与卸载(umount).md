---
title: Linux基础命令之挂载(mount)与卸载(umount)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



# 1.mount 挂载
```
#语法
mount [选项] <-t 类型> [-o 挂载选项] <设备> <挂载点>

mount -t ext3 /dev/hdb1 /mnt

```

# 2.umount卸载
```
#语法
umount <挂载点|设备>

$ umount /mnt
#or
$ umount /dev/hdb1


```

&emsp;有些时候，可能某些设备(通常是 CD-ROM)正忙或无法响应。此时，大多数用户的解决办法是重新启动计算机。我们大可不必这么做。例如，如果 umount /dev/hdc 失败的话，您可以试试“lazy” umount。语法十分简单：
umount -l <挂载点|设备>

&emsp;卸载的时候,需要umount设备文件名,但是注意不要在挂载的设备中执行卸载,这样是卸载不掉的,因为坐在椅子上如何将椅子拿掉,所以需要在挂载设备的外面进行卸载
 

 