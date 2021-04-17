[toc]



# gin路由

1. API参数

   ```go
   //curl ip:8000/user/:name
   //curl ip:8000/user/:name/*action
   
   	r.GET("/user/:name/*action", func(c *gin.Context) {
   		name := c.Param("name")
   		action := c.Param("action")
   		c.String(http.StatusOK, name + " is " + action)
   
   		c.String(http.StatusOK, "hello world")
   	})
   ```

   

2. URL参数

   ```go
   /*
   URL参数可以通过DefaultQuery or Query方法获取
   DefaultQuery：若参数不存在返回指定的默认值
   Query：若参数不存在，返回空串
   curl ip:8000/user/add?name=zs
   */
   
   
   // curl ip:8000/welcome?name=zhangsan
   	r.GET("/welcome", func(c *gin.Context) {
   		name := c.DefaultQuery("name", "jacck")
   
   		c.String(http.StatusOK, name)
   	})
   ```

   

3. 表单参数

   ```html
   <html>
       <head>
           <body>
               <form action="http://local:8000/form" method="post" enctype="application/x-www-form-urlencoded">
                   user name : <input type="text" name="username">
                   <br>
                   密码 : <input type="password" name="password">
                   <br>
                   兴趣: <input type="checkbox" name="hobby" value="run">
                   兴趣: <input type="checkbox" name="hobby" value="game">
                   兴趣: <input type="checkbox" name="hobby" value="money">
                   <br>
                   <input type="submit" value="login">
               </form>
           </body>
       </head>
   </html>
   ```

   

   ```go
   
   	r.POST("/form", func(c *gin.Context) {
   		//表单参数也可以设置默认值
   		typel := c.DefaultPostForm("type", "alert")
   		username := c.PostFormArray("username")
   		password := c.PostFormArray("password")
   		//多选
   		hobbys := c.PostFormArray("hobby")
   
   		c.String(http.StatusOK,
   			fmt.Sprintf("type is %s, uername is %s ,password is %s" +
   				" hobby is %v", typel, username, password, hobbys ))
   
   	})
   ```

   

4. 上传单个文件

   * mutipart/form-data格式用于文件上传
   * gin文件上传与原生的net/http方法类似，不同在于gin把原生的request封装到c.Request中

   ```html
   <html>
       <head>
           <body>
               <form action="http://local:8000/upload" method="post" enctype="mutipart/form-data">
                   file1 : <input type="file" name="file">
                  <br>
                   <input type="submit" value="upload">
               </form>
           </body>
       </head>
   </html>
   ```

   ```go
   	r.POST("/upload", func(c *gin.Context) {
   		//表单文件
   		file, _ := c.FormFile("file")
   
   		log.Println(file.Filename)
   
   		//传到项目的根目录
   		c.SaveUploadedFile(file, file.Filename)
   
   		//response
   		c.String(http.StatusOK, fmt.Sprintf("upload ok"))
   
   	})
   ```

   

5. 上传多个文件

   ```html
   <html>
       <head>
           <body>
               <form action="http://local:8000/upload" method="post" enctype="mutipart/form-data">
                   file1 : <input type="file" name="file" multiple>
                  <br>
                   <input type="submit" value="upload">
               </form>
           </body>
       </head>
   </html>
   ```

   ```go
   //限制表单的文件的大小：默认是32M
   	r.MaxMultipartMemory = 8<<20
   	r.POST("/upload", func(c *gin.Context) {
   		form, err := c.MultipartForm()
   		if err != nil {
   			c.String(http.StatusBadRequest, fmt.Sprintf("get err %s", err.Error()))
   		}
   
   		//获取所有的file
   		files := form.File["file"]
   		for _, file := range files {
   			//逐个存file
   			if err := c.SaveUploadedFile(file, file.Filename); err != nil {
   				c.String(http.StatusBadRequest, fmt.Sprintf("get err %s", err.Error()))
   			}
   		}
   
   		c.String(http.StatusOK, fmt.Sprintf("upload ok %d files ", len(files)))
   
   
   	})
   ```

   

