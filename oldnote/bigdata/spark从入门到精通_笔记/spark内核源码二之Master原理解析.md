---
title: spark内核源码二之Master原理解析
categories: spark  
tags: [spark]
---


# master主备切换机制


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master主备切换机制.png)


# master的注册机制

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/master的注册机制.png)



# 改变状态机制源码分析

DriverStateChanged

```

  override def receive: PartialFunction[Any, Unit] = {
	///.....
	case DriverStateChanged(driverId, state, exception) => {
      state match {
		//如果Driver的状态是错误,完成,被杀掉,失败,那么就移除Driver
        case DriverState.ERROR | DriverState.FINISHED | DriverState.KILLED | DriverState.FAILED =>
          removeDriver(driverId, state, exception)
        case _ =>
          throw new Exception(s"Received unexpected state update for driver $driverId: $state")
      }
    }
	//...
}







private def removeDriver(
    driverId: String,
    finalState: DriverState,
    exception: Option[Exception]) {
	//找到driverId对应的Driver
  drivers.find(d => d.id == driverId) match {
    case Some(driver) =>
      logInfo(s"Removing driver: $driverId")
	  //将driver从内存缓存中移除
      drivers -= driver
      if (completedDrivers.size >= RETAINED_DRIVERS) {
        val toRemove = math.max(RETAINED_DRIVERS / 10, 1)
        completedDrivers.trimStart(toRemove)
      }
	  
	  //向completedDrivers中加入driver
      completedDrivers += driver
	  
	  //使用持久化引擎去除driver的持久化信息(如:假设使用的引擎是zookeeper,那么是移除zk上的driver节点)
      persistenceEngine.removeDriver(driver)
	  
	  //设置driver的state,exception
      driver.state = finalState
      driver.exception = exception
	  
	  //将driver所在的Worker中的driver移除
      driver.worker.foreach(w => w.removeDriver(driver))
	  
	  //同样调用schedule方法(当有资源改变的时候会调用)
      schedule()
    case None =>
      logWarning(s"Asked to remove unknown driver: $driverId")
  }
}



```


ExecutorStateChanged

```




override def receive: PartialFunction[Any, Unit] = {
 
	//....
    case ExecutorStateChanged(appId, execId, state, message, exitStatus) => {
	  //找到Executor对应的App,然后反过来通过app内部的Executor缓存获取Executor信息
      val execOption = idToApp.get(appId).flatMap(app => app.executors.get(execId))
      execOption match {
		//如果找到了Executor
        case Some(exec) => {
          val appInfo = idToApp(appId)
          val oldState = exec.state
		  //设置Executor的当前状态
          exec.state = state

          if (state == ExecutorState.RUNNING) {
            assert(oldState == ExecutorState.LAUNCHING,
              s"executor $execId state transfer from $oldState to RUNNING is illegal")
            appInfo.resetRetryCount()
          }
		  
		  //向driver同步发送ExecutorUpdated消息(在AppClient.receive方法中有ExecutorUpdated对应接收消息)
          exec.application.driver.send(ExecutorUpdated(execId, state, message, exitStatus))
		
		  //判断如果Executor完成了
          if (ExecutorState.isFinished(state)) {
            // Remove this executor from the worker and app
            logInfo(s"Removing executor ${exec.fullId} because it is $state")
            // If an application has already finished, preserve its
            // state to display its information properly on the UI
			//从app的缓冲中移除executor
            if (!appInfo.isFinished) {
              appInfo.removeExecutor(exec)
            }
			//从运行worker的缓存中移除executor
            exec.worker.removeExecutor(exec)

            val normalExit = exitStatus == Some(0)
            // 判断,如果Executor的退出状态是非正常的
            if (!normalExit) {
			  //判断Application当前的重试次数,是否达到了最大值
              if (appInfo.incrementRetryCount() < ApplicationState.MAX_NUM_RETRY) {
				//重新进行调度
                schedule()
              } else {
				//否则,那么就进行removeApplication
				//也就是说,Executor反复调度都失败的话,那么就认为Application也失败了,所以移除Application
                val execs = appInfo.executors.values
                if (!execs.exists(_.state == ExecutorState.RUNNING)) {
                  logError(s"Application ${appInfo.desc.name} with ID ${appInfo.id} failed " +
                    s"${appInfo.retryCount} times; removing it")
                  removeApplication(appInfo, ApplicationState.FAILED)
                }
              }
            }
          }
        }
        case None =>
          logWarning(s"Got status update for unknown executor $appId/$execId")
      }
    }

	//....
}



```




# 资源调度机制源码分析(schedule(),两种资源调度算法)

在Master类中有schedule方法,我们先来看:**在schedule方法中Driver的启动**

