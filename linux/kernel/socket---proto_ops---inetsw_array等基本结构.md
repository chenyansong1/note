[toc]

转：https://blog.csdn.net/hhhhhyyyyy8/article/details/102755866

# socket---proto_ops---inetsw_array等基本结构

为了体现一切皆文件的理念，套接口在创建时，即与一个文件及文件描述符绑定，此后所有对该套接口的操作都是通过文件描述符来进行的，包括专门的套接口系统调用，基于标准IO系统调用。

为了体现一切皆文件的理念，套接口在创建时，即与一个文件及文件描述符绑定，此后所有对该套接口的操作都是通过文件描述符来进行的，包括专门的套接口系统调用，基于标准IO系统调用。

## 套接口层整体流程图

![img](https://img-blog.csdnimg.cn/20191027105221315.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2hoaGhoeXl5eXk4,size_16,color_FFFFFF,t_70)

## 1. socket结构体

```cpp
struct socket {
	socket_state		state;//socket接口状态，例如SS_CONNECTED
 
	kmemcheck_bitfield_begin(type);
	short			type;//socket类型，例如SOCK_STREAM
	kmemcheck_bitfield_end(type);
 
	unsigned long		flags;//一组标志，SOCK_ASYNC_NOSPACE(发送队列是否已满)等
 
	struct socket_wq __rcu	*wq;//等待该套接字的进程队列
 
	struct file		*file;//指向与该socket相关联的file结构的指针
	struct sock		*sk;//socket网络层表示
	const struct proto_ops	*ops;//用来将套接口系统调用映射到传输层相应的协议实现的结构
};
```

**socket_state state;**   

端口状态，该标志有些状态只对TCP套接字有意义，因为只有TCP是面向连接的，有状态转换的过程。而UDP和RAW则不需要维护端口的状态。

```cpp
//include/linux/net.h
typedef enum {
	SS_FREE = 0,			/* not allocated		*/
	SS_UNCONNECTED,			/* unconnected to any socket	*/
	SS_CONNECTING,			/* in process of connecting	*/
	SS_CONNECTED,			/* connected to socket		*/
	SS_DISCONNECTING		/* in process of disconnecting	*/
} socket_state;
```

**short type;**

套接字类型：

```cpp
enum sock_type {
	SOCK_STREAM	= 1,
	SOCK_DGRAM	= 2,
	SOCK_RAW	= 3,
	SOCK_RDM	= 4,
	SOCK_SEQPACKET	= 5,
	SOCK_DCCP	= 6,
	SOCK_PACKET	= 10,
};
```

**unsigned long flags;** 

一组标志位

```cpp
#define SOCK_ASYNC_NOSPACE	0
#define SOCK_ASYNC_WAITDATA	1
#define SOCK_NOSPACE		2
#define SOCK_PASSCRED		3
#define SOCK_PASSSEC		4
#define SOCK_EXTERNALLY_ALLOCATED 5
```

![img](https://img-blog.csdnimg.cn/20191026213725235.png)

![img](https://img-blog.csdnimg.cn/20191026213740477.png)

**const struct proto_ops   \*ops;**

**用来将套接口系统调用映射到传输层相应的协议实现**。

![img](https://img-blog.csdnimg.cn/20191026213945461.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2hoaGhoeXl5eXk4,size_16,color_FFFFFF,t_70)

## 2. proto_ops结构体

是一组与套接口系统调用相对应的传输层函数指针，proto_ops是套接口系统调用到传输层函数的映射，其中某些操作函数会继续通过proto结构跳转表，进入具体的传输层或网络层的处理。

```cpp
struct proto_ops {
	int		family;//协议族,如AF_INET
	struct module	*owner;//所属模块
        /*与socket系统调用相对应的传输层函数指针*/
	int		(*release)   (struct socket *sock);
	int		(*bind)	     (struct socket *sock,
				      struct sockaddr *myaddr,
				      int sockaddr_len);
	int		(*connect)   (struct socket *sock,
				      struct sockaddr *vaddr,
				      int sockaddr_len, int flags);
	int		(*socketpair)(struct socket *sock1,
				      struct socket *sock2);
	int		(*accept)    (struct socket *sock,
				      struct socket *newsock, int flags);
	int		(*getname)   (struct socket *sock,
				      struct sockaddr *addr,
				      int *sockaddr_len, int peer);
	unsigned int	(*poll)	     (struct file *file, struct socket *sock,
				      struct poll_table_struct *wait);
	int		(*ioctl)     (struct socket *sock, unsigned int cmd,
				      unsigned long arg);
#ifdef CONFIG_COMPAT
	int	 	(*compat_ioctl) (struct socket *sock, unsigned int cmd,
				      unsigned long arg);
#endif
	int		(*listen)    (struct socket *sock, int len);
	int		(*shutdown)  (struct socket *sock, int flags);
	int		(*setsockopt)(struct socket *sock, int level,
				      int optname, char __user *optval, unsigned int optlen);
	int		(*getsockopt)(struct socket *sock, int level,
				      int optname, char __user *optval, int __user *optlen);
#ifdef CONFIG_COMPAT
	int		(*compat_setsockopt)(struct socket *sock, int level,
				      int optname, char __user *optval, unsigned int optlen);
	int		(*compat_getsockopt)(struct socket *sock, int level,
				      int optname, char __user *optval, int __user *optlen);
#endif
	int		(*sendmsg)   (struct socket *sock, struct msghdr *m,
				      size_t total_len);
	/* Notes for implementing recvmsg:
	 * ===============================
	 * msg->msg_namelen should get updated by the recvmsg handlers
	 * iff msg_name != NULL. It is by default 0 to prevent
	 * returning uninitialized memory to user space.  The recvfrom
	 * handlers can assume that msg.msg_name is either NULL or has
	 * a minimum size of sizeof(struct sockaddr_storage).
	 */
	int		(*recvmsg)   (struct socket *sock, struct msghdr *m,
				      size_t total_len, int flags);
	int		(*mmap)	     (struct file *file, struct socket *sock,
				      struct vm_area_struct * vma);
	ssize_t		(*sendpage)  (struct socket *sock, struct page *page,
				      int offset, size_t size, int flags);
	ssize_t 	(*splice_read)(struct socket *sock,  loff_t *ppos,
				       struct pipe_inode_info *pipe, size_t len, unsigned int flags);
	int		(*set_peek_off)(struct sock *sk, int val);
};
```

PF_IENT协议族中定义了三种proto_ops结构 

```cpp
const struct proto_ops inet_stream_ops = {
	.family		   = PF_INET,
	.owner		   = THIS_MODULE,
	.release	   = inet_release,
	.bind		   = inet_bind,
	.connect	   = inet_stream_connect,
	.socketpair	   = sock_no_socketpair,
	.accept		   = inet_accept,
	.getname	   = inet_getname,
	.poll		   = tcp_poll,
	.ioctl		   = inet_ioctl,
	.listen		   = inet_listen,
	.shutdown	   = inet_shutdown,
	.setsockopt	   = sock_common_setsockopt,
	.getsockopt	   = sock_common_getsockopt,
	.sendmsg	   = inet_sendmsg,
	.recvmsg	   = inet_recvmsg,
	.mmap		   = sock_no_mmap,
	.sendpage	   = inet_sendpage,
	.splice_read	   = tcp_splice_read,
#ifdef CONFIG_COMPAT
	.compat_setsockopt = compat_sock_common_setsockopt,
	.compat_getsockopt = compat_sock_common_getsockopt,
	.compat_ioctl	   = inet_compat_ioctl,
#endif

};


const struct proto_ops inet_dgram_ops = {
	.family		   = PF_INET,
	.owner		   = THIS_MODULE,
	.release	   = inet_release,
	.bind		   = inet_bind,
	.connect	   = inet_dgram_connect,
	.socketpair	   = sock_no_socketpair,
	.accept		   = sock_no_accept,
	.getname	   = inet_getname,
	.poll		   = udp_poll,
	.ioctl		   = inet_ioctl,
	.listen		   = sock_no_listen,
	.shutdown	   = inet_shutdown,
	.setsockopt	   = sock_common_setsockopt,
	.getsockopt	   = sock_common_getsockopt,
	.sendmsg	   = inet_sendmsg,
	.recvmsg	   = inet_recvmsg,
	.mmap		   = sock_no_mmap,
	.sendpage	   = inet_sendpage,
#ifdef CONFIG_COMPAT
	.compat_setsockopt = compat_sock_common_setsockopt,
	.compat_getsockopt = compat_sock_common_getsockopt,
	.compat_ioctl	   = inet_compat_ioctl,
#endif

};
```

 

```cpp
static const struct proto_ops inet_sockraw_ops = {
	.family		   = PF_INET,
	.owner		   = THIS_MODULE,
	.release	   = inet_release,
	.bind		   = inet_bind,
	.connect	   = inet_dgram_connect,
	.socketpair	   = sock_no_socketpair,
	.accept		   = sock_no_accept,
	.getname	   = inet_getname,
	.poll		   = datagram_poll,
	.ioctl		   = inet_ioctl,
	.listen		   = sock_no_listen,
	.shutdown	   = inet_shutdown,
	.setsockopt	   = sock_common_setsockopt,
	.getsockopt	   = sock_common_getsockopt,
	.sendmsg	   = inet_sendmsg,
	.recvmsg	   = inet_recvmsg,
	.mmap		   = sock_no_mmap,
	.sendpage	   = inet_sendpage,
#ifdef CONFIG_COMPAT
	.compat_setsockopt = compat_sock_common_setsockopt,
	.compat_getsockopt = compat_sock_common_getsockopt,
	.compat_ioctl	   = inet_compat_ioctl,
#endif
};
```

**proto_ops结构完成的是从与协议无关的套接口层到协议相关的传输层的转换，proto结构完成的是传输层到网络层的映射。**因此，传输层的每个协议都要定义一个特定的proto_ops结构和proto结构实例，**在IPv4协议簇中，一个传输层协议对应一个inet_protosw结构体，inet_protosw结构体包括了proto_ops结构和proto结构**。协议族中的所有inet_protosw结构实例都定义在静态数据inetsw_array中。并且，在网络子系统初始化时，根据每个结构的type成员，也就是socket类型，注册到一个全局的list_head结构体数组inetsw中，socket类型相同的inet_protosw结构体构成双向循环链表。

不知道这个sw是什么意思，socket w?? (switch 转换)

**(1). inetsw[]数组**

```cpp
/* The inetsw table contains everything that inet_create needs to
 * build a new socket.
 */

static struct list_head inetsw[SOCK_MAX];
```

**(2). inetsw_array[]数组**

这个数组初始化害人啦，它有一个元素struct list_head list没有初始化，搞得我以为它只有这几个元素，是说它怎么挂到inetsw[]数组中的元素中去的。

```cpp
static struct inet_protosw inetsw_array[] =
{
	{
		.type =       SOCK_STREAM,
		.protocol =   IPPROTO_TCP,
		.prot =       &tcp_prot,
		.ops =        &inet_stream_ops,
		.flags =      INET_PROTOSW_PERMANENT |
			      INET_PROTOSW_ICSK,
	},
 
	{
		.type =       SOCK_DGRAM,
		.protocol =   IPPROTO_UDP,
		.prot =       &udp_prot,
		.ops =        &inet_dgram_ops,
		.flags =      INET_PROTOSW_PERMANENT,
       },
 
       {
		.type =       SOCK_DGRAM,
		.protocol =   IPPROTO_ICMP,
		.prot =       &ping_prot,
		.ops =        &inet_sockraw_ops,
		.flags =      INET_PROTOSW_REUSE,
       },
 
       {
	       .type =       SOCK_RAW,
	       .protocol =   IPPROTO_IP,	/* wild card */
	       .prot =       &raw_prot,
	       .ops =        &inet_sockraw_ops,
	       .flags =      INET_PROTOSW_REUSE,
       }
};
```

**(3). inet_protosw结构体**
看一下，这个结构体中有一个struct list_head list元素，用于挂到inetsw[]中的元素上。

```cpp
/* This is used to register socket interfaces for IP protocols.  */
struct inet_protosw {
	struct list_head list;
 
        /* These two fields form the lookup key.  */
	unsigned short	 type;	   /* This is the 2nd argument to socket(2). */
	unsigned short	 protocol; /* This is the L4 protocol number.  */
 
	struct proto	 *prot;
	const struct proto_ops *ops;
  
	unsigned char	 flags;      /* See INET_PROTOSW_* below.  */
};
```

 **(4). 在inet_init函数中，对inetsw[]数组进行了初始化，**

```cpp
static int __init inet_init(void)
{
	struct inet_protosw *q;
	struct list_head *r;
	...
	/* Register the socket-side information for inet_create. */
	/*初始化链表头结点*/
	for (r = &inetsw[0]; r < &inetsw[SOCK_MAX]; ++r)
		INIT_LIST_HEAD(r);
	
	/*把type相同的inetsw_array元素，链接到对应的双向链表中。*/
	for (q = inetsw_array; q < &inetsw_array[INETSW_ARRAY_LEN]; ++q)
		inet_register_protosw(q);
	...
}
```

**(5). inet_register_protosw()函数**

在这个函数中，把inetsw_array结构体挂到对应的链表上。

```cpp
void inet_register_protosw(struct inet_protosw *p)
{
	struct list_head *lh;
	struct inet_protosw *answer;
	int protocol = p->protocol;
	struct list_head *last_perm;
 
	spin_lock_bh(&inetsw_lock);
 
	if (p->type >= SOCK_MAX)
		goto out_illegal;
 
	/* If we are trying to override a permanent protocol, bail. */
	answer = NULL;
	last_perm = &inetsw[p->type];
	list_for_each(lh, &inetsw[p->type]) {
		answer = list_entry(lh, struct inet_protosw, list);
 
		/* Check only the non-wild match. */
		/*这个函数这里有点怪，不知道它想要干什么，按理说，找到对应的首节点之后，
		直接插入元素就可以了，怎么还干这么多幺蛾子。*/
		if (INET_PROTOSW_PERMANENT & answer->flags) {
			if (protocol == answer->protocol)
				break;
			last_perm = lh;
		}
 
		answer = NULL;
	}
	if (answer)
		goto out_permanent;
 
	/* Add the new entry after the last permanent entry if any, so that
	 * the new entry does not override a permanent entry when matched with
	 * a wild-card protocol. But it is allowed to override any existing
	 * non-permanent entry.  This means that when we remove this entry, the
	 * system automatically returns to the old behavior.
	 */
	list_add_rcu(&p->list, last_perm);
out:
	spin_unlock_bh(&inetsw_lock);
 
	return;
 
out_permanent:
	pr_err("Attempt to override permanent protocol %d\n", protocol);
	goto out;
 
out_illegal:
	pr_err("Ignoring attempt to register invalid socket type %d\n",
	       p->type);
	goto out;

}
```

初始化完成后，inetsw[]和inetsw_array[]关系示意图如图所示。

![img](https://img-blog.csdnimg.cn/20191026135631564.png)

以第一个元素type=SOCK_STREAM，protocol=IPPROTO_TCP为例，该类型适用与tcp协议，当创建tcp socket时，其操作socket->ops赋值为&inet_stream_ops，对应的传输控制块操作sock->sk_prot赋值为&tcp_prot；

https://www.cnblogs.com/wanpengcoder/p/7623101.html


