---
title: 第二十章 抽象成员
categories: scala   
tags: [scala,scala编程]
---




# 1.抽象成员的快速浏览
```
/*
我们称不完全定义的类或特质的成员为抽象成员,抽象成员将被声明类的子类实现
*/

/*
下面的特质对每种抽象成员各声明了一个例子,他们分别是:类型(T), 方法(transform), val(initial) , 以及var(current):
*/
trait Abstrant {
  type T
  def transform(x: T): T
  val initial: T
  val current: T
}

//Abstract的具体实现需要对每种抽象成员填入定义,下面的例子是提供这些定义的实现
class Concrete extends Abstrant{
  type T = String
  override def transform(x: String) = x + x
  val initial= "hi"
  override var current = initial
}
/*
这个实现为类型T提供了具体的含义,他被定义为类型String的别名,transform被定义为参数字符串与其自身连接的操作,而initial和current值都被设置为"hi"
*/



```

# 2.类型成员
```
/*
抽象类型这个术语在scala中是指不带具体定义的,由"type"关键字声明为类或特质的成员的类型,类本身可以是抽象的,而特质本来就是抽象的,但不论哪种都不是scala中所指的抽象类型,scala的抽象乐行永远都是某个类或者特质的成员,就好像特质Abstract里的类型T那样


你可以把非抽象的类型成员,如Concrete类里的类型T,想象成是类型定义新的名称,或别名的方式,例如Concrete类中,类型String被指定了别名T,因此,任何出现在Concrete定义中的T指的都是String,这也包含了transform的参数和结果类型initial,以及current,这些在Abstract超特质中声明的时候提到的T的成员,因此当Concrete类实现这些抽象成员的时候,所有的T都被解释为String


使用类型成员的理由之一是为类型定义短小的,具有说明性的别名,因为类型的实际名称可能比别名更冗长,或语义不清,这种类型成员有助于净化类或特质的代码,类型成员的另一种主要用途是声明必须被定义为子类的抽象类型
*/

```

# 3.抽象val
```
//抽象val以如下形式定义
val initial: String 

//他指明了val的名称和类型,但不指定值,该值必须由子类的具体val定义提供,例如:Concrete类以如下方式实现了val:
val initial    = "hi"

//抽象的val声明类似于抽象的无参数方法声明,如:
def    initial: String
/*
如果initial是抽象val,那么客户就获得了保证,每次引用都将得到同样的值,如果initial是抽象方法,就不会获得这样的保证,因为在这种情况下initial可以实现为每次调用时都返回不同值的具体方法
*/


abstract class Fruit{
  val v: String //'v'代表value(值)
  def m: String //'m'代表method方法
}
abstract class Apple extends Fruit{
  val v: String
  val m: String //可以用"val" 重写def
}
abstract class BadApple extends Fruit{
  def v: String //error , 不能用"def"重写val
  def m: String
}
/*
换句话说,抽象的val限制了合法的实现方式,任何实现都必须是val类型的定义,不可以是var或def,另一方面,抽象方法声明可以被实现为具体的方法定义或具体的val定义,
*/

```
# 4.抽象var
```
//与抽象val类似,抽象var只声明名称和类型,没有初始值,如下:
trait AbstractTime{
  var hour: Int
  var minute: Int
}

/*
类似于hour和minute这样的抽象var表达的是什么意思呢?在18.2节看到声明为类成员的var实际配备了getter和setter方法,对于抽象var来说也是如此,比如如果你声明了名为hour的抽象var,实际上是隐式声明了抽象getter方法,hour及抽象setter方法(hour_=) 
*/
trait AbstractTime{
  def hour: Int //'hour' 的getter方法
  def hour_=(x:Int) //"hour" 的setter方法
  def minute: Int //"minute"的getter方法
  def minute_=(x:Int) //'minute'的setter方法
}




```

