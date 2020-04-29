---
title: 第十六章 使用列表
categories: scala   
tags: [scala,scala编程]
---



# 1.列表字面量
```
val fruit = List("apples", "oranges", "pears")
val nums = List(1,2,3,4)
val diag3 = 
  List(
    List(1,0,1),
    List(0,1,0),
    List(0,0,1)
  )
val empty = List()
/*
列表与数组非常相似,不过有两点重要的差别,首先,列表时不可变的,也就是说,不能通过赋值改变列表的元素,其次列表具有递归的结构,而数组是连续的
 */


```

<!--more-->

# 2.list类型
```
/*
列表的所有元素具有相同的类型,元素类型为T的列表类型写成List[T] ,例如:以下四个列表的例子都明确指定了类型:
*/
val fruit: List[String] = List("apples", "oranges", "pears")
val nums: List[Int] = List(1,2,3,4)
val diag3: List[List[Int]] = 
  List(
    List(1,0,1),
    List(0,1,0),
    List(0,0,1)
  )
val empty = List()

/*
scala里的列表类型是协变的,这意味着对于每一对类型S和T来说,如果S是T的子类型,那么List[S] 是List[T]的子类型,比如说,List[String] 是List[Object]的子类型,这很自然,因为每个字符串列表都同样可以被看做是对象列表

注意空列表的类型为List[Nothing] ,Nothing是scala的类层级的底层类型,他是每个scala类型的子类,因为列表时协变的,所以对于任意类型的T的列表List[T]来说,List[Nothing] 都是其子类,因此类型为List[Nothing]的空列表对象,还可以被当做是其他任何形式为List[T] 的列表类型的对象,这也是为何如下的代码是正确的
*/
//List() 同样也是List[String] 的
val xs: List[String] = List()

```

# 3.构造列表
```
/*
所有的列表都是由两个基础构造块Nil和:: 构造出来的,Nil代表空列表,中缀操作符:: ,表示列表从前端扩展,也就是说, x::xs 代表了第一个元素为x ,后面跟着xs的列表,因此之前的列表值可以如下的方式定义:
*/
val fruit = "apple" :: ("orange"::("pears"::Nil))
val nums = 1::(2::(3::(4::Nil)))
val diag3 = (1::(0::(1::Nil)))::
  (0::(1::(0::Nil)))::
  (0::(0::(1::Nil)))::Nil
val empty = Nil

//实际上之前的List(...) 形式是对fruits,nums, diag3 和empty的定义只不过是扩展为这些定义的包装,例如:List(1,2,3)创建了列表 1::(2::(3::Nil))


/*由于以冒号结尾, :: 操作遵循右结合规则:  A::B::C 等同于 A::(B::C) 因此,你可以去掉前面定义里用到的括号,例如:
*/
val nums = 1::2::3::4::Nil

```

# 4.列表的基本操作
```
//对列表的所有操作都可以表达Wie以下三种形式
head     //返回列表的第一个元素
tail        //返回除第一个之外所有元素组成的列表
isEmpty    //如果列表为空,则返回真

//这些操作都定义了List类的方法,如下表:
```

操作			|行为
:---------------:|:------:
empty.isEmpty	|返回true
fruits.isEmpty	|返回false
fruits.head		|返回"apples"
fruits.tail.head|	返回"orange"
diag3.head		|返回List(1,0,0)



```
/*
head和tail方法仅能够作用在非空列表上,如果执行在空列表上,会抛出异常:
*/
scala> Nil.head
java.util.NoSuchElementException: head of empty list


/*
可以考虑把数值列表以升序的方式排序,较简单的做法是插入排序
工作方式如下:排序非空列表x::xs , 可以先排序列表xs,然后把x插入正确的地方,而对空列表的排序结果还是空列表:
插入排序算法大致如下:
*/

def isort(xs: List[Int]): List[Int] = 
  if (xs.isEmpty) Nil
  else insert(xs.head, isort(xs.tail))

def insert(x: Int, xs: List[Int]): List[Int] =
  if (xs.isEmpty || x <= xs.head) x::xs
  else xs.head :: insert(x, xs.tail)

```


