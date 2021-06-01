XSS原理

1. 可控变量
2. 输出变量

```php
<?php
    $code = $_GET['x'];
	echo $code
?>
```



```shell
curl localhost/test/test.php?x=<script>alert(1);</script>

#在这个curl被执行的时候，script里面的语句就会被执行，同理如果我们在script中写拿到cookie的逻辑，那么我们就可以拿到web的cookie的值，进而登录cookie
```



网上的跨站平台



一般在网站留言中将跨站代码写入



利用工具(linux中kali)：beef, xsser



如果直播平台有跨站的问题，每一条留言显示，那么就可以拿到看直播人的登录信息了



如何判定网站存在xss

> 判断一个你输入的语句是否可以在页面上回显，如果可以，那么我们就将script写进入，然后回显的时候就拿到了cookie

一般使用工具就可以扫出来xss