# 5.初始化抽象val
```
/*
抽象val有时会扮演类似于超类的参数这样的角色:他们能够让你在子类中提供超类缺少的细节信息,这对于特质来说尤其重要,因为特质缺少能够用来传递参数的构造器,因此通常参数化特质的方式就是通过需要在子类中实现的抽闲val完成
*/
trait RationalTrait {
  val numerArg: Int
  val denomArg: Int
}

//实现
new RationalTrait {
  override val denomArg: Int = 1
  override val numerArg: Int = 1
}
/*
这里关键字new出现在特质名称(RationalTrait)之前,然后是花括号包围的类结构体,这个表达式可以产生混入了特质并被结构体定义的匿名类实例,这种特使的匿名类实例化结果与使用new Rational(1,2)的方式创建的实例具有类似的效果,不过这种类比并非完美,两者在表达式的初始化顺序方面存在着微妙的差别
*/ 
//当你写下:
new Rational(expr1, expr2)

//两个表达式,expr1和expr2会在类Rational初始化之前计算,因此在执行类的初始化操作是expr1和expr2的值已经可用,然而对于特质来说,情况正好相反,当你写下:
new RationalTrait{
    val numerArg = expr1
    val denomArg = expr2
}
//表达式expr1和expr2被作为匿名初始化的一部分计算,但匿名类的初始化在RationalTrait之后,因此numerArg和denomArg的值在RationalTrait初始化期间还没有主备好(更为精确的说,选用任何值都将得到Int类型的默认值,0),对于之前的RationalTrait定义来说,这不是问题,因为特质的初始化没有用到numerArg和denomArg的值,但是对于下面的代码来说就成为一个问题,因为其中定义了经过约分的分子和分母
trait RationalTrait{
  val numerArg: Int
  val denomArg: Int
  require(denomArg != 0)

  private val g = gcd(numerArg, denomArg)

  val number = numerArg / g
  val denom = denomArg / g

  private def gcd(a:Int, b:Int): Int =
    if (b == 0) a else gcd(b, a%b)

  override def toString = number + "/" + denom
}

//测试

scala> new RationalTrait{
     | val numerArg = 1*x
     | val denomArg = 2*x
     | }
java.lang.IllegalArgumentException: requirement failed
        at scala.Predef$.require(Predef.scala:221)
        at RationalTrait$class.$init$(<console>:10)
        at $anon$1.<init>(<console>:10)
        at .<init>(<console>:10)
        at .<clinit>(<console>)
        at .<init>(<console>:7)

/*
出错的原因是:当类RationalTrait初始化的时候,denomArg仍然为他的默认值0,使得require调用失败
上述例子演示了类参数和抽象字段的初始化顺序并不一致,类参数在被传递给构造器之前计算(除非参数是传名的),相反子类对于val定义的实现,是在超类完成了初始化之后执行的
*/
```
> fields预初始化字段
```
/*
第一种解决方案,预初始化字段,可以让你在调用超类之前初始化子类的字段,操作的方式是把字段加上花括号,放在超类构造器调用之前,
*/
new {
  val numerArg = 1*x
  val denomArg = 2*x
} with RationalTrait


//预初始化字段不仅限于匿名类,他们还可以被用于对象或有名称的子类

//预初始化段落在每个例子中都被定义的对象或类的extends关键字之后
object twoThirds extends {
  val numerArg = 2
  val denomArg = 3
} with RationalTrait


/*
由于预初始化的字段在超类构造器调用之前被初始化,因此他们的初始化器不能引用正被构造的对象,相应的结果是,如果有引用this的这种初始化器,那么实际指向的是包含了正被构造的类或对象的对象,而不是被构造对象本身
*/
scala> new {
     | val numerArg = 1
     | val denomArg = this.numerArg*2
     | } with RationalTrait
<console>:11: error: value numerArg is not a member of object $iw
              val denomArg = this.numerArg*2
                                  ^
/*
上述例子编译通过的原因在于this.numerArg引用是在包含new的对象中numerArg字段(这个例子中是指名为$iw的合成对象,解释器会把用户输出的语句放在这个对象中)
*/

//示例了如何在超特质的初始化过程中使用类参数的通用模式
class RationalClass(n: Int, d:Int) extends {
  val numerArg = n
  val denomArg = d
}with RationalTrait{
  def + (that: RationalClass) = new RationalClass(
    number * that.denom + that.number*denom, denom * that.denom
  )
}
```

