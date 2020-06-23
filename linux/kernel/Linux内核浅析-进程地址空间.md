[toc]
Linux内核浅析-进程地址空间

转自：https://zhuanlan.zhihu.com/p/81850840

本文介绍下linux如何管理内存。作为架构师，我去做一个系统时，通常从两个方面入手：1）了解上层业务和需求。2）熟悉下层可以使用的工具和能力。本质上，做任何系统，或者做任何事情吧，纵向上来说，我们都是做上层和下层之间的枢纽，横向上来说，我们做的是整个任务链上的一个节点，也是前、后节点之间的传动齿轮。linux就是这样，是上层业务程序和下层体系结构之间的枢纽，其封装了底层体系结构的复杂性，以更方便的操作界面供上层操作，用最近流行的一个词语，”认知折叠“，那linux就是折叠了体系结构的认知，让上层不必关注硬件体系结构，而专注于自身的业务。

## 内存管理的背景

**上层业务需求**

1、save and get：程序将运行所需要的**指令**和**数据**进行暂存（不需要持久化），并在需要时能够准确索引之前暂存的任何值，并load入cpu寄存器进行运算。

2、安全性：要做到进程之间的隔离，用户态、内核态的隔离，保证系统级别的安全，即使一个进程出现内存溢出、寻址越界等情况，不能影响系统和其他进程的运行。

3、高效：meta信息要少，内存操作要快。

**底层提供的能力**

最底层的能力就是内存条，能够根据索引存储数据和获取数据，但是x86体系结构封装了cpu和内存的交互过程，通过下列x86汇编完成cpu和内存交互。

```text
mov BX, 1000H  // 将立即数送入BX寄存器
mov DS  BX     // 将BX的值送入DS，作为段地址（后面会详细将）。DS不允许直接通过立即数赋
               // 值，所以通过BX中转一下
mov al[0]      // 将段地址为1000H，段偏移地址为0的内存数据加载到eax寄存器的低位al。[]中
               // 的值是段偏移地址，al是目标寄存器。
```

可以看到，x86提供了基于（段地址 + 段偏移）进行存取的能力，x86内部会将(段地址+段偏移)转换为物理地址后进行存储，但地址转换时需要依赖于页表机制，页表的内容由linux写入，整个过程后面会详细聊到（https://zhuanlan.zhihu.com/p/82406447），此处只是了解下底层能力。

可以看到，上层需要如此丰满，底层能力如此露骨，我们通常面对的就是这种情况，但这种情况下恰恰可以大有作为。

## Linux内存管理方案

