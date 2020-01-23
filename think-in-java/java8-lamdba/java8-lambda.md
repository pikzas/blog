# Java8 Lambda 的使用指南
原文地址：https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html#syntax
> 对于匿名内部类存在一个问题，如果接口只有一个方法，那么该接口的匿名内部类的实现将看起来很臃肿，lambda表达式将可以让你的实现更加优雅。
> 如果打算将一个方法作为一个参数传入另一个方法，例如对按钮的点击事件做出响应，那么lambda将帮你更容易的实现。

## 使用lambda的几个场景的场景.

> 场景1，从列表中找出符合条件的人员(年龄大于18岁的人)
```Java
public class Person {
    public enum Gender{
        MALE, FEMALE
    }
    private String name;
    private Gender sex;
    private Integer age;
    public String getInfo(){
        return toString();
    }
    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", sex=" + sex +
                ", age=" + age +
                '}';
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Gender getSex() {
        return sex;
    }
    public void setSex(Gender sex) {
        this.sex = sex;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
}


public class Demo {
    public static void main(String[] args) {
        List<Person> ps = new ArrayList<Person>();
        getAgeOlderThan(ps,18);
    }   

    public static void getAgeOlderThan(List<Person> ps, int age) {
        for (Person p: ps) {
            if(p.getAge() > age){
                System.out.println(p.getInfo());
            }
        }
    }
}
```

> 场景2，如果上面的查询条件变动，那就需要重写代码（查询条件改为，在50岁与18岁之间的人）
```Java
public class Demo {
    public static void main(String[] args) {
        List<Person> ps = new ArrayList<Person>();
        getAgeBetween(ps,18,50);
    }   

    public static void getAgeBetween(List<Person> ps, int down,int up) {
        for (Person p: ps) {
            if(p.getAge() > down && p.getAge() < up){
                System.out.println(p.getInfo());
            }
        }
    }
}
```
> 场景3，可以看到上面的实现，每当出现一个新的查询条件，都需要新添加一个方法，我们想到引入接口使用多态来覆盖不同场景，引入用来做判断的接口PersonChecker和他的实现类CheckPersonWithGender。如果需要修改需求，只需要在添加一个实现类，比方说通过name来查找，只需要额外添加要给CheckPersonWithName类。
```Java
public interface CheckPerson {
    boolean check(Person p);
}

// 找出所有性别为指定类型的人
public class CheckPersonWithGender implements CheckPerson{
    @Override
    public boolean check(Person p) {
        if(p.getSex().equals(Person.Gender.FEMALE)){
            return true;
        }
        return false;
    }
}

// 找出所有名字为pikzas的人
public class CheckPersonWithName implements CheckPerson{
    @Override
    public boolean check(Person p) {
        if(p.getName().equals("pikzas")){
            return true;
        }
        return false;
    }
}

public class Demo {
    public static void main(String[] args) {
        List<Person> ps = new ArrayList<Person>();
        getByFilter(ps,new CheckPersonWithGender());
        getByFilter(ps,new CheckPersonWithName());
    }   

    public static void getByFilter(List<Person> ps,CheckPerson checker) {
        for (Person p: ps) {
            if(checker.check(p)){
                System.out.println(p.getInfo());
            }
        }
    }
}
```

> 场景4，上面的接口的引入方便了我们使用多态来扩展系统，但是每针对一个特定的过滤条件，我们都需创建一个新的接口实现类，于是可以通过匿名类的方式来将实现简化
```java
public class Demo {
    public static void main(String[] args) {
        List<Person> ps = new ArrayList<Person>();
        // 此处通过匿名类的方式避免了新创建一个类
        getByFilter(ps,new CheckPerson(){
            @Override
            public boolean check(Person p) {
                return p.getAge() > 50;
            }
        });
    }   

    public static void getByFilter(List<Person> ps,CheckPerson checker) {
        for (Person p: ps) {
            if(checker.check(p)){
                System.out.println(p.getInfo());
            }
        }
    }
}
```

