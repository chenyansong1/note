# 1.导入缓存相关的坐标

```
        <!-- 缓存坐标 -->        <dependency>             <groupId>org.springframework.boot</groupId>             <artifactId>spring-boot-starter-cache</artifactId>        </dependency>        <!-- Ehcache 支持 --> 
        <dependency>             <groupId>net.sf.ehcache</groupId>             <artifactId>ehcache</artifactId>        </dependency>
```

# 2.配置 ehcache.xml

该文件通常放在 resources 目录下

```
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../config/ehcache.xsd">

    <diskStore path="java.io.tmpdir"/>

	<!-- defaultCache： 默认配置 -->
    <defaultCache
            maxElementsInMemory="10000"
            eternal="false"
            timeToIdleSeconds="120"
            timeToLiveSeconds="120"
            maxElementsOnDisk="10000000"
            diskExpiryThreadIntervalSeconds="120"
            memoryStoreEvictionPolicy="LRU">
        <persistence strategy="localTempSwap"/>
    </defaultCache>
    
    <!-- 缓存名称为emp的配置 -->
    <cache name="emp"
            maxElementsInMemory="10000"
            eternal="false"
            timeToIdleSeconds="120"
            timeToLiveSeconds="120"
            maxElementsOnDisk="10000000"
            diskExpiryThreadIntervalSeconds="120"
            memoryStoreEvictionPolicy="LRU">
        <persistence strategy="localTempSwap"/>
    </cache>
    
    
</ehcache>
    
```

# 3.配置 application.properties

```

spring.cache.ehcache.config=ehcache.xml

```



# 4.在启动类加上注解


```
@SpringBootApplication 
@EnableCaching // 启用缓存 
public class Application {    public static void main(String[] args) {        SpringApplication.run(Application.class, args);    } 
}
```

# 5.在指定的方法上使用缓存注解

```
@Service
@Transactional
public class EmpServiceImpl implements EmpService {

	@Resource
	private EmpRepository EmpRepository;
	
	@Override
	@Cacheable(value="emp")
	public List<Emp> finAll() {
		return EmpRepository.findAll();
	}

	@Override
	@Cacheable(value="emp")   //@Cacheable:把当前方法的返回值放入缓存,value属性：缓存配置的名称
	public Emp findById(Integer id) {
		return EmpRepository.findOne(id);
	}

	@Override
	@Cacheable(value="emp")
	public Page<Emp> findByPage(Pageable pageable) {
		return EmpRepository.findAll(pageable);
	}

	@Override
	@CacheEvict(value="emp",allEntries=true)  // @CacheEvict: 把数据清除出缓存
	public void save(Emp emp) {
		EmpRepository.save(emp);
	}

}

```


# 测试代码

```
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)
public class EmpServiceTest {

	@Resource
	private EmpService empService;
	
	@Test
	public void testFindById(){
		//查询第一次
		System.out.println(empService.findById(1));
		//查询第二次
		System.out.println(empService.findById(1));
	}
}

```

如果没有添加缓存之前，可以看到执行了2次SQL查询

![](/Users/chenyansong/Documents/note/images/spring-boot/ehcache1.png)

如果添加了缓存我们看到的是如下的查询

![](/Users/chenyansong/Documents/note/images/spring-boot/ehcache2.png)




**无法进行序列化**

ehcache的原理就是将查询的结果序列化到磁盘，所以查询结果需要能够序列化

![](/Users/chenyansong/Documents/note/images/spring-boot/ehcache3.png)

所以我们需要让实体实现序列化接口

![](/Users/chenyansong/Documents/note/images/spring-boot/ehcache4.png)

