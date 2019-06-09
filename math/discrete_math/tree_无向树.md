连通且不含任何简单回路的无向图称为无向树，简称树，树中度数为1的顶点称为叶子，度数大于1的顶点称为分枝点

* 由定义可知，树必定是不含重边和自环的，即树一定是简单图
* 不含任何简单回路的图称为森林



![image-20190609161622850](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609161622850.png)



定理1：

设n(n>=2)阶无向连通图G的边数满足m=n-1,则图G中至少存在两个度数为1的顶点

定理2

设T是(n,m)-无向图，则下述命题相互等价

1. T是树，即T连通且不存在简单回路
2. T的每一对相异顶点之间存在唯一的简单道路
3. T不存在简单回路，但在任何两个不相邻的顶点之间加一条新边后得到的图中存在简单回路(也称作"极大无圈")
4. T连通，但是删去任何一边后便不再连通，即T中每一条边都是桥(也称作"极小连通")

![image-20190609162818446](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609162818446.png)



![image-20190609163257359](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609163257359.png)



定理2

设T是(n,m)-无向图，则下述命题等价

1. T是树，即T连通且不存在简单回路
2. T连通且m=n-1
3. T不存在简单回路且m=n-1

![image-20190609163547471](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609163547471.png)



推论

1. 任何非平凡树至少有2个叶子顶点
2. 对于任何无向(n,m)-图，若图中不存在简单回路，则m<=n-1



定理3

无向树都是平面图



定理4

假设无向树T中有$a_i$个度数为i的顶点，则T的叶子树为$\sum_{i=3}(i-2)*a_i + 2$







