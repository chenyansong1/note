[toc]

转：https://mp.weixin.qq.com/s/w1hgQKibzZq-FA81vvz-9A

参考：https://www.jianshu.com/p/53751f749c4b







**链接视图和执行视图**

![](../..\images\linux\kernel\image-20210630175610157.png)



> 上图左边为链接视图，主要指前面ELF头部的节头表定义的节区分布，它指明了目标代码文件的内容布局。
> 上图右边为执行视图，主要指前面ELF头部的程序头表定义的段区分布，它指明了程序运行时的内存布局。



链接视图由节区组成，执行视图由要由段区组成，我们平时在进行程序构建的时候理解的.text、.bss、.data段，这些都是section，也就节区的概念，这些节区通过section headers table进行组织与重定位。而program headers table则组织了程序运行时的内存布局，它的真实内容由section填充，这里借用网络上的一张图来粗略地表达这层关系：

![](../..\images\linux\kernel\image-20210630181033622.png)



