SpringBoot文件上传

# 1.创建文件上传页面

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>

<div>文件上传页面</div>


<form action="uploadFile" method="post" enctype="multipart/form-data">
    请选择文件：<input type="file" name="attach" /><br/>
    <input type="submit" value="开始上传" />
</form>

</body>


</html>
```
这里需要注意的几个点:

* form表单请求的action，就是在controller中写好的
* 请求的方法一定要是post,上传文件的类型一定要是 multipart/form-data
* 因为是文件上传，所以type=file，name要和controller中的一一对应



# 2.写controlller

这里需要注意几点:

* 文件上传的接收参数是MultipartFile，这里可以指定参数名称，使用RequestParam指定attach名称的文件，赋值给file这个名称

* file.transferTo将文件保存到指定的路径下

```
package cn.sn1234.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: cys
 * Date: Created in 2018/4/30
 * Description:
 */

@RestController
public class UploadController {

    private Map<String, Object> result = new HashMap<>();

    @RequestMapping("/uploadFile")
    public Map<String, Object> upload(@RequestParam("attach") MultipartFile file){

        System.out.println("文件的原名：="+file.getOriginalFilename());
        System.out.println("文件的类型：="+file.getContentType());

        // 指定保存到磁盘的路径
        try {
            System.out.println("name="+file.getName());
            System.out.println("文件大小，size="+file.getSize());
            file.transferTo(new File("/Users/chenyansong/Desktop/"+file.getOriginalFilename()));

            result.put("result", "success file upload");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

```

# 3.浏览器上传测试

![](/Users/chenyansong/Documents/note/images/spring-boot/fileupload2.png)


![](/Users/chenyansong/Documents/note/images/spring-boot/fileupload3.png)


后台上传日志

```
2018-04-30 16:58:13.164  INFO 2166 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : FrameworkServlet 'dispatcherServlet': initialization completed in 15 ms
文件的原名：=ima_4.png
文件的类型：=image/png
name=attach
文件大小，size=195855
```

# 3.解决上传文件的大小限制



springBoot的默认文件上传大小为10M，如果选择的文件超过10M，那么会报如下的错误：

![](/Users/chenyansong/Documents/note/images/spring-boot/fileupload1.png)


这时发现 SpringBoot 上传文件限制不超过 10M，但是可以修改限制 在 src/main/resources 目录下建立 application.properties 文件

```
# 修改单个文件的大小限制
spring.http.multipart.maxFileSize=100MB
# 修改一个请求(包括多个文件)的大小限制
spring.http.multipart.maxRequestSize=200MB

```


