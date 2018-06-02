springmvc入门程序

[TOC]

商品的列表查询



### 1.前端控制器（dispatcerServlet)



* 1.在web.xml中配置
* 参数contextConfigLocation配置
  * 需要加载的配置文件：（处理器映射器，适配器）
  * config/springmvc.xml
* servlet-mapping
  * *.action 访问以.action结尾的由DispatcherServlet进行解析
  * /  : 所有访问的地址都由DispatcherServlet进行解析（对于静态文件的解析，我们需要配置不让DispatcherServlet进行解析），使用此种方式可以实现RESTful风格
  * /* : 这样配置不对（使用这种配置，最终要转发到一个jsp页面时，仍然会由DispatcherServlet）



### 2.配置处理器适配器





### 3.Hander处理器映射器





### 4.配置视图解析器





### 5.非注解映射器和适配器



### 6.注解映射器和适配器

