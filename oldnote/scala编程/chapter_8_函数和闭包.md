---
title: 第八章 函数和闭包
categories: scala   
tags: [scala,scala编程]
---


# 1.方法
```
/*
作为某个对象的成员的函数,被称之为方法(method),
*/
import scala.io.Source
object LongLines{
  def processFile(fileName: String, width: Int): Unit ={
    val source = Source.fromFile(fileName)
    for (line <- source.getLines())
      processLine(fileName, width, line)
  }

  private def processLine(fileName: String, width: Int, line: String): Unit ={
    if (line.length>width)
      println(fileName + ": " + line.trim)
  }
}

```

<!--more-->

# 2.本地函数
```
//把函数定义在别的函数之内,就好像本地变量那样,这种本地函数仅在包含他的代码块中可见,外部无法访问
import scala.io.Source
object LongLines{
  def processFile(fileName: String, width: Int): Unit ={
    //函数中的函数
    def processLine(fileName: String, width: Int, line: String): Unit ={
      if (line.length>width)
        println(fileName + ": " + line.trim)
    }

    val source = Source.fromFile(fileName)
    for (line <- source.getLines())
      processLine(fileName, width, line)
  }
}

/*因为本地函数可以访问包含其函数的参数,你可以直接使用外部processLine 函数的参数 */
import scala.io.Source
object LongLines{
  def processFile(fileName: String, width: Int): Unit ={
    //函数中的函数
    def processLine(line: String): Unit ={
      if (line.length>width)
        println(fileName + ": " + line.trim)
    }

    val source = Source.fromFile(fileName)
    for (line <- source.getLines())
      processLine(line)
  }
}
```


# 3.头等函数
```
/*
scala的函数是头等函数,你不仅 可以定义和调用函数,还可以把他们写成匿名的字面量,并把他们作为值传递
*/
//简单的函数
(x: Int) => x+1   //=>指明这个函数把左边的东西(任何整数x)转变成右边的东西(x+1) ,所以这个函数可以把任意整数x映射为 x+1

//函数值是对象,所以如果你愿意,可以将其存入变量,当然也是可以使用括号的写法对其进行调用

scala> var increase = (x:Int) => x+1
increase: Int => Int = <function1>

scala> increase(10)
res0: Int = 11



/*
如果你想让函数字面量包含多条语句,可以用花括号包住函数体,一行放一条语句,这样就组成了代码块,
与方法一样,当函数值被调用时,所有的语句将被执行,而函数的返回值就是最后一行表达式产生的值
*/
increase = (x: Int) =>{
  println("We")
  println("are")
  println("here")
  x + 1
}

//执行结果
increase(10)
We
are
here
rest4: Int = 11


//将函数作为字面量传递给函数的参数
val someNumbers = List(-11, -10, -5, 0, 5, 10)
someNumber.foreach((x: Int) => println(x) )


```


# 4.函数字面量的短格式
```
//去除参数类型
someNumber.foreach( (x) => println(x) )   //因为x的类型在someNumber中已经确定了,所有可以知道x的类型,所以就没有必要指定x的类型

//更加简洁的做法
someNumber.foreach( x => println(x) )

```


# 5.占位符语法
```
/*可以把下划线当做是一个或多个参数的占位符,这样让函数字面量更简洁,只要每个参数在函数字面量内仅出现一次*/
someNumber.filter(x => x>0)
//简写为:
someNumber.filter(_ > 0)    //即_ 相当于 x=>x


//有时你把下划线当做参数的占位符,编译器可能无法推断缺失的参数类型,例如,假设你只是写:
scala> val f = _ + _

<console>:7: error: missing parameter type for expanded function ((x$1, x$2) =>
x$1.$plus(x$2))
       val f = _ + _
               ^

//上述情况下你可以使用冒号指定类型,如下:
val f = (_: Int) + (_: Int)
f(5,10)
/*
请注意_+_将扩展成带两个参数的函数字面量,这样也解释了为何仅当每个参数在函数字面量中最多出现一次时,你才能使用这种短格式,多个下划线指代多个参数,而不是单个参数的重复使用,第一个下划线代表第一个参数,第二个下划线代表第二个参数...
*/


```


