---
title: zookeeper选举及数据一致性
categories: hadoop   
tags: [zookeeper]
---



[TOC]

# zab协议

ZooKeeper Atomic Broadcast 即ZooKeeper原子消息广播协议，简称为ZAB（支持崩溃恢复的原子广播协议）
* 选举过程需要依赖此协议
* 数据写入过程也需要此协议
* ab的核心是定义了那些会改变zk服务器数据状态的事务请求的处理方式


所有事务请求必须由一个全局唯一的服务器来协调处理，这样的服务器被称为Leader服务器，而余下的其它服务器则成ОFollower服务器。 Leader服务器负责将一个客户端事务请求转换成一个proposal(提议),并将该Proposal分发给集群中所有的Follower服务器。之后Leader服务器需要等待所有Follower服务器的反馈，一旦超过半数的Follower服务器进行了正确的反馈后，那ТLeader就会再次向所有的Follower服务器分发Commit消息，要求其将前一个Proposal进行提交


* zab协议的三阶段:
  * 发现ͧDiscovery，即选举Leader过程
  * 同步(Synchronization),选举出新的Leader后,Follwer或者Observer从Leader同步最新数据
  * 广播,同步完成之后,就可以接收客户端新的事物请求,并进行广播,实现数据在集群节点的副本存储



## 1.原子广播



如果大家了解分布式事务的 2pc 和 3pc 协议的话(不了解 也没关系，我们后面会讲)，消息广播的过程实际上是一个 简化版本的二阶段提交过程 

1. leader 接收到消息请求后，将消息赋予一个全局唯一的 64 位自增 id，叫:zxid，通过 zxid 的大小比较既可以实现因果有序这个特征 

2. leader 为每个 follower 准备了一个 FIFO 队列(通过 TCP 协议来实现，以实现了全局有序这一个特点)将带有 zxid的消息作为一个提案(proposal)分发给所有的 follower 

3. 当 follower 接收到 proposal，先把 proposal 写到磁盘， 写入成功以后再向 leader 回复一个 ack 

4. 当 leader 接收到合法数量(超过半数节点)的 ACK 后，leader 就会向这些 follower 发送 commit 命令，同时会 

   在本地执行该消息 

5. 当 follower 收到消息的 commit 命令以后，会提交该消息 



![image-20180710203900889](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/yuanzi.png)

leader 的投票过程，不需要 Observer 的 ack，也就是 Observer 不需要参与投票过程，但是 Observer 必须要同 步 Leader 的数据从而在处理请求的时候保证数据的一致性 



## 2.崩溃恢复

1. 当leader失去了过半的follower节点联系
2. 当leader服务挂了

此时整个集群就会进去崩溃恢复阶段



ZAB 协议的这个基于原子广播协议的消息广播过程，在正 常情况下是没有任何问题的，但是一旦 Leader 节点崩溃， 或者由于网络问题导致 Leader 服务器失去了过半的 Follower 节点的联系(leader 失去与过半 follower 节点联 系，可能是 leader 节点和 follower 节点之间产生了网络分 区，那么此时的 leader 不再是合法的 leader 了)，那么就 会进入到崩溃恢复模式。在 ZAB 协议中，为了保证程序的 正确运行，整个恢复过程结束后需要选举出一个新的 Leader 

为了使 leader 挂了后系统能正常工作，需要解决以下两 个问题

1. 已经被处理的消息不能丢失 (对于follower来说，需要选举事物id最大的节点作为follower)

当 leader 收到合法数量 follower 的 ACKs 后，就向 各个 follower 广播 COMMIT 命令，同时也会在本地 执行 COMMIT 并向连接的客户端返回「成功」。但是如 果在各个 follower 在收到 COMMIT 命令前 leader 就挂了，导致剩下的服务器并没有执行都这条消息。 



当leader收到合法数据，一部分节点收到commit，但是有部分节点没有收到commit，

![image-20180710213910789](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/huifu.png)

leader 对事务消息发起 commit 操作，但是该消息在 follower1 上执行了，但是 follower2 还没有收到 commit， 就已经挂了，而实际上客户端已经收到该事务消息处理成 功的回执了。所以在 zab 协议下需要保证所有机器都要执 行这个事务消息  



2. 被丢弃的消息不能再次出现 