> 懒加载val
```
/*
如果你把lazy修饰符前缀val定义上,那么右侧的初始化表达式直到val第一次被使用的时候才计算
*/


scala> object Demo{
     | val x = {println("initializing x"); "done"}
     | }
defined module Demo

scala> Demo
initializing x
res6: Demo.type = Demo$@3d0035d2


scala> Demo.x
res7: String = done
/*
发现,用到Demo的时候,他的x字段就完成了初始化,x的初始化成为了Demo初始化的一部分
*/

//将x字段定义为lazy

scala> object Demo{
     | lazy val x = {println("initialing x"); "done"}
     | }
defined module Demo

scala> Demo
res8: Demo.type = Demo$@3c78e551

scala> Demo.x
initialing x
res9: String = done

scala> Demo.x
res10: String = done

/*
初始化Demo不会执行初始化x的调用,x的初始化将延迟到第一次使用x的时候
第一次计算懒加载val的时候结果就被保存了下来,以备同样的val后续使用
*/



trait RationalTrait{
  val numerArg: Int
  val denomArg: Int

  private lazy val g = {
    require(denomArg != 0)
    gcd(numerArg, denomArg)
  }

  lazy val numer = numerArg / g
  lazy val denom = denomArg / g

  override def toString = numer + "/" + denom

  private def gcd(a:Int, b:Int): Int =
    if (b == 0) a else gcd(b, a%b)
}

//测试
scala> val x = 2
x: Int = 2

scala> new RationalTrait{
     | val numerArg = 1*x
     | val denomArg = 2*x
     | }
res13: RationalTrait = 1/2

//执行过程
/*
1.首先,RationalTrait的新实例被创建出来,特质的初始化代码被运行,该初始化代码为空,没有任何字段被初始化
2.之后,有new表达式定义的匿名子类的主构造器被执行,他把numerArg初始化为2,把denomArg初始化为4
3.之后,解释器调用了构造器对象的toString方法,结果值被打印出来
4.之后,numer字段被特质RationalTrait的toString方法首次访问,因此它的初始化器执行计算
5.numer的初始化器访问了私有字段g,因此g接下来被初始化计算,这次计算访问了numerArg和denomArg,他们定义在第二步
6.之后,toString方法访问了denom值,引发denom的计算,这次访问计算了denomArg和g的值,g字段的初始化器不再重新计算,因为他已经在第五步执行过
7.最终,结果字符串"1/2"被构造出来并被打印
*/

/*
请注意,在RationalTrait类中,g的定义在代码文本中处于numer和denom定义之后,尽管如此,因为所有的三个值都是懒加载的,所以g将在numer和denom完成初始化之前被初始化,这说明了懒加载val的一个很重要的属性,定义的文本顺序不用多加考虑,因为初始化是按需的,从而,懒加载val可以免去你作为程序员不得不认真考虑的问题,及如何安排val定义顺序,以确保所有东西在需要的时候已经完成定义
*/
```

# 6.抽象类型
```
/*
与所有其他抽象声明一样,抽象类型声明也是将在子类中具体定义的事务的占位符,这里,他是将在之后的类层次中定义的类型,因此上文的T是对在声明点尚不可知的类型的引用,不同的子类可以提供不同的T实现
*/

//假设给了你一个为动物饮食习惯建模的任务,你或许会以Food类和带有eat方法的Animal类开始工作
class Food
abstract class Animal {
  def eat(food: Food)
}

//然后你或许尝试把这两个类特化为Cow类吃Grass类(牛吃草)
class Grass extends Food
class Cow extends Animal{
  override def eat(food: Grass) = {}//不能编译
}
/*
这里的情况是Cow类的eat方法不能重写Animal类的eat方法,因为参数类型不同---Cow类里是Grass, 而Animal类里是Food
*/

//为什么要做这样的限制?
class Food
abstract class Animal {
  def eat(food: Food)
}

class Grass extends Food
class Cow extends Animal{
  override def eat(food: Grass) = {}//不能编译,不过如果能够编译通过的话,...
}

class Fish extends Food
val bessy: Animal = new Cow
bessy eat (new Fish)  //.....你将能用鱼喂牛


/*
你应该做的是采用更为精确的建模方式,Animal的确吃Food,但Animal具体吃什么类型的Food取决于Animal,这可以使用抽象类型干净的表示出来
*/
class Food
abstract class Animal{
  type SuitableFood <: Food
  def eat(food: SuitableFood)
}


/*
有了新的定义,Animal就可以只吃适合的食物了,不过到底什么食物合适,这并不在Animal类的层面决定,这也就是SuitableFood被建模为抽象类型的原因,具体具有上界约束:Food,表达为" <:Food " 子句,说明任何(Animal子类中的)SuitableFood的具体实例化结果都必须是Food的子类
*/
class Grass extends Food
class Cow extends Animal{
  type SuitableFood = Grass
  def eat(food: Grass){}
}

```