# 6.部分应用的函数
```
/*
尽管前面的例子里下划线替代的只是单个参数,你还可以使用单个下划线替换整个参数列表,例如:
写成println(_) ,或者更好的方法你还可以写成println_ 
*/
someNumber.foreach(println _)
//等价于
someNumber.foreach( x => println(x) )
/*
在上述例子中,下划线不是单个参数的占位符,他是整个参数列表的占位符,请记住要在函数名和下划线之间留一个空格,因为不这样做编译器会认为你是在说明一个不同的符号(将println_当做一个整体)
*/

/*
像上面的方式使用下划线,你就正在写一个部分应用函数,部分应用函数是一种表达式,你不需要提供函数需要的所有参数,代之以仅提供部分,或不提供所需参数,比如要创建调用sum的部分应用表达式,而不提供任何3个所需参数,只要在sum之后放一个下划线即可,然后可以把得到的函数存入变量,如下:
*/
def sum(a: Int, b:Int, c: Int) = a + b +c
val a = sum _    //实例化一个带3个缺失整数参数的函数值,并把这个新的函数值的索引赋给变量a,那么就可以使用a了
a(1, 2, 3)

/*
上述的过程如下:
名为a的变量指向一个函数值对象,这个函数值是由scala编译器依照部分应用函数表达式sum _ ,自动产生的类的一个实例,编译器产生的类有一个apply方法带3个参数,之所以带3个参数是因为sum _ 表达式缺少的参数数量为3,scala编译器把表达式a(1,2,3)翻译成对函数值的apply方法的调用,传入3个参数1,2,3,因此a(1,2,3)是下列代码的短格式:
a.apply(1,2,3)
*/

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/8/1.png)


 _形式的偏函数

```
/*
在sum _ 的例子里,他没有应用于任何参数,不过还可以通过提供某些但不是全部需要的参数表达一个偏函数,如下:
*/
val b = sum(1, _: Int, 3)
//调用
b(2)        //b.apply调用了sum(1,2,3)
/*
scala的编译器会产生一个新的函数类,其apply方法带有一个参数,在使用一个参数调用的时候,这个产生的函数的apply方法调用sum,传入(1, 参数, 3)
*/
b(5)    //b.apply调用了sum(1, 5, 3)




//如果可以省略所有的参数,则可以写成println _ 或则sum _ 这样的形式, 更进一步,如果在代码的那个地方正需要一个函数,你可以去掉下划线从而表达得更简明
someNumbers.foreach(println _)
//简写
someNumbers.foreach(println)

/*
最后一种格式仅在需要写函数的地方,如例子中的foreach调用,才能使用,编译器知道这种情况下需要一个函数,因为foreach需要一个函数作为参数传入,在不需要函数的情况下,尝试使用这种格式将引发一个编译错误:
*/

scala> def sum(a:Int, b:Int, c:Int) = a+b+c
sum: (a: Int, b: Int, c: Int)Int

scala> val c = sum        //编译错误
<console>:8: error: missing arguments for method sum;
follow this method with `_' if you want to treat it as a partially applied funct
ion
       val c = sum
               ^

scala> val c = sum _        //正确
c: (Int, Int, Int) => Int = <function3>

```


# 7.闭包
```
(x: Int) => x+more
/*
自由变量:more,因为函数字面量没有给出其含义
绑定变量:x,被定义为函数的唯一参数是Int
*/
/*
如果你尝试独立使用这个函数字面量,范围内没有任何more的定义,编译器会报错说:
*/

scala> (x:Int) => x+more
<console>:8: error: not found: value more
              (x:Int) => x+more
                           ^

//所以首先要对more进行定义

scala> var more = 1
more: Int = 1

scala> val addMore = (x:Int) => x+more
addMore: Int => Int = <function1>


/*
不带自由变量的函数字面量,如:(x:Int)=>x+1被称为封闭项
同理:(x:Int)=>x+more都是开放项
任何以(x:Int)=>x+more为模板在运行期创建的函数值将必须捕获对自由变量more的绑定,因此得到的函数值将包含指向捕获的more变量的索引,又由于函数值是关闭这个开放项(x:Int)=>x+more的行动的最终产物,因此被称之为闭包
*/


scala> var more = 1
more: Int = 1

scala> val addMore = (x:Int) => x+more
addMore: Int => Int = <function1>

scala> addMore(10)
res1: Int = 11

scala> more = 999
more: Int = 999

scala> addMore(10)
res2: Int = 1009
/*
直觉上闭包捕获的是变量本身 ,而不是变量指向的值,所以自由变量的改变,在闭包内可以看到,
即:在(x:Int)=>x+more 中是可以看到外面对于more的改变的
同理,如果在闭包内部将自由变量more改变,那么在闭包的外部也是可以看到的,如下:
*/

scala> val someNumbers = List(-11, -5, -10, 0, 10)
someNumbers: List[Int] = List(-11, -5, -10, 0, 10)                                      ^

scala> var sum = 0
sum: Int = 0

scala> someNumbers.foreach(sum += _)

scala> sum
res5: Int = -16

/*
上述例子中变量sum处于函数字面量sum+=_的外围,函数字面量把数累加到sum上,尽管这是一个在运行期改变sum的闭包,作为结果的累加值,-16,仍然在闭包之外可见
*/

/*闭包使用了某个函数的本地变量*/

scala> def makeIncreaser(more:Int) = (x:Int)=>x+more
makeIncreaser: (more: Int)Int => Int

scala> val inc1 = makeIncreaser(1)
inc1: Int => Int = <function1>

scala> val inc999 = makeIncreaser(999)
inc999: Int => Int = <function1>

scala> inc1(10)
res6: Int = 11

scala> inc999(10)
res7: Int = 1009
/*
因为闭包是依赖的是函数的本地变量,所以即使对函数本地变量传递不同的值,也是得到不同的闭包函数,而不是改变了原有创建的闭包
*/

```