当 leader 接收到消息请求生成 proposal 后就挂了，其 他 follower 并没有收到此 proposal，因此经过恢复模式 重新选了 leader 后，这条消息是被跳过的。 此时，之前 挂了的 leader 重新启动并注册成了 follower，他保留了 被跳过消息的 proposal 状态，与整个系统的状态是不一 致的，需要将其删除。 

ZAB 协议需要满足上面两种情况，就必须要设计一个 leader 选举算法:能够确保已经被 leader 提交的事务 Proposal 能够提交、同时丢弃已经被跳过的事务 Proposal。 针对这个要求 

1. 如果 leader 选举算法能够保证新选举出来的 Leader 服 务器拥有集群中所有机器最高编号(ZXID 最大)的事务 Proposal，那么就可以保证这个新选举出来的 Leader 一 定具有已经提交的提案。因为所有提案被 COMMIT 之 前必须有超过半数的 followerACK，即必须有超过半数 节点的服务器的事务日志上有该提案的 proposal，因此， 只要有合法数量的节点正常工作，就必然有一个节点保存了所有被 COMMIT 消息的 proposal 状态 另外一个，zxid 是 64 位，高 32 位是 epoch 编号，每经过 一次 Leader 选举产生一个新的 leader，新的 leader 会将 epoch 号+1，低 32 位是消息计数器，每接收到一条消息 这个值+1，新 leader 选举后这个值重置为 0.这样设计的好 处在于老的 leader 挂了以后重启，它不会被选举为 leader， 因此此时它的 zxid 肯定小于当前新的 leader。当老的 leader 作为 follower 接入新的 leader 后，新的 leader 会 让它将所有的拥有旧的 epoch 号的未被 COMMIT 的 proposal 清除 



1. 首先是leader在同步了事物p1-01到2个follower之后，挂了

![image-20180710214359327](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/xuanju2.png)

2.此时选择出来了一个节点作为leader, 然后我们将原来挂掉的leader恢复，那么此时它将是follower的角色，此时需要数据同步，那么会将此节点（原来是leader的节点）中的 p2-02,p2-03从本地删除，然后同步p2-10的事物

![image-20180710213510402](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/xuanju3.png)







epoch

vim /tmp/zookeeper/version-2/currentEpoch






# 关于 2PC 提交做分布式事物

(Two Phase Commitment Protocol)当一个事务操作需 要跨越多个分布式节点的时候，为了保持事务处理的 ACID 特性，就需要引入一个“协调者”(TM)来统一调度所有分 布式节点的执行逻辑，这些被调度的分布式节点被称为 AP。 TM 负责调度 AP 的行为，并最终决定这些 AP 是否要把事 务真正进行提交;因为整个事务是分为两个阶段提交，所 以叫 2pc 



![image-20180710195950711](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/2pc.png)





![image-20180710200054166](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/2pc2.png)





阶段一:提交事务请求(投票)

1. 事务询问 协调者向所有的参与者发送事务内容，询问是否可以执行事 务提交操作，并开始等待各参与者的响应 
2. 执行事务
    各个参与者节点执行事务操作，并将 Undo 和 Redo 信息记 录到事务日志中，尽量把提交过程中所有消耗时间的操作和 准备都提前完成确保后面 100%成功提交事务 
3. 各个参与者向协调者反馈事务询问的响应 如果各个参与者成功执行了事务操作，那么就反馈给参与者 yes 的响应，表示事务可以执行;如果参与者没有成功执行 事务，就反馈给协调者 no 的响应，表示事务不可以执行， **上面这个阶段有点类似协调者组织各个参与者对一次事务 操作的投票表态过程**，因此 2pc 协议的第一个阶段称为“投 票阶段”，即**各参与者投票表名是否需要继续执行接下去的 事务提交操作**。 



阶段二:执行事务提交 

在这个阶段，协调者会根据各参与者的反馈情况来决定最终是 否可以进行事务提交操作，正常情况下包含两种可能:执行事务、 中断事务 



# 选择的过程



在 zookeeper 中，客户端会随机连接到 zookeeper 集群中 的一个节点，如果是读请求，就直接从当前节点中读取数 据，如果是写请求，那么请求会被转发给 leader 提交事务， 然后 leader 会广播事务，只要有超过半数节点写入成功， 那么写请求就会被提交(类 2PC 事务) 



