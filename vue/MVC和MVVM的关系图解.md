* MVC和MVVM的关系图解

![MVC和MVVM的关系图解](/Users/chenyansong/Documents/note/images/vue/MVC和MVVM的关系图解.png)



* 下面是通过代码，来说明MVVM的关系

```Html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>

    <!--1.导入Vue的包-->
    <script src="./lib/vue-2.4.0.js"></script>
</head>
<body>
    <!--3.将来new的Vue实例，就会控制这个元素中的所有的内容
    Vue 实例控制的这个元素的区域，就是我们MVVM中的 V
    -->
    <div id="app">
        <p>{{ msg }}</p>
    </div>
    <script>
        //2.创建一个Vue的实例
        // 当我们导入包之后，在浏览器中的内存中，就多了一个Vue的构造函数，我们构造的这个实例就是我们MVVM中的 VM
        var vm = new Vue({
            el: '#app', //el表示页面的中的一个元素，表示，当前我们new的这个Vue的实例，要控制页面上的哪个区域
            data:{//data属性中，存放的是el中要用到的数据， data 就相当于我们MVVM中的 Model
                msg:"welcome to vue!" //通过Vue提供的指令，很方便的将数据渲染到页面中，我们就不需要操作dom元素，Vue框架是不提倡我们手动操作dom元素
            }
        })
    </script>
</body>
</html>
```

