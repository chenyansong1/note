[toc]

# 编写模块代码

```c
#include <linux/init.h>
#include <linux/module.h>

MODULE_LICENSE("Dual BSD/GPL");

static int hello_init(void){
        printk(KERN_EMERG "Hello, world\n");
        return 0;
}

static void hello_exit(void){
        printk(KERN_EMERG "Goodbye, cruel world\n");
}

module_init(hello_init);
module_exit(hello_exit);
```

# make编译

首先将你的文件移到一个目录，这里假设为你的Home目录下的test目录(~/test)，因为等会编译会产生多个文件，避免跟前期目录混在一起。

把hello.c文件移入test目录，在test目录下新建一个Makefile文件，输入如下内容。

```makefile
obj-m += hello.o # 这里的hello.o名字对应你的hello.c文件

all:
        make -C /lib/modules/`uname -r`/build M=`pwd` modules
clean:
        make -C /lib/modules/`uname -r`/build M=`pwd` clean
```

保存退出，执行make编译。

编译生成多个文件，包括hello.ko。

# 加载模块

使用命令

```shell
# 装载模块
insmod hello.ko
# 卸载模块
rmmod hello
```

就会提示Hello, world了，如果没显示内容，可以执行`tail /var/log/messages`命令查看。没在屏幕上显示的原因是日志级别不够。

你也可以修改hello.c文件，将KERN_ALTER改为KERN_EMERG，重新编译装入模块，就可以在屏幕显示Hello, world了。