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

import org.apache.kafka.common.utils.Time

import scala.math._

// 延迟项：用于标记那些在给定延迟时间之后执行的对象
class DelayedItem(delayMs: Long) extends Delayed with Logging {
  // delayMs:一个延迟时间

  // 当前毫秒值+延迟的毫秒数 ， 得到 应有的毫秒数
  private val dueMs = Time.SYSTEM.milliseconds + delayMs

  def this(delay: Long, unit: TimeUnit) = this(unit.toMillis(delay))

  /**
   * The remaining delay time
   */
  def getDelay(unit: TimeUnit): Long = {// 延迟时间的单位
    unit.convert(max(dueMs - Time.SYSTEM.milliseconds, 0), TimeUnit.MILLISECONDS)
  }

  // 比较两个 DelayedItem 之间的延迟的大小
  def compareTo(d: Delayed): Int = {
    val other = d.asInstanceOf[DelayedItem]
    java.lang.Long.compare(dueMs, other.dueMs)
  }

}