> 场景5，可以看到上面通过内部类的方式已经使代码相当精简，但是还是有内部类的存在，于是便在Java8中推出了Lambda来替换这种只有一个方法需要实现的内部类，将代码再次简化。
```java
public class Demo {
    public static void main(String[] args) {
        List<Person> ps = new ArrayList<Person>();
        // 此处通过一个lambda表达式清晰明了的替换了一大堆内部类的实现
        getByFilter(ps,(Person p) -> p.getAge() > 18);
    }   

    public static void getByFilter(List<Person> ps,CheckPerson checker) {
        for (Person p: ps) {
            if(checker.check(p)){
                System.out.println(p.getInfo());
            }
        }
    }
}
```

> 场景6，上面用<code>(Person p) -> p.getAge() > 18</code> 替换了匿名内部类，主要是需要对数据做逻辑判断的接口CheckPerson的实现。但是该接口在Java8中有官方给定的一个替代接口,java.util.function.Predicate\<T>,于是又可以少定义一个接口。
```java
public interface CheckPerson {
    boolean check(Person p);
}
// 替换为
interface Predicate<T> {
    boolean test(T t);
}
```
```java
public class Demo {
    public static void main(String[] args) {
        List<Person> ps = new ArrayList<Person>();
        //
        getByFilter(ps, p -> p.getAge() > 18);
        getByPreditor(ps, (Person p) -> p.getAge() < 50);
    }
    public static void getByFilter(List<Person> ps,CheckPerson checker) {
        for (Person p: ps) {
            if(checker.check(p)){
                System.out.println(p.getInfo());
            }
        }
    }
    // 使用 Predicate<Person> checker 替换了 CheckPerson checker 进一步抽象，使接口变得更为通用
    // 对应用来做条件判定的方法变为Predicate中的test方法
    public static void getByPreditor(List<Person> ps,Predicate<Person> checker) {
        for (Person p: ps) {
            if(checker.test(p)){
                System.out.println(p.getInfo());
            }
        }
    }
}
```
> 场景7，上面的例子都是简单的将符合条件的对象打印了出来，同理于官方定义Predicate\<T>用来对数据做过滤，Java8还提供了一个接口Consumer\<T>来定义对满足条件的数据该执行的动作。
```java
public interface Consumer<T> {
    void accept(T t);
}
```
```java
public class Demo {

    public static void main(String[] args) {
        List<Person> ps = new ArrayList<Person>();
        getByFilter(ps, p -> p.getAge() > 18);
        getByPreditor(ps, p -> p.getAge() < 50);
        getByPreditorAndActByConsumer(
            ps,
            p-> p.getAge() > 18 && p.getAge() < 50, // 对Predicate接口中test方法的实现
            p -> System.out.println(p.getInfo()));  // 对Consumer接口中accept方法的实现
    }

    public static void getByFilter(List<Person> ps, CheckPerson checker) {
        for (Person p : ps) {
            if (checker.check(p)) {
                System.out.println(p.getInfo());
            }
        }
    }

    public static void getByPreditor(List<Person> ps, Predicate<Person> checker) {
        for (Person p : ps) {
            if (checker.test(p)) {
                System.out.println(p.getInfo());
            }
        }
    }

    // 对符合条件的数据应用动态的处理方式
    public static void getByPreditorAndActByConsumer(List<Person> ps, Predicate<Person> checker, Consumer<Person> consumer) {
        for (Person p : ps) {
            if (checker.test(p)) {
                consumer.accept(p);
            }
        }
    }

}
```