# 5.列表模式
```
/*
列表还可以使用模式匹配做拆分,这时列表模式需要逐一匹配要拆分的列表表达式,你既可以用List(...) 形式的模式对列表所有的元素做匹配,也可以用:: 操作符和Nil常量组成的模式逐位拆分列表
*/
scala> val fruit = "apple" :: ("orange"::("pears"::Nil))
fruit: List[String] = List(apple, orange, pears)

scala> val List(a,b,c) = fruit
a: String = apple
b: String = orange
c: String = pears


//如果起先不知道列表元素的数量,那么最好还是使用:: 做匹配,例如, 模式a::b::rest可以匹配长度至少为2的列表
scala> val a::b::rest = fruit
a: String = apple
b: String = orange
rest: List[String] = List(pears)

```
 
List的模式匹配
```
//使用模式匹配实现插入排序
def isort(xs: List[Int]): List[Int] = xs match {
  case List() => List()
  case x::xsl => insert(x, isort(xsl))
}

def insert(x: Int, xs: List[Int]): List[Int] = xs match {
  case List() => List()
  case y::ys => if (x<=y) x::xs
                else y::insert(x,ys)
}

/*
通常,采用模式匹配做拆分会比使用那些基本方法更为清晰,因此你的列表处理工具箱中应该加入模式匹配这样的工具
*/

```

# 6.list类的一阶方法
```
//一阶方法是指不以函数作为传入参数的方法
```

连接列表
```
//连接操作是与 :: 接近的一种操作,写做" ::" 不过不像 ::: 的两个操作都是列表, xs:::ys 的结果依次包含xs和ys所有元素的新列表


scala> List(1,2):::List(3,4,5)
res20: List[Int] = List(1, 2, 3, 4, 5)

scala> List():::List(1,2,3)
res21: List[Int] = List(1, 2, 3)

scala> List(1,2,3):::List(4)
res22: List[Int] = List(1, 2, 3, 4)

//与:: 一样,列表的连接操作也是右结合的,如下:
xs:::ys:::zs
//等价于
xs:::(ys:::zs)

```

 分治原则
```
/*
连接操作(:::) 被实现为List类的方法,下面是通过使用模式匹配来"手工"实现
*/
def append[T](xs: List[T], ys: List[T]): List[T]

/*
列表的许多算法首先使用模式匹配把输入列表拆分为更简单的样本,这是原则里所说的"分",然后根据每个样本构建结果,
如果结果是非空列表,那么一块块部件将通过同样的递归遍历算法构建出来,这就是原则里说的"治"
*/
def append[T](xs: List[T], ys: List[T]): List[T] = xs match {
  case List() => ys
  case x::xsl => x::append(xsl,ys)
}

```


 计算列表的长度:length方法
```
scala> List(1,2,3).length
res23: Int = 3

/*
相对于数组来说,列表的length方法是较费时的操作,为了找到尾部,需要遍历整个列表,因此其花费的时间和列表元素数量成正比,这也是在判断列表是否为空时,应当采用xs.isEmpty方法,而不采用xs.length==0的理由,虽然两种测试的结果一致,但是第二种更加的慢,尤其是列表xs较长的时候
*/

```

 访问列表的尾部:init方法和last方法
```
/*
你已经知道了head和tail基本操作,相应可以获得列表的第一个元素及除了第一个元素之外余下的列表,他们都有成对的操作: last返回(非空)列表的最后一个元素,init返回除了最后一个元素之外余下的列表
*/


scala> val abcde = List('a', 'b', 'c', 'd', 'e')
abcde: List[Char] = List(a, b, c, d, e)

scala> abcde.last
res24: Char = e

scala> abcde.init
init    inits

scala> abcde.init
res25: List[Char] = List(a, b, c, d)

//与head和tail一样的是,对空列表调用这些方法的时候,会抛出异常
scala> List().init
java.lang.UnsupportedOperationException: empty.init


//不一样的是,head和tail运行的时间都是常量,但是init和last需要遍历整个列表以计算结果,因此所耗的时间与列表长度成正比
//组织好数据,以便让所有的访问都集中在列表的头部,而不是尾部
```

 反转列表:reverse方法
```
//如果出于某种原因,某种算法需要频繁的访问列表的尾部,那么可以首先把列表反转过来然后再处理,如下:
scala> abcde
res29: List[Char] = List(a, b, c, d, e)

scala> abcde.reverse
res27: List[Char] = List(e, d, c, b, a)

//与所有其他列表操作一样,reverse创建了新的列表而不是就地改变被操作列表
scala> abcde
res28: List[Char] = List(a, b, c, d, e)

```

 前缀与后缀:drop,take,和splitAt
