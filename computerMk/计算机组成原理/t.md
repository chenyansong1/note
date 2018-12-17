[TOC]

# 冯诺依曼计算机的特点

1. 计算机由五大部件组成
2. 指令和数据以同等地位存于存储器，可按地址寻访
3. 指令和数据用二进制表示
4. 指令由操作码和地址组成
5. 存储程序
6. 以运算器为中心



# 冯诺依曼计算机硬件框图



![image-20181211215014690](/Users/chenyansong/Documents/note/images/computermk/fnym.png)

# 冯诺依曼计算机的改进

由上面的图可以知道，冯诺依曼计算机的瓶颈在运算器（所有的东西都要经过运算器）

![image-20181211215717265](/E:/git-workspace/note/images/computermk/fnym2.png)



# 现代计算机的硬件框图



![image-20181211220039892](/E:/git-workspace/note/images/computermk/fnym3.png)



![image-20181211220144739](/E:/git-workspace/note/images/computermk/xdjsj.png)



# 编程举例

计算

$$
ax^2+bx+c
$$



**实现的步骤如下：**

* 取x至运算器中
* 乘以x在运算器中
* 乘以a在运算器中
* 存ax^2在存储器中
* 取b至运算器中
* 乘以x在运算器中
* 加$ax^2$在运算器中
* 加c在运算器中 



将上面的式子变形
$$
(ax+b)x+c
$$
**实现的步骤如下：**

* 取x至运算器中
* 乘以a在运算器中
* 加b在运算器中
* 乘以x在运算器中
* 加c在运算器中





# 指令格式举例

指令是由操作码和数据的地址组成的，**操作码和数据的地址都是二进制的**

![1544576807059](E:\git-workspace\note\images\computermk\zhilinggeshi.png)



计算下面表达式的程序清单
$$
ax^2+bx+c
$$

![1544577037710](E:\git-workspace\note\images\computermk\zhilinchengxuqingdan.png)



# 存储器



![1544661870559](E:\git-workspace\note\images\computermk\cunchuqi.png)



# 运算器

![1544661930049](E:\git-workspace\note\images\computermk\yunsuanqi.png)



* 加法的过程

![1544662062108](E:\git-workspace\note\images\computermk\yunsuanqi_jiafa.png)



* 减法的过程

![1544662152829](E:\git-workspace\note\images\computermk\yunsuanqi_jianfa.png)

* 乘法的过程

![1544662204035](E:\git-workspace\note\images\computermk\yunsuanqi_chengfa.png)

* 除法的过程

![1544662238740](E:\git-workspace\note\images\computermk\yunsuanqi_chufa.png)



# 控制器



![1545006714309](E:\git-workspace\note\images\computermk\cunchuqi_1.png)



* 主机完成一条指令的过程

**以取数指令为例**

![1545006863078](E:\git-workspace\note\images\computermk\cunchuqi_2.png)

**以存数指令为例**









# 其他blog

https://www.jianshu.com/p/7355584679d3



https://blog.csdn.net/Mark__Zeng/article/details/39966731



