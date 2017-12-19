---
title: 第二章 在scala中使用函数式编程
categories: scala   
toc: true  
tags: [scala,scala函数式编程]
---
# 1.scala语言介绍:一个例子
```
object MyModule{//声明一个单例对象,即:同时声明一个类和他的唯一实例
  def abs(n: Int): Int =
    if (n<0) -n
    else n
  
  private def formatAbs(x: Int) = {
    val msg = "The absolute value of %d is %d"  //%d表示数字
    msg.format(x, abs(x))
  }

  def main(args: Array[String]): Unit = {
    println(formatAbs(-42))
  }
}

```

> object关键字

&emsp;object关键字创建一个新的单例类型,就像一个class只有一个被命名的实例,如果你熟悉Java,在Scala中声明一个object有些像创建了一个匿名类的实例,scala没有等同于Java中static关键字的概念,object在scala中经常用到,就像你在Java中用一个带有静态成员的class一样

> abs方法

&emsp;abs方法是一个接收整型类型参数并返回其绝对值的纯函数
```
  def abs(n: Int): Int =
    if (n<0) -n
    else n

```

&emsp;在def关键字之后紧跟着是方法名,然后是小括号,里面是方法参数,在这个例子中,abs方法只有一个Int类型的单个参数,在参数列表闭合括号之后是一个可选的类型(:Int) , 他表示返回值是Int类型(前面的冒号表示存在某种类型)
&emsp;在等号(=)之后的部分称之为方法体,有时我们将等号前面的部分称之为"左右边的"或"签名",等号后面的部分称之为"右手边的"或"定义",注意一般不使用return关键字,一个方法的返回值就是右手边的求值结果

> formatAbs方法

```
private def formatAbs(x: Int) = {
  val msg = "The absolute value of %d is %d"  //%d表示数字
  msg.format(x, abs(x))
}
```
&emsp;这个方法定义为private,意味着无法再MyModule对象之外调用,他接收一个Int类型,返回一个String,注意返回值类型没有声明,因为scala有能力判断一个方法的返回值类型,所以我们省略了,但是为了保持良好的代码风格,建议显示的声明返回值类型
&emsp;记住,一个方法只是简单的返回他右手边的值,所以不需要return关键字,如果右手边是一个代码块,那么返回的是代码块中最后一个表达式的值,此例中返回的是msg.format(x,abs(x))的返回值



> main方法

```
def main(args: Array[String]): Unit = {
  println(formatAbs(-42))
}
```

&emsp;以main命名的方法是一个特殊方法,当程序运行时scala会查找以main命名的特定签名的方法,他接收一个字符串数组作为参数,返回值类型为Unit,main方法的返回值没有任何意义,所以他是一个特殊的Unit类型,该类型只有唯一的值,文法上写为(),一对小括号,通常返回Unit类型的方法暗示他包含副作用(因为通常的纯函数是一个输入对应有一个输出),在此例中,println方法的返回值是Unit,也正是main方法需要返回值的类型


# 2.运行程序
&emsp;运行scala程序的最简单的方式是从命令行直接调用scala编译器,先把代码放到一个名为MyModule.scala之类的文件里,然后使用scalac编程成java字节码文件
```
scalac    MyModule.scala
```

&emsp;这将生成一些以.class后缀结尾的文件,这些文件包含可运行在java虚拟机上的编译过的代码,该代码可以使用scala命令行工具来执行
```
scala    MyModule
```
&emsp;实际上scala代码并不需要先通过scalac编译,像之前写的简单程序可以直接通过命令行传递给scala解析器来运行:
```
scala    MyModule.scala
```


# 3.模块,对象和命名空间
&emsp;scala中的每一个值都可以当成一个对象,每个对象都有零个或多个成员,对象的主要目的是给成员一个命名空间,有时我们也称为模块,一个成员可以是以def地鞥一的方法,或以val或object声明的对象
&emsp;访问对象中的成员使用"."符号,也就是一个命名空间后面跟着一个点,在后面跟着成员的名字,如MyModule.abs(-42) 
&emsp;注意:即使2+1这样的表达式也是调用对象成员,这里是对象2调用其+方法成员,他是表达式2.+(1)的语法糖,把1传给对象2的+方法,scala中没有操作符的概念,+在scala中是一个方法,若方法只是包含一个参数,可以使用中缀方式来调用,即省略点和小括号,比如:MyModule.abs(-42) 可以写成为 MyModule abs -42,结果是一样的
&emsp;可以将一个对象的成员导入到当前作用域,这样就可以不受约束的使用它们了:
```
scala> import MyModule.abs
import MyModule.abs

scala> abs(-42)
res0: Int = 42

```

