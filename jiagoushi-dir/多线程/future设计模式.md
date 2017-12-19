Future模式有点类似于我们发送Ajax请求的时候，页面是异步的进行后台处理，用户无须一直等待请求的结果，可以继续浏览或者操作其他内容

![](/Users/chenyansong/Documents/note/images/multiThread/future.png)

上图的流程是这样的：
* 客户端有一个数据查询请求，会调用call
* 客户端会对这个请求进行一次封装（**启动一个线程去进行查询的任务**），然后立即返回客户端（**此时客户端可以去做别的事情，不用等待服务端真正的跑完所有的业务逻辑**），同时会异步的进行一个查询，并且异步的将结果返回
* 在客户端真正要用到这个查询的数据的时候，再从Future中去取数据


一下是上面思想的代码实现：


1.首先客户端会有一个查询的请求，此时客户端中有一个Future对象，立即将一个空数据的对象返回

```
package com.bjsxt.height.design014;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		
		FutureClient fc = new FutureClient();
		Data data = fc.request("请求参数");
		System.out.println("请求发送成功!");
		System.out.println("做其他的事情...");
		
		String result = data.getRequest();
		System.out.println(result);
		
	}
}

```

2.在Future对象中开启了一个线程进行数据的查询工作，在线程中将查询的结果数据放入空对象中

```
package com.bjsxt.height.design014;

public class FutureClient {

	public Data request(final String queryStr){
		//1 我想要一个代理对象（Data接口的实现类）先返回给发送请求的客户端，告诉他请求已经接收到，可以做其他的事情
		final FutureData futureData = new FutureData();
		//2 启动一个新的线程，去加载真实的数据，传递给这个代理对象
		new Thread(new Runnable() {
			@Override
			public void run() {
				//3 这个新的线程可以去慢慢的加载真实对象，然后传递给代理对象
				RealData realData = new RealData(queryStr);
				futureData.setRealData(realData);
			}
		}).start();
		
		return futureData;
	}
}

```

3.以下是其他的类

```
package com.bjsxt.height.design014;

public class FutureData implements Data{

	private RealData realData ;
	
	private boolean isReady = false;
	
	public synchronized void setRealData(RealData realData) {
		//如果已经装载完毕了，就直接返回
		if(isReady){
			return;
		}
		//如果没装载，进行装载真实对象
		this.realData = realData;
		isReady = true;
		//进行通知
		notify();
	}
	
	@Override
	public synchronized String getRequest() {
		//如果没装载好 程序就一直处于阻塞状态
		while(!isReady){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//装载好直接获取数据即可
		return this.realData.getRequest();
	}


}

```


```
package com.bjsxt.height.design014;

public class RealData implements Data{

	private String result ;
	
	public RealData (String queryStr){
		System.out.println("根据" + queryStr + "进行查询，这是一个很耗时的操作..");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("操作完毕，获取结果");
		result = "查询结果";
	}
	
	@Override
	public String getRequest() {
		return result;
	}

}

```


```
package com.bjsxt.height.design014;

public interface Data {

	String getRequest();

}

```







