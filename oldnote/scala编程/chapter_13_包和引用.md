---
title: 第十三章 包和引用
categories: scala   
tags: [scala,scala编程]
---



&emsp;做程序的时候,尤其是很大的程序,使耦合最小化是很重要的,低耦合能降低程序一部分的细微改变影响到另一部分的正常执行这样的风险,减少耦合的方式之一是使用模块化风格编写代码,把程序分解成若干比较小的模块,把每块分成内部和外部,在模块的内部(即:模块的实现部分) 工作时,你只需要和同样工作于这个模块的程序员交互,只有当你必须改变模块的外部(即模块的接口)时,才需要和工作于其他模块的开发人员交互

<!--more-->

# 1.包
```
/*
你可以用两种方式把代码放在命名包中,一种是通过把package子句放在文件顶端的方式把整个文件内容放进包里,如下:
*/
package bobsrockets.navigation
class Navigator

/*
由于scala代码是Java生态系统的一部分,在你发布到公开场合的时候,推荐遵循Java的反域名习惯设置scala包名,因此,更好的Navigator包名应该是: com.bobsrockets.navigation ,然而在本章里我们是为了让例子容易理解我们去掉了"com." 
*/

//scala里另一种把代码放进包里的方式更像C#的命名空间,可以在package子句之后把要放到包里的定义用花括号括起来,除此之外,这种语法还能让你把文件的不同部分放在不同的包里,例如:你或许会把类的测试与原始代码一起放在同一个文件,但在不同的包里,如下:
package bobsrockets {
  package navigation {
    class Navigator //bobsrockets.navigation包中
    package tests {
      class NavigatorSuite  //bobsrockets.navigation.tests包中
    }
  }
}

//实际上如果一个包只是用来嵌入另一个包的话,你可以使用如下的方式:
package bobsrockets.navigation {
    class Navigator //bobsrockets.navigation包中
    package tests {
      class NavigatorSuite  //bobsrockets.navigation.tests包中
    }
}

/*
正如你看到的一样,scala的包的确是嵌套的,也就是说,包navigation从语义上讲在包bobsrockets的内部,Java包尽管是分级的,却不是嵌套的,在Java里,当你命名一个包的时候,你必须从包层级的根开始
*/
package bobsrockets {
  package navigation {
    class Navigator //bobsrockets.navigation包中
  }
  package launch {
    class Booster{
      //不用写bobsrockets.navigation.Navigator
      val nav = new navigation.Navigator
    }
  }
}

/*
上面的方式之所以不用写全路径,是因为Booster类包含在bobsrockets包汇总,而这个包又含有navigationy
成员,因此可以直接写navigation,务必使用前缀,就好像类方法里的代码可以直接用类的其他方法而不用前缀
*/

//文件launch.scala
package launch {
  class Booster3
}

//文件bobsrockets.scala
package bobsrockets {
  package navigation {
    package launch {
      class Booster1
    }
    class MissionControl {
      val booster1 = new launch.Booster1
      val booster2 = new bobsrockets.launch.Booster2
      val booster3 = new _root_.launch.Booster3
    }
  }
  package launch {
    class Booster2
  }
}
/*
为了处理这种情况,scala在所有用户可创建的包之外提供了一个名为_root_的包,换句话说,任何你能写出来的顶层包都被当做是_root_包的成员,例如上述代码中的launch和bobsrockets都是_root_包的成员,因此,_root_.launch让你能访问顶层的launch包,_root_.launch.Booster3指向的就是最外面的booster类 
*/
```

