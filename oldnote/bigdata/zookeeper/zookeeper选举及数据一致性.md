---
title: zookeeper选举及数据一致性
categories: hadoop   
tags: [zookeeper]
---



# zab协议

ZooKeeper Atomic Broadcast 即ZooKeeper原子消息广播协议，简称为ZAB
* 选举过程需要依赖此协议
* 数据写入过程也需要此协议
* ab的核心是定义了那些会改变zk服务器数据状态的事务请求的处理方式


所有事务请求必须由一个全局唯一的服务器来协调处理，这样的服务器被称为Leader服务器，而余下的其它服务器则成ОFollower服务器。 Leader服务器负责将一个客户端事务请求转换成一个proposal(提议),并将该Proposal分发给集群中所有的Follower服务器。之后Leader服务器需要等待所有Follower服务器的反馈，一旦超过半数的Follower服务器进行了正确的反馈后，那ТLeader就会再次向所有的Follower服务器分发Commit消息，要求其将前一个Proposal进行提交


* zab协议的三阶段:
	* 发现ͧDiscovery，即选举Leader过程
	* 同步(Synchronization),选举出新的Leader后,Follwer或者Observer从Leader同步最新数据
	* 广播,同步完成之后,就可以接收客户端新的事物请求,并进行广播,实现数据在集群节点的副本存储


# 服务器角色
* Leader
	* 事物请求的唯一调度和处理者,保证集群事物处理的顺序性
	* 集群内部各服务器的调度者
* Follower
	* 处理客户端非事物请求,转发事物请求给Leader服务器
	* 参与事物请求Proposal的投票
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



