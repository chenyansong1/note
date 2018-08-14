/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.scripting.xmltags;

/**
 * @author Clinton Begin
 */
public class StaticTextSqlNode implements SqlNode {
  private final String text;

  public StaticTextSqlNode(String text) {
    this.text = text;
  }

  /*
  StaticTextSqlNode 中使用 text 字段(String 类型）记录了对应的非动态 SQL 语句节点，
   其apply（）方法直接将 text 宇段追加到 DynamicContext.sqlBuilder 字段中
   */
  @Override
  public boolean apply(DynamicContext context) {
    context.appendSql(text);
    return true;
  }

}