---
title: scala初探
date: 2017-01-5 08:55:29   
categories: scala   
toc: true  
tag: scala
---

# 1.变量定义
>先写变量后写类型
```
scala> val msg: java.lang.String = "hello world!"
msg: String = hello world!

//或者
scala> val msg: String = "hello world!"        #先写变量名称,再写变量类型
msg: String = hello world!    #定义了一个名称为msg的变量,类型为String, 值为 "hello world!"

scala>

```
>自动类型推断
```
#也可以省略类型的定义,scala会自动推断出变量的类型
scala> val msg = "hello world!"    #因为值的类型是String,所以可以推断出msg的类型是String,所以在定义变量的时候可以省略对msg类型的推断
msg: String = hello world!        

scala>

```
>val类型变量不能修改,var可以
```
#val类型的变量不能再次对其进行赋值操作

scala> msg = "goodby cruel world!"
<console>:8: error: reassignment to val
       msg = "goodby cruel world!"
           ^


#var可以重复赋值

scala> var msg_var: String = "hello world!"
msg_var: String = hello world!

#对变量重新赋值
scala> msg_var = "hello 2222 world!"
msg_var: String = hello 2222 world!

scala>

```


# 2.函数定义
>函数的基本结构
```
def max(x: Int, y:Int): Int = {
  if(x > y) x else y
}
#scala的条件表达式可以像Java的三元操作符那样生成结果值
if(x > y) x else y      #scala中的if/else不仅控制语句的执行流程,同时有返回值,所以他不等同于Java中的if/else
#等同于Java中的:
(x > y)? x: y
```

 

>函数的返回值类型
```
#函数的返回值类型可以不用写,因为可以使用函数体推断出来,但是如果函数是递归的,那么必须明确的说明返回值的类型

#尽管如此,显示的说明函数结果类型也经常是一个好主意,这种类型标注可以使代码便于阅读,因为读者不用研究函数体之后再去猜测结果类型

```
>函数体
```
如果函数仅包含一个语句,那么连花括号都可以选择不谢,这样max函数就可以写成:
def max2(x: Int, y:Int): Int =  if(x > y) x else y

```
>没有参数和返回值的函数
```
  
scala> def max2(x: Int, y:Int): Int =  if(x > y) x else y
max2: (x: Int, y: Int)Int
 
scala> def greet() = println("Hello world!")
greet: ()Unit        #greet是函数名,  ()说明函数不带参数,  Unit是greet的结果类型,指的是函数没有有效的返回值



```

# 3.编写scala脚本
```
#在hello.scala文件中,有如下的代码:
println("hello world , from a script! ")

#执行上述脚本文件
$ scala hello.scala

#系统输出
hello world , from a script! 


#传参,scala脚本的命令行参数保存在名为args的scala数组中
println("hello world , from " + args(0) + " a script! ")      //注意scala中的数组是以()去取元素的,而不是像Java中是以[]去取元素


#再次执行脚本
$ scala hello.scala xxxx
hello world , from xxxx a script! 


```


# 4.用while循环,用if做判断
```
#在printargs.scala文件里,输入以下代码测试while
var i = 0
while (i < args.length) {
  println(args(i))
  i += 1
}

#上述代码并不是scala推荐的代码风格,在这里只是有助于解释while循环
#在scala中并没有++/--,必须写成+= / -=这样的操作


```




# 5.用foreach和for做枚举
>foreach
```
#上面所写的while循环的编码风格被称之为指令式编程(即:逐条执行指令,并经常改变不同函数之间的共享状态,在Java/C++/C这些语言中常见),在scala中更偏向的是函数式编程,如下:

#在pa.scala文件中,如下代码:
args.foreach(arg=>println(arg))

#执行
$ scala pa.scala xx1 xx2 xx3

#打印
xx1
xx2
xx3

#在上述例子中,scala解释器可以推断arg的类型为String,因为String是调用foreach的那个数组的元素类型,当然也可以更明确的给args加上类型名:
args.foreach( (arg: String) =>p rintln(arg))


#更加简洁的写法

args.foreach(p rintln(arg))

```


 

>for
```
#scala也是提供了像指令式的for

for (arg <- args){
  println(arg)
}
#<- 右侧是已经在前面见过的args数组,<-左侧的arg是val的名称(不是var,这里一定是val),尽管arg可能感觉像var,因为每次枚举都会得到新的值,但这的确是val,因为他不能在for表达式的函数体中被重新赋值,所以,对于args数组的每个元素,枚举的时候都会创建并初始化新的arg值,然后调用执行for的函数体



```
 








