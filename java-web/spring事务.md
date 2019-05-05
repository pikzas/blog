# Spring transaction
## 什么是事务
A用户向B用户转帐100，第一步要从A帐户扣出100，第二步要将B帐户加上100。其中无论是第一步失败，还是第二步失败。都应该将A、B帐户的余额保持和转帐操作之前一致。
事务就是一系列相关联操作的集合，一个事务可以是多个步骤组成，如果一个步骤失败，那么整个流程都应该回滚到初始状态。

### 事务的四个特性
* 原子性（Atomicity） 一个事务是一个整体，无论有多少个步骤组成，要么所有步骤都成功，要么所有步骤都失败。
* 一致性（Consistency） 一个事务完成（无论是成功还是失败），所有的业务都应该处于一致的状态，不应该部分步骤成功，部分步骤失败，显示中的数据一致性不会被破坏。
* 隔离性（Isolation） 多个事务处理相同的数据的时候，事务间应该是相互隔离的，防止数据损坏。
* 持久性（Durability） 一旦事务完成，无论系统发生什么异常，结果都不应该受到影响，通常事务的结果被写入到数据库中。

### Spring处理事务的核心接口
#### Spring涉及到事务管理的核心接口相互关系如下
![](./tx.jpg)
#### Spring并没有直接提供事务管理的实现，而是提供了一个接口PlatformTransactionManager。具体的实现依赖项目中所使用的持久化接口。
![](./PlatformTransactionManager.jpg)
#### PlatformTransactionManager 定义了所有的具体实现类必须要有的方法
* 通过TransactionDefinition获取TransactionStatus
* commit 提交事务
* rollback 回滚事务
#### TransactionDefinition接口
![](./Transaction%20Definition.PNG)
#### TransactionDefinition主要包含的属性和方法
* 事务的传播级别
* 事务的隔离级别
* 事务超时
* 只读状态
#### TransactionStatus接口
![](./TransactionStatus.jpg) 

#### 以上可以看到事务的具体实现可以有四种
* 使用JDBC事务，添加配置的方式为
```xml
   <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
       <property name="dataSource" ref="dataSource" />
   </bean>
```
* 使用Hibernate事务，添加配置的方式为
```xml
   <bean id="transactionManager" class="org.springframework.orm.hibernate3.HibernateTransactionManager">
　　   <property name="sessionFactory" ref="sessionFactory" />
   </bean>
```
* 使用JAVA持久化API事务（JPA）
```xml
   <bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
       <property name="sessionFactory" ref="sessionFactory" />
   </bean>
```
* JAVA原生API事务 如果没有使用以上任何一个实现，或者是使用了多个事务管理源（使用了多个数据源）
```xml
    <bean id="transactionManager" class="org.springframework.transaction.jta.JtaTransactionManager">
       <property name="transactionManagerName" value="java:/TransactionManager" />
    </bean>
```

### 事务属性的定义
#### 事务的传播级别
在上面TransactionDefinition接口中可以看到，一共有7个级别
* PROPAGATION_REQUIRED 当前方法必须运行在事务中，已经有事务，则在当前事务中；没有事务，会新建一个事务。
* PROPAGATION_SUPPORTS 当前方法可以运行在事务中，已经有事务，则在当前事务中；没有事务，不会创建新事务。
* PROPAGATION_MANDATORY 当前方法必须运行在事务中，如果没有事务，会抛出异常。
* PROPAGATION_REQUIRES_NEW 当前方法必须运行在自己的事务中，已经有事务，则将当前事务挂起，新建一个事务；没有事务，会新建一个事务。（如果使用JTATransactionManager的话，则需要访问TransactionManager）
* PROPAGATION_NOT_SUPPORTED 当前方法<bold>不支持</bold>运行在事务中，已经有事务，将当前事务挂起（如果使用JTATransactionManager的话，则需要访问TransactionManager）
* PROPAGATION_NEVER 当前方法不能运行在事务中，已经有事务，会抛出异常。
* PROPAGATION_NESTED 表示如果当前已经存在一个事务，那么该方法将会在嵌套事务中运行。嵌套的事务可以独立于当前事务进行单独地提交或回滚。如果当前事务不存在，那么其行为与PROPAGATION_REQUIRED一样

