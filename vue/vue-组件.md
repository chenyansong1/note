[TOC]



* 组件：是为了拆分Vue实例的代码量的，能够让我们以不同的组件，来划分不同的功能的模块，将来我们需要什么样的功能，就可以调用对应的组件即可
* 组件化和模块话的不同
  * 模块化：从代码逻辑的角度进行划分的，方便后台代码的分层开发，保证每个功能模块的 **职能单一**
  * 组件化：从UI界面的角度进行划分的，方便UI组件的重用



## 1.创建组件的helloworld



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

    <div id="app">
        <!-- 2.使用组件：直接把组件的名称，以HTML标签的形式，引入到页面中即可-->
        <!-- 如果组件的名称在定义的时候，使用的是大小写的驼峰的命名形式，如：myCom1 ,那么在使用的时候，必须是 <my-com1></my-com1>-->
        <mycom1></mycom1>
        
    </div>

    <script>

        // 1.1.使用Vue.extend来参见全局的Vue的组件
        var com1 = Vue.extend({
            template:'<h3>这是使用vue.extend创建的组件 </h3>'// 指定了组件要展示的HTML结构
        })


        // 1.2.使用Vue.component('组件的名称', 组件创建出来的模板对象)
        Vue.component('mycom1', com1)
        
        var vm = new Vue({
            el: '#app', 
            data:{
                msg:"welcome to vue!" 
            },
            methods:{
          
            }

        })

    </script>
</body>
</html>
```

## 2.创建组件的三种方式

### 2.1.方式1

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
    <!--vue-resource 依赖于Vue， 所以注意导入的先后顺序-->
    <script src="./lib/vue-resource-1.3.4.js"></script>
</head>


<body>

    <div id="app">
        <p>{{ msg }}</p>
        <!--get请求-->
        <input type="button" value="修改msg方法" @click="getInfo">

        <!-- 2.使用组件：直接把组件的名称，以HTML标签的形式，引入到页面中即可-->
        <!-- 如果组件的名称在定义的时候，使用的是大小写的驼峰的命名形式，如：myCom1 ,那么在使用的时候，必须是 <my-com1></my-com1>-->
        <mycom1></mycom1>
        
    </div>

    <script>

        // // 1.1.使用Vue.extend来参见全局的Vue的组件
        // var com1 = Vue.extend({
        //     template:'<h3>这是使用vue.extend创建的组件 </h3>'// 指定了组件要展示的HTML结构
        // })


        // // 1.2.使用Vue.component('组件的名称', 组件创建出来的模板对象)
        // Vue.component('mycom1', com1)


        // 可以将上面的方式进行合并，得到下面的结果
        Vue.component('mycom1', Vue.extend({
            //组件必须在唯一的一个根元素下
            template:'<h3>这是使用vue.extend创建的组件 </h3>'// 指定了组件要展示的HTML结构
        }))
        
        var vm = new Vue({
            el: '#app', 
            data:{
                msg:"welcome to vue!" 
            },
            methods:{
          
            }

        })




    </script>
</body>
</html>
```



### 2.2.方式2



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
    <!--vue-resource 依赖于Vue， 所以注意导入的先后顺序-->
    <script src="./lib/vue-resource-1.3.4.js"></script>
</head>


<body>

    <div id="app">
        <!-- 2.使用组件：直接把组件的名称，以HTML标签的形式，引入到页面中即可-->
        <!-- 如果组件的名称在定义的时候，使用的是大小写的驼峰的命名形式，如：myCom1 ,那么在使用的时候，必须是 <my-com1></my-com1>-->
        <mycom1></mycom1>
        
    </div>

    <script>

        // // 1.1.使用Vue.extend来参见全局的Vue的组件
        // var com1 = Vue.extend({
        //     template:'<h3>这是使用vue.extend创建的组件 </h3>'// 指定了组件要展示的HTML结构
        // })


        // // 1.2.使用Vue.component('组件的名称', 组件创建出来的模板对象)
        // Vue.component('mycom1', com1)


        // // 方式1：可以将上面的方式进行合并，得到下面的结果
        // Vue.component('mycom1', Vue.extend({
        //     template:'<h3>这是使用vue.extend创建的组件 </h3>'// 指定了组件要展示的HTML结构
        // }))


        // 方式2：直接通过一个对象创建
        Vue.component('mycom1', {
            // 组件必须在唯一的一个根元素下
            // template:'<h3>这是使用vue.extend创建的组件 </h3><a>链接</a>'
            template:'<div><h3>这是使用vue.extend创建的组件 </h3><a>链接</a></div>'
        })


        
        var vm = new Vue({
            el: '#app', 
            data:{
                msg:"welcome to vue!" 
            },
            methods:{
          
            }

        })


    </script>
</body>
</html>
```





### 2.3.方式3

通过template标签写模板

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
    <!--vue-resource 依赖于Vue， 所以注意导入的先后顺序-->
    <script src="./lib/vue-resource-1.3.4.js"></script>
</head>


<body>

    <div id="app">
        <!-- 2.使用组件：直接把组件的名称，以HTML标签的形式，引入到页面中即可-->
        <!-- 如果组件的名称在定义的时候，使用的是大小写的驼峰的命名形式，如：myCom1 ,那么在使用的时候，必须是 <my-com1></my-com1>-->
        <mycom1></mycom1>
        
    </div>

    <!-- 在被Vue控制的#app外部，使用template元素，定义组件的HTML模板结构-->
    <template id="tmp1">
        <!--和其他方式一样，组件也是必须只能有一个根元素-->
        <div>
            <h1>这是一个template在外组件定义的元素，这种方式有代码的提示</h1>
            <h4>very good</h4>
        </div>
    </template>



    <script>

        // 方式3
        Vue.component('mycom1', {
            template:'#tmp1'
        })


        
        var vm = new Vue({
            el: '#app', 
            data:{
                msg:"welcome to vue!" 
            },
            methods:{
          
            }

        })


    </script>
</body>
</html>
```



