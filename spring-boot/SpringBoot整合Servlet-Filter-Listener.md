SpringBoot整合Servlet-Filter-Listener


Spring Boot 使用 Servlet 的 API 有两种方法: 
1)使用@ServletComponentScan 注解 
2)使用@Bean 注解


# 1.Servlet


## 1.1.写一个Servlet类

涉及到3点，需要注意：

1.继承HttpServlet
2.添加注解WebServlet（相当于配置web.xml文件，这里需要指定访问的URL）
3.重写doGet方法

```
package cn.sn1234.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */

// 这里其实就是用注解代替xml的配置
/*
等同于web.xml配置
    <!--指定servlet的名字-->
    <servlet>
        <servlet-name></servlet-name>
        <servlet-class>cn.sn1234.servlet.HelloServlet</servlet-class>
    </servlet>
    <!--为指定名称的servlet，配置一个映射，访问的时候，就是这个映射-->
    <servlet-mapping>
        <servlet-name>helloServlet</servlet-name>
        <url-pattern>/helloServlet</url-pattern>
    </servlet-mapping>
 */
@WebServlet(name="helloServlet", urlPatterns = "/helloServlet") // WebServlet声明该类为Servlet程序
public class HelloServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(" 执行了 helloServlet 的 doGet方法");

    }
}

```

## 1.2.在application启动类中配置，扫描servlet

```
package cn.sn1234;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */
@SpringBootApplication
@ServletComponentScan   // 扫描webServlet注解,这个注解除了可以扫描servlet注解，还能Filter，listener
public class Application {
    public static void main(String[] args){

        // 在这个启动器中，指定启动类，和参数
        SpringApplication.run(Application.class, args);

    }
}

```


## 1.3.启动application，浏览器访问

* 启动

![](/Users/chenyansong/Documents/note/images/spring-boot/servlet.png)

* 浏览器访问

> http://localhost:8080/helloServlet

* 后台打印如下

![](/Users/chenyansong/Documents/note/images/spring-boot/servlet2.png)



# 2.filter


## 2.1.写一个Filter类,实现接口

涉及到3点，需要注意：

1.实现Filter
2.添加注解WebFilter（相当于配置web.xml文件，这里需要指定filter的名称，拦截的URL）
3.重写方法doFilter


```
package cn.sn1234.servlet;

import org.springframework.context.annotation.ComponentScan;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */
/*
这里就是配置一个filter：
filterName : 指定了filter的名字
urlPatterns : filter拦截的路径
 */
@WebFilter(filterName = "helloFilter", urlPatterns = "/helloServlet")
public class HelloFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        System.out.println("执行了前面的代码");

        // 放行，执行目标资源：HelloServlet
        filterChain.doFilter(servletRequest, servletResponse);

        System.out.println("执行了后面的代码");

    }

    @Override
    public void destroy() {

    }
}

```

## 2.2.在application启动类中配置，扫描filter

```
package cn.sn1234;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */
@SpringBootApplication
@ServletComponentScan   // 扫描webServlet注解,这个注解除了可以扫描servlet注解，还能Filter，listener
public class Application {
    public static void main(String[] args){

        // 在这个启动器中，指定启动类，和参数
        SpringApplication.run(Application.class, args);

    }
}

```


## 2.3.启动application，浏览器访问

* 启动

![](/Users/chenyansong/Documents/note/images/spring-boot/filter1.png)

* 浏览器访问

> http://localhost:8080/helloServlet

* 后台打印如下

![](/Users/chenyansong/Documents/note/images/spring-boot/filter2.png)



# 3.listener

## 3.1.创建一个listener

* 实现ServletContextListener
* 实现方法

```
package cn.sn1234.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */

@WebListener
public class HelloListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("ServletContext 对象消耗了");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("ServletContext 对象创建了");

    }
}

```


## 3.2.启动application

查看日志如下

* 后台打印如下

![](/Users/chenyansong/Documents/note/images/spring-boot/listener1.png)




上面的方式是通过@ServletComponentScan的方式扫描到servlet，filter，listener等，下面是使用@Bean的方式去向application注册

# 使用@Bean的方式

* servlet

* filter

* listener


```
package cn.sn1234;

import cn.sn1234.servlet.HelloFilter;
import cn.sn1234.servlet.HelloListener;
import cn.sn1234.servlet.HelloServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletContextListener;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */
@SpringBootApplication
//@ServletComponentScan   // 扫描webServlet注解,这个注解除了可以扫描servlet注解，还能Filter，listener
public class Application {
    public static void main(String[] args){

        // 在这个启动器中，指定启动类，和参数
        SpringApplication.run(Application.class, args);

    }


    // 注册Servlet程序
    // 1.设置Bean
    @Bean
    // 2.返回 ServletRegistrationBean
    public ServletRegistrationBean getServletRegistrationBean(){

        // 3.将HelloServlet注册到Bean中
        ServletRegistrationBean bean = new ServletRegistrationBean(new HelloServlet());

        // 4.为这个servlet，设置访问路径
        bean.addUrlMappings("/helloServlet");
        return bean;

    }

    // 注册filter
    // 1.设置Bean
    @Bean
    // 2.返回 FilterRegistrationBean
    public FilterRegistrationBean getFilterRegistrationBean(){

        // 3.将 HelloFilter 注册到Bean中
        FilterRegistrationBean bean = new FilterRegistrationBean(new HelloFilter());


        // 4.设置拦截的路径
        bean.addUrlPatterns("helloServlet");
        return bean;

    }

    @Bean
    public ServletListenerRegistrationBean getServletListenerRegistrationBean(){

        ServletListenerRegistrationBean bean = new ServletListenerRegistrationBean(new HelloListener());

        return bean;

    }
}

```