#### 原生JDBC的事务
代码展示了如何通过将Connection设置成conn.setAutoCommit(false)，然后在代码执行成功之后再通过conn.commit()方法来提交，或者是conn.rollback()来回滚操作。
```java
public class Demo {
    public static void main(String[] args) {
        test1();
    }

    public static Connection getConn() {
        String driver = "oracle.jdbc.OracleDriver";
        String url = "jdbc:oracle:thin:@xxxxx";
        String username = "username";
        String password = "password";
        Connection conn = null;
        try{
            Class.forName(driver);
            conn = (Connection) DriverManager.getConnection(url, username, password);
        }catch (Exception e){
            e.printStackTrace();
        }
        return conn;
    }

    public static void test1() {
        Connection conn = getConn();
        Random random = new Random();
        int id = random.nextInt(100);
        System.out.println("--------id is "+id+"--------");
        String sql = "insert into test (ID,NAME) values(?,?)";
        PreparedStatement pstmt;
        try {
            conn.setAutoCommit(false);
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, id+"");
            pstmt.setString(2, "haha");

            pstmt.executeUpdate();
            if(id%2==0){
                System.out.println("--------even num--------");
                int error = 9/0;
            }else{
                System.out.println("--------odd num--------");
            }
            conn.commit();
            pstmt.close();
            conn.close();
            System.out.println("----------commit-------------");
        } catch (Exception e) {
            try {
                conn.rollback();
                System.out.println("----------rollback-------------");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
```

#### 事务的传播级别详解—REQUIRED
* 场景1 
```java
class Demo{
    @Transactional(propagation = Propagation.REQUIRED)
    methodB();
}
```
如果此时单独调用methodB(),刚进入方法的时候,是没有事务的；然后由于注解上的事务传播级别是REQUIRED；所以此时Spring会默认创建一个事务，将methodB()内的所有操作放在一个事务内。
* 场景2
```java
class Demo{
    @Transactional(propagation = Propagation.REQUIRED)
    methodA(){
        methodB();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    methodB();
}
```
此时如果调用methodA()，Spring同样会为methodA()上新建一个事务，然后再调用methodB()时，由于已经存在事务，此时methodB()将在已有的事务中运行。Spring会保证methodB()内所有操作数据库使用的连接是和methodA()中使用到的数据库连接是同一个。

#### 事务的传播级别详解—SUPPORTS
* 场景1 
```java
class Demo{
    @Transactional(propagation = Propagation.SUPPORTS)
    methodB();
}
```
单独调用methodB()，由于默认没有事务，所以方法就不会有事务的存在。
* 场景2
```java
class Demo{
    @Transactional(propagation = Propagation.REQUIRED)
    methodA(){
        methodB();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    methodB();
}
```
此时如果调用methodA()，Spring同样会为methodA()上新建一个事务，然后再调用methodB()时，由于已经存在事务，此时methodB()将在已有的事务中运行。
#### 事务的传播级别详解—MANDATORY
* 场景1 
```java
class Demo{
    @Transactional(propagation = Propagation.MANDATORY)
    methodB();
}
```
单独调用methodB()，由于默认没有事务，此时会报错。
* 场景2
```java
class Demo{
    @Transactional(propagation = Propagation.REQUIRED)
    methodA(){
        methodB();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    methodB();
}
```
通过methodA()来调用methodB()，由于methodA()中有事务，methodB()将运行在已有的事务中。
#### 事务的传播级别详解—REQUIRES_NEW
* 场景1 
```java
class Demo{
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    methodB();
}
```
单独调用methodB()，由于默认没有事务，会新创建一个事务。
* 场景2
```java
class Demo{
    @Transactional(propagation = Propagation.REQUIRED)
    methodA(){
        methodB();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    methodB();
}
```
通过methodA()来调用methodB()，由于methodA()中有事务，methodB()将会把已有的事务挂起，新创建一个事务，methodB()将运行在新创建的事务中。methodB()和methodA()相互的运行结果不会因为对方失败而回滚，因为他们是在两个相互独立的事务中。如果要使用PROPAGATION_REQUIRES_NEW,需要使用 JtaTransactionManager作为事务管理器。
#### 事务的传播级别详解—NEVER
方法决不能运行在事务中，如果有事务存在，则会抛出异常。
#### 事务的传播级别详解—NESTED
* 场景1 
```java
class Demo{
    @Transactional(propagation = Propagation.NESTED)
    methodB();
}
```
单独调用methodB()，行为和Propagation.REQUIRED一样，由于默认没有事务，会新创建一个事务。
* 场景2
```java
class Demo{
    @Transactional(propagation = Propagation.REQUIRED)
    methodA(){
        funBefore();
        methodB();
        funAfter();
    }　

    @Transactional(propagation = Propagation.NESTED)
    methodB();
}
```
嵌套事务一个非常重要的概念就是内层事务依赖于外层事务。外层事务失败时，会回滚内层事务所做的动作。而内层事务操作失败并不会引起外层事务的回滚。
#### PROPAGATION_NESTED 与PROPAGATION_REQUIRES_NEW的区别
>如果不存在事务，两者的行为和PROPAGATION_REQUIRES一致，都会创建一个新的事务。
>如果已存在事务，两者也都会新创建一个事务。
* PROPAGATION_REQUIRES_NEW注解中，内外层事务不会相互影响，内层事务开启的时候，外层事务是挂起的，内外层事务部是用的同一个数据访问连接，可以看作两个独立的事务，任何一方失败都不会对另一方造成影响。
* PROPAGATION_NESTED注解中,内部事务和外部事务使用的是同一个数据访问连接，只是在内部事务开启之前，会创建一个Savepoint，内部事务回滚，会回滚到这个Savepoint；外部事物如果回滚，会连带内部事务一起回滚，应为内部事务完成之后并没有提交事务。

