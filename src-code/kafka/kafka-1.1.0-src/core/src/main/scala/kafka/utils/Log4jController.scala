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

import java.util
import java.util.Locale

import org.apache.log4j.{Level, LogManager, Logger}


/**
 * An MBean that allows the user to dynamically alter log4j levels at runtime.
 * The companion object contains the singleton instance of this class and
 * registers the MBean. The [[kafka.utils.Logging]] trait forces initialization
 * of the companion object.
 */
private class Log4jController extends Log4jControllerMBean {

  // 返回当前的一组logger(也包括root logger)，每个元素都是logger名称=日志级别这样的格式
  def getLoggers = {
    val lst = new util.ArrayList[String]()
    lst.add("root=" + existingLogger("root").getLevel.toString)
    val loggers = LogManager.getCurrentLoggers
    while (loggers.hasMoreElements) {
      val logger = loggers.nextElement().asInstanceOf[Logger]
      if (logger != null) {
        val level =  if (logger != null) logger.getLevel else null
        lst.add("%s=%s".format(logger.getName, if (level != null) level.toString else "null"))
      }
    }
    lst
  }


  //创建一个日志器logger，可能是root logger，更可能是普通的logger
  private def newLogger(loggerName: String) =
    if (loggerName == "root")
      LogManager.getRootLogger
    else LogManager.getLogger(loggerName)


  // 根据loggerName返回对应的logger
  private def existingLogger(loggerName: String) =
    if (loggerName == "root")
      LogManager.getRootLogger
    else LogManager.exists(loggerName)


  // 根据给定的logger name获取对应的日志级别
  def getLogLevel(loggerName: String) = {
    val log = existingLogger(loggerName)
    if (log != null) {
      val level = log.getLevel
      if (level != null)
        log.getLevel.toString
      else "Null log level."
    }
    else "No such logger."
  }


  // 设定日志级别。值得注意的是，如果loggerName为空或日志级别为空，返回false表明设置不成功
  def setLogLevel(loggerName: String, level: String) = {
    val log = newLogger(loggerName)
    if (!loggerName.trim.isEmpty && !level.trim.isEmpty && log != null) {
      log.setLevel(Level.toLevel(level.toUpperCase(Locale.ROOT)))
      true
    }
    else false
  }

}


/*
1. getLoggers: 返回一个日志器名称的列表List[String]
2. getLogLevel: 获取日志级别
3. setLogLevel: 设置日志级别。不过与普通的setter方法不同的是，该方法返回一个boolean，原因后面在其实现类里面说。
*/
private trait Log4jControllerMBean {
  def getLoggers: java.util.List[String]
  def getLogLevel(logger: String): String
  def setLogLevel(logger: String, level: String): Boolean
}

