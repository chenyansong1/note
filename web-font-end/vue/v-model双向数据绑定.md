[TOC]





* 什么是双向数据绑定？

当我们修改 M 中的数据的时候，V中的数据会实时修改；同时当我们修改V中的数据的时候，M中的数据会被实时修改



# 1.v-bind实现单相数据绑定



当我们修改 vm.msg中的属性的时候，页面的数据就会被修改，如下图：



![image-20180525163302060](/Users/chenyansong/Documents/note/images/vue/v-model.png)

同时，当我们修改了页面的数据的时候，我们的vm.msg也是被修改了的

我们想要页面和我们交互，那么可以使用input



下面是使用v-bind实现的单项数据绑定



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
       <h4>{{ msg }}</h4>

       <!-- : 表示属性绑定 v-bind -->
       <input type="text" :value="msg">
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



> v-bind只能实现数据的单项绑定(**只能实现 从M到V的单项绑定**)





# 2.v-model实现双向数据绑定



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
       <h4>{{ msg }}</h4>

       <!-- : 表示属性绑定 v-bind -->
       <input type="text" v-model="msg">
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





![image-20180525165139945](/Users/chenyansong/Documents/note/images/vue/v-mode3.png)

当我们修改input中的数据的时候，我们可以看到h4中的数据也是对应被修改了

![image-20180525165151864](/Users/chenyansong/Documents/note/images/vue/v-mode4.png)



> v-model只能运用在表单元素中，input，select，checkbox, radio, text, eamil, textarea







