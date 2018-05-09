# dao

```

public interface EmpRepository extends JpaRepository<Emp, Integer>,JpaSpecificationExecutor<Emp>{
	
}
```


# 1.员工类

```

@Entity
@Table(name="t_emp")
public class Emp {
	
	@id // 逐渐
	@GeneratedValue(strategy=GenerationType.IDENTITY)  //生成策略，自动增长
	@Column(name="id")
	private Integer id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="gender")
	private String gender;
	
	@Column(name="telephone")
	private String telephone;
	
	@Column(name="address")
	private String address;
	
	//关联部门（1方）
	@ManyToOne(cascade=CascadeType.PERSIST)// 级联操作，在保存员工的同时保存部门
	//@JoinColumn：维护外键字段
	@JoinColumn(name="dept_id")
	private Dept dept;
```

# 2.部门类

```

@Entity
@Table(name="t_dept")
public class Dept {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="dept_name")
	private String deptName;
	
	//关联员工（多方）
	@OneToMany(mappedBy="dept")//dept是员工类中的部门字段
	private Set<Emp> emps = new HashSet<Emp>();

```

# 3.一对多关联操作

```

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)
public class OneToManyTest {

	@Resource
	private EmpRepository empRepository;
	
	//添加操作
	@Test
	public void testSave(){
		//创建部门
		Dept dept = new Dept();
		dept.setDeptName("秘书部");
		
		//创建员工
		Emp emp = new Emp();
		emp.setName("小红");
		
		//关联
		dept.getEmps().add(emp);
		emp.setDept(dept);
		
		//保存数据
		empRepository.save(emp);
	}
	
	//查询
	@Test
	public void testFind(){
		//查询员工
		Emp emp = empRepository.findOne(9);
		
		//所在部门
		Dept dept = emp.getDept();
		System.out.println("员工"+emp.getName()+"的部门是："+dept.getDeptName());
	}
	
}
```



![](/Users/chenyansong/Documents/note/images/spring-boot/yingshe1.png)

**我们看下表的数据**

![](/Users/chenyansong/Documents/note/images/spring-boot/yingshe2.png)


![](/Users/chenyansong/Documents/note/images/spring-boot/yingshe3.png)



