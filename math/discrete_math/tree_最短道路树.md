[TOC]

# 单源最短道路

洪水从S开始流入，图中的边表示到达对应顶点，所需的时间

![image-20190610191240461](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190610191240461.png?raw=true)

假设(G,W)为赋权图，则图中一条道路的长度(length)，是指该道路中各条边权重之和



![image-20190612190609106](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612190609106.png)

**最短道路的一部分也是最短道路**

![image-20190610191957834](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190610191957834.png?raw=true)



>  **d(v)就是我们所说的标记**



![image-20190610192637643](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190610192637643.png?raw=true)

![image-20190610193035185](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190610193035185.png?raw=true)

**其中的w(uv)是标识边的权重**



![image-20190610193608392](https://github.com/chenyansong1/note/blob/master/images/discrete_math/image-20190610193608392.png?raw=true)



最后得到的是一棵树，称之为最短道路树

![image-20190612192015681](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612192015681.png)



注解：

* 该算法解决的是已知起始点最短路径的问题，而已知终点求最短路径的问题与之完全相同
* 该算法也适用于有向图的情况，所得结果构成一棵根树
* 该算法仅仅适合于边全部是正值的情况



原型图形：

https://blog.csdn.net/summer_dew/article/details/81582989



# 程序实现

程序实现：

https://www.jianshu.com/p/ff6db00ad866

```java
public class Dijkstra {
    public static final int M = 10000; // 代表正无穷
    
    public static void main(String[] args) {
        // 二维数组每一行分别是 A、B、C、D、E 各点到其余点的距离, 
        // A -> A 距离为0, 常量M 为正无穷
        int[][] weight1 = {
                {0,4,M,2,M}, 
                {4,0,4,1,M}, 
                {M,4,0,1,3}, 
                {2,1,1,0,7},   
                {M,M,3,7,0} 
            };

        int start = 0;
        
        int[] shortPath = dijkstra(weight1, start);

        for (int i = 0; i < shortPath.length; i++)
            System.out.println("从" + start + "出发到" + i + "的最短距离为：" + shortPath[i]);
    }

    public static int[] dijkstra(int[][] weight, int start) {
        // 接受一个有向图的权重矩阵，和一个起点编号start（从0编号，顶点存在数组中）
        // 返回一个int[] 数组，表示从start到它的最短路径长度
        int n = weight.length; // 顶点个数
        int[] shortPath = new int[n]; // 保存start到其他各点的最短路径
        String[] path = new String[n]; // 保存start到其他各点最短路径的字符串表示
        for (int i = 0; i < n; i++)
            path[i] = new String(start + "-->" + i);
        int[] visited = new int[n]; // 标记当前该顶点的最短路径是否已经求出,1表示已求出

        // 初始化，第一个顶点已经求出
        shortPath[start] = 0;
        visited[start] = 1;

        for (int count = 1; count < n; count++) { // 要加入n-1个顶点
            int k = -1; // 选出一个距离初始顶点start最近的未标记顶点
            int dmin = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (visited[i] == 0 && weight[start][i] < dmin) {
                    dmin = weight[start][i];
                    k = i;
                }
            }

            // 将新选出的顶点标记为已求出最短路径，且到start的最短路径就是dmin
            shortPath[k] = dmin;
            visited[k] = 1;

            // 以k为中间点，修正从start到未访问各点的距离
            for (int i = 0; i < n; i++) {
                //如果 '起始点到当前点距离' + '当前点到某点距离' < '起始点到某点距离', 则更新
                if (visited[i] == 0 && weight[start][k] + weight[k][i] < weight[start][i]) {
                    weight[start][i] = weight[start][k] + weight[k][i];
                    path[i] = path[k] + "-->" + i;
                }
            }
        }
        for (int i = 0; i < n; i++) {
            
            System.out.println("从" + start + "出发到" + i + "的最短路径为：" + path[i]);
        }
        System.out.println("=====================================");
        return shortPath;
    }
    
}
```



