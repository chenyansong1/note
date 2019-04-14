---
title: Linux基础命令之tar压缩,解压缩
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



[TOC]



# 1.归档



```
# 归档
tar -cf

tar -cf test.tar test*.log

# 解归档
tar -xf
tar -xf test.tar

# 压缩归档
tar -zcf test.tar.gz test*.log #打包成gz后缀
tar -jcf test.tar.bz2 test*.log  #打包成bz后缀
tar -Jcf test.tar.xz test*.log  #打包成bz后缀

#解压缩解归档
tar -zxf test.tar.gz
tar -jxf test.tar.gz
tar -Jcf test.tar.xz




```



## 1.1.tar -zcvf 

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/基础命令/tar_1.png)



**注意：如果没有-z选项，那么打包（归档）的文件就是一个tar文件，使用-z的原因是，调用gzip的压缩工具进行压缩，所以-zcvf产生的文件后缀是.tar.gz，解压缩也是一样的，我们使用-zxvf 中-z就是解压缩gzip文件的作用 **





## 1.2.tar -tf (查看压缩列表)

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/基础命令/tar_2.png)


## 1.3.--include=文件(排除不需要打包的文件)

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/基础命令/tar_3.png)


## 1.4.tar -zcvfX (排除指定的文件列表文件)

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/基础命令/tar_4.png)


## 1.5.打包压缩的过程
```
#打包成tar文件：
tar  cf test.tar /var/www/html
#打包成tar.gz文件（经过压缩z）
tar zcf test.tar.gz /var/www/html

```

## 1.6.到要打包的文件或者是目录上级进行打包
```
[root@lamp01 chenyansong]ll
总用量 4
drwxr-xr-x 3 root root 4096 2月  13 16:00 tardir
[root@lamp01 chenyansong]tar zcvf ta11.tar.gz tardir/
tardir/
tardir/aa_1.jpg
tardir/test/
tardir/test/1.txt
tardir/aa_0.jpg
[root@lamp01 chenyansong]tar zcvf ta22.tar.gz ./tardir/
./tardir/
./tardir/aa_1.jpg
./tardir/test/
./tardir/test/1.txt
./tardir/aa_0.jpg
[root@lamp01 chenyansong]

#??不懂为什么???

```

## 1.7.对软链接文件打包
```
[root@lb01 keepalived]# ll -h /etc/rc.local
lrwxrwxrwx. 1 root root 13 7月   3 21:00 /etc/rc.local -> rc.d/rc.local
tar zcvfh rc.tar.gz  /etc/rc.local
-h  follow symlinks; archive and dump the files they point to

```

# 2.解归档

## 2.1.tar zxvf
```
[root@linux-study cys_test]# tar zxvf test_tar2.tar.gz
# x是解压缩

```

## 2.2.tar -zxvf -C 解压到指定目录
```
[root@linux-study cys_test]# tar zxvf test_tar3.tar.gz -C ./aa/
 
-C, --directory=DIR      change to directory DIR

```


## 2.3.打包成xx.tar.bz格式
```
使用tar  jcvf test.tar.bz /cys_test/    #此时不用z进行压缩,用j
解包：tar  jxvf test.tar.bz
 
 通用的解包方式：tar vf test.tar.bz   or  tar vf test.tar.gz
让程序去判断

```



# 3.压缩解压缩命令

## 3.1.gzip/gunzip 压缩和解压缩
gzip只能压缩文件，不能压缩目录，并且压缩之后源文件不见了，只剩下压缩文件。

```
gzip /path/to/somefile #压缩完成之后，会删除源文件
gzip /test/aa.log # 会删除源文件，生成aa.log.gz

gzip -d /test/aa.log.gz 相当于解压
gzip -# : #是指定压缩比
gzip -9 aa.log #指定压缩比为9，默认是6

# 临时查看文件内容
zcat aa.log.gz 



#解压缩
gunzip /path/to/somefile.gz
```



## 3.2.zip
1. 能够改变源文件
2. 能够压缩目录
```
zip 选项[-r] [压缩后文件名] [文件或目录]
    #-r表示压缩目录

#假设有两个文件：aa.txt  aa1.txt
zip a.zip aa1.txt aa.txt 
```


## 3.3.bzip2
```
bzip2 选项[-k] [文件]
    # -k 产生压缩文件后保留原文件


#解压缩
bunzip2 filename
#or
bzip2 -d filname

#查看文件
bzcat filename 
```



## 3.4.xz



```
xz filename
	-d 
	-# :1-9 默认6
	-k:压缩时保留源文件
	
xz -d 解压缩

#解压缩
unzx filename

#查看压缩文件内容
xzcat
```





# 问题



```
tar xvfz apache-tomcat-8.5.12.tar.gz
gzip: stdin: not in gzip format
tar: Child returned status 1
tar: Error is not recoverable: exiting now

#解决的方式
This error is coming due to file is not completely downloaded.
```