```
/*
drop和take操作泛化了tail和init,他们可以返回列表任意长度的前缀或后缀,表达式" xs take n " 返回xs列表的前n个元素,如果n大于xs.length,则返回整个xs,操作 " xs drop n"返回xs列表除了前n个元素之外的所有元素,如果n大于xs.length,则返回空列表

splitAt操作在指定位置拆分列表,并返回对偶列表
xs splitAt n  //等价于 ( xs take n, xs drop n)
*/

scala> abcde
res29: List[Char] = List(a, b, c, d, e)

scala> abcde take 2
res30: List[Char] = List(a, b)

scala> abcde drop 2
res31: List[Char] = List(c, d, e)

scala> abcde splitAt 2
res32: (List[Char], List[Char]) = (List(a, b),List(c, d, e))

```
 

元素选择:apply方法和indices方法
```
//apply方法实现了随机元素的元素,不过与数组中的同名方法相比,他使用的并不广泛

scala> abcde
res29: List[Char] = List(a, b, c, d, e)

abcde(2)    // c


//indices 方法可以返回指定列表的所有有效索引值组成的列表
scala> abcde.indices
res35: scala.collection.immutable.Range = Range(0, 1, 2, 3, 4)

```

 齿合列表:zip
```
//zip操作可以把两个列表组成一个对偶列表

scala> abcde.indices zip abcde
res36: scala.collection.immutable.IndexedSeq[(Int, Char)] = Vector((0,a), (1,b), (2,c), (3,d), (4,e))

//如果两个列表的长度不一致,那么任何不能匹配的元素将被丢弃
scala> val zipped = abcde zip List(1,2,3)
zipped: List[(Char, Int)] = List((a,1), (b,2), (c,3))

//常用到的情况是把列表元素与索引值啮合在一起,这是使用zipWithIndex方法更为有效
scala> abcde.zipWithIndex
res37: List[(Char, Int)] = List((a,0), (b,1), (c,2), (d,3), (e,4))

```

 显示列表:toString方法和mkString方法
```
//toString操作返回列表的标准字符串表达形式
scala> abcde.toString
res39: String = List(a, b, c, d, e)

/*
xs mkString (pre, sep, post) 操作有四个操作元,带显示的列表xs ,需要显示在所有元素之前的前缀字符串pre,需要显示在每两个元素之间的分隔符字符串sep,以及显示在最后面的后缀字符串post,操作的结果就是字符串
*/

//mkString方法有两个重载的变体以便让你可以忽略部分乃至全部参数,第一个变体仅带有分隔符字符串:
xs mkString sep  //等价于 xs mkString ("", sep, "")
//第二个变体让你可以忽略所有的参数:
xx.mkString     //等价于 xs mkString ""


scala> abcde mkString("++++", ",", "%%%%")
res40: String = ++++a,b,c,d,e%%%%

scala> abcde mkString ""
res42: String = abcde

scala> abcde.mkString
res44: String = abcde

//mkString方法还有addString的变体,他可以把构建好的字符串添加到StringBuilder对象中,而不是作为结果返回

scala> val buf = new StringBuilder
buf: StringBuilder =

scala> abcde addString (buf,"(",":",")")
res45: StringBuilder = (a:b:c:d:e)

//mkString和addString方法都继承自List的超特质Iterable ,因此他们可以应用到各种可枚举的集合类上

```

转换列表:elements,toArray. copyToArray
```
//要想让数据存储格式在连续存放的数组和递归存放的列表之间进行转换,可以使用List类的toArray方法和Array类的toList方法

scala> abcde
res46: List[Char] = List(a, b, c, d, e)

scala> val arr = abcde.toArray
arr: Array[Char] = Array(a, b, c, d, e)

scala> arr.toList
res47: List[Char] = List(a, b, c, d, e)

//另外还有一个方法叫copyToArray ,可以把列表元素复制到目标数组的一段连续空间,操作如下:
xs copyToArray (arr, start)
/*
这将把列表xs的所有元素复制到数组arr中,填入位置开始为start,必须确保目标数组arr有足够的空间可以全部放下列表元素,如下:
*/
scala> val arr2 = new Array[Int](10)
arr2: Array[Int] = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

scala> List(1,2,3) copyToArray (arr2,3)

scala> arr2
res51: Array[Int] = Array(0, 0, 0, 1, 2, 3, 0, 0, 0, 0)


//如果你需要使用枚举器访问列表元素,可以使用elements方法
```

 
 举例:归并排序
