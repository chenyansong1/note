# 1.引入maven的依赖

```
        <!-- thymeleaf -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
```


# 1.建立一个controller

```
package cn.sn1234.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */

@Controller
public class UserController {

    @RequestMapping("demo1")
    public String demo1(Model model){

        // model相当于一个response，这里：向返回response中添加数据
        model.addAttribute("message", "this is a demo thymleaf");

        // 跳转到demo1.html （/templates/demo1.html）
        return "demo1";
    }
}

```


# 2.写Thymeleaf模板页面

```
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8" />
    <title>Thymeleaf入门案例</title>
</head>
<body>
    <span th:text="${message}"></span>
</body>
</html>
```

当我们访问时
> http://localhost:8080/demo1 ，会报错

![](/Users/chenyansong/Documents/note/images/spring-boot/Thymeleaf1.png)

> 后台报错

![](/Users/chenyansong/Documents/note/images/spring-boot/Thymeleaf2.png)


原因:Thymeleaf3.0 以下的版本就会严格要求 html 页面上所有标签都要结束。 

解决的方法
1.添加结束标签（只能暂时解决问题）

![](/Users/chenyansong/Documents/note/images/spring-boot/Thymeleaf5.png)

我们再次访问：

![](/Users/chenyansong/Documents/note/images/spring-boot/Thymeleaf3.png)


2.根本的解决方式，不添加结束标签，但是升级thymeleaf
把 thymeleaf 的版本升级到 3.0 以上的版本!

> 现在的版本

![](/Users/chenyansong/Documents/note/images/spring-boot/Thymeleaf4.png)


在pom中添加版本升级

![](/Users/chenyansong/Documents/note/images/spring-boot/Thymeleaf6.png)



 