![image-20180710200720832](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/xuanju.png)



* 对一次事物请求（request）
* 2pc(第一次是粉红色，返回的是绿色；第二次是commit请求)

所有事务请求必须由一个全局唯一的服务器来协调处理， 这个服务器就是 Leader 服务器，其他的服务器就是 follower。leader 服务器把客户端的失去请求转化成一个事 务 Proposal(提议)，并把这个 Proposal 分发给集群中的 所有 Follower 服务器。之后 Leader 服务器需要等待所有 Follower 服务器的反馈，一旦超过半数的 Follower 服务器 进行了正确的反馈，那么 Leader 就会再次向所有的 Follower 服务器发送 Commit 消息，要求各个 follower 节 点对前面的一个 Proposal 进行提交; 



这就是为什么要zookeeper中需要2n+1的节点数目，因为需要有投票机制的存在，那么此时就需要有超过半数的投票



* 选举的状态（LOOKING）
* leader的状态LEADING
* follower的状态FOLLOWING
* Observer的状态OBSERVIING



通常 zookeeper 是由 2n+1 台 server 组成，每个 server 都 知道彼此的存在。对于 2n+1 台 server，只要有 n+1 台(大 多数)server 可用，整个系统保持可用。我们已经了解到， 一个 zookeeper 集群如果要对外提供可用的服务，那么集 群中必须要有过半的机器正常工作并且彼此之间能够正常 通信，基于这个特性，如果向搭建一个能够允许 F 台机器 down 掉的集群，那么就要部署 2*F+1 台服务器构成的 zookeeper 集群。因此 3 台机器构成的 zookeeper 集群， 能够在挂掉一台机器后依然正常工作。一个 5 台机器集群 的服务，能够对 2 台机器怪调的情况下进行容灾。**如果一台由 6 台服务构成的集群，同样只能挂掉 2 台机器**。因此， **5 台和 6 台在容灾能力上并没有明显优势，反而增加了网 络通信负担**。系统启动时，集群中的 server 会选举出一台 server 为 Leader，其它的就作为 follower(这里先不考虑 observer 角色)。 之所以要满足这样一个等式，是因为一个节点要成为集群 中的 leader，需要有超过及群众过半数的节点支持，这个 涉及到 leader 选举算法。同时也涉及到事务请求的提交投 票 



## 服务器启动时的 leader 选举 

每个节点启动的时候状态都是 LOOKING，处于观望状态， 接下来就开始进行选主流程 

进行 Leader 选举，至少需要两台机器(具体原因前面已经 讲过了)，我们选取 3 台机器组成的服务器集群为例。在集 群初始化阶段，当有一台服务器 Server1 启动时，它本身是 无法进行和完成 Leader 选举，当第二台服务器 Server2 启 动时，这个时候两台机器可以相互通信，每台机器都试图 找到 Leader，于是进入 Leader 选举过程。选举过程如下 

1. 每个 Server 发出一个投票。由于是初始情况，Server1 

   和 Server2 都会将自己作为 Leader 服务器来进行投 票，每次投票会包含所推举的服务器的 myid 和 ZXID、 epoch，使用(myid, ZXID,epoch)来表示，此时 Server1 的投票为(1, 0)，Server2 的投票为(2, 0)，然后各自将 这个投票发给集群中其他机器。 

2. 接受来自各个服务器的投票。集群的每个服务器收到 投票后，首先判断该投票的有效性，如检查是否是本 轮投票(epoch)、是否来自 LOOKING 状态的服务器。 

3. 处理投票。针对每一个投票，服务器都需要将别人的 投票和自己的投票进行 PK，PK 规则如下 

   1.  优先检查 ZXID。ZXID 比较大的服务器优先作为 Leader 
   2. 如果 ZXID 相同，那么就比较 myid。myid 较大的 服务器作为 Leader 服务器。 

   对于 Server1 而言，它的投票是(1, 0)，接收 Server2 的投票为(2, 0)，首先会比较两者的 ZXID，均为 0，再 比较 myid，此时 Server2 的 myid 最大，于是更新自 己的投票为(2, 0)，然后重新投票，对于 Server2 而言， 它不需要更新自己的投票，只是再次向集群中所有机 器发出上一次投票信息即可 

