---
title: 第十七章 集合类型
categories: scala   
tags: [scala,scala编程]
---










 # 1.集合库概览

 ![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/17/1.png)

```
/*
Iterable是主要特质,他同时还是可变和不可变序列(Seq) , 集(Set), 以及映射(Map)的超特质,序列是有序的集合,例如:数组和列表,集可以通过==方法确定对每个对象最多只包含一个,映射则包含了键值映射关系的额集合

命名为Iterable是为了说明集合对象可以通过名为elements的方法产生Iterator(枚举器),
*/
def elements: Iterator[A]

/*
例子中的A是Iterator的类型参数,他指代集合中包含的元素的类型, elements返回的Iterator被参数化为同样的类型,,例如:Iterable[Int] 的elements方法将创建Iterator[Int]

Iterable包含几十个有用的具体方法,所有这些方法都是使用了elements返回的Iterator实现的,而elements是Iterable唯一的抽象方法,Iterable定义的方法中,许多是高阶方法,多数都已经在前面的章节中出现过,其中包含map, flatMap,filter, exists及find 

Iterator有许多与Iterable相同的方法,包括哪些高阶方法,但他们不属于同一层级,如图
*/
```

 ![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/17/2.png)

```
/*
特质Iterator扩展了AnyRef,Iterable与Iterator之间的差异在于特质Iterable指代的是可以被枚举的类型(如集合类型),而特质Iterator是用来执行枚举操作的机制,尽管Iterable可以被枚举若干次,但Iterator仅能使用一次,一旦你使用Iterator枚举遍历了集合对象,你就不能再使用它了,如果你需要再次枚举该集合对象,你需要对他调用elements方法获得新的Iterator
*/

/*
Iterator提供的具体方法都使用了next和hasNext抽象方法实现
*/

def hasNext: Boolean
def next: A

```


# 2.序列
&emsp;序列是继承自特质Seq的类,他可以让你处理一组线性分布的数据,因为元素是有序的,所以你可以请求第一个元素,第二个元素,...第n个元素

 列表
```
scala> val colors = List("red", "blue", "green")
colors: List[String] = List(red, blue, green)

scala> colors.head
res42: String = red

scala> colors.tail
res43: List[String] = List(blue, green)

```

 数组
```
/*
数组能够让你保留一组元素序列并可以基于零的索引高效访问(无论是获取还是添加)处于任意位置的元素,下列代码说明了如何创建长度已知但内容未知的数组
*/
scala> val fiveInts = new Array[Int](5)
fiveInts: Array[Int] = Array(0, 0, 0, 0, 0)

scala> val fiveToOne = Array(5,4,3,2,1)
fiveToOne: Array[Int] = Array(5, 4, 3, 2, 1)

//正如之前提到的,scala中数组的访问方式是通过把索引值放在圆括号里,而不是像java里那样放在方括号里,下面的例子和更新了数组元素:

scala> fiveInts(0) = fiveToOne(4)

scala> fiveInts
res45: Array[Int] = Array(1, 0, 0, 0, 0)

```

 列表缓存
```
/*
List类能够提供对列表头部,而非尾部的快速访问,因此,如果需要通过向结尾添加对象的方式建造列表,你应该考虑先以对表头前缀元素的方式反向构造列表,完成之后再调用reverse使得元素反转为你需要的顺序
*/

/*
另一种方式是使用ListBuffer, 这可以避免reverse操作,ListBuffer是可变对象(包含在scala.collection.mutable包中),他可以更高效的通过添加元素的方式构建列表,ListBuffer能够支持常量的添加和前缀操作,元素的添加使用+= 操作符,前缀使用+: 操作符,完成之后,可以通过对ListBuffer调用toList方法获得List,举例如下:
*/

scala> import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ListBuffer

scala> val buf = new ListBuffer[Int]
buf: scala.collection.mutable.ListBuffer[Int] = ListBuffer()

scala> buf += 1
res46: buf.type = ListBuffer(1)

scala> buf += 2    //向ListBuffer的后面添加元素
res47: buf.type = ListBuffer(1, 2)

scala> buf
res48: scala.collection.mutable.ListBuffer[Int] = ListBuffer(1, 2)

scala> 3 +: buf        //在前面添加新的元素,生成新的ListBuffer
res49: scala.collection.mutable.ListBuffer[Int] = ListBuffer(3, 1, 2)

scala> buf
res50: scala.collection.mutable.ListBuffer[Int] = ListBuffer(1, 2)

scala> buf.toList
res51: List[Int] = List(1, 2)

/*
使用ListBuffer替代List的另一个理由是为了避免栈溢出的风险,即使你能够使用前缀的方式以正确的次序构建列表,但是所需的递归算法不是尾递归,那么你也可以使用for表达式或while循环及ListBuffer做替代
*/

```

 数组缓存
