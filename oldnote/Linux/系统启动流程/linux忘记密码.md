[TOC]

转自：https://blog.csdn.net/xujing19920814/article/details/75103607

1.重启开机 
2.按e 

![](E:\git-workspace\note\images\linux\command\boot)

3.编辑修改两处：ro改为rw,在LANG=en_US.UFT-8后面添加init=/bin/she 

![](E:\git-workspace\note\images\linux\command\boot2.png)

![](E:\git-workspace\note\images\linux\command\boot3.png)

4、按Ctrl+X重启，并修改密码 
输入passwd –stdin root 

![è¿éåå¾çæè¿°](E:\git-workspace\note\images\linux\command\boot4.png)

5、由于selinux开启着的需要执行以下命令更新系统信息,否则重启之后密码未生效 
touch /.autorelabel 
6.重启系统 
exec /sbin/init