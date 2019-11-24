package com.yavor.projects.weather.api.configuration;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.yavor.projects.weather.api.repository",
        entityManagerFactoryRef = "timeSeriesEntityManager",
        transactionManagerRef = "timeSeriesTransactionManager"
)
public class TimeseriesDataSourceConfig {

    @Value("${timeseries.url}")
    private String url;

    @Value("${timeseries.username}")
    private String username;

    @Value("${timeseries.password}")
    private String password;

    @Value("${timeseries.driverName}")
    private String driverName;

    @Bean
    LocalContainerEntityManagerFactoryBean timeSeriesEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(timeSeriesDataSource());
        em.setPackagesToScan("com.yavor.projects.weather.api.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.jdbc.lob.non_contextual_creation", true);
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    DataSource timeSeriesDataSource() {
        var config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverName);
        config.setConnectionTimeout(10 * 1000);
        config.setConnectionTestQuery("SELECT 1");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(1);
        config.setValidationTimeout(5 * 1000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

    @Bean
    PlatformTransactionManager timeSeriesTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(timeSeriesEntityManager().getObject());
        return transactionManager;
    }
}