&emsp;也可以使用下划线来导入一个对象的所有成员(非私有)
```
scala> import MyModule._

```



# 4.高阶函数:把函数传给函数
&emsp;你需要了解的第一个新的概念:函数也是值,就像其他类型的值,比如整型,字符串,列表;函数也可以赋值给另一个变量,存储在一个数据结构里,像参数一样传递给另一个函数,把一个函数当做参数传递给另一个函数在纯函数式编程离很有用,他被称之为高阶函数


## 4.1.迂回做法:使用循环方式
&emsp;首先我们来写一个阶乘
```
def factorial(n: Int): Int = {
  def go(n: Int, acc: Int): Int =    //一个内部函数或一个局部定义函数,在scala中吧一个函数定义在另一个函数体内很常见,在函数式编程中,认为他跟局部整数或局部的字符串没有什么不同
    if (n<=0) acc
    else go(n-1, n*acc)
  
  go(n, 1)
}
```
&emsp;scala会检测到上述的尾递归现象,只要递归发生在尾部,编译器优化成类似while循环的字节码,这种尾递归在每次迭代时不消耗栈帧的调用消耗(栈调用再输入很大的情况下可能会StackOverflowError)

> scala中的尾递归

&emsp;我们所说的尾递归是指调用者在一个递归调用后不做其他的事情,只是返回这个调用结果,比如之前的递归调用go(n-1, n*acc) ,他是一个尾递归,因为他没有做其他事情,直接返回了这个递归调用的结果,另一种情况是:1+go(n-1, acc*n) ,这里的go不是尾递归,因为这个方法的结果还要参与其他的运算(即结果还要再与1相加)
&emsp;如果递归调用时一个函数的尾递归,scala会自动编译为循环迭代,这样不会每次都进行栈的操作,默认情况下,scala不会告诉你尾递归是否消除成功,可以通过tailrec注释来告诉编译器,如果编译器不会消除尾部调用会给出编译错误,语法如下:
```
def factorial(n: Int): Int = {
  @annotation.tailrec
  def go(n: Int, acc: Int): Int =
    if (n<=0) acc
    else go(n-1, n*acc)

  go(n, 1)
}

```


## 4.2.第一个高阶函数
&emsp;下面是引入了阶乘函数
```
object MyModule{//声明一个单例对象,即:同时声明一个类和他的唯一实例
 //..省略这里的abs和factorial定义
  private def formatAbs(x: Int) = {
    val msg = "The absolute value of %d is %d"  //%d表示数字
    msg.format(x, abs(x))
  }

  private def formatFactorial(x: Int) = {
    val msg = "The factorial of %d is %d"  //%d表示数字
    msg.format(x, factorial(x)
  }

  def main(args: Array[String]): Unit = {
    println(formatAbs(-42))
   println(formatFactorial(7))
  }
}
```
&emsp;formatAbs和formatFactorial这两个函数几乎是相同的,可以将他们泛化为一个formatResult函数,他接收一个函数参数
```
def formatResult(name: String, n: Int, f: Int=>Int): Unit ={
  val msg = "The %s of %d is %d."
  msg.format(name, n, f(n))
}
```
&emsp;formatResult是一个高阶函数,他接收一个函数f作为参数,我们给f参数声明一个类型,就像其他参数那样,他的类型是Int=>Int,表示f接收一个整数参数并返回一个整型结果

> 变量命名约定

&emsp;对于高阶函数,以f,g或h来命名是一种习惯做法,在函数式编程中,我们倾向于使用短的变量名,甚至单个字母命名,因为高阶函数的参数通常没法表示参数到底执行什么,无法体现他们的含义(所以就没有必要体现),许多函数式程序员觉得短名称让代码更易读,因为代码结构第一眼看上去更简单


# 5.多态函数:基于类型的抽象
&emsp;目前我们定义的函数都是单态的:函数只操作一种数据类型,比如abs和factorial的指定参数类型是Int,高阶函数formatResult也是固定的操作Int=>Int类型的参数,通常,特别是在写高阶函数时,希望写出的这段代码能够使用于任何类型,他们被称为"多态",这儿的多态与面向对象中的多态稍有差别,面向对象的多态通常意味着某种形式的子类型或继承类型,这个例子中没有接口或子类型,这里所用的多态形式有时也称为参数化多态

