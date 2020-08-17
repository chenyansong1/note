[toc]

转自：https://www.cnblogs.com/alantu2018/p/8612722.html





```c
main()  
{  
    int sock;  
    FILE *fp;  
    struct fd_set fds;  
    struct timeval timeout={3,0}; //select等待3秒，3秒轮询，要非阻塞就置0  
    char buffer[256]={0}; //256字节的接收缓冲区  
    /* 假定已经建立UDP连接，具体过程不写，简单，当然TCP也同理，主机ip和port都已经给定，要写的文件已经打开 
    sock=socket(...); 
    bind(...); 
    fp=fopen(...); */  
    while(1)  
   {  
        FD_ZERO(&fds); //每次循环都要清空集合，否则不能检测描述符变化  
        FD_SET(sock,&fds); //添加描述符  
        FD_SET(fp,&fds); //同上  
        maxfdp=sock>fp?sock+1:fp+1;    //描述符最大值加1  
        switch(select(maxfdp,&fds,&fds,NULL,&timeout))   //select使用  
        {  
            case -1: exit(-1);break; //select错误，退出程序  
            case 0:break; //再次轮询  
            default:  
                  if(FD_ISSET(sock,&fds)) //测试sock是否可读，即是否网络上有数据  
                  {  
                        recvfrom(sock,buffer,256,.....);//接受网络数据  
                        if(FD_ISSET(fp,&fds)) //测试文件是否可写  
                            fwrite(fp,buffer...);//写入文件  
                         buffer清空;  
                   }// end if break;  
          }// end switch  
     }//end while  
}//end main   
```







内核轮询的逻辑：

1. 循环遍历传入的fdset，对传入的fd=1的调用fd对应的poll函数（poll：轮询），poll返回的结果是：读是否准备好，或者写是否准备好
2. poll的逻辑，其实就是将当前进程加入fd等待资源的wait队列中，这样如果等待的资源可用了，那么就会唤醒队列中的进程







