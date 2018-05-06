
这里的表单验证功能是说的：SpringBoot的后台验证，不涉及前端验证

原理:利用 hibernate-validate 的注解实现的

# 1.一个简单的错误验证

## 1.1.User 类

```
package cn.sm1234.domain;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

public class User {

	private Integer id;
	//@NotBlank(message="用户名不能为空") // 非空
	@NotEmpty(message="用户名不能为空")
	private String name;
	@NotBlank(message="密码不能为空") // 非空
	@Length(min=4,max=10,message="密码必须在4-10位之间")
	private String password;
	@Min(value=0)
	private Integer age;
	@Email(message="邮箱不合法")
	private String email;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", password=" + password + ", age=" + age + "]";
	}
	
}

```


## 1.2.UserController

```
package cn.sm1234.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.sm1234.domain.User;

@Controller
@RequestMapping("/user")
public class UserController {

	/**
	 * 跳转到add.html
	 * @return
	 */
	@RequestMapping("toAdd")
	public String toAdd(User user){
		return "add";
	}
	
	/**
	 * 用户添加
	 * BindingResult: 用于封装验证对象（user）里面的验证结果
	 */
	@RequestMapping("add")
	public String add(@Valid User user,BindingResult result){
		//如果存在验证错误
		if(result.hasErrors()){
			//返回add.html
			return "add";
		}
		
		System.out.println("保存用户:"+user);
		return "succ";
	}
}

```

## 1.3.添加页面 add.html

在添加页面回显错误信息

```
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>用户添加</title>
</head>
<body>
<h3>用户添加</h3>
<form action="/user/add" method="post">
用户名：<input type="text" name="name"/><font color="red" th:errors="${user.name}"></font><br/>
密码：<input type="password" name="password"/><font color="red" th:errors="${user.password}"></font><br/>
年龄：<input type="text" name="age"/><font color="red" th:errors="${user.age}"></font><br/>
邮箱：<input type="text" name="email"/><font color="red" th:errors="${user.email}"></font><br/>
<input type="submit" value="保存"/>
</form>
</body>
</html>
```

这时发生错误:add.html 页面无法绑定 user 对象 解决办法:

![](/Users/chenyansong/Documents/note/images/spring-boot/error6.png)


![](/Users/chenyansong/Documents/note/images/spring-boot/error8.png)


# 2.常用的表单验证注解

```@NotBlank: 判断字符串是否为 null 或空字符串(去掉两边的空格) 
@NotEmpty:判断字符串是否为 null 或空字符串 
@Length:判断字符串长度(包括最小或最大) 
@Min:判断数值类型的最小值@Max:判断数值类型的的最大值@Email: 判断邮箱格式是否合法 www@qq.com
```

![](/Users/chenyansong/Documents/note/images/spring-boot/error7.png)