由此可见, PROPAGATION_REQUIRES_NEW 和 PROPAGATION_NESTED 的最大区别在于, PROPAGATION_REQUIRES_NEW 完全是一个新的事务, 而 PROPAGATION_NESTED 则是外部事务的子事务, 如果外部事务 commit, 嵌套事务也会被 commit, 这个规则同样适用于 roll back.

### 事务的隔离级别
#### 事务之间并发可能产生的后果
* 脏读 
    >发生在一个事务读取了另一个事务改写但尚未提交的数据时。如果改写在稍后被回滚了，那么第一个事务获取的数据就是无效的。
* 不可重复读
    >不可重复读发生在一个事务执行相同的查询两次或两次以上，但是每次都得到不同的数据时。这通常是因为另一个并发事务在两次查询期间进行了更新。
* 幻读
   >幻读与不可重复读类似。它发生在一个事务（T1）读取了几行数据，接着另一个并发事务（T2）插入了一些数据时。在随后的查询中，第一个事务（T1）就会发现多了一些原本不存在的记录。
#### 幻读与不可重复读的区别在于, 不可重复读常指一条记录，几次的查询结果不一致。幻读常指某一查询条件的结果的个数，多次查询后结果不一致。
隔离级别|含义
-|-
ISOLATION_DEFAULT | 使用数据库的默认隔离级别
ISOLATION_READ_UNCOMMITTED | 最低的隔离级别，允许读取到尚未COMMIT的数据，会导致脏读，幻读和不可重复读
ISOLATION_READ_COMMITTED |允许读取并发事务已经COMMIT的事务，可以阻止脏读，但是幻读和不可重复读还会发生
ISOLATION_REPEATABLE_READ | 对同一字段的多次读取结果都是一致的，除非数据是被本身事务自己所修改，可以阻止脏读和不可重复读，但幻读仍有可能发生
ISOLATION_SERIALIZABLE | 最高的隔离级别，完全服从ACID的隔离级别，确保阻止脏读、不可重复读以及幻读，也是最慢的事务隔离级别，因为它通常是通过完全锁定事务相关的数据库表来实现的

### 只读
### 事务超时　
为了使应用程序很好地运行，事务不能运行太长的时间。因为事务可能涉及对后端数据库的锁定，所以长时间的事务会不必要的占用数据库资源。事务超时就是事务的一个定时器，在特定时间内事务如果没有执行完毕，那么就会自动回滚，而不是一直等待其结束。
### 回滚规则
这些规则定义了哪些异常会导致事务回滚而哪些不会。默认情况下，事务只有遇到运行期异常时才会回滚，而在遇到检查型异常时不会回滚,但是你可以声明事务在遇到特定的检查型异常时像遇到运行期异常那样回滚。同样，你还可以声明事务遇到特定的异常不回滚，即使这些异常是运行期异常。

### 事务状态
这个接口描述的是一些处理事务提供简单的控制事务执行和查询事务状态的方法，在回滚或提交的时候需要应用对应的事务状态。
```java
public interface TransactionStatus{
    boolean isNewTransaction(); // 是否是新的事物
    boolean hasSavepoint(); // 是否有恢复点
    void setRollbackOnly();  // 设置为只回滚
    boolean isRollbackOnly(); // 是否为只回滚
    boolean isCompleted; // 是否已完成
} 
```

## 3 如何使用事务
### 3.1 编程式事务和声明式事务的区别
* 编程式事务，需要将事务控制的代码与业务逻辑写在一起，优点是可以精准的控制事务的行为、缺点就是有非业务代码侵入。
* 声明式事务，将事务的控制通过Spring AOP来实现，优点是没有代码侵入，只需要在使用事务的地方添加注解，缺点是配置复杂。
  
