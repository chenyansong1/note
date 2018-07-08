* spring-src系列
  * 官网
  * spring能做什么
    * 主要是用来管理Bean，就是Java中的502，能够将很多的东西进行整合
    * 将Java Bean实现无缝对接
    * 反射创建Bean（根据配置获取名字）
    * IOC：容器（存放Bean的地方），这里涉及到的模式：工厂模式，单例模式，我们可以将Bean存入map中，原来是用户自己new对象，如果使用spring，那么是spring来创建对象（这就是控制反转）
    * DI：get, set, constructor (依赖注入)，这里涉及到赋值的问题，还是用的是反射（invoke）
    * AOP：为了增强原始的Bean的功能，这里就涉及到动态代理



* spring的BeanFactory的加载流程的那个
  * 定位：用的Reader结尾
  * 注册：BeanDefinition保存类信息，包括OOP关系
  * 加载：Factory，Context就是用户所定义的Bean放到IOC容器中（Map）





