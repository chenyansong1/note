[toc]

转自：https://www.kernel.org/doc/html/v4.14/translations/zh_CN/coding-style.html



这里说明的一点是：kernel中如果使用的如下的定义，那么是使用的是typedef

```c
 typedef struct wait_queue *wait_queue_head_t;


typedef struct page {
    struct list_head list;
    struct address_space *mapping;
    unsigned long index;
    struct page *next_hash;
    atomic_t count;
    unsigned long flags;    /* atomic flags, some possibly updated asynchronously */
    struct list_head lru;
    unsigned long age;
    wait_queue_head_t wait;
    struct page **pprev_hash;
    struct buffer_head * buffers;
    void *virtual; /* non-NULL if kmapped */
    struct zone_struct *zone;
} mem_map_t;
```

> _t是对typedef的标注











