---
title:  python之生成器(generator)详解(转)
categories: python   
toc: true  
tags: [python]
---




# 引文
编程派前几天推送了一篇文章,叫“Python学习进阶路线(简版)”,生成器(generator)赫然在列.可是我不太会.不会怎么办?学咯.于是上网看了不少教程,又看了官方文档,学到了不少知识.在此,权且做个学习笔记,也与大家分享一下.


# 正文

要理解generator,我们先从迭代(iteration)与迭代器(iterator)讲起.当然,本文的重点是generator,iteration与iterator的知识将点到即止.直接看generator

迭代是重复反馈过程的活动，其目的通常是为了接近并到达所需的目标或结果。每一次对过程的重复被称为一次“迭代”，而每一次迭代得到的结果会被用来作为下一次迭代的初始值。

以上是维基百科对迭代的定义.在python中,迭代通常是通过for ... in ...来完成的,而且只要是可迭代对象(iterable),都能进行迭代.这里简单讲下iterable与iterator:

iterable是实现了__iter__()方法的对象.更确切的说,是container.__iter__()方法,该方法返回的是的一个iterator对象,因此iterable是你可以从其获得iterator的对象.使用iterable时,将一次性返回所有结果,都存放在内存中,并且这些值都能重复使用.以上说法严重错误!对于iterable,我们该关注的是,它是一个能一次返回一个成员的对象(iterable is an object capable of returning its members one at a time),一些iterable将所有值都存储在内存中,比如list,而另一些并不是这样,比如我们下面将讲到的iterator.

iterator是实现了iterator.__iter__()和iterator.__next__()方法的对象.iterator.__iter__()方法返回的是iterator对象本身.根据官方的说法,正是这个方法,实现了for ... in ...语句.而iterator.__next__()是iterator区别于iterable的关键了,它允许我们显式地获取一个元素.当调用next()方法时,实际上产生了2个操作:

1. 更新iterator状态,令其指向后一项,以便下一次调用
2. 返回当前结果


如果你学过C++,它其实跟指针的概念很像(如果你还学过链表的话,或许能更好地理解).

正是__next__(),使得iterator能在每次被调用时,返回一个单一的值(有些教程里,称为一边循环,一边计算,我觉得这个说法不是太准确.但如果这样的说法有助于你的理解,我建议你就这样记),从而极大的节省了内存资源.另一点需要格外注意的是,iterator是消耗型的,即每一个值被使用过后,就消失了.因此,你可以将以上的操作2理解成pop.对iterator进行遍历之后,其就变成了一个空的容器了,但不等于None哦.因此,若要重复使用iterator,利用list()方法将其结果保存起来是一个不错的选择.

我们通过代码来感受一下.
```
>>> from collections import Iterable, Iterator
>>> a = [1,2,3]   # 众所周知,list是一个iterable
>>> b = iter(a)   # 通过iter()方法,得到iterator,iter()实际上调用了__iter__(),此后不再多说
>>> isinstance(a, Iterable)
True
>>> isinstance(a, Iterator)
False
>>> isinstance(b, Iterable)
True
>>> isinstance(b, Iterator)
True
# 可见,itertor一定是iterable,但iterable不一定是itertor
 
# iterator是消耗型的,用一次少一次.对iterator进行变量,iterator就空了!
>>> c = list(b)
>>> c
[1, 2, 3]
>>> d = list(b)
>>> d
[]
 
 
# 空的iterator并不等于None.
>>> if b:
...   print(1)
...
1
>>> if b == None:
...   print(1)
...
 
# 再来感受一下next()
>>> e = iter(a)
>>> next(e)     #next()实际调用了__next__()方法,此后不再多说
1
>>> next(e)
2
```

既然提到了for ... in ...语句,我们再来简单讲下其工作原理吧,或许能帮助理解以上所讲的内容.

```
>>> x = [1, 2, 3]
>>> for i in x:
...     ...
```

我们对一个iterable用for ... in ...进行迭代时,实际是先通过调用iter()方法得到一个iterator,假设叫做X.然后循环地调用X的next()方法取得每一次的值,直到iterator为空,返回的StopIteration作为循环结束的标志.for ... in ... 会自动处理StopIteration异常,从而避免了抛出异常而使程序中断.如图所示

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/generator/1.png)

磨刀不误砍柴工,有了前面的知识,我们再来理解generator与yield将会事半功倍.
 
首先先理清几个概念:

```
generator: A function which returns a generator iterator. It looks like a normal function except that it contains yield expressions for producing a series of values usable in a for-loop or that can be retrieved one at a time with the next() function.
generator iterator: An object created by a generator funcion.
generator expression: An expression that returns an iterator.

```

以上的定义均来自python官方文档.可见,我们常说的生成器,就是带有yield的函数,而generator iterator则是generator function的返回值,即一个generator对象,而形如(elem for elem in [1, 2, 3])的表达式,称为generator expression,实际使用与generator无异.

```
>>> a = (elem for elem in [1, 2, 3])
>>> a
<generator object <genexpr> at 0x7f0d23888048>
>>> def fib():
...     a, b = 0, 1
...     while True:
...         yield b
...         a, b = b, a + b
...
>>> fib
<function fib at 0x7f0d238796a8>
>>> b = fib()
<generator object fib at 0x7f0d20bbfea0>
```
 

