---
title: 第十章 组合与继承
categories: scala   
tags: [scala,scala编程]
---


&emsp;组合是指一个类持有另一个的引用,借助被引用的类完成任务,继承是超类/子类的关系

<!--more-->

# 1.二维布局库
```
/*
如下:Element是二维布局中的元素,该元素可以通过名为"elem"的工厂方法来通过传入的数据构造新的元素
*/
elem(s: String): Element

//如上,你可以对名为Element的类型建模,可以对元素调用above或beside方法,并传入第二个元素来获取一个合并了前两个元素的新的元素
val column1 = elem("hello") above elem("****")
val column2 = elem("****") above elem("world")

//该表达式的显示结果为:
hello ****
**** world


```


# 2.抽象类
```
abstract class Element{
  def contents: Array[String]
}
/*
contents被声明为没有实现的方法,他是Element类的抽象成员,具有抽象成员的类本省必须被声明为抽象的,要声明一个类是抽象的,那么只要在类的class之前加上abstract关键字即可
抽象类是不能实例化的

注意:Element类的contents方法并没带abstract修饰符,一个方法只要没有实现(即没有等号或方法体),他就是抽象的
*/

```

# 3.定义无参数方法
```
/*
接下来,我们向Element添加显示宽度和高度的方法,height方法返回contents里的行数,width方法返回第一行的长度,如果元素没有行则返回零
*/
abstract class Element{
  def contents: Array[String]
  def height: Int = contents.length
  def width: Int = if (height==0) 0 else contents(0).length
}

/*
注意:Element的三个方法都没有参数列表,甚至连空列表都没有,例如:不同于方法:
*/
def width():Int
//以下的方法定义中不含括号:
def width: Int

/*
scala的使用惯例是:只要方法中没有参数并且方法仅能通过读取所包含对象的属性方法区访问可变状态,就使用无参数方法,这个惯例支持统一访问原则,就是说客户端代码不应由属性是通过字段实现还是方法实现而受到影响,以下是将width和height作为字段而不是方法来实现,只要简单的将def修改成val,即可:
*/
abstract class Element{
  def contents: Array[String]
  val height: Int = contents.length
  val width: Int = if (height==0) 0 else contents(0).length
}

/*
到底是使用字段还是方法,这取决于类的使用情况
访问字段比调用方法略快,因为字段值在类初始化的时候被预计算,而方法调用在每次调用的时候都要计算
使用字段需要为每个Element对象分配更多的内存空间
*/

/*
特别地,如果类的字段变为了访问方法,只要访问方法是纯的,即没有副作用且不依赖可变状态,那么Element类的客户端代码就不需要重写,客户端代码不应该关心到底用哪种方式实现的
*/

//统一访问原则:
str.length    //可以说是调用了str的对象的一个字段(属性)
str.length    //也可以说是调用了str对象的无参数方法


/*
scala在遇到混合了无参数和空括号方法的情况时很自由,特别是,你可以用空括号方法重写无参数方法,反之亦然,你还可以在调用任何不带参数的方法时省略空的括号,如下:
*/
Array(2, 3, 4).toString
"abc".length


/*
原则上,scala的函数调用中可以省略所有的空括号,然而,在调用的方法超出其调用者对象的属性时(即:不能看做是调用者的属性的空参数函数),推荐仍然写一对空的括号,例如:如果方法执行了I/O,或写入了可重新赋值的变量(var),或读取不是调用者字段你的var,总之无论是直接还是非直接的使用可变对象,都应该添加空括号
*/
"hello".length    //没有副作用,所以无需()
println()    //最好别省略()    , 因为他的副作用就是打印

//如果你调用的函数执行了操作就使用括号,但如果仅提供了对某个属性的访问,那么省略括号

```


# 4.扩展类
```
/*
上面创建的抽象类,我们不能实例化,所以需要创建一个新的类,继承了上面的抽象类
*/
class ArrayElement(conts: Array[String]) extends Element {
  def contents = conts
}
/*
extends子句有两个效果,使得ArrayElement类继承Element类的所有非私有的成员,并且让ArrayElement类型成为Element类型的子类型,因为ArrayElement扩展了Element,所以ArrayElement类被称为Element类的子类,反过来,Element是ArrayElement的超类
如果你省略extends的子句,scala编译器将隐式的假设你的类扩展子scala.AnyRef,这与Java平台上的Java.lang.Object 相同
 */
```


![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/10/1.png)

