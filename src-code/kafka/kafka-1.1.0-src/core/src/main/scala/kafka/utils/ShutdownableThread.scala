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

import java.util.concurrent.{CountDownLatch, TimeUnit}

import org.apache.kafka.common.internals.FatalExitError
/*
isInterruptible:表明是否允许中断

 */
abstract class ShutdownableThread(val name: String, val isInterruptible: Boolean = true) extends Thread(name) with Logging {
  this.setDaemon(false)
  this.logIdent = "[" + name + "]: "
  private val shutdownInitiated = new CountDownLatch(1)
  private val shutdownComplete = new CountDownLatch(1)

  def shutdown(): Unit = {
    initiateShutdown()
    awaitShutdown()
  }

  def isShutdownComplete: Boolean = {
    shutdownComplete.getCount == 0
  }

  def initiateShutdown(): Boolean = {
    this.synchronized {
      if (isRunning) {
        info("Shutting down")
        shutdownInitiated.countDown()
        if (isInterruptible)
          interrupt()
        true
      } else
        false
    }
  }

  /**
   * After calling initiateShutdown(), use this API to wait until the shutdown is complete
   */
  def awaitShutdown(): Unit = {
    shutdownComplete.await()
    info("Shutdown completed")
  }

  /**
   *  Causes the current thread to wait until the shutdown is initiated,
   *  or the specified waiting time elapses.
   *
   * @param timeout
   * @param unit
   */
  def pause(timeout: Long, unit: TimeUnit): Unit = {
    if (shutdownInitiated.await(timeout, unit))
      trace("shutdownInitiated latch count reached zero. Shutdown called.")
  }

  /**
   * This method is repeatedly invoked until the thread shuts down or this method throws an exception
   */
  def doWork(): Unit

  override def run(): Unit = {
    info("Starting")
    try {
      /*
      该类维护的isRunning类似于一个状态标志，控制着线程何时退出执行操作的循环。
      每次循环前检测isRunning值，如果一旦发现是false，则退出执行并将shutdownLatch阀门至于关闭状态；
      如果是true，则调用doWork来执行真正的操作
       */
      while (isRunning)
        // 我们只需要重写这个方法即可
        doWork()
    } catch {
      case e: FatalExitError =>
        shutdownInitiated.countDown()
        shutdownComplete.countDown()
        info("Stopped")
        Exit.exit(e.statusCode())
      case e: Throwable =>
        if (isRunning)
          error("Error due to", e)
    } finally {
       shutdownComplete.countDown()
    }
    info("Stopped")
  }

  def isRunning: Boolean = {
    shutdownInitiated.getCount() != 0
  }
}
