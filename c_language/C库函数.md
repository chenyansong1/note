[toc]

# C库函数

![](/Users/chenyansong/Documents/note/images/c_languge/C库IO函数工作流程.png)

> C库函数是在内部封装了一个I/O缓冲区，但是如果是系统的I/O函数是没有这样的缓冲区的



# 虚拟地址空间

![](/Users/chenyansong/Documents/note/images/c_languge/pcb和文件描述符.png)

![](/Users/chenyansong/Documents/note/images/c_languge/虚拟地址空间.png)

查看文件的格式

```shell
chenyansongdeMacBook-Pro:c_language chenyansong$ file a.out 
a.out: Mach-O 64-bit executable x86_64
chenyansongdeMacBook-Pro:c_language chenyansong$ file /tmp//sunlogin_helper.log 
/tmp//sunlogin_helper.log: ASCII text
chenyansongdeMacBook-Pro:c_language chenyansong$ 
```

![image-20191103111757079](/Users/chenyansong/Documents/note/images/c_languge/image-20191103111757079.png)



>  cpu 为什么要使用虚拟地址空间与物理地址空间映射？解决了什么样的问题？

1. 方便编译器和操作系统安排程序的地址分布。
   		程序可以使用一系列相邻的虚拟地址来访问物理内存中不相邻的大内存缓冲区。
   
2. 方便进程之间隔离
   不同进程使用的虚拟地址彼此隔离。一个进程中的代码无法更改正在由另一进程使用的物理内存。
   
3. 方便OS使用你那可怜的内存。
   程序可以使用一系列虚拟地址来访问大于可用物理内存的内存缓冲区。当物理内存的供应量变小时，
   内存管理器会将物理内存页（通常大小为 4 KB）保存到磁盘文件。数据或代码页会根据需要在物理内存与磁盘之间移动。
      		
   
      		

![](/Users/chenyansong/Documents/note/images/c_languge/image-20191103114054898.png)



# C库函数与系统函数的关系

![7_库函数与系统函数的关系](/Users/chenyansong/Documents/note/images/c_languge/库函数与系统函数的关系.png)



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

![image-20191103144732114](/Users/chenyansong/Documents/note/images/c_languge/image-20191103144732114.png)



![image-20191103144909912](/Users/chenyansong/Documents/note/images/c_languge/image-20191103144909912.png)

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

![](/Users/chenyansong/Documents/note/images/c_languge/image-20191103163846065.png)

```c
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

int stat(const char *path, struct stat *buf);
int fstat(int fd, struct stat *buf);
int lstat(const char *path, struct stat *buf);
```

![image-20191103164733805](/Users/chenyansong/Documents/note/images/c_languge/image-20191103164733805.png)

![image-20191103164832416](/Users/chenyansong/Documents/note/images/c_languge/image-20191103164832416.png)

![3_st_mode](/Users/chenyansong/Documents/note/images/c_languge/st_mode.png)

![image-20191103165609239](/Users/chenyansong/Documents/note/images/c_languge/image-20191103165609239.png)

![](/Users/chenyansong/Documents/note/images/c_languge/image-20191103170006135.png)

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

![](/Users/chenyansong/Documents/note/images/c_languge/5_链接的追踪.png)



## access函数

![](/Users/chenyansong/Documents/note/images/c_languge/image-20191103212836954.png)

