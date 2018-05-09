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

package kafka.common

import util.matching.Regex
import kafka.utils.Logging

/*
代码不复杂，就只是一个简单的trait，里面只有一个无返回值的方法：
validateChars——目的也很简单就是验证给定的属性值里面有没有非法字符。目前只允许字母，数字，句点(.)，下划线(_)以及横线(-)。
其他字符都视为非法字符，一旦发现存储非法字符立即抛出异常。Producer和Consumer都有对应的config实现了这个Config trait。
 */
trait Config extends Logging {

  def validateChars(prop: String, value: String) {
    val legalChars = "[a-zA-Z0-9\\._\\-]"
    val rgx = new Regex(legalChars + "*")

    rgx.findFirstIn(value) match {
      case Some(t) =>
        if (!t.equals(value))
          throw new InvalidConfigException(prop + " " + value + " is illegal, contains a character other than ASCII alphanumerics, '.', '_' and '-'")
      case None => throw new InvalidConfigException(prop + " " + value + " is illegal, contains a character other than ASCII alphanumerics, '.', '_' and '-'")
    }
  }
}




