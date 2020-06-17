控制元素是否显示



* v-if会将元素移除,或者添加元素（有较高的切换性能消耗，可以用于 **如果元素从始至终都没有显示过**）
* v-show只是display:none（有较高的初始性能消耗，可以用于 **元素频繁的切换**）

这是两者的区别



```HTML
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

        <h3 v-if="flag">这是v-if控制的元素</h3>

        <h3 v-show="flag">这是v-show控制的元素</h3>

        <input type="button" value="btn" @click="toggle">
        <input type="button" value="btn2" @click="flag=!flag">
    </div>

    <script>
        var vm = new Vue({
            el:'#app',
            data:{
               flag:true
            },
            methods:{
                toggle(){
                    this.flag=!this.flag
                }
            }
        })
    </script>
</body>
</html>
```

