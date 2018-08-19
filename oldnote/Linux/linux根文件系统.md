FHS:文件系统层级标准



* /boot

```Shell
[webuser@VM_0_4_centos ~]$ ll /boot/
总用量 131532
-rw-r--r--. 1 root root   126426 11月 20 2015 config-3.10.0-327.el7.x86_64
-rw-r--r--  1 root root   137701 7月   4 2017 config-3.10.0-514.26.2.el7.x86_64
drwxr-xr-x. 2 root root     4096 4月  21 2016 grub
drwx------. 6 root root     4096 3月  19 11:36 grub2
-rw-------. 1 root root 44282364 4月  21 2016 initramfs-0-rescue-5e1e8c3a990d887d4ac1.img
-rw-------. 1 root root 17174163 4月  21 2016 initramfs-3.10.0-327.el7.x86_64.img
-rw-------  1 root root 15608069 3月  19 11:34 initramfs-3.10.0-327.el7.x86_64kdump.img
-rw-------  1 root root 18264846 3月  19 11:37 initramfs-3.10.0-514.26.2.el7.x86_64.img
-rw-------  1 root root 15933647 5月  19 12:34 initramfs-3.10.0-514.26.2.el7.x86_64kdump.img
-rw-r--r--. 1 root root   602628 4月  21 2016 initrd-plymouth.img
-rw-r--r--. 1 root root   252612 11月 20 2015 symvers-3.10.0-327.el7.x86_64.gz
-rw-r--r--  1 root root   277955 7月   4 2017 symvers-3.10.0-514.26.2.el7.x86_64.gz
-rw-------. 1 root root  2963044 11月 20 2015 System.map-3.10.0-327.el7.x86_64
-rw-------  1 root root  3114352 7月   4 2017 System.map-3.10.0-514.26.2.el7.x86_64
-rwxr-xr-x. 1 root root  5156528 4月  21 2016 vmlinuz-0-rescue-f9d400c5e1e8c3a8209e990d887d4ac1
-rwxr-xr-x. 1 root root  5156528 11月 20 2015 vmlinuz-3.10.0-327.el7.x86_64
-rwxr-xr-x  1 root root  5397008 7月   4 2017 vmlinuz-3.10.0-514.26.2.el7.x86_64
[webuser@VM_0_4_centos ~]$ 

# 和系统启动相关的文件
initramfs-xxx.img 
vmlinuz-xxx
grub
```

* /dev

  设备文件

  ​	块设备：随机访问，数据块，没有文件大小，只是存储的是设备元数据

  ​	字符设备：线性访问，按字符为单位：显示器；键盘；鼠标

* /etc



* /home

用户的家目录，在/home下有一个和用户名同名的目录，root用户的家在:/root



* /lib库文件

  /lib/modules:内核模块文件目录

  静态库：.a	将依赖单独打包，这样可以直接运行

  动态库：.so (shared object)，如果第一个程序启动的时候载入该库到内存中，那么当第二个程序也需要该库的时候，就直接读取内存，所以叫做共享库

  > 库文件不能被单独执行，只能被调用



* /media  /mnt 挂载点

 我们必须将 /dev/xxx 下的设备挂载到指定的目录下，然后我们才能访问

/media 挂载点 移动设备

/mnt : 挂载点目录，额外临时文件系统



* /opt

 可选目录



* /proc: 伪文件系统，内核映射文件（内核的可调参数和工作数据）



* /sys	: 伪文件系统：跟硬件设备相关的属性映射文件，硬件设备管理
* /tmp : 临时文件目录：每隔一个月系统自动删除，每个人都可以删除自己创建的文件
* /var : 可变化的文件

​	

* /bin : 可执行文件，用户命令

* /sbin: 管理命令

* /usr: shared,read-only 全局共享只读文件

  /usr/bin: 

  /usr/sbin

  /usr/lib

  

​      /usr/local 第三方软件需要依赖

​	/usr/local/bin

​	/usr/local/sbin

​	/usr/local/lib





* 命令规则
  * 长度不能超过255个字符
  * 不能使用/当做文件名
  * 严格区分大小写
* 相对路径：
* 绝对路径



mkdir /x/y/z -p

p：parent



mkdir /x/y/{a,b,c} -p



mkdir /x/y/{a/aa,y/yy}



mkdir /x/{a,d}_{b,c}

/x/a_b

/x/a_c

/x/d_d

/x/d_c





touch:  change file timestamp 改变文件的时间戳

touch aa.txt  默认创建aa.txt文件，如果不需要创建文件，那么使用-c创建

touch -c bb.txt 



-a change only the access time 只改变文件的访问时间

-m change only the modification time 只改变修改时间

-c do not create any files 不创建文件

-d 指定时间

-t 指定时间戳



![image-20180819151259924](/Users/chenyansong/Documents/note/images/linux/command/touch_stat2.png)



![image-20180819151449023](/Users/chenyansong/Documents/note/images/linux/command/touch_stat3.png)



查看一个文件的状态信息

stat bb.txt

![image-20180819151026419](/Users/chenyansong/Documents/note/images/linux/command/touch_stat.png)



rm  aa.txt  之所以有提示，因为使用了别名的缘故

\rm  aa.txt  \表示使用的是原生的命令，而原生的命令是没有提示的



cp

复制

一个文件到一个文件

cp /etc/passwd /tmp/				#复制到目录下

cp /etc/passwd /tmp/new_passwd	#重命名

cp -r /etc/init.d/  /tmp/    #递归复制目录

cp -r /etc/init.d  /tmp/hello     # 将init.d重命名为hello

多个文件到一个目录

cp -f 强制复制

cp -p 保留原来文件的属组，和时间戳信息

cp -a 保留文件的所有属性，相当于 -dR   **归档复制，常用于备份**

对于软连接，只是使用cp那么复制的是该文件

cp -P 保持连接自有的属性，这样cp之后，还是链接

cp -L 复制的是链接指向的文件

cp /etc/{passwd, inittab, rc.d/rc.ssyinit} 	/tmp  #复制3个文件









