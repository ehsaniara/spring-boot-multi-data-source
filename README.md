# Spring Boot 2 with Multiple DataSource 

There are times that even having the best DataBase (PostgresSql, Oracle, MySql,... ) Tuning can not be as help-full as Application Level separating Read and Writes

Spring Boot 2.2.2 with Multiple DataSource 
## Postgres Setup
For This Demo you need 2.2.2 separate Postgres DataBase where one as Master and the other re one as a Replica.


for simplicity just run:
```docker-compose up --force-recreate```

the docker-compose.yml is already in the project which contains 2 PostgresSql in 2 different ports, with ```demo``` DataBase

> you can always uninstall it as: ```docker-compose down``` if you needed to.


Now if run this line you create customer in **postgres_primary**:
```
curl -H "Content-Type: application/json" --request POST --data '{"name":"Jay"}'   http://localhost:8080/customer
```
OR
```
curl -H "Content-Type: application/json" --request PUT --data '{"id":1 , "name":"Jay ehsaniara"}'   http://localhost:8080/customer
```

But if you run this line you getting data from **postgres_replica**:
```
 curl --request GET  http://localhost:8080/customer/1
```


---
### Spring Boot Setup
From https://start.spring.io/ select **web**, **data-jpa**, **lombok**, **postgresDriver**
Or Select the following share link:
Spring Initializr generates spring boot project with just what you need to start quickly!start.spring.io

Once you Generate and download the zip file, you should have similar POM file as:
```xml

<dependencies>

   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
   </dependency>

   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
   </dependency>

   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-configuration-processor</artifactId>
   </dependency>

   <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
   </dependency>

   <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
      <scope>runtime</scope>
   </dependency>

   
</dependencies>

```

for this demo I use HikariDataSource as a default connection pool library by Spring Boot 2.2.2
we need to have 2 separate DataSource and EntityManager one for the Writes(Master/Primary) and one for Reads(Slave/Secondary).
(application.yml)
```yaml

spring:
  datasource-write:
    driver-class-name: org.postgresql.Driver
    jdbc-url: jdbc:postgresql://localhost:5432/demo
    username: postgres
    password: password
    platform: postgresql
    hikari:
      idle-timeout: 10000
      maximum-pool-size: 10
      minimum-idle: 5
      pool-name: WriteHikariPool

  datasource-read:
    driver-class-name: org.postgresql.Driver
    jdbc-url: jdbc:postgresql://localhost:5433/demo
    username: postgres
    password: password
    platform: postgresql
    hikari:
      idle-timeout: 10000
      maximum-pool-size: 10
      minimum-idle: 5
      pool-name: ReadHikariPool
```

as you see I have 2 data-source as: datasource-write and datasource-read with their own credentials.

DataSource Configurations for WriteDB:
```java
@Configuration
@ConfigurationProperties("spring.datasource-write")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryWrite",
        transactionManagerRef = "transactionManagerWrite",
        basePackages = {"com.ehsaniara.multidatasource.repository.writeRepository"}
)
public class DataSourceConfigWrite extends HikariConfig {
    
    @Bean
    public HikariDataSource dataSourceWrite() {
        return new HikariDataSource(this);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryWrite(
            final HikariDataSource dataSourceWrite) {

        LocalContainerEntityManagerFactoryBean entityManagerFactory
                = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactory.setDataSource(dataSourceWrite);
        entityManagerFactory.setDataSource(dataSourceWrite);
        entityManagerFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactory.setPersistenceUnitName("write");
        entityManagerFactory.setPackagesToScan(MODEL_PACKAGE);
        entityManagerFactory.setJpaProperties(JPA_PROPERTIES);
        return entityManagerFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManagerWrite(EntityManagerFactory entityManagerFactoryWrite) {
        return new JpaTransactionManager(entityManagerFactoryWrite);
    }
}
```

DataSource Configurations for ReadDB:

```java
@Configuration
@ConfigurationProperties("spring.datasource-read")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryRead",
        transactionManagerRef = "transactionManagerRead",
        basePackages = {"com.ehsaniara.multidatasource.repository.readRepository"}
)
public class DataSourceConfigRead extends HikariConfig {

    @Bean
    public HikariDataSource dataSourceRead() {
        return new HikariDataSource(this);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryRead(
            final HikariDataSource dataSourceRead) {

        LocalContainerEntityManagerFactoryBean factoryBean 
                = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSourceRead);
        factoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        factoryBean.setPersistenceUnitName("read");
        factoryBean.setPackagesToScan(MODEL_PACKAGE);
        factoryBean.setJpaProperties(JPA_PROPERTIES);

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManagerRead(EntityManagerFactory entityManagerFactoryRead) {
        return new JpaTransactionManager(entityManagerFactoryRead);
    }
}
```

Read and Write repositories should be in a separated packages:

  +  Write: ```com.ehsaniara.multidatasource.repository.writeRepository```

  +  Read: ```com.ehsaniara.multidatasource.repository.readRepository```

you also need to set:
(write)
```java
    Properties properties = new Properties();
    properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
    properties.put("hibernate.hbm2ddl.auto", "update");
    properties.put("hibernate.ddl-auto", "update");
    properties.put("show-sql", "true");
```

on read config 
```java
    Properties properties = new Properties();
    properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
    properties.put("show-sql", "true");
```
Note: tables are automatically replicated

and the actual logic are in the service layer:

```java
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerReadRepository customerReadRepository;
    private final CustomerWriteRepository customerWriteRepository;

    public CustomerServiceImpl(CustomerReadRepository customerReadRepository, CustomerWriteRepository customerWriteRepository) {
        this.customerReadRepository = customerReadRepository;
        this.customerWriteRepository = customerWriteRepository;
    }

    public Optional<Customer> getCustomer(Long id) {
        return customerReadRepository.findById(id);
    }

    public Customer createCustomer(Customer customer) {

        return customerWriteRepository.save(customer);
    }

    public Customer updateCustomer(Customer customer) {

        return customerWriteRepository.save(customer);
    }
}
```
