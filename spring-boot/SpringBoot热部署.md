下面是两种热部署的方式

* SpringLoader插件
* devtools工具

# 1.SpringLoader插件

## 1.1.pom依赖添加

```
<!-- 导入springloader插件 -->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <dependencies>
                <dependency>
                    <groupId>org.springframework</groupId>
                    <artifactId>springloaded</artifactId>
                    <version>1.2.5.RELEASE</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

## 1.2.启动

```
进入terminal
>mvn spring-boot:run
```

注意：idea需要**设置自动构建**

![](/Users/chenyansong/Documents/note/images/spring-boot/rebushu.png)



## 1.3.缺点

* 后台代码修改，可以实时的部署
* 前端页面修改，无法实时的部署，所以页面修改我们看不到效果
* 这种方式的缺点是程序启动后，在系统后台开启进程，而且需要手动杀死。
* 网上说还有一种方式是引入jar的方式，但是没试过

# 2. devtools工具



## 2.1.pom依赖添加

```
<!-- 热部署模块 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional> <!-- 这个需要为 true 热部署才有效 -->
</dependency>
```


注意：

> 注意2：如果使用Thymeleaf模板引擎，需要把模板默认缓存设置为false

在application.properties中添加：

```
#禁止thymeleaf缓存（建议：开发环境设置为false，生成环境设置为true）
spring.thymeleaf.cache=false
```

每次更改代码，项目都会重新部署，页面不会重新部署，但是刷新是可以看到实时的页面的

参考：

http://www.cnblogs.com/yjmyzz/p/use-devtools-of-spring-boot-framework.html


https://www.cnblogs.com/jiangbei/p/8439394.html 