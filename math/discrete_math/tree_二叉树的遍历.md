[TOC]

# 二叉位置树的遍历

## 遍历方式

对一棵树的每个顶点系统的访问一次且仅一次的方式称作树的遍历，有时也称做树的搜索

树的遍历可以根据访问各个顶点的次序对算法进行分类

![image-20190612203106093](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612203106093.png)



左子树的访问

根的访问

右子树的访问

按照上面的访问，可以分为9中次序进行访问

![image-20190612203245485](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612203245485.png)

根据访问根的次序，将访问分为：

![image-20190612203543104](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612203543104.png)



## 前序遍历

* 先访问根
* 然后递归的前序遍历根的左子树
* 最后递归的前序遍历根的右子树
* (也称之为深度优先遍历)

![image-20190612203822621](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612203822621.png)

![image-20190612203924707](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612203924707.png)

从根向下，能走多深走多深，直到遍历到叶子，然后返回，尝试右侧的道路，所以称之为深度优先遍历

![image-20190612204158566](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612204158566.png)



## 中序遍历

* 先递归的中序遍历根的左子树
* 然后访问根
* 最后递归的中序遍历根的右子树

![image-20190612204335437](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612204335437.png)

![image-20190612204428256](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612204428256.png)

我们像下图一样投影之后，产生的就是一个中序遍历

![image-20190612204546016](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612204546016.png)

## 后序遍历

![image-20190612204809379](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612204809379.png)

![image-20190612204822998](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612204822998.png)

![image-20190612204845092](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612204845092.png)



## 算法实现

![image-20190612205014561](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612205014561.png)





前序遍历的结果：前缀表示或波兰式

中序遍历的结果：中缀表示

后序遍历的结果：后缀表示或逆波兰式



# 标号树

对数中的顶点和/或边进行"标号"