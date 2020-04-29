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

import java.io._
import java.nio._
import java.nio.channels._
import java.util.concurrent.locks.{Lock, ReadWriteLock}
import java.lang.management._
import java.util.{Properties, UUID}
import javax.management._

import scala.collection._
import scala.collection.mutable
import kafka.cluster.EndPoint
import org.apache.kafka.common.network.ListenerName
import org.apache.kafka.common.security.auth.SecurityProtocol
import org.apache.kafka.common.utils.{Base64, KafkaThread, Utils}
import org.slf4j.event.Level

/**
 * General helper functions!
 * 最常用的帮助函数
 *
 * This is for general helper functions that aren't specific to Kafka logic. Things that should have been included in
 * the standard library etc.
  *
  * 按照这个文件中注释提示的那样，这个文件里面的帮助函数都是最最常用的函数，并不限定为Kafka的逻辑服务。
  * 因此如果要在这个文件中添加一个函数，一定要确保这个函数有很完善的文档另外还要保证一定不能只在某个特定的领域使用，应该适用于最常见的场景中
 *
 * If you are making a new helper function and want to add it to this class please ensure the following:
 * 1. It has documentation
 * 2. It is the most general possible utility, not just the thing you needed in one particular place
 * 3. You have tests for it if it is nontrivial in any way
 */
object CoreUtils extends Logging {

  /**
   * Return the smallest element in `traversable` if it is not empty. Otherwise return `ifEmpty`.
   */
  def min[A, B >: A](traversable: TraversableOnce[A], ifEmpty: A)(implicit cmp: Ordering[B]): A =
    if (traversable.isEmpty) ifEmpty else traversable.min(cmp)

  /**
   * Wrap the given function in a java.lang.Runnable
   * @param fun A function
   * @return A Runnable that just executes the function
   */
  // 接收一个无返回值的函数封装到一个新建的Java Runnable对象中返回
  def runnable(fun: => Unit): Runnable =
    new Runnable {
      def run() = fun
    }

  /**
    * Create a thread
    *
    * @param name The name of the thread
    * @param daemon Whether the thread should block JVM shutdown
    * @param fun The function to execute in the thread
    * @return The unstarted thread
    */
  // 创建新的线程，参数决定线程名称、执行操作以及是否为守护线程
  def newThread(name: String, daemon: Boolean)(fun: => Unit): Thread =
    new KafkaThread(name, runnable(fun), daemon)

  /**
    * Do the given action and log any exceptions thrown without rethrowing them.
    *
    * @param action The action to execute.
    * @param logging The logging instance to use for logging the thrown exception.
    * @param logLevel The log level to use for logging.
    */
  // 执行给定的操作(以函数参数方式传递), 记录下任何异常但绝不抛出异常，而是吞掉异常
  def swallow(action: => Unit, logging: Logging, logLevel: Level = Level.WARN) {
    try {
      action
    } catch {
      case e: Throwable => logLevel match {
        case Level.ERROR => logger.error(e.getMessage, e)
        case Level.WARN => logger.warn(e.getMessage, e)
        case Level.INFO => logger.info(e.getMessage, e)
        case Level.DEBUG => logger.debug(e.getMessage, e)
        case Level.TRACE => logger.trace(e.getMessage, e)
      }
    }
  }

  /**
   * Recursively delete the list of files/directories and any subfiles (if any exist)
   * @param files sequence of files to be deleted
   */
  def delete(files: Seq[String]): Unit = files.foreach(f => Utils.delete(new File(f)))

  /**
   * Invokes every function in `all` even if one or more functions throws an exception.
   *
   * If any of the functions throws an exception, the first one will be rethrown at the end with subsequent exceptions
   * added as suppressed exceptions.
   */
  // Note that this is a generalised version of `Utils.closeAll`. We could potentially make it more general by
  // changing the signature to `def tryAll[R](all: Seq[() => R]): Seq[R]`
  def tryAll(all: Seq[() => Unit]): Unit = {
    var exception: Throwable = null
    all.foreach { element =>
      try element.apply()
      catch {
        case e: Throwable =>
          if (exception != null)
            exception.addSuppressed(e)
          else
            exception = e
      }
    }
    if (exception != null)
      throw exception
  }

