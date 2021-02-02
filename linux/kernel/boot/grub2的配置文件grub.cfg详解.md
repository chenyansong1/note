[toc]







```shell
set root=(hd0,7)
#设定boot分区  

search –no-floppy –fs-uuid –set f255285a-5ad4-4eb8-93f5-4f767190d3b3
#设定uuid=****的分区为root，和上句重复，可删除  


####设备与分区


GRUB2对设备与分区的命名规则举例如下，看看就能明白。需要说明的是磁盘从"0"开始计数，分区从"1"开始计数。

(fd0)          第一软盘
(hd0)          第一硬盘[大多数U盘与USB接口的移动硬盘以及SD卡也都被当作硬盘看待]
(hd1,1)        第二硬盘的第一分区(通用于MBR与GPT分区)
(hd0,msdos2)   第一硬盘的第二MBR分区，也就是传统的DOS分区表
(hd1,msdos5)   第二硬盘的第五MBR分区，也就是第一个逻辑分区
(hd0,gpt1)     第一硬盘的第一GPT分区
(cd)           启动光盘[仅在从光盘启动GRUB时可用]
(cd0)          第一光盘

```

