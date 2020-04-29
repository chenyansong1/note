# chrome调试技巧

* F12进入chrome的开发者界面

## console

* 可以在console中运行一些js代码，如console

![](/e:/note/images/chrome/console.jpg)


* 执行一些js中的方法

![](/e:/note/images/chrome/console2.jpg)


## element查看元素

* 可以查看元素上的事件

![](/e:/note/images/chrome/elements.jpg)

* 定位页面上指定的元素

![](/e:/note/images/chrome/elements2.jpg)


## sources

* 查看源文件

![](/e:/note/images/chrome/sources.jpg)

* 断点调试

![](/e:/note/images/chrome/sources2.jpg)


## network


![](/e:/note/images/chrome/network.jpg)

解决浏览器js缓存的另一种方式是将js的引用之后加上？time
```
<script src="/public/admin/assets/js/jquery.dashboard.js?2017"></script>
//其实？后面加上什么无所谓，但是要加上东西
```

## application查看应用的session和cookie

![](/e:/note/images/chrome/application.jpg)