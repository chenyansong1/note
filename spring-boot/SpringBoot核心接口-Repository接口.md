# 1.Repository 接口

* 提供基于方法名称命名查询
* 提供基于@Query查询与修改

## 1.1.提供基于方法名称命名查询

```
findByNameIs(String name);// 查询某个字段
findByNameEquals(String name);// 查询某个字段
findByName(String name); // 省略Is or Equals

```


```
public interface EmpRepository extends Repository<Emp, Integer>{    //查询 name(驼峰式名称)    public List<Emp> findByName(String name);//单条件查询
    public List<Emp> findByNameAndGender(String name,String gender);//多条件查询
    public List<Emp> findByTelephoneLike(String telphone);//模糊查询
    /*
    1.findBy queryBy getBy 都行，
    2.Name是字段名称    3. 多条件查询，中间使用And
    4.模糊查询使用Like,查询的字段的值可以添加 % _
    */}

```

下面是Junit提供的测试类

```
package cn.sm1234.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.sm1234.Application;
import cn.sm1234.dao.EmpRepository;
import cn.sm1234.domain.Emp;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class EmpRepositoryTest {

	@Resource
	private EmpRepository empRepository;


	@Test
	public void testFindByName() {
		List<Emp> list = empRepository.findByName("张三");
		for (Emp emp : list) {
			System.err.println(emp);
		}
	}

	@Test
	public void testFindByNameAndGender() {
		List<Emp> list = empRepository.findByNameAndGender("李四", "男");
		for (Emp emp : list) {
			System.err.println(emp);
		}
	}

	/*
	 * 如果是Like查询，那么需要加上模糊查询的关键词 
	   %： 匹配任意个字符 
	   _ : 匹配一个字符
	 */
	@Test
	public void testFindByTelephoneLike() {
		List<Emp> list = empRepository.findByTelephoneLike("%134%");
		for (Emp emp : list) {
			System.err.println(emp);
		}
	}

	
}

```

## 1.2.提供基于@Query查询与修改

```
public interface EmpRepository extends Repository<Emp, Integer>{
	
	//查询name(驼峰式名称)
	public List<Emp> findByName(String name);
	public List<Emp> findByNameAndGender(String name,String gender);
	public List<Emp> findByTelephoneLike(String telphone);
	
	// 
	@Query("from Emp where name = ?")//nativeQuery=false这个是默认的，如果是false，那么查询是走：hql,走hql,则查询的是实体类，所以这里是Emp实体
	public List<Emp> queryName(String name);
	
	@Query(value="select * from t_emp where name = ?",nativeQuery=true)// true表示查询走的是sql![]()
	public List<Emp> queryName2(String name);
	
	@Query("update Emp set address = ? where id = ?")
	@Modifying // 进行修改操作
	public void updateAddressById(String address,Integer id);
}

```

测试类如下:

```

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class EmpRepositoryTest {

	@Resource
	private EmpRepository empRepository;


	@Test
	public void testQueryName() {
		List<Emp> list = empRepository.queryName("张三");
		for (Emp emp : list) {
			System.err.println(emp);
		}
	}

	@Test
	public void testQueryName2() {
		List<Emp> list = empRepository.queryName2("张三");
		for (Emp emp : list) {
			System.err.println(emp);
		}
	}

	@Test
	@Transactional // 开启事务操作 注意：@Transactional和@Test一起使用的时候，事务会自动回滚,所以需要事物生效，那么需要：取消自动回滚
	@Rollback(false) // 取消自动回滚
	public void testupdateAddressById() {
		empRepository.updateAddressById("广州越秀", 2);
	}
}

```