# 2.引用
```
/*
在scala里,包和其他成员可以用import子句来引用,之后引用的项目就可以用File这样的简单名访问,否则就要用Java.io.File这样的全称
*/
package bobsdelights
abstract class Fruit(val name: String, val color: String)
object Fruits {
  object Apple extends Fruit("apple", "red")
  object Orange extends Fruit("orange", "orange")
  object Pear extends Fruit("pear", "yellowish")

  val menu = List(Apple, Orange, Pear)
}

//易于访问Fruit
import bobsdelights.Fruit

//易于访问bobsdelights的所有成员,在Java中使用的是*号,在scala里使用的是_
import bobsdelights._

//易于访问Fruits的所有成员
import bobsdelights.Fruits._

/*
scala引用可以出现在任何地方,而不是仅仅在编译单元的开始处,同样,他们可以指向任意值,例如,
*/

def showFruit(fruit: Fruit) {
  import fruit._
  println(name + "s are " + color)
}
/*
方法showFruit引用了他的参数(fruit)的所有成员,之后println语句就可以直接使用name和color了,这两个引用值等价于fruit.name 和fruit.color,当你把对象当做模块使用时这种语法尤其有用
*/


/*
scala的import子句比Java的更为灵活,在他们之间存在三点主要差异,在scala中,引用:
1.可以出现在任何地方
2.可以指的是(单例或正统的)对象及包
3.可以重命名或隐藏一些被引用的成员
*/

/*
scala的引用还可以重命名或隐藏成员,这可以在被引用成员的对象之后加上括号里的引用选择器子句来做到
*/

//此例只引用了对象Fruits的Apple和Orange成员
import Fruits.{Apple, Orange}

//此例从对象Fruits引用了Apple和Orange两个成员,不过,Apple对象重命名MacIntosh,重命名子句的格式"<原始名> => <新名>"
import Fruits.{Apple =>McIntosh, Orange}

/*
此例以SDate的名字引用了sql的日期类,以便同时以Date的名字引用普通的Java日期类
 */
import java.sql.{Date => SDate}

/*
此例以名称S引用了Java.sql包,这样你就可以写成S.Date
 */
import java.{sql => S}

/*
此例引用了对象Fruits的所有成员,这与import Fruits._同义
 */
import Fruits.{_}

//此例从Fruits对象引用所有成员,不过重命名Apple为McIntosh
import Fruits.{Apple => McIntosh, _}

/*
此例引用了Fruits的所有成员,Pear除外," <原始名> => _ " 格式的子句会从被引用的名字
中排除<原始名> ,从某种意义上来说,把某样东西重命名为 "_" 就是表示把它隐藏掉,这对避免出现
混淆的局面有所帮助,比如说你有两个包,Fruits和Notebooks ,他们都定义了类Apple
如果你只是想得到名为Apple的笔记本,而不是水果,你可以使用如下的方式:
 */
import Fruits.{Pear => _, _}

import Notebooks._
import Fruits.{Apple => _, _}

/*
总结:
1.简单名x,把x包含进引用名集
2.重命名子句x=>y 让名为x的程艳以名称y出现
3.隐藏子句x=>_ 把x排除在引用名集之外
4.全包括 '_" 引用除了前面子句提到的之外的全体成员,如果存在全包括,那么必须是引用选择的最后一个

*/
```

# 3.隐式引用
```
/*
scala为每个程序隐式的添加了一些引用,就好像每个以".scala"为扩展名的源文件的顶端都加在了下列的三个引用子句
*/
import Java.lang._   //java.lang包的所有东西
import scala._      //scala包的所有东西
import Predef._       //Predef对象的所有东西

/*
因为Java.lang是隐式引用的,所以说你就可以直接使用Thread而不需要写成java.lang.Thread
又如:scala包被隐式引用, 你可以直接写LIst, 而不用写成scala.LIst
Predef对象包含了许多scala程序中常用到的类型,方法和隐式的定义,比方说:因为Predef是隐式引用,所以你可以直接写assert而不用写成Predef.assert


上面的这三个引用子句与其他的稍有不同,出现在靠后位置的引用将覆盖靠前的引用,例如:StringBuilder类被定义在scala包及包java.lang中,因为scala引用覆盖了java.lang引用,所以StringBuilder简单名被看做scala.StringBuilder,而不是java.lang.StringBuilder
*/
```

# 4.访问修饰符
&emsp;包/类或对象的成员可以访问修饰符private和protected做标记,这些修饰符把对成员的访问限制在代码确定的区域中,scala大体上遵守java对访问修饰符的对待方式,但也有一些重要的差异

 私有成员
