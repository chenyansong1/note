# @Cacheable:把方法返回值放入缓存
* value:ehcache.xml 的缓存配置名称
* key 可以用来标识一个查询的结果

![](/Users/chenyansong/Documents/note/images/spring-boot/ehcache6.png)



```
	@Override
	@Cacheable(value="emp" key="#pageable")
	public Page<Emp> findByPage(Pageable pageable) {
		return EmpRepository.findAll(pageable);
	}
```

我们使用测试代码去测试

```
	@Test
	public void testFindByPage(){
		//查询第一次
		Pageable pageable = new PageRequest(0,2);
		System.out.println(empService.findByPage(pageable).getTotalElements());
		
		//查询第二次
		System.out.println(empService.findByPage(pageable).getTotalElements());
		
		//换页
		pageable = new PageRequest(1,2);
		System.out.println(empService.findByPage(pageable).getTotalElements());
		
	}
```
得到如下的结果

![](/Users/chenyansong/Documents/note/images/spring-boot/ehcache7.png)


但是如果我们修改key值，如下

```
	@Override
	@Cacheable(value="emp" key="#pageable.pageSize")
	public Page<Emp> findByPage(Pageable pageable) {
		return EmpRepository.findAll(pageable);
	}
```


我们会得到如下的结果：从这里我们就可以知道key的作用了

![](/Users/chenyansong/Documents/note/images/spring-boot/ehcache5.png)



# @CacheEvict

```

	@Override
	@Cacheable(value="emp")
	public List<Emp> finAll() {
		return EmpRepository.findAll();
	}

	@Override
	public void save(Emp emp) {
		EmpRepository.save(emp);
	}
```


第一次查询的结果会被缓存，但是如果我们此时再添加一个用户，然后再次查询，那么查询的结果会是从缓存中去取，还是重新查询呢？

```
	
	@Test
	public void testFindAll(){
		//查询第一次
		System.out.println(empService.finAll().size());
		
		//添加
		Emp emp = new Emp();
		emp.setName("陈六");
		empService.save(emp);
		
		//查询第二次
		System.out.println(empService.finAll().size());
	}
```

从下图的查询结果知道，第二次查询还是从缓存中去取的数据，这不是我们要的效果

![](/Users/chenyansong/Documents/note/images/spring-boot/ehcache8.png)


我们在保存的时候，将缓存中的数据清除，那么就不会从缓存中去取数据

```

	@Override
	@Cacheable(value="emp")
	public List<Emp> finAll() {
		return EmpRepository.findAll();
	}

	@Override
	@CacheEvict(value="emp",allEntries=true)  // @CacheEvict: 把数据清除出缓存
	public void save(Emp emp) {
		EmpRepository.save(emp);
	}
```

