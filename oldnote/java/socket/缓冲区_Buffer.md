---
title: 缓冲区-Buffer
categories: socket   
tags: [socket,NIO]
---


在NIO中，不得不提到的是关于抽象的数据容器ByteBuffer，ByteBuffer允许<font color=red>相同的数据在不同ByteBuffer之间共享而不需要再拷贝一次来处理(参见：《传统的IO原理一文》)</font>

# 1.缓冲区的属性 

所有的缓冲区都具有四个属性来提供关于其所包含的数据元素的信息，他们是：
1. 容量（Capacity）
    缓冲区能够容纳的数据元素的最大数量，这一容量在缓冲创建时被设定，并且永远不能被改变
2. 上界（Limit）
    缓冲区的第一个不能被读或写的元素，或者说，缓冲区中现存元素的计数
3. 位置（Position）
    下一个要被读或写的元素的索引，位置会自动由相应的get()和put()函数更新
4. 标记（Mark）
    一个备忘位置，调用mark()来设定mark = position，调用reset()设定position = mark，标记前是未定义的（underfined）

# 2.新建的缓冲区示意图

![](http://ols7leonh.bkt.clouddn.com//assert/img/java/socket/NIO_buffer_1.png)

位置被设为 0，而且容量和上界被设为 10，刚好经过缓冲区能够容纳的最后一个字节。标记最初未定义。容量是固定的，但另外的三个属性可以在使用缓冲区时改变。


# 3.缓冲区API
```
package java.nio;
public abstract class Buffer {
    public final int capacity( )
    public final int position( )
    public final Buffer position (int newPositio
    public final int limit( )
    public final Buffer limit (int newLimit)
    public final Buffer mark( )
    public final Buffer reset( )
    public final Buffer clear( )
    public final Buffer flip( )
    public final Buffer rewind( )
    public final int remaining( )
    public final boolean hasRemaining( )
    public abstract boolean isReadOnly( );
}

```

# 4.缓冲区存取
上文所列出的的 Buffer API 并没有包括 get()或 put()方法。每一个 Buffer 类都有这两个方法，但它们所采用的参数类型，以及它们返回的数据类型，对每个子类来说都是唯一
的，所以它们不能在顶层 Buffer 类中被抽象地声明。它们的定义必须被特定类型的子类所遵从。如：ByteBuffer
```
public abstract class ByteBuffer  extends Buffer implements Comparable{
   // This is a partial API listing
   public abstract byte get( );
   public abstract byte get (int index);
   public abstract ByteBuffer put (byte b);
   public abstract ByteBuffer put (int index, byte b);
}

```

# 5.填充到缓冲区
填充
```
buffer.put((byte)'H').put((byte)'e').put((byte)'l').put((byte)'l').put((byte)'o');
```
![](http://ols7leonh.bkt.clouddn.com//assert/img/java/socket/NIO_buffer_2.png)

修改
```
buffer.put(0,(byte)'M').put((byte)'w');
```
![](http://ols7leonh.bkt.clouddn.com//assert/img/java/socket/NIO_buffer_3.png)


# 6.翻转
&emsp;我们已经写满了缓冲区，现在我们必须准备将其清空。我们想把这个缓冲区传递给一个通道，以使内容能被全部写出。但如果通道现在在缓冲区上执行 get()，那么它将从我们刚刚
插入的有用数据之外取出未定义数据。如果我们将位置值重新设为 0，通道就会从正确位置开始获取，但是它是怎样知道何时到达我们所插入数据末端的呢？这就是上界属性被引入的目
的。上界属性指明了缓冲区有效内容的末端。我们需要将上界属性设置为当前位置，然后将位置重置为 0。我们可以人工用下面的代码实现：
```
buffer.limit(buffer.position()).position(0); //将limit设置为当前position，然后将position=0
```
Buffer.flip(); 方法就是实现了上面的人工代码

![](http://ols7leonh.bkt.clouddn.com//assert/img/java/socket/NIO_buffer_4.png)

# 7.释放缓冲区
```
buffer.clear( );
```
 此时position=0；limit=capacity


# 8.创建缓冲区

```
public abstract class CharBuffer extends Buffer implements CharSequence, Comparable{
   // This is a partial API listing
   public static CharBuffer allocate (int capacity)
   public static CharBuffer wrap (char [] array)
   public static CharBuffer wrap (char [] array, int offset,  int length)
   public final boolean hasArray( )
   public final char [] array( )
   public final int arrayOffset( )
}

```

要分配一个容量为 100 个 char 变量的 Charbuffer:
```
CharBuffer charBuffer = CharBuffer.allocate (100);
#这段代码隐含地从堆空间中分配了一个 char 型数组作为备份存储器来储存 100 个 char变量
```


提供您自己的数组用做缓冲区的备份存储器，请调用 wrap()方法
```
char [] myArray = new char [100];
CharBuffer charbuffer = CharBuffer.wrap (myArray);

CharBuffer charbuffer = CharBuffer.wrap (myArray, 12, 42);
/*
创建了一个 position 值为 12， limit 值为 54，容量为 myArray.length 的缓冲区
这个方法并不像您可能认为的那样，创建了一个只占用了一个数组子集的缓冲区。这个缓冲区可以存取这个数组的全部范围； offset 和 length 参数只是设置了初始的状态。调用使
用上面代码中的方法创建的缓冲区中的 clear()方法，然后对其进行填充，直到超过上界值，这将会重写数组中的所有元素。
*/

```


# 9.Example
```
package cn.itcast.test;
import java.nio.IntBuffer;
public class NIOBuffer {
    public static void main(String[] args) {
        IntBuffer buffer = IntBuffer.allocate(10);
        int[] array = new int[]{1,3,5};
        
        //使用数组来创建一个缓冲区视图【1,3，5】
        buffer = buffer.wrap(array);
        
        //利用数组的某一个区间来创建视图
//        buffer = buffer.wrap(array,0,2);
        
        //对数据缓冲区某一个位置上面的元素进行修改
        buffer.put(0,7); //index,value
        
        System.out.println("缓冲区中的数据如下：");
        //遍历缓冲区中的数据
        for(int i=0;i<buffer.limit()-1;i++){
            System.out.println(buffer.get()+"\t");
        }
        
        //验证缓冲区视图和数组中的元素是对应的
        System.out.println("数组中的数据如下：");
        for(int a: array){
            System.out.println(a+"\t");
        }
        
        //复制一个新的缓冲区
        IntBuffer duplicate = buffer.duplicate();
        duplicate.clear();
        System.out.println(duplicate);
        duplicate.put(0,11);
        for(int i=0;i<duplicate.limit();i++){
            System.out.println("duplicate:"+duplicate.get()+"\t");
        }
        
        buffer.clear();
        for(int i=0;i<buffer.limit();i++){
            System.out.println("buffer:"+buffer.get()+"\t");
        }
        
        for(int a: array){
            System.out.println("array:"+a+"\t");
        }
        
        //对缓冲区中的内容进行反转
//        System.out.println("flip前："+buffer);//flip前：java.nio.HeapIntBuffer[pos=3 lim=3 cap=3]
//        buffer.flip();// limit=pos; pos=0
//        System.out.println("flip后："+buffer);//flip后：java.nio.HeapIntBuffer[pos=0 lim=3 cap=3]
        
        //完全清零（当limit和capacity一样的时候，是和flip函数是一样的）
        System.out.println("clear前："+buffer);//flip前：java.nio.HeapIntBuffer[pos=3 lim=3 cap=3]
        buffer.clear();//limit=capacity; pos=0
        System.out.println("clear后："+buffer);//flip后：java.nio.HeapIntBuffer[pos=0 lim=3 cap=3]
        
    }
}

```
























