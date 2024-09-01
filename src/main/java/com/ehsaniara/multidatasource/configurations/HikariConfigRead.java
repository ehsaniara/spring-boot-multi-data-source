package com.ehsaniara.multidatasource.configurations;

import com.zaxxer.hikari.HikariConfig;

import java.util.Properties;

public class HikariConfigRead extends HikariConfig {

    protected final HikariReadProperties hikariReadProperties;
    protected final Properties jpaReadProperties;

    protected HikariConfigRead(HikariReadProperties hikariReadProperties) {
        this.hikariReadProperties = hikariReadProperties;
        setPoolName(this.hikariReadProperties.getPoolName());
        setMinimumIdle(this.hikariReadProperties.getMinimumIdle());
        setMaximumPoolSize(this.hikariReadProperties.getMaximumPoolSize());
        setIdleTimeout(this.hikariReadProperties.getIdleTimeout());

        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
        properties.put("show-sql", "true");
        this.jpaReadProperties = properties;
    }
}
