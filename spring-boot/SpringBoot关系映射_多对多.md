这里演示的是 用户与角色的多对多的关系

# 1.用户类


```

@Entity
@Table(name="t_user")
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) //生成策略，自动增长
	@Column(name="id")
	private Integer id;
	
	@Column(name="user_name")
	private String name;
	
	@Column(name="password")
	private String password;
	
	//关联角色
	@ManyToMany(cascade=CascadeType.PERSIST,fetch=FetchType.EAGER)// 级联操作，那么会级联添加关联的表；fetch=FetchType.EAGER表示放弃延迟（例如在查询用户的时候，会立即查询对应的角色）
	//@JoinTable: 映射中间表
	//  joinColumns: 当前表在中间表的外键字段
	//  inverseJoinColumns：另外一张表的外键字段
	@JoinTable(name="t_user_role",joinColumns=@JoinColumn(name="user_id"),inverseJoinColumns=@JoinColumn(name="role_id"))
	private Set<Role> roles = new HashSet<Role>();
}
```

# 2.角色类

```

@Entity
@Table(name="t_role")
public class Role {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="role_name")
	private String name;
	
	//关联用户:这里我们没有指定中间表，因为对于关联关系，我们只需要维护一方即可（我们在User表中进行了维护）
	@ManyToMany(mappedBy="roles")
	private Set<User> users = new HashSet<User>();

}
```
# dao类

```
public interface UserRepository extends JpaRepository<User, Integer>{
}

```

# 3.关联操作

```

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)
public class ManyToManyTest {

	@Resource
	private UserRepository userRepository;
	
	//添加
	@Test
	public void testSave(){
		//创建用户
		User user = new User();
		user.setName("eric");
		user.setPassword("1234");
		
		//创建角色
		Role role = new Role();
		role.setName("超级管理员");
		
		Role role2 = new Role();
		role2.setName("普通管理员");
		
		//关联
		user.getRoles().add(role);
		user.getRoles().add(role2);
		role.getUsers().add(user);
		role2.getUsers().add(user);
		
		//保存数据
		userRepository.save(user);
	}
	
	//查询
	@Test
	public void testFind(){
		User user = userRepository.findOne(1);
		Set<Role> roles = user.getRoles();
		System.out.println("用户："+user.getName()+"的角色为");
		for (Role role : roles) {
			System.out.println(role.getName());
		}
	}
}
```


![](/Users/chenyansong/Documents/note/images/spring-boot/yingshe4.png)

**表中的数据如下**

![](/Users/chenyansong/Documents/note/images/spring-boot/yingshe5.png)
