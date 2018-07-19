服务动态上下线感知



服务端

```Java
public class RegisterCenterImpl implements IRegisterCenter{

    private CuratorFramework curatorFramework;

    {
        curatorFramework=CuratorFrameworkFactory.builder().
                connectString(ZkConfig.CONNNECTION_STR).
                sessionTimeoutMs(4000).
                retryPolicy(new ExponentialBackoffRetry(1000,
                        10)).build();
        curatorFramework.start();
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        //注册相应的服务
        String servicePath=ZkConfig.ZK_REGISTER_PATH+"/"+serviceName;

        try {
            //判断 /registrys/product-service是否存在，不存在则创建
            if(curatorFramework.checkExists().forPath(servicePath)==null){
                curatorFramework.create().creatingParentsIfNeeded().
                        withMode(CreateMode.PERSISTENT).forPath(servicePath,"0".getBytes());
            }

            // 服务节点的路径
            String addressPath=servicePath+"/"+serviceAddress;
            // 这里创建的是临时节点
            String rsNode=curatorFramework.create().withMode(CreateMode.EPHEMERAL).
                    forPath(addressPath,"0".getBytes());
            System.out.println("服务注册成功："+rsNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```



从下图可以看出：商品的服务，会向/registrys/product-service/节点下注册，也是就在这个路径下面，创建节点（ip:port）,即这些IP:port 将提供服务



![image-20180719201024975](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/register-server.png)



用户去拿到/registrys/product-service/节点的所有子节点，这里应该有一个负载的机制，然后选择其中一个节点去连接

和远程RPC一起调用