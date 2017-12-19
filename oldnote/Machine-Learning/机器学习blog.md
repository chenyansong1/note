---
title: 第十九章 类型参数化
categories: scala   
tags: [scala,scala编程]
---


# 1.queue函数式队列
```
//函数式队列是一种具有以下三种操作方式的数据结构
head     //返回队列的第一个元素
tail    //返回除第一个元素之外的队列
append    //返回尾部添加了指定元素的新队列

//不像可变队列,函数式队列在添加元素的时候不会改变其内容,而是返回包含了这个元素的新队列
scala> import scala.collection.immutable.Queue
scala> val q = Queue(1,2,3)
scala> val q1 = q append 4



```
<!--more-->

# 2.信息隐藏
 私有构造器及工厂方法
```
/*
Java中,你可以把构造器声明为私有的使其不可见,scala中,主构造器无需明确定义,不过虽然他的定义隐含于类参数及方法体中,还是可以通过private修饰符添加在类参数列表的前边把主构造器隐藏起来
*/
class Queue[T] private (
   private val leading: List[T],
   private val trailing: List[T]
 )

//夹在类名与其参数之间的private修饰符表名Queue的构造器是私有的,他只能被类本身及伴生对象访问,类名Queue仍然是公开的,因此你可以继续使用这个类,但不能调用他的构造器
scala> new Queue(List(1,2), List(3))
<console>:24: error: constructor Queue in class Queue cannot be accessed in object $iw
              new Queue(List(1,2), List(3))
              ^

//上面的代码的主构造器不能调用,那么可以使用辅助构造器,如下:
def    this() = this(Nil, Nil)

//上面的辅助构造器可以构建空队列,通过改良,他可以带上初始化队列元素列表:
def    this(elems: T*) = this(elems.toList, Nil)    //T*是重复参数的标注


//另一种可能性是添加可以用初始元素序列创建队列的工厂方法,比较简洁的做法是定义与类同名的Queue对象及apply方法

object Queue {
  //用初始化元素"xs"构造队列
  def apply[T](xs: T*) = new Queue[T](xs.toList, Nil)
}
/*
再把这个对象放在类Queue的同一个源文件中,你就把他变成了类的伴生对象,在13,4节已经知道,伴生对象与类具有相同的访问权,据此,即使Queue类的构造器是私有的,对象Queue的apply方法也可以创建新的Queue对象
 */

/*
注意:工厂方法名为apply,因此客户可以使用类似于Queue(1,2,3) 这样的表达式创建队列,由于/Queue是对象而不是函数,这个表达式会被扩展为Queue.apply(1,2,3) , 结果,对于客户来说,Queue就好像是全局定义的工厂方法,实际上,scala没有全局可见的方法,每个方法都必须被包含在对象或类中,然而,使用定义在全局对象中的名为apply的方法,你就能够提供这种看上去好像是对全局方法调用的使用模式了
*/

```

 可选方案: 私有类
```
/*
私有构造器和私有成员是隐藏类的初始化代码和表达代码的一种方式,另一种更为彻底的方式是直接把类本身隐藏掉,仅提供能够暴露类公共接口的特质
*/
trait Queue[T] {
  def head: T
  def tail: Queue[T]
  def append(x: T): Queue[T]
}

object Queue{
  def apply[T](xs: T*): Queue[T] = new QueueImpl[T](xs.toList, Nil)

  private class QueueImpl[T](private val leading: List[T], private val trailing: List[T]) extends Queue[T]{
    def mirror =
      if (leading.isEmpty)
        new QueueImpl(trailing.reverse, Nil)
      else
        this

    def head: T = mirror.leading.head

    def tail: QueueImpl[T] = {
      val q = mirror
      new QueueImpl(q.leading.tail, q.trailing)
    }

    def append(x: T) =
      new QueueImpl(leading, x::trailing)
  }
}
//这个版本隐藏的是全体实现类


```