6. routes group

   ```go
   r := gin.Default()
   
   //路由组，处理GET请求
   v1 := r.Group("/v1")
   {
     v1.GET("/login", login)
     v1.GEt("/submit", submit)
   }
   
   v2 := r.Group("v2")
   {
     v2.Post("/login", login)
     v2.Post("/submit", submit)
   }
   
   
   
   func login(c *gin.Context){
     name := c.DefaultQuery("name", "jack")
     c.String(200, fmt.Sprintf("hello %s\n", name))
   }
   
   
   func submit(c *gin.Context){
     
     
   }
   ```

   

7. 路由原理

   https://github.com/julienschmidt/httprouter

   httprouter将所有的路由规则构造一棵前缀树

   ![preview](../../../images/go/v2-92bd06d42f26bcdb52894046f1016cd5_r.png)

   ```go
   r := gin.Default()
   
   r.Post("/", xx)
   r.Post("/search", x2x)
   r.Post("/support", xx)
   r.Post("/blog/:post", xx)
   r.Post("/contact", xx)
   r.Post("/about", xx)
   
   r.Run()
   ```



# gin数据解析和绑定

1. json数据解析和绑定

   客户端传参，后端接收并解析到结构体

   ```go
   //定义接收数据的结构体
   // binding: "required" 必选字段，若接收值为空值，则报错
   type Login struct{
     User string `form:"username" json:"user" uri:"user" xml:"user" binding "required"`
     Password string `form:"password" json:"password" uri:"password" xml:"password" binding "required"`
   
   }
   
   func main(){
     r != gin.Default()
     r.Post("/loginJson", func(c *gin.Context){
       var json Login
       //将request的body中的数据，自动解析json格式到结构体
       if err :=c.ShouldBindJSON(&json); err != nil {
         //gin.H 封装了生成json的工具
         c.JSON(http.statusBadRequest, gin.H{"error": err.Error()})
         return
       }
       
       //判断用户名，密码
       if json.User != "root" || json.Password != "admin" {
         c.JSON(http.statusBadRequest, gin.H{"status": "304"})
         return
       }
       c.JSON(http.statusOk, gin.H{"status": "200"})
         return
       
     })
   }
   
   // curl ip:port/loginJSON -X POST -H 'content-type:application/json' -d {"user": "root", "password":"admin"}
   ```

   

2. 表单数据解析和绑定

   ```go
   <html>
       <head>
           <body>
               <form action="http://local:8000/loginForm" method="post" enctype="mutipart/form-data">
                   user name : <input type="text" name="username">
                   <br>
                   密码 : <input type="password" name="password">
                   <br>
               </form>
           </body>
       </head>
   </html>
   
   
   
   type Login struct{
     User string `form:"username" json:"user" uri:"user" xml:"user" binding "required"`
     Password string `form:"password" json:"password" uri:"password" xml:"password" binding "required"`
   
   }
   
   func main(){
     r != gin.Default()
     r.Post("/loginForm", func(c *gin.Context){
       var form Login
       //Bind()默认解析并绑定form格式
       //根据请求头中的content-type自动推断的
       if err := c.Bind(&form); err != nil{
         c.JSON(http.statusBadRequest, gin.H{"error":err.Err()})
       }
       
       //判断用户名，密码
       if json.User != "root" || json.Password != "admin" {
         c.JSON(http.statusBadRequest, gin.H{"status": "304"})
         return
       }
       c.JSON(http.statusOk, gin.H{"status": "200"})
       return
       
     })
   }
   ```

   

3. URI数据解析和绑定

   ```go
   //http://localhost:8000/root/admin  将root ， admin解析到结构体中
   ```

   ```go
   r.GET("/:user/:password", fn)
   c.ShouldBindRUi(&login)
   ```





# 多种响应方式

1. json

   ```go
   func main(){
     r != gin.Default()
     r.Post("/loginForm", func(c *gin.Context){
       
       c.JSON(http.statusOk, gin.H{"status": "200"})
       
     })
   }
   
   ```

