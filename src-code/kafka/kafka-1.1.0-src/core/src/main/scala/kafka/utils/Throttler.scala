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

import kafka.metrics.KafkaMetricsGroup
import org.apache.kafka.common.utils.Time

import java.util.concurrent.TimeUnit
import java.util.Random

import scala.math._

/**
 * A class to measure and throttle the rate of some process. The throttler takes a desired rate-per-second
 * (the units of the process don't matter, it could be bytes or a count of some other thing), and will sleep for 
 * an appropriate amount of time when maybeThrottle() is called to attain the desired rate.
 * 
 * @param desiredRatePerSec: The rate we want to hit in units/sec
 * @param checkIntervalMs: The interval at which to check our rate
 * @param throttleDown: Does throttling increase or decrease our rate?
 * @param time: The time implementation to use
 */
/*
主要目的是限制某些操作的执行速度，其实主要用于清理日志时限制IO速度。这个类会接收一个给定的期望速率(单位是 每秒，
这里的**其实不重要，可以是字节或个数，主要是限制速率)
 */
@threadsafe
class Throttler(desiredRatePerSec: Double,// 期望速率
                checkIntervalMs: Long = 100L, // 检查间隔，单位ms
                throttleDown: Boolean = true,// 是否需要往下调节速度，即降低速率
                metricName: String = "throttler",// 待调节项名称
                units: String = "entries",// 待调节项单位，默认是字节
                time: Time = Time.SYSTEM) // 时间字段
  extends Logging with KafkaMetricsGroup {

  private val lock = new Object
  private val meter = newMeter(metricName, units, TimeUnit.SECONDS)
  private val checkIntervalNs = TimeUnit.MILLISECONDS.toNanos(checkIntervalMs)
  private var periodStartNs: Long = time.nanoseconds
  private var observedSoFar: Double = 0.0
  
  def maybeThrottle(observed: Double) {
    val msPerSec = TimeUnit.SECONDS.toMillis(1)
    val nsPerSec = TimeUnit.SECONDS.toNanos(1)

    /*
    该类还实现了KafkaMetricsGroup trait，你可以认为后者就是构造度量元对象用的(例如通过newMeter)。
    Throttle类只有一个方法: maybeThrottle。该方法代码写了一大堆，一句一句分析太枯燥，

    我直接举个例子说吧: 假设我们要限制IO速率，单位是字节/秒，每100毫秒查一次。我们想要限制速率为10字节/毫秒。
    现在我们在500ms内检测到一共发送了6000字节，那么实际速率是6000/500 = 12字节/毫秒，比期望速率要高，因此我们要限制IO速率，
    此时怎么办呢？很简单，如果是按照期望速率，应该花费6000/10 = 600ms，比实际多花了100ms，
    因此程序sleep 100ms把那段多花的时间浪费掉就起到了限制速率的效果。简单来说程序就是这么实现的: )
     */
    meter.mark(observed.toLong)
    lock synchronized {
      observedSoFar += observed
      val now = time.nanoseconds
      val elapsedNs = now - periodStartNs
      // if we have completed an interval AND we have observed something, maybe
      // we should take a little nap
      if (elapsedNs > checkIntervalNs && observedSoFar > 0) {
        val rateInSecs = (observedSoFar * nsPerSec) / elapsedNs
        val needAdjustment = !(throttleDown ^ (rateInSecs > desiredRatePerSec))
        if (needAdjustment) {
          // solve for the amount of time to sleep to make us hit the desired rate
          val desiredRateMs = desiredRatePerSec / msPerSec.toDouble
          val elapsedMs = TimeUnit.NANOSECONDS.toMillis(elapsedNs)
          val sleepTime = round(observedSoFar / desiredRateMs - elapsedMs)
          if (sleepTime > 0) {
            trace("Natural rate is %f per second but desired rate is %f, sleeping for %d ms to compensate.".format(rateInSecs, desiredRatePerSec, sleepTime))
            time.sleep(sleepTime)
          }
        }
        periodStartNs = now
        observedSoFar = 0
      }
    }
  }

}

object Throttler {
  
  def main(args: Array[String]) {
    val rand = new Random()
    val throttler = new Throttler(100000, 100, true, time = Time.SYSTEM)
    val interval = 30000
    var start = System.currentTimeMillis
    var total = 0
    while(true) {
      val value = rand.nextInt(1000)
      Thread.sleep(1)
      throttler.maybeThrottle(value)
      total += value
      val now = System.currentTimeMillis
      if(now - start >= interval) {
        println(total / (interval/1000.0))
        start = now
        total = 0
      }
    }
  }
}
