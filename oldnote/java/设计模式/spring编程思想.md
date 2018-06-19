spring编程思想



* AOP面向切面编程

  动态代理只是AOP的一种技术实现手段，AOP只是一种编程思想

​	

* BOP

  Bean与Bean之间的关系，不希望每次人为的重复管理，由程序来实现自动管理，spring开始就是从Bean的管理开始的



* IOC

  控制反转：创建对象的控制权反转（new的权利），以前是：谁使用谁new，但是现在在spring中，所有的Bean都是由spring来new，所以才叫做控制反转

  new出来以后的对象需要统一管理，所以才有了IOC容器的概念，所谓的IOC容器就是一个Map



* DI

  解决对象动态赋值的问题，动态调用get,set（通常采用的是反射）

  * Dependency Injection(依赖注入)，或者是Dependency Lookup（依赖查找）
  * 依赖注入，依赖查找，spring不仅保存自己创建的对象，而且保存对象与对象之间的关系
  * 注入即赋值，主要三种方式构造方法：set,直接赋值，先清理关系在赋值

  

  

  