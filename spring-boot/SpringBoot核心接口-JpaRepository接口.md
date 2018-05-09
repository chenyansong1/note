CrudRepository 接口

作用：增删改查的基本功能，继承了Repository 接口

JpaRepository 接口，这个接口的作用主要继承 PagingAndSortingRepository 接口。
但这个接口还有额外的小功能，对之前接口的方法进行适配。也就是说，他不会像PagingAndSortingRepository中的方法一样有类型转换的概念，不需要我们强制转换

![](/Users/chenyansong/Documents/note/images/spring-boot/jiekou6.png)


注意:我们在实际开发中通常我们的接口都是继承 JpaRepository 接口

## 1.2.继承JpaRepository接口

```
public interface EmpRepository extends JpaRepository<Emp, Integer>{
	
}


```


## 1.2.测试类

```

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class EmpRepositoryTest {

	@Resource
	private EmpRepository empRepository;

	// 查询所有数据
	@Test
	public void testFindAllJpaRepository() {
		// 不需要强制转换
		List<Emp> list = empRepository.findAll();
		for (Emp emp : list) {
			System.out.println(emp);
		}
	}
	
}
```