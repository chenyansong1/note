Spring Data 是 Spring 旗下的一个持久态简化的框架。 而 Spring Data JPA 是 Spring Data 项目的一个模块。Spring Data JPA 的作用:简化 Spring 项目的基于 JPA 的开发，简化后的效果就是 持久态不需要编写实现类，只需要接口即可!


# 1.导入SpringData-JPA的依赖

* SpringData的依赖
* MySQL相关的依赖


```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.4.RELEASE</version>
	</parent>
	<groupId>cn.sm1234</groupId>
	<artifactId>10.springboot-jap</artifactId>
	<version>0.0.1-SNAPSHOT</version>

	<dependencies>
		<!-- web依赖 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<!-- 导入thymeleaf坐标 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>

		<!-- 导入devtools的坐标 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<optional>true</optional>
			<scope>true</scope>
		</dependency>

		<!-- 导入SpringDataJPA的坐标 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<!-- 连接数据库驱动 -->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
		</dependency>
		<!-- 连接池 -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.0.9</version>
		</dependency>
		
		<!--junit测试-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
		</dependency>
	</dependencies>

	<properties>
		<java.version>1.8</java.version>
		<thymeleaf.version>3.0.2.RELEASE</thymeleaf.version>
		<thymeleaf-layout-dialect.version>2.0.4</thymeleaf-layout-dialect.version>
	</properties>

</project>
```

# 2.配置连接信息

```
spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/test
spring.datasource.username=root
spring.datasource.password=root

# 连接池
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource

# 配置自动建表
spring.jpa.hibernate.ddl-auto=update
# 打印SQL语句
spring.jpa.show-sql=true
```


# 3.编写实体类

```
package cn.sm1234.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * 多方
 * @author lenovo
 *
 */
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
	@ManyToOne(cascade=CascadeType.PERSIST)
	//@JoinColumn：维护外键字段
	@JoinColumn(name="dept_id")
	private Dept dept;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Emp [id=" + id + ", name=" + name + ", gender=" + gender + ", telephone=" + telephone + ", address="
				+ address + "]";
	}

	public Dept getDept() {
		return dept;
	}

	public void setDept(Dept dept) {
		this.dept = dept;
	}
	
	
}

```

# 4.编写 Dao 接口


继承的JpaRepository类就有很多的自带的方法，我们可以使用

```
package cn.sm1234.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.sm1234.domain.Emp;
/**
 * 参数一：需要映射的实体
 * 参数二：实体里面的OID的类型
 * @author lenovo
 *
 */
public interface EmpRepository extends JpaRepository<Emp, Integer>,JpaSpecificationExecutor<Emp>{
	
	
}

```


# 5.编写测试代码

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
	
}


```

执行测试类，然后在表中就会有对应的表和对应的数据生成
