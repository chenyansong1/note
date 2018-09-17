[TOC]



# rpm由来

我们一般按照软件的思路是，在二进制的文件，依赖的库文件，配置文件放到指定的目录下，这样就能完成安装，但是如果我们有500个这个的软件需要安装，那么就是一个重复的，费时的工作，这时就有了RPM的由来



软件包管理器：

* 打包成一种文件：二进制文件，库文件，配置文件，帮助文件
* 生成数据库，记录所安装的每一个文件（将来我们要删除的时候，我们可以根据记录移除即可）



软件包管理器的核心功能：

1. 制作软件包
2. 能够实现：安装，卸载升级，查询，校验，数据库的重建



# rpm



## 命名



rpm包命名

包：组成部分

​	主包：

​		bind-9.7.1.1-1.e15.i586.rpm

​	子包：

​		bind-libs--9.7.1.1-1.e15.i586.rpm

​		bind-utils--9.7.1.1-1.e15.i586.rpm

包名格式：

​		name-version-release.arch.rpm

​		bind-majaor.minor.release-release.arch.rpm

主版本号：重大改进

次版本号：某个子功能发生重大变化

发行号：修正了部分bug，调整了一点功能



格式：

​	二进制格式的：rpm包作者下载源程序，编译配置完成后，制作成rpm包

​		bind-9.7.1-1.el5.rpm  ：9.7.1是程序的version，后面的1.el5是rpm的信息，1表示该rpm包第一次制作，el5是红帽企业版5（适用的平台）

​		bind-9.7.1-1.noarch.rpm   ：noarch表示和平台无关(uname -a 查看平台)

​	源码格式的



## 安装

```
rpm -i /path/to/packge_file
	-h : 以#显示进度，每个#表示2%
	-v ：显示详细信息
	-vv : 更详细的信息
	--nodeps:忽略依赖关系(可能导致软件装上了，也用不了)
	--replacepkgs:重新安装(替换原有的安装)
	--oldpackage:降级安装
	
	--force:强行安装，可以实现重装，或者降级

rpm -ivh /path/to/package_file

```



## 查询

```
rpm 
	-q package_name ： 查询指定的包是否安装
	-qa :查询所有已经安装的包
		rpm -qa|grep "xxx"
	-qi package_name:查询指定包的说明信息
	-ql package_name:查询指定包安装后生成的文件列表
		rpm -ql zsh|more
	-qf /path/to/somefile:查询指定的文件是由哪个rpm包安装生成的
	-qc package_name :查询指定软件包安装的配置文件
		[webuser@VM_0_4_centos ~]$ rpm -qc mysql-community-server
        /etc/logrotate.d/mysql
        /etc/my.cnf
        
    -qd package_name:查询指定包安装的帮助文档(d:document)
    
    -q --scripts package_name: 查询指定包中包含的脚本
    	安装前，安装后，卸载前，卸载后 四类脚本
    
	#如果某rpm包尚未安装，我们需要查询其说明信息，安装以后会生成的文件
	rpm -qpi /path/to/package_file	#软件包的说明信息
	rpm -qpl /path/to/package_file	#可能会生成哪些文件
	rpm -qpc /path/to/package_file	#可能生产的配置文件
	rpm -qpd /path/to/package_file
	
	

#eg
rpm -q zsh
#模糊匹配
rpm -qa zs*  

```



查询指定包执行的脚本

```
[webuser@VM_0_4_centos ~]$ rpm -q --scripts  mysql-community-server
preinstall scriptlet (using /bin/sh):安装前脚本
/usr/sbin/groupadd -g 27 -o -r mysql >/dev/null 2>&1 || :
/usr/sbin/useradd -M -N -g mysql -o -r -d /var/lib/mysql -s /bin/false \
    -c "MySQL Server" -u 27 mysql >/dev/null 2>&1 || :
postinstall scriptlet (using /bin/sh):安装后脚本
datadir=$(/usr/bin/my_print_defaults server mysqld | grep '^--datadir=' | sed -n 's/--datadir=//p' | tail -n 1)
/bin/chmod 0751 "$datadir" >/dev/null 2>&1 || :
/bin/touch /var/log/mysqld.log >/dev/null 2>&1 || :
/bin/chown mysql:mysql /var/log/mysqld.log >/dev/null 2>&1 || :

if [ $1 -eq 1 ] ; then 
        # Initial installation 
        systemctl preset mysqld.service >/dev/null 2>&1 || : 
fi 

/usr/bin/systemctl enable mysqld >/dev/null 2>&1 || :
preuninstall scriptlet (using /bin/sh):卸载前脚本

if [ $1 -eq 0 ] ; then 
        # Package removal, not upgrade 
        systemctl --no-reload disable mysqld.service > /dev/null 2>&1 || : 
        systemctl stop mysqld.service > /dev/null 2>&1 || : 
fi
postuninstall scriptlet (using /bin/sh):卸载后脚本

systemctl daemon-reload >/dev/null 2>&1 || : 
if [ $1 -ge 1 ] ; then 
        # Package upgrade, not uninstall 
        systemctl try-restart mysqld.service >/dev/null 2>&1 || : 
fi
[webuser@VM_0_4_centos ~]$ 
```



## 升级,降级

```
rpm 
	-Uvh /path/to/new_package_file	:如果装有老版本的，则升级，否则，就安装
	-Fvh /path/to/new_package_file ：如果装有老版本的包就升级，否则就退出
	
```



如果升级出现问题，我们需要降级会原来的版本

```
rpm -Uvh --oldpackage package_name
```



# 卸载

```
rpm -e package_name

```



## 校验

```
rpm -V package_name
#没有信息就说明校验通过
```

我们改变zsh的其中两个文件（删除一个，修改一个）

![image-20180917102641760](/Users/chenyansong/Documents/note/images/linux/filesystem/rpm1.png)





![image-20180917102758815](/Users/chenyansong/Documents/note/images/linux/filesystem/rpm2.png)



## 检验来源合法性，以及软件完整性

```
[webuser@VM_0_4_centos ~]$ ll /etc/pki/rpm-gpg/
总用量 20
-rw-r--r--. 1 root root 1690 12月  9 2015 RPM-GPG-KEY-CentOS-7
-rw-r--r--. 1 root root 1004 12月  9 2015 RPM-GPG-KEY-CentOS-Debug-7
-rw-r--r--. 1 root root 1690 12月  9 2015 RPM-GPG-KEY-CentOS-Testing-7
-rw-r--r--  1 root root 5969 10月 16 2015 RPM-GPG-KEY-mysql

#导入秘钥
rpm --import /etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7

#验证
rpm -K /package/to/package_file

dsa,gpg:验证来源合法性，即验证签名；--nosignature 略过验证
sha1,md5:验证软件包完整性；--nodigest 略过此项

#
rpm -K -nodigest 
```



## 重建



```
rpm 
	--rebuilddb : 重建数据库
	--initdb:初始化数据库
```