![img](https://pic4.zhimg.com/80/v2-e940ca1193cc2d0b6b565681388bb2ab_720w.jpg)

1）管理的粒度：linux将内存分页，每页默认大小为4k。这里有个trade off的问题，如果粒度小，比如1 byte，则可以提升空间利用率，但是管理的meta信息就会很多，如果分配较大空间时，耗时就会更长；如果粒度大，则meta信息少，分配大内存时耗时少，但分配小块内存时会有浪费。所以一般在线系统或者mysql库，使用默认页大小（此处不是只mysql的页，而是mysql使用的linux的页），olap系统或者离线存储系统一般使用大页，linux提供配置参数可以配置大页。

2）进程地址空间：每个进程独有的，用字段task_struct -> mm_struct表示，其也叫线性地址，其转换关系：线性地址 -> 逻辑地址 -> 物理地址。

- 解耦：因为刚才提到的都是x86支持的方式，还是arm等体系结果需要支持，所以linux通过进程地址空间这个entity进行解耦，所以内存的操作都在用户态完成，真正分配内存时，通过缺页异常搞定。
- 隔离：由于进程地址空间按进程单位进行隔离，保证进程访问时相互隔离。同时空间内部也分为kernel space和user space，保证用户态和内核态的访问的内存相互隔离。同时采用分段机制，将指令、不同类型的数据分开存储，以支持进程运行模型。

> text segment：存储代码指令的区域
> data segment：存储已初始化的全局或静态变量
> bss segment：存储未初始化的全局或静态变量
> heap：堆，用于动态开辟内存空间，brk或malloc开辟的空间
> memory mapping space：mmap系统调用使用的空间，通常用于文件映射到内存或匿名映射（开辟大块空间），当malloc大于128k时（此处依赖于glibc的配置），也使用该区域。在进程创建时，会将程序用到的平台、动态链接库加载到该区域
> stack：进程运行的栈

- 空间利用率： 由于32位只支持4G物理内存寻址，一种方式N个进程平分4G内存，这是最简单的方式，但有个问题，有些进程占用内存，但一直在sleep，相当于很浪费。另一种方式就是把内存空间给最需要的进程，把sleep进程的内存swap到硬盘，需要的时候再swap进内存。此时就有一种极端情况，就是一个进程需要独占4G内存，linux显然选择第二种利用率更高的方式，所以进程地址空间能映射4G的物理内存。

3）逻辑地址：逻辑地址 = 段地址 + 段偏移，段地址和段偏移的值由linux进行提供。

4）页表：保存逻辑地址到物理地址的映射，其数据由linux初始化，并将热页表项加载TLB快表进行缓存，加快转换速度。然后x86体系的硬件依赖页表做逻辑地址 -> 物理地址的转换。

5）伙伴系统：管理物理内存的分配，其在缺页中断中被调用，仅负责更改页表和meta（strcut page）。当逻辑地址和物理地址映射上后，指令使用物理地址写入内存设备。

## **进程地址空间管理**

```c
struct mm_struct {
    struct vm_area_struct * mmap;  //指向虚拟区间(VMA)的链表
    struct rb_root mm_rb;          //指向线性区对象红黑树的根 
    pgd_t * pgd;                   //指向页全局目录 
    unsigned long mmap_base;	   //表示mmap区域的起始位置 
    unsigned long total_vm;	   //总共映射的页面数，包括映射到内存中和已经换出到银盘的
    unsigned long locked_vm;	   //表示不能swap到硬盘的页数
    unsigned long pinned_vm;	   //表示不能换出，不能移动的页数
    unsigned long data_vm;	   //存储数剧占的总页数
    unsigned long exec_vm;	   //存储指令占的总页数
    unsigned long stack_vm;	   //栈所占的总页数
    // text、data segment的开始和结束地址
    unsigned long start_code, end_code, start_data, end_data;
    // 堆的开始、当前位置。栈的起始位置，栈的当前地址在esp寄存器中
    unsigned long start_brk, brk, start_stack;
    // 命令行参数列表、环境变量的起始、结束地址，其都位于栈的高地址
    unsigned long arg_start, arg_end, env_start, env_end;
}
```

task_struct -> mm_struct是对进程地址空间描述的结构体，主要包含其统计信息和各个segment的起始、结束地址，几个变量如下图右侧的标注。

![img](https://pic2.zhimg.com/80/v2-c355ecf2f4c0ae01806075ae7f032799_720w.jpg)

进程地址空间的地址是向上增长的。可以看到有几个radom offset，其在段与段之间缝隙，防止固定的内存布局被黑客黑掉。

vm_area_struct是描述每个段具体信息结构体，其实一个单链表，通过task_struct- > mm_struct -> mmap表示，由于每次分配内存时会要到vm_area_struct，需要快速寻找，所以task_struct- > mm_struct -> mm_rb是根据vm_area_struct -> vm_start字段，构建vm_area_struct的一棵红黑树。

```c
struct vm_area_struct {
	/* The first cache line has the info for VMA tree walking. */
	unsigned long vm_start;		/* Our start address within vm_mm. */
	unsigned long vm_end;		/* The first byte after our end address within vm_mm. */
	/* linked list of VM areas per task, sorted by address */
	struct vm_area_struct *vm_next, *vm_prev;
	struct rb_node vm_rb;
	struct mm_struct *vm_mm;	/* The address space we belong to. */
	struct list_head anon_vma_chain; /* Serialized by mmap_sem &
					  * page_table_lock */
	struct anon_vma *anon_vma;	/* Serialized by page_table_lock */
	/* Function pointers to deal with this struct. */
	const struct vm_operations_struct *vm_ops;
	struct file * vm_file;		/* File we map to (can be NULL). */
	void * vm_private_data;		/* was vm_pte (shared mem) */
} __randomize_layout;
```

每个字段的具体含义可以参见注释。其中anon_vma是匿名映射，即分配大块内存，vm_file即是指向文件映射映射的文件。vm_ops即是这段内存上对应的操作。

mm_struct和vm_area_struct的初始化是在load_elf_binary的时候初始化，主要做的几个事情如下：

1）调用 setup_new_exec，设置内存映射区 mmap_base。

2）调用setup_arg_pages，设置栈的vm_area_struct和current -> mm -> start_stack。

3）调用elf_map，将elf文件中的代码映射到对应区域中。

4）set_brk，设置堆的vm_area_struct，current -> mm -> start_brk = current -> mm -> brk，此时栈是空的。

5）load_elf_interp将依赖的so加载到mmap区域。

load_elf_binary完成后，线性空间的布局就基本如上图，当进程运行时会修改栈空间的大小，通过malloc申请空间时会改变heap、mmmap空间的大小。

## brk & mmap

分配内存常使用malloc，这是glibc提供的方法，其内部也有block的管理。但是底层都依赖于linux提供的系统调用：brk和mmap。若malloc小于128k，则使用brk，大于则使用mmap，可通过M_MMAP_THRESHOLD修改128k这个边界值。如下图，A = malloc(30K)，B = malloc(40K)，C = malloc(200K)，D=malloc(100K)。

![img](https://pic3.zhimg.com/80/v2-649706e85c57a7378addd867711d7362_720w.jpg)

**brk**

其通过系统调用设置current -> mm -> brk指针，在heap空间来分配和回收内存空间。

```text
SYSCALL_DEFINE1(brk, unsigned long, brk)
{
	unsigned long retval;
	unsigned long newbrk, oldbrk;
	struct mm_struct *mm = current->mm;
	struct vm_area_struct *next;
        // 1、如果新的brk和当前mm->brk按页对齐后相等，则说明不需要跨页分配，则直接设置当前brk即可
	newbrk = PAGE_ALIGN(brk);
	oldbrk = PAGE_ALIGN(mm->brk);
	if (oldbrk == newbrk)
		goto set_brk;


	// 2、如果新的brk小于mm->brk，说明需要进行线性空间的回收
	if (brk <= mm->brk) {
		if (!do_munmap(mm, newbrk, oldbrk-newbrk, &uf))
			goto set_brk;
		goto out;
	}
 	// 3、找到next vma，vm_start_gap可以理解返回的是next.vm_start，然后比较下一个vma和当前vma是否
        // 能容纳新申请的空间。这里有两个注意点:
        // 1）oldbrk是按页对齐后的brk地址，此时通过fina_vma找到的是第一个满足vma.vm_end > oldbrk的  
        // vma，所以是下一个vma，用next指针表示
        // 2）brk的入参是一个addr，所以只能查看当前vma和下一个vma之间是否足够分配，不能线性
        // 查找，这一点不同于mmap方式。
	next = find_vma(mm, oldbrk);
	if (next && newbrk + PAGE_SIZE > vm_start_gap(next))
		goto out;

        // 4、对于brk，此处仅设置vm_area_struct的vm_end和其他相关指针，不会新分配vma
	if (do_brk(oldbrk, newbrk-oldbrk, &uf) < 0)
		goto out;

// 5、设置brk
set_brk:
	mm->brk = brk;
	return brk;
out:
	retval = mm->brk;
	return retval
```

**mmap**

通过遍历mmap区域，找到大小合适的区域进行文件映射或匿名映射，其入口是SYSCALL_DEFINES6，主要逻辑在do_mmap中。

```c
unsigned long do_mmap(struct file *file, unsigned long addr,
                        unsigned long len, unsigned long prot,
                        unsigned long flags, vm_flags_t vm_flags,
                        unsigned long pgoff, unsigned long *populate,
                        struct list_head *uf) {
        struct mm_struct *mm = current->mm;	
        int pkey = 0;

        *populate = 0;
        if (!len)
                return -EINVAL;
 .......

	/* pang */
        len = PAGE_ALIGN(len);
        if (!len)
                return -ENOMEM;

	// 判断该进程的地址空间的虚拟区间数量是否超过了限制
        if (mm->map_count > sysctl_max_map_count)
                return -ENOMEM;


    	// 从mmap区域获取未被映射且length合适的vma addr
        addr = get_unmapped_area(file, addr, len, pgoff, flags);
 .......
        /* file指针不为nullptr, 即从文件到虚拟空间的映射 */
    	if (file) { 
 .......
        } else {
                switch (flags & MAP_TYPE) {
                case MAP_SHARED:
 .......               
                case MAP_PRIVATE:
 .......
                default:
                        return -EINVAL;
                }
        }

        // 映射到vm_area_struct
        addr = mmap_region(file, addr, len, vm_flags, pgoff, uf);
        if (!IS_ERR_VALUE(addr) &&
            ((vm_flags & VM_LOCKED) ||
             (flags & (MAP_POPULATE | MAP_NONBLOCK)) == MAP_POPULATE))
                *populate = len;
        return addr;
```

get_unmapped_area：从mmap区域中找到未被映射，且length满足要求的vma的起始addr。

- 若addr != 0，则从指定addr查找。先调用fina_vma，找到vma，使其满足vma.vm_end > addr同时还需要满足addr + len < vma.vm.start，则返回该addr。
- 若addr == 0或步骤1找不到符合要求的addr，则从mm->free_area_cache从新开始全局搜索，如果还搜索不到，就返回错误。mm->free_area_cache在初始化时被设置为用户空间的三分之一（1G的位置，1G以下是为text、data、bss保留）。

mmap_region：根据addr，映射vma，可能是和原有的vma合并，也可能是重新创建vma，逻辑在vma_merge中，具体可以参见[http://edsionte.com/techblog/archives/3586](https://link.zhihu.com/?target=http%3A//edsionte.com/techblog/archives/3586)。

## 线性地址与逻辑地址的映射


逻辑地址 = 段地址 + 段偏移，从上图可以看出线性地址 = 段偏移，在linux中，段地址都被初始化为0（也可参见https://zhuanlan.zhihu.com/p/73937048）。我理解原因有两个：

1）不是所有体系结构都有段地址的概念，比如arm，linux为了支持多个体系架构，所以做了兼容设计。

2）避免了一次地址转换，使得线性地址直接对应段偏移地址，减少了计算复杂度。

那为何不直接干掉段地址，linux干嘛还初始化为0？因为x86体系要求啊，x86会通过mmu将段地址 + 段偏移 -> 物理地址，如果linux不设置段地址，x86就不work了，就没法转换为物理地址，这个后面一篇文章会详细讲。

## 内核地址空间

kernel代码运行需要使用的内存地址，比如创建task_struct描述符，内核代码运行栈等等。

![img](https://pic2.zhimg.com/80/v2-9a47d270a5abab1115bb02cae9739769_720w.jpg)

**直接映射区**：也叫高端映射区，其和物理内存一一对应，这并不是说内核可以直接使用物理地址，而是这段区域和物理地址的映射关系是一一对应的。虚拟地址空间的3G + 896M直接映射物理地址的低896M。

**内核动态映射区**：用户态malloc会在堆分配，那内核使用vmalloc函数分配的空间就在该区域。该区域和用户区域的映射规则一样。比如物理内存896M~2G被用户态使用，那内核态就需要映射2G以上的物理内存，但是内核态的线性地址只有1G，此时就不能用直接映射了，该区域的映射可以映射到任何物理内存。

**永久映射区**：分配alloc_pages，用管理物理内存。

很多人到这里有疑问吧，为什么要有高端映射？完全统一为动态映射，用户态和内核态保持一致不就ok？内核需要保证足够的物理内存来运行，如果这段区域不连续的话，不方便统计，每次用户态分配内存时是不是都要检查？那干脆加一个直接映射区，同时让用户态无法映射到该物理区域就ok了。