### 3.2 编程式事务
#### 3.2.1 使用TransactionTemplate实现的编程式事务
```java
TransactionTemplate tt = new TransactionTemplate(); // 新建一个TransactionTemplate
Object result = tt.execute(
    new TransactionCallback(){  
        public Object doTransaction(TransactionStatus status){  
            updateOperation();  
            return resultOfUpdateOperation();  
        }  
}); // 执行execute方法进行事务管理
```
或者是
```java
TransactionTemplate tt = new TransactionTemplate(); // 新建一个TransactionTemplate
Object result = tt.execute(
    new TransactionCallbackWithoutResult(){
        @Override
        protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
            // call dao
        }
}); // 执行execute方法进行事务管理
```
#### 3.2.2 使用PlatformTransactionManager实现的编程式事务
```java
DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(); //定义一个某个框架平台的TransactionManager，如JDBC、Hibernate
dataSourceTransactionManager.setDataSource(this.getJdbcTemplate().getDataSource()); // 设置数据源
DefaultTransactionDefinition transDef = new DefaultTransactionDefinition(); // 定义事务属性
transDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED); // 设置传播行为属性
TransactionStatus status = dataSourceTransactionManager.getTransaction(transDef); // 获得事务状态
try {
    // 数据库操作
    dataSourceTransactionManager.commit(status);// 提交
} catch (Exception e) {
    dataSourceTransactionManager.rollback(status);// 回滚
}
```
### 3.3 配置式事务

Spring配置文件中关于事务配置总是由三个组成部分，分别是DataSource、TransactionManager和代理机制这三部分，无论哪种配置方式，一般变化的只是代理机制这部分。

DataSource、TransactionManager这两部分只是会根据数据访问方式有所变化，比如使用Hibernate进行数据访问时，DataSource实际为SessionFactory，TransactionManager的实现为HibernateTransactionManager。

具体如下图：

![123](./Spring_transaction_config.jpg)
#### 3.3.1 每个Bean都有一个代理
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="http://www.springframework.org/schema/beans 
           http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
           http://www.springframework.org/schema/context
           http://www.springframework.org/schema/context/spring-context-2.5.xsd
           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">

    <bean id="sessionFactory"  
            class="org.springframework.orm.hibernate3.LocalSessionFactoryBean">  
        <property name="configLocation" value="classpath:hibernate.cfg.xml" />  
        <property name="configurationClass" value="org.hibernate.cfg.AnnotationConfiguration" />
    </bean>  

    <!-- 定义事务管理器（声明式的事务） -->  
    <bean id="transactionManager"
        class="org.springframework.orm.hibernate3.HibernateTransactionManager">
        <property name="sessionFactory" ref="sessionFactory" />
    </bean>
    
    <!-- 配置DAO -->
    <bean id="userDaoTarget" class="com.bluesky.spring.dao.UserDaoImpl">
        <property name="sessionFactory" ref="sessionFactory" />
    </bean>
    
    <bean id="userDao"  
        class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">  
           <!-- 配置事务管理器 -->  
           <property name="transactionManager" ref="transactionManager" />     
        <property name="target" ref="userDaoTarget" />  
         <property name="proxyInterfaces" value="com.bluesky.spring.dao.GeneratorDao" />
        <!-- 配置事务属性 -->  
        <property name="transactionAttributes">  
            <props>  
                <prop key="*">PROPAGATION_REQUIRED</prop>
            </props>  
        </property>  
    </bean>  
