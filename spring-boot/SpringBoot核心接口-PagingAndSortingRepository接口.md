
PagingAndSortingRepository 接口，作用用于分页和排序查询。 
注意:PagingAndSortingRepository 接口继承了 CrudRepository 接口

![](/Users/chenyansong/Documents/note/images/spring-boot/jiekou4.png)


该接口提供了如下的方法
* 排序方法
* 分页方法

![](/Users/chenyansong/Documents/note/images/spring-boot/jiekou5.png)


# 1.继承接口

```
public interface EmpRepository extends PagingAndSortingRepository<Emp, Integer>{

}

```


# 2.测试类


涉及下面的内容：

* 排序
* 分页
* 排序+分页


```

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class EmpRepositoryTest {

	@Resource
	private EmpRepository empRepository;



	// 排序
	@Test
	public void testSort() {
		// 封装排序条件的对象:按照ID，倒序排列
		Sort sort = new Sort(new Order(Direction.DESC, "id"));

		Iterable<Emp> list = empRepository.findAll(sort);
		for (Emp emp : list) {
			System.out.println(emp);
		}
	}

	// 分页
	@Test
	public void testPagealbe() {
		// Pageable：用于封装分页参数 。 当前页码和查询记录数（注意：当前页码从0开始的）
		Pageable pageable = new PageRequest(1, 2);// 查询第一页，查询2条记录
		// 封装排序条件的对象
		// Page: 用于封装分页查询后的结果
		Page<Emp> pageData = empRepository.findAll(pageable);

		System.out.println("总记录数：" + pageData.getTotalElements());// 查询总记录数
		List<Emp> content = pageData.getContent();// 拿到当前页的数据
		for (Emp emp : content) {
			System.out.println(emp);
		}
		System.out.println("总页数：" + pageData.getTotalPages());// 总页数
	}

	// 排序+ 分页
	@Test
	public void testSortAndPagealbe() {
		// Pageable：用于封装分页参数 。 当前页码和查询记录数（注意：当前页码从0开始的）
		Sort sort = new Sort(new Order(Direction.DESC, "id"));

		Pageable pageable = new PageRequest(0, 2, sort);
		// 封装排序条件的对象
		// Page: 用于封装分页查询后的结果
		Page<Emp> pageData = empRepository.findAll(pageable);

		System.out.println("总记录数：" + pageData.getTotalElements());
		List<Emp> content = pageData.getContent();
		for (Emp emp : content) {
			System.out.println(emp);
		}
		System.out.println("总页数：" + pageData.getTotalPages());
	}

	// 查询所有数据
	@Test
	public void testFindAllJpaRepository() {
		// 不需要强制转换
		List<Emp> list = empRepository.findAll();
		for (Emp emp : list) {
			System.out.println(emp);
		}
	}

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

