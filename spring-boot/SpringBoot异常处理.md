
SpringBoot的异常处理分为下面四种情况：

* 自定义error的错误页面
* @ExceptionHandler注解提供方法
* @ControllerAdvice+@ExceptionHander
* 配置SimpleMappingExceptionResolver类
* 自定义HandlerExceptionResovler类


# 1.自定义error的错误页面


我们要知道的是 SpringBoot 应用默认已经提供一套错误处理机制:把所有后台错误统一交给 error 请求，然后跳转到了本身自己的错误提示页面。
![](/Users/chenyansong/Documents/note/images/spring-boot/error1.png)

这时，如果需要修改提示页面，我们可以在 templates 目录直接建立 error.html 页面即可

```
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>自定义错误页面</title>
</head>
<body>
<h3>错误页面</h3>
<div th:text="${exception}"></div>
</body>
</html>
```

> 这种情况适用于：所有的错误都使用同一个页面进行显示，但是我们的项目中对某些特定的异常类型，我们希望交给不同的错误提示页面



# 2.@ExceptionHandler注解提供方法


在controller类中定义，处理对应异常的方法

```
	// 处理java.lang.ArithmeticException
	@ExceptionHandler(value = { java.lang.ArithmeticException.class })
	public ModelAndView handlerArithmeticException(Exception e) { // e:该对象包含错误信息

		// 设置错误信息
		ModelAndView mv = new ModelAndView();
		mv.addObject("exception", e.toString());
		mv.setViewName("error1");// 跳转到error1的错误处理页面
		return mv;
	}

	// 处理java.lang.NullPointerException
	@ExceptionHandler(value = { java.lang.NullPointerException.class })
	public ModelAndView handlerNullPointerException(Exception e) { // e:该对象包含错误信息

		// 设置错误信息
		ModelAndView mv = new ModelAndView();
		mv.addObject("exception", e.toString());
		mv.setViewName("error2");// 跳转到error2的错误处理页面
		return mv;
	}
	
```



> 这种方式是定义在controller方法中的，但是如果有10个controller，那么就要在这10个controller中定义10个这种异常，所以我们需要定义一个全局的异常


![](/Users/chenyansong/Documents/note/images/spring-boot/error2.png)




# 3.@ControllerAdvice+@ExceptionHander


我们可以定义一个全局的ExceptionHander,这样就不用在每个controller中都定义异常了

![](/Users/chenyansong/Documents/note/images/spring-boot/error3.png)



# 4.配置SimpleMappingExceptionResolver类

![](/Users/chenyansong/Documents/note/images/spring-boot/error4.png)



# 5.自定义HandlerExceptionResovler实现类

![](/Users/chenyansong/Documents/note/images/spring-boot/error5.png)




