[toc]

gdb:调试文件，调试的思路：

1. 查看源文件，在指定的位置打断点

```shell
#添加调试信息
gcc *.c -o app -g
gcc *.c -o app1
```

![](/Users/chenyansong/Documents/note/images/c_languge/image-20191102165753909.png)

```shell
#调试启动
gdb app

```

![image-20191102170047469](/Users/chenyansong/Documents/note/images/c_languge/image-20191102170047469.png)

查看源文件内容

```shell
#查看的是包含main的文件的前10行
gdb)l
```



![image-20191102170635697](/Users/chenyansong/Documents/note/images/c_languge/image-20191102170635697.png)

查看其它文件

```shell
#直接显示指定的第多少行的后10行
gdb)l select_sort.c:20

#查看文件中的指定函数
gdb)l select_sort.c:selectSort
gdb)l  #继续列出该函数以后的内容
gdb)  #回车，默认执行上一次的操作，这样就可以向下翻页看源文件的内容
```

打断点

```shell
gdb)break 22
```

![image-20191102171325203](/Users/chenyansong/Documents/note/images/c_languge/image-20191102171325203.png)

```shell
gdb)b 37  #break的简写形式 b

```

03_gdb调试(下)