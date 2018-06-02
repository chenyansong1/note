springmvc和mybatis整合



# 1.整合思路

```
业务层
表现层
```





1. 整合持久层
  mybatis和spring的整合，通过spring管理mapper接口，使用mapper的扫描器自动扫描mapper接口在spring中进行注册

2. 整合Service

  通过spring管理Service接口，使用配置的方式将Service接口在spring配置文件中，这里需要实现事物控制

3. 整合springmvc

   由于springmvc是spring的模块，所以不需要整合

   

# 2.配置





# 3.逆向工程生成po类

针对的是单表



针对有关联查询的情况，我们需要自定义mapper

* 定义



# 4.整合Service