</beans>
```
#### 3.3.2 所有Bean共享一个代理基类
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="http://www.springframework.org/schema/beans 
           http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
           http://www.springframework.org/schema/context
           http://www.springframework.org/schema/context/spring-context-2.5.xsd
           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">

    <bean id="sessionFactory"  
            class="org.springframework.orm.hibernate3.LocalSessionFactoryBean">  
        <property name="configLocation" value="classpath:hibernate.cfg.xml" />  
        <property name="configurationClass" value="org.hibernate.cfg.AnnotationConfiguration" />
    </bean>  

    <!-- 定义事务管理器（声明式的事务） -->  
    <bean id="transactionManager"
        class="org.springframework.orm.hibernate3.HibernateTransactionManager">
        <property name="sessionFactory" ref="sessionFactory" />
    </bean>
    
    <bean id="transactionBase"  
            class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean"  
            lazy-init="true" abstract="true">  
        <!-- 配置事务管理器 -->  
        <property name="transactionManager" ref="transactionManager" />  
        <!-- 配置事务属性 -->  
        <property name="transactionAttributes">  
            <props>  
                <prop key="*">PROPAGATION_REQUIRED</prop>  
            </props>  
        </property>  
    </bean>    
   
    <!-- 配置DAO -->
    <bean id="userDaoTarget" class="com.bluesky.spring.dao.UserDaoImpl">
        <property name="sessionFactory" ref="sessionFactory" />
    </bean>
    
    <bean id="userDao" parent="transactionBase" >  
        <property name="target" ref="userDaoTarget" />   
    </bean>
</beans>
```
#### 3.3.3 使用拦截器
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xsi:schemaLocation="http://www.springframework.org/schema/beans 
           http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
           http://www.springframework.org/schema/context
           http://www.springframework.org/schema/context/spring-context-2.5.xsd
           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">

    <bean id="sessionFactory"  
            class="org.springframework.orm.hibernate3.LocalSessionFactoryBean">  
        <property name="configLocation" value="classpath:hibernate.cfg.xml" />  
        <property name="configurationClass" value="org.hibernate.cfg.AnnotationConfiguration" />
    </bean>  

    <!-- 定义事务管理器（声明式的事务） -->  
    <bean id="transactionManager"
        class="org.springframework.orm.hibernate3.HibernateTransactionManager">
        <property name="sessionFactory" ref="sessionFactory" />
    </bean> 
   
    <bean id="transactionInterceptor"  
        class="org.springframework.transaction.interceptor.TransactionInterceptor">  
        <property name="transactionManager" ref="transactionManager" />  
        <!-- 配置事务属性 -->  
        <property name="transactionAttributes">  
            <props>  
                <prop key="*">PROPAGATION_REQUIRED</prop>  
            </props>  
        </property>  
    </bean>
      
    <bean class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">  
        <property name="beanNames">  
            <list>  
                <value>*Dao</value>
            </list>  
        </property>  
        <property name="interceptorNames">  
            <list>  
                <value>transactionInterceptor</value>  
            </list>  
        </property>  
    </bean>  
  
    <!-- 配置DAO -->
    <bean id="userDao" class="com.bluesky.spring.dao.UserDaoImpl">
        <property name="sessionFactory" ref="sessionFactory" />
    </bean>
</beans>
```
#### 3.3.4 使用tx标签配置的拦截器
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xmlns:tx="http://www.springframework.org/schema/tx"
    xsi:schemaLocation="http://www.springframework.org/schema/beans 
           http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
           http://www.springframework.org/schema/context
           http://www.springframework.org/schema/context/spring-context-2.5.xsd
           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd
           http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">

    <context:annotation-config />
    <context:component-scan base-package="com.bluesky" />

    <bean id="sessionFactory"  
            class="org.springframework.orm.hibernate3.LocalSessionFactoryBean">  
        <property name="configLocation" value="classpath:hibernate.cfg.xml" />  
        <property name="configurationClass" value="org.hibernate.cfg.AnnotationConfiguration" />
    </bean>  

    <!-- 定义事务管理器（声明式的事务） -->  
    <bean id="transactionManager"
        class="org.springframework.orm.hibernate3.HibernateTransactionManager">
        <property name="sessionFactory" ref="sessionFactory" />
    </bean>

    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <tx:attributes>
            <tx:method name="*" propagation="REQUIRED" />
        </tx:attributes>
    </tx:advice>
    
    <aop:config>
        <aop:pointcut id="interceptorPointCuts"
            expression="execution(* com.bluesky.spring.dao.*.*(..))" />
        <aop:advisor advice-ref="txAdvice"
            pointcut-ref="interceptorPointCuts" />        
    </aop:config>      
</beans>
```
#### 3.3.5 全注解
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xmlns:tx="http://www.springframework.org/schema/tx"
    xsi:schemaLocation="http://www.springframework.org/schema/beans 
           http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
           http://www.springframework.org/schema/context
           http://www.springframework.org/schema/context/spring-context-2.5.xsd
           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd
           http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">

    <context:annotation-config />
    <context:component-scan base-package="com.bluesky" />

    <tx:annotation-driven transaction-manager="transactionManager"/>

    <bean id="sessionFactory"  
            class="org.springframework.orm.hibernate3.LocalSessionFactoryBean">  
        <property name="configLocation" value="classpath:hibernate.cfg.xml" />  
        <property name="configurationClass" value="org.hibernate.cfg.AnnotationConfiguration" />
    </bean>  

    <!-- 定义事务管理器（声明式的事务） -->  
    <bean id="transactionManager"
        class="org.springframework.orm.hibernate3.HibernateTransactionManager">
        <property name="sessionFactory" ref="sessionFactory" />
    </bean>
    
</beans>
```

### 4 一个声明式事务的实例
//todo
