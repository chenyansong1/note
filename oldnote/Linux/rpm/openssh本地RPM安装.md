```
1、在一台完整的CentOS 5.8查找scp所在的包：
# which scp
/usr/bin/scp
# rpm -qf /usr/bin/scp
openssh-clients-4.3p2-82.el5

2、查出是属于openssh-clients包后，插入CentOS 6.1系统光盘，挂载后找到openssh-clients，安装时提示：
libedit.so.0()(64bit) is needed by openssh-clients-5.3p1-52.el6.x86_64

需要先安装libedit，再安装openssh-clients
# rpm -ivh cdrom/Packages/libedit-2.11-4.20080712cvs.1.el6.x86_64.rpm
warning: cdrom/Packages/libedit-2.11-4.20080712cvs.1.el6.x86_64.rpm: Header V3 RSA/SHA256 Signature, key ID c105b9de: NOKEY
Preparing...                ########################################### [100%]
   1:libedit                ########################################### [100%]
# rpm -ivh cdrom/Packages/openssh-clients-5.3p1-52.el6.x86_64.rpm
warning: cdrom/Packages/openssh-clients-5.3p1-52.el6.x86_64.rpm: Header V3 RSA/SHA1 Signature, key ID c105b9de: NOKEY
Preparing...                ########################################### [100%]
   1:openssh-clients        ########################################### [100%]
3、也可以通过以下命令直接安装：
# yum install openssh-clients

```



其实直接安装 opensshxxx.rpm 如果缺少什么依赖，他会报错的，直接安装响应的依赖就行了