其实说白了,generator就是iterator的一种,以更优雅的方式实现的iterator.官方的说法是:
```
Python’s generators provide a convenient way to implement the iterator protocol.

```

你完全可以像使用iterator一样使用generator,当然除了定义.定义一个iterator,你需要分别实现__iter__()方法和__next__()方法,但generator只需要一个小小的yield(好吧,generator expression的使用比较简单,就不展开讲了.)
 
前文讲到iterator通过__next__()方法实现了每次调用,返回一个单一值的功能.而yield就是实现generator的__next__()方法的关键!先来看一个最简单的例子:

```
>>> def g():
...     print("1 is")
...     yield 1
...     print("2 is")
...     yield 2
...     print("3 is")
...     yield 3
...
>>> z = g()
>>> z
<generator object g at 0x7f0d2387c8b8>
>>> next(z)
1 is
1
>>> next(z)
2 is
2
>>> next(z)
3 is
3
>>> next(z)
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
StopIteration
```


第一次调用next()方法时,函数似乎执行到yield 1,就暂停了.然后再次调用next()时,函数从yield 1之后开始执行的,并再次暂停.第三次调用next(),从第二次暂停的地方开始执行.第四次,抛出StopIteration异常.
 
事实上,generator确实在遇到yield之后暂停了,确切点说,是先返回了yield表达式的值,再暂停的.当再次调用next()时,从先前暂停的地方开始执行,直到遇到下一个yield.这与上文介绍的对iterator调用next()方法,执行原理一般无二.
 
有些教程里说generator保存的是算法,而我觉得用中断服务子程序来描述generator或许能更好理解,这样你就能将yield理解成一个中断服务子程序的断点,没错,是中断服务子程序的断点.我们每次对一个generator对象调用next()时,函数内部代码执行到”断点”yield,然后返回这一部分的结果,并保存上下文环境,”中断”返回.
 
怎么样,是不是瞬间就明白了yield的用法?
 
我们再来看另一段代码.

```
>>> def gen():
...     while True:
...         s = yield
...         print(s)
...
>>> g = gen()
>>> g.send("kissg")
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
TypeError: can't send non-None value to a just-started generator
>>> next(g)
>>> g.send("kissg")
kissg
```

我正是看到这个形式的generator,懵了,才想要深入学习generator与yield的.结合以上的知识,我再告诉你,generator其实有第2种调用方法(恢复执行),即通过send(value)方法将value作为yield表达式的当前值,你可以用该值再对其他变量进行赋值,这一段代码就很好理解了.当我们调用send(value)方法时,generator正由于yield的缘故被暂停了.此时,send(value)方法传入的值作为yield表达式的值,函数中又将该值赋给了变量s,然后print函数打印s,循环再遇到yield,暂停返回.
 
调用send(value)时要注意,要确保,generator是在yield处被暂停了,如此才能向yield表达式传值,否则将会报错(如上所示),可通过next()方法或send(None)使generator执行到yield.
 
再来看一段yield更复杂的用法,或许能加深你对generator的next()与send(value)的理解.

```
>>> def echo(value=None):
...   while 1:
...     value = (yield value)
...     print("The value is", value)
...     if value:
...       value += 1
...
>>> g = echo(1)
>>> next(g)
1
>>> g.send(2)
The value is 2
3
>>> g.send(5)
The value is 5
6
>>> next(g)
The value is None
```

上述代码既有yield value的形式,又有value = yield形式,看起来有点复杂.但以yield分离代码进行解读,就不太难了.第一次调用next()方法,执行到yield value表达式,保存上下文环境暂停返回1.第二次调用send(value)方法,从value = yield开始,打印,再次遇到yield value暂停返回.后续的调用send(value)或next()都不外如是.
 
但是,这里就引出了另一个问题,yield作为一个暂停恢复的点,代码从yield处恢复,又在下一个yield处暂停.可见,在一次next()(非首次)或send(value)调用过程中,实际上存在2个yield,一个作为恢复点的yield与一个作为暂停点的yield.因此,也就有2个yield表达式.send(value)方法是将值传给恢复点yield;调用next()表达式的值时,其恢复点yield的值总是为None,而将暂停点的yield表达式的值返回.为方便记忆,你可以将此处的恢复点记作当前的(current),而将暂停点记作下一次的(next),这样就与next()方法匹配起来啦.
 
generator还实现了另外两个方法throw(type[, value[, traceback]])与close().前者用于抛出异常,后者用于关闭generator.不过这2个方法似乎很少被直接用到,本文就不再多说了,有兴趣的同学请看这里


# 小结

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/generator/2.png)

1. 可迭代对象(Iterable)是实现了__iter__()方法的对象,通过调用iter()方法可以获得一个迭代器(Iterator)
2. 迭代器(Iterator)是实现了__iter__()和__next__()的对象
3. for ... in ...的迭代,实际是将可迭代对象转换成迭代器,再重复调用next()方法实现的
4. 生成器(generator)是一个特殊的迭代器,它的实现更简单优雅.
5. yield是生成器实现__next__()方法的关键.它作为生成器执行的暂停恢复点,可以对yield表达式进行赋值,也可以将yield表达式的值返回.








转自:
[python之生成器详解](http://kissg.me/2016/04/09/python-generator-yield/)



