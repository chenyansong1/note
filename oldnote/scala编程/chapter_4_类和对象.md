---
title: 第四章 类和对象
categories: scala   
tags: [scala,scala编程]
---


# 1.类 对象和方法

```
#类是对象的蓝图,一旦你定义了类,就可以用关键字new来创建对象
class CheckSumAccumulator {

}

#使用类创建对象
new CheckSumAccumulator 

```

<!--more-->

```
类定义里,可以放置字段和方法,这些被笼统的称之为成员,
字段:使用val或者是var定义,方法使用这些数据执行对象的运算工作
方法:使用def定义,包含了可执行的代码,字段保留了对象的状态或数据

当类被实例化的时候,运行时环境会预留一些内存来保留对象的状态映像------即变量的内容

class CheckSumAccumulator {
  var sum = 0
}

//实例化两次
val acc = new CheckSumAccumulator
val csa = new CheckSumAccumulator
//内存里对象的状态映像看上去大概是这样的,如下图:
```

![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/3/1.png)
 
```
由于在类CheckSumAccumulator里定义的字段sum是var,而不是val,因此之后可以重新赋给不同的Int值,如下
acc.sum = 3

```
![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/3/2.png)
 
```

需要注意的是:尽管acc对象是val,但是仍然可以修改对象中变量的值,但是我们不能修改对象的指向,
#编译不过,因为acc是val
acc = new CheckSumAccumulator

```

 private
 
```
class CheckSumAccumulator {
  private var sum = 0
}
val csa = new CheckSumAccumulator
acc.sum = 5 //编译不过,因为sum是私有的

/*
在scala里把成员公开的方法是不显示的指定任何访问修饰符,换句话说,在Java里要写上public的地方,在scala里只要什么都不要写就可以了,public是scala里默认的访问级别
*/

```

 方法的参数
 
```
class CheckSumAccumulator {
  private var sum = 0
  def add(b: Byte): Unit ={
    sum += b
  }

  def checkSum():Unit = {
    return -(sum & 0xFF) + 1
  }
}
/*
传递给方法的任何参数都可以在方法内部使用,scala里方法参数的一个重要特征是他们都是val,不是var,如果你想在方法里面给参数赋值,结果是编译失败:
  def add(b: Byte): Unit ={
   b = 1 //编译不过,因为b是val
    sum += b
  }
*/
```


checkSum方法最后的return语句是多余的可以去掉,如果没有发现任何显示的返回值,scala方法将返回方法中最后一次计算的到的结果
**方法的推荐风格是尽量避免使用返回语句,尤其是多条返回语句,代之以把每个方法当做是创建返回值的表达式,这种逻辑鼓励你分层简化方法,把较大的方法分解为_多个更小的方法_**

在这里checksum只要计算值,不用return,所以这个方法有另一种简写方式,假如某个方法仅计算单个表达式,则可以去掉花括号,如果表达式很短,甚至可以把它放在def的同一行里,这样改动之后,CheckSumAccumulator如下:

```

class CheckSumAccumulator {
  private var sum = 0
  def add(b: Byte):Unit = sum += b
  def checkSum():Int = -(sum & 0xFF) + 1
}

/*
如果去掉方法体前面的等号,那么方法的结果类型就必定是Unit,这种说法不论方法体里面包含什么都成立,因为scala编译器可以把任何类型转换为Unit,例如:如果方法的最后结果是String,但结果类型被声明为Unit,那么String将变为Unit并丢弃原值
*/

```

# 2.分号推断
```
/*
在scala里,语句末尾的分号通常是可选的,愿意可以加,若一行中仅有一个语句也可以不加,不过一行中包含多条语句时,分号则是必须的
*/
val s = "Hello"; println(s)

/*
输入跨越多行的语句时,多数情况下无需特别处理,scala将在正确的位置分割语句,如下的代码被当成跨四行的一条语句
*/
if (x<2)
  println("to small")
else
  println("ok")

/*
然而,偶尔scala也许并不如你所愿,把句子拆分成两部分
*/
x
+ y   //会被当做两个语句x 和 +y, 如果希望把它作为一个语句x + y ,可以把它放在括号里

(x
+ y)



/*
或者可以把+号放在行末,也正源于此,串接类似于+这样的中缀操作符的时候,scala通常的风格是把操作符放在行尾而不是行头
*/
x +
y +
z


```

分号推断的规则,除非以下情况的一种成立,否则行尾被认为是一个分号

