转：http://blog.csdn.net/t1dmzks/article/details/78369590


传统的BIO通信：同步阻塞模式

![](/Users/chenyansong/Documents/note/images/java/nio/3_nio.png)

传统的模式是BIO模式，是同步阻塞的，直接看下面这个例子吧。 
**Server端**

```
       ServerSocket server = null; //bio, 使用一个ServerSocket类进行socket传输
        int PROT = 9999;
        try {
            server = new ServerSocket(PROT);
            System.out.println(" server start .. ");
            //进行阻塞，
            while (true){
                Socket socket = server.accept();//!!直到client建立socket连接的时候，才会走下面的操作
                System.out.println("client建立scoket连接后才走这里");
                //新建一个线程执行客户端的任务
                new Thread(new ServerHandler(socket)).start();
            }

        //下面的不用看了
        } catch (Exception e)
....


```
ServerHandler处理类,主要用来输出，这里停留5000毫秒

```
in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
out = new PrintWriter(this.socket.getOutputStream(), true);
String body = null;
while((body = in.readLine())!=null){
    System.out.println("Server :" + body);
    Thread.sleep(5000);
    //给客户端响应
    out.println("这是服务器端回送响的应数据.");
}

```

Client端

```
String ADDRESS = "127.0.0.1";
int PORT = 9999;

socket = new Socket(ADDRESS, PORT);
in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
out = new PrintWriter(socket.getOutputStream(), true);

//向服务器端发送数据
out.println("接收到客户端的请求数据...");
System.out.println("执行完这一句后，下面的in.readLine是阻塞的");
//in.readLine();是阻塞的，必须等待服务器端完成之后才能读的到
String response = in.readLine();
System.out.println("Client: " + response);

```


分析NIO的模式， 先运行Server，Server输出


```
server start .. 

```
再运行client，client输出

```
执行完这一句后，下面的in.readLine是阻塞的  

```

Server端输出

```
client建立scoket连接后才走这里
Server :接收到客户端的请求数据...
```
5秒后再运行client输出:

```
Client: 这是服务器端回送响的应数据.

```

总结： Server端的 Socket socket = server.accept(); 会造成阻塞，直到client建立连接的时候才会执行后面的操作 
Client端的： String response = in.readLine(); 也会造成阻塞，直到Server.accept响应后才能执行后面的操作 
而且，每个Client都会在Server端建立一个线程，如果client并发较多的时候，Server服务器会承受不住从而导致瘫痪


不支持太多的客户端同时进行连接，如果在服务器端为每个客户端的连接都创建一个线程进行处理，那么服务器端就会有创建线程的压力（即，如果客户端有100个，那么在服务器端就会创建100个线程，但是如果客户端有1w个呢，岂不是要在服务器端创建1w个线程，那么服务器端的压力就会很大）




**伪异步IO的模式**

![](/Users/chenyansong/Documents/note/images/java/nio/4_nio.png)



在没有实现NIO之前的一种模式 
直接看例子,和上面那个差不错，只不过Server端使用了线程池而已,将客户端的socket封装成一个task任务，这样client并发多的时候，就会通过等待来执行，不会让线程一下子起的太多,下面就只见到记录一下Server端的代码，其他代码和上面的例子一致 

**Server端**

```
int PORT = 9999;
ServerSocket server = null;
BufferedReader in = null;
PrintWriter out = null;
try {
    server = new ServerSocket(PORT);
    System.out.println("server start");
    Socket socket = null; 
    HandlerExecutorPool executorPool = new HandlerExecutorPool(50, 1000);  //其实整体都是一样，只不过这里再加了一个线程池而已
    while(true){
        socket = server.accept();
        executorPool.execute(new ServerHandler(socket));
    }

} catch (Exception e) 

```

**线程池封装类HandlerExecutorPool**

```
public class HandlerExecutorPool {

    private ExecutorService executor;
    public HandlerExecutorPool(int maxPoolSize, int queueSize){
        this.executor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                maxPoolSize, 
                120L, 
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize));
    }

    public void execute(Runnable task){
        this.executor.execute(task);
    }
}

```


**io(BIO)和nio的区别**

本质就是阻塞和非阻塞的区别：

* 阻塞：应用程序在获取网络数据的时候，如果网络传输数据很慢，那么程序就一直等着，直到数据传输完毕为止

* 非阻塞：应用程序直接可以获取已经准备好的数据，无须等待

BIO为同步阻塞形式，NIO为同步非阻塞形式，NIO并没有实现异步，在JDK1.7之后，升级了NIO库，支持异步非阻塞通信模型即NIO2.0(AIO)

**同步和异步**

同步和异步一般是面向操作系统与应用程序对IO操作的层面上来区别的，

* 同步：应用程序会直接参与IO读写操作，并且我们的应用程序会直接阻塞到某一个方法上，直到数据准备就绪，或者采用轮训的策略实时检查数据的就绪状态，如果就绪则获取数据

* 异步：所有的IO读写操作交给操作系统处理，与我们的应用程序没有直接关系，我们程序不需要关心IO读写，当操作系统完成了IO读写操作时，会给我们应用程序发送通知，我们的应用程序直接拿走数据即可

> 同步说的是server服务器端的执行方式
阻塞说的是具体的技术，接收数据的方式,状态（io,nio)

