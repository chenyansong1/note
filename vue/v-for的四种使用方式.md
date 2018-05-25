[TOC]

## 1.迭代数组

### 1.1.迭代普通的数组



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

        <!--遍历数组中的元素-->
        <p v-for="item in list">{{item}}</p>

        <!--如果我们要拿到数组的index-->
        <p v-for="(item,index) in list">{{item}}---{{index}}</p>

    </div>

    <script>
        var vm = new Vue({
            el:'#app',
            data:{
                list:[1,2,3,4,5]
            },
            methods:{ // methods属性中定义了当前Vue实例所有的可用的方法
               
            }
        })
    </script>
</body>
</html>
```



### 1.2.迭代对象数组

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

        <!--遍历数组中的元素-->
        <p v-for="user in list">{{user.id}}-{{user.name}}</p>
        
        <!--遍历数组中的元素-->
        <p v-for="(user,index) in list">{{user.id}}-{{user.name}}-{{index}}</p>

    </div>

    <script>
        var vm = new Vue({
            el:'#app',
            data:{
                list:[
                    {id:1, name:'zhangsan'},
                    {id:2, name:'lsi'},
                    {id:3, name:'wangwu'}
                ]
            },
            methods:{ // methods属性中定义了当前Vue实例所有的可用的方法
               
            }
        })
    </script>
</body>
</html>
```





## 2.迭代对象

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

        <!--遍历数组中的元素-->
        <p v-for="(val,key) in user">值={{val}}---键={{key}}</p>
        
        <!-- 遍历对象上的键值对，除了有 val,key,之外也是有一个索引的-->
        <p v-for="(val,key,index) in user">值={{val}}---键={{key}}---索引={{index}}</p>

    </div>

    <script>
        var vm = new Vue({
            el:'#app',
            data:{
                user:{
                    id:1,
                    name:'zhangsan',
                    gender:'man'
                }
            },
            methods:{ // methods属性中定义了当前Vue实例所有的可用的方法
               
            }
        })
    </script>
</body>
</html>
```



## 3.迭代数字

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

        <!--迭代数字，count从1开始-->
        <p v-for="count in 10">这是第 {{ count }}  次循环</p>
        
    </div>

    <script>
        var vm = new Vue({
            el:'#app',
            data:{
               
            },
            methods:{ // methods属性中定义了当前Vue实例所有的可用的方法
               
            }
        })
    </script>
</body>
</html>
```



![image-20180525181619711](/Users/chenyansong/Documents/note/images/vue/v-for-1.png)



## 4.使用v-for中key的注意事项



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

    <div>
      <label>Id:
        <input type="text" v-model="id">
      </label>

      <label>Name:
        <input type="text" v-model="name">
      </label>

      <input type="button" value="添加" @click="add">
    </div>

    <!-- 注意： v-for 循环的时候，key 属性只能使用 number获取string -->
    <!-- 注意： key 在使用的时候，必须使用 v-bind 属性绑定的形式，指定 key 的值 -->
    <!-- 在组件中，使用v-for循环的时候，或者在一些特殊情况中，如果 v-for 有问题，必须 在使用 v-for 的同时，指定 唯一的 字符串/数字 类型 :key 值 -->
    <p v-for="item in list" :key="item.id">
      <!--这里使用id作为唯一值，不然选中的CheckBox会有问题-->
      <input type="checkbox">{{item.id}} --- {{item.name}}
    </p>
  </div>

  <script>
    // 创建 Vue 实例，得到 ViewModel
    var vm = new Vue({
      el: '#app',
      data: {
        id: '',
        name: '',
        list: [
          { id: 1, name: '李斯' },
          { id: 2, name: '嬴政' },
          { id: 3, name: '赵高' },
          { id: 4, name: '韩非' },
          { id: 5, name: '荀子' }
        ]
      },
      methods: {
        add() { // 添加方法
          this.list.unshift({ id: this.id, name: this.name })
        }
      }
    });
  </script>
</body>

</html>
```

