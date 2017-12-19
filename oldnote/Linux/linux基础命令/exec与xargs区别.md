---
title: Linux基础命令之exec与xargs区别
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



```
#对找到的结果一次性打包
find /home/chenyansong/ -type f|xargs tar zcvf test.gar.gz


#对每一个找到的文件都进行打包一次(其实就是对最后一个文件进行了打包,因为后面的打包的文件名对前面的进行了覆盖)
find /home/chenyansong/ -type f -exec tar zcvf test_exec.tar.gz {} \;

```

举例说明:
```
$find test/ -type f
test/myfile.name
test/files/role_file
test/files/install_file
 
 #使用xargs去打印找到的文件
$find test/ -type f |xargs echo
test/myfile.name test/files/role_file test/files/install_file
 
 
#使用-exec去打印找到的文件
$find test/ -type f -exec echo {} \;
test/myfile.name
test/files/role_file
test/files/install_file

/*
很明显是:xargs把\n转换成了空格,exec是对每个找到的文件执行一次命令，除非这单个的文件名超过了几k，否则不会出现命令行超长出报错的问题。
而xargs是把所有找到的文件名一股脑的转给命令。当文件很多时，这些文件名组合成的命令行参数很容易超长，导致命令出错。

*/
```

&emsp;另外， find | xargs 这种组合在处理有空格字符的文件名时也会出错，因为这时执行的命令已经不知道哪些是分割符、哪些是文件名中的空格！ 而用exec则不会有这个问题。

```
$touch test/'test zzh'
 
$find test/ -name *zzh
test/test zzh
 
$find test/ -name *zzh |xargs rm
rm: cannot remove `test/test': No such file or directory
rm: cannot remove `zzh': No such file or directory
 
$find test/ -name *zzh -exec rm {} \;

```

> 总结

相比之下，也不难看出各自的缺点
1、exec 每处理一个文件或者目录，它都需要启动一次命令，效率不好;
2、exec 格式麻烦，必须用 {} 做文件的代位符，必须用 \; 作为命令的结束符，书写不便。
3、xargs 不能操作文件名有空格的文件,当文件很多时，这些文件名组合成的命令行参数很容易超长，导致命令出错；
 
&emsp;综上，如果要使用的命令支持一次处理多个文件，并且也知道这些文件里没有带空格的文件，那么使用 xargs比较方便; 否则，就要用 exec了。


