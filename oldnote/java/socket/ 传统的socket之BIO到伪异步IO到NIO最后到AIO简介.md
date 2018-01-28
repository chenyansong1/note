转：http://blog.csdn.net/t1dmzks/article/details/78369590

http://blog.csdn.net/t1dmzks/article/details/78369590





传统的socket处理的逻辑图：




传统的socket的问题:

不支持太多的客户端同时进行连接，如果在服务器端为每个客户端的连接都创建一个线程进行处理，那么服务器端就会有创建线程的压力（即，如果客户端有100个，那么在服务器端就会创建100个线程，但是如果客户端有1w个呢，岂不是要在服务器端创建1w个线程，那么服务器端的压力就会很大）


![](/Users/chenyansong/Documents/note/images/java/nio/1_nio.png)