```
private def schedule(): Unit = {
  //判断,master不是alive的话,就直接返回
  //也就是说,standby master是不会进行application的资源调度的
  if (state != RecoveryState.ALIVE) { return }
  
  //Random.shuffle对传入的元素集合进行随机的打乱
  val shuffledWorkers = Random.shuffle(workers) // Randomization helps balance drivers
  for (worker <- shuffledWorkers if worker.state == WorkerState.ALIVE) {//过滤出来状态为ALIVE的worker
    //首先,调度driver
	//为什么要调度driver,什么情况下会注册Driver,并且会导致Driver被调度
	//其实,只有用yarn-cluster模式提交的时候,才会注册Driver
	//因为yarn-client和Standalone模式都会在本地直接启动Driver,而不会来注册Driver,就更不可能让master调度Driver,其实在worker.endpoint.send(LaunchDriver(driver.id, driver.desc))中就可以看到这点

	for (driver <- waitingDrivers) {//Driver的注册的时候会把Driver放入waitingDrivers中(Master.receiveAndReply()中的case RequestSubmitDriver)
      //如果当前的worker的可用内存比driver要的内存要大,并且当前worker的可用核数比driver要的要大
	  //那么就调用launchDriver方法启动driver
	  if (worker.memoryFree >= driver.desc.mem && worker.coresFree >= driver.desc.cores) {
        launchDriver(worker, driver)//launchDriver见下面的详解:这里是使用endpoint去启动Worker上的Driver
        //从等待缓存中移除driver
		waitingDrivers -= driver
      }
    }
  }
  //这里是Executor的启动
  startExecutorsOnWorkers()
}



===============================
//在某一个worker上启动Driver
private def launchDriver(worker: WorkerInfo, driver: DriverInfo) {
  logInfo("Launching driver " + driver.id + " on worker " + worker.id)
  //将该driver加入到worker的内存的缓存结构中
  worker.addDriver(driver)
  
  //同时将worker也加入到driver的内存缓存中
  driver.worker = Some(worker)
  
  //拿到这个worker的endpoint引用,然后向这个worker发送LaunchDriver消息(让worker来启动driver)
  worker.endpoint.send(LaunchDriver(driver.id, driver.desc))
  //将driver的状态设置为RUNNING
  driver.state = DriverState.RUNNING
}



//那么在Worker类中有一个方法去接收发送过来的消息,就是receive方法
override def receive: PartialFunction[Any, Unit] = synchronized {
  //...
  
  case LaunchDriver(driverId, driverDesc) => {
    logInfo(s"Asked to launch driver $driverId")
    val driver = new DriverRunner(
      conf,
      driverId,
      workDir,
      sparkHome,
      driverDesc.copy(command = Worker.maybeUpdateSSLSettings(driverDesc.command, conf)),
      self,
      workerUri,
      securityMgr)
	  
	//加入内存缓存中
    drivers(driverId) = driver
    
	//启动driver
	driver.start()
	
	//将已使用的内存和核数修改
    coresUsed += driverDesc.cores
    memoryUsed += driverDesc.mem
  }
  //...
}

===============================

```



**在Master类中有schedule方法中同时还要启动Executor**

```
private def schedule(): Unit = {
  if (state != RecoveryState.ALIVE) { return }
  
  val shuffledWorkers = Random.shuffle(workers)
  for (worker <- shuffledWorkers if worker.state == WorkerState.ALIVE) {
	for (driver <- waitingDrivers) {
	  if (worker.memoryFree >= driver.desc.mem && worker.coresFree >= driver.desc.cores) {
        launchDriver(worker, driver)
		waitingDrivers -= driver
      }
    }
  }

  //这里是Executor的启动
  startExecutorsOnWorkers()
}




```

而startExecutorsOnWorkers的方法如下

```

private def startExecutorsOnWorkers(): Unit = {
  // Right now this is a very simple FIFO scheduler. We keep trying to fit in the first app
  // in the queue, then the second app, etc.
  for (app <- waitingApps if app.coresLeft > 0) {
    val coresPerExecutor: Option[Int] = app.desc.coresPerExecutor

	// 过滤掉那些不能启动该app的worker
    val usableWorkers = workers.toArray.filter(_.state == WorkerState.ALIVE) //过滤出worker状态为ALIVE
      .filter(worker => worker.memoryFree >= app.desc.memoryPerExecutorMB && //worker的空闲内存大于memoryPerExecutorMB
        worker.coresFree >= coresPerExecutor.getOrElse(1))  //worker上的空闲CPU大于每个Executor需要的CPU
      .sortBy(_.coresFree).reverse//按照worker的coresFree倒序排列
	  
	//assignedCores = new Array[Int](numUsable)每个worker上分配的core数量
    val assignedCores = scheduleExecutorsOnWorkers(app, usableWorkers, spreadOutApps)

    // Now that we've decided how many cores to allocate on each worker, let's allocate them
    for (pos <- 0 until usableWorkers.length if assignedCores(pos) > 0) {
      allocateWorkerResourceToExecutors(app, assignedCores(pos), coresPerExecutor, usableWorkers(pos))
    }
  }
}
```

allocateWorkerResourceToExecutors方法如下:在指定的worker上用分配的核数去启动一个或多个Executor