## 3.定义私有组件

上面定义的全局组件是可以被多个Vue实例使用，如果想要定义只被特定Vue实例控制的组件，那么使用私有定义即可

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
    <!--vue-resource 依赖于Vue， 所以注意导入的先后顺序-->
    <script src="./lib/vue-resource-1.3.4.js"></script>
</head>


<body>

    <div id="app">
        <!-- 2.使用组件：直接把组件的名称，以HTML标签的形式，引入到页面中即可-->
        <!-- 如果组件的名称在定义的时候，使用的是大小写的驼峰的命名形式，如：myCom1 ,那么在使用的时候，必须是 <my-com1></my-com1>-->
        <mycom1></mycom1>

        <!--使用私有的组件-->
        <login></login>
    </div>

    <!-- 在被Vue控制的#app外部，使用template元素，定义组件的HTML模板结构-->
    <template id="tmp1">
        <!--和其他方式一样，组件也是必须只能有一个根元素-->
        <div>
            <h1>这是一个template在外组件定义的元素，这种方式有代码的提示</h1>
            <h4>very good</h4>
        </div>
    </template>

    

    <template id="tmp2">
            <h1>这是私有的login组件</h1>
    </template>

    <script>

        // 方式3
        Vue.component('mycom1', {
            template:'#tmp1'
        })


        
        var vm = new Vue({
            el: '#app', 
            data:{
                msg:"welcome to vue!" 
            },
            methods:{
          
            },

            // 定义实例内部的私有组件
            components:{
                login:{
                    // template:'<h1>这是私有的login组件</h1>'
                    template:'#tmp2'
                }
            }

        })


    </script>
</body>
</html>
```





## 4.组件中的data和methods

* 组件中的data,必须是一个函数，并且函数返回的是必须是一个对象
* 组件中的模板也是可以使用 在组件中定义的data,使用方式和Vue实例中使用data的方式完全一样



下面的代码是在组件中定义了一个点击计数的组件

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
    <!--vue-resource 依赖于Vue， 所以注意导入的先后顺序-->
    <script src="./lib/vue-resource-1.3.4.js"></script>
</head>


<body>

    <div id="app">
        <!-- 使用组件-->
        <mycom1></mycom1>
    </div>

    

    <template id="tmp1">
        <div>
            <input type="button" value="+1" @click="increament">
            <h3>{{ count }}</h3>
        </div>

    </template>

    <script>

        var dataObj = { count:0}

        // 方式3
        Vue.component('mycom1', {
            template:'#tmp1',
           
            // 组件中的data,必须是一个函数，并且函数返回的是必须是一个对象
            data:function(){

                // return dataObj
                return {count:0}
            },
            // 这里在组件中定义了 方法
            methods:{
                increament(){
                    // 对组件的数据进行 加1 操作
                    this.count += 1
                }
            }
        })


        
        var vm = new Vue({
            el: '#app', 
            data:{
                msg:"welcome to vue!" 
            },
            methods:{
          
            }

        })


    </script>
</body>
</html>
```





## 5.不同组件之间的切换

应用场景：点击按钮，切换到不同的组件

### 5.1.方式1

通过v-if, v-else + flag进行组件切换

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
    <!--vue-resource 依赖于Vue， 所以注意导入的先后顺序-->
    <script src="./lib/vue-resource-1.3.4.js"></script>
</head>


<body>

    <div id="app">
        <a href="" >登录</a>
        <a href="" >注册</a>

    
        <login v-if="flag"></login>
        <regitser v-else="flag"></regitser>
    </div>

    

    <template id="tmp_login">
        <div>
            <h3>登录页面</h3>
        </div>
    </template>

    <template id="tmp_regitser">
            <div>
                <h3>注册页面</h3>
            </div>
    </template>

    <script>

        var dataObj = { count:0}


        Vue.component('login', {
            template:'#tmp_login'
        })

        Vue.component('regitser', {
            template:'#tmp_regitser'
        })


        
        var vm = new Vue({
            el: '#app', 
            data:{
                flag:true
            },
            methods:{
          
            }

        })


    </script>
</body>
</html>
```



### 5.2.方式2

使用component来展示组件

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>

    <!--1.导入Vue的包-->
    <script src="./lib/vue-2.4.0.js"></script>
    <!--vue-resource 依赖于Vue， 所以注意导入的先后顺序-->
    <script src="./lib/vue-resource-1.3.4.js"></script>
</head>


<body>

    <div id="app">

        <a href="" @click.prevent="componName='login'">登录</a>
        <a href="" @click.prevent="componName='regitser'">注册</a>
        <!-- Vue 提供了 component，来展示对应名称的组件
        他就是一个占位符， :is 属性：可以用来指定要展示组件的名称 ,这样我在这里指定哪个组件，
        就会显示哪个组件 -->
        <!-- <component :is="'regitser'"></component> -->
        <component :is="componName"></component>
    </div>

    

    <template id="tmp_login">
        <div>
            <h3>登录页面</h3>
        </div>
    </template>

    <template id="tmp_regitser">
            <div>
                <h3>注册页面</h3>
            </div>
    </template>

    <script>

        var dataObj = { count:0}

        // 登录组件
        Vue.component('login', {
            template:'#tmp_login'
        })

        // 注册组件
        Vue.component('regitser', {
            template:'#tmp_regitser'
        })

        
        var vm = new Vue({
            el: '#app', 
            data:{
                flag:true,
                componName:'regitser'// 组件名称
            },
            methods:{
          
            }

        })


    </script>
</body>
</html>
```



