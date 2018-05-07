CrudRepository 接口

作用：增删改查的基本功能，继承了Repository 接口


![](/Users/chenyansong/Documents/note/images/spring-boot/jiekou1.png)


## 1.2.继承CrudRepository 接口

```
public interface EmpRepository extends CrudRepository<Emp, Integer>{

}

```

接口提供的方法有如下：


![](/Users/chenyansong/Documents/note/images/spring-boot/jiekou3.png)



## 1.2.测试类

```

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class EmpRepositoryTest {

	@Resource
	private EmpRepository empRepository;


	// 添加
	@Test
	public void testSave() {
		Emp emp = new Emp();
		emp.setName("王五666");
		emp.setGender("女");
		emp.setTelephone("1346666677777");
		emp.setAddress("广州番禺");

		empRepository.save(emp);
	}

	// 修改
	@Test
	public void testUpdate() {
		Emp emp = new Emp();
		emp.setId(4);
		emp.setName("王五6666");
		emp.setGender("女");
		emp.setTelephone("1346666677777");
		emp.setAddress("广州白云");

		empRepository.save(emp);
	}

	// 查询所有数据
	@Test
	public void testFindAll() {
		// 强制转换: 类型转换
		List<Emp> list = (List<Emp>) empRepository.findAll();
		for (Emp emp : list) {
			System.out.println(emp);
		}
	}

	// 查询一个对象
	@Test
	public void testFineOne() {
		// 强制转换
		Emp emp = empRepository.findOne(4);
		System.out.println(emp);
	}

	// 删除一个对象
	@Test
	public void testDelete() {
		// 删除ID为4的对象
		empRepository.delete(4);
	}
	
}

```


他的保存方法，也可以是更新方法

![](/Users/chenyansong/Documents/note/images/spring-boot/jiekou2.png)