# 3.变化型注解
```
//在上面的代码中Queue是特质,因为他带类型参数,结果,你将不能创建类型为Queue的变量

scala> def doenNotCompile(q: Queue){}
<console>:23: error: trait Queue takes type parameters
       def doenNotCompile(q: Queue){}
                             ^

//取而代之,特质Queue能够制定参数化的类型,如:Queue[String], Queue[Int], 或Queue[AnyRef], 也就是说,Queue是特质,而Queue[String]是类型,Queue也被称为类型构造器,因为有了他你就能够通过指定参数类型构造新的类型

scala> def doenNotCompile(q: Queue[AnyRef]){}



/*
你也可以认为Queue是泛型的特质,
类型参数的组合与子类型化产生了一些有趣的问题,例如:在Queue[T] 产生的类型家族的成员之间是否有任何特定的子类型关系?更具体的说就是:是否把Queue[String] 当做是Queue[AnyRef] 的子类型?
或从更广泛的意义上来说,如果S是类型T的子类型,那么是否可以把Queue[S] 当做Queue[T]的子类型?如果是,你可以认为Queue特质是与他的类型参数T保持协变的,由于他只有一个类型参数,你可以简单的说Queue是协变的,协变的Queue将意味着,你可以把,比方说Queue[String] ,传递给之前看到过的值参数类型为Queue[AnyRef] 的doesCompile方法

直观上,这些看上去都很合理,因为String队列似乎就是AnyRef的特例,然而在scala中,泛型类型缺省的是非协变的,子类型化,也就是说,根据定义的Queue,不同元素类型的队列之间没有子类型关系,Queue[String] 对象不能被用作Queue[AnyRef], 然而,可以用如下方式改变Queue类定义的第一行,以要求队列协变的子类型化
*/
trait Queue[+T] {....}
/*
在正常的类型参数前加上+号标明这个参数的子类型是协变的,这个符号是向scala说明你希望可以把,比方说Queue[String] ,当做Queue[AnyRef] 的子类型
*/



/*
除了+号以外,还可以前缀加上-号,这标明是需要逆变的子类型化,如果Queue定义如下:
*/
trait Queue[-T] { ....}

/*
那么如果T是类型S的子类型,这将意味着Queue[S] 是Queue[T]的子类型,无论类型参数是协变的,逆变的,还是非协变的,都被称为参数的变化型,可以放在类型参数前的+号和-号被称为变化型注解
*/

class Cell[T](init: T) {
  private[this] var current = init
  def get = current
  def set(x: T) { current = x}
}

/*
代码中的cell类型被声明为非协变的,为了方便讨论,假设暂时声明为协变的--也就是说,声明为Cell[+T] , 并发给scala编译器,(实际上没有,我们会在之后解释),
*/
class Cell[+T](init: T) {
  private[this] var current = init
  def get = current
  def set(x: T) { current = x}
}
//报如下错误:
<console>:24: error: covariant type T occurs in contravariant position in type T
 of value x
         def set(x: T) { current = x}

//下面是测试代码
val c1 = new Cell[String]("abc")
val c2 = Cell[Any] = c1
c2.set(1)
val s:String = c1.get
/*
上面的四条语句从自身来看,每条都很正常,第一行创建String单元格并保存在c1中,第二行定义c2类型是Cell[Any],并初始化为c1,这没问题,因为前提设定Cell为协变的,第三行把c2的值设为1,这也没问题,因为被赋予的值1是c2的元素类型Any的实例,最后,第四行把c1的元素值赋给字符串,这没什么奇怪的,两边的值都是同样的类型,但把他们放在一起,这四行代码要完成的是把整数1赋给字符串s,这明显是对类型声明的破坏
根本的原因是:String类型的Cell并不是Any类型的Cell,因为有些事可以对Any的Cell做,但不能对String的cell做,比如:不能以Int类型的入参调用String的Cell的set方法
*/
```
 变化型和数组
```
/*
与java数组比较会很有趣,基本上,除了数组可以有超过一个元素之外,他与单元格没什么差别,然而,java中数组被认为是协变的,例如下面是用Java数组模拟了上面的单元格交互操作
*/

//在java里
String[] a1 = {"abc"}
Object[] a2 = a1
a2[0] = new Integer(17)    //执行程序会在把Integer对象赋值给a2[0]时引发异常
String s = a1[0]


//下面是使用scala的方式去翻译上面的代码

scala> val a1 = Array("abc")
a1: Array[String] = Array(abc)

scala> val a2: Array[Any] = a1
<console>:22: error: type mismatch;
 found   : Array[String]
 required: Array[Any]
Note: String <: Any, but class Array is invariant in type T.
You may wish to investigate a wildcard type such as `_ <: Any`. (SLS 3.2.10)
       val a2: Array[Any] = a1
                            ^
/*
这里的情况是scala把数组当做是非协变的,因此Array[String] 对象不能被当做与Array[Any] 一致,然而,有时需要使用对象数组作为模拟泛型数组的手段与java的遗留方法执行交互
*/

val a2: Array[Object] = a1.asInstanceOf[Array[Object]]


```



