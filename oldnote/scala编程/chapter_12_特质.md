---
title: 第十二章 特质
categories: scala   
tags: [scala,scala编程]
---




# 1.特质是如何工作的?
```
//特质的定义除了使用关键字trait之外,与类的定义无异
trait Philosophical {
  def philosophize() {
    println("I consumne memory , therefore i am!")
  }
}
/*
这个特质名为Philosophical ,他没有声明超类,因此和类一样,有个默认的超类AnyRef
*/

//一旦特质被定义了,就可以使用extends或with关键字,把它混入类中,是"混入"特质而不是继承他们,因为特质的混入与那些其他语言中的多重继承有重要的差别
class Frog extends Philosophical {
  override def toString = "green"
}
/*
你可以使用extends关键字混入特质:这种情况下隐式地继承了特质的超类,
如:Frog类是AnyRef(Philosohpical的超类)的子类并混入了Philosohpical,从特质继承的方法可以像从超类继承的方法那样使用
*/
val frog = new Frog
frog.philosophize()

//特质同样也是类型,以下是把Philosophical用作类型的例子
val phil: Philosohpical = frog    //变量phil可以被初始化为任何混入了Philosohpical特质的类的对象

//如果想要混入多个特质,都加在with子句里就可以了
class Animal
trait HasLegs
class Frog extends Animal with Philosohpical with HasLegs {
    override def toString = "green"
}

//类Frog重写Philosohpical的philosophize方法,语法与重写超类总定义的方法一样
class Animal
class Frog extends Animal with Philosohpical {
    override def toString = "green"
    override def philosophize(){
        println("I am easy being a man")
    }
}

/*
你或许会得出以下的结论:特质就像是带有具体方法的Java接口,不过他其实能做更多的事情,例如:特质可以声明字段和维持状态值,但是特质有两点不同:
1.特质不能有任何"类"参数,即传递给类的主构造器的参数
class Point(x:Int, y:Int)
trait NoPoint(x:Int, y:Int)    //不能编译通过
2.类和特质的另一个差别在于不论在类的哪个角落,super调用都是静态绑定的,而在特质中,他们是动态绑定的,如果你在类中写下"super.toString" ,你很明确哪个方法实现将被调用,然而如果你在特质中写了同样的东西,在你定义特质的时候super调用的方法实现尚未定义,调用的实现将在每一次特质被混入到具体类的时候才被决定(根据多态的形式决定的),这种处理super的有趣的行为是使得特质能以可堆叠的改变方式工作的关键
*/
```

# 2.瘦接口对阵胖接口

略


# 3.样例:长方形对象
```
trait Rectangular {
  def topLeft: Point
  def bottomRight: Point

  def left = topLeft.x
  def right = bottomRight.x
  def width = right - left
  //...
}

abstract class Component extends Rectangular{
  //其他方法...
}

class Rectangle(val topLeft:Point, val bottomRight: Point) extends  Rectangular{
  //其他方法...
}
```


# 4.ordered特质
```
/*
当你比较两个排序对象时,如果一个方法调用就能获知精确的比较结果将非常便利,如果你想要"小于",你会调用<,如果你想要"小于等于",你会调用<=,对于瘦接口来说,你或许只有<方法,所以或许什么时候你会不得不写出类似于"(x<y)||(x==y)" 这样的东西
*/

//在第六章里,我们知道了Rational表示的是一个分数(有理数)
class Rational(n:Int, d:Int) {
  //...
  def < (that: Rational) =
    this.number * that.denom > that.number * this.denom
  def > (that:Rational) = that < this
  def <= (that:Rational) = (this<that)||(this==that)
  def >= (that:Rational) = (this>that)||(this==that)
}
/*
注意到三个比较操作符都定义在使用第一个的基础上,例如:>被定义为<的反转,<=被定义为句法上的"小于或等于" ,另外,还可以注意到所有这三个方法对于任何可比较的类来说都是一样的,所以讨论<=的时候不会有任何对于分数来说特别的东西,在比较的上下文中,<=永远表示着"小于或等于",总而言之,这个类的代码中存在着与任何其他实现了比较操作符的类一样的大量的固定格式写法
*/

//由于比较操作时如此的常见,以至于scala专门提供了一个特质解决他,这个特质就是Ordered,要使用它,你首先要用一个compare方法替换所有独立的比较方法(相当于上面的<方法),然后Ordered特质就会利用这个方法为你定义<,>,<=和>= ,Ordered特质让你可以通过仅仅实现了一个方法--compare,使你的类具有了全套的比较方法
class Rational(n: Int, d: Int) extends Ordered[Rational] {
  //...
  def compare(that: Rational) = //这个compare就是所有比较的基础,就像上面的<方法
    (this.number * that.denom) - (that.number * this.denom)
}
/*
这个版本的Rational混入了Ordered特质,不像你之前看到过的特质,Ordered需要你在混入的时候设定类型参数:type parameter,所以实际上混入的是Ordered[C] ,这里的C是你比较的元素的类,在本例中Rational混入了Ordered[Rational]
你要做的第二件事就是compare方法来比较两个对象,这个方法应该能比较方法的接受者this和当做方法参数传入的对象,如果对象相同应该返回一个整数零,否则返回正数或者是负数
*/
val hafl = new Rational(1,2)
val third = new Rational(1,3)
half < third        //false
half > third         //true

/*
混入Ordered特质,你可以实现某种排序的类,请当心,Ordered特质并没有为你定义equals方法,因为他无法做到,问题在于要通过使用compare实现equals需要检查传入对象的类型,但是因为类型擦除,Ordered本身无法做这种测试,因此,即使你继承了Ordered,也还是需要自己定义equals
*/
```


