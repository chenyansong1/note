[TOC]

# C库函数

![](https://github.com/chenyansong1/note/blob/master/images/c_languge/C库IO函数工作流程.png?raw=true)

> C库函数是在内部封装了一个I/O缓冲区，但是如果是系统的I/O函数是没有这样的缓冲区的



# 虚拟地址空间

![](https://github.com/chenyansong1/note/blob/master/images/c_languge/pcb和文件描述符.png?raw=true)

![](https://github.com/chenyansong1/note/blob/master/images/c_languge/虚拟地址空间.png?raw=true)

查看文件的格式

```shell
chenyansongdeMacBook-Pro:c_language chenyansong$ file a.out 
a.out: Mach-O 64-bit executable x86_64
chenyansongdeMacBook-Pro:c_language chenyansong$ file /tmp//sunlogin_helper.log 
/tmp//sunlogin_helper.log: ASCII text
chenyansongdeMacBook-Pro:c_language chenyansong$ 
```

![image-20191103111757079](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191103111757079.png?raw=true)



>  cpu 为什么要使用虚拟地址空间与物理地址空间映射？解决了什么样的问题？

1. 方便编译器和操作系统安排程序的地址分布。
   		程序可以使用一系列相邻的虚拟地址来访问物理内存中不相邻的大内存缓冲区。
   
2. 方便进程之间隔离
   不同进程使用的虚拟地址彼此隔离。一个进程中的代码无法更改正在由另一进程使用的物理内存。
   
3. 方便OS使用你那可怜的内存。
   程序可以使用一系列虚拟地址来访问大于可用物理内存的内存缓冲区。当物理内存的供应量变小时，
   内存管理器会将物理内存页（通常大小为 4 KB）保存到磁盘文件。数据或代码页会根据需要在物理内存与磁盘之间移动。
      		
   
      		

![](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191103114054898.png?raw=true)



# C库函数与系统函数的关系

![7_库函数与系统函数的关系](https://github.com/chenyansong1/note/blob/master/images/c_languge/库函数与系统函数的关系.png?raw=true)



# 系统IO函数

## open函数

```c
#可以看到系统函数属于第2章
man man 

#查看第2章的open函数
man 2 open

  
int open(const *pathname, int flags);

//不存在创建， mode指定创建的文件的访问权限
int open(const *pathname, int flags, mode_t mode);

//返回的是文件描述符，函数调用失败的时候，全局变量errno会被赋值，不同的数据表示不同的错误信息

```

## errno

![image-20191103144732114](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191103144732114.png?raw=true)



![image-20191103144909912](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191103144909912.png?raw=true)

## 使用

```c
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <unistd.h>

#include <stdio.h>
#include <stdlib.h>

//打开存在的文件
int main01()
{
  int fd;
  
  // open exist file
  fd = open("hello.c", O_RDWR);
  if(fd == -1)
  {
    perror("open file:");
    exit(1);
  }
  
  //close file
  int ret = close(fd);
  printf("ret= %d\n", ret);
  return 0;
}


//打开创建的新文件
int main02()
{
  int fd;
  
  // create file,777是文件的权限，这个会减去umask的值，最终是文件的实际权限
  fd = open("hello_new.c", O_RDWR| O_CREAT, 0777);
  if(fd == -1)
  {
    perror("open file:");
    exit(1);
  }
  
  //close file
  int ret = close(fd);
  printf("ret= %d\n", ret);
  return 0;
}

//判断文件是否存在
int main()
{
  int fd;
  
  // create file,777是文件的权限，这个会减去umask的值，最终是文件的实际权限
  //判断文件是否存在
  fd = open("hello_new.c", O_RDWR| O_CREAT| O_EXCL, 0777);
  //将文件截断为0，清空文件内容
  //fd = open("hello_new.c", O_RDWR| O_TRUNC);
  if(fd == -1)
  {
    perror("open file:");
    exit(1);
  }
  
  //close file
  int ret = close(fd);
  printf("ret= %d\n", ret);
  if(ret == -1)
  {
    perror("close file");
    exit(1);
  }
  
  return 0;
}



/**
打开方式：
	必选项：
		O_RDONLY
		O_WDONLY
		O_RDWR
	可选项：
		O_CREAT:根据本地的umask去求文件的实际权限
		O_TRUNC
		O_EXCL				
		O_APPEND				

*/

```

## read函数

```c
#include <unistd.h>

//signed size_t
ssize_t read(int fd, void *buf, size_t count);

//返回值
		-1 ：读文件失败
		0  ：文件读完了
		>0 ：读取的字节数
```



## write函数

```c
#include <unistd.h>
//
ssize_t write(int fd, const void *buf, size_t count);
```

## lseek

```c
/**
1. 获取文件大小
2. 移动文件指针
3. 文件扩展---系统函数专有
*/

#include <sys/types.h>
#include <unistd.h>

off_t lseek(int fd, off_t offset, int whence);

/**
whence:
SEEK_SET 开始位置
SEEK_CUR 当前位置
SEEK_END 结束位置
*/

#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>


int main()
{
  int fd = open("english.txt", O_RDONLY);
  if(fd == -1)
  {
    perror("open file");
    exit(-1);
  }
  
  //获取文件长度
  int ret = lseek(fd, 0, SEEK_END);
  
  //文件的拓展，只能向后拓展(拓展文件大小)
  int ret2 = lseek(fd, 2000, SEEK_END);
  //实现文件拓展，需要再最后做一次写操作
  write(fd, "a", 1);//
  //文件拓展的应用场景：比如下载一个电影10G，那么本地最开的时候，就会有一个拓展的空洞文件10G，多线程可以对10G的多个块进行同时操作
  
  //close file
  close(fd);
  
  return 0;
}

```



## read_write写大文件

```c
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>


int main()
{
  int fd = open("english.txt", O_RDONLY);
  if(fd == -1)
  {
    perror("open file");
    exit(-1);
  }
  
  //create new file
  int fd1 = open("newfile", O_CREAT|O_WRONLY, 0664);
  if(fd1 == -1)
  {
    perror("open file");
    exit(-1);
  }
  
  //read file
  char buf[1024] = {0};
  
  int count = read(fd, buf, sizeof(buf));
  if(count == -1)
  {
    perror("read");
    exit(-1);
  }
  while(count)
  {
    //将读出的数据写入到另一个文件中
    int ret = write(fd1, buf, count);
    printf("write bytes %d\n", ret);
    
    //continue read file
    count = read(fd, buf, sizeof(buf));
  }
  
  //close file
  close(fd);
  close(fd1);
  
  
  return 0;
}
```

## inode说明

* 索引节点inode：保存的其实是实际的数据的一些信息，这些信息称为“元数据”(也就是对文件属性的描述)。
  例如：文件大小，设备标识符，用户标识符，用户组标识符，文件模式，扩展属性，文件读取或修改的时间戳，
  链接数量，指向存储该内容的磁盘区块的指针，文件分类等等。
  ( 注意数据分成：元数据+数据本身 )

* 注意inode怎样生成的：每个inode节点的大小，一般是128字节或256字节。inode节点的总数，在格式化时就给定
  (现代OS可以动态变化)，一般每2KB就设置一个inode。一般文件系统中很少有文件小于2KB的，所以预定按照2KB分，
  一般inode是用不完的。所以inode在文件系统安装的时候会有一个默认数量，后期会根据实际的需要发生变化。

* 注意inode号：inode号是唯一的，表示不同的文件。其实在Linux内部的时候，访问文件都是通过inode号来进行的，
  所谓文件名仅仅是给用户容易使用的。当我们打开一个文件的时候，首先，系统找到这个文件名对应的inode号；然后，
  通过inode号，得到inode信息，最后，由inode找到文件数据所在的block，现在可以处理文件数据了。

* inode和文件的关系：当创建一个文件的时候，就给文件分配了一个inode。一个inode只对应一个实际文件，
  一个文件也会只有一个inode。inodes最大数量就是系统文件的最大数量。



## stat函数

![](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191103163846065.png?raw=true)

```c
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

int stat(const char *path, struct stat *buf);
int fstat(int fd, struct stat *buf);
int lstat(const char *path, struct stat *buf);
```

![image-20191103164733805](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191103164733805.png?raw=true)

![image-20191103164832416](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191103164832416.png?raw=true)

![3_st_mode](https://github.com/chenyansong1/note/blob/master/images/c_languge/st_mode.png?raw=true)

![image-20191103165609239](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191103165609239.png?raw=true)

![](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191103170006135.png?raw=true)

```c
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <time.h>
#include <pwd.h>
#include <grp.h>


int main(int argc, char* argv[])
{
    if(argc < 2)
    {
        printf("./a.out filename\n");
        exit(1);
    }

    struct stat st;
    int ret = stat(argv[1], &st);//穿透函数，能够读取到软连接对应的链接文件
  	//如果是软链接，读取的只是软连接文件的大小
  	int ret = lstat(argv[1], &st);
  
    if(ret == -1)
    {
        perror("stat");
        exit(1);
    }

    // 存储文件类型和访问权限
    char perms[11] = {0};
    // 判断文件类型
    switch(st.st_mode & S_IFMT)
    {
        case S_IFLNK:
            perms[0] = 'l';
            break;
        case S_IFDIR:
            perms[0] = 'd';
            break;
        case S_IFREG:
            perms[0] = '-';
            break;
        case S_IFBLK:
            perms[0] = 'b';
            break;
        case S_IFCHR:
            perms[0] = 'c';
            break;
        case S_IFSOCK:
            perms[0] = 's';
            break;
        case S_IFIFO:
            perms[0] = 'p';
            break;
        default:
            perms[0] = '?';
            break;
    }
    // 判断文件的访问权限
    // 文件所有者
    perms[1] = (st.st_mode & S_IRUSR) ? 'r' : '-';
    perms[2] = (st.st_mode & S_IWUSR) ? 'w' : '-';
    perms[3] = (st.st_mode & S_IXUSR) ? 'x' : '-';
    // 文件所属组
    perms[4] = (st.st_mode & S_IRGRP) ? 'r' : '-';
    perms[5] = (st.st_mode & S_IWGRP) ? 'w' : '-';
    perms[6] = (st.st_mode & S_IXGRP) ? 'x' : '-';
    // 其他人
    perms[7] = (st.st_mode & S_IROTH) ? 'r' : '-';
    perms[8] = (st.st_mode & S_IWOTH) ? 'w' : '-';
    perms[9] = (st.st_mode & S_IXOTH) ? 'x' : '-';

    // 硬链接计数
    int linkNum = st.st_nlink;
    // 文件所有者
    char* fileUser = getpwuid(st.st_uid)->pw_name;
    // 文件所属组
    char* fileGrp = getgrgid(st.st_gid)->gr_name;
    // 文件大小,返回的是unix的int,需要转成C的int
    int fileSize = (int)st.st_size;
    // 修改时间
    char* time = ctime(&st.st_mtime);
    char mtime[512] = {0};
    strncpy(mtime, time, strlen(time)-1);

    char buf[1024];
    sprintf(buf, "%s  %d  %s  %s  %d  %s  %s", perms, linkNum, fileUser, fileGrp, fileSize, mtime, argv[1]);

    printf("%s\n", buf);

    return 0;
}

```

![](https://github.com/chenyansong1/note/blob/master/images/c_languge/5_链接的追踪.png?raw=true)



## access函数

![](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191103212836954.png?raw=true)

## chown函数

```c
#include <sys/stat.h>

// mode = 0777
int chmod(const char *path, mode_t mode);
int fchmod(int fd, mode_t mode);
```

```c
#include <stdio.h>
#include <stdlib.h>


int main(int argc, char* argv[])
{
    if(argc < 2)
    {
        printf("a.out filename!\n");
        exit(1);
    }

    // user->ftp  group->ftp
    int ret = chown(argv[1], 116, 125);
    if(ret == -1)
    {
        perror("chown");
        exit(1);
    }
    return 0;
}
```

## truncate 函数

![](https://github.com/chenyansong1/note/blob/master/images/c_languge/1572827628991.png?raw=true)

## link

![1572827793798](https://github.com/chenyansong1/note/blob/master/images/c_languge/1572827793798.png?raw=true)

## unlink

```c
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

int main(void)
{
    int fd = open("tempfile", O_CREAT | O_RDWR, 0755);
    if(fd == -1)
    {
        perror("open");
        exit(1);
    }

    //因为文件还存在，等文件关闭的时候，文件才会被删除
    int ret = unlink("tempfile");
    if(ret == -1)
    {
        perror("unlink");
        exit(1);
    }

    //write file
    write(fd, "hello", 5);
    
    //重置文件指针
    lseek(fd, 0, SEEK_SET);
    //read file
    char buf[512] = {0};
    int len = read(fd, buf, sizeof(buf));
    
    //将读取的内容写入屏幕：STDOUT_FILENO==1
    write(STDOUT_FILENO, buf, len);

    close(fd);

    return 0;
}
```

## rename函数

![](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191104191520937.png?raw=true)

# 目录操作

## chdir,getcwd,mkdir,rmdir

![](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191104192029234.png?raw=true)

```c
#include <stdio.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <unistd.h>

int main(int argc, char* argv[])
{
    if(argc < 2)
    {
        printf("a.out dir\n");
        exit(1);
    }

  	//改变process的路径
    int ret = chdir(argv[1]);
    if(ret == -1)
    {
        perror("chdir");
        exit(1);
    }

    int fd = open("chdir.txt", O_CREAT | O_RDWR, 0777);
    if(fd == -1)
    {
        perror("open");
        exit(1);
    }
    close(fd);

    char buf[128];
  	//当前process的工作目录
    getcwd(buf, sizeof(buf));
    printf("current dir: %s\n", buf);

    return 0;
}
```



## opendir,readdir

![](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191104192444422.png?raw=true)

![image-20191104192951768](https://github.com/chenyansong1/note/blob/master/images/c_languge/c_languge/image-20191104192951768.png?raw=true)

![image-20191104193249078](/Users/chenyansong/Documents/note/images/c_languge/image-20191104193249078.png)

```c
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <dirent.h>

int getFileNum(char* root)
{
    // 打开目录
    DIR* dir = opendir(root);
    if(dir == NULL)
    {
        perror("opendir");
        exit(0);
    }

    // 读目录
    int total = 0;
    char path[1024] = {0};
    struct dirent* ptr = NULL;
    while((ptr = readdir(dir)) != NULL)
    {
        // 跳过 . 和 ..
        if(strcmp(ptr->d_name, ".") == 0 || strcmp(ptr->d_name, "..") == 0)
        {
            continue;
        }
        // 判断是不是文件
        if(ptr->d_type == DT_REG)
        {
            total ++;
        }
        // 如果是目录
        if(ptr->d_type == DT_DIR)
        {
            // 递归读目录,拼接新的path
            sprintf(path, "%s/%s", root, ptr->d_name);
            total += getFileNum(path);
        }
    }
    closedir(dir);
  
    return total;
}

int main(int argc, char* argv[])
{
    // 读目录， 统计文件个数
    int total = getFileNum(argv[1]);
    // 打印
    printf("%s has file number: %d\n", argv[1], total);
    return 0;
}
```



## 文件描述符的复制(重定向)

默认情况下，一个文件描述符对应一个文件，但是可以有多个文件描述符指向同一个文件，这就是文件描述符的复制

```c
#include <unistd.h>

int dup(int oldfd);//返回的就是一个新的文件描述符(文件描述符表中没有被占用的最小的文件描述符)

int dup2(int oldfd, int newfd);
int dup2(int oldfd, int newfd, int flags);

```

```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>


int main()
{
    int fd = open("a.txt", O_RDWR);
    if(fd == -1)
    {
        perror("open");
        exit(1);
    }

    printf("file open fd = %d\n", fd);

    // 找到进程文件描述表中 ==第一个== 可用的文件描述符
    // 将参数指定的文件复制到该描述符后，返回这个描述符
    int ret = dup(fd);
    if(ret == -1)
    {
        perror("dup");
        exit(1);
    }
    printf("dup fd = %d\n", ret);
    char* buf = "你是猴子派来的救兵吗？？？？\n";
    char* buf1 = "你大爷的，我是程序猿！！！\n";
    write(fd, buf, strlen(buf));
    write(ret, buf1, strlen(buf1));

    close(fd);
    return 0;
}
```

![image-20191104200244080](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191104200244080.png?raw=true)

```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>


int main()
{
    int fd = open("english.txt", O_RDWR);
    if(fd == -1)
    {
        perror("open");
        exit(1);
    }

    int fd1 = open("a.txt", O_RDWR);
    if(fd1 == -1)
    {
        perror("open");
        exit(1);
    }

    printf("fd = %d\n", fd);
    printf("fd1 = %d\n", fd1);

  	//此时fd会被关掉，指向fd1
    int ret = dup2(fd1, fd);
    if(ret == -1)
    {
        perror("dup2");
        exit(1);
    }
    printf("current fd = %d\n", ret);
    char* buf = "主要看气质 ^_^！！！！！！！！！！\n";
    write(fd, buf, strlen(buf));
    write(fd1, "hello, world!", 13);

    close(fd);
    close(fd1);
    return 0;
}

```



## fcntl函数

改变已经打开的文件的属性

比如：打开文件的时候，只读，然后后面发现需要向文件中写内容，此时就必须修改文件的属性(添加写属性)

![image-20191104201142306](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191104201142306.png?raw=true)

![image-20191104201008593](https://github.com/chenyansong1/note/blob/master/images/c_languge/image-20191104201008593.png?raw=true)

```c
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>


int main(void)
{
    int fd;
    int flag;

    // 测试字符串
    char *p = "我们是一个有中国特色的社会主义国家！！！！！！";
    char *q = "呵呵, 社会主义好哇。。。。。。";
    

    // 只写的方式打开文件
    fd = open("test.txt", O_WRONLY);
    if(fd == -1)
    {
        perror("open");
        exit(1);
    }

    // 输入新的内容，该部分会覆盖原来旧的内容
    if(write(fd, p, strlen(p)) == -1)
    {
        perror("write");
        exit(1);
    }

    // 使用 F_GETFL 命令得到文件状态标志
    flag = fcntl(fd, F_GETFL, 0);
    if(flag == -1)
    {
        perror("fcntl");
        exit(1);
    }

    // 将文件状态标志添加 ”追加写“ 选项
    flag |= O_APPEND;//按位或
    // 将文件状态修改为追加写，此时会在文件的末尾添加
    if(fcntl(fd, F_SETFL, flag) == -1)
    {
        perror("fcntl -- append write");
        exit(1);
    }

    // 再次输入新内容，该内容会追加到旧内容的后面
    if(write(fd, q, strlen(q)) == -1)
    {
        perror("write again");
        exit(1);
    }

    // 关闭文件
    close(fd);

    return 0;
}
```