# 7.路径依赖类型
```
/*
通常情况下,不同的路径将产生不同的依赖
*/
class DogFood extends Food
class Dog extends Animal{
  override type SuitableFood = DogFood
  override def eat(food: DogFood) = {}
}
//如果你尝试把牛的饲料用来喂狗,你的代码将无法通过编译

scala> val bessy = new Cow
bessy: Cow = Cow@59edb4f5

scala> val lassie = new Dog
lassie: Dog = Dog@7ea2412c

scala> lassie eat (new bessy.SuitableFood)
<console>:16: error: type mismatch;
 found   : Grass
 required: DogFood
              lassie eat (new bessy.SuitableFood)
                          ^
/*
问题在于传递给eat方法的SuitableFood对象的类型(bessy.SuitableFood), 不能匹配eat的参数类型,lassie.SuitableFood,然而如果同样是Dog的话,情况会不一样,因为Dog的SuitableFood类型被定义为DogFood类的别名,所以对于两条Dog来说,他们的SuitableFood类型实际上是一样的,
*/

scala> val bootsie = new Dog
bootsie: Dog = Dog@11381415

scala> lassie eat (new bootsie.SuitableFood)

scala>

/*
路径依赖类型会让我们想起java中的内部类语法,但两者有决定性的差别:路径依赖类型表达了外在的对象,而内部类表达了外在的类
*/


class Outer{
  class Inner
}
/*
scala中,内部类的表达形式为Outer#Inner, 而不是java的Outer.Inner ,"." 语法保留给对象使用,例如,假设你实例化了类型Outer的两个对象
*/
val o1 = new Outer
val o2 = new Outer
/*
这里o1.Inner和o2.Inner是两个路径依赖类型,o1.Inner类型是指特定(o1引用的)外部对象的Inner类
*/


//实例化内部类
new o1.Inner   //因为o1.Inner是属于o1对象的内部类,所以new o1.Inner是new出来的对象
//返回的内部对象将包含其外部对象的引用,即o1的对象引用,相反Outer#Inner没有指明任何特定Outer实例,因此你不能创建他的实例
new Outer#Inner    //error


```
# 8.枚举
```
/*
scala中如果想要创建新的枚举,只需要定义扩展scala.'Enumeration这个类的对象即可
*/

object Color extends Enumeration{
  val Red = Value
  val Green = Value
  val Blue = Value
}
//等价于
object Color extends Enumeration{
  val Red, Green, Blue = Value
}
/*
这个对象定义提供了三个值:Color.Red, Color.Green, Color.Blue,你可以引用Color的全部内容:
*/
import Color._
//然后简单写成Red, Green, 和Blue ,但这些值的类型是什么?Enumeration定义了内部类,名为Value,以及同名的无参方法Value返回该类的新对象,也就是说诸如Color.Red类的值类型是Color.Value,而Color.Value也正是定义在对象Color中的所有枚举值的类型,他是路径依赖类型,其中Color是路径,Value是依赖类型,这里很重要的一点是他是全新的类型,与其他所有的类型都不一样

object Direction extends Enumeration{
  val North, East, South, West = Value
}
//Direction.Value与Color.Value不同,因为两种类型的路径部分不同


//scala的Enumeration类还提供了其他语言的枚举设计中所拥有的许多其他特质,你可以通过使用Value方法不同的重载变体把名称与枚举值联系起来
object Direction extends Enumeration{
  val North = Value("North")
  val East = Value("East")
  val South = Value("South")
  val West = Value("West")
}

//遍历枚举的所有值
for(d <-Direction) print(d+" ")    //North East South West

//枚举值从0开始计数,你可以用枚举值的id方法获得他的计数值
scala> Direction.East.id
res20: Int = 1

//也可以反过来,通过非零的整数获得id为该数的枚举值
scala> Direction(1)
res19: Direction.Value = East

```


