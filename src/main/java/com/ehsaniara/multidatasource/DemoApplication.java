package com.ehsaniara.multidatasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
public class DemoApplication {

    public final static String MODEL_PACKAGE = "com.ehsaniara.multidatasource.model";

    public final static Properties JPA_PROPERTIES = new Properties() {{
        put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
        put("hibernate.hbm2ddl.auto", "update");
        put("hibernate.ddl-auto", "update");
        put("show-sql", "true");
    }};


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
