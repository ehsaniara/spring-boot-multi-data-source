# Spring Boot 2 with Multiple DataSource for Postgres Data Replication

There are times that even having the best DataBase (PostgresSql, Oracle, MySql, .. ) Tuning can not be as help-full as Application Level separating Read and Writes

Spring Boot 2.2.2 with Multiple DataSource for Postgres Data Replication
## Postgres Setup
For This Demo you need 2.2.2 separate Postgres DataBase where one as Master and the other re one as a Replica.


---

## Spring Boot Setup
From https://start.spring.io/ select **web**, **data-jpa**, **lombok**, **postgresDriver**
Or Select the following share link:
Spring Initializr
Initializr generates spring boot project with just what you need to start quickly!start.spring.io


Once you Generate and download the zip file, you should have similar POM file as:
```...
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

   ...
</dependencies>
...
```

for this demo I use HikariDataSource as a default connection pool library by Spring Boot 2.2.2
we need to have 2 separate DataSource and EntityManager one for the Writes(Master/Primary) and one for Reads(Slave/Secondary).
```
application.yml
spring:
  datasource-write:
    driver-class-name: org.postgresql.Driver
    jdbc-url: jdbc:postgresql://localhost:5432/demo
    username: 'postgres_user_for_db_write'
    password: 'you_password'
    platform: postgresql
    hikari:
      idle-timeout: 10000
      maximum-pool-size: 10
      minimum-idle: 5
      pool-name: WriteHikariPool

  datasource-read:
    driver-class-name: org.postgresql.Driver
    jdbc-url: jdbc:postgresql://localhost:5433/demo
    username: 'postgres_user_for_db_read'
    password: 'you_password'
    platform: postgresql
    hikari:
      idle-timeout: 10000
      maximum-pool-size: 10
      minimum-idle: 5
      pool-name: ReadHikariPool
```

as you see I have 2 data-source as: datasource-write and datasource-read with their own credentials.

DataSource Configurations for WriteDB:
```
@Configuration
@ConfigurationProperties("spring.datasource-write")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryWrite",
        transactionManagerRef = "transactionManagerWrite",
        basePackages = {"com.ehsaniara.multidatasource.repository.writeRepository"}
)
public class DataSourceConfigWrite extends HikariConfig {

    public final static String PERSISTENCE_UNIT_NAME = "write";

    @Bean
    public HikariDataSource dataSourceWrite() {
        return new HikariDataSource(this);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryWrite(
            final HikariDataSource dataSourceWrite) {

        return new LocalContainerEntityManagerFactoryBean() {{
            setDataSource(dataSourceWrite);
            setPersistenceProviderClass(HibernatePersistenceProvider.class);
            setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
            setPackagesToScan(MODEL_PACKAGE);
            setJpaProperties(JPA_PROPERTIES);
        }};
    }

    @Bean
    public PlatformTransactionManager transactionManagerWrite(EntityManagerFactory entityManagerFactoryWrite) {
        return new JpaTransactionManager(entityManagerFactoryWrite);
    }
}
```

DataSource Configurations for ReadDB:

```
@Configuration
@ConfigurationProperties("spring.datasource-read")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryRead",
        transactionManagerRef = "transactionManagerRead",
        basePackages = {"com.ehsaniara.multidatasource.repository.readRepository"}
)
public class DataSourceConfigRead extends HikariConfig {

    public final static String PERSISTENCE_UNIT_NAME = "read";


    @Bean
    public HikariDataSource dataSourceRead() {
        return new HikariDataSource(this);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryRead(
            final HikariDataSource dataSourceRead) {

        return new LocalContainerEntityManagerFactoryBean() {{
            setDataSource(dataSourceRead);
            setPersistenceProviderClass(HibernatePersistenceProvider.class);
            setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
            setPackagesToScan(MODEL_PACKAGE);
            setJpaProperties(JPA_PROPERTIES);
        }};
    }

    @Bean
    public PlatformTransactionManager transactionManagerRead(EntityManagerFactory entityManagerFactoryRead) {
        return new JpaTransactionManager(entityManagerFactoryRead);
    }
}
```

Read and Write repositories should be in a separated packages :

Write: ```com.ehsaniara.multidatasource.repository.writeRepository```

Read: ```com.ehsaniara.multidatasource.repository.readRepository```

you also need to set:
```
public final static String MODEL_PACKAGE = "com.ehsaniara.multidatasource.model";

public final static Properties JPA_PROPERTIES = new Properties() {{
    put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
    put("hibernate.hbm2ddl.auto", "update");
    put("hibernate.ddl-auto", "update");
    put("show-sql", "true");
}};
```
and the actual logic are in the service layer:

```
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

        Assert.notNull(customer, "Invalid customer");
        Assert.isNull(customer.getId(), "customer id should be null");
        Assert.notNull(customer.getName(), "Invalid customer name");

        return customerWriteRepository.save(customer);
    }

    public Customer updateCustomer(Customer customer) {

        Assert.notNull(customer, "Invalid customer");
        Assert.notNull(customer.getId(), "Invalid customer id");

        return customerWriteRepository.save(customer);
    }
}
```

