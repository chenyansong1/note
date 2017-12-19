---
title:  python进程之进程间通讯——共享内存
categories: python   
toc: true  
tags: [python]
---



# 1. 共享内存简介
* 共享内存：进程间的一种通讯方式，他允许多个进程访问相同的内存，一个进程改变其中的数据后，其他的进程都可以看到数据的变化
* Linux的内存模型：
 1. 每个进程的虚拟内存被分为页（page）
 2. 每个进程维护自己的内存地址到虚拟内存页之间的映射
 3. 实际的数据存在于进程的内存地址上
 4. 每个进程都有自己的地址空间，多个进程的映射还是可以指定相同的页

* 数据可以使用Value或Array类型存储在共享内存映射中，需要导入：from multiprocessing import Process, Value, Array
* Manger()返回的管理者，支持类型包括：list  、dict、Namespance、Lock、RLock、Semaphore、BoundedSemaphore、Condition、Event、Queue、Avalue and Array
 
# 2.Value and Array 帮助

```
In [5]: from multiprocessing import Process,Value,Array                    #导入模块
 
In [6]: help(Value)                                                #Value
Help on function Value in module multiprocessing:
 
Value(typecode_or_type, *args, **kwds)                            #typecode_or_type是指定数据类型，见下文
    Returns a synchronized shared object
 
 
In [7]: help(Array)                                             #Array
Help on function Array in module multiprocessing:
 
Array(typecode_or_type, size_or_initializer, **kwds)
    Returns a synchronized shared array

```

# 3.typecode_or_type数据类型

|Type code|	C Type	|Python Type	|Minimum size in bytes	|Notes|
|-|-|-|-|-|
|'b'	|signed char	|int	|1	       |                          |
|'B'	|unsigned char	|int	|1	       |                      |
|'u'	|Py_UNICODE	Unicode character	|2	|(1)              |
|'h'	|signed short	|int	|2	         |                    |
|'H'	|unsigned short	|int	|2	        |                     |
|'i'	|signed |int	int	|2	           |                      |
|'I'	|unsigned |int	int	|2	          |                   |
|'l'	|signed long|	int	|4	          |                       |
|'L'	|unsigned long	|int	|4	          |                   |
|'q'	|signed long long	|int	|8	|(2)    |                  |
|'Q'	|unsigned long long	|int|	8	|(2)     |                 |
|'f'	|float	|float|	4	|          |                       |
|'d'	|double	|float	|8	    |        |                     |


```
array('l')
array('u', 'hello \u2641')
array('l', [1, 2, 3, 4, 5])
array('d', [1.0, 2.0, 3.14])

```


# 4.Array、Value常用方法
```
#Array
In [8]: ay = Array('i', range(10))                #指定数据类型，创建Array
In [9]: ay
Out[9]: <SynchronizedArray wrapper for <multiprocessing.sharedctypes.c_long_Array_10 object at 0x96f48e4>>
In [10]: ay.                                           #常用的方法
ay.acquire                                           #加锁
ay.get_lock                                          #获取锁
ay.get_obj                                           #获取对象
ay.release                                          #释放锁
 
In [10]: c = ay.get_obj()                       #获取对象      
In [11]: c
Out[11]: <multiprocessing.sharedctypes.c_long_Array_10 at 0x96f48e4>
In [12]: c[:]                                            #打印对象分片
Out[12]: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
 

-------------------------------------------------------------------------------------
#Value
In [13]: value = Value('i',11)
In [14]: value.
value.acquire                  #加锁   
value.get_lock                #获取锁
value.get_obj                  #获取对象
value.release                  #释放锁
value.value                     #获取其中的值
 
In [14]: value.value              #打印值      
Out[14]: 11

```
 