4. 统计投票。每次投票后，服务器都会统计投票信息， 判断是否已经有过半机器接受到相同的投票信息，对 于 Server1、Server2 而言，都统计出集群中已经有两 台机器接受了(2, 0)的投票信息，此时便认为已经选出 了 Leader。 

5. 改变服务器状态。一旦确定了 Leader，每个服务器就 会更新自己的状态，如果是 Follower，那么就变更为 FOLLOWING，如果是 Leader，就变更为 LEADING。 



## 运行过程中的 leader 选举 



当集群中的 leader 服务器出现宕机或者不可用的情况时， 那么整个集群将无法对外提供服务，而是进入新一轮的 Leader 选举，服务器运行期间的 Leader 选举和启动时期 的 Leader 选举基本过程是一致的。 

1. 变更状态。Leader 挂后，余下的非 Obser ver 服务器 都会将自己的服务器状态变更为 LOOKING，然后开 始进入 Leader 选举过程。 
2. 每个 Ser ver 会发出一个投票。在运行期间，每个服务 器上的 ZXID 可能不同，此时假定 Server1 的 ZXID 为 123，Server3 的 ZXID 为 122;在第一轮投票中，Server1 和 Server3 都会投自己，产生投票(1, 123)，(3, 122)， 然后各自将投票发送给集群中所有机器。接收来自各 个服务器的投票。与启动时过程相同。 
3. 处理投票。与启动时过程相同，此时，Ser ver1 将会成 为 Leader。 
4. 统计投票。与启动时过程相同 
5. 改变服务器的状态。与启动时过程相同 



# 服务器角色
* Leader
  * 事物请求的唯一调度和处理者,保证集群事物处理的顺序性
  * 集群内部各服务器的调度者
* Follower
  * 处理客户端非事物请求,转发事物请求给Leader服务器
  * 参与事物请求 Proposal 的投票(需要半数以上服务器 通过才能通知 leader commit 数据; Leader 发起的提案， 要求 Follower 投票)  
  * 参与Leader选举投票
* Observer
  * 处理客户端非事物请求,转发事物请求给Leader服务器
  * 不参与任何形式的投票,包括选举和事务投票(超过半数确认)
  * 此角色存在通常是为了提高读性能


# 服务器状态

* LOOKING
	* 寻找Leader状态
	* 当服务器处于此状态时,表示当前没有Leader,需要进入选举流程
* Following
	* 跟随者状态,表名当前服务器角色是Follower
* Observing
	* 观察者状态,表明当前服务器角色为Observer
* Leading
	* Leader状态,表明当前服务器角色为Leader

以上四种状态由:org.apache.zookeeper.server.quorum. ServerState类维护


# 集群通信
* 基于tcp协议

为了避免重复创建两个节点之间的tcp连接,zk按照myid数值方向来建立连接,id大的向小的发起连接，在zk实现中，当发现自己的id比发起连接的id还大时,会关闭此连接

* 多端口

配置文件中第一个端口是通信和数据同步端口,默认是2888
第二个端口是投票端口,默认是3888


# 选举触发的时机

* 集群启动
  * 寻找Leader状态
  * 当服务器处于此状态时,表示当前没有Leader,需要进入选举流程

* 崩溃恢复
	* Leader宕机
	* 网络原因导致过半数节点与Leader心跳中断


# 影响成为Leader的因素

* 数据新旧程度
	* 只有拥有最新数据的接单才能有机会成为Leader
	* 通过事务id(zxid)的大小来表示数据的新旧,越大代表数据越新

* myid
	* 集群启动时,会在data目录下配置myid文件,里面的数字代表当前zk服务器节点的编号
	* 当zk服务器节点数据一样新时,myid中数字越大的就会选举称为Leader
	* 当集群中已经有Leader时,新加入的节点不会影响原有的集群

* 投票数量
	* 只有得到集群中多半的投票,才能称为Leader
	* 多半即:(n/2)+1,其中n为集群中的节点数量


# zxid(事务id)的构成

* 主进程周期
  * 也叫epoch
  * 选举的轮次,每多一次选举,则主进程周期加1
  * zxid总共64位来表示,其高32位代表主进程周期
  * 比较数据新旧的时候,先比较epoch的大小