# 5.特质用来做可堆叠的改变
```
/*
需求:对一个整数队列堆叠改动,队列有两种操作,put把整数放入队列,get从尾部取出他们,队列是先进先出的
假设有一个类实现了这样的队列,你可以定义特质执行如下的改动:
Dobling: 把所有放入到队列的数字加倍
Incrementing: 把所有放入队列的数字增值
'Filtering:从队列中过滤掉负整数

以上三种特质代表了改动,因为他们改变了原始队列的行为而非定义了全新的队列类,这三种同样也是可堆叠的,你可以选择三者中的若干,把他们混入类中,并获得你所需改动的新类
*/
//下面是抽象的IntQueue类,使用了ArrayBuffer的实现队列
abstract class IntQueue{
  def get(): Int
  def put(x:Int)
}

import scala.collection.mutable.ArrayBuffer
class BasicIntQueue extends IntQueue{
  private val buf = new ArrayBuffer[Int]()
  def get() = buf.remove(0)
  def put(x: Int){buf += x}
}

//调用
val queue = new BasicIntQueue
queue.put(10)
queue.put(20)
queue.get()    //10


//下面的方法是使用特质改变行为
trait Doubling extends IntQueue{
  abstract override def put(x: Int){ super.put(2 * x) }
}
/*
上面的特质Doubling在声明为抽象的方法中有一个super调用,这种调用对于普通的类来说是非法的,因为他们在执行时必然失败,然而对于特质来说,这样的调用实际能够成功,因为特质里的super调用是动态绑定的,特质Doubling的super调用将直接被混入另一个特质或类之后
例如:使用super.put(2*x)是对超类的调用,所以具体是看超类是怎么样实现的

为了告诉编译器你的目的,比必须对这种方法打上abstract override的标志,这种标识符的组合仅在特质成员的定义中被认可,在类中则不行,他意味着特质必须被混入某个具有期待方法的具体定义的类中:因为是重写put方法,所以使用override,因为方法没有实现(因为super.put没有实现,所以说是abstract的)所以定义为abstract的
*/
class MyQueue extends BasicIntQueue with Doubling
//因为BasicIntQueue是MyQueue的超类,所以在特质Doubling中super.put就是调用BasicIntQueue的put方法
val queue = new MyQueue
queue.put(10)
queue.get()    //20

/*
注意:MyQueue没有定义一行新代码,只是简单的指明了一个类混入了一个特质,这种情况下,你甚至可以直接new 一个 " BasicIntQueue with Doubling  "以替代命名类 
*/
val queue = new BasicIntQueue with Doubling
queue.put(10)
queue.get()    //20

//以下是Incrementing和Filtering,这两个特质的实现展示如下:
trait Incrementing extends IntQueue{
  abstract override def put(x: Int) { super.put(x + 1)}
}
trait Filtering extends IntQueue{
  abstract override def put(x: Int){
    if(x>=0) super.put(x)
  }
}

/*
有了上面的改动,你现在可以挑选想要的组成特定的队列,比方说,这里有一个队列能够过滤负数有对每个队列的数字增量
*/
val queue = (new BasicIntQueue with Incrementing with Filtering)
queue.put(-1);queue.put(0); queue.put(1)
queue.get()    //1
queue.get()    //2
/*
混入的次序非常重要,越靠近右侧的特质越先其作用,当你调用带混入的类的方法时,最右侧特质的方法首先被调用,如果那个方法调用了super,他调用其左侧特质的方法,以此类推,其中Filtering的super.put调用的是Incrementing的put,Incrementing的super.put调用的是类BasicIntQueue的put
*/
```
# 6.为什么不是多重继承
```
/*
对于多重继承来说,super调用导致的方法调用可以在调用发生的地方明确决定,而对于特质来说,方法调用时由类和被混入到类的特质的线性化所决定的
*/
class Animal
trait Furry extends Animal
trait HasLegs extends Animal
trait FourLegged extends HasLegs
class Cat extends Animal with Furry with FourLegged
/*
Cat类的继承层级和线性化次序展示在下图中,其中白色三角箭头表名继承,箭头指向超类型
黑底非三角箭头说明线性化次序,牵头指向super调用解决的方法
*/
```

 
![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/12/1.png)
 
![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/12/2.png)


```
//当上述类和特质中的任何一个通过super调用了方法,那么被调用的实现将是他线性化的右侧的第一个实现
```


# 7.特质,用还是不用
```
/*
当你实现了一个可重用的行为集合时,你将必须决定是使用特质还是抽象类,这里没有固定的规律,但是本节包含了一条可供考虑的规则
1.如果行为不被重用,那么就把它当做具体类,具体类没有可重用的行为
2.如果要在多个不相关的类中重用,就做成特质,只有特质可以混入到不同的类层级中
*/
```