# 5.Value、Array举例
```
[root@backup python]# cat gongxiang.py
#!/usr/bin/python
 
from multiprocessing import Process,Value,Array            #导入相应的模块
import time
import os
 
def child_func(g_value, g_array, ar):
        g_value.value = ar
        g_array[ar] = ar*ar
        print("g_value.value=",g_value.value)
 
listp = []
 
g_value = Value('i',0)                                    #初始化共享类型
g_array = Array('i',range(10))
 
print("init g_value={0},g_array={1}".format(g_value.value,g_array[:]))
 
for i in range(10):
        p = Process(target=child_func, args=(g_value,g_array,i))       #启动子进程，子进程会调用对应的函数，所有的子进程会共享g_value,g_array，因为他们会继承父进程的内存
        p.start()
        listp.append(p)
 
for i in range(10):
        listp[i].join()                                                                #等待子进程关闭
 
print("end vlaue=",g_value.value)
print("end array=",g_array[:])

----------------------------------------------------------------------------
#打印结果
[root@backup python]# python gongxiang.py
init g_value=0,g_array=[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
('g_value.value=', 1)
('g_value.value=', 0)
('g_value.value=', 2)
('g_value.value=', 3)
('g_value.value=', 4)
('g_value.value=', 5)
('g_value.value=', 6)
('g_value.value=', 7)
('g_value.value=', 9)
('g_value.value=', 8)
('end vlaue=', 8)
('end array=', [0, 1, 4, 9, 16, 25, 36, 49, 64, 81])

```
 
  

# 6.Manger查看帮助及常用方法
因为Value和Array中的数据类型是固定的，所以我们引入了Manger的方式来共享内存
```

In [20]: from multiprocessing import Manager
#帮助
In [22]: help(Manager)
Help on function Manager in module multiprocessing:
 
Manager()
    Returns a manager associated with a running server process
 
    The managers methods such as `Lock()`, `Condition()` and `Queue()`
    can be used to create shared objects.


#常用方法
In [23]: ma = Manager() 
#可以获取下面的数据类型
In [25]: ma.
ma.Array                        #数组 
ma.JoinableQueue     
ma.Queue                     #队列
ma.address          
 ma.join              
ma.start
ma.BoundedSemaphore  
ma.Lock              
ma.RLock            
ma.connect           
ma.list                         #列表
ma.Condition         
ma.Namespace         
ma.Semaphore         
ma.dict                       #字典
ma.register         
ma.Event            
ma.Pool              
ma.Value             
ma.get_server        
ma.shutdown         
  
In [26]: dict = ma.dict()            #返回一个字典类型
 
In [27]: dict
Out[27]: <DictProxy object, typeid 'dict' at 0x9795aac>

 
In [28]: dict['name']='zhangsan'            #为字典赋值

In [32]: dict.values()
Out[32]: ['zhangsan']
 
In [34]: dict['name']                           #取出其中的值
Out[34]: 'zhangsan'

```

# 7.Manger举例

```
[root@backup python]# cat manager.py
#!/usr/bin/python
 
from multiprocessing import Process,Value,Array,Manager
import time
import os
 
 
def child_func(g_value,g_array,g_dict,ar):
        g_value.value = ar
        g_array[ar] = ar*ar
        g_dict[ar] = ar+ar
 
listp = []
g_value = Value('i',0)
g_array = Array('i',range(10))
manager = Manager()
g_dict = manager.dict()                                                #返回一个dict类型的字典
 
print("init g_value={0},g_array={1},g_dict={2}".format(g_value.value,g_array[:],g_dict))
 
for i in range(10):
        p = Process(target=child_func, args=(g_value,g_array,g_dict,i))
        p.start()
        listp.append(p)
 
for i in range(10):
        listp[i].join()
 
print("init g_value={0},g_array={1},g_dict={2}".format(g_value.value,g_array[:],g_dict))


-------------------------------------------------------------
#执行结果
[root@backup python]# python manager.py
init g_value=0,g_array=[0, 1, 2, 3, 4, 5, 6, 7, 8, 9],g_dict={}
init g_value=8,g_array=[0, 1, 4, 9, 16, 25, 36, 49, 64, 81],g_dict={0: 0, 1: 2, 2: 4, 3: 6, 4: 8, 5: 10, 6: 12, 7: 14, 8: 16, 9: 18}

```




