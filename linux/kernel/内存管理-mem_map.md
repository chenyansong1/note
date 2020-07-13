[toc]

内存管理-mem_map



页框的状态信息保存在一个类型为page的页描述符中，所有的页描述符存放在mem_map数组中，因为每个描述符长度为32字节，所以mem_map所需要的空间略小于这个RAM的1% .

```c
//线性地址addr对应的页描述符地址
virt_to_page(addr)
    
//与页框号pfn对应的页描述符地址
pfn_to_page(pfn)
```







![](../../images/linux/kernel/mm_global.png)





参考：

https://blog.csdn.net/qq_33620667/article/details/70474298



