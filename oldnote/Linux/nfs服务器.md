[toc]

nfs类似于Windows的共享文件夹

```shell
#install
sudo apt-get install nfs-kernel-server

#创建共享目录

#modify config
##vi /etc/exports
##添加共享目录: * 代表IP网段; 读写权限：ro, wo, rw; sync实时同步数据到磁盘 
/home/Robin/NfsShare *(rw,sync)


#reload
sudo service nfs-kernel-server restart


#客户端访问，通过挂载服务器共享目录的方式访问
sudo mount serverIp:sharedDir /mnt
```