* 1.疑问行由一个不能合法作为语句结尾的字结束,如句点或中缀操作符
* 2.下一行开始于不能作为语句开始的词,如下一行开始于+
* 3.行结束语括号() 或方框[] 内部,因为这些符号不可能容纳多个语句



# 3.singleton对象

**scala不能定义静态成员,取而代之的是单例对象**,除了用object关键字替换了class关键字以外,单例对象的定义看上去与类定义一致,可以将单例对象看做是类中的实例成员

```

class CheckSumAccumulator {
  private var sum = 0
  def add(b: Byte){sum += b}
  def checkSum(): Int  = -(sum & 0xFF) + 1
}


object CheckSumAccumulator{
  import scala.collection.mutable.Map
  private val cache = Map[String, Int]()
  def calculate(s: String): Int =
    if(cache.contains(s))
      cache(s)  //包含,返回
    else{
      val acc = new CheckSumAccumulator
      for(c <- s)
        acc.add(c.toByte)
      val cs = acc.checkSum()
      cache += (s->cs) //加入cache映射表,因为Map是mutable类型的,所以可以+=
      cs
    }
}


```

当单例对象与某个类共享一个名称时,他就被称为这个类的伴生对象,类和他的伴生对象必须定义在一个源文件里,类被称为是这个单例对象的伴生类,类和他的伴生对象可以互相访问其私有成员


```
/*
对于Java程序员来说,可以把单例对象当做是Java中可能会用到的静态方法工具类,也可以用类似的语法做方法调用,单例对象名,点,方法名,例如,可以调用CheckSumAccumulator单例对象的calculate方法,如下:
*/

CheckSumAccumulator.calculate("Every value is an object")

```

```
/*
单例对象不只是静态方法的工具类,他同样是头等的对象,因此单例对象的名字可以被看做是贴在对象上的"名签"
*/

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/3/3.png)


类和单例对象间的差别是:单例对象不带参数,而类可以,因为单例对象不是用new关键字实例化的,所以没机会传递给它实例化参数,每个单例对象都被实现为虚构类的实例,并指向静态的变量,因此他们与Java静态类有着相同的初始化语义,特别要指出的是,单例对象在第一次被访问的时候才会被初始化
不与伴生类共享名称的单例对象被称为独立对象,他可以用在很多地方,例如作为相关功能方法的工具类,或者定义scala应用的入口点


# 4.scala程序

想要编写能够独立运行的scala程序,就必须创建有main(仅带一个参数Array[String],且结果类型为Unit)方法的单例对象,任何拥有合适签名的main方法的单例对象都可以用来作为程序的入口点


```
object Summer{
  def main(args: Array[String]): Unit = {
    for (arg <- args){
      import CheckSumAccumulator.calculate
      //定义CheckSumAccumulator对象中calculate方法的引用,它允许你在后面的文件里使用方法的简化名
      println(arg + ": " + calculate(arg))
    }
  }
}

/*
scala的每个源文件都隐含了对包Java.lang ,包scala,以及单例对象Predef的成员引用,包scala中的Predef对象包含了许多有用的方法,例如,scala源文件中写下的println语句,实际调用的是Predef的println(Predef.println转而调用Console.println,完成真正的工作),写下assert,实际是在调用Predef.assert
*/


/*
scala和Java之间有一点不同,Java需要你把公共类放在以这个类命名的源文件中,如类SpeedRacer要放在文件SpeedRacer.java里,scala对于源文件的命名没有硬性的规定,但是在通常情况下,如果不是脚本,推荐的风格是像Java里那样按照所包含的类名来命名文件,这样程序员就可以比较容易的根据文件名找到类
*/
```

 编译scala文件
 
```
$ scalac CheckSumAccumulator.scala Summer.scala

//编译之后再运行
$scala Summer of love

```


# 5.application特质
```
import CheckSumAccumulator.calculate
object FallWinterSpringSummer extends Application{
  for (seasion <- List("fail", "winter", "apring")){
    println(seasion + ": " + calculate(seasion))
  }
}

/*
首先在单例对象后面写上"extends Application" ,然后代之以main方法,你可以把想要执行的代码直接放在单例对象的花括号之间,如此而已,之后就可以正常的编译和运行了

能这么做是因为Application声明了带有合适签名的main方法,并被你写的单例对象继承,使他可以像scala程序那样,好括号之间的代码被收集进了单例对象的主构造器

*/

```
