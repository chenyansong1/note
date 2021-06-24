

# python debug on vim

转自：https://www.cnblogs.com/hoxis/p/10287903.html

pdb 调试器是 Python 标准库提供的，因此最为方便，不需要安装其他组件，直接 import 后就能使用。

pdb 调试器提供了调试所需的大多数功能，如断点、单行步进、堆栈帧的检查等等。

# 常用命令

```
l # 查看运行到哪行代码 
n # 单步运行，跳过函数 
s # 单步运行，可进入函数 
p 变量 # 查看变量值 
b 行号 # 断点设置到第几行 
b # 显示所有断点列表 
cl 断点号 # 删除某个断点 
cl # 删除所有断点 
c # 跳到下一个断点 
r # return 当前函数 
!var #改变变量的值
exit # 退出
```

# 使用示例

> 本文 Python 环境：Python 3.5.2

我们先准备一小段演示程序：

```python
# -*- coding: utf-8 -*-

def add(a, b):
    return a + b

if __name__ == '__main__':
    print("===start===")
    c = add(1, 3)
    print("===end===")
```

**使用方法 1**：

运行 pdb 的最简单方法是从命令行，将程序作为参数传递来调试。

```bash
$ python -m pdb test_pdb.py
```

这时，就开始单步执行了。

![](../../images\python\20190109161704.png)

这种方法对代码没有侵入性，但是每次都需要设置断点。

**使用方法 2**：

在代码头部引入 pdb，然后可以在代码里，通过 `pdb.set_trace()` 来设置断点：

```python
# -*- coding: utf-8 -*-
import pdb

def add(a, b):
    pdb.set_trace()
    return a + b

if __name__ == '__main__':
        print("===start===")
        pdb.set_trace()
        c = add(1, 3)
        print("===end===")
```

此时，运行程序，就会自动跳转到设置的断点处：

![](../..\images\python\20190109165557.png)



动态改变变量的值 (!var)

```shell
 (Pdb) !b="333333"
 (Pdb)
```





# 总结

没了图形化页面，调试只能这么来了，不过还好，pdb 使用看着不难吧。

其实还有一些增强的调试器，比如 IPython 的 ipdb 和 pdb++，它们一般都提供了更好的用户体验，添加了有用的额外功能，例如语法突出高亮、更好的回溯和自省。
