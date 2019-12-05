[TOC]

```shell
[root@master ~]# cat  if_info.sh         
#!bin/bash

data_file=/root/if_netflow_info.txt

if_cnt=`cat $data_file |awk -F ":" '{print $1}'|sort |uniq|wc -l`
msg_cnt=$((if_cnt * 120))
#echo $msg_cnt

tail -$msg_cnt $data_file>${data_file}.tmp
mv ${data_file}.tmp ${data_file}

```



转自：https://blog.csdn.net/wojiushiwoba/article/details/73872215

这几天在做ARM linux嵌入式设备的应用程序，要记录设备的启动记录，但是又怕长时间运行，记录文件太大，导致写爆存储，故想之保留记录文件的最后N行来保证存储不会被写爆。

故开始问度娘要解决方案，网上提供了head和tail两个命令。
    
1. head命令
  
       head命令用于显示文件文字区块，可以显示文件的前N行，例如：head -10 test.txt  ,该命令会打印test.txt文件的前10行到终端。
  
2. tail命令
  
        tail命令用于显示文件文字区块，可以显示文件的最后N行，例如：tail -10 test.txt  ,该命令会打印test.txt文件的最后10行到终端。
   
   我想让记录文件之保留最后1000行，尝试使用tail命令然后重定向的方式来实现，使用tail -1000 test.txt > test.txt，但是使用该命令后test.txt文件直接被清空了，看来不能把输出重定向到自己。
   
   然后接着尝试，使用比较折中的办法，先把记录文件的最后1000行重定向到另一个文件B，然后删除原有的文件，再把B文件修改为原有的记录文件名，经过试验该方法可以实现我想要的结果。命令如下：
   
   ```shell
   tail -22 $data_file>${data_file}.tmp
   mv ${data_file}.tmp ${data_file}
   ```
   
   方法虽然有点曲折，但是还是实现了要求，不知大神是否有别的更直接的方式实现保留某个文件的最后N行的方法，还请大神赐教。
   