2. 结构体响应

   ```go
   func main(){
     r != gin.Default()
     r.Post("/loginForm", func(c *gin.Context){
       var msg struct {
         Name string
         Message string
         Number int
       }
       
       msg.Name = "zhansan"
       msg.Message = "message"
       msg.Number = 123
       
       c.JSON(http.statusOk, msg)
       
     })
   }
   ```

3. xml

   ```go
   func main(){
     r != gin.Default()
     r.GET("/loginXML", func(c *gin.Context){
       
       c.XML(http.statusOk, gin.H{"message":"abc"})
       
     })
   }
   ```

4. yaml格式

   ```go
   func main(){
     r != gin.Default()
     r.GET("/loginYAML", func(c *gin.Context){
       
       c.YAML(http.Ok, gin.H{"name":"zhangan"})
       
     })
   }
   ```

5. protobuf格式

   ```go
   func main(){
     r != gin.Default()
     r.GET("/loginProtobuf", func(c *gin.Context){
       
       reps != []int64{int64(1), int64(2)}
       label := "label"
       data = &protoexample.Test{
         Label: &label
         Reps: reps
       }
       c.ProtoBuf(http.ok, data)
       
     })
   }
   ```



# HTML模板渲染

* gin支持加载HTML模板，然后根据模板参数进行配置并返回相应的数据，本质上就是**字符串替换**
* LoadHTMLGlob()方法可以加载模板文件

```html
<html>
  <h1>
    {{.title}}
  </h1>
</html>
```



```go
func main(){
  r != gin.Default()
  
  //加载模板文件
  r.LoadHTMLGlob("template/*")
  r.LoadHtmlFiles("template/index.html")
  
  
  r.GET("/index", func(c *gin.Context){
    //json将title替换
    c.HTML(http.ok, "index.html", gin.H{"title":"my title"})
  })
}
```



# 重定向

```go
func main(){
  r != gin.Default()
  
  //
  r.GET("/redirect", func(c *gin.Context){
    
    //支持内部和外部重定向
    c.Redirect(http.StatusMovePermanent, "https://www.baidu.com")
    
  })
}
```



# 同步异步

goroutine 机制可以方便的实现异步处理

另外，在启动新的goroute时，不应该使用原始上下文（c gin.context），必须使用他的只读副本

```go
func main(){
  r != gin.Default()
  
  //异步
  r.GET("/long_async", func(c *gin.Context){
    //复制上下文
    copyContext := c.Copy()
    
    go func(){
      time.Sleep(3*time.Second)
      log.Println("异步执行" + copyContext.Request.URL.Path)
    }()
    
  })
  
  
  //同步
  r.GET("/long_sync", func(c *gin.Context){
    time.Sleep(3*time.Second)
    log.Println("异步执行" + copyContext.Request.URL.Path)
  })
}

```



# 中间件

* gin可以构建中间件，但他只对注册过的路由函数起作用
* 对于分组路由、嵌套使用中间件，可以限定中间件的作用范围
* 中间件分为全局中间件、单个路由中间件和群组中间件
* gin中间件必须是一个车gin.HandlerFunc类型



# 全局中间件

```go
//定义中间件
func MiddleWare() gin.HandleFunc(){
  
  
  return func(c *gin.Context){
    t := time.Now()
    
    fmt.printf("中间件开始执行")
    
    //设置变量到context中，可以通过get()取
    c.Set("request", "中间件")
    
    //执行函数
    c.Next()
    
    //Writer ---> reponse
    status := c.Writer.Status()
    
    fmt.println("中间件执行完毕", status)
    
    t2 := time.Since(t)
    
    fmt.Println("time:", t2)
    
  }
}
```

```go
func main(){
  r != gin.Default()
  
  //注册中间件
  r.Use(MiddleWare())
  {//为了代码规范
    r.GET("/middleware", func(c *gin.Context){
      //取值(中间件)
      req,_ := c.Get("request")
      fmt.Println("request:", req)
      
      c.JSON(http.ok, gin.H{"request": req})
    })
  }
  
}

```



# Next() 方法

```go
/*
中间件1 prefix
中间件2 prefix

路由1


中间件2 post
中间件1 post
*/
```



# 局部中间件

