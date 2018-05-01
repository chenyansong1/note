# 建表

```
CREATE TABLE `t_customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `gender` char(1) DEFAULT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  `address` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8

```


# 工程的目录结构

下面是工程的目录结构：

![](/Users/chenyansong/Documents/note/images/spring-boot/mybatis1.png)




# 1.添加pom依赖


* mybatis相关的坐标
* build中指定在构建的时候指定将Java包中的xml打包


```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>cn.cm.hello</groupId>
    <artifactId>spring-hello</artifactId>
    <version>1.0-SNAPSHOT</version>


    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.1.RELEASE</version>
    </parent>

    <!--引入web支持的坐标，spring MVC，Servlet, Filter, Listener等，引入一个web的启动器-->
    <!--会引入aop,beans.context,core,expression,web,webmvc-->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- 引入freeMarker的依赖包. -->

        <!--<dependency>-->

            <!--<groupId>org.springframework.boot</groupId>-->

            <!--<artifactId>spring-boot-starter-freeMarker</artifactId>-->

        <!--</dependency>-->

        <!-- thymeleaf -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>



        <!-- mybatis相关的坐标 -->
        <!-- mysql -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <!-- druid连接池 -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.0.9</version>
        </dependency>
        <!-- SpringBoot的Mybatis启动器 -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.1.1</version>
        </dependency>

    </dependencies>


    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <excludes>
                    <exclude>**/*.properties</exclude>
                    <exclude>**/*.xml</exclude>
                </excludes>
                <filtering>true</filtering>
            </resource>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>true</filtering>
            </resource>
        </resources>
    </build>


</project>

```


# 2.重写配置文件

```
# 上传文件配置
spring.http.multipart.maxFileSize=100MB
spring.http.multipart.maxRequestSize=200MB

# 数据库连接的配置
spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/ssm
spring.datasource.username=root
spring.datasource.password=root

# 连接池
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource

# mybatis自动扫描的包（mybatis自动去这个包下面扫描，给实体起别名，在mapper.xml中会用到）
mybatis.type-aliases-package=cn.sn1234.domain


```


# 3.实体类（Customer）

```
public class Customer {

    private Integer id;
    private String name;
    private String gender;
    private String telephone;
    private String address;

    // ...get set    

}
```



# 4.dao(mapper)

```
package cn.sn1234.dao;

import cn.sn1234.domain.Customer;

import java.util.List;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */

public interface CustomerMapper {
    public void save(Customer customer);

    public List<Customer> findAll();

    public Customer findById(Integer id);

    public void update(Customer customer);

    public void delete(Integer id);
}

```


# 5.写sql文件
在 Mapper 接口同目录下建立和 Mapper 接口同名的 xml 文件

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 该文件存放CRUD的sql语句 -->
<!-- 这里是和CustomerMapper类对应的一个xml接口-->
<mapper namespace="cn.sn1234.dao.CustomerMapper">

	<!-- 这里的参数类型是customer，这是一个别名，在application.xml中给实体起别名的时候，配置过，所以这里可以使用实体的别名-->
	<insert id="save" parameterType="customer">
		INSERT INTO ssm.t_customer 
			(
			NAME, 
			gender, 
			telephone, 
			address
			)
			VALUES
			( 
			#{name}, 
			#{gender}, 
			#{telephone}, 
			#{address}
			)
	</insert>
	
</mapper>
```

# 6.Service接口和实现

**接口**

```
package cn.sn1234.service;

import cn.sn1234.domain.Customer;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */

public interface CustomerService {

    public void save(Customer customer);
}

```


**实现**

```
package cn.sn1234.service.impl;

import cn.sn1234.dao.CustomerMapper;
import cn.sn1234.domain.Customer;
import cn.sn1234.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */

@Service
@Transactional //加上事物
public class CustomerServiceImpl implements CustomerService {

    // 注入mapper接口的对象
    @Resource
    private CustomerMapper customerMapper;


    @Override
    public void save(Customer customer) {
        System.out.println("xxxxxx="+customer.getName());
        System.out.println("yyyyy="+customerMapper);
        customerMapper.save(customer);
    }
}

```


# 7.编写controller

```
package cn.sn1234.controller;

import cn.sn1234.domain.Customer;
import cn.sn1234.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Resource
    private CustomerService customerService;


    //设置一个跳转页面
    @RequestMapping("/input")
    public String input(){
        return "input";
    }

    /*
    保存方法
     */
    @RequestMapping("/save")
    public String save(Customer customer){
        customerService.save(customer);
        // 跳转到succ页面
        return "succ";
    }


}

```


# 8.前端模板页面

```

<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8" />
    <title>customer info input</title>
</head>
<body>

<form th:action="@{~/customer/save}" method="post">
    姓名:<input name="name"  type="text"/><br/>
    性别:<input name="gender"  type="text"/><br/>
    手机:<input name="telephone"  type="text"/><br/>
    地址:<input name="address"  type="text"/><br/>

    <input type="submit" value="保存" />
</form>

</body>
</html>

```

# 9.启动类application

```
package cn.sn1234;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */
@SpringBootApplication
@MapperScan("cn.sn1234.dao") //作用：用于扫描mybatis的mapper接口的包
public class Application {
    public static void main(String[] args){

        // 在这个启动器中，指定启动类，和参数
        SpringApplication.run(Application.class, args);

    }

}

```

CustomerMapper.java
CustomerMapper.xml一定是一样的，要对应起来

![](/Users/chenyansong/Documents/note/images/spring-boot/chaolianjie.png)