```
//前面看到的插入排序编写起来较为简洁,但是效率不高,归并排序是更为有效的排序算法
/*
归并排序的工作原理:
首先如果列表长度为零或仅有一个元素,他就已经是排好序的了,因此可以不加改变的返回,长列表可以拆分为两个子列表,每个包含大概一半的原列表元素,每个子列表采用对排序函数的递归调用完成排序,然后再用归并操作把生产的两个排好序的列表合并在一起
*/

def msort[T](less: (T, T) => Boolean)(xs: List[T]):List[T] = {
  def merge(xs:List[T], ys:List[T]): List[T] = (xs,ys) match {
    case (Nil,_) => ys
    case (_,Nil) => xs
    case (x::xsl, y::ysl) =>
      if (less(x,y)) x::merge(xsl,ys)
      else y::merge(xs,ysl)
  }
  val n = xs.length/2
  if (n==0) xs
  else {
    val (ys,zs) = xs splitAt n
    merge(msort(less)(ys), msort(less)(zs))
  }
}

```



# 7.list类的高级方法
```
/*
对于列表的许多操作都具有相同的结构,模式重复不断的出现,例如:用某种方式转变列表的所有元素,检查列表的所有元素是否都具有某种特质,取出列表中满足特定条件的元素,或使用某种操作符合并列表的元素,在java中,这样的模式通常需要用for或while循环的固定组合表达,scala中,可以通过使用以List类的方法实现的高阶操作符来更为简洁和直接的表达这些模式
*/

```

列表见映射:map , flatMap, 和foreach
```
xs map f //操作以类型为List[T] 的列表xs和类型为T=>U的函数f为操作单元,返回把函数f应用在xs的每个列表元素之后由此组成的新列表


scala> List(1,2,3) map (_+1)
res0: List[Int] = List(2, 3, 4)

scala> val words = List("the", "quick", "brown", "fox")
words: List[String] = List(the, quick, brown, fox)

scala> words map (_.length)
res1: List[Int] = List(3, 5, 5, 3)

scala> words map (_.toList.reverse.mkString)
res2: List[String] = List(eht, kciuq, nworb, xof)

//flatMap操作符与map类似,不过他的右操作元是能够返回元素列表的函数,他对列表的每个元素调用该方法,然后连接所有方法的结果并返回,map和flatMap的差异如下:

scala> words map (_.toList)
res3: List[List[Char]] = List(List(t, h, e), List(q, u, i, c, k), List(b, r, o,w, n), List(f, o, x))

scala> words flatMap (_.toList)
res4: List[Char] = List(t, h, e, q, u, i, c, k, b, r, o, w, n, f, o, x)

//可以发现map返回的是包含列表的列表,而flatMap返回的是把所有元素列表连接之后的单个列表

scala> List.range(1,5) flatMap (
     i=> List.range(1,i) map (j => (i,j))
     )
res5: List[(Int, Int)] = List((2,1), (3,1), (3,2), (4,1), (4,2), (4,3))

//List.range是可以创建某范围内所有整数列表的工具方法

scala> List.range(1,1)
res6: List[Int] = List()

scala> List.range(1,2)
res7: List[Int] = List(1)


//请注意:使用for表达式也能得到同样的列表
scala> for(i<-List.range(1,5);j<-List.range(1,i)) yield (i,j)
res8: List[(Int, Int)] = List((2,1), (3,1), (3,2), (4,1), (4,2), (4,3))



/*
foreach是第三种与映射类似的操作,然而不像map和flatMap,foreach的右操作元是过程(返回类型为Unit的函数),他只是对每个列表元素都调用一遍过程,操作的结果仍然是Unit,不会产生结果列表
*/
var sum = 0
List(1,2,3,4,5) foreach (sum += _)

```

