# 1.导入坐标

```
<!-- 导入spring data redis -->
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

# 2.编写Redis的配置类


```
import redis.clients.jedis.JedisPoolConfig;
/**
 * 整合Spring Data Redis的配置类
 * @author lenovo
 *
 */
@Configuration
public class RedisConfig {

	/**
	 * 1.创建JedisPoolConfig对象：连接池参数
	 * @return
	 */
	@Bean
	@ConfigurationProperties(prefix="spring.redis")//这里使用的是application.properties的配置
	public JedisPoolConfig getJedisPoolConfig(){
		JedisPoolConfig poolConfig = new JedisPoolConfig();
	
	/*	application.properties的配置 来替换这里的配置
	   //最大空闲数
		poolConfig.setMaxIdle(5);
		poolConfig.setMinIdle(3);
		//最大连接数
		poolConfig.setMaxTotal(10);
		*/
		
		
		return poolConfig;
	}
	
	/**
	 * 2.创建JedisConnectionFactory: 配置redis连接参数
	 */
	@Bean 
	@ConfigurationProperties(prefix="spring.redis")
	public JedisConnectionFactory getJedisConnectionFactory(JedisPoolConfig poolConfig){
		JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
		
		//关联
		connectionFactory.setPoolConfig(poolConfig);
		
		/*//redis主机地址
		connectionFactory.setHostName("localhost");
		//redis端口
		connectionFactory.setPort(6379);
		//redis数据库索引
		connectionFactory.setDatabase(1);*/
		
		return connectionFactory;
	}
	
	/**
	 * 3.创建RedisTemplate： 用于执行redis的操作方法
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory){
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();

		//关联connectionFactory
		redisTemplate.setConnectionFactory(connectionFactory);
		
		//设置key序列化
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		//设置value序列化
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		
		return redisTemplate;
	}
	
}

```

application.properties的配置如下：

```
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.database=1

spring.redis.pool.max-idle=5
spring.redis.pool.min-idle=3
spring.redis.pool.max-active=10

```

# 3.写测试方法

* string的set,get
* javaBean对象的set,get
* json格式的set,get JavaBean


```


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class RedisTest {

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	// 存入字符串:StringRedisSerializer
	@Test
	public void testSet() {
		redisTemplate.opsForValue().set("name", "sm1234@qq.com");
	}

	// 取出字符串:StringRedisSerializer
	@Test
	public void testGet() {
		String name = (String) redisTemplate.opsForValue().get("name");
		System.out.println(name);
	}

	// 存入JavaBean:JdkSerializationRedisSerializer
	@Test
	public void testSetJavaBean() {
		Emp emp = new Emp();// 注意如果要序列化，此时的实体类
		emp.setName("张三");
		emp.setGender("男");

		// 重新设置value序列化器，JavaBean的序列化器：JdkSerializationRedisSerializer
		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());

		redisTemplate.opsForValue().set("emp", emp);
	}

	// 取出JavaBean
	@Test
	public void testGetJavaBean() {
		// 重新设置value序列化器
		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());

		Emp emp = (Emp) redisTemplate.opsForValue().get("emp");
		System.out.println(emp);
	}

	// 以JSON格式存入JavaBean
	@Test
	public void testSetJavaBeanUserJson() {
		// 重新设置value序列化区
    		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Emp.class));

		Emp emp = new Emp();
		emp.setName("李四");
		emp.setGender("男");
		
		redisTemplate.opsForValue().set("emp_json", emp);
	}
	
	// 以JSON格式取出JavaBean
	@Test
	public void testGetJavaBeanUserJson() {
		// 重新设置value序列化区
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Emp.class));

		Emp emp = (Emp) redisTemplate.opsForValue().get("emp_json");
		System.out.println(emp);
	}
}

```


**javaBean对象的set,get**

当我们存入一个对象到Redis中的时候，如下：

![](/Users/chenyansong/Documents/note/images/spring-boot/redis1.png)

当我们取出的时候，如下：

![](/Users/chenyansong/Documents/note/images/spring-boot/redis2.png)



**json格式的set,get JavaBean**

存入json格式的JavaBean

![](/Users/chenyansong/Documents/note/images/spring-boot/redis3.png)

取出json格式的JavaBean

![](/Users/chenyansong/Documents/note/images/spring-boot/redis4.png)

