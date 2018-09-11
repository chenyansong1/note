[TOC]



# read 语法

read 读取输入到指定的变量中



```
chenyansongdeMacBook-Pro:Linux chenyansong$ read name age
chenyansong 21
chenyansongdeMacBook-Pro:Linux chenyansong$ echo $name
chenyansong
chenyansongdeMacBook-Pro:Linux chenyansong$ echo $age
21

# 输入的参数少了
chenyansongdeMacBook-Pro:Linux chenyansong$ read name age
jerry
chenyansongdeMacBook-Pro:Linux chenyansong$ echo $name
jerry
chenyansongdeMacBook-Pro:Linux chenyansong$ echo $age

# 输入的参数多了
chenyansongdeMacBook-Pro:Linux chenyansong$ read name age
chenyansong 22 33 44
chenyansongdeMacBook-Pro:Linux chenyansong$ echo $name
chenyansong
chenyansongdeMacBook-Pro:Linux chenyansong$ echo $age
22 33 44
chenyansongdeMacBook-Pro:Linux chenyansong$ 



#read -p "给出的提示"
#read -t timeNuber  #等待timeNuber 秒

```



# 实例



## 计算两个输入的数的和

```
#!/bin/bash

read -p "input two intergers:" A B
echo "$A + $B = $[$A+$B]"

```



##  给一个输入的默认超时时间



```


# 超过输入的时间，给一个默认值
read -t 5 -p "Input two integers[default a=100,b=1000]:" A B
[ -z $A ]&&A=100
[ -z $B ]&&B=1000

echo "$A + $B = $[$A+$B]"

```



# 对给定的文件归档



```Shell
#!/bin/bash

read -p "thredd files: " file1 file2 file3
read -p "destion:" dest

tar -jcf $dest.tar.bz2 $file1 $file2 $file3

```



* 改进版

```Shell
#!/bin/bash

read -p "thredd files: " file1 file2 file3
read -p "destion:" dest
read -p "Compress[gzip|bzip2|xz]:" comp

case $comp in
gzip)
	tar -zcf $dest.tar.bz2 $file1 $file2 $file3
	;;
bzip2)
	tar -jcf $dest.tar.bz2 $file1 $file2 $file3
	;;
xz)
	tar -Jcf $dest.tar.xz $file1 $file2 $file3
	;;
*)
	echo "没有遮掩的压缩格式"
	exit 9
	;;
esac

```











