# 1.在maven中导入freemarker的启动器(依赖坐标)

```
        <!-- freemarker -->        <dependency>             <groupId>org.springframework.boot</groupId>             <artifactId>spring-boot-starter-freemarker</artifactId>        </dependency>
```

# 2.写一个controller


```
package cn.sn1234.controller;

import cn.sn1234.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */

@Controller
public class UserController {

    @RequestMapping("/list")
    public String list(Model model){// 这里的model就相当于response，我们只需要将参数放入到response中，然后返回对应的页面名称，然后让页面加载response中的数据


        List<User> userList = new ArrayList<User>();

        // 模拟用户数据
        userList.add(new User(1,"zhangSan",28));
        userList.add(new User(2,"lisi",26));
        userList.add(new User(3,"wangwu",22));


        // 把数据存入model
        model.addAttribute("list", userList);



        // 跳转到freemarker页面: list.ftl
        return "list";
    }

}

```

# 3.建立 list.ftl 文件

注意:首先需要在 src/main/resources 目录下新建 templates 目录。

下面的语法使用的是freemarker的语法 

```
<html> <title>用户列表展示</title> <meta charset="utf-8"/> <body><h3>用户列表展示</h3> <table><tr> <th>编号</th><th>姓名</th><th>年龄</th> </tr>             <#list list as user>             <tr>                 <td>${user.id}</td>                 <td>${user.name}</td>                 <td>${user.age}</td></tr>             </#list>         </table>    </body></html> 
```

