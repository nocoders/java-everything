# mybatis学习
 目录  
- [基本使用](#基本使用)
## 基本使用
1. 全局属性mapUnderscoreToCamelCase  
 再mybatis-config.xml中通过配置这个属性为 true 可以自动将以下画线方式命名的数据库列映射到 Java 对象的驼峰式命名属性中。  
### 注解使用
1. @Results注解使用  
 该注解同xml中的resultMap标签功能一致，定义id，之后便可在接口其他地方使用。
``` java
    @Select(value = "select * from user")
    @Results(id = "userMap",value = {  
            @Result(id = true,column ="id" ,property ="userId" ),
            @Result(column = "username" ,property = "userName"),
            @Result(column = "sex",property = "userSex"),
            @Result(column = "address",property = "userAddress"),
            @Result(column = "birthday",property = "userBirthday")
    })
    public List<User> findAll();
```

## 面试相关

### #{}和${}的区别

1. #{}是预编译处理，是占位符。${}是字符串替换，是字符串拼接符
2. mybatis在处理#{}符号时，会将#{}替换成？，调用preparedStatement进行赋值
3. mybatis在处理${}符号时，直接将其替换成变量的值，在mappedStatement的getBoundSql方法调用DynamicSqlSource.getBoundSql进行赋值
4. #{}能够有效防止sql注入，提高系统安全性

### xml文件同dao接口如何创建联系

1. mybatis初始化SQLSessionFactoryBean时，根据扫描路径扫描xml文件，根据xml文件中的namespace找到对应的接口。
2. 在解析 select|delete|update|insert标签时，每个标签都会解析成一个mappedStatement对象，mappedStatement对象包含id和sqlSource。id是类全名加上方法名称，sqlSource是由sql语句解析而成。mappedStatement创建完成后会 存储到Configuration里面。
3. 将解析完成后的类注册到Spring bean中：这个时候注册的是动态代理后的对象，在动态代理invoke方法中会处理参数信息，调用SQLSession中的真正方法。

### Mybatis是如何进行分页的？分页插件的原理是什么？

1. Mybatis使用RowBounds对象进行分页，它是针对ResultSet结果集执行的内存分页,一次性将所有数据查询出来然后代码中截取数据。
2. PageHelper插件是实现mybatis提供的Interceptor接口，拦截Executor中的query方法，拦截待执行sql，重写sql查询满足查询条件的数量、根据dialect方言给待执行sql拼接分页条件。

### 简述如何编写一个插件，以及Mybatis的插件运行原理

1. 如何编写插件：
   - 类实现mybatis的Interceptor接口并复写intercept()方法
   - 加上@Intercepts注解，在注解中表明拦截的类，方法和请求参数。
2. 插件运行原理
   - 在mybatis-config.xml中解析Plugins插件，添加到configuration中的拦截器链(interceptorChain)中。
   - 初始化executor、statementHandler、resultSetHandler和parameterHandler时，调用interceptorChain.pluginAll()方法植入相应的插件逻辑
   - Plugin实现了invocationHandler方法，他的invoke方法会拦截所有的方法调用，检查是否执行插件逻辑，最后执行被拦截的方法

### Mybatis是否支持延迟加载以及的实现原理

1. 在mybatis-config.xml文件中settings标签中配置lazyLoadingEnabled=true开启延迟加载，在特定的关联关系中指定fetchType属性可覆盖该项开关状态
2. 当延迟加载逻辑被触发时，MyBatis 会为需要延迟加载的类生成代理类，代理逻辑会拦截实体类的方法调用。