```
/*
哪些超类的成员不会被继承:
1.超类的私有成员不会被子类继承,
2.超类中的成员若与子类中实现的成员具有相同的名称和参数则不会被子类继承,这种情况下被称为子类的成员重写了超类的成员,
*/


/*
如果子类的成员是具体的而超类的中是抽象的,我们可以具体的成员实现了抽象的成员
*/
val ae = new ArrayElement(Array("hello", "world"))
ae.width  //使用


//子类型化:指子类的值可以在任何需要其超类的值的地方使用
val e: Element = new ArrayElement(Array("hello", "world"))
//其实类似于Java中的多态

```

# 5.重写方法和字段
```
/*
统一访问原则只是scala在对待字段和方法上比Java更统一的一个方面,另一个差异是scala里的字段和方法属于相同的命名空间,这让字段可以重写无参数方法,例如,你可以通过改变ArrayElement类中的contents的实现将其从一个方法变成一个字段,而无需修改类Element中的contents的抽象方法定义,如下:
*/
class ArrayElement(conts: Array[String]) extends Element {
  val contents:Array[String] = conts
}

//在scala里禁止在同一个类里用同样的名称定义字段和方法,尽管Java中可以允许你这样做,但是scala中将不能编译通过


class WontCompile(conts: Array[String]) extends Element {
  private var f = 0    //不能编译通过,因为字段和方法同名
  def f = 1
}


/*
命名空间:
Java准备了四个命名空间(分别是字段/方法/类型和包)
scala仅有两个命名空间:
值(字段,方法,包还有单例对象)
类型(类和特质)

scala把字段和方法放进同一个命名空间的理由很明确,因为这样你就可以实现使用val重写无参数方法这种你在Java里做不到的事情
*/

```

# 6.定义参数化字段
```

class ArrayElement(conts: Array[String]) extends Element {
  def contents = conts
}
/*
在上述代码中需要定义字段contents时需要将conts再重新赋值给contents,这样的做法有点不必要的累赘和重复,可以采用如下的定义:
*/
class ArrayElement(val contents: Array[String]) extends Element  //注意contents参数的前缀是val,ArrayElement类现在拥有了一个可以从类外部访问的字段contents,字段使用参数值初始化
//与下面的写法类似
class ArrayElement(x123: Array[String]) extends Element{
  val contents: Array[String] = x123
}

/*
类的参数同样也可以使用var做前缀,这种情况下相应的字段可以被重新赋值,这些参数化字段还可以添加如:private,protected,或override这类的修饰符
*/
class Tiger (
  override val dangerous:Boolean,
  private var age:Int
)extends Cat

/*
Tiger的定义是下面另一种类定义方式的简写,其中包含了重写成员dangerous和私有成员age
*/
class Tiger2(param1: Boolean, param2: Int) extends Cat{
  override val dangerous = param1
  private var age = param2
}

```



# 7.调用超类构造器
```
//需求:现在想要创造由给定的单行字符串构成的布局元素
class LineElement(s: String) extends ArrayElement(Array(s)){
  override def width = s.length
  override def height = 1   //因为是单行,所以设置为1
}

```

 
![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/10/2.png)


# 8.使用override修饰符
```
/*
若子类成员重写了父类的具体成员则必须带有这个修饰符,
若成员实现的是同名的抽象成员时,则这个修饰符是可选的,
若成员并未重写或实现什么其他基类里的成员则禁用这个修饰符
*/


```

# 9.多态
```
/*
在上面的例子中可以看到Element类型的变量可以指向ArrayElement类型的对象,这种现象叫做多态,Element对象可以有许多形式,目前为止,你已经看到了两种形式:ArrayElement,和LineElement,你可以定义新的Element子类创造Element的更多形式,例如:下面给出了如何定义拥有给定长度和高度并充满指定字符的新的Element
*/
class UniformElement(
  ch:Char,
  override val width: Int, 
  override val height: Int
) extends Element{
  private val line = ch.toString * width
  def contents = Array.make(height, line)
}
```


 

![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/10/3.png)





# 10.定义final成员
```
/*有时你想要确保一个成员不被子类重写,在scala中可以和Java一样通过给成员添加final修饰符来实现
*/
//如果不想子类重写父类的方法,可以在方法上添加final
class ArrayElement extends Element{
  final override def demo(): Unit ={
    println("ArrayElement's implementation invoked")
  }
}
//这样如果ArrayElement的子类如果想要重写demo方法,是无法编译通过的

//如果想要确保整个类都不会有子类,那么需要在类的声明上添加final
final class ArrayElement extends Element{
  final override def demo(): Unit ={
    println("ArrayElement's implementation invoked")
  }
}

//class LineElement extends ArrayElement  将编译不通过

```