## 5.1.一个多态函数的例子
&emsp;下面的单态函数findFirst返回数组里第一个匹配到key的索引,或在匹配不到的情况下返回-1,如下是从一个字符串数组中查找一个字符串的特例
```
def findFirst(ss: Array[String], key: String): Int = {
  @annotation.tailrec
  def loop(n: Int): Int =
    if (n>=ss.length) -1  //如果n到了数组的结尾,返回-1,表示这个key在数组里不存在
    else if(ss(n)==key) n //ss(n)抽取数组ss里的第n个元素,如果第n个元素等于key返回n,表示这个元素出现在数组的索引
    else loop(n+1)
  
  loop(0) //从数组的第一个元素开始启动loop
}
```
&emsp;上诉的代码不是我们关注的细节,重要的是不管是从Array[String]中查找一个String,还是从Array[Int]中查找一个Int,或从任何Array[A]中查找一个A,他们看起来几乎都是相同的,我们可以写一个更泛化的适用任何类型A的findFirst函数,他接收一个函数参数,用来对A进行判定
```
def findFirst[A](as: Array[A], p: A=>Boolean): Int = {
  @annotation.tailrec
  def loop(n: Int): Int =
    if (n>=as.length) -1  
    else if(p(as(n))) n 
    else loop(n+1)

  loop(0) 
}

```
&emsp;在参数列表中引入的类型变量,可以在其他类型签名中引用(类似于参数列表中的参数变量可在函数体中引用),在findFirst函数中类型变量A被两个地方引用:一处是数组元素要求是类型A(声明为Array[A]) , 另一处是函数p必须接受类型A(声明为A=>Boolean),这两处类型签名中引用相同的类型变量,意味着他们的类型必须相同,当我们调用findFirst时编译器会强制检测,如果在Array[Int]中查找一个String,可能会造成类型匹配错误


## 5.2.对高阶函数传入匿名函数
&emsp;在使用高阶函数时,不必非要提供一些有名函数,可以传入匿名函数或函数字面量,如下测试findFirst函数
```
scala> findFirst(Array(7,9,13), (x:Int)=>x==9)
res1: Int = 1

```
&emsp;表达式Array(7,9,13)是一段"数组字面量",他用3个整数构造一个数组,注意构造数组时并没有使用new关键字
&emsp;语法(x:Int)=>x==9 是一段"函数字面量'或"匿名函数",不必先定义一个有名称的方法,可以利用语法的便利,在调用的时候再定义
&emsp;通常函数的参数声明在=>箭头的左边,可以在箭头右边的函数体内使用它们,比如写一个比较两个整数是否相等的函数
```
scala> (x:Int, y:Int) => x==y
res2: (Int, Int) => Boolean = <function2>

```
&emsp;在REPL结果中的<function2>符号表示res2接收2个参数的函数,如果scala可以从上下文推断输入参数的类型,函数参数可以省略掉类型符号,例如:(x,y)=>x<y

> 在scala中函数也是值

&emsp;当我们定义一个函数字面量的时候,实际上定义了一个包含一个apply方法的scala对象,scala对这个方法名有特别的规则,一个有apply方法的对象可以把他当成方法一样调用,我们定义函数字面量(a,b)=>a<b,他其实是一段创建函数对象的语法糖
```
val lessThan = new Function2[Int, Int, Boolean]{
    def apply(a:Int , b:Int) = a<b
}
```
&emsp;lessThan的类型是Function2[Int, Int, Boolean] ,通常写成(Int, Int)=>Boolean ,注意Function2接口(在scala中是trait)包含一个apply方法,当我们以lessThan(10,20)的方式调用函数lessThan时他实际是对apply方法调用的语法糖
&emsp;Function2只是一个有scala标准库提供的普通的特质(接口),代表接收两个参数的函数对象,同样在标准库里还提供了Function1,Function3等其他函数对象,接收的参数个数从名称里能看出来


# 6.通过类型来实现多态
&emsp;函数partial1接收一个值和一个带有两个参数的函数,并返回一个带有一个参数的函数,如下:
```
def partial1[A, B, C](a: A, f: (A,B)=>C): B=>C

```
&emsp;函数partial1有三个类型参数:A,B和C,他带有两个参数,参数f本省是一个有两个类型分别为A和B的参数,返回值为C的函数,函数partial1的返回值也是一个函数,类型为B=>C
&emsp;看一下返回的类型,partial1返回值类型是B=>C,可以写一个接受B参数类型的函数字面量:
```

def partial1[A, B, C](a: A, f: (A,B)=>C): B=>C = 
    (b: B) => ???

```
&emsp;如果你是第一次写匿名函数可能觉得很怪异,B是从哪儿来的?其实我们只是写了一个"返回一个函数,这个函数接收一个类型为B的参数值b",在右箭头(=>)的右手边(使用问号的地方)跟着一个匿名函数的方法体,匿名函数方法体中可以引用值b,同样可以引用partial1方法体中的a
```

def partial1[A, B, C](a: A, f: (A,B)=>C): B=>C    =
    (b: B) => f(a,b)

```
