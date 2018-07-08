# 官网文档

http://www.mybatis.org/mybatis-3/zh/dynamic-sql.html



* 为什么要执行下面的动作？
  * 1.首次是进行的占位符，可能查询还在继续中，别人不能使用该key
  * 2.移除是为了去掉，此时已经有了查询结果
  * 3.最后，放入的是查询的结果





* Lazy loading
* 在spring中使用mybatis
* 如果单独的使用是programming ，如果和spring进行整合那就是managed