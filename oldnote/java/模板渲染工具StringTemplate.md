模板渲染工具StringTemplate
ji


解决：多个条件拼接，避免在代码中增加判断的问题

https://www.jianshu.com/p/d87ef30a2f63


https://github.com/antlr/stringtemplate4/blob/master/doc/java.md



# 两种填充数据的方式

```
# 1.add


ST hello = new ST("Hello, <name>");
hello.add("name", "World");
System.out.println(hello.render());


# 2.addAggr


ST hello = new ST(jsonTempleteCoentent);
hello.addAggr("ip.{srcip,dstip}", "SRCIP", "DStIPxx");
System.out.println(hello.render());


#下面是一个json的模板

{
  <!-------------- query 查询条件 --------------!>
  "query":{
    "term":{
      <if(ip.srcip)>
        "srcip":"<ip.srcip>"
        "dstip":"<ip.dstip>"
      <endif>
    }
  }
}


```