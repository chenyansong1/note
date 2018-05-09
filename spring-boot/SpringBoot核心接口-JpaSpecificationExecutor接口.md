5.8. JpaSpecificationExecutor 接口


JpaSpecificationExecutor 接口，作用是用于(组合)条件查询(条件+分页)。注意:JpaSpecificationExecutor 接口是独立，和JpaRepository没有关系

所以在实际开发中，我们采用继承 JpaRepository，同时也去继承JpaSpecificationExecutor接口

# 1.实现接口

```
public interface EmpRepository extends JpaRepository<Emp, Integer>,JpaSpecificationExecutor<Emp>{
	
}
```


# 2.测试接口

```

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class EmpRepositoryTest {

	@Resource
	private EmpRepository empRepository;



	// 使用JPASpecificationExecutor接口的方法（1个条件）
	@Test
	public void testFindAllJPASpecificationExecutor1() {
		/**
		 * Specification： 用于封装条件数据的对象
		 */
		Specification<Emp> spec = new Specification<Emp>() {
			//Predicate:该对象用于封装条件
			/**
			 * Root<Emp> root ： 根对象，用于查询对象的属性
			 * CriteriaQuery<?> query： 执行普通的查询
			 * CriteriaBuilder cb: 查询条件构造器，用于完成不同条件的查询
			 */
			@Override
			public Predicate toPredicate(Root<Emp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// where name = ?
				/**
				 * 参数一：查询的属性（需要使用root进行查询）
				 * 参数二：条件值
				 */
				Predicate pre = cb.equal(root.get("name"),"张三");
				
				return pre;
			}
		};
		
		List<Emp> list = empRepository.findAll(spec);
		for (Emp emp : list) {
			System.out.println(emp);
		}
	}
	
	// 使用JPASpecificationExecutor接口的方法（多个条件）
	@Test
	public void testFindAllJPASpecificationExecutor2() {
		Specification<Emp> spec = new Specification<Emp>() {
			@Override
			public Predicate toPredicate(Root<Emp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// where name = ? and gender = ?
				List<Predicate> preList = new ArrayList<Predicate>();
				
				preList.add( cb.equal(root.get("name"), "张三") );
				preList.add( cb.equal(root.get("gender"), "男") );
				
				Predicate[] preArray = new Predicate[preList.size()];
				return cb.and(preList.toArray(preArray));
			}
		};
		
		List<Emp> list = empRepository.findAll(spec);
		for (Emp emp : list) {
			System.out.println(emp);
		}
	}
	
}
```

上面只是实现了多条件的查询，我们可以添加上排序 or 分页的参数，如下：



![](/Users/chenyansong/Documents/note/images/spring-boot/jiekou7.png)

> 分页和排序的写法，参见：JpaReposity