# 11.使用组合与继承
```
/*
组合和继承是利用其它现存类定义新类的两个方法,
如果你追求的是根本上的代码重用,那么通常更推荐采用组合而不是继承,
只有继承才受累于脆基类问题,因为你可能在改造超类时无意中破坏了子类
*/

class LineElement(s: String) extends Element{
  val contents = Array(s)
  override def width = s.length
  override def height = 1
}
/*
在前一个版本中,LineElement与ArrayElement间是继承关系,LineElement从ArrayElement那里继承了contents,现在他与Array是组合关系,在它自己的contents字段中持有字符串数组的引用
*/

```

# 12.实现above,beside,和toString
```
//above是将两个Element上下连接在一起
def above(that: Element): Element = {
  new ArrayElement(this.contents ++ that.contents) // ++操作符是连接两个数组,
}

//beside将创造出一个新的元素,新元素的每一行都来自两个原始元素的相应行的串联,为简单起见,我们首先假设两个元素高度相同,这样就可以如下设计beside方法:
def beside(that: Element):Element ={
  val contents = new Array[String](this.contents.length)
  for (i <- 0 until this.contents.length)
    contents(i) = this.contents(i) + that.contents(i)
  new ArrayElement(contents)
}

//上面的代码是指令式的,如下的采用的是函数式

new ArrayElement(
  for(
    (line1, line2) <- this.contents zip that.contents
  ) yield line1+line2
)
//上述代码避免了显示的数组索引(使用索引可能有角标越界的情况出现),因此出错的机会更少

//toString方法
override def toString: String = contents.mkString("\n")
//注意:toString没有带空参数列表,这遵循了统一访问原则的建议,因为toString是一个不带任何参数的纯方法

```

# 13.定义工厂对象
```
/*
工厂对象包含了构建其他对象的方法,客户端将使用这些工厂方法构造对象而不是直接使用new构造对象,这种方式的一个好处就是可以将对象的创建集中化并且隐藏对象实际代表的类的细节
这种隐藏一方面可以让客户端更容易理解你的库,因为暴露的细节更少了,另一方面,你更多的机会可以在不破坏客户端代码的前提下改变库的实现
*/

//工厂方法应该放在何处?
//一种直接的方法是创建Element类的伴生对象并把它作为布局元素的工厂方法,使用这种方式,你唯一要暴露给客户端的就是Element的类/对象的组合,隐藏ArrayElement,LineElement,和UniformElement三个类的实现

object Element{
  def elem(contents:Array[String]):Element =
    new ArrayElement(contents)
  def elem(chr:Char, width: Int, height: Int ): Element =
    new UniformElement(chr,width,height)
  def elem(line: String): Element =
    new LineElement(line)
}

/*
为了直接调用工厂方法而不必使用单例对象的名臣Element做限定,我们将在源文件的开始引用Element.elem,那么在Element类的内部,只要使用elem就可以调用工厂方法
*/

import Element.elem
abstract class Element{
  def contents: Array[String]
  def height: Int = contents.length
  def width: Int = if (height==0) 0 else contents(0).length
  def above(that: Element): Element = {
    elem(this.contents ++ that.contents)
  }
  def beside(that: Element):Element ={
    elem(
      for(
        (line1, line2) <- this.contents zip that.contents
      ) yield line1+line2      
    )
  }
  override def toString: String = contents.mkString("\n")
}


/*
有了上述的工厂方法之后,子类ArrayElement,LineElement,和UniformElement就可以是私有的,因为他们不再需要直接被客户访问,在scala中,你可以在类和单例对象的内部定义其他的类和单例对象,因此一种让Element的子类私有化的方式就是把他们放在Element单例对象中并在那里声明他们为私有的,如下:
*/

object Element{
  private class ArrayElement(x123: Array[String]) extends Element{
    val contents: Array[String] = x123
  }
  private class LineElement(s: String) extends Element{
    val contents = Array(s)
    override def width = s.length
    override def height = 1
  }
  private class UniformElement(
    ch:Char,
    override val width: Int,
    override val height: Int
  ) extends Element{
    private val line = ch.toString * width
    def contents = Array.make(height, line)
  }

  def elem(contents:Array[String]):Element =
    new ArrayElement(contents)
  def elem(chr:Char, width: Int, height: Int ): Element =
    new UniformElement(chr,width,height)
  def elem(line: String): Element =
    new LineElement(line)
}

```



