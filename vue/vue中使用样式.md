[TOC]

# 1.使用class样式

## 1.2.传统的class属性赋值



```Html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>


    <script src="./lib/vue-2.4.0.js"></script>

    <style>
        .red{
            color:red;
        }
        .thin{
            font-weight: 200;
        }
        .italie{
            font-style: italic;
        }
        .active{
            letter-spacing: 0.5em;
        }
    </style>
</head>
<body>
    
    <div id="app">
	   <!--这里我们引用，定义的样式-->
       <h1 class="red thin"> 这是一个H1</h1>
    </div>

    <script>
        var vm = new Vue({
            el:'#app',
            data:{
                msg:'我们都是好学生'

            },
            methods:{ // methods属性中定义了当前Vue实例所有的可用的方法
               
            }
        })
    </script>
</body>
</html>
```



得到下面的样式



![image-20180525171416304](/Users/chenyansong/Documents/note/images/vue/style-1.png)



## 1.2.使用属性绑定的形式，为class指定属性值

如果使用属性绑定的形式，需要我们加上引号，不然Vue会将其当做变量，保存如下：

![image-20180525172233912](/Users/chenyansong/Documents/note/images/vue/style-2.png)



下面是几种属性绑定的方式

```Html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>


    <script src="./lib/vue-2.4.0.js"></script>

    <style>
        .red{
            color:red;
        }
        .thin{
            font-weight: 200;
        }
        .italie{
            font-style: italic;
        }
        .active{
            letter-spacing: 0.5em;
        }
    </style>
</head>
<body>
    
    <div id="app">

       <!-- <h1 class="red thin"> 这是一个H1</h1> -->

       <!--这种方式是错误的，Vue会将red当做一个变量去data中找，但是此时是找不到的，我们需要将属性变成字符串-->
       <!-- <h1 :class="[red,thin]"> 这是一个H1</h1> -->

       <!--方式1：直接传递一个数组，这里的class需要使用 v-bind进行数据绑定，里面的数据必须是字符串-->
       <!-- <h1 :class="['red', 'thin']"> 这是一个H1</h1> -->

        <!--方式2：在数组中使用三元表达式，这里的flag就是一个数据变量了-->
        <!-- <h1 :class="['red', 'thin', flag?'active':'']"> 这是一个H1</h1> -->

        <!--方式3：在数组中使用对象,这样代码可读性更好，这里的flag就是一个数据变量了-->
        <!-- <h1 :class="['red', 'thin', {'active':flag}]"> 这是一个H1</h1> -->

        <!--方式4：直接使用对象-->
        <!-- <h1 :class="{active:true, italic:false}"> 这是一个H1</h1> -->
        <h1 :class="classObj"> 这是一个H1</h1>

    </div>

    <script>
        var vm = new Vue({
            el:'#app',
            data:{
                flag:true,
                classObj:{active:true, italic:false}

            },
            methods:{ // methods属性中定义了当前Vue实例所有的可用的方法
               
            }
        })
    </script>
</body>
</html>
```





# 2.使用内联样式(行内样式)

 ## 2.1.绑定对象

```Html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>


    <script src="./lib/vue-2.4.0.js"></script>

</head>
<body>
    
    <div id="app">

        <!-- 方式1：直接通过bind绑定一个对象，来设置样式-->
        <h1 :class="{color:'red', 'font-weight':200}"> 这是一个H1</h1>
        <!--也是可以将对象放在data中的-->
        <h1 :class="classObj"> 这是一个H1</h1>
        

    </div>

    <script>
        var vm = new Vue({
            el:'#app',
            data:{
                classObj:{color:'red', 'font-weight':200}
            },
            methods:{ // methods属性中定义了当前Vue实例所有的可用的方法
               
            }
        })
    </script>
</body>
</html>
```



## 2.2.设置数组对象

```Html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>


    <script src="./lib/vue-2.4.0.js"></script>

</head>
<body>
    
    <div id="app">

        <!-- 方式1：直接通过bind绑定一个对象，来设置样式-->
        <!-- <h1 :class="{color:'red', 'font-weight':200}"> 这是一个H1</h1> -->
        <!--也是可以将对象放在data中的-->
        <h1 :class="[classObj1, classObj2]"> 这是一个H12</h1>
        

    </div>

    <script>
        var vm = new Vue({
            el:'#app',
            data:{
                classObj1:{color:'red'},
                classObj2:{'font-style':'italic'}
            },
            methods:{ // methods属性中定义了当前Vue实例所有的可用的方法
               
            }
        })
    </script>
</body>
</html>
```

