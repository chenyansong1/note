[toc]



转：http://www.wowotech.net/kernel_synchronization/per-cpu.html



https://blog.csdn.net/yldfree/article/details/85000275

在SMP中，为了防止多个CPU之间的抢占，在每个CPU内部都维护了一些变量，把这些变量称为per-CPU变量

通常在程序中定义的全局变量，编译后这些变量位于执行文件的数据区，可执行文件加载后，这些变量被复制到对应的内存中，源程序中的变量名用于对变量进行寻址，**要求根据CPU的个数，在内存中生成多份拷贝，并且能够根据变量名和CPU编号，正确的对各个CPU的变量进行寻址**

![](../../images/linux/kernel/per_cpu.png)















