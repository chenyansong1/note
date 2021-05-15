[toc]

# collections.namedtuple



## 一、绪论

collections 作为 Python 的内建集合模块，实现了许多十分高效的特殊容器数据类型，即除了 Python 通用内置容器： dict、list、set 和 tuple 等的替代方案。在 IDLE 输入 help(collections) 可查看帮助文档，其中常见的类/函数如下：

```
名称	功能
namedtuple	用于创建具有命名字段的 tuple 子类的 factory 函数 (具名元组)
deque	类似 list 的容器，两端都能实现快速 append 和 pop (双端队列)
ChainMap	类似 dict 的类，用于创建多个映射的单视图
Counter	用于计算 hashable 对象的 dict 子类 (可哈希对象计数)
OrderedDict	记住元素添加顺序的 dict 子类 (有序字典)
defaultdict	dict 子类调用 factory 函数来提供缺失值
UserDict	包装 dict 对象以便于 dict 的子类化
UserList	包装 list 对象以便于 list 的子类化
UserString	包装 string 对象以便于 string 的子类化
而本文详述的对象为具名/命名元组 —— namedtuple 。
```

 

## 二、namedtuple 函数

**Python 内建普通元组 tuple 存在一个局限，即不能为 tuple 中的元素命名，故 tuple 所要表达的意义并不明显。**

因此，引入一工厂函数 (factory function) collections.namedtuple，以构造一个带字段名的 tuple。具名元组 namedtuple 的实例和普通元组 tuple 消耗的内存一样多 (因为字段名都被保存在对应的类中) 但却更具可读性 (namedtuple 使 tuple 变成自文档，根据字段名很容易理解用途)，令代码更易维护；同时，namedtuple 不用命名空间字典(namespace dictionary) __dict__ 来存放/维护实例属性，故比普通 dict 更加轻量和快速。但注意，具名元组 namedtuple 继承自 tuple ，其中的属性均不可变。

2.1 说明
collections.namedtuple(typename, field_names, *, verbose=False, rename=False, module=None)

namedtuple，顾名思义是已具命名的元组 (简称具名元组)，它返回一个 tuple 子类。

**其中，namedtuple 名称为参数 typename，各字段名称为参数 field_names。**

其中，field_names 既可以是一个类似 ['x', 'y'] 的字符串序列 (string-seq)，也可以是用空格或逗号分隔开的纯字符串 string，如 'x y' 或 'x, y'。任何 Python 的有效标识符都可作为字段名。所谓有效标识符由字母，数字，下划线组成，但首字母不能是数字或下划线，且不能与 Python 关键词重复，如 class, for, return 等。

具名元组 namedtuple 向后兼容普通 tuple，从而既可通过 field_names 获取元素值/字段值，也能通过索引和迭代获取元素值/字段值。

```python

>>> from collections import namedtuple
 
# Point = namedtuple("Point", 'x, y')  # 等价的初始化方式
# Point = namedtuple("Point", 'x y')   # 等价的初始化方式
>>> Point = namedtuple("Point", ['x', 'y'])  # 初始化一个具名元组 Point
>>> Point
<class '__main__.Point'>
# -------------------------------------------------------------------------
>>> p1 = Point(2, 3)  # 实例化一个具名元组 Point 对象 p1
>>> p1                # 可读 (readable __repr__ with a name=value style)
Point(x=2, y=3)
# -------------------------------------------------------------------------
>>> p1.x   # 通过字段名获取元素值/字段值 (fields also accessible by name)
2
>>> p1[0]  # 通过索引获取元素值/字段值 (indexable like the plain tuple (2, 3))
2
>>> for i in p1:  # 通过迭代获取元素值/字段值
	print(i)
2
3
# -------------------------------------------------------------------------
>>> a, b = p1  # 能够像普通 tuple 一样解绑 (unpack like a regular tuple)
>>> a, b

```





# 另一种解释

https://baijiahao.baidu.com/s?id=1613589944704758634

Python中的tuple大家应该都非常熟悉了。它可以存储一个Python对象序列。与list不同的是，你不能改变tuple中元素的值。tuple的元素是通过索引进行访问的：

![](..\..\images\python\python-namedtuple1.png)

Tuple还有一个兄弟，叫namedtuple。虽然都是tuple，但是功能更为强大。对于namedtuple，你不必再通过索引值进行访问，你可以把它看做一个字典通过名字进行访问，只不过其中的值是不能改变的。

![](..\..\images\python\namedtuple2.png)

为了构造一个namedtuple需要两个参数，分别是tuple的名字和其中域的名字。比如在上例中，tuple的名字是“Animal”，它包括三个域，分别是“name”、“age”和“type”。

Namedtuple比普通tuple具有更好的可读性，可以使代码更易于维护。同时与字典相比，又更加的轻量和高效。但是有一点需要注意，就是namedtuple中的属性都是不可变的。任何尝试改变其属性值的操作都是非法的。

![](..\..\images\python\namedtuple3.png)

Namedtuple还有一个非常好的一点是，它与tuple是完全兼容的。也就是说，我们依然可以用索引去访问一个namedtuple。

![](..\..\images\python\namedtuple4.png)


