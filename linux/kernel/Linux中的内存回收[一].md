[toc]

# Linux中的内存回收[一]

涉及如下的内容：

1. 两种不同的内存
   1. file对应的mapping内存 (page cache)
   2. 匿名页内存（anonymous page)
2. 两个链表：active list ; inactive list (这里还有两个参数：**PG_referenced**和**PG_active**两个标志位)
3. LRU回收策略
4. swappiness 的值

转自：https://zhuanlan.zhihu.com/p/70964195

系统运行一段时间后，内存渐渐被分配出去，剩余的空闲内存越来越少，这时内核就要通过page reclaim机制回收一部分内存。[page cache](https://zhuanlan.zhihu.com/p/68071761)中的页面和[anonymous page](https://zhuanlan.zhihu.com/p/70964551)都是可以被回收的，如果不加选择，则刚刚discard或swap out的页面可能很快又要用到，又得重新从backing store换回来，造成page thrashing（同cache thrashing）。

那如何合理地挑选要回收的page frames呢？通常，应该选择那些最近不使用（不活跃）的页面，依据是最近不使用的页面在较短的时间内也不会被频繁使用，这就是LRU(Least Recently Used)算法的基本思想。

**太极生两仪**

为此，我们需要有timestamp来标识一个page最近被访问的时间，然而像x86这样的架构并没有从硬件上提供这种机制。

在86中，当一个page被访问后，硬件会将page对应的PTE中的Access位置1（参考[这篇文章](https://zhuanlan.zhihu.com/p/67053210)），如果完全按照LRU的思想来设计，那么此时软件需要为这个page关联一个定时器，然后将PTE中的Access位清0。在定时器timeout之前，如果该page没有再被访问到，那么就将这个page回收。然而，page frames数量庞大，维护那么多的内核定时器显然是不切实际的。

那如何实现才能保证相对公平又高效呢？Linux采用的方法是维护2个双向链表，一个是包含了最近使用页面的**active list**，另一个是包含了最近不使用页面的**inactive list**（struct page中的lru域含有指向所在链表中前后页面的指针），并且在struct page的page flags中使用了**PG_referenced**和**PG_active**两个标志位来标识页面的活跃程度。

PG_active标志位决定page在哪个链表，也就是说active list中的pages的PG_active都为1，而inactive list中的pages的PG_active都为0。PG_referenced标志位则是表明page最近是否被使用过。当一个page被访问，mark_page_accessed()会检测该page的PG_referenced位，如果PG_referenced为0，则将其置为1。

> inactive, unreferenced -> inactive, referenced
> active, unreferenced -> active, referenced

不管是active list还是inactive list，都是采用FIFO(First In First Out)的形式，新的元素从链表头部加入，中间的元素逐渐向尾端移动。在需要进行内存回收时，内核总是选择inactive list尾端的页面进行回收。

如果inactive list上PG_referenced为1的page在回收之前被再次访问到，也就是说它在inactive list中时被访问了2次，mark_page_accessed()就会调用activate_page()将其置换到active list的头部，同时将其PG_active位置1，PG_referenced位清0（可以理解为两个PG_referenced才换来一个PG_active），这个过程叫做promotion（逆袭）。

> inactive, referenced -> active, unreferenced

这3种情景的代码实现是这样的（为了演示需要在源代码的基础上做了简化和修改）：

```c
void mark_page_accessed(struct page *page)
{
	if (!PageActive(page) && PageReferenced(page)) {
	    activate_page(page);
	} 
        else if (!PageReferenced(page)) {
	    SetPageReferenced(page);
	}
}
```

如果inactive list中的page在达到链表尾端的时候PG_referenced为0（在inactive list期间没有被访问到），那还有什么可说的，不回收你回收谁。如果此时page的PG_referenced为1（在inactive list期间只被访问了一次），是否还是有豁免的机会呢？

这就依赖于其他的一些机制了，事实上，在内存回收这一块，很多时候都是empirical（经验主义）的，根据实际效果的好坏差异来选用合适的机制。Inactive list可以被视作是内核在不确定是否立刻要回收一个页面时，用于暂时保存该页面的一个场所，相当于是给予了第二次机会。

当active list中的一个page在到达链表尾端时，如果其PG_referenced位为1，则被放回链表头部，但同时其PG_referenced会被清0。如果其PG_referenced位为0，那么就会被放入inactive list的头部，这个过程叫做demotion。可见，Linux采用的这种active list和inactive list并不是严格按照时间顺序来置换page的，所以是一种伪LRU算法。

![img](https://picb.zhimg.com/80/v2-747e335e41448bd474719b7b6a7511cf_720w.jpg)

Inactive list中尾端的页面不断被释放，相当于一个消费者，active list则不断地将尾端PG_referenced为0的页面放入inactive list，相当于一个生产者。不难想象，这2个链表的锁（lru_lock）应该是高度竞争的，如果从active list向inactive list的页面转移是一个一个进行的，那对锁的争抢将会十分严重。

为了解决这个问题，内核加入了一个per-CPU的**lru cache**（用struct pagevec表示），从active list换出的页面先放入当前CPU的lru cache中，直到lru cache中已经积累了PAGEVEC_SIZE（15）个页面，再获取lru_lock，将这些页面批量放入inactive list中。

![img](https://pic2.zhimg.com/80/v2-2347157c68450e7f467edbadce62b585_720w.jpg)

当一个页面被回收后又再次访问到，将会触发page fault，需要将页面从磁盘换入。因为由demand paging或者demand alloction产生这个页面的时候已经有一次page fault了，所以这时的page fault被称为**refault**。

如果inactive list比较长，那么每个页面在被回收之前有充分的时间被再次访问，从而被promote到active list，这样可以减少刚刚被回收又发生refault的页面数量（page thrashing）。但是由于内存总量有限，inactive list较长就意味着active list相对较短，那这2个链表应该分别多长比较合适呢？

我们可以在一个页面被回收时记录一个timestamp，当这个页面refault被换入时再记录一个timestamp，如果将inactive list的长度增加，使得页面回收增长的时间超过这2个timestamp的差值时，那么这个页面就可以因为在inactive list中被再次访问，避免了被回收的命运，也减小了refault造成的开销。

可是前面也提到过，由于硬件的限制，给每个page维护一个timestamp是不现实的。还是类似的解决办法，用inactive中回收页面的个数来替代timestamp，每回收一个页面，计数器加1（相当于用counter替代了timer）。那这个counter的值保存在哪里呢？

对于属于page cache的页面，由于其被换出之前是放在radix tree/xarray中的，当页面被回收时，我们可以把它曾经所在的entry利用起来，记录一下当前的counter值，页面被换入的时候再比较一下entry中的counter值，这种entry被称为**shadow entry**。关于这个算法更详细的介绍请参考[这篇文章](https://link.zhihu.com/?target=http%3A//tinylab.org/lwn-495543/)。

对于一个基于文件的page frame，好像它既在page cache结构中，又在active/inactive list结构中？没错，放在page cache中以radix tree/xarray的方式组织，是为了方便快速查找和读写它的内容，放在active/inactive list中则是为了方便内存回收。当一个基于文件的page frame被创建时，它就已经被加入到这2个结构中了：

```c
int add_to_page_cache_lru(struct page *page, struct address_space *mapping,
			  pgoff_t offset, gfp_t gfp_mask)
{
    __add_to_page_cache_locked(page, mapping, offset,  gfp_mask, &shadow);
    lru_cache_add(page);					
}
```

这里同样是先放入lru cache，而不是直接放进active/inactive list中。

**两仪生四象**

对于anonymous pages，总是需要先写入swap area才能回收。而对于page cache，有一些可以直接discard（比如elf的text段对应的页面，data段对应的页面中clean的部分），有一些dirty的页面需要先write back同步到磁盘。

由于有flusher thread定期的write back，回收时还是dirty的page cache页面不会太多。而且，page cache中的页面有对应的文件和在文件中的位置信息，需要换入恢复的时候也更加容易。

因此，内核通常更倾向于换出page cache中的页面，只有当内存压力变得相对严重时，才会选择回收 anonymous pages。用户可以根据具体应用场景的需要，通过"/proc/sys/vm/swappiness"调节内存回收时anonymous pages和page cache的比重。

**swappiness**的值从0到100不等，默认一般是60（只是一个经验值），这个值越高，则回收的时候越优先选择anonymous pages。当swappiness等于100的时候，anonymous pages和page cache就具有相同的优先级。至于为什么不从开销最小的角度将swappiness设为0，将在后面给出答案。

```c
/*
 * With swappiness at 100, anonymous and file have the same priority.
 * This scanning priority is essentially the inverse of IO cost.
 */
anon_prio = swappiness;
file_prio = 200 - anon_prio
```

早期的Linux实现中，每个 zone 中有 active 和 inactive 两个链表，每个链表上存放的页面不区分类型。为了实现优先回收page cache，之后每个链表拆分成了LRU_ANON和LRU_FILE，因此形成了LRU_INACTIVE_ANON, LRU_ACTIVE_ANON, LRU_INACTIVE_FILE和LRU_ACTIVE_FILE四种链表，**而且改成了一个node对应一组链表（per-node），由代表node的struct pglist_data中的struct lruvec包含各个链表的头结点** 。

关于链表拆分的由来可以参考[这篇文章](https://link.zhihu.com/?target=https%3A//tinylab.org/lwn-226756/)（原作是2007年）。

```c
#define LRU_BASE 0
#define LRU_ACTIVE 1
#define LRU_FILE 2

enum lru_list {
	LRU_INACTIVE_ANON = LRU_BASE,
	LRU_ACTIVE_ANON = LRU_BASE + LRU_ACTIVE,
	LRU_INACTIVE_FILE = LRU_BASE + LRU_FILE
	LRU_ACTIVE_FILE = LRU_BASE + LRU_FILE + LRU_ACTIVE,
	LRU_UNEVICTABLE,
	NR_LRU_LISTS
};

struct lruvec {
	struct list_head lists[NR_LRU_LISTS];
        ...
}
```

这里还有一个LRU_UNEVICTABLE链表。这个链表存储了flag为PG_unevictable的页面，因为unevictable的页面是不可以回收的，扫描的时候忽略LRU_UNEVICTABLE链表，将可以减少回收时扫描页面的总体时间。关于LRU_UNEVICTABLE更详细的介绍请参考[这篇文章](https://link.zhihu.com/?target=https%3A//www.kernel.org/doc/Documentation/vm/unevictable-lru.txt)。

可以通过"/proc/zoneinfo"查看这4种链表在node的各个zone上的分布：

![img](https://picb.zhimg.com/80/v2-b8f386d20d891b12773f38840810de48_720w.jpg)

更丰富的信息则包含在"/proc/meminfo"中，比如"Dirty"表示修改后还没有write back的页面，"Writeback"表示正在执行I/O操作进行回写的页面，还有就是系统所有"active"和"inactive"的page cache和anonymous page的统计数据，详情请参考[这篇文章](https://zhuanlan.zhihu.com/p/93228929)。

![img](https://pic3.zhimg.com/80/v2-439e8b59c73414925d2d31c12d009dd2_720w.png)

如果swappiness的值为0，那么内核在启动内存回收时，将完全忽略anonymous pages，这将带来一个好处，就是内核只需要扫描page cache对应的inactive list（LRU_ACTIVE_FILE）就可以了，根本不用扫描anonymous pages对应的inactive list（LRU_ACTIVE_ANON），这样能极大的节约内存回收时花在扫描LRU链表上的时间。

但是，swappiness设置为0只适用于系统中page cache比较多的场景，如果系统中anonymous pages比page cache多很多，只回收page cache的话，可能无法满足direct relaim或者kswapd的需求。

这时本来系统中还有很多anonymous pages可供回收以释放内存，由于swappiness的限制，内核也许只有选择OOM killer了，所以用户在设置swappiness参数的时候，需要对自己的应用场景和内存的使用情况有比较深入的了解，要不然你可能会困惑：明明available的内存还很多啊，为啥老有进程被OOM kill呢。

[下文](http://zhuanlan.zhihu.com/p/72998605)将介绍内存回收的触发机制和实现过程。



**参考:**

[http://tinylab.org/lwn-495543/](https://link.zhihu.com/?target=http%3A//tinylab.org/lwn-495543/)