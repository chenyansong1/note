[toc]

![image-20191103091312591](/Users/chenyansong/Documents/note/images/c_languge/image-20191103091312591.png)

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
gdb)b 22  #break的简写形式 b

```

设置断点的条件

```shell
#第15行，当i=15的时候进入断点
gdb) b 15 if i==15

gdb)info
gdb)i
#查看已经设置的断点信息
ddb)i b
```

![image-20191103084024756](/Users/chenyansong/Documents/note/images/c_languge/image-20191103084024756.png)

启动断点

```shell
#只会执行一步，然后停止
gdb)start
#另一种启动的方式
gdb)run

#单步，一步一步向下走
gdb)n  #next

#进入下一个断点
gdb)c  #continue

#进入函数里面step 
gdb)s  #step 进入函数内部
```

![image-20191103084327936](/Users/chenyansong/Documents/note/images/c_languge/image-20191103084327936.png)

```shell
#此时，如果你想看函数中的源代码
gdb)l #list 查看代码
gdb)l filename:rowNum #list 查看代码
gdb)l filename:funcName #list 查看代码




#此时也是可以断函数中的某一行打断点
gdb)b 23  #break 给当前文件设置断点
gdb)b fileName:rowNum(/函数名) #给其他文件设置断点

#这样就会跳转到上面的断点上
gdb)c

#想看其中的变量的值
gdb)p j #此时,打印变量j的值
gdb)p array[j] #打印数组的值


#然后继续单步执行
gdb)n

#查看变量的类型
gdb)ptype array
```

![image-20191103084901142](/Users/chenyansong/Documents/note/images/c_languge/image-20191103084901142.png)

```shell
#通过命令追踪变量的值
gdb)display i
gdb)display j
gdb)n #执行单步，如果是for循环，那么就会打印i,j的值

#去掉追踪的变量的打印
gdb)info display  #首先获取追踪变量的编号
gdb)undisplay 1 #去掉最变量i的追踪
```

![image-20191103085301119](/Users/chenyansong/Documents/note/images/c_languge/image-20191103085301119.png)

```shell
#跳出单次循环，类似于continue
gdb)u


#跳出当前的函数,但是在这之前，需要将函数中的所有的断点去掉
gdb)info break
gdb)delete 4
gdb)finish

```

![](/Users/chenyansong/Documents/note/images/c_languge/image-20191103085712381.png)

```shell
#在for循环中，当i=10的时候，才去display里面的值
gdb)set var i=10
gdb)n

```

![image-20191103085915791](/Users/chenyansong/Documents/note/images/c_languge/image-20191103085915791.png)

```shell
#退出调试
gdb)quit
```

