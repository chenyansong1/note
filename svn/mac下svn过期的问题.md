mac svn客户端versions过期解决办法

来自：

从codez5mac看到的解决办法，在我电脑上已经运行成功。
 
 
```
rm ~/.AB64CF89  
      rm ~/Library/.CF89AB64  
      rm ~/Library/Preferences/com.picodev.Versions.plist  
      open ~/Library/Preferences/.GlobalPreferences.plist  
      Delete the key: com.picodev.Versions.ezsAuthorizedLibs and save it.  

```


* 打开plist 文件，需要安装plistedit pro
* 参见
> http://www.sdifen.com/plisteditpro184.html



* 转自
>https://blog.csdn.net/Richer1997/article/details/50534849
http://www.maiyadi.com/thread-48076-1-1.html
