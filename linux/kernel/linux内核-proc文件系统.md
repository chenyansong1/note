[toc]

proc文件系统是一个虚拟的文件系统，最初是用来进行内核调试，我们可以通过配置和获取/proc/xx文件系统的信息来进行和内核的交互，常见的proc文件：

```shell
#cpu信息
/proc/cpuinfo

#内存的信息
/proc/meminfo
```



* 在proc文件系统下添加一个文件

  ```c
  通过使用内核接口在/proc目录下添加文件
  struct proc_dir_entry *proc_create_data(
  	const char *name,					//文件名称
  	umode_t mode, 					//读写权限
  	struct proc_dir_entry *parent,			//上级目录
  	const struct file_operations *proc_fops,	//操作函数
  	void *data	//私有数据
  );					
  
  
  读写权限：是一个十进制数
       格式 0abc，a代表本用户权限，b代表用户组权限，c代表其他用户权限。其中读、写、执行分别用4、2、1表示。如用户具有读权限，用4表示；具有读写权限用4+2=6来表示，同样，7表示用户同时具有读、写和执行权限。
       例如：0644表示本用户具有读写权限，用户组和其他用户具有只读权限。
  上级目录：是一个指针，其中NULL表示文件就在/proc目录下创建
  操作函数：这是Linux文件系统非常重要的一个结构体，包含对文件的读、写、打开、关闭等等操作。
  	
  struct file_operations {
  	//......
  	int (*open) (struct inode *, struct file *);
  	ssize_t (*read) (struct file *, char __user *, size_t, loff_t *);
  	ssize_t (*write) (struct file *, const char __user *, size_t, loff_t *);
  	int (*release) (struct inode *, struct file *);
  	//......
  };
  ```

  