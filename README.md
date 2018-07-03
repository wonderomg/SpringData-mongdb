# idea搭建springdata+mongodb+maven+springmvc 

今天我们来学习一下SpringData操作MongoDB。
项目环境：IntelliJ IDEA2017+maven3.5.0+MongoDB 3.2+JDK1.7+spring4.3.8

推荐网站（适合学习各种知识的基础）：[http://www.runoob.com/](http://www.runoob.com/)

mongo安装请参考：http://www.runoob.com/mongodb/mongodb-window-install.html



## **1. 创建maven工程**

首相创建maven工程，New project:
![创建maven工程](https://raw.githubusercontent.com/wonderomg/SpringData-mongdb/master/images/create-1.png)

![自行填写](https://raw.githubusercontent.com/wonderomg/SpringData-mongdb/master/images/create-2.png)

这里不使用idea自带maven插件，改用下载好的3.5.0版maven；

由于最近osChina的maven仓库挂掉，推荐大家使用阿里的镜像，速度快的飞起
`maven`配置：`D:\apache-maven-3.5.0\conf\setting.xml`中找到`mirrors`:

```xml
<mirrors>
    <mirror>
      <id>alimaven</id>
      <name>aliyun maven</name>
      <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
      <mirrorOf>central</mirrorOf>        
    </mirror>
</mirrors>
```

![选择下好的本地maven3.5.0](https://raw.githubusercontent.com/wonderomg/SpringData-mongdb/master/images/create-3.png)

![next](https://raw.githubusercontent.com/wonderomg/SpringData-mongdb/master/images/create-4.png)

项目结构如下图，手动创建相关文件夹及文件，并设置文件夹的对应属性：

![工程目录](https://raw.githubusercontent.com/wonderomg/SpringData-mongdb/master/images/create-5.png)

![目录结构](https://raw.githubusercontent.com/wonderomg/SpringData-mongdb/master/images/create-6.png)

至此，maven工程创建完毕。

## **2. 访问mongodb数据方式**

这里`dao`与`mongoDao`分别为`mongoDB`的两种查询方式：

> (1) `dao`为`JPA`的查询方式（请参考`springdataJPA`）；
>
> (2) `mongoDao`使用`mongoTemplate`，类似于关系型数据库使用的`jdbcTemplate`。

## **3. 详细代码**

先看配置文件
spring-context.xml为最基本的`spring`配置:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">
    <!-- 开启注解 -->
    <context:annotation-config/>
    <!--扫描service包嗲所有使用注解的类型-->
    <context:component-scan base-package="com.lewis.mongo">
        <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>

    <!-- 导入mongodb的配置文件 -->
    <import resource="spring-mongo.xml"/>
</beans>
```

spring-web.xml为`springmvc`的基本配置:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/mvc
       http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context-4.0.xsd">
    <!--配置springmvc-->
    <!--1.开启springmvc注解模式-->
    <context:annotation-config/>
    <!--简化配置：
        (1)主动注册DefaultAnnotationHandlerMapping,AnnotationMethodHandlerAdapter
        (2)提供一系列功能：数据绑定，数字和日期的format @NumberFormt @DataTimeFormat，xml json默认的读写支持-->
    <mvc:annotation-driven/>
    <!--servlet-mapping-->
    <!--2静态资源默认的servlet配置，
        （1）允许对静态资源的处理：js，gif
        （2）允许使用“/”做整体映射-->
    <!-- 容器默认的DefaultServletHandler处理 所有静态内容与无RequestMapping处理的URL-->
    <mvc:default-servlet-handler/>
    <!--3:配置jsp 显示viewResolver-->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/views/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

    <!-- 4自动扫描且只扫描@Controller -->
    <context:component-scan base-package="com.lewis.mongo.controller"/>

    <!-- 定义无需Controller的url<->view直接映射 -->
    <mvc:view-controller path="/" view-name="redirect:/hi/hello"/>
</beans>
```

spring-mongo.xml为mongo配置:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mongo="http://www.springframework.org/schema/data/mongo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/data/mongo
       http://www.springframework.org/schema/data/mongo/spring-mongo.xsd">

    <!-- 加载mongodb的属性配置文件 -->
    <context:property-placeholder location="classpath*:mongo.properties"/>
    <!-- spring连接mongodb数据库的配置 -->
    <mongo:mongo-client replica-set="${mongo.hostport}" id="mongo">
        <mongo:client-options connections-per-host="${mongo.connectionsPerHost}"
                              threads-allowed-to-block-for-connection-multiplier="${mongo.threadsAllowedToBlockForConnectionMultiplier}"
                              connect-timeout="${mongo.connectTimeout}" max-wait-time="${mongo.maxWaitTime}"
                              socket-timeout="${mongo.socketTimeout}"/>
    </mongo:mongo-client>
    <!-- mongo的工厂，通过它来取得mongo实例,dbname为mongodb的数据库名，没有的话会自动创建 -->
    <mongo:db-factory id="mongoDbFactory" dbname="mongoLewis" mongo-ref="mongo"/>

    <!-- 只要使用这个调用相应的方法操作 -->
    <bean id="mongoTemplate" class="org.springframework.data.mongodb.core.MongoTemplate">
        <constructor-arg name="mongoDbFactory" ref="mongoDbFactory"/>
    </bean>
    <!-- mongodb bean的仓库目录，会自动扫描扩展了MongoRepository接口的接口进行注入 -->
    <mongo:repositories base-package="com.lewis.mongo"/>
</beans>
```

mongo.properties:

```properties
mongo.hostport=10.10.16.234:27017  
mongo.connectionsPerHost=8  
mongo.threadsAllowedToBlockForConnectionMultiplier=4  
#连接超时时间
mongo.connectTimeout=1000  
#等待时间
mongo.maxWaitTime=1500  
mongo.autoConnectRetry=true  
mongo.socketKeepAlive=true  
#Socket超时时间
mongo.socketTimeout=1500  
mongo.slaveOk=true
```

pom.xml，这里要注意的是`junit`版本需要4.12以上，不然idea会报错；`spring-data-mongodb`版本要1.10.1以上；

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <properties>
        <!-- spring-framework-bom 版本号-->
        <spring.version>4.3.8.RELEASE</spring.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <groupId>com.liu</groupId>
    <artifactId>mongo</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <name>mongo Maven Webapp</name>
    <url>http://maven.apache.org</url>
    <dependencies>
        <!--使用junit4，注解的方式测试-->
        <!--<dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.11</version>
            <scope>test</scope>
        </dependency>-->
        <!-- https://mvnrepository.com/artifact/junit/junit -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>

        <!--日志-->
        <!--日志 slf4j,log4j,logback,common-logging-->
        <!--slf4j是规范/接口-->
        <!--log4j,logback,common-logging是日志实现 本项目使用slf4j + logback -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.12</version>
        </dependency>
        <!--实现slf4j并整合-->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <version>1.1.1</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.1.1</version>
        </dependency>
        <!--数据库相关-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.22</version>
            <!--maven工作范围 驱动在真正工作的时候使用，故生命周期改为runtime-->
            <scope>runtime</scope>
        </dependency>

        <!--servlet web相关 jstl1.2以上版本就不需要standard这个包了，
        不然会报错： TLD skipped. URI: http://java.sun.com/jstl/core_rt is already defined-->
        <!--<dependency>
            <groupId>taglibs</groupId>
            <artifactId>standard</artifactId>
            <version>1.1.2</version>
        </dependency>-->
        <dependency>
            <groupId>jstl</groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.5.4</version>
        </dependency>

        <!--spring-->
        <!--spring核心-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>4.3.8.RELEASE</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.springframework/spring-asm -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-asm</artifactId>
            <version>3.1.4.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>4.3.8.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>4.3.8.RELEASE</version>
        </dependency>
        <!--spring dao-->
        <!--<dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-mongodb</artifactId>
            <version>1.8.0.RELEASE</version>
        </dependency>-->
        <!-- https://mvnrepository.com/artifact/org.springframework.data/spring-data-mongodb -->
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-mongodb</artifactId>
            <version>1.10.1.RELEASE</version>
        </dependency>


        <dependency>
            <groupId>org.mongodb</groupId>
            <artifactId>mongo-java-driver</artifactId>
            <version>3.2.2</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.springframework/spring-aop -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
            <version>4.3.8.RELEASE</version>
        </dependency>
        <!--spring web-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>4.3.8.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>4.3.8.RELEASE</version>
        </dependency>
        <!--spring test-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>4.3.8.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>commons-collections</groupId>
            <artifactId>commons-collections</artifactId>
            <version>3.2.2</version>
        </dependency>
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.3.2</version>
        </dependency>
        <dependency>
            <groupId>commons-codec</groupId>
            <artifactId>commons-codec</artifactId>
            <version>1.10</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/javax/javaee-api -->
        <dependency>
            <groupId>javax</groupId>
            <artifactId>javaee-api</artifactId>
            <version>7.0</version>
            <scope>provided</scope>
        </dependency>

    </dependencies>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-framework-bom</artifactId>
                <version>${spring.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>net.sf.ehcache</groupId>
                <artifactId>ehcache-core</artifactId>
                <version>2.6.9</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <build>
        <finalName>mongo</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.3</version>
                <configuration>
                    <source>1.6</source>
                    <target>1.6</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

两个实体类：

Address.java:

```java
package com.lewis.mongo.entity;

/**
 * Created by liu on 2017/6/7.
 */
public class Address {

    private String city;
    private String street;
    private int num;

    public Address() {
    }

    public Address(String city, String street, int num) {
        this.city = city;
        this.street = street;
        this.num = num;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Address{" +
                "city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", num=" + num + '}';
    }
}
```

Person.java:

```java
package com.lewis.mongo.entity;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by liu on 2017/6/7.
 */
@Document(collection = "person")
public class Person implements Serializable {

    @Id
    private ObjectId id;
    private String name;
    private int age;
    private Address address;

    public Person() {
    }

    public Person(String name, int age, Address address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Person{" + "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", address=" + address + '}';
    }
}
```

JPA的dao，注意这里只要继承MongoRepository不用写注解spring就能认识这是个Repository，MongoRepository提供了基本的增删改查，不用实现便可直接调用，例如testMongo的personDao.save(persons);

PersonDao.java:

```java
package com.lewis.mongo.dao;

import com.lewis.mongo.entity.Person;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by liu on 2017/6/7.
 */
public interface PersonDao extends MongoRepository<Person, ObjectId> {

    @Query(value = "{'age' : {'$gte' : ?0, '$lte' : ?1}, 'name' : ?2}", fields = "{'name' : 1, 'age' : 1}")
    List<Person> findByAge(int age1, int age2, String name);
}

```

mongoTemplate的dao，PersonMongoDao.java:

```java
package com.lewis.mongo.mongoDao;

import com.lewis.mongo.entity.Person;
import java.util.List;

/**
 * Created by liu on 2017/6/7.
 */
public interface PersonMongoDao {
    List<Person> findAll();
    void insertPerson(Person user);
    void removePerson(String userName);
    void updatePerson();
    List<Person> findForRequery(String userName);
}
```

PersonMongoImpl.java:

```java
package com.lewis.mongo.mongoDao;

import com.lewis.mongo.entity.Person;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by liu on 2017/6/7.
 */
@Repository("personMongoImpl")
public class PersonMongoImpl implements PersonMongoDao {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public List<Person> findAll() {
        return mongoTemplate.findAll(Person.class, "person");
    }

    @Override
    public void insertPerson(Person person) {
        mongoTemplate.insert(person, "person");
    }

    @Override
    public void removePerson(String userName) {
        mongoTemplate.remove(Query.query(Criteria.where("name").is(userName)), "person");
    }

    @Override
    public void updatePerson() {
        mongoTemplate.updateMulti(Query.query(Criteria.where("age").gt(3).lte(5)), Update.update("age", 3), "person");
    }

    @Override
    public List<Person> findForRequery(String userName) {
        return mongoTemplate.find(Query.query(Criteria.where("name").is(userName)), Person.class);
    }
}
```

JPA查询的测试类，PersonDaoTest.java:

```java
import com.lewis.mongo.dao.PersonDao;
import com.lewis.mongo.entity.Address;
import com.lewis.mongo.entity.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 2017/6/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-context.xml", "classpath:spring/spring-mongo.xml"})
public class PersonDaoTest {

    @Resource
    private PersonDao personDao;

    /*先往数据库中插入10个person*/
    @Test
    public void testMongo() {
        List<Person> persons = new ArrayList<Person>();
        for (int i = 0; i < 10; i++) {
            persons.add(new Person("name" + i, i, new Address("广州市", "天河区", i)));
        }
        personDao.save(persons);
    }

    @Test
    public void findMongo() {
        System.out.println(personDao.findByAge(2, 8, "name6"));
    }
}
```

mongoTemplate查询的测试类，MongoTemplateTest.java:

```java
import com.lewis.mongo.entity.Address;
import com.lewis.mongo.entity.Person;
import com.lewis.mongo.mongoDao.PersonMongoImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by liu on 2017/6/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-context.xml", "classpath:spring/spring-mongo.xml"})
public class MongoTemplateTest {
    @Resource
    private PersonMongoImpl personMongo;

    @Test
    public void testMongoTemplate() {

        //personMongo.insertPerson(new Person("Lewis",24,new Address("广州","天河",20)));
        personMongo.removePerson("name3");
        personMongo.updatePerson();
        System.out.println(personMongo.findAll());
        System.out.println(personMongo.findForRequery("Lewis"));
    }
}
```

> **注意测试前请先通过`PersonDaoTest.java`中的`testMongo()`方法向数据库中插入数据。**

- **项目源码Git地址，仅供学习使用**：[https://github.com/wonderomg/SpringData-mongdb](https://github.com/wonderomg/SpringData-mongdb)
- 参考资料：http://docs.spring.io/spring-data/mongodb/docs/current/reference/html/
- 参考文章：[http://www.imooc.com/article/13777](http://www.imooc.com/article/13777)