列表过滤:filter,partition, find, takeWhile, dropWhile和span
```
/*
xs filter p     //操作把类型为List[T]的列表xs和类型为T=>Boolean的判断函数作为操作元,产生xs中符合p(x) 为true的所有元素x组成的列表
*/
scala> List(1,2,3,4,5) filter (_ % 2==0)
res9: List[Int] = List(2, 4)

scala> words
res10: List[String] = List(the, quick, brown, fox)

scala> words filter(_.length == 3)
res11: List[String] = List(the, fox)


/*
partition方法与filter方法类似,不过返回的是列表对,其中一个包含所有论断为真的元素,另外一个包含所有论断为假的元素
*/
xs partition p        //等价于    (xs filter p,    xs filter (!p(_)))    //相当于分组,返回p成立的组,和p不成立的组

scala> List(1,2,3,4,5) partition (_%2==0)
res12: (List[Int], List[Int]) = (List(2, 4),List(1, 3, 5))

xs find p
/*
find方法同样与filter类似,不过返回的是第一个满足给定论断的元素,而并非全部, xs find p 操作以列表xs和论断p为操作元,返回可选值,如果xs中存在元素x使得p(x) 为真, Some(x) 将被返回,若p对所有的元素都不成立,None将返回
*/

scala> List(1,2,3,4,5) find (_ %2 ==0)
res14: Option[Int] = Some(2)

scala> List(1,2,3,4,5) find (_ <= 0)
res15: Option[Int] = None



/*
takeWhile和dropWhile操作符同样带有论断做右操作元,xs takeWhile p 操作返回列表xs 中最长的更够满足p的前缀,类似的,xs dropWhile p 操作符最长的不满足p的前缀
*/

scala> List(1,2,3,-4,5) takeWhile (_ > 0)
res16: List[Int] = List(1, 2, 3)

scala> words
res17: List[String] = List(the, quick, brown, fox)

scala> words dropWhile (_ startsWith "t")
res18: List[String] = List(quick, brown, fox)

/*
span 方法把takeWhile和dropWhile组合成一个操作,就好像splitAt组合了take和drop一样,他返回一对列表
*/
xs span p  // 等价于 (xs takeWhile p,  xs dropWhile p)

scala> List(1,2,3,-4,5) span (_ > 0)
res19: (List[Int], List[Int]) = (List(1, 2, 3),List(-4, 5))


```


列表的论断:forall和exists
```
/*
操作xs forall p 以列表xs和论断p为参数,如果列表的所有元素满足p则返回true,与之相对,在xs exists p操作里,xs 中只要有一个值满足论断p就返回true
*/
def hasZeroRow(m: List[List[Int]]) 
  = m exists(row => row forall (_ == 0))

```

折叠列表: /: 和:\
```
/*
其他常用的操作会对列表元素始终执行某种操作
*/
sum(List(a, b, c))    //等价于 0+a+b+c
//下面可以认为是折叠操作的一个具体实例
def sum(xs: List[Int]): Int = (0 /: xs)(_ + _)

product(List(a, b, c))     //等价于 1*a*b*c
//下面是具体实现:
def produce(xs: List[Int]): Int = (0 /: xs)(_ * _)


/*
左折叠(fold left) 操作 (z /: xs)(op) 与三个对象有关,开始值z , 列表xs , 以及二元操作op,折叠的结果是op应用到前缀值z及每个相邻元素上,如下:
*/
(z /: List(a, b, c))(op)   //等价于 下面的图形操作
```


![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/16/1.png)

 
```
//另一个例子: 用空格连接所有字符串列表中的所有单词
scala> ("" /: words)(_ + " " + _)
res21: String = " the quick brown fox"


//结果在最开始的地方多了一个空格,要去掉他,可以稍微改变为:
scala> (words.head /: words.tail)(_ +" "+ _)
res22: String = the quick brown fox


/*
与上面类似的操作是: :\ ,表示如下:
*/
(List(a, b, c) :\ z)(op)        //等价于 下图
```

![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/16/2.png)
 
```
/*
看冒号在哪一边,与冒号连接时列表,用于连接的字符在左侧就是左折叠,用于连接的字符在右侧就是右折叠
*/

/*
最后,尽管/:和:\操作符已经从斜杠的方向上描绘了他们的操作树的倾斜方向,并且冒号字符的结合性也保证了开始值在操作树和表达式中同样的位置,但还是有很多人感觉产生与直觉相去甚远,因此如果你喜欢,可以使用名为foldLeft和foldRight的方法,他们同样定义在List类中
*/
//foldLeft的方法签名
def foldLeft[B](z: B)(f: (B, A) => B): B = {
```

例子:使用折叠操作完成列表反转
```
/*
通过折叠实现列表反转
*/
def reverseLeft[T](xs: List[T]) = (startValue /: xs)(operation)
//实现如下:
def reverseLeft[T](xs: List[T]) = (List[T]() /: xs){
  (ys,y) => y::ys
}

//或者:下面使用了圆括号
def reverseLeft[T](xs: List[T]) = (List[T]() /: xs)(
  (ys,y) => y::ys
 )

```