# 4.检查变化型注解
```
class StrangeIntQueue extends Queue[Int] {
  override def append(x: Int) = {
    println(Math.sqrt(x))
    super.append(x)
  }
}

//测试如下:
val x:Queue[Any] = new StrangeIntQueue
x.append("abc")

/*
第一行是有效的,因为StrangeIntQueue是Queue[Int]子类,并且假设队列是协变的,即Queue[Int]是Queue[Any]的子类型,第二行是有效的因为你可以对Queue[Any]添加String对象,然而,这两行放在一起的效果就是取字符串的平方根,毫无意义
*/



/*
只要泛型的参数类型被当做方法参数的类型,那么包含他的类或特质就有可能不能与这个类型参数一起协变,对于对垒来说,append方法违反了以下情况:
*/
class Queue[+T] {
    def append(x: T) = 
        //...
}

//编译上面的代码将报错

/*
可重新赋值的字段是" 不允许使用+号注解的类型参数用作方法参数类型" 这条规则的具体例子,如在18.2节提到过可重新赋值的字段, " var x:T"在scala里被看做一种getter方法," def x:T" 和setter方法," def x_ = (y:T) , 正如你所见,setter方法带有字段类型为T的参数,因此类型将不是协变的
*/


/*
为了核实变化型注解的正确性,scala编译器会把类或特质结构体的所有位置分类为正,负,或中立,所谓的"位置" 是指类(或特质)的结构体内可能会用到类型参数的地方,例如,任何方法的值参数都是这种位置,因为方法值参数具有类型,所以类型参数可以出现在这个位置上,编译器检查的类型参数的每一个用法,注解了+号的类型参数只能被用在正的位置上,而注解了-号的类型参数只能用在负的位置上,没有变化型注解的类型参数可以用于任何位置,因此它是唯一能被用在类结构体的中性位置上的类型参数
*/

```


# 5.下界
```
/*
回到Queue类中来,你已经看到了前面演示的Queue[T]定义中不能实现对T的协变,因为T作为参数类型出现在append方法中,而这里是负的位置
有一个办法可以打开这个结: 可以通过把append变为多态以使其泛型化,并使用他的类型参数的下界
*/

class Queue[+T](private val leading:List[T],private  val trailing:List[T]){
  def append[U>:T](x: U)
    new Queue[U](leading, x::trailing)
}

/*
新的定义指定了append的类型参数U,并通过语法"U>:T", 定义了T为U的下界,结果U必须是T的超类型,append的参数现在变为类型U而不是类型T,而方法的返回值现在也变为Queue[T] ,取代了Queue[T]

假设存在类Fruit及两个子类,Apple和Orange,通过使用Queue类的新定义,现在可以把Orange对象加入到Queue[Apple],而返回的结果为Queue[Fruit]类型

append的改进定义是类型正确的,直观的看,如果T比预期的类型更为特化(例如:用Apple替代Fruit),那么append的调用就仍然正确,因为U(Fruit)是T(Apple)的超类型

上述情况说明:变化型注解与下界可以相互协作,这是很好的类型驱动设计的例子,由接口的类型引导其细节的设计和实现
*/
```


# 6.逆变
```
trait OutputChannel[-T] {
  def write(x: T)
}

//同时存在逆变和协变
trait Function1(-S, +T){
  def apply(x: S): T
}

```


# 7.对象私有数据(略)
```


```


# 8.上界
```
class Person(val firstName: String, val lastName: String) extends Ordered[Person] {
  def compare(that: Person) = {
    val lastNameComparison = lastName.compareToIgnoreCase(that.lastName)
    if (lastNameComparison != 0)
      lastNameComparison
    else
      firstName.compareToIgnoreCase(that.firstName)
  }
  
  override def toString = firstName + " " + lastName
}

//测试
scala> val robert = new Person("Robert", "Jones")
robert: Person = Robert Jones

scala> val sally = new Person("Sally", "Smith")
sally: Person = Sally Smith

scala> robert < sally
res0: Boolean = true


def orderedMergeSort[T <: Ordered[T]](xs: List[T]):List[T] = {
  def merge(xs: List[T], ys: List[T]): List[T] = (xs, ys) match {
    case (Nil, _) => ys
    case (_, Nil) => xs
    case (x::xs1, y::ys1) =>
      if (x<y) x::merge(xs1,ys)
      else y::merge(xs,ys1)
  }

  val n = xs.length/2
  if (n == 0) xs
  else{
    val (ys, zs) = xs splitAt n
    merge(orderedMergeSort(ys), orderedMergeSort(zs))
  }


}


//测试
val people = List(
  new Person("Larry", "Wail"),
  new Person("Anders", "Hejlsberg"),
  new Person("Guide", "van Rossum"),
  new Person("Alan", "Kay"),
  new Person("Yukihiro", "matsumoto")
)
orderedMergeSort(people)

/*
尽管上述代码很好的诠释了上界的用法,但他实际上并非scala中设计能够充分利用Ordered特质的排序函数的通用方式,比方说,你不能够用OrderedMergeSort函数对整数列表做排序,因为Int类不是Ordered[Int]的子类型
*/

```








