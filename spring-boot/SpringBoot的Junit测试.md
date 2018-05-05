在如下的目录中新建一个测试类：

![](/Users/chenyansong/Documents/note/images/spring-boot/junit1.png)


修改pom文件

```
  	<!-- junit测试支持 -->
  	<dependency>
  		 <groupId>org.springframework.boot</groupId>
   		 <artifactId>spring-boot-starter-test</artifactId>
  	</dependency>
```


测试类

```

package cn.sm1234.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.sm1234.Application;
import cn.sm1234.service.UserService;

/**
 * Spring Boot测试类
 * @author lenovo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)  // @RunWith: 让junit和Spring环境进行整合
@SpringBootTest(classes={Application.class})     // @SpringBootTest: 该类是一个SpringBoot测试类，加载SpringBoot启动器类
// Spring： @ContextConfiguation("classpath:applicationContext.xml")
public class UserServiceTest {

	@Resource
	private UserService userService;
	
	@Test
	public void testSave(){
		userService.save();
	}
}


```






