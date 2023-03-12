# Spring

## 谈谈你理解的Spring

##### 什么是Spring

​	Spring是一个轻量级的控制反转（IoC）和面向切面编程（AOP）的容器框架。通过控制反转达到松耦合的目的，面向切面编程将系统性功能和业务性功能分离，强大可拓展性可将其他框架 粘合在一起，简化开发。

## IOC

### IOC介绍

- IOC容器是一个map，在项目启动时会读取配置文件下和指定注解（@Controller、@Service、@Repository、@Component）的bean节点，通过反射放到容器map里面，当我们使用指定对象时，再通过DI（依赖注入）从IOC容器中获取对象
- 控制反转：在没有引入IOC容器前，对象A依赖对象B，当程序运行到某一节点时，使用对象B之前必须手动创建对象B，控制权在自己手里。引入IOC容器后，两个对象之间没有了直接联系，当对象A需要使用对象B时，IOC容器会主动创建对象B给A使用
- 依赖注入：创建对象的控制权由自身声明注入变成了依赖IOC容器注入

### IOC原理

## AOP

面向切面编程，通常将与业务无关但是业务模块共同调用的逻辑封装起来，代码复用降低耦合度。

### 通知类型

- **前置通知(Before Advice)**: 在连接点之前执行的Advice，不过除非它抛出异常，否则没有能力中断执行流。使用 `@Before` 注解使用这个Advice。
- **返回之后通知(After Retuning Advice)**: 在连接点正常结束之后执行的Advice。例如，如果一个方法没有抛出异常正常返回。通过 `@AfterReturning` 关注使用它。
- **抛出（异常）后执行通知(After Throwing Advice)**: 如果一个方法通过抛出异常来退出的话，这个Advice就会被执行。通用 `@AfterThrowing` 注解来使用。
- **后置通知(After Advice)**: 无论连接点是通过什么方式退出的(正常返回或者抛出异常)都会执行在结束后执行这些Advice。通过 `@After` 注解使用。
- **围绕通知(Around Advice)**: 围绕连接点执行的Advice，就你一个方法调用。这是最强大的Advice。通过 `@Around` 注解使用。

### AOP的原理

## BeanFactory和ApplicationContext的区别

- BeanFactory是访问Spring bean容器的根接口，提供了最简单的容器功能，创建对象和拿对象，特点是每次获取对象时才会创建对象。
- ApplicationContext 是BeanFactory的子接口，除了继承了BeanFactory的功能外，还拓展了很多高级特性，特点是容器启动时就会创建所有的bean。
  - 继承MessageSource，因此支持国际化。
  - 资源文件访问，如URL和文件（ResourceLoader）。
  - 载入多个（有继承关系）上下文（即同时加载多个配置文件） ，使得每一个上下文都专注于一个特定的层次，比如应用的web层。
  - 提供在监听器中注册bean的事件。

##  Spring容器的启动流程

1. 初始化
   - 生成beanFactory工厂（创建bean对象）
   - 实例化beanDefinitionReader读取器（对特定注解读取生成beanDefinition）
   - 实例化路径扫描器（包扫描查找bean对象）
2. 配置类注入：解析传入的配置类
3. 容器刷新

   1. 刷新前预处理
   2. beanFactory的获取、预处理、子类进一步处理、后置处理器执行
   3. bean后置处理器注册
   4. MessageSource组件初始化、事件派发器初始化
   5. 容器刷新时子容器自定义逻辑
   6. 监听器注册
   7. 初始化剩下的所有的单实例bean
   8. 发布容器刷新完成事件

## Spring bean的生命流程

1. 实例化bean对象
2. 设置对象属性
3. 处理aware接口（实现aware接口就会通过aware接口可以拿到Spring容器的一些资源）
4. BeanPostProcessor前置处理，（实现了BeanPostProcessor的postProcessBeforeInitialization方法）
5. InitializingBean接口/init-method：初始化
6. BeanPostProcessor后置处理
7. DisposableBean/destroy-method:清理

# SpringMVC

# SpringBoot

## SpringBoot和SpringMVC的区别

- SpringBoot是一个配置框架，实现自动配置，简化项目搭建复杂度
- SpringMVC是一个MVC框架，用于解决WEB开发的问题



## 参考链接

- [Spring IoC有什么好处呢？](https://www.zhihu.com/question/23277575/answer/169698662)
- [Spring 中的设计模式详解](https://javaguide.cn/system-design/framework/spring/spring-design-patterns-summary.html)
- [Spring AOP and AspectJ AOP 有什么区别？](https://blog.csdn.net/jiayoudangdang/article/details/123297362)
- [比较Spring AOP与AspectJ](https://juejin.cn/post/6844903555531276296)
- [Spring 事务详解](https://javaguide.cn/system-design/framework/spring/spring-transaction.html)
- [Spring事务传播行为的理解。](https://mp.weixin.qq.com/s?__biz=Mzg2OTA0Njk0OA==&mid=2247486668&idx=2&sn=0381e8c836442f46bdc5367170234abb&chksm=cea24307f9d5ca11c96943b3ccfa1fc70dc97dd87d9c540388581f8fe6d805ff548dff5f6b5b&token=1776990505&lang=zh_CN#rd)

## IOC和DI

控制反转，将原本在程序中手动创建的对象的控制权交由Spring容器管理。IOC的实现方式是DI（依赖注入）

### Spring Bean