* 事务单调递增的计数器
  * zxid的低32位表示,选举完成后,从0开始

zxid，也就是事务 id， 为了保证事务的顺序一致性，zookeeper 采用了递增的事 务 id 号(zxid)来标识事务。所有的提议(proposal)都 在被提出的时候加上了 zxid。实现中 zxid 是一个 64 位的 数字，它高 32 位是 epoch(ZAB 协议通过 epoch 编号来 区分 Leader 周期变化的策略)用来标识 leader 关系是否 改变，每次一个 leader 被选出来，它都会有一个新的 epoch=(原来的 epoch+1)，标识当前属于那个 leader 的 统治时期。低 32 位用于递增计数。低 32 位是消息计数器，每接收到一条消息 这个值+1，新 leader 选举后这个值重置为 0.

epoch 的变化大家可以做一个简单的实验，

1. 启动一个 zookeeper 集群。 
2. 在/tmp/zookeeper/VERSION-2 路径下会看到一个 

currentEpoch 文件。文件中显示的是当前的 epoch

3. 把 leader 节点停机，这个时候在看 currentEpoch 会有 变化。 随着每次选举新的 leader，epoch 都会发生变化 






# zk的初次启动时的选举过程
* 第一步:启动myid为1的节点,此时zxid为0,此时没法选举出主节点
* 第二步:启动myid为2的节点,他的zxid也为0,此时2这个节点成为主节点
* 第三步:启动myid为3的节点,因为已经有主节点,则3加入集群,2还是leader


# zk主节点宕机的选举过程

* 场景说明
	* 3台机器,此时server2为主,并且server2宕机

* 选举流程
	* 变更状态
		* 当leader宕机后,其他节点的状态变更为Looking
	* 每个server发出一个投自己的票的投票
		* 生成投票信息(myid,zxid)
		* 假定:server1为(1,123),server3为(2,122)
		* server1发给server3,server3发给server1
	* 接收投票
	* 投票处理
		* server1收到server3,因为server1的zxid(123)比server3的zxid(122)大,所以server3修改自己的投票为(1,123),然后发送给server1
		* server1收到server3的投票,因为123大于122,因此不改变自己的投票
	* 投票统计
		* server3统计:自己收到投票(包括自己投的)中,(1,123)是两票
		* server1统计:自己收到的投票(包括自己投的)中,(1,123)是两票
	* 修改服务器状态
		* server3,选出的leader是server1,因此自己进入followering,即:follower角色
		* server1,选出的leader是server1,即自己,因此自己进入leading状态,即:自己是leader角色



# 同步
* 同步时机

当leader完成选举后,follower需要与新的leader同步数据

* 同步准备--leader
	* leader告诉其他follower当前最新数据是什么即zxid
		* leader构建一个newleader的包,包括当前最大的zxid,发送给所有的follower或者Observer
	* leader给每个follower创建一个线程leaderHandler来负责处理每个follower的数据同步请求,同时主线程开始阻塞,只有超过一半的follower同步完成,同步过程才完成,leader才能成为真正的leader
	* 根据同步算法进行操作
* 同步准备--follower端
	* 选举完成后,尝试与leader建立同步连接,如果一段时间没有连接上就报错超时,重新回到选举状态
	* 向leader发送followerinfo包,带上follower自己最大的zxid
	* 根据不同同步算法进行操作
* 初始化
	* minCommittedLog:最小的事务日志id,即zxid(没有被快照存储的日志文件的第一条,每次快照存储完,会重新生成一个事务日志文件)
	* maxCommittedLog:事务日志中最大的事务,即zxid
* 同步算法
	* 直接差异化同步(diff同步)
	* 仅回滚同步(trunc),即删除多余的事务日志,比如原来的主宕机后重新加入,可能存在他自己写入提交但是别的节点还没来得及提交
	* 先回滚再差异化同步(trunc+diff同步)
	* 全量同步(snap同步)
	

# 同步应用的场景
## 场景一
* 把follower最后的事务zxid称为peerLastZxid
* 当minCommittedLog<peerLastZxid<maxCommittedLog

同步方案:
* 直接差异化同步
* leader会给follower服务器发送diff指令,意思是:进入差异化数据同步阶段,leader会把proposal同步给follower
* 实际同步过程会先发送数据修改proposal,然后再发送commit指令数据包

