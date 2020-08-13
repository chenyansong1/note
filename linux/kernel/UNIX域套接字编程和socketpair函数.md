[toc]

转自：https://blog.csdn.net/jnu_simba/article/details/9079359

# 一、UNIX Domain Socket IPC

socket API原本是为网络通讯设计的，但后来在socket的框架上发展出一种IPC机制，就是UNIX Domain Socket。虽然网络socket也可用于同一台主机的进程间通讯（通过loopback地址127.0.0.1），但是UNIX Domain Socket用于IPC更有效率：不需要经过网络协议栈，不需要打包拆包、计算校验和、维护序号和应答等，只是将应用层数据从一个进程拷贝到另一个进程。UNIX域套接字与TCP套接字相比较，在同一台主机的传输速度前者是后者的两倍。这是因为，IPC机制本质上是可靠的通讯，而网络协议是为不可靠的通讯设计的。UNIX Domain Socket也提供面向流和面向数据包两种API接口，类似于TCP和UDP，但是面向消息的UNIX Domain Socket也是可靠的，消息既不会丢失也不会顺序错乱。



使用UNIX Domain Socket的过程和网络socket十分相似，也要先调用socket()创建一个socket文件描述符，address family指定为AF_UNIX，type可以选择SOCK_DGRAM或SOCK_STREAM，protocol参数仍然指定为0即可。

UNIX Domain Socket与网络socket编程最明显的不同在于地址格式不同，用结构体sockaddr_un表示，网络编程的socket地址是IP地址加端口号，而UNIX Domain Socket的地址是一个socket类型的文件在文件系统中的路径，这个socket文件由bind()调用创建，如果调用bind()时该文件已存在，则bind()错误返回。

```c
#define UNIX_PATH_MAX    108

struct sockaddr_un {
sa_family_t sun_family;        /* AF_UNIX */
char sun_path[UNIX_PATH_MAX];  /* pathname */
};
```

# 二、客户服务器程序

```c
/*************************************************************************
    > File Name: echoser_tcp.c
    > Author: Simba
    > Mail: dameng34@163.com
    > Created Time: Sun 03 Mar 2013 06:13:55 PM CST
 ************************************************************************/

#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<errno.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<string.h>
#include<sys/un.h>

#define ERR_EXIT(m) \
    do { \
        perror(m); \
        exit(EXIT_FAILURE); \
    } while (0)

void echo_ser(int conn)
{
    char recvbuf[1024];
    int n;
    while (1)
    {

        memset(recvbuf, 0, sizeof(recvbuf));
        n = read(conn, recvbuf, sizeof(recvbuf));
        if (n == -1)
        {
            if (n == EINTR)
                continue;
            ERR_EXIT("read error");
        }
        else if (n == 0)
        {
            printf("client close\n");
            break;
        }

        fputs(recvbuf, stdout);
        write(conn, recvbuf, strlen(recvbuf));
    }

    close(conn);
}

/* unix domain socket与TCP套接字相比较，在同一台主机的传输速度前者是后者的两倍。*/
int main(void)
{
    int listenfd;
    if ((listenfd = socket(PF_UNIX, SOCK_STREAM, 0)) < 0)
        ERR_EXIT("socket error");

    unlink("/tmp/test socket"); //地址复用
    struct sockaddr_un servaddr;
    memset(&servaddr, 0, sizeof(servaddr));
    servaddr.sun_family = AF_UNIX;
    strcpy(servaddr.sun_path, "/tmp/test socket");

    if (bind(listenfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
        ERR_EXIT("bind error");

    if (listen(listenfd, SOMAXCONN) < 0)
        ERR_EXIT("listen error");

    int conn;
    pid_t pid;

    while (1)
    {
        conn = accept(listenfd, NULL, NULL);
        if (conn == -1)
        {
            if (conn == EINTR)
                continue;
            ERR_EXIT("accept error");
        }
        pid = fork();
        if (pid == -1)
            ERR_EXIT("fork error");
        if (pid == 0)
        {
            close(listenfd);
            echo_ser(conn);
            exit(EXIT_SUCCESS);
        }
        close(conn);
    }
    return 0;
}
```

客户端

```c
/*************************************************************************
    > File Name: echocli_tcp.c
    > Author: Simba
    > Mail: dameng34@163.com
    > Created Time: Sun 03 Mar 2013 06:13:55 PM CST
 ************************************************************************/

#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>

#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<errno.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<string.h>
#include<sys/un.h>

#define ERR_EXIT(m) \
    do { \
        perror(m); \
        exit(EXIT_FAILURE); \
    } while (0)

void echo_cli(int conn)
{
    char sendbuf[1024] = {0};
    char recvbuf[1024] = {0};
    while (fgets(sendbuf, sizeof(sendbuf), stdin) != NULL)
    {
        write(conn, sendbuf, strlen(sendbuf));
        read(conn, recvbuf, sizeof(recvbuf));
        fputs(recvbuf, stdout);
        memset(recvbuf, 0, sizeof(recvbuf));
        memset(sendbuf, 0, sizeof(sendbuf));
    }
    close(conn);
}


int main(void)
{
    int sock;
    if ((sock = socket(PF_UNIX, SOCK_STREAM, 0)) < 0)
        ERR_EXIT("socket error");

    struct sockaddr_un servaddr;
    memset(&servaddr, 0, sizeof(servaddr));
    servaddr.sun_family = AF_UNIX;
    strcpy(servaddr.sun_path, "/tmp/test socket");

    if (connect(sock, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
        ERR_EXIT("connect error");

    echo_cli(sock);

    return 0;
}
```

server 使用fork 的形式来接受多个连接，server调用bind 会创建一个文件，如下所示：

simba@ubuntu:~/Documents/code/linux_programming/UNP/socket$ ls -l /tmp/test\ socket 
srwxrwxr-x 1 simba simba 0 Jun 12 15:27 /tmp/test socket

即文件类型为s，表示SOCKET文件，与FIFO（命名管道）文件，类型为p，类似，都表示内核的一条通道，读写文件实际是在读写内核通道。程序中调用unlink（解除硬链接） 是为了在开始执行程序时删除以前创建的文件，以便在重启服务器时不会提示address in use。其他方面与以前说过的回射客户服务器程序没多大区别，不再赘述。

# 三、UNIX域套接字编程注意点

1、bind成功将会创建一个文件，权限为0777 & ~umask
2、sun_path最好用一个绝对路径
3、UNIX域协议支持流式套接口与报式套接口
4、UNIX域流式套接字connect发现监听队列满时，会立刻返回一个ECONNREFUSED，这和TCP不同，如果监听队列满，会忽略到来的SYN，这导致对方重传SYN。

