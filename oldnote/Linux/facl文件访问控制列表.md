FACL: Filesystem Access Control List

利用文件扩展保存额外的访问控制列表



需求：如果tom用户创建的文件，我jetty用户想要访问和修改



```Shell
# 设置
setfacl 
	-m : 设定
		u:UID:perm
		g:GID:perm
		
	-x : 取消设定

setfacl -m g:group:rw filename
setfacl -m u:hadoop:rw filename
	
setfacl -x u:username filename
setfacl -x g:groupname filename


#获取facl
getfacl

```



![image-20180905204231093](/Users/chenyansong/Documents/note/images/linux/filesystem/facl.png)



> 一个文件的访问权限顺序是这样的，
>
> 1. 比较属主是否相同；
> 2. 比较facl中的uid是否相同
> 3. 比较属主是否在文件的属组中
> 4. 比较facl的gid是否相同
> 5. 比较other的情况





![image-20180905210305541](/Users/chenyansong/Documents/note/images/linux/filesystem/facl2.png)

> 设置了facl的文件，后面有一个“+”号

