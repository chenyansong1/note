/*
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

package org.apache.spark.scheduler

/**
 * An interface for sort algorithm
 * FIFO: FIFO algorithm between TaskSetManagers
 * FS: FS algorithm between Pools, and FIFO or FS within Pools
 */
private[spark] trait SchedulingAlgorithm {
  def comparator(s1: Schedulable, s2: Schedulable): Boolean
}

private[spark] class FIFOSchedulingAlgorithm extends SchedulingAlgorithm {
  // 首先会根据作业的编号判断，作业编号越小，优先级越高
  // 如果是同一个作业，会再比较调度阶段优先级，根据调度阶段编号判断，调度阶段编号越小，优先级越高
  override def comparator(s1: Schedulable, s2: Schedulable): Boolean = {
    val priority1 = s1.priority
    val priority2 = s2.priority
    var res = math.signum(priority1 - priority2)
    if (res == 0) {
      val stageId1 = s1.stageId
      val stageId2 = s2.stageId
      res = math.signum(stageId1 - stageId2)
    }
    res < 0
  }
}

/*
FAIR 调度器策略中包含了两层调度，第一层中根调度池rootPool中包含了下级调度池Pool，
第二层为下级调度池Pool，其包含了TaskSetManager，具体的参见文件$SPARK_HOME/conf/fairscheduler.ml文件
在该文件中包含多个下级调度池Pool配置项
其中minShare--->最小任务数，weight--------->任务权重， 这两个参数用来设置第一级调度算法
而schedulingMode参数，用来设置第二层调度算法

 */
private[spark] class FairSchedulingAlgorithm extends SchedulingAlgorithm {


  /*
  在FAIR算法中，将正在运行的任务小于最小任务数 称为 饥饿状态
  1.如果某个调度处于饥饿状态，另一个处于非饥饿状态，则先满足处于饥饿状态的调度
  2.如果两个调度都处于饥饿状态，则比较资源比，先满足资源比 小的调度
  3.如果两个调度都处于非饥饿状态，则比较权重，先满足权重比 小的调度
  4.以上情况都相同时，根据调度的名称排序
   */
  override def comparator(s1: Schedulable, s2: Schedulable): Boolean = {
    // 最小任务数
    val minShare1 = s1.minShare
    val minShare2 = s2.minShare
    // 正在运行的任务数
    val runningTasks1 = s1.runningTasks
    val runningTasks2 = s2.runningTasks

    // 饥饿程序：判断 正在运行的任务数 是否小于最小任务数
    val s1Needy = runningTasks1 < minShare1
    val s2Needy = runningTasks2 < minShare2
    // 资源比:正在运行的任务数/最小任务数
    val minShareRatio1 = runningTasks1.toDouble / math.max(minShare1, 1.0)
    val minShareRatio2 = runningTasks2.toDouble / math.max(minShare2, 1.0)

    // 权重比：正在运行的任务数/任务的权重
    val taskToWeightRatio1 = runningTasks1.toDouble / s1.weight.toDouble
    val taskToWeightRatio2 = runningTasks2.toDouble / s2.weight.toDouble

    var compare = 0
    if (s1Needy && !s2Needy) {
      return true
    } else if (!s1Needy && s2Needy) {
      return false
    } else if (s1Needy && s2Needy) {
      compare = minShareRatio1.compareTo(minShareRatio2)
    } else {
      compare = taskToWeightRatio1.compareTo(taskToWeightRatio2)
    }
    if (compare < 0) {
      true
    } else if (compare > 0) {
      false
    } else {
      s1.name < s2.name
    }
  }
}

