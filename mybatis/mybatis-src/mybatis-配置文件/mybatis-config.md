---
title: mybatis-config配置文件
categories: mybatis   
toc: true  
tag: [mybatis]

---



首先mybatis-config.xml配置文件中，配置了数据库的URL地址，数据库用户名和密码，别名信息，映射文件的位置以及一些全局的配置信息，如下：

```xml
<configuration>
	<!--定义属性值-->
    <properties>
    	<property name="username" value="root" />
        <property name="id" value="123" />
    </properties>
    
    
    <!--全局配置信息-->
    <settings>
    	<setting name="cacheEnabled" value="true" />
        ...
    </settings>
    
    
    <!--配置别名信息，在映射文件中可以直接使用blog这个别名代替 com.xxx.Blog 这个类-->
    <typeAliases>
    	<typeAlias type="com.xxx.Blog" alias="blog" />
    </typeAliases>
    
    
    <!--配置事物，数据库连接等信息-->
    <environments>
    	<environment>
            <!--配置事物管理器的类型-->
        	<transactionManager type="JDBC"/>
            <dataSource type="POOLED">
            	<property name="driver" value="com.mysql.jdbc.Driver"/>
            	<property name="url" value="jdbc:mysql://localhost:3306/test"/>
            	<property name="username" value="root"/>
            	<property name="password" value="123456"/>
            </dataSource>
        </environment>
    </environments>
    
    <!--配置映射文件的位置-->
    <mappers>
		<mapper resource="com/xxx/BlogMapper.xml" />	
    </mappers>

</configuration>

```