  /**
   * Register the given mbean with the platform mbean server,
   * unregistering any mbean that was there before. Note,
   * this method will not throw an exception if the registration
   * fails (since there is nothing you can do and it isn't fatal),
   * instead it just returns false indicating the registration failed.
   * @param mbean The object to register as an mbean
   * @param name The name to register this mbean with
   * @return true if the registration succeeded
   */
  /*
  将给定的mbean注册到平台mbean服务器
  如果已经注册过会先卸载该mbean再重新注册。该方法不会抛出任何异常，只是简单滴返回false表示注册失败
   */
  def registerMBean(mbean: Object, name: String): Boolean = {
    try {
      val mbs = ManagementFactory.getPlatformMBeanServer()
      mbs synchronized {
        val objName = new ObjectName(name)
        // 如果已经注册，先卸载，再注册
        if(mbs.isRegistered(objName))
          mbs.unregisterMBean(objName)
        mbs.registerMBean(mbean, objName)
        true
      }
    } catch {
      case e: Exception => {
        error("Failed to register Mbean " + name, e)
        false
      }
    }
  }

  /**
   * Unregister the mbean with the given name, if there is one registered
   * @param name The mbean name to unregister
   */
  // 取消mbean的注册。如果没有注册过，就什么都不做
  def unregisterMBean(name: String) {
    val mbs = ManagementFactory.getPlatformMBeanServer()
    mbs synchronized {
      val objName = new ObjectName(name)
      if(mbs.isRegistered(objName))
        mbs.unregisterMBean(objName)
    }
  }

  /**
   * Read some bytes into the provided buffer, and return the number of bytes read. If the
   * channel has been closed or we get -1 on the read for any reason, throw an EOFException
    * 读取给定ReadableByteChannel的buffer到一个ByteBuffer，如果读取失败抛出EOFException异常，否则返回读取的字节数
   */
  def read(channel: ReadableByteChannel, buffer: ByteBuffer): Int = {
    channel.read(buffer) match {
      case -1 => throw new EOFException("Received -1 when reading from channel, socket has likely been closed.")
      case n: Int => n
    }
  }

  /**
   * This method gets comma separated values which contains key,value pairs and returns a map of
   * key value pairs. the format of allCSVal is key1:val1, key2:val2 ....
   * Also supports strings with multiple ":" such as IpV6 addresses, taking the last occurrence
   * of the ":" in the pair as the split, eg a:b:c:val1, d:e:f:val2 => a:b:c -> val1, d:e:f -> val2
   */
  /*
 接收一个逗号分隔的key/value对，类似于key1: value1,key2: value2,...
 使用正则表达式解析成一个HashMap并返回。该方法还有个形式，是返回成一个字符串序列，即字符串数组
   */
  def parseCsvMap(str: String): Map[String, String] = {
    val map = new mutable.HashMap[String, String]
    if ("".equals(str))
      return map
    val keyVals = str.split("\\s*,\\s*").map(s => {
      val lio = s.lastIndexOf(":")
      (s.substring(0,lio).trim, s.substring(lio + 1).trim)
    })
    keyVals.toMap
  }

  /**
   * Parse a comma separated string into a sequence of strings.
   * Whitespace surrounding the comma will be removed.
   */
  def parseCsvList(csvList: String): Seq[String] = {
    if(csvList == null || csvList.isEmpty)
      Seq.empty[String]
    else {
      csvList.split("\\s*,\\s*").filter(v => !v.equals(""))
    }
  }

  /**
   * Create an instance of the class with the given class name
    * 通过反射机制为给定类名表示的类创建一个实例
   */
  def createObject[T <: AnyRef](className: String, args: AnyRef*): T = {
    // 反射出一个实例对象
    val klass = Class.forName(className, true, Utils.getContextOrKafkaClassLoader()).asInstanceOf[Class[T]]
    val constructor = klass.getConstructor(args.map(_.getClass): _*)
    constructor.newInstance(args: _*)
  }

  /**
   * Create a circular (looping) iterator over a collection.
   * @param coll An iterable over the underlying collection.
   * @return A circular iterator over the collection.
   */
  def circularIterator[T](coll: Iterable[T]) =
    for (_ <- Iterator.continually(1); t <- coll) yield t

  /**
   * Replace the given string suffix with the new suffix. If the string doesn't end with the given suffix throw an exception.
    * 替换字符串的后缀，如果无法找到要替换的后缀直接抛出异常
   */
  def replaceSuffix(s: String, oldSuffix: String, newSuffix: String): String = {
    if(!s.endsWith(oldSuffix))
      throw new IllegalArgumentException("Expected string to end with '%s' but string is '%s'".format(oldSuffix, s))
    s.substring(0, s.length - oldSuffix.length) + newSuffix
  }

  /**
   * Read a big-endian integer from a byte array
    * 从字节数组中的指定位移处读取一个integer
   */
  def readInt(bytes: Array[Byte], offset: Int): Int = {
    ((bytes(offset) & 0xFF) << 24) |
    ((bytes(offset + 1) & 0xFF) << 16) |
    ((bytes(offset + 2) & 0xFF) << 8) |
    (bytes(offset + 3) & 0xFF)
  }