列表排序: sort
```
/*
对列表xs的操作, xs sort before 可以对列表的元素执行排序,其中 "before" 是比较元素的方法,表达式 x beforey在x应按照顺序处于y之前的时候要能够返回true 
*/
List(1, -3, 4, 2, 6) sort (_ < _)    //List(-3, 1, 2, 4, 6)
/*
解析:    
fn(x,y) = _ < _ 是一个函数
最后的结果是 x before y 为true    ,所以可以看出是升序
*/
```



# 8.list对象的方法
&emsp;目前为止,上面所看到的所有操作都实现为List类的方法,因此你是在独立的类对象上调用他们,还有些方法是定义在全局可访问对象scala.List上的,他是List类的伴生对象,其中的一些操作是创建列表的工厂方法,另外一些是对某些特定类型列表的操作

通过元素创建列表: List.apply
```
/*
你已经在很多场合看到过以List(1,2,3) 形式出现的列表字面量,他的语法没有什么特别的,类似于List(1,2,3)这样的字面量只是List对象对元素1,2,3的简单应用,也就说,他等价于List.apply(1,2,3)
*/
List.apply(1,2,3)
```

创建数值范围: List.range
```
/*
最简单的形式是List.range(from, until) ,可以创建从from开始到until-1 的所有数值的列表,因此尾部值until不再范围之内
还有一个版本的range可以带step值作为第三参数,这个操作可以产生from开始的,间隔为step的列表元素,step可以为正,也可以为负
*/

scala> List.range(1,5)
res24: List[Int] = List(1, 2, 3, 4)

scala> List.range(1,9,2)
res25: List[Int] = List(1, 3, 5, 7)

scala> List.range(9,1,-3)
res26: List[Int] = List(9, 6, 3)


```

创建统一的列表: List.make
```
/*
make方法创建由相同元素的零份或多份拷贝组成的列表,他带两个参数: 待创建列表的长度,需重复的元素
*/


scala> List.make(5,'a')
warning: there were 1 deprecation warning(s); re-run with -deprecation for detals
res28: List[Char] = List(a, a, a, a, a)

scala> List.make(3,"hello")
warning: there were 1 deprecation warning(s); re-run with -deprecation for detals
res29: List[String] = List(hello, hello, hello)

```


 解除啮合列表:List.unzip
```
/*
unzip操作是zip的相反,zip把两个列表组成对偶列表,unzip把对偶列表拆分还原为两个列表,其中一个列表由每对对偶的第一个元素组成,另一个由第二个元素组成
*/
scala> val zipped = "abcde".toList zip List(1,2,3)
zipped: List[(Char, Int)] = List((a,1), (b,2), (c,3))

scala> List.unzip(zipped)
res31: (List[Char], List[Int]) = (List(a, b, c),List(1, 2, 3))

```

 连接列表: List.flatten, List.concat
```
/*
flatten方法以列表的列表做参数,并把所有的元素列表连接在一起
*/
scala> List.flatten(test)
warning: there were 1 deprecation warning(s); re-run with -deprecation for detals
res32: List[Char] = List(a, b, c, d, e)


//concat方法与flatten类似,它能够连接多个元素列表,将多个列表以重复参数的形式直接传递给方法,数量不限

scala> List.concat(List('a','b'), List('c'))
res33: List[Char] = List(a, b, c)

scala> List.concat(List(),List('b'),List('c'))
res34: List[Char] = List(b, c)

scala> List.concat()
res35: List[Nothing] = List()

```

 映射及测试配对列表: List.map2, List.forall2 , List.exists2
```
// map2方法与map相似,不过他同时带两个列表及能够把两个元素值映射为结果的函数做参数,函数会应用到两个列表相关的元素上,然后把这些结果值变为列表

scala> List.map2(List(10,20),List(7,4,5))(_ * _)    //分别取两个list的参数,然后交给 _*_ 
warning: there were 1 deprecation warning(s); re-run with -deprecation for detals
res37: List[Int] = List(70, 80)

scala> List.forall2(List("abc","de"),List(3,2))(_.length == _)    //分别取两个list的参数,交给 _.length == _
warning: there were 1 deprecation warning(s); re-run with -deprecation for detals
res38: Boolean = true

scala> List.exists2(List("abc","de"),List(3,2))(_.length != _)
warning: there were 1 deprecation warning(s); re-run with -deprecation for detals
res40: Boolean = false

```