# 9.案例研究:货币
```
//本章的剩余部分提供了一个解释scala中如何使用抽象类型的案例研究,任务是设计Currency类,典型的Currency实例可以代表美元,欧元,日元,或其他货币种类的一笔金额,而且还有可能需要一些关于货币方面的计算,比方说,相同货币的两笔金额应该能够相加,或金额应该能够乘上代表利率的因子

abstract class Currency{
  val amount: Long    //金额大小
  def designation: String    //标识货币的字符串

  override def toString: String = amount + " " + designation
  def +(that: Currency): Currency =     ...
  def *(x:Double): Currency =   ...
}

//产生的结果如下:
79 USD
11000 Yen
99 Euro

//抽象的实现
new Currency {
  override val amount: Long = 79L
  override def designation = "USD"
}


//如果我们仅仅是对单一货币建模,那么这种设计不存在问题,可是一旦我们需要处理若干货币种类,这样做就不行了,假设你对美元和欧元建模为货币类的两个子类
abstract class Dollar extends Currency{
  override def designation = "USD"
}

abstract class Euro extends Currency{
  override def designation = "Euro"
}

/*
上面的做法看上去很有道理,但是在执行加法的时候将两种货币放在一起感觉古怪,你要的应该是+方法更具体化的版本,实现在Dollar类中的时候,他应该带Dollar参数并产生Dollar结果,实现在Euro类中的时候,应该带Euro参数并产生Euro结果,因此加法的类型应该依赖于所在类而改变,尽管如此,你还是希望方法只写一次即可,而不是每次定义新的货币都要重写
*/
//第二版
abstract class AbstractCurrency{
  type Currncy <:AbstractCurrency
  val amount: Long
  def designation: String
  override def toString: String = amount + " " + designation
  def +(that: Currency): Currency =   ...
  def *(x: Double): Currency =   ...
}

//第二版与前面的一版的却别在于:类现在成为AbstractCurrency,并且包含了抽象类型Currency,代表未知的真实货币种类,每种AbstractCurrency的具体子类将需要把Currency类型修改为这个类本身,从而能够把两者结合在一起
//使用
abstract class Dollar extends AbstractCurrency{
  override type Currncy = Dollar
  override def designation = "USD"
}

/*
这个设计仍不完美,问题之一是隐藏在AbstractCurrency类省略号中的方法定义+和* 如何具体化? 像下面这样吗?
*/
def +(that: Currency): Currency = new Currency {
  val amount = this.amount + that.amount
}
//编译不通过,因为scala对待抽象类型的一种限制是你既不能创建抽象类型的实例,也不能把抽象类型当做其他类的超类型,因此编译器将拒绝上面例子的代码实例化Currency的尝试
//解决的方法是通过工厂方法
abstract  class CurrencyZone{
  type Currency <: AbstractCurrency
  def make(x: Long): Currency
  abstract class AbstractCurrency{
    val amount: Long
    def designation: String
    override def toString: String = amount + " " + designation
    def +(that: Currency): Currency = new Currency {
      make((this.amount + that.amount))
    }
    def *(x: Double): Currency =
      make((this.amount*x).toLong)
  }
}

//实现
object US extends Currency{
  abstract class Dollar extends AbstractCurrency{
    def designation = "USD"
  }
  type Currency = Dollar
  def make(x:Long) = new Dollar {val amount = x}
}

/*
上面的情况是:每种货币都仅用一个测量单位:美元,欧元,或日元,然而大多数货币都有子单位,例如:在美国有美元和美分,下面将引入CurrencyUnit字段,以包含货币一个标准单位的金额
*/
//实现2
object US extends Currency{
  abstract class Dollar extends AbstractCurrency{
    def designation = "USD"
  }
  type Currency = Dollar
  def make(cents:Long) = new Dollar {val amount = cents}
  val Cent = make(1)  //美分
  val Dollar = make(100)  //美元
  val CurrencyUnit = Dollar
}

//改进toString方法,例如:10美元与23美分的总和应该打印成小数:10.23USD
override def toString: String = ((amount.toDouble/CurrencyUnit.amount.toDouble) formatted (
  "%."+decimals(CurrencyUnit.amount) + "f"))

private def decimals(n: Long): Int =
  if (n == 1) 0 else 1+decimals(n/10)


//改进:添加货币特征转换,首先,你可以编写Converter对象,以包含适用的货币汇率
object Converter{
  var exchangeRate = Map{
    "USD" -> Map("USD"->1.0, "EUR"->0.7596, "JPY"->1.211, "CHF"->1.223)
    "EUR"-> Map("USD"->1.316, "EUR"->1.0, "JPY"->1.594, "CHF"->1.623)
    "JPY"-> Map("USD"->0.8257, "EUR"->0.6272, "JPY"->1.0, "CHF"->1.018)
    "CHF"-> Map("USD"->0.8108, "EUR"->0.6160, "JPY"->0.982, "CHF"->1.0)
  }
}

def from(other: CurrencyZone#AbstractCurrency): Currency =
  make(Math.round(
    other.amount.toDouble * Converter.exchangeRate(other.designation)(this.designation)
  ))



//总代码

abstract  class CurrencyZone{
  type Currency <: AbstractCurrency
  def make(x: Long): Currency
  abstract class AbstractCurrency{
    val amount: Long
    def designation: String
    override def toString: String = amount + " " + designation
    def +(that: Currency): Currency = new Currency {
      make((this.amount + that.amount))
    }
    def *(x: Double): Currency =
      make((this.amount*x).toLong)
  }
  def from(other: CurrencyZone#AbstractCurrency): Currency =
    make(Math.round(
      other.amount.toDouble * Converter.exchangeRate(other.designation)(this.designation)
    ))
  private def decimals(n: Long): Int =
    if (n == 1) 0 else 1+decimals(n/10)

  override def toString: String = ((amount.toDouble/CurrencyUnit.amount.toDouble) formatted (
    "%."+decimals(CurrencyUnit.amount) + "f"
    ))
  val CurrencyUnit: Currency
}

```


