单例模式（Singleton Pattern）：

Spring容器中的Bean默认是单例的，这意味着每个Bean只有一个实例，以确保资源的有效使用。这有助于减少资源消耗和提高性能。
工厂模式（Factory Pattern）：

Spring使用工厂模式来创建和管理Bean。通过配置文件或注解，Spring容器能够根据需要创建并返回相应类型的Bean。
依赖注入模式（Dependency Injection）：

Spring框架是以依赖注入为核心的，它通过注入对象的方式来管理对象之间的依赖关系，从而降低了耦合度，提高了可测试性。
观察者模式（Observer Pattern）：

Spring的事件机制可以被视为观察者模式的一种实现。组件可以发布事件，而其他组件可以订阅这些事件并采取相应的行动。
装饰者模式（Decorator Pattern）：

Spring AOP（面向切面编程）使用装饰者模式来添加横切关注点，如日志、事务管理等，而不需要修改核心业务逻辑。
模板方法模式（Template Method Pattern）：

Spring提供了JdbcTemplate等模板类，这些模板类定义了通用的算法结构，但留下了一些具体实现细节供开发者自行填充，使数据库访问更加方便。
策略模式（Strategy Pattern）：

Spring的Bean可以根据需要动态切换实现，这就是通过策略模式实现的。您可以在运行时选择不同的Bean实现。
代理模式（Proxy Pattern）：

Spring AOP使用代理模式来拦截方法调用并添加额外的行为，如事务管理和安全性检查。
模型-视图-控制器模式（MVC Pattern）：

Spring框架鼓励使用MVC模式来组织Web应用程序，其中模型表示数据，视图负责展示数据，控制器负责处理用户请求并协调模型和视图之间的交互。
建造者模式（Builder Pattern）：

Spring框架中的配置文件通常使用建造者模式来构建和配置应用程序上下文。