```
/*
私有成员的处理方式与java的相同,标记为private的成员仅在包含了成员定义的类或对象内部可见
*/

class Outer {
  class Inner {
    private def f() { println("f")}
    class InnerMost {
      f() //Ok
    }
  }
  (new Inner).f() //f不可访问
}
/*
在scala里, (new Inner).f() 访问非法,因if在Inner中被声明为private而访问不再类Inner之内,相反,在类InnerMost里访问f没有问题,因为这个访问包含在Inner类之内,java允许这两种访问,因为他允许外部类访问其内部类的私有成员
*/
```

 保护成员
```
/*
scala里,保护成员只在定义了成员的类的子类中可以被访问,而在java中还允许同一个包的其他类中进行这种访问
*/
package p{
  class Super{
    protected def f() { println("f")}
  }
  class Sub extends Super {
    f()
  }
  class Other {
    (new Super).f()  //error ,f不可访问
  }
}

/*
例子中,Sub类对f的访问没有问题,因为f在Super中被声明为protected,而Sub是Super的子类,相反Other对f的访问不被允许,因为Other没有继承自Super,java里,后者同样会被认可,因为Other和Sub在同一个包里
*/
```
 公开成员
```
/*
任何默认标记为private或protected的成员都是公开的,公开成员没有显示的修饰符,这样的成员可以在任何地方被访问
*/
```

 保护的作用域
```
/*
在scala里的访问修饰符可以通过使用限定词强调,格式为private[X] ,或protected[X] 的修饰符表示 "直到" X的私有或保护,这里X指代某个所属的包,类或单例对象
在这段代码中,类Navigator被标记为private[bobsrockets] ,就是说这个类对包含在bobsrockets包里的所有的类和对象可见

这种技巧在横跨若干包的大型项目中非常有用,他允许你定义一些在你项目的若干子包中可见但对于项目外部的客户却始终不可见的东西
*/
package bobsrockets {
  package navigation {
    private [bobsrockets] class Navigator {//可以在bobsrockets包中访问
      protected [navigation] def useStarChar(){}//该方法能被Navigator所有子类及包含在navigation包里的所有代码访问
      class LegOfJourney {
        private[Navigator] val distance = 100 //在类Navigator的任何地方都可见
      }
      private[this] var speed = 200 //仅能在包含了定义的同一个对象中被访问
    }
  }

  package launch {
    import navigation._
    object Vehicle {
      private[launch] val guide = new Navigator
    }
  }
}

```
&emsp;下表罗列了private限定字的效果,每一行说明了一个被限定的私有修饰符及如果这个修饰符被附加在LegOfJourney类里声明的distance变量上意味着什么

没有修饰符				|公开访问
:-------         |:--------
private[bobsrockets]	|在外部包中访问
private[navigation]		|与java的包可见度相同
private[Navigator]		|与java的private相同
private[LegOfJourney]	|与scala的private相同
private[this]			|仅在同一个对象中可以访问

 可见性和伴生对象
```
/*
java里,静态成员和实例成员属于同一个类,因此访问修饰符可以统一的应用在他们之上,你已经知道在scala里没有静态成员,作为替代,可以拥有包含成员的单例的伴生对象
*/

class Rocket {
  import Rocket.fuel
  private def canGoHomeAgain = fuel > 20
}

object Rocket {
  private def fuel = 10
  def chooseStrategy(rocket: Rocket){ 
    if (rocket.canGoHomeAgain)
      goHome()
    else 
      pickAstar()
  }
  def goHome(){}
  def pickAstar(){}
}
/*
对于私有或保护访问来说,scala的访问规则给予了伴生对象和类一些特权,类的所有访问权限都对伴生对象开放,反过来也是如此,具体的说,就是对象可以访问所有他的伴生类的私有成员,就好像类也可以访问伴生对象的所有私有成员一样

如上面的例子:Rocket类可以访问fuel方法,而他在Rocket对象中是被声明为private的,类似的,Rocket对象也可以访问Rocket类里面的私有方法canGetHome
*/
```








