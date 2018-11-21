[TOC]



nginx实现：

* 负载均衡（其实就是反向代理之后，请求分发）
* 页面缓存
* epoll模型





![image-20181120221945890](/Users/chenyansong/Documents/note/images/nginx/instruction_1.png)



![image-20181120222200586](/Users/chenyansong/Documents/note/images/nginx/instruction_2.png)

![image-20181120223322191](/Users/chenyansong/Documents/note/images/nginx/instruction_3.png)





* HTTP协议:80/tcp HyperText Transfer Procotol

* html : HyperText Mark Language

* MIME: 在1.0之前仅能够传输文本，在1.0之后，实现了MIME(Multipurpose Internet Mail Extension),能够支持多媒体

  major/minor:通过这个知道文本的格式，从而调用对应的应用程序解析文本

  ​	text/plain

  ​	image/jpeg

* URL: scheme://server[:port]/path/to/source

* http事物：request <——> response

  * request
    * <method> URL <version>
    * <headers>
    * <body>  不是所有的请求都有body
  * response
    * <version> <status 响应状态码> <reason phrase>
    * <headers>
    * 空行
    * <body>

  * method
    * GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS
  * status
    * 1xx:信息提示
    * 2xx:成功响应类
    * 3xx:重定向，301，302，304
    * 4xx:客户端错误类,401,403,404
    * 5xx:服务器端错误，500，502
  * header
    * 通用首部：请求，响应都可以使用
    * 请求首部：
      * if-Modified-Since
      * If-None-Match
    * 响应首部
    * 实体首部：body中使用
    * 扩展首部：未必是标准的
  * web页面
    * 浏览器会缓存资源
    * 浏览器多线程请求资源，每一个线程对应一个连接
  * 资源映射
    * Alias
    * DocumentRoot
  * httpd:MPM，多路处理模块
    * prefork: 主进程，生成多个子进程，每个子进程处理一个请求
    * worker：主进程，生成多个子进程，每个子进程生成多个线程，每个线程响应一个请求
    * event：主进程，生成多个子进程，每个子进程响应多个请求

* IO类型：

  * 同步和异步 **关注的是被调用者**

    * 关注的是消息通知机制
    * 同步：调用发出之后，不会立即返回，但是一旦返回，则返回的是最终的结果数据
    * 异步：调用发出之后，被调用方立即返回消息，单返回的并非最终结果，被调用者通过状态，通知机制等来通知调用者，或通过回调函数来处理结果

  * 阻塞和非阻塞 **关注的是调用者**

    * 关注的是**调用者**等待被调用者返回调用结果时的状态
    * 阻塞：调用结果返回之前，调用者会被挂起，调用者只有在得到返回结果之后，才能继续
    * 非阻塞：调用者在结果返回之前，不会被挂起，即：调用不会阻塞调用者

  * IO模型

    * Blocking IO : 阻塞式IO
    * nonblocking IO ： 非阻塞式IO
    * IO multiplexing：复用型IO
    * signal driven IO：事件驱动式IO
    * asynchronous IO：异步IO


  一次read读磁盘操作，用户空间的进程会发起一次系统(kenel)调用，让内核去加载磁盘数据到内核空间的内存中，然后又将内核空间的数据拷贝到用户空间的属于该进程的内存中

  * 多路(复用型)IO
    * select (systemV)
    * poll (BSD风格)
    * 阻塞在select上
  * 事件驱动
    * 在等待内核的数据的时候不是阻塞的，但是当数据加载完毕，内核通知用户进程，此时用户进程调用内核拷贝数据到用户空间的时候，此时是用户进程还是阻塞
    * 当内核的数据完成之后，之后需要通知用户空间的进程，如果此时用户空间的进程需要正在因为内核拷贝数据到用户空间的阶段，此时进程就接收不到内核的另外的通知(如另一个数据已经读取完毕)，这里就涉及到一个通知机制：
      * 水平触发：多次通知，知道处理
      * 边缘触发：只通知一次，用户进程回调