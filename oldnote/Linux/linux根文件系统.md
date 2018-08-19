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

  ​	块设备：随机访问

  ​	





