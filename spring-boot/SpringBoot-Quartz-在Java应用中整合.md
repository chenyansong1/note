![](/Users/chenyansong/Documents/note/images/spring-boot/schedule1.png)


# 1.Quartz应用思路
* Job - 任务 - 你要干什么?* Trigger- 触发器- 你什么时候干? 
* Scheduler- 任务调度- 你什么时候需要干什么?


# 2.导入quartz 坐标

```
<!-- Quartz支持 -->
<dependency>
	<groupId>org.quartz-scheduler</groupId>
	<artifactId>quartz</artifactId>
	<version>2.2.1</version>
	<exclusions>
		<exclusion>
			<artifactId>slf4j-api</artifactId>
			<groupId>org.slf4j</groupId>
		</exclusion>
	</exclusions>
</dependency>

```


# 3.定义Job任务类

```

/**
 * 自定义Job类
 * @author lenovo
 *
 */
public class MyJob implements Job{
	
	@Resource
	private EmpService empService;

	//任务被触发时被执行
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("任务被执行:"+new Date());
		empService.save();
	}

}

```


# 4.编写代码创建任务调度程序

```

public class QuartzMain {

	public static void main(String[] args) throws Exception {
		//1.创建Job对象 - 你需要干什么？
		JobDetail job = JobBuilder.newJob(MyJob.class).build();
		
		//2.创建Trigger对象 - 你什么时候干？
		/**
		 * 简单trigger：简单地重复
		 * cron trigger：按照cron表达式
		 */
		/*Trigger trigger = TriggerBuilder
							.newTrigger()
							.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever())
							.build();*/
		
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withSchedule(CronScheduleBuilder.cronSchedule("0/3 * * * * ?"))
				.build();
		
		//3.创建Scheduler
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.scheduleJob(job, trigger);
		
		//4.启动Scheduler
		scheduler.start();
	}
}
```