举例说明

某个时刻Leader服务器为proposal队列对应的zxid依次是0x500000001 0x500000002 0x500000003 0x500000004 0x500000005
此时follower的peerLastZxid为0x500000003，因此需要把0x500000004 0x500000005同步给follower

差异化同步的消息发送顺序如下:

发送顺序|数据包类型|对应的zxid
:-------:|:---------:|:--------:
1|proposal|0x500000004
2|commit|0x500000004
3|proposal|0x500000005
4|commit|0x500000005



执行的流程如下:
* follower端收到diff指令,然后进入diff同步阶段
* follower收到同步的数据和提交命令,并应用到内存数据库中
* 同步完成后:
	* leader会发送一个newLeader指令,通知follower已经将最新的数据都同步给follower了
	* follower收到newLeader指令后反馈一个ack消息,表名自己已经完成同步
* 单个follower的同步完成,Leader进入集群的"过半策略"等待状态
* 当有超过一半的follower都同步完成后,leader会向已经完成同步的follower发送uptodate指令,用于通知follower完成数据同步,可以对外提供服务了
* follower收到leader的uptodate指令后,会终止数据同步流程,向leader再次反馈ack消息


## 场景二

Leader在提交本地完成，还没有把事务Proposal提交给其它节点前，leader宕机了

假设3个节点的集群，分别是A,B,Cͺ没有宕机前，leader是B，已经发送过0X500000001和0X500000002的数据和事务提交proposal，并且发送了0X500000003的数据修改提议，但是在B节点
发送事务提交的proposal之前，B宕机了，由于B是本机发送，所以B的本地事务已经提交，即B最新的数据是0X500000003

在A和C进行选举后，C成为主,并且进行过两次数据修改，对应的Proposal是0X600000001 0X600000002


B机器恢复后加入集群(AC), 重新进行数据同步，对于B来说，peerLastZxidО0X500000003,对于当前的主C来说, minCommittedLog= 0X500000001 maxCommittedLog= 0X600000002


同步方案:
* B恢复后,并且向已有的集群(AC)注册后,向C发起同步连接请求
* B向leader(C)发送followerinfo包,带上follower自己最大的zxid
* C发现B上没有自己的事务提交记录(0X500000003),则向B发送trunc命令,让B混滚到0X500000002
* B完成混滚后,向C发送信息包,确认完成,并说明当前的zxid为0X500000002
* C向B发送diff同步命令
* B收到Diff命令后进入同步状态,并向C发送ack确认包
* C陆续把对应的差异数据和Commit提交proposal发送给B,当数据发送完成后,在发送通知包给B
* B应用用于内存的数据结构,当收到C通知已经完成同步后,B给回应ACK,并且结束同步



## 场景三

某个节点宕机时间太长,当恢复并且加入集群后,数据的事务日志文件已经生成多个,此时的minCommittedLog比节点宕机时的最大日志还要大

假设B宕机后,几天后才恢复,此时minCommittedLog为0X6000008731，而peerLastZxid为0X500000003


同步方案

* 采用全量同步(snap)
* 当leader(C)发现,B的zxid小于minCommittedLog时,向B发送snap指令
* B收到指令,进入同步阶段
* leader(C)会从内存数据库中获取全量的数据发送给B
* B收到数据处理完成后,C还会把最新的proposal(全量同步期间产生)通过增量的方式发送给B




# 广播流程

* 集群选举完成后,并且完成数据同步后,即可开始对外提供服务,接收读写请求
* 当leader接收到客户端新的事务请求后,会生成对应的事务proposal,并根据zxid的顺序向所有的follower发送提案,即:proposal
* 当follower收到leader的事务proposal时,根据接收的先后顺序处理这些proposal,即如果先后收到1,2,3条,则如果处理完了第3条,则代表1,2两条一定已经处理成功
* 当leader收到follower针对某个事务proposal过半的ack后,则发起事务提交,重新发送一个commit的proposal
* follower收到commit的proposal后,记录事务提交,并把数据更新到内存数据库
* 补充说明:
	* 由于只有过半的机器给出反馈,则可能存在某时刻某些节点数据不是最新的
	* 业务上如果需要确认读取到的数据是最新的,则可以读取之前,调用sync方法进行数据同步



