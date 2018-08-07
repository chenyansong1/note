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

package org.apache.ibatis.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

public class ParamNameResolver {

  private static final String GENERIC_NAME_PREFIX = "param";

  /**
   * <p>
   * The key is the index and the value is the name of the parameter.<br />
   * The name is obtained from {@link Param} if specified. When {@link Param} is not specified,
   * the parameter index is used. Note that this index could be different from the actual index
   * when the method has special parameters (i.e. {@link RowBounds} or {@link ResultHandler}).
   * </p>
   *
   ParamNameResolver 使用 name 宇段（ SortedMap<Integer, String＞类型
   记录了参数在参数列表中的位置索引与参数名称之间的对应关系，
   其中 key 表示参数在参数列表中的索引位置，value 表示参数名称 ，
   参数名称可以通过＠Param 注解指定，如果没有指定＠Param 注解，则使用参数索引作为其名称
   如果参数列表中包含 RowBounds 类型或 ResultHandler 类型的参数 ，
   则这两种类型的参数并不会被记录到 name 集合中，这就会导致参数的索引与名称不一致，例子如下
   *
   * <ul>
   * <li>aMethod(@Param("M") int a, @Param("N") int b) -&gt; {{0, "M"}, {1, "N"}}</li>
   * <li>aMethod(int a, int b) -&gt; {{0, "0"}, {1, "1"}}</li>
   * <li>aMethod(int a, RowBounds rb, int b) -&gt; {{0, "0"}, {2, "1"}}</li>
   * </ul>
   */
  private final SortedMap<Integer, String> names;

  // ParamNameResolver 的 hasParamAnnotation 字段（boolean 类型）记录对应方法的参数列表中是否使用了＠Param 注解
  private boolean hasParamAnnotation;

  public ParamNameResolver(Configuration config, Method method) {
    // 获取参数列表中每个参数的类型
    final Class<?>[] paramTypes = method.getParameterTypes();
    // 获取参数列表上的注解
    final Annotation[][] paramAnnotations = method.getParameterAnnotations();
    // 该集合用于记录参数索引与参数名称的对应关系
    final SortedMap<Integer, String> map = new TreeMap<Integer, String>();

    int paramCount = paramAnnotations.length;
    // get names from @Param annotations
    for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
      // 如果参数是 RowBounds 类型或 ResultHandler 类型， 则跳过对该参数的分析
      if (isSpecialParameter(paramTypes[paramIndex])) {
        // skip special parameters
        continue;
      }
      String name = null;
      // 遍历该参数对应的注解集合
      for (Annotation annotation : paramAnnotations[paramIndex]) {
        if (annotation instanceof Param) {// @Param("aa")
          // @Param 注解出现过一次，就将 hasParamAnnotation 初始化为 true
          hasParamAnnotation = true;
          // 获取自 Param 注解指定的参数名称
          name = ((Param) annotation).value();
          break;
        }
      }

      // 这个 if 代码段解释了上面的示例中 names 集合项的 value 为什么是 ”0”和” 1 ”
      if (name == null) {// 没有注解时
        // @Param was not specified.
        if (config.isUseActualParamName()) {// 该参数没有对应的 @Param 注解，则根据自己置决定是否使用参数实际名称作为其名称
          name = getActualParamName(method, paramIndex);
        }
        if (name == null) {
          // use the parameter index as the name ("0", "1", ...)
          // gcode issue #71  使用参数的索引作为其名称
          name = String.valueOf(map.size());
        }
      }
      map.put(paramIndex, name);
    }

    // 初始化 names 集合
    names = Collections.unmodifiableSortedMap(map);
  }

  private String getActualParamName(Method method, int paramIndex) {
    if (Jdk.parameterExists) {
      return ParamNameUtil.getParamNames(method).get(paramIndex);
    }
    return null;
  }

  private static boolean isSpecialParameter(Class<?> clazz) {
    return RowBounds.class.isAssignableFrom(clazz) || ResultHandler.class.isAssignableFrom(clazz);
  }

  /**
   * Returns parameter names referenced by SQL providers.
   */
  public String[] getNames() {
    return names.values().toArray(new String[0]);
  }

  /**
   * <p>
   * A single non-special parameter is returned without a name.<br />
   * Multiple parameters are named using the naming rule.<br />
   * In addition to the default names, this method also adds the generic names (param1, param2,
   * ...).
   * </p>
   */
  public Object getNamedParams(Object[] args) {// args 是传过来的参数值 数组
    final int paramCount = names.size();
    if (args == null || paramCount == 0) {// 无参数，返回 null
      return null;
    } else if (!hasParamAnnotation && paramCount == 1) {// 未使用@Param 且只有一个参数
      return args[names.firstKey()];
    } else {// 处理使用自 Param 注解指定了参数名称或有多个参数的情况
      final Map<String, Object> param = new ParamMap<Object>();
      int i = 0;
      for (Map.Entry<Integer, String> entry : names.entrySet()) {
        // 参数名， 参数值
        param.put(entry.getValue(), args[entry.getKey()]);
        // add generic param names (param1, param2, ...)
        // //下面是为参数创建”param＋索号”格式的默认参数名称，例如： paraml , param2 等 并添加到 param 集合 中
        final String genericParamName = GENERIC_NAME_PREFIX + String.valueOf(i + 1);
        // ensure not to overwrite parameter named with @Param
        // 如采 @Param 注解指定的参数名称就是 ” param＋索 引”格式的，则不需要再添加
        if (!names.containsValue(genericParamName)) {
          param.put(genericParamName, args[entry.getKey()]);
        }
        i++;
      }
      return param;
    }
  }
}