```
private def allocateWorkerResourceToExecutors(
    app: ApplicationInfo,
    assignedCores: Int,
    coresPerExecutor: Option[Int],
    worker: WorkerInfo): Unit = {
  // If the number of cores per executor is specified, we divide the cores assigned
  // to this worker evenly among the executors with no remainder.
  // Otherwise, we launch a single executor that grabs all the assignedCores on this worker.
  //得到的是在一个worker上的executor数量
  val numExecutors = coresPerExecutor.map { assignedCores / _ }.getOrElse(1)
  val coresToAssign = coresPerExecutor.getOrElse(assignedCores)
  //如果每个executor的core数量没有指定,那么在worker上的executor数量为1,同时将assignedCores都给该executor
  //如果指定了每个executor的core数量,那么用分配的assignedCores/coresPerExecutor得到executor的数量
  for (i <- 1 to numExecutors) {//循环启动worker上的executor
    val exec = app.addExecutor(worker, coresToAssign)//coresToAssign是每个要启动的executor上的core数量
	//启动Executor
    launchExecutor(worker, exec)
    app.state = ApplicationState.RUNNING
  }
}


/*
spread out调度算法的举例
我们在spark-shell脚本中,可以指定要多少个executor,每个executor多少个CPU(core),多少内存,那么基于此处的机制
实际上,最后executor的实际数量,以及每个executor的CPU,可能与配置是不一样的,因为这里是基于总的CPU进行分配的
比如配置中是:3个executor,每个executor要3个CPU,我们的机器有9个worker,每个有1个CPU
那么根据上面的算法,会给每个worker分配一个core,然后给每个worker启动一个executor,
最后会启动9个executor,每个executor有1个cpu core
*/

/*
非spread out调度算法的举例
比如总共有10个worker,每个有10个core,此时有app要总共分配20个core,那么其实,只会分配到两个worker上
每个worker占满10个core,
那么其他的app,就只能分配到下一个worker了

比如我们的spark-shell配置里,要10个executor,每个要2个core,那么总共有20个core,
但是在非spread out算法下,其实总共只会启动2个executor,每个要有10个core
*/

//综上

```






launchExecutor方法如下

```
private def launchExecutor(worker: WorkerInfo, exec: ExecutorDesc): Unit = {
  logInfo("Launching executor " + exec.fullId + " on worker " + worker.id)
  worker.addExecutor(exec)
	
	//拿到worker的Actor引用,然后发送消息去启动Executor,在Worker.receive方法中有对应的case LaunchExecutor去处理
  worker.endpoint.send(LaunchExecutor(masterUrl,
    exec.application.id, exec.id, exec.application.desc, exec.cores, exec.memory))
	
	//还要通知Driver端有Executor添加了,在APPClient.receive方法中有:case ExecutorAdded处理
  exec.application.driver.send(
    ExecutorAdded(exec.id, worker.id, worker.hostPort, exec.cores, exec.memory))
}

```

**spread out和非spread out的不同**

scheduleExecutorsOnWorkers方法源码

```


  private def scheduleExecutorsOnWorkers(
      app: ApplicationInfo,
      usableWorkers: Array[WorkerInfo],
      spreadOutApps: Boolean): Array[Int] = {
    val coresPerExecutor = app.desc.coresPerExecutor
    val minCoresPerExecutor = coresPerExecutor.getOrElse(1)
    val oneExecutorPerWorker = coresPerExecutor.isEmpty
    val memoryPerExecutor = app.desc.memoryPerExecutorMB
    val numUsable = usableWorkers.length
    val assignedCores = new Array[Int](numUsable) // Number of cores to give to each worker
    val assignedExecutors = new Array[Int](numUsable) // Number of new executors on each worker
    var coresToAssign = math.min(app.coresLeft, usableWorkers.map(_.coresFree).sum)

    /** Return whether the specified worker can launch an executor for this app. */
    def canLaunchExecutor(pos: Int): Boolean = {
		//.....
    }

    // Keep launching executors until no more workers can accommodate any
    // more executors, or if we have reached this application's limits
    var freeWorkers = (0 until numUsable).filter(canLaunchExecutor)
    while (freeWorkers.nonEmpty) {
      freeWorkers.foreach { pos =>
        var keepScheduling = true
		
		//keepScheduling默认是true,即:采用spread out调度机制,如果我们指定了为false,那么可能的将一个worker上的资源分配完之后,再去其他的worker上分配资源
        while (keepScheduling && canLaunchExecutor(pos)) {
          coresToAssign -= minCoresPerExecutor
          assignedCores(pos) += minCoresPerExecutor

          // If we are launching one executor per worker, then every iteration assigns 1 core
          // to the executor. Otherwise, every iteration assigns cores to a new executor.
          if (oneExecutorPerWorker) {
            assignedExecutors(pos) = 1
          } else {
            assignedExecutors(pos) += 1
          }

		  //spread out 调度算法是将app尽可能多的分布到每个worker上,而非spread out是尽可能多的将每个worker上的资源用完了,再去分配其他的worker
          if (spreadOutApps) {
            keepScheduling = false
          }
        }
      }
      freeWorkers = freeWorkers.filter(canLaunchExecutor)
    }
    assignedCores
  }

```