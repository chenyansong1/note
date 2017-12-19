---
title: 多job串联
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---




一个稍复杂点的处理逻辑往往需要多个mapreduce程序串联处理，多job的串联可以借助mapreduce框架的JobControl实现

```
//.........所有的job设置完毕，然后进行下面的操作

      ControlledJob cJob1 = new ControlledJob(job1.getConfiguration());
        ControlledJob cJob2 = new ControlledJob(job2.getConfiguration());
        ControlledJob cJob3 = new ControlledJob(job3.getConfiguration());
 
        cJob1.setJob(job1);
        cJob2.setJob(job2);
        cJob3.setJob(job3);
 
        // 设置作业依赖关系
        cJob2.addDependingJob(cJob1);
        cJob3.addDependingJob(cJob2);
 
        JobControl jobControl = new JobControl("RecommendationJob");
        jobControl.addJob(cJob1);
        jobControl.addJob(cJob2);
        jobControl.addJob(cJob3);
 
 
        // 新建一个线程来运行已加入JobControl中的作业，开始进程并等待结束
        Thread jobControlThread = new Thread(jobControl);
        jobControlThread.start();
        while (!jobControl.allFinished()) {
            Thread.sleep(500);
        }
        jobControl.stop();
 
        return 0;





```


在实际应用中，并不建议这样做，因为在代码中将3个job的关系写死了，那么想改就很难了,建议的做法是：将3个job封装成为单独的jar文件，然后使用shell去启动job，shell可以控制每个job的启动顺序，来实现job之间的关联性，这样更加的灵活。

