![](/Users/chenyansong/Documents/note/images/mybatis/yuanli.png)


# 1.工程架构说明


# 2.配置文件


# 3.CRUD功能的开发


## 3.1.创建映射文件



需要指定输入映射，输出映射


# 4.程序代码

${} 可以拼接字符串，如果传入的参数类型是简单类型，那么${}中只能是value，


# 5.映射







# SQLSession的使用范围



线程不安全的


原始dao的开发问题

1.dao大量的模板代码重复的问题

2.设想：能否将这些公共的方法提取出来，


3.调用的sqlSession方法时，将statement 的ID硬编码了

4.如果在传入参数的时候，传入的变量错误了，由于SQLSession使用环形，那么在编译阶段不报错，这样就不能很好的发现错误


基于上面的原因，于是出现了mapper接口的形式



程序员需要：
1.需要编写mapper接口（相当于dao接口）
2.程序员还需要编写mapper.xml映射文件

mybatis可以自动生成mapper接口实现类的代理对象


namespace就是mapper接口的地址

mapper.java中的接口的方法名和mapper.xml中statement的id要一致

mapper.java接口中的方法的输入参数类型和mapper.xml中statement的parameterType指定的类型一样

mapper.java接口中的方法的返回值的类型和映射文件中statement的resultType一致


**也就是对原来dao的代理自动生成，这样程序员只需要将关注点放在xml中的SQL中即可**



通过SQLSession来创建Mapper的代理对象，然后就可以使用代理对象了


mapper接口方法的参数只有一个是否影响 实际的应用开发

系统的框架中，dao层的代码是被业务层公用的，即使mapper接口只有一个参数，可以使用包装乐心的pojo满足不同的业务方法的需求，

注意：你的持久层中方法的参数可以使用包装类型，但是你的Service方法中的建议不要使用包装类型（因为Service方法是给其他业务调用，如果使用包装类型是不利于业务的调用的）

# sqlMapconfig.xml



只能说是模块化，更加的清晰

只是说方面对配置参数进行统一的管理，可以供其他的应用

在properties中加载外部的属性文件


属性文件

设置属性的顺序

注意：
1.不要讲properties中添加任何的属性，尽管是可以这样做的，但是并不建议，
2.在外部属性的文件编写的时候，尽量将属性名定义有一定的特殊性 xx.yyy.xx


settings全局的参数配置


别名的定义

默认是有一些别名和基本类型的映射，但是对于我们自定义的类，我们需要为其定义别名 

使用别名的时候，可以大写别名(User)，也可以小写别名（user)



<mapper class="" /> 通过类去加载xml

1.mapper接口和mapper.xml映射的文件名称保持一致
2.并且两个文件在同一个目录下


# 输入映射

pojo的包装对象


UserQueryVo 是视图层的对象

UserResultPo是持久层的对象

对于包装类型，其实就是对原有对象的扩展，如User可以扩展为UserCustomer，然后我们可以在UserCustomer类中添加额外的属性，这样不至于对原来的类进行污染，

用户信息综合查询（查询条件复杂）




# 输出映射

查询指定的字段


查询出来的列名和pojo的属性名 要一致

只有查询出来的结果集只有一行，且只有一列，那么可以使用简单类型来进行映射



如果输出的是一个map，那么xml中的requestType=hashmap,
java 接口中写成List<Map>或者Map 这个具体要看查询的数据是多少行，然后查询数据的每一行列名作为map的key,列值作为map的value



# 动态SQL


定义SQL片段，这样可以重复使用

**其实就是拼 where条件的情况**


1.在SQL片段中一般不要包含where，这样有利于条件的拼接

用于查询关联查询单个对象的信息，里面的就是配置关联对象的属性














