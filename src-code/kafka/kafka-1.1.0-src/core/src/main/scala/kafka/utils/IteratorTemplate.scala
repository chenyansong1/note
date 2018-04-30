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

/*
迭代器模板有一个状态字段，
因此在定义迭代器模板抽象类之前首先定义了一个State状态object，
以及一组具体的状态object：完成(DONE)，READY(准备就绪)，NOT_READY(未准备)和FAILED(失败)
*/
class State
object DONE extends State
object READY extends State
object NOT_READY extends State
object FAILED extends State


/**
 * Transliteration of the iterator template in google collections. To implement an iterator
 * override makeNext and call allDone() when there is no more items
  *
  * 主要为遍历消息集合使用。
 */
abstract class IteratorTemplate[T] extends Iterator[T] with java.util.Iterator[T] {
  
  private var state: State = NOT_READY
  // 执行遍历中的下一个对象
  private var nextItem = null.asInstanceOf[T]

  // 如果迭代器已遍历完并无法找到下一项或下一项为空，直接抛出异常；否则将状态置为NOT_READY并返回下一项
  def next(): T = {
    if(!hasNext())// 既然你想要next()，那么没有next了，hasNext()就没有意义了，所以这里会抛出异常
      throw new NoSuchElementException()
    state = NOT_READY
    if(nextItem == null)
      throw new IllegalStateException("Expected item but none found.")
    nextItem
  }

  // 只是探查一下迭代器是否遍历完，如果是抛出异常，否则直接返回下一项，并不做非空判断，也不做状态设置
  def peek(): T = {
    if(!hasNext) // 这里就很Scala，没有参数就应该写成
      throw new NoSuchElementException()
    nextItem
  }

  // 如果状态为FAILED直接抛出异常，如果是DONE返回false，如果是READY返回true，否则调用maybeComputeNext方法
  def hasNext: Boolean = {
    if(state == FAILED)
      throw new IllegalStateException("Iterator is in failed state")
    state match {
      case DONE => false
      case READY => true
      case _ => maybeComputeNext()
    }
  }

  // 返回下一项，这是你需要唯一需要实现的抽象方法，同时你还需要在该方法中对状态字段进行更新
  protected def makeNext(): T

  // 调用makeNext获取到下一项，如果状态是DONE返回false，否则返回true并将状态置为READY
  def maybeComputeNext(): Boolean = {
    state = FAILED
    nextItem = makeNext()
    if(state == DONE) {
      false
    } else {
      state = READY
      true
    }
  }
  
  protected def allDone(): T = {
    state = DONE
    null.asInstanceOf[T]
  }
  
  override def remove =
    throw new UnsupportedOperationException("Removal not supported")

  protected def resetState() {
    state = NOT_READY
  }
}

