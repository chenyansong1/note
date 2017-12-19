---
title: 自定义rpc框架的设计思路
categories: java   
tags: [java,rpc]
---



# 关联的知识
1. spring的注解和自定义注解
2. netty
3. 反射
4. 动态代理
5. zookeeper

<!--more-->

# 1.实现原理图(详细)


![](http://ols7leonh.bkt.clouddn.com//assert/img/java/rpc/rpc.png)


# 2.实现简图

![](http://ols7leonh.bkt.clouddn.com//assert/img/java/rpc/rpc_simple.png)


## 2.1.对客户端来说
我只是需要调用一个业务的实现，所以我调用对应业务实现的接口中的方法

## 2.2.对服务端来说
我只是实现业务接口中的方法，然后自定义注解，将业务实现类交给spring进行管理



# 3.工程rpc-sample-server

## 3.1.服务端的启动入口

加载spring的配置文件，构造其中的bean

```
public class RpcBootstrap {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring.xml");
    }
}  

```


## 3.2.spring的配置文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">
       
    <!-- 扫描指定包下的类 -->
    <context:component-scan base-package="cn.itcast.rpc.sample.server"/>
    <!-- 指定加载一个properties文件 -->
    <context:property-placeholder location="classpath:rpc.properties"/>

    <bean id="serviceRegistry" class="cn.itcast.rpc.registry.ServiceRegistry">
        <constructor-arg name="registryAddress" value="${registry.address}"/><!-- 使用ognl表达式，key-value在properties中配置了 -->
    </bean>
    <bean id="rpcServer" class="cn.itcast.rpc.server.RpcServer">
        <constructor-arg name="serverAddress" value="${server.address}"/>
        <constructor-arg name="serviceRegistry" ref="serviceRegistry"/><!-- 构造函数的参数指向了另一个Bean -->
    </bean>
</beans>  

```


## 3.3.业务实现类
1. 实现接口中的方法
2. 自定义注解RpcService

```
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService { 
    //..........
} 
```


# 4.工程rpc-server
## 4.1.spring启动的过程中会调用RpcServer中指定的方法
由于本类实现了ApplicationContextAware 和 InitializingBean,<font color=red>spring构造本对象时会调用setApplicationContext()方法，从而可以在方法中通过自定义注解获得用户的业务接口和实现，还会调用afterPropertiesSet()方法，在方法中启动netty服务器</font>


```
public class RpcServer implements ApplicationContextAware, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

	private String serverAddress;
	private ServiceRegistry serviceRegistry;

	//用于存储业务接口和实现类的实例对象(由spring所构造)
	private Map<String, Object> handlerMap = new HashMap<String, Object>();

	//服务器绑定的地址和端口由spring在构造本类时从配置文件中传入
	public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
		this.serverAddress = serverAddress;
		//用于向zookeeper注册名称服务的工具类
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * 通过注解，获取标注了rpc服务注解的业务类的----接口及impl对象，将它放到handlerMap中
	 */
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
		if (MapUtils.isNotEmpty(serviceBeanMap)) {
			for (Object serviceBean : serviceBeanMap.values()) {
				//从业务实现类上的自定义注解中获取到value，从来获取到业务接口的全名
				String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
				handlerMap.put(interfaceName, serviceBean);
			}
		}
	}

	/**
	 * 在此启动netty服务，绑定handle流水线：
	 * 1、接收请求数据进行反序列化得到request对象
	 * 2、根据request中的参数，让RpcHandler从handlerMap中找到对应的业务imple，调用指定方法，获取返回结果
	 * 3、将业务调用结果封装到response并序列化后发往客户端
	 *
	 */
	public void afterPropertiesSet() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap
					.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel channel)
								throws Exception {
							channel.pipeline()
									.addLast(new RpcDecoder(RpcRequest.class))// 注册解码 IN-1
									.addLast(new RpcEncoder(RpcResponse.class))// 注册编码 OUT
									.addLast(new RpcHandler(handlerMap));//注册RpcHandler IN-2
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			
			String[] array = serverAddress.split(":");
			String host = array[0];
			int port = Integer.parseInt(array[1]);

			ChannelFuture future = bootstrap.bind(host, port).sync();
			LOGGER.debug("server started on port {}", port);

			if (serviceRegistry != null) {
				serviceRegistry.register(serverAddress);
			}

			future.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}


```


# 5.工程rpc-sample-app

## 5.1.调用业务接口中方法
```
public class HelloServiceTest {
    //注解
    @Autowired
    private RpcProxy rpcProxy;
    @Test
    public void helloTest1() {
        // 调用代理的create方法，代理HelloService接口
        HelloService helloService = rpcProxy.create(HelloService.class);
        
        // 调用代理的方法，执行invoke
        String result = helloService.hello("World");
        System.out.println("服务端返回结果：");
        System.out.println(result);
    } 
} 

```




# 6.工程rpc-client

## 6.1.获取代理
向zk查找可用的服务主机地址
在代理中调用rpc client发送消息
```
public class RpcProxy {
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;
    public RpcProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }
    /**
     * 创建代理
     * 
     * @param interfaceClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[] { interfaceClass }, new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //创建RpcRequest，封装被代理类的属性
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        //拿到声明这个方法的业务接口名称
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        //查找服务
                        if (serviceDiscovery != null) {
                            serverAddress = serviceDiscovery.discover();
                        }
                        //随机获取服务的地址
                        String[] array = serverAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        //创建Netty实现的RpcClient，链接服务端
                        RpcClient client = new RpcClient(host, port);
                        //通过netty向服务端发送请求
                        RpcResponse response = client.send(request);
                        //返回信息
                        if (response.isError()) {
                            throw response.getError();
                        } else {
                            return response.getResult();
                        }
                    }
                });
    }
}  

```

# 7.工程rpc-registry
1. 提供rpc server向zk注册
2. rpc client可以查找可用的主机地址

# 8.工程rpc-common
1.对通信过程中的流数据解码封装成类，或者编码成为流

 


# 9.代码实现

[github地址](https://github.com/chenyansong1/custom_rpc/tree/master/%E8%87%AA%E5%AE%9A%E4%B9%89rpc%E9%A1%B9%E7%9B%AE%E5%B7%A5%E7%A8%8B)








