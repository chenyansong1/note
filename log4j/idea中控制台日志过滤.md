
我们通常会使用IDE（例如Intellij IDEA）开发Spark应用，而程序调试运行时会在控制台中打印出所有的日志信息。它描述了（伪）集群运行、程序执行的所有行为。


在很多情况下，这些信息对于我们来说是无关紧要的，我们更关心的是最终结果，无论是正常输出还是异常停止。
 

幸运的是，我们可以通过log4j主动控制日志输出的级别。引入log4j.Logger和log4j.Level，并在对象中设置Logger.getLogger("org").setLevel(Level.ERROR)

```
import org.apache.log4j.{Level, Logger}

object Example {
  Logger.getLogger("org").setLevel(Level.ERROR)

  def main(args: Array[String]) {
    ......
  }
}
```

以此运行后，控制台只输出ERROR级别信息，并不会错过输出结果和调试报错。

















