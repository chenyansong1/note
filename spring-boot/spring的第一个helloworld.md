
# 1.建立一个maven工程，pom文件

* 使用idea建立一个maven工程（指定3坐标），直接下一步，下一步，
* 然后新建包
* 最后需要向pom文件中添加依赖，如下

```

    <groupId>cn.cm.hello</groupId>
    <artifactId>spring-hello</artifactId>
    <version>1.0-SNAPSHOT</version>


    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.1.RELEASE</version>
    </parent>

    <!--这里添加一些属性-->
    <properties>
        <java.version>1.8</java.version>
    </properties>



    <!--引入 Web 启动器，引入web支持的坐标，spring MVC，Servlet, Filter, Listener等，引入一个web的启动器-->
    <!--会引入aop,beans.context,core,expression,web,webmvc-->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

```


# 2.编写一个controller

```
package cn.sn1234.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */

//@Controller
@RestController// 用于替代Controller，ResponseBody
//@RequestMapping("/hello")
public class HelloController {


    private Map<String, Object> result = new HashMap<String, Object>();

    @RequestMapping("/hello")// 用于请求
    //@ResponseBody //转换json的注解，其实在导入web的时候，就会导入跟json相关的jar（jackson-annotations, jackson-core, jackson-databind）
    public Map<String, Object> hello(){

        result.put("name", "zhangsan");
        result.put("age", "21");
        result.put("gender", "男");

        return result;
    }


}

```

# 3.编写启动器

```
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */
@SpringBootApplication//这个注解必须要
public class Application {
    public static void main(String[] args){

        // 在这个启动器中，指定启动类，和参数
        SpringApplication.run(Application.class, args);

    }
}

```


# 4.运行程序

在启动类中，右键->run main ,这样就能启动

**启动日志如下**

![](/Users/chenyansong/Documents/note/images/spring-boot/spring-hello.png)

通过日志可以看到，会将controller有一个映射

**浏览器访问**

![](/Users/chenyansong/Documents/note/images/spring-boot/spring-hello2.png)



# 5.需要注意的问题

1. application的作为一个启动类，必须在controller的外一层，不然启动的时候会报错

2. 在controller的类上，加上一个RestController，那么可以就可以省略@Controller,@ResponseBody, 其实进去看RestController就可以看到，RestController里面就存在这两个注解