```
/*
ArrayBuffer与数组类似,只是额外还允许你在序列的开始或结束的地方添加和删除元素,所有的Array操作都被保留,只是由于实现中的包装层导致执行的稍微有些慢,
*/
//在使用ArrayBuffer之前,你必须首先从可变集合包中引用它
scala> import scala.collection.mutable.ArrayBuffer

//创建ArrayBuffer的时候,你必须指定他的类型参数,但可以不用指定长度,ArrayBuffer可以自动调整分配的空间:

scala> val buf = new ArrayBuffer[Int]()
buf: scala.collection.mutable.ArrayBuffer[Int] = ArrayBuffer()

//ArrayBuffer还能使用 += 操作添加元素
scala> buf += 12    
res0: buf.type = ArrayBuffer(12)

scala> buf += 15
res1: buf.type = ArrayBuffer(12, 15)

scala> buf.length    //获得数组的长度
res2: Int = 2

scala> buf(0)    //通过索引访问元素
res3: Int = 12

```

 队列(Queue)
```
/*
如果你需要先进先出序列,可以使用Queue,scala的集合库提供了可变和不可变的Queue
*/
import scala.collection.immutable.Queue
val empty = new Queue[Int]

//你可以使用enqueue为不可变队列添加元素
val has1 = empty.enqueue(1)

//如果要添加多个元素的话,可以把集合当做enqueue调用的参数
val has123 =has1.enqueue(List(2,3))

//从队列的头部移除元素,可以使用dequeue
val (element, has23) = has123.dequeue        //element =1   has23 = Queue(2,3)


//对于不可变队列来说,dequeue方法将返回由队列头部元素和移除该元素之后的剩余队列组成的对偶(Tuple2)


//可变队列的使用方式与不可变队列一样,只是代之以enqueue方法,你可以使用 += ,及 ++= 操作符添加元素,还有,对于可变队列来说,dequeue方法将只从队列移除元素头并返回

scala> import scala.collection.mutable.Queue
import scala.collection.mutable.Queue

scala> val queue = new Queue[String]
queue: scala.collection.mutable.Queue[String] = Queue()

//添加元素
scala> queue += "a"
res4: queue.type = Queue(a)

//添加List
scala> queue ++= List("b", "c")
res5: queue.type = Queue(a, b, c)

//返回头部
scala> queue.dequeue
res6: String = a

scala> queue
res7: scala.collection.mutable.Queue[String] = Queue(b, c)


```

 栈
```
//如果需要的是先进后出的序列,你可以使用Stack,他同样在scala的集合库中也有可变和不可变版本,元素的推入使用push,弹出使用pop,只获取栈顶的元素而不移除可以使用top,下面是使用的可变栈的例子

scala> import scala.collection.mutable.Stack
import scala.collection.mutable.Stack

scala> val stack = new Stack[Int]
stack: scala.collection.mutable.Stack[Int] = Stack()

scala> stack.push(1)
res8: stack.type = Stack(1)

scala> stack
res9: scala.collection.mutable.Stack[Int] = Stack(1)

scala> stack.push(2)
res10: stack.type = Stack(2, 1)

scala> stack
res11: scala.collection.mutable.Stack[Int] = Stack(2, 1)

scala> stack.top
res12: Int = 2

scala> stack
res13: scala.collection.mutable.Stack[Int] = Stack(2, 1)

scala> stack.pop
res14: Int = 2

scala> stack
res15: scala.collection.mutable.Stack[Int] = Stack(1)

```

 字符串(经RichString隐式转换)
