# jedis获取连接 java.util.NoSuchElementException: Unable to validate object



错误日志

```
redis.clients.jedis.exceptions.JedisConnectionException: Could not get a resource from the pool at
…
Caused by: java.util.NoSuchElementException: Unable to validate object at
…
org.apache.commons.pool2.impl.GenericObjectPool 类

borrowObject(long borrowMaxWaitMillis) 方法中 2个地方 跑出了Unable to validate object异常
```

1：factory.activateObject(p);

    public void activateObject(PooledObject<Jedis> pooledJedis)
        throws Exception {
    final BinaryJedis jedis = pooledJedis.getObject();
    if (jedis.getDB() != database) {
        jedis.select(database);
    }



    }

2：validate = factory.validateObject(p); （这个是xml中配置了<property name="testOnBorrow" value="true" />才会进入的方法）

 public boolean validateObject(PooledObject<Jedis> pooledJedis) {
	final BinaryJedis jedis = pooledJedis.getObject();
	try {
	    return jedis.isConnected() && jedis.ping().equals("PONG");
	} catch (final Exception e) {
	    return false;
	}

    }

总结：我是在第二种情况下抛的异常，发现ping   redis的返回结果不是 PONG。这个时候你可以尝试ping一下redis 获取返回内容进行分析，我的是redis内存不住导致的

