Play Framework template engine模板引擎&模板隐含变量

# 模板语法

## 表达式：${}

```
<h1>Client ${client.name}</h1>

#If you can’t be sure of client being null, there is a Groovy shortcut:

<h1>Client ${client?.name}</h1>
#Which will only display the client name if the client is not null.
```


## 模板装饰器: #{extends /} and #{doLayout /}

Use #{get} and #{set} tags to share variables between the template and the decorator.
```
#{extends 'simpledesign.html' /}
 
#{set title:'A decorated page' /}
This content will be decorated.
```


simpledesign.html

```
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>#{get 'title' /}</title>
  <link rel="stylesheet" type="text/css" href="@{'/public/stylesheets/main.css'}" />
</head>
<body>
  <h1>#{get 'title' /}</h1>
  #{doLayout /}
  <div class="footer">Built with the play! framework</div>
</body>
</html>
```

在父模板中doLayout就是继承父模板的页面要存放的位置，简单说：就是子页面将插入#{doLayout /}的地方，子页面和父页面共享的变量通过set在子页面中设置，然后通过get在父页面中获取

## 标签：#{tagName /}

举例；下面是一个script标签，去加载一个JavaScript文件
```
#{script 'jquery.js' /}
A tag has to be closed, either directly or by an end tag:

#{script 'jquery.js' /}

#or

#{script 'jquery.js'}#{/script}
```


在play中使用的标签，最后都会被转义，如果你不希望转义，那么使用raw()方法

```
#All dynamic expressions are escaped by the template engine to avoid XSS security issues in your application. So the title variable containing <h1>Title</h1> is now escaped:

${title} --> &lt;h1&gt;Title&lt;/h1&gt;


#If you really want to display it in an unescaped way, you need to explicitely call the raw() method:

${title.raw()} --> <h1>Title</h1>


#Also, if you want to display a large part of raw HTML, you can use the #{verbatim /} tag:

#{verbatim}
    ${title} --> <h1>Title</h1>
#{/verbatim}

```

## Actions:@{…} or @@{…}

在模板中，我们使用@{…}来生成URL。

```
<div class="col-md-2">
    <button class="btn btn-default search" data-json-url="@{DataAPI.getEventDataJson()}" data-table-url="@{DataAPI.getEventDataTable()}">查询</button>
</div>


#对应的HTML如下：
<div class="col-md-2">
    <button class="btn btn-default search" data-json-url="/dataapi/geteventdatajson" data-table-url="/dataapi/geteventdatatable">查询</button>
</div>

```

@@{…}动作做和@{…}一样的事情，不同之处在于它生成的是相对URL（对于email的处理尤其有用。）

## 注释：*{…}* 

注释语法，会被模板引擎自动忽略。 

Comments aren’t evaluated by the template engine. They are just comments…
```
*{**** Display the user name ****}*
<div class="name">
    ${user.name}
</div>
```


## Scripts: %{…}%

脚本比表达式复杂很多。脚本能声明一些变量和使用一些语句。使用%{…}%来插入脚本。 
```
%{ 
   fullName = client.name.toUpperCase()+' '+client.forname; 
}% 

<h1>Client ${fullName}</h1> 
```
脚本也能直接使用out来输出内容 
```
%{ 
   fullName = client.name.toUpperCase()+' '+client.forname; 
   out.print('<h1>'+fullName+'</h1>'); 
}% 
```
同时脚本也能用来做循环遍历等操作 
```
<h1>Client ${client.name}</h1> 
<ul> 
%{ 
     for(account in client.accounts) { 
}% 
     <li>${account}</li> 
%{ 
     } 
}% 
</ul> 
```
需要特别强调的是，模板并不是一个用来做复杂操作的地方。所以更加推荐使用标签，或者把复杂计算移动到控制器、模型中区。 

# 模板继承

模板能继承其他模板，譬如能被作为其他模板的一部分包含进来，要继承其他模板，使用extends语法，如下： 
```
#{extends 'main.html' /} 

<h1>Some code</h1> 
```
main.html模板是一个单独的模板，**它使用#{doLayout /} 来包含其他内容**。 
```
<h1>Main template</h1> 

<div id="content"> 
    #{doLayout /} 
</div> 
```


# 自定义标签

参见：
http://desert3.iteye.com/blog/1553262
https://www.playframework.com/documentation/1.4.x/templates


# 模板中的隐含变量： 
参见：
https://www.playframework.com/documentation/1.4.x/templates

所有被加到renderArgs中的变量都直接变成模板中的变量，在模板中直接使用。 
譬如，你可以如下把控制器中把一个“user”bean注入到模板中。 
renderArgs.put("user", user ); 
当从action中渲染模板时，框架同时会把下面的对象中加到模板中 
即模板中可以直接使用的变量： 
```
session, the Session object
flash, the Flash scope
request, the current Request
params, HTTP params
play, a reference to play.Play
lang, the current language
messages, the messages map
out, the Writer
```



