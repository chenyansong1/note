play的标签语法：

官网：
https://www.playframework.com/documentation/1.4.x/tags

#  if else标签
```
#{if user}  
    Connected user is ${user}  
#{/if}  
#{else}  
    Please log in  
#{/else}  
```


# list else标签

else标签页能配置list标签时候，使得当list为空时，显示特殊信息 

```
#{list items:task, as:'task'}  
    <li>${task}</li>  
#{/list}
#{else}  
    Nothing to do...  
#{/else}
```

# elseif标签

也能喝list配合使用 

```
#{if tasks.size() > 1}  
    Busy tasklist  
#{/if}  
   
#{elseif tasks}  
    One task on the list  
#{/elseif}  
   
#{else}  
    Nothing to do  
#{/else}  

```


# error标签

用来输出Validator验证错误的标签。可以再参数中指定被验证的项目。 

```
#{error 'user.name'/}  
```


# get标签

```
<head>
    <title>#{get 'title' /}</title>
</head>

<head>
    <title>#{get 'title'}Homepage #{/get}</title>
</head>

#{if get('title')}
    <h1>#{get 'title' /}</h1>
#{/if}
```


# if标签

```
#{if user.countryCode == 'en' }
    Connected user is ${user}
#{/if}

#{if ( request.actionMethod == 'administer'  && user.isAdmin() ) }
    You are admin, allowed to administer.
#{/if}

```


# include标签

```
#Includes another template. All of the current template’s variables are directly available in the included template.

<div id="tree">
    #{include 'tree.html' /}
</div>

```

# list标签

```
<ul>
#{list items:products, as:'product'}
    <li>${product}</li>
#{/list}
</ul>

/*
The tag defines implicit variables in its body. The variable names are prefixed with the loop variable name.

name_index, the item’s index, starting at 1
name_isLast, true for the last element
name_isFirst, true for the first element
name_parity, alternates between odd and even
*/

<ul>
#{list items:products, as:'product'}
    <span class="${product_parity}">${product_index}. ${product}</span>
    ${product_isLast ? '' : '-'}
#{/list}
</ul>
```

The items parameter is optional and can be replaced by the default arg argument.

```
#{list items:users, as:'user'}
    <li>${user}</li>
#{/list}

#可以写成
#{list users, as:'user'}
    <li>${user}</li>
#{/list}

```

for loops are easy to create using Groovy range object:
```
#{list items:0..10, as:'i'}
    ${i}
#{/list}
#{list items:'a'..'z', as:'letter'}
    ${letter} ${letter_isLast ? '' : '|' }
#{/list}
```


The as parameter is optional as well. It uses _ as default variable name:

```
#{list users}
    <li>${_}</li>
#{/list}
```

# option标签

Insert an option tag in the template.
```
value - option’s value
#{option user.id} ${user.name} #{/option}

#将输出
<option value="42">jto</option>
```

# select标签

```
#Insert a select tag in the template.

#{select 'booking.beds', value:2, id:'select1'}
    #{option 1}One king-size bed#{/option}
    #{option 2}Two double beds#{/option}
    #{option 3}Three beds#{/option}
#{/select}

#将输出
<select name="booking.beds" size="1" id="select1" >
  <option value="1">One king-size bed</option>
  <option value="2" selected="selected">Two double beds</option>
  <option value="3">Three beds</option>
</select>

#第一个参数是select的name,第二个参数是回显的option，第三个参数是select的id值
```

通过items也是可以生成select的

```
#{select 'users', items:users, valueProperty:'id', labelProperty:'name', value:5, class:'test', id:'select2' /}

#将输出的HTML为：
<select name="users" size="1" class="test" id="select2" >
  <option value="0" >User-0</option>
  <option value="1" >User-1</option>            
  <option value="2" >User-2</option>            
  <option value="3" >User-3</option>
  <option value="4" >User-4</option>
  <option value="5" selected="selected">User-5</option>
</select>
```

* items (optional) - list of objects, used to create options
* value (optional) - selected element in items (note that multiple selections are not supported)
* labelProperty (optional) - for each item, attribute used as option’s label
* valueProperty (optional) - for each item, attribute used as option’s value. id is used by default


# set标签

```
#{set title:'Admin' /}
#{set style:'2columns' /}


#You can also use variables:
#{set title:'Profile of ' + user.login /}

#You can define the value of variables in the body:
#{set 'title'}
    Profile of ${user.login}
#{/set}
```
