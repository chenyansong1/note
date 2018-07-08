[TOC]



lombok

可以省略get ,set方法

 

#### mybatis中作用域（生命周期）

| 类                       | 作用域（scope）                                              |
| ------------------------ | ------------------------------------------------------------ |
| SqlSessionFactoryBuilder | 只使用一次（method）                                         |
| SqlSessionFactory        | 是全局级别的（application），单例                            |
| SqlSession               | 是一个session中有效，request/method（可以认为是线程级别的）  |
| Mapper                   | 是方法级别的（method），在编程式中，mybatis是method级别的，只使用一次，但是在和spring集成中，Mapper是一个单例的，并不是在使用完成之后释放，而是放了容器中 |

#### mybatis的配置方式



Mapper的xml或者注解的方式配置SQL，这两种方式可以退同时存在，但是不能使用同一个key同时存在

| 配置方式   | Pros                                                     | Cons                                 |
| ---------- | -------------------------------------------------------- | ------------------------------------ |
| Mapper.xml | 跟接口分离，统一管理；复杂的语句可以不影响接口的可读性   | 过多的xml文件                        |
| Annotation | 接口就能看到SQL语句，可读性高，不需要再去找xml文件，方便 | 复杂的联合查询不好维护，代码可读性差 |

#### config文件部分解读

* environment:配置数据源，事物管理器等

* typeHandler:就是MySQL字段类型和Java类型的映射，mybatis有许多默认的TypeHandler
  * 1.自定义的TypeHandler
    * 继承BaseTypeHandler
    * 去配置文件中注册：setTypeHandler

* Plugins

  例如：分页就是使用的插件，他就是一个拦截器，只能拦截下面的东西

  

  ![image-20180619223304382](/Users/chenyansong/Documents/note/images/mybatis/plugins.png)