> 场景8，如果对于满足条件的数据需要额外的处理，然后再交给Consumer方法来执行动作，Java8提供了，例如我需要将每个人男性的年龄都加10，然后打印出来。
```java
public interface Function<T, R> {
    R apply(T t);
}
```
```java
public class Demo {
    public static void main(String[] args) {
        List<Person> ps = new ArrayList<Person>();

        ps.add(new Person("alex",Person.Gender.MALE,24));
        ps.add(new Person("Blex",Person.Gender.MALE,29));
        getByPreditorAndActByConsumerAndHandleWithFunction(
                ps,
                p-> p.getAge() > 18 && p.getAge() < 50, // 对Predicate接口中test方法的实现
                p -> p.getAge() + 10 ,
                p -> System.out.println(p));  // 对Consumer接口中accept方法的实现
    }


    // 对符合条件的数据应用动态的处理方式
    public static void getByPreditorAndActByConsumerAndHandleWithFunction(List<Person> ps, Predicate<Person> checker, Function<Person,Integer> function, Consumer<Integer> consumer) {
        for (Person p : ps) {
            if (checker.test(p)) {
                Integer ret = function.apply(p);
                consumer.accept(ret);
            }
        }
    }
}
```

## lambda的格式
> 从前面已经可以看到 lanmda主要由三部分构成 形参部分表达式 -> 逻辑代码表达
### 形参部分表达式的标准写法是（ParamA a,ParamB b,....)，参数类型可以省略，如果入参个数只有一个，那么小括号也能省略。
```java
public class Demo {
    public static void main(String[] args) {
        List<Person> ps = new ArrayList<Person>();
        getByPreditorAndActByConsumerAndHandleWithFunction(
                ps,
                p -> p.getAge() > 18 && p.getAge() < 50, // 单个参数，直接写形参
                (Person p) -> p.getAge() + 10 , // 单个参数，用括号包围起来，写上参数类型
                (p) -> System.out.println(p));  // 单个参数，用括号包围起来，不写上参数类型
    }


    // 对符合条件的数据应用动态的处理方式
    public static void getByPreditorAndActByConsumerAndHandleWithFunction(List<Person> ps, Predicate<Person> checker, Function<Person,Integer> function, Consumer<Integer> consumer) {
        for (Person p : ps) {
            if (checker.test(p)) {
                Integer ret = function.apply(p);
                consumer.accept(ret);
            }
        }
    }
}

public class MultiParam {
    public interface MathMethod{
        int operation(int a, int b);
    }

    public int doRun(int a, int b, MathMethod method){
        return method.operation(a,b);
    }

    public static void main(String[] args) {
        MathMethod addOp = (int a,int b) -> a+b; // 入参用括号围起来，标明入参数据类型
        MathMethod subOp = (a,b) -> a-b; // 入参用括号围起来，不标数据类型
        MultiParam demo = new MultiParam();
        System.out.print("23 + 55 = ");
        System.out.print(demo.doRun(23,55,addOp));
        System.out.println();
        System.out.print("55 + 22 = ");
        System.out.print(demo.doRun(55,22,subOp));
    }
}
```

### 逻辑代码表达的标准写法为{return expression}，在实现的接口方法不是void的情况下，可以同时将花括号和return省略，如果接口方法是void返回。则不能写return。
```java
public class MultiParam {
    public interface MathMethod{
        int operation(int a, int b); // 接口内方法不是void
    }

    public int doRun(int a, int b, MathMethod method){
        return method.operation(a,b);
    }

    public static void main(String[] args) {
        MathMethod addOp = (int a,int b) -> {return a+b;}; // 这时是一个标准写法，应为接口方法不是void，所以return必须存在。
        MathMethod subOp = (a,b) -> a-b; // {}和return可以同时去掉
        MultiParam demo = new MultiParam();
        System.out.print("23 + 55 = ");
        System.out.print(demo.doRun(23,55,addOp));
        System.out.println();
        System.out.print("55 + 22 = ");
        System.out.print(demo.doRun(55,22,subOp));
    }
    //之前的Consumer里面的accept方法返回值为void 就可以通过如下方式来写
    (p) -> {System.out.println(p)};  // 带上花括号，但是括号里面不能有return，因为这是lambda实现的接口方法返回的void。
    (p) -> System.out.println(p);  // 省略花括号，同理return 也不能存在。
    
```

