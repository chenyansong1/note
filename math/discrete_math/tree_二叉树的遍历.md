[TOC]

# 二叉位置树的遍历

## 遍历方式

对一棵树的每个顶点系统的访问一次且仅一次的方式称作树的遍历，有时也称做树的搜索

树的遍历可以根据访问各个顶点的次序对算法进行分类

![image-20190612203106093](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612203106093.png?raw=true)



左子树的访问

根的访问

右子树的访问

按照上面的访问，可以分为9中次序进行访问

![image-20190612203245485](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612203245485.png?raw=true)

根据访问根的次序，将访问分为：

![image-20190612203543104](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612203543104.png?raw=true)



## 前序遍历

* 先访问根
* 然后递归的前序遍历根的左子树
* 最后递归的前序遍历根的右子树
* (也称之为深度优先遍历)

![image-20190612203822621](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612203822621.png?raw=true)

![image-20190612203924707](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612203924707.png?raw=true)

从根向下，能走多深走多深，直到遍历到叶子，然后返回，尝试右侧的道路，所以称之为深度优先遍历

![image-20190612204158566](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612204158566.png?raw=true)



## 中序遍历

* 先递归的中序遍历根的左子树
* 然后访问根
* 最后递归的中序遍历根的右子树

![image-20190612204335437](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612204335437.png?raw=true)

![image-20190612204428256](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612204428256.png?raw=true)

我们像下图一样投影之后，产生的就是一个中序遍历

![image-20190612204546016](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612204546016.png?raw=true)

## 后序遍历

![image-20190612204809379](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612204809379.png?raw=true)

![image-20190612204822998](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612204822998.png?raw=true)

![image-20190612204845092](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612204845092.png?raw=true)



## 算法实现

![image-20190612205014561](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190612205014561.png?raw=true)





前序遍历的结果：前缀表示或波兰式

中序遍历的结果：中缀表示

后序遍历的结果：后缀表示或逆波兰式



# 标号树

## 定义

对数中的顶点和/或边进行"标号"

![](E:\git-workspace\note\images\discrete_math\biaohaoshu.png)

如下是对定点进行标号

![](E:\git-workspace\note\images\discrete_math\biaohaoshu2.png)



## 标号树的应用



![](E:\git-workspace\note\images\discrete_math\biaohaoshu3.png)

![](E:\git-workspace\note\images\discrete_math\biaohaoshu4.png)

![](E:\git-workspace\note\images\discrete_math\biaohaoshu6.png)

![](E:\git-workspace\note\images\discrete_math\biaohaoshu7.png)



对应的中序遍历，后序遍历，也可以画出来

![](E:\git-workspace\note\images\discrete_math\biaohaoshu8.png)

![](E:\git-workspace\note\images\discrete_math\biaohaoshu9.png)

![](E:\git-workspace\note\images\discrete_math\biaohaoshu10.png)

![](E:\git-workspace\note\images\discrete_math\biaohaoshu11.png)

## 

![](E:\git-workspace\note\images\discrete_math\biaohaoshu11.png)

## 波兰式和逆波兰式

波兰式：将前序遍历的结果依次入栈，然后遇到两个相邻的数字就取出两个数字和一个符号，进行运算，将运算的结果再入栈，如果不满足上述条件就一次对前序遍历结果入栈，最后栈中的数字就是运算的结果

![](E:\git-workspace\note\images\discrete_math\biaohaoshu12.png)

