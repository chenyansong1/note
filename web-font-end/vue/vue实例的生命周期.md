

* 什么是生命周期：从Vue实例创建，运行，到销毁，总是伴随着各种各样的事件，这些事件统称为生命周期
* 生命周期钩子：就是生命周期事件的别名而已
* 主要的生命周期函数分类：
  * 创建期间的生命周期函数
    * beforeCreate：实例在内存中创建出来，此时还没有初始化好data和methods属性
    * created：实例已经在内存中创建OK，此时data和methods已经创建OK，此时还没有开始编译模板
    * beforeMount：此时已经完成了模板的编译，但是还没有挂在到页面
    * mounted：此时已经将编译好的模板挂载到了页面指定的容器中显示
  * 运行期间的生命周期函数
    * beforeUpdate：状态更新之前执行此函数，此时data中的状态是最新的，但是界面上显示的数据还是旧的，因为此时还没有重新渲染dom节点
    * updated：实例更新完毕之后调用此函数，此时内存中的data的状态值和页面上显示的数据都已经完成了更新，界面已经重新渲染好
  * 销毁期间的生命周期函数
    * beforeDestroy：实例销毁之前调用，在这一步，实例仍然完全可用
    * destroy: Vue实例销毁后调用，调用后，Vue实例指示的所有东西都会被解绑，所有的事件监听器会被移除，所有的子实例也会被销毁





**vue实例的创建过程**

![vue实例的创建过程](/Users/chenyansong/Documents/note/images/vue/lifecycle.png)



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
        <p>{{ msg }}</p>

        <input type="button" value="修改msg方法" @click="msg='NO'">
    </div>

    <script>
        
        var vm = new Vue({
            el: '#app', 
            data:{
                msg:"welcome to vue!" 
            },
            methods:{
                show(){
                    console.log('执行了show方法')
                }
            },

            beforeCreate(){
                this.show()
                console.log(this.msg)// 输出 undefined ，说明此时data上的数据没有初始化，同理methods中的方法也是没有被初始化
            },

            created(){//调用 已经初始化好的data和methods ,此时可以操作data中的数据和methods中的方法
                this.show()
                console.log(this.msg)// 输出 undefined ，说明此时data上的数据没有初始化，同理methods中的方法也是没有被初始化
            },

            // 模板已经编译完成，但是尚未将模板渲染到页面中
            beforeMount(){

                console.log(document.getElementById('p').innerHTML)// 打印输出的是：{{ msg }},说明此时数据还未加载
                /*
                在beforeMount执行的时候，页面中的元素并没有被替换
                */
        
            },

            //表示内存中的模板已经真实的挂在到了页面中，用户已经可以看到渲染好的页面了
            mounted(){
                console.log(document.getElementById('p').innerHTML)// 此时输出： welcome to vue!
                // 注意：这个mounted是实例创建期间的最后一个生命周期函数，当执行完mounted，表示实例就已经完全创建好了，此时如果没有其他操作的话，这个实例就静静的躺在我们的内存中，一动不动

            },

            // 运行中的事件
            // 当数据被改变的时候，但是此时界面还没有被更新
            beforeUpdate(){
                console.log('界面山上的元素==' + document.getElementById('p').innerHTML)
                console.log('data中的msg数据==' + this.msg)
                // 发现页面上的数据还是老的，但是data中的数据是最新的
            },

            updated(){
                console.log('界面山上的元素==' + document.getElementById('p').innerHTML)
                console.log('data中的msg数据==' + this.msg)
                // 发现页面上的数据 和 data中的数据 都是最新的
            }

        })




    </script>
</body>
</html>
```