```
/*
RichString也是应该知道的序列,他的类型是Seq[Char] ,因为Predef包含了从String到RichString的隐式转换,所以你可以把任何字符串字符当做Seq[Char],举例如下:
*/

scala> def hasUpperCase(s: String) = s.exists(_.isUpperCase)
<console>:12: error: value isUpperCase is not a member of Char
       def hasUpperCase(s: String) = s.exists(_.isUpperCase)
                                                ^

scala> def hasUpperCase(s: String) = s.exists(_.isUpperCase)    

scala> hasUpperCase("Robert Frost")    // true

scala> hasUpperCase("e e cummings")    // false

/*
本例中的hasUpperCase方法体中,字符串s调用了exists方法,而String类本身并没有定义名为"exists"的方法,因此scala编译器会把s隐式转换为含有这个方法的RichString类,exists方法把字符串看做Seq[Char] ,并且如果所有的字符都是大写字母则返回值
*/
```


# 3.集(set)和映射(map)
```
/*
默认情况下在你使用"Set" 或" Map" 的时候,获得的都是不可变对象,如果需要的是可变版本,你需要首先写明引用,scala让你更易于使用不可变的版本,期望能够以此方式而并非相对的可变版本,这种访问易于来自Predef对象的支持,他被每个scala源文件隐含引用
*/

object Predef {
    type Set[T] = scala.collection.immutable.Set[T]
    type Map[K,V] = scala.collection.immutable.Map[K, V]
    type Set  = scala.collection.immutable.Set    //默认
    type Map = scala.collection.immutable.Map        //默认
///.....
}


//如果同一个源文件中既要用到可变版本,也要用到不可变版本的集合或映射,方式之一是引用包含了可变版本的包名

scala> import scala.collection.mutable
import scala.collection.mutable

scala> val mutaSet = mutable.Set(1,2,3)
mutaSet: scala.collection.mutable.Set[Int] = Set(1, 2, 3)

```

 使用集
```
/*
集的关键特性在于他可以使用对象的==操作检查,确保任何时候每个对象只在集中保留最多一个副本,
*/

scala> val text = "See Spot run, Run, Spot, Run!"

scala> val wordsArray = text.split("[!,. ]+")
wordsArray: Array[String] = Array(See, Spot, run, Run, Spot, Run)

scala> for(word <- wordsArray)
     words += word.toLowerCase

```

 集的常用操作

