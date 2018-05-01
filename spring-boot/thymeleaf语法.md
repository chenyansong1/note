# 1.变量输出


```
//变量输出 @RequestMapping("/demo2")public String demo2(Model model){    model.addAttribute("name", "张三");    return "demo2"; 
}
```

```<h3>变量输出</h3><h4 th:text="${name}"></h4> 
<h4 th:text="李四"></h4>
```

# 2.条件判断

```
//条件判断 
@RequestMapping("/demo3")public String demo3(Model model){    model.addAttribute("gender", "女");
    model.addAttribute("grade",3);
    return "demo2"; 
}```

```

<h3>条件判断</h3><div th:if="${gender} == '男'">    这是一位男性朋友</div><div th:if="${gender} == '女'">    这是一位女性朋友</div><br/><div th:switch="${grade}">    <span th:case="1">这是 1 的情况</span> 
    <span th:case="2">这是 2 的情况</span> 
    <span th:case="3">这是 3 的情况</span></div>

```

# 3.迭代遍历

```
//迭代遍历 @RequestMapping("/demo4")public String demo4(Model model){    List<User> list = new ArrayList<User>();
    list.add(new User(1,"eric",20)); 
    list.add(new User(2,"jack",22)); 
    list.add(new User(3,"rose",24));
        model.addAttribute("list", list); 
    return "demo2";
}
```

```
<table border="1"> 
    <tr>        <td>编号</td> 
        <td>姓名</td> 
        <td>年龄</td>    </tr>    <tr th:each="user : ${list}">        <td th:text="${user.id}"></td> 
        <td th:text="${user.name}"></td> 
        <td th:text="${user.age}"></td>    </tr></table>

```


# 4.域对象的使用

* request:我们一般很少用，因为放在request中的数据，我们一般放在model中

* session：一般我们放入的是登陆的信息

* application：全局的域对象


```
//域对象的获取@RequestMapping("/demo5")public String demo5(HttpServletRequest request,Model model){    //request    request.setAttribute("request", "request's data");    //session    request.getSession().setAttribute("session", "session's data");
    
    //application
    request.getSession().getServletContext().setAttribute("application","application's data"); 
    return "demo2";}
```



```
<h3>域对象数据的获取</h3>request: <span th:text="${#httpServletRequest.getAttribute('request')}"></span>
<br/> 
session: <span th:text="${session.session}"></span>
<br/>application: <span th:text="${application.application}"></span>
<br/>
```

![](/Users/chenyansong/Documents/note/images/spring-boot/yuduixiang.png)


# 5.超链接语法


我们正常的超链接的写法，如下：

```
<a href="demo1">访问demo1</a>
<!--href=http://localhost:8080/demo1-->
```


```
<h3>超链接的语法</h3>
<!-- ~表示项目的路径 --><a th:href="@{~/demo1}">访问 demo1</a><br/>

<!--在链接上传递参数--><a th:href="@{~/demo1(id=1,name=eric)}">访问 demo1,传递参数</a>

```



![](/Users/chenyansong/Documents/note/images/spring-boot/chaolianjie.png)