# 8.重复参数
```
/*
在scala中你可以指明函数的最后一个参数是重复的,从而允许客户向函数传入可变长度参数列表,想要标注一个重复参数,可以在参数的类型之后放一个星号
*/


scala> def echo(args:String*) = for(arg<-args) println(arg)
echo: (args: String*)Unit

scala> echo()

scala> echo("one")
one

scala> echo("one","two","three")
one
two
three


/*
函数内部,重复参数的类型是声明参数类型的数组,如:echo函数里被声明为类型"String"的args的类型实际上是Array[String],然而,如果你有一个合适类型的数组,并尝试把它当做重复参数传入,你会得到一个编译器错误:
*/

scala> val arr = Array("what","is","up")
arr: Array[String] = Array(what, is, up)

scala> echo(arr)
<console>:10: error: type mismatch;
 found   : Array[String]
 required: String
              echo(arr)
                   ^


//要实现上诉做法,需要在数组参数后添加一个冒号和一个_*符号,如下:

scala> echo(arr:_*)    //这个标注告诉编译器把arr的每个元素当做参数,而不是当做单一的参数传给echo
what
is
up


```


# 9.尾递归
```
def approximate(guess: Double): Double ={
  if (isGoodEnough(guess)) guess
  else approximate(improve(guess))
}

def isGoodEnough(guess: Double) ={
  true    //假设实现
}
def improve(guess: Double)={
  1.0 //假设实现
}
/*
这样的函数,带合适的isGoodEnough和improve的实现,经常在查找问题中,
在上面approximate的例子中,scala编译器可以应用一个重要的优化,注意递归调用时approximate函数体执行的最后一件事,像approximate这样,在他们最后一个动作调用自己的函数,被称为尾递归(tail recursive)
*/
```
 尾递归函数的追踪
 
```
/*
尾递归函数将不会为每个调用制造新的堆栈结构,所有的调用将在一个结构内执行,
*/

scala> def boom(x:Int):Int=
   if(x==0) throw new Exception("boom Exception") 
   else   boom(x-1)+1

/*
上述函数不是尾递归,因为在递归调用之后执行了递增操作,如果执行他,会得到如下的结果:
*/

scala> boom(3)
java.lang.Exception: boom Exception
        at .boom(<console>:7)
        at .boom(<console>:7)
        at .boom(<console>:7)
        at .boom(<console>:7)
....

//现在修改boom从而让他变成尾递归
scala> def bang(x:Int):Int=
   if(x==0) throw new Exception("bang Exception") 
   else   bang(x-1)

//执行结果如下:

scala> bang(5)
java.lang.Exception: bang Exception
        at .bang(<console>:9)
        at .<init>(<console>:9)
......
/*
上述打印结果中只是看到了bang的一个堆栈结构,
*/
```
 尾递归的局限
 
```
/*
在scala里尾递归的使用局限很大,因为JVM指令集使实现更加先进的尾递归形式变得很困难
*/
//如果递归是间接的,就像在下面的例子里两个相互递归的函数,就没有优化的可能性了
def isEven(x: Int):Boolean=
  if (x==0) true else isOdd(x-1)
def isOdd(x: Int): Boolean=
  if (x==0) false else isEven(x-1)

//如果最后一个调用是一个函数值,你也不能获得优化
val funValue = nestedFun _
def nestedFun(x: Int): Unit ={
  if (x!=0){
    println(x)
    funValue(x-1)
  }
}

```
