---
title: mapreduce的Mapper类结构
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---





```
public class Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {

  public abstract class Context implements MapContext<KEYIN,VALUEIN,KEYOUT,VALUEOUT> {
  }
  
  protected void setup(Context context) throws IOException, InterruptedException {
  }

  protected void map(KEYIN key, VALUEIN value, Context context) throws IOException, InterruptedException {
    context.write((KEYOUT) key, (VALUEOUT) value);//默认是有一个map方法的,所以如果我们不写,还是有map的存在的
  }

  protected void cleanup(Context context) throws IOException, InterruptedException {
  }
  
    /*
    会启动一个线程去执行
   */
  public void run(Context context) throws IOException, InterruptedException {
    setup(context);//map中最开始会执行一次
    try {
      while (context.nextKeyValue()) {//context中对应的keyValue，就去调用map方法
        map(context.getCurrentKey(), context.getCurrentValue(), context);
      }
    } finally {
      cleanup(context);//最终会执行一次
    }
  }
}  

```
