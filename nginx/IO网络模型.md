



# 传统IO模型



![image-20181119211841954](/Users/chenyansong/Documents/note/images/nginx/io_1.png)



-  当用户进程调用了recvfrom这个系统调用，kernel就开始了IO的第一个阶段：准备数据。对于network io来说，很多时候数据在一开始还没有到达（比如，还没有收到一个完整的UDP包），这个时候kernel就要等待足够的数据到来。而在用户进程这边，整个进程会被阻塞。当kernel一直等到数据准备好了，它就会将数据从kernel中拷贝到用户内存，然后kernel返回结果，用户进程才解除block的状态，重新运行起来。
- 所以，blocking IO的特点就是在IO执行的两个阶段（等待数据和拷贝数据两个阶段）都被block了。



# 多路复用

- 实际上，除非特别指定，几乎所有的IO接口 ( 包括socket接口 ) 都是阻塞型的。这给网络编程带来了一个很大的问题，如在调用send()的同时，线程将被阻塞，在此期间，线程将无法执行任何运算或响应任何的网络请求。
- ​    一个简单的改进方案是在服务器端使用多线程（或多进程）。多线程（或多进程）的目的是让**每个连接都拥有独立的线程（或进程），这样任何一个连接的阻塞都不会影响其他的连接**。具体使用多进程还是多线程，并没有一个特定的模式。传统意义上，进程的开销要远远大于线程，所以如果需要同时为较多的客户机提供服务，则不推荐使用多进程；如果单个服务执行体需要消耗较多的CPU资源，譬如需要进行大规模或长时间的数据运算或文件访问，则进程较为安全。



![image-20181119220459022](/Users/chenyansong/Documents/note/images/nginx/io_2.png)

![image-20181119220526879](/Users/chenyansong/Documents/note/images/nginx/io_3.png)



# 




https://blog.csdn.net/yyxyong/article/details/62894064

http://blog.chinaunix.net/uid-28458801-id-4464639.html

http://blog.jobbole.com/104638/

https://blog.csdn.net/qq_36675830/article/details/79283113