```go
func main(){
  r != gin.Default()
  
  //注册中间件
  r.Use(MiddleWare())
  {//为了代码规范
    r.GET("/middleware", func(c *gin.Context){
      //取值(中间件)
      req,_ := c.Get("request")
      fmt.Println("request:", req)
      
      c.JSON(http.ok, gin.H{"request": req})
    })
    
    //局部中间件
    r.GET("/middleware2", MiddleWare(), func(c *gin.Context){
      //取值(中间件)
      req,_ := c.Get("request")
      fmt.Println("request:", req)
      
      c.JSON(http.ok, gin.H{"request": req})
    })
  }
  
}


```



# 会话控制

## cookie

```go
func main(){
  
  r := gin.default()
  
  //服务端要给客户端cookie
  r.GET("/cookie", func(c *gin.Context){
    
    cookie, err := c.Cookie("key_cookie")
    
    if err != nil {
      cookie = "NotSet"
      
      //60s , 
      // / cookie所在的目录
      // domain : string 域名
      // secure :是否只能https访问
      // httpOnly: 是否允许别人通过js获取自己的cookie
      c.SetCookie("key_cookie", "value_cookie", 60, 
                  "/",
                 "localhost", false, true)
      
      
    }
    fmt.Printf("cookie %s\n", cookie)
    
  })
  
  
}
```



```go
func main(){
  
  r := gin.Default()
  
  r.GET("/login", function(c *gin.Context){
    //设置cookie
    c.SetCookie("abc", "123", 60, "/", "localhost", false ,true)
    
    //返回
    c.String(http.ok, "Login success")
    
    
  })
  
  //定义一个中间件，去验证cookie
  r.GET("/home", AuthMiddleWare(), function(c *gin.Context){
    
    c.JSON(http.ok, gin.H{"data":"home"})
  })
  
}


func AuthMiddleWare() gin.HandlerFunc{
  
  //获取客户端cookie，并校验
  return func(c *gin.Context){
    
    if cookie, err := c.Cookie("abc"); err == nil {
      if cookie == "123" {
        c.Next()
        return
      }
    }
    
    c.JSON(http.304, gin.H{"error": "err"})
    //如果验证不通过,直接丢弃
    c.Abort()
    return
  }
  
}
```



cookie的缺点

1. 不安全：明文
2. 增加了带宽的消耗
3. 可以被禁用
4. cookie是有上限的



## session

session可以弥补cookie的不足，session必须依赖于cookie才能石笋，生成一个session放在cookie里传给客户端就可以

1. cookie是将所有的东西存在客户端，不安全
2. session是将所有的东西存在服务器端，而cookie里面存入的是session的一个key



session中间件：一个通用的session服务，支持内存存储和Redis存储

session模块设计

* 本质上是一个k-v系统，通过key进行crud

* session可以存储在mem ，Redis中

* session的接口

  * Set()
  * Get()
  * Del()
  * Save() : session存储，用的时候在加载，延迟加载

* sessionManager接口

  * Init() 初始化，加载Redis地址
  * createSession()
  * GetSession(sessionId)

* 接口实现

  * MemSession
    * 定义MemSession对象（<sessionId, 存k-v的map>) 读写锁
    * 构造函数，为了获取对象
    * Set()
    * Get()
    * Del()
    * Save() 
  * MemSessionManager
    * 定义对象 <map<map>>
    * Init() 初始化，加载Redis地址
    * createSession()
    * GetSession(sessionId)
  * RedisSession实现
    * 定义RedisSession对象 （sessionId, k-v map 读写锁，Redis连接，记录内存中map是否被修改的标记）
    * Set()：将session存入内存中的map
    * Get()：取数据，实现延迟加载
    * Del()
    * Save() ：将session存入Redis

  * RedisSessionManager
    * 定义对象 <map<map>> （Redis地址，密码，连接池，读写锁）
    * 构造函数
    * createSession()
    * GetSession(sessionId)

![image-20210417163453089](/Users/chenyansong/Documents/note/images/go/image-20210417163453089.png)





---

todo 

1. goland的环境变量的两个参数：
   1. gopath
   2. goroot
   3. 项目的放置位置



