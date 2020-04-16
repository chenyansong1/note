[toc]

makefile项目代码管理工具，因为如果针对一个具体的项目，我们不可能使用手动的gcc去指定参数去编译，这样会很复杂，他类似于Java中的maven



# 第一个makefile

* makefile命名

  Makefile/makefile

* makefile的规则

  1. 目标
  2. 依赖
  3. 命令

  ![](/Users/chenyansong/Documents/note/images/c_languge/makefile基本原则.png)

```shell
#vim makefile
目标:依赖条件
	命令 #之前必须要有一个tab缩进

app:main.c add.c sub.c mul.c
	gcc main.c add.c sub.c mul.c -o app
	
#完成之后，我们make
make
```

![image-20191103092523531](/Users/chenyansong/Documents/note/images/c_languge/image-20191103092523531.png)

# 第二个版本makefile

分开对源文件进行编译

```shell
#第一条规则是终极目标，以后下面的规则是为生成终极目标服务的(依赖)
app:main.o add.o sub.o mul.o
	gcc main.o add.o sub.o mul.o -o app
	
main.o:main.c
	gcc -c main.c
	
add.o:add.c
	gcc -c add.c
	
sub.o:sub.c
	gcc -c sub.c
	
mul.o:mul.c
	gcc -c mul.c
	
```

![image-20191103093637641](/Users/chenyansong/Documents/note/images/c_languge/image-20191103093637641.png)

如果我们只对其中一个文件进行修改，重新make的时候，只会对修改的文件进行重新编译

![](/Users/chenyansong/Documents/note/images/c_languge/image-20191103093959636.png)



# makefile的工作原理

![](/Users/chenyansong/Documents/note/images/c_languge/makefile工作原理-1.png)

通过对比.o和.c文件的修改时间，正常是.o文件比.c文件生成的时间要晚，如果不是，那么说明.c文件发生了修改，那么就要重新编译

![](/Users/chenyansong/Documents/note/images/c_languge/makefile工作原理-2.png)



# makefile中的变量

## 第三个版本

对于makefile文件中的重复的部分，我们可以使用变量去替换

```shell
#自定义的变量
obj=main.o add.o sub.o mul.o
target=app
$(target):$(obj)
	gcc $(obj) -o $(target)
# gcc $^ -o $@
	
%.o:%.c
	gcc -c $< -o $@

#makefile的自动变量(只能在规则中的命令来使用)
$<  : 规则中的第一个依赖 ， 对于上面就是 main.o
$@  : 规则中的目标
$^  : 规则中的所有依赖


#main.o:main.c
#	gcc -c main.c

#Makefile自己维护的变量
CPPFLAGS
```

![image-20191103100738960](/Users/chenyansong/Documents/note/images/c_languge/image-20191103100738960.png)



# makefile函数

makefile中所有的函数都是有返回值的

```shell
#自定义的变量
#obj=main.o add.o sub.o mul.o #如果项目中有很多的.o，那么这里就会很麻烦
target=app
	
#获取指定目录下的.c
#定义了一个变量，获取函数的返回值
src=$(wildcard ./*.c) #当前目录下的.c
#将所有的.c替换为.o, .c的输入来自 src变量
obj=$(patsubst ./%.c, ./%.o, $(src))


$(target):$(obj)
	gcc $(obj) -o $(target)
# gcc $^ -o $@
	
%.o:%.c
	gcc -c $< -o $@

#删除当前目录下的所有.o , target
clean:
	rm $(obj) $(target) -f  #如果文件不存在，会报错;-f表示强制执行
	
#make clean 这样就能执行清除
```

![](/Users/chenyansong/Documents/note/images/c_languge/image-20191103103150469.png)



执行我们自己定义的函数

```shell
hello:
	echo "hello, makefile"
	
#执行hello
```

![](/Users/chenyansong/Documents/note/images/c_languge/image-20191103104150909.png)

伪目标

```shell
.PHONY:clean  #如果是伪目标，那么就不会进行时间戳的比较，每次重新生成
clean:
	-rm #如果当前的命令执行失败，继续向下执行
	-mkdir /aaa  #如果创建失败，忽略，直接向下执行
	rm $(obj) $(target) -f 
```

![image-20191103105112403](/Users/chenyansong/Documents/note/images/c_languge/image-20191103105112403.png)