操作									|行为
:--                                     |:--
val nums = Set(1,2,3)					|创建不可变集(nums.toString) 返回Set(1,2,3)
nums += 5								|添加元素(返回Set(1,2,3,5))
nums -= 3								|删除元素(返回Set(1,2))
nums ++ List(5,6)						|添加多个元素(返回Set(1,2,3,5,6)
nums – List(1,2)						|删除多个元素(返回Set(3))
nums ** Set(1,3,5,7)					|获得交集(返回Set(1,3))
nums.size								|返回集中包含的对象数量(返回3)
nums.contains(3)						|检查是否包含(返回true)
import scala.collection.mutable			|引用可变集合类型
val words = mutable.Set.empty[String]	|创建空可变集(words.toString, 返回Set())
words += "the"							|添加元素(words.toString返回Set(the))
words -= "the"							|如果存在元素,则删除(words.toString 返回Set())
words ++= List("do", "re", "md")		|添加多个元素(words.toString ,返回Set(do,re,md)
words –= List("do", "re")				|删除多个元素(words.toString 返回Set(md))
words.clear								|删除所有元素(words.toString 返回Set())


使用映射

```

scala> val map = scala.collection.mutable.Map.empty[String, Int]
map: scala.collection.mutable.Map[String,Int] = Map()
/*
在创建映射的时候,你必须指定两个类型,第一个类型是用来定义映射的键(key) , 第二个用来定义值(value), 在这个例子中,键是字符串,值是整数
*/


scala> map("hello") = 1
scala> map("there") = 2

scala> map
res5: scala.collection.mutable.Map[String,Int] = Map(hello -> 1, there -> 2)


scala> map("hello")
res6: Int = 1


```
 映射的常用操作
    
操作											|行为
:-------                                        |:------- 
val nums = Map("i" -> 1, "ii" -> 2)				|创建不可变映射
nums + ("vi" -> 6)								|添加条目(返回Map(i->1, II->2, vi->6)
nums - "ii"										|删除条目(返回Map(i->1))
nums += List("iii" -> 3, "v"->5)				|添加多个条目
nums -- List("i", "ii")							|删除多个条目
nums.size										|返回映射的条目的数量
nums("ii")										|获取指定键的关联值(返回2)
nums.key										|返回键枚举器(返回字符串"i", 和"ii"的Iterator)
nums.keySet										|返回键集
nums.values										|返回值枚举器(返回整数1,2 的Iterator)
nums.isEmpty									|指明映射是否为空(返回false)
import scala.collection.mutable	 				|引用可变集合类型
val words = mutable.Map.empty[String,Int]		|创建空的可变集合
words += ("one"->1)								|添加一条映射
words -= "one"									|若存在映射条目,则删除
words ++= List("one" ->1, "two"->2, "three"->3)	|添加多个映射条目
words --= List("one", "two")					|删除多个对象





 默认的(Default)集和映射
```
/*
工厂方法提供的实现都使用了快速查找算法,通常都涉及哈希表,因此他们能够快速反应对象是否存在于集合中,
如scala.collection.mutable.Set() 工厂方法返回scala.collection.mutable.HashSet,则其在内部使用了哈希表
类似的,scala.collection.mutable.Map() 工厂方法返回了scala.collection.mutable.HashMap


不可变集和映射的情况更为复杂一些,例如:scala.collection.immutable.Set() 工厂方法返回的类,取决于你传递给他的元素, 具体说明参加下表,对于少于5个元素的集,类型完全取决于他的元素数量,以获得最优的性能,然而一旦你请求的集包含了5个元素以上,工厂方法返回的将是不可变的HashSet
*/
```

![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/17/3.png)
 

```
类似的,scala.collection.immutable.Map()工厂方法返回的类取决于传递进去的键值对数量,参见下表,对于少于5个元素的不可变映射,类型完全取决于其键值对数量,以获得最优的性能,但如果包含了5个或以上的键值对,则使用的是不可变的HashMap
```

![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/17/4.png)



 有序的(Sorted) 集和映射
```
/*
有时,可能你需要集或映射的枚举器能够返回那特定顺序排序的元素,为此,scala的集合库提供了SortedSet和SortedMap特质,这两个特质分别有类TreeSet和TreeMap实现,他们都使用了红黑树有序的保存元素(TreeSet类) 或键(TreeMap)类,具体的顺序取决于Ordered特质,集的元素类型或映射的键类型要么混入,要么能够隐式的转换成Ordered的特质,这些类只有不可变类型的版本
*/

scala> import scala.collection.immutable.TreeSet
import scala.collection.immutable.TreeSet

scala> val ts = TreeSet(1,2,3,8,9)
ts: scala.collection.immutable.TreeSet[Int] = TreeSet(1, 2, 3, 8, 9)

scala> val cs = TreeSet('t','u','n')
cs: scala.collection.immutable.TreeSet[Char] = TreeSet(n, t, u)

scala> import scala.collection.immutable.TreeMap
import scala.collection.immutable.TreeMap

scala> val tm = TreeMap(3->'x', 1->'x', 4->'x')
tm: scala.collection.immutable.TreeMap[Int,Char] = Map(1 -> x, 3 -> x, 4 -> x)

scala> tm
res12: scala.collection.immutable.TreeMap[Int,Char] = Map(1 -> x, 3 -> x, 4 -> x)

```
 同步的集和映射
```
/*
我们曾经提到过如果需要线程安全的映射,可以把SynchronizedMap特质混入到你想要的特定类实现中,例如,把SynchronizedMap混入HashMap,
*/

import scala.collection.mutable
import scala.collection.mutable.{HashMap, Map, SynchronizedMap}
object MapMaker {
  def makMap:Map[String,String] = {
    new HashMap[String,String] with SynchronizedMap[String,String] {
      override def default(key: String): String = "why do you want to know?"
    }
  }
}

/*
scala编译器将产生混入了SynchronizedMap的HashMap合成子类,并创建他的返回实例,这个合成子类还重载了名为default的方法

如果你请求映射返回与特定键关联的值,而该键的映射实际不存在,默认你将得到NoSuchElementException,然而如果你定义了新的映射类并重载了default方法,那么这个新的映射将在查询不存在的键时返回default方法的返回值,这里是返回"why do you want to know?"
*/

/*
由于makeMap方法返回的可变映射混入了SynchronizedMap特质,因此可以立即用于多线程环境,每次对映射的访问都被同步操作,下面是单线程访问映射的情况:
*/
val capital = MapMaker.makeMap
capital ++ List("us"->"Washington", "paris"->"France","Japan"->"Tokyo")
capital("Japan")    // Tokyo
capital("New Zealand")    //why do you want to know?


/*
对于同步的Set,同理可以创建SynchronizedSet特质创建同步的HashSet
*/
import scala.collection.mutable
val synchroSet = new mutable.HashSet[Int] with mutable.SynchronizedSet[Int]

/*
对于同步,你也可以考虑使用java.util.concurrent的并发集合,又或者,还可以使用非同步的集合及scala的actor
*/
```


# 4.可变(mutable)集合vs不可变(immutable)集合
```
/*
不可变集合比可变集合更为紧促,节省大量的空间
*/

scala> val people = Set("Nancy", "Jane")
people: scala.collection.immutable.Set[String] = Set(Nancy, Jane)

scala> people += "Bob"
<console>:11: error: value += is not a member of scala.collection.immutable.Set[String]
              people += "Bob"    //因为是val的,所以不能重新赋值
                     ^

scala> var people = Set("Nancy", "Jane")
people: scala.collection.immutable.Set[String] = Set(Nancy, Jane)

scala> people += "Bob"

scala> people
res15: scala.collection.immutable.Set[String] = Set(Nancy, Jane, Bob)

/*
尽管集合是不可变类型的,过程是:首先,创建集合,然后,people将被重新赋值为新集合
经过一系列操作之后,people变量现在指向新的不可变集合,其中包含了添加的字符串"Bob",同样的理念可以应用于以=结尾的方法,而不仅是+=方法,
*/
people -= "Jane"
people ++= List("Tom", "Harry")


/*
如果你想使用可变集合,仅需要引用可变版本的Map即可,这样就可以重写对不可变Map的默认引用
*/

import scala.collection.mutable.Map   //唯一的改变
var capital = Map("Us"->"Washington", "France"->"Paris")
capital += ("Japan"->"Tokyo")


```


# 5.初始化集合
```
/*
最常见的创建和初始化集合的办法是把初始值传递给要用的集合类型的伴生对象的工厂方法,你只需把元素放在伴生对象名后面的括号中,scala编译器就会把它转化为该伴生对象的apply方法调用
*/

scala> List (1,2,3)
res16: List[Int] = List(1, 2, 3)

scala> Set('a','b','c')
res17: scala.collection.immutable.Set[Char] = Set(a, b, c)

scala> import scala.collection.mutable
import scala.collection.mutable

scala> mutable.Map("hi"->2,"there"->5)
res19: scala.collection.mutable.Map[String,Int] = Map(hi -> 2, there -> 5)

scala> Array(1.0, 2.0, 3.0)
res20: Array[Double] = Array(1.0, 2.0, 3.0)

/*
尽管通常都可以让scala编译器从传递给工厂方法的元素推断集合的元素类型,但有些时候或许你会希望指定以不同于编译器所选的类型创建集合,尤其对于可变集合来说更为如此
*/

scala> import scala.collection.mutable
import scala.collection.mutable

scala> val stuff = mutable.Set(42)
stuff: scala.collection.mutable.Set[Int] = Set(42)

scala> stuff += "abcde"
<console>:14: error: type mismatch;
 found   : String("abcde")
 required: Int
              stuff += "abcde"
                       ^

/*
上面的问题在于stuff被指定元素类型为Int,如果想要让他的类型为Any,你需要明确的说明,把元素类型放在方括号中
*/

scala> val stuff = mutable.Set[Any](42)
stuff: scala.collection.mutable.Set[Any] = Set(42)

/*
另一种特殊情况是,你想要把集合初始化为指定类型,例如:设想你要把列表中的元素保存在TreeSet中
*/

scala> val colors = List("blue", "yellow","red")
colors: List[String] = List(blue, yellow, red)

//你不能把colors列表传递给TreeSet工厂方法
scala> import scala.collection.immutable.TreeSet
import scala.collection.immutable.TreeSet

scala> val treeSet = TreeSet(colors)
<console>:14: error: No implicit Ordering defined for List[String].
       val treeSet = TreeSet(colors)
                            ^
//需要创建空的TreeSet[String] 对象并使用TreeSet的++ 操作符把列表元素加入其中
scala> val treeSet = TreeSet[String]() ++ colors
treeSet: scala.collection.immutable.TreeSet[String] = TreeSet(blue, red, yellow)


```

 数组与列表之间的互转
```
/*
如果你需要用集合初始化列表或数组,使用集合初始化列表,只需对集合调用toList方法
*/

scala> treeSet
res22: scala.collection.immutable.TreeSet[String] = TreeSet(blue, red, yellow)

scala> treeSet.toList
res23: List[String] = List(blue, red, yellow)


//或者你需要的是数组
scala> treeSet.toArray
res24: Array[String] = Array(blue, red, yellow)

/*
对TreeSet调用toList产生的列表元素是按照字母顺序排列的,如下
*/.
scala> val test = TreeSet("ff", "bb", "ee", "cc")
test: scala.collection.immutable.TreeSet[String] = TreeSet(bb, cc, ee, ff)

scala> test.toList
res25: List[String] = List(bb, cc, ee, ff)


/*
请牢记:转变为列表或数组同样需要复制集合的所有元素,因此对于大型集合来说可能比较慢,所以toList和toArray对于小的Set转成List或者Array还是可以的
*/


```

 集和映射的可变与不可变互转
```
/*
另一种偶尔发生的情况是:把可变集或映射转换成不可变类型,或者反向转换,
可以先创建空不可变集合,然后把可变集合的元素用++操作符添加进去
*/

scala> import scala.collection.mutable
import scala.collection.mutable

scala> treeSet
res26: scala.collection.immutable.TreeSet[String] = TreeSet(blue, red, yellow)

scala> val mutaSet = mutable.Set.empty ++ treeSet
mutaSet: scala.collection.mutable.Set[String] = Set(red, blue, yellow)

scala> val immutaSet = Set.empty ++ mutaSet
immutaSet: scala.collection.immutable.Set[String] = Set(red, blue, yellow)


/*
使用同样的技巧实现可变映射与不可变映射之间的转换
*/

scala> val muta = mutable.Map('i'->1, "ii"->2)
muta: scala.collection.mutable.Map[Any,Int] = Map(ii -> 2, i -> 1)

scala> val immu = Map.empty ++ muta
immu: scala.collection.immutable.Map[Any,Int] = Map(ii -> 2, i -> 1)

```


# 6.元组
```
/*
元组可以把固定数量的条目组合在一起以便于作为整体传送,不像数组或列表,元组可以保存不同类型的对象,下面是可以作为整体保存整数,字符串,和控制台的元组
*/
(1, "hello", Console)

/*
由于元组可以组合不同类型的对象,因此他不能继承自Iterator,如果你发现自己想要的是把"一个"整数和"一个"字符串组合在一起,那么你需要的就是元组,不是List,也不是Array
*/

//元组常用来返回方法的多个值,如:下面的方法找到集合中的最长单词并返回他的索引
def longestWord(words: Array[String]) = {
  var word = words(0)
  var idx = 0
  for (i <- 1 until words.length)
    if (words(i).length > word.length)
      word = words(i)
      idx = 1
  (word,idx)
}

//使用
scala> val longest = longestWord("the quick brown fox" split(" "))
longest: (String, Int) = (quick,1)


//访问元组的元素
scala> longest._1
res27: String = quick

scala> longest._2
res28: Int = 1


//而且,你可以把元组的每个元素赋值给他自己的变量(这种模式实际上是模式匹配的特例)
scala> val (word, idx) = longest
word: String = quick
idx: Int = 1

scala> val word, idx = longest        //相当于为每个变量赋值
word: (String, Int) = (quick,1)
idx: (String, Int) = (quick,1)
//每个变量被初始化为右侧表达式的单次执行结果


```

