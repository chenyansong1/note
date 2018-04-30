/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kafka.utils

import java.util.concurrent._
import atomic._
import org.apache.kafka.common.utils.KafkaThread

/**
 * A scheduler for running jobs
 * 
 * This interface controls a job scheduler that allows scheduling either repeating background jobs 
 * that execute periodically or delayed one-time actions that are scheduled in the future.
 */
trait Scheduler {
  
  /**
   * Initialize this scheduler so it is ready to accept scheduling of tasks
   */
  def startup()
  
  /**
   * Shutdown this scheduler. When this method is complete no more executions of background tasks will occur. 
   * This includes tasks scheduled with a delayed execution.
   */
  def shutdown()
  
  /**
   * Check if the scheduler has been started
   */
  def isStarted: Boolean
  
  /**
   * Schedule a task
   * @param name The name of this task
   * @param delay The amount of time to wait before the first execution
   * @param period The period with which to execute the task. If < 0 the task will execute only once.
   * @param unit The unit for the preceding times.
    * 调度一个任务的执行
   */
  def schedule(
               name: String, // 任务名称
               fun: ()=>Unit, // 完全是副作用(side effect，返回Unit)的函数，用于任务调度时执行
               delay: Long = 0, // 延时时间
               period: Long = -1, //  执行间隔，如果小于0，说明是一次性任务调度
               unit: TimeUnit = TimeUnit.MILLISECONDS // 延时时间单位，默认是毫秒
              )
}

/**
 * A scheduler based on java.util.concurrent.ScheduledThreadPoolExecutor
 * 
 * It has a pool of kafka-scheduler- threads that do the actual work.
 * @param threads The number of threads in the thread pool
 * @param threadNamePrefix The name to use for scheduler threads. This prefix will have a number appended to it.
 * @param daemon If true the scheduler threads will be "daemon" threads and will not block jvm shutdown.
 */
@threadsafe
class KafkaScheduler(
                     val threads: Int,  // 线程池中的线程的数量
                     val threadNamePrefix: String = "kafka-scheduler-", // 线程池中的线程名字前缀(threadNamePrefix,默认是kakfa-scheduler-)
                     daemon: Boolean = true // 指定是否是后台守护进程(daemon)，即这些线程不会阻塞JVM关闭
                    ) extends Scheduler with Logging {

  private var executor: ScheduledThreadPoolExecutor = null
  // 与线程池线程前缀一起组成和线程名称
  private val schedulerThreadId = new AtomicInteger(0)

  override def startup() {
    debug("Initializing task scheduler.")
    this synchronized {
      if(isStarted) // 如果调度器正常关闭类字段executor应该总是null，所以在startup方法开始需要先判断executor是否为空,如果不为空抛出异常说明调度器可能已经运行
        throw new IllegalStateException("This scheduler has already been started!")
      executor = new ScheduledThreadPoolExecutor(threads) // 创建具有threads个线程的线程池
      executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false) // 设置线程池关闭后不再执行任何类型的调度任务(包括重复调度执行的后台任务和一次性的延迟调度任务)
      executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false)
      executor.setThreadFactory(new ThreadFactory() {// 创建一个线程工厂来初始化那些线程。这里用到了包中Utils.scala中的newThread方法来创建线程
                                  def newThread(runnable: Runnable): Thread = 
                                    new KafkaThread(threadNamePrefix + schedulerThreadId.getAndIncrement(), runnable, daemon)
                                })
    }
  }


  override def shutdown() {
    debug("Shutting down task scheduler.")
    // We use the local variable to avoid NullPointerException if another thread shuts down scheduler at same time.
    val cachedExecutor = this.executor
    if (cachedExecutor != null) {
      this synchronized {
        cachedExecutor.shutdown()
        this.executor = null
      }
      cachedExecutor.awaitTermination(1, TimeUnit.DAYS)
    }
  }

  def schedule(name: String, fun: ()=>Unit, delay: Long, period: Long, unit: TimeUnit) {
    debug("Scheduling task %s with initial delay %d ms and period %d ms."
        .format(name, TimeUnit.MILLISECONDS.convert(delay, unit), TimeUnit.MILLISECONDS.convert(period, unit)))
    this synchronized {
      // 确保调度器已经启动
      ensureRunning

      // 返回一个封装了fun 的Runnable
      val runnable = CoreUtils.runnable {
        try {
          trace("Beginning execution of scheduled task '%s'.".format(name))
          fun()
        } catch {
          case t: Throwable => error("Uncaught exception in scheduled task '" + name +"'", t)
        } finally {
          trace("Completed execution of scheduled task '%s'.".format(name))
        }
      }

      if(period >= 0)// 需要重复调度执行的任务
        executor.scheduleAtFixedRate(runnable, delay, period, unit)
      else // 一次性的延时任务
        executor.schedule(runnable, delay, unit)
    }
  }

  def resizeThreadPool(newSize: Int): Unit = {
    executor.setCorePoolSize(newSize)
  }
  
  def isStarted: Boolean = {
    this synchronized {
      executor != null
    }
  }

  // 一个纯副作用的函数，只会被用在shutdown方法中。主要目的就是确保调度器已经启动。就是单纯地判断executor是否为空，如果为空抛出异常
  private def ensureRunning(): Unit = {
    if (!isStarted)
      throw new IllegalStateException("Kafka scheduler is not running.")
  }
}