  /**
   * Execute the given function inside the lock
    * 在加锁的情况下执行一段函数
   */
  def inLock[T](lock: Lock)(fun: => T): T = {
    lock.lock()
    try {
      fun
    } finally {
      lock.unlock()
    }
  }

  // 使用给定的锁分别获取一个读锁和写锁，然后执行函数体
  def inReadLock[T](lock: ReadWriteLock)(fun: => T): T = inLock[T](lock.readLock)(fun)
  def inWriteLock[T](lock: ReadWriteLock)(fun: => T): T = inLock[T](lock.writeLock)(fun)


  //JSON strings need to be escaped based on ECMA-404 standard http://json.org
  // 将字符串中的某些字符进行转义，比如\b转成\\b
  def JSONEscapeString (s : String) : String = {
    s.map {
      case '"'  => "\\\""
      case '\\' => "\\\\"
      case '/'  => "\\/"
      case '\b' => "\\b"
      case '\f' => "\\f"
      case '\n' => "\\n"
      case '\r' => "\\r"
      case '\t' => "\\t"
      /* We'll unicode escape any control characters. These include:
       * 0x0 -> 0x1f  : ASCII Control (C0 Control Codes)
       * 0x7f         : ASCII DELETE
       * 0x80 -> 0x9f : C1 Control Codes
       *
       * Per RFC4627, section 2.5, we're not technically required to
       * encode the C1 codes, but we do to be safe.
       */
      case c if (c >= '\u0000' && c <= '\u001f') || (c >= '\u007f' && c <= '\u009f') => "\\u%04x".format(c: Int)
      case c => c
    }.mkString
  }

  /**
   * Returns a list of duplicated items
    * 返回列表中的重复项
   */
  def duplicates[T](s: Traversable[T]): Iterable[T] = {
    s.groupBy(identity)
      .map { case (k, l) => (k, l.size)}
      .filter { case (_, l) => l > 1 }
      .keys
  }

  def listenerListToEndPoints(listeners: String, securityProtocolMap: Map[ListenerName, SecurityProtocol]): Seq[EndPoint] = {
    def validate(endPoints: Seq[EndPoint]): Unit = {
      // filter port 0 for unit tests
      val portsExcludingZero = endPoints.map(_.port).filter(_ != 0)
      val distinctPorts = portsExcludingZero.distinct
      val distinctListenerNames = endPoints.map(_.listenerName).distinct

      require(distinctPorts.size == portsExcludingZero.size, s"Each listener must have a different port, listeners: $listeners")
      require(distinctListenerNames.size == endPoints.size, s"Each listener must have a different name, listeners: $listeners")
    }

    val endPoints = try {
      val listenerList = parseCsvList(listeners)
      listenerList.map(EndPoint.createEndPoint(_, Some(securityProtocolMap)))
    } catch {
      case e: Exception =>
        throw new IllegalArgumentException(s"Error creating broker listeners from '$listeners': ${e.getMessage}", e)
    }
    validate(endPoints)
    endPoints
  }

  def generateUuidAsBase64(): String = {
    val uuid = UUID.randomUUID()
    Base64.urlEncoderNoPadding.encodeToString(getBytesFromUuid(uuid))
  }

  def getBytesFromUuid(uuid: UUID): Array[Byte] = {
    // Extract bytes for uuid which is 128 bits (or 16 bytes) long.
    val uuidBytes = ByteBuffer.wrap(new Array[Byte](16))
    uuidBytes.putLong(uuid.getMostSignificantBits)
    uuidBytes.putLong(uuid.getLeastSignificantBits)
    uuidBytes.array
  }

  def propsWith(key: String, value: String): Properties = {
    propsWith((key, value))
  }

  def propsWith(props: (String, String)*): Properties = {
    val properties = new Properties()
    props.foreach { case (k, v) => properties.put(k, v) }
    properties
  }

  /**
   * Atomic `getOrElseUpdate` for concurrent maps. This is optimized for the case where
   * keys often exist in the map, avoiding the need to create a new value. `createValue`
   * may be invoked more than once if multiple threads attempt to insert a key at the same
   * time, but the same inserted value will be returned to all threads.
   *
   * In Scala 2.12, `ConcurrentMap.getOrElse` has the same behaviour as this method, but that
   * is not the case in Scala 2.11. We can remove this method once we drop support for Scala
   * 2.11.
   */
  def atomicGetOrUpdate[K, V](map: concurrent.Map[K, V], key: K, createValue: => V): V = {
    map.get(key) match {
      case Some(value) => value
      case None =>
        val value = createValue
        map.putIfAbsent(key, value).getOrElse(value)
    }
  }
}
