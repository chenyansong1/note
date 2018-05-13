# 1.创建配置类

```

@Configuration
public class QuartzConfig {

	/**
	 * 1.创建Job对象
	 */
	@Bean
	public JobDetailFactoryBean getJobDetailFactoryBean(){
		JobDetailFactoryBean factory = new JobDetailFactoryBean();
		
		//关联我们定义Job类
		factory.setJobClass(MyJob.class);
		
		return factory;
	}
	
	/**
	 * 2.创建Trigger
	 */
	/*@Bean
	public SimpleTriggerFactoryBean getSimpleTriggerFactoryBean(JobDetailFactoryBean jobDetailFactoryBean){
		SimpleTriggerFactoryBean factory = new SimpleTriggerFactoryBean();
		
		//关联JobDetail对象
		factory.setJobDetail(jobDetailFactoryBean.getObject());
		
		//重复间隔时间（毫秒为单位）
		factory.setRepeatInterval(5000);
		
		//重复次数
		factory.setRepeatCount(4);
		
		return factory;
	}*/
	
	@Bean
	public CronTriggerFactoryBean getSimpleTriggerFactoryBean(JobDetailFactoryBean jobDetailFactoryBean){
		CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
		
		//关联JobDetail对象,jobDetailFactoryBean 这是一个工厂，需要通过工厂，拿到对应的对象
		factory.setJobDetail(jobDetailFactoryBean.getObject());
		
		factory.setCronExpression("0/3 * * * * ?");
		
		return factory;
	}
	
	
	/**
	 * 3.创建Scheduler对象
	 */
	@Bean
	public SchedulerFactoryBean getSchedulerFactoryBean(CronTriggerFactoryBean triggerFactoryBean){
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		
		//关联trigger
		factory.setTriggers(triggerFactoryBean.getObject());
				
		return factory;
	}
	
}

```

# 2.导入事物的pom依赖


```
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-tx</artifactId>
</dependency>

```

# 3.在启动类中添加调度启动


```

@SpringBootApplication
@EnableScheduling //启动调度
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

```


# 解决Job对象中无法注入Spring容器对象的问题

有时我们需要在job中注入service的对象，这样我们就能在Job中使用service的Bean，如我们像这样：

```
public class MyJob implements Job{
	
	@Resource // 我们需要注入一个Service
	private EmpService empService;

	//任务被触发时被执行
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("任务被执行:"+new Date());
		
	   // 调用service的方法
		empService.save();
	}

}
```

但是此时会报空指针异常，_因为EmpService没有注入到MyJob这个类中_


我们查看spring的源码知道，在new我们定义的MyJob的时候，会执行如下的代码


![](/Users/chenyansong/Documents/note/images/spring-boot/schedule3.png)


MyJob并不在spring的环境中，所以他里面注入的对象，也就没有随着MyJob的实例化而创建，所以会报空指针异常

所以我们只需要重写AdaptableJobFactory中的createJobInstance方法即可


```

@Component("jobFactory")
public class MyJobFactory extends AdaptableJobFactory{

	@Resource
	private AutowireCapableBeanFactory factory;
	
	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		
		//1.创建Job对象
		Object jobInstance = super.createJobInstance(bundle);
		
		//2.把JobInstance对象放入Spring容器,这样jobInstance对象中的依赖Bean也会注入，即：ServiceBean也会实例化
		factory.autowireBean(jobInstance);
		
		return jobInstance;
	}

}

```


此时需要在配置类中重写下面的方法

```

@Configuration
public class QuartzConfig {

	/**
	 * 1.创建Job对象
	 */
	@Bean
	public JobDetailFactoryBean getJobDetailFactoryBean(){
		JobDetailFactoryBean factory = new JobDetailFactoryBean();
		
		//关联我们定义Job类
		factory.setJobClass(MyJob.class);
		
		return factory;
	}
	
	/**
	 * 2.创建Trigger
	 */
	/*@Bean
	public SimpleTriggerFactoryBean getSimpleTriggerFactoryBean(JobDetailFactoryBean jobDetailFactoryBean){
		SimpleTriggerFactoryBean factory = new SimpleTriggerFactoryBean();
		
		//关联JobDetail对象
		factory.setJobDetail(jobDetailFactoryBean.getObject());
		
		//重复间隔时间（毫秒为单位）
		factory.setRepeatInterval(5000);
		
		//重复次数
		factory.setRepeatCount(4);
		
		return factory;
	}*/
	
	@Bean
	public CronTriggerFactoryBean getSimpleTriggerFactoryBean(JobDetailFactoryBean jobDetailFactoryBean){
		CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
		
		//关联JobDetail对象,jobDetailFactoryBean 这是一个工厂，需要通过工厂，拿到对应的对象
		factory.setJobDetail(jobDetailFactoryBean.getObject());
		
		factory.setCronExpression("0/3 * * * * ?");
		
		return factory;
	}
	
	
	/**
	 * 3.创建Scheduler对象
	 */
	@Bean
	public SchedulerFactoryBean getSchedulerFactoryBean(CronTriggerFactoryBean triggerFactoryBean,JobFactory jobFactory){
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		
		//关联trigger
		factory.setTriggers(triggerFactoryBean.getObject());
		
		//重新设置JobFactory，这样我们Job在实例化的时候，就会将Job中的ServiceBean注入
		factory.setJobFactory(jobFactory);
		
		return factory;
	}
	
}

```