package com.onlyonegames.util.replication;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

//@AllArgsConstructor
//@Configuration
public class DataSourceConfig {

//    private final DatabaseProperty databaseProperty;
//
//    public DataSource createDataSource(String url) {
//        HikariConfig hikariConfig = new HikariConfig();
//        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        hikariConfig.setJdbcUrl(url);
//        hikariConfig.setUsername(databaseProperty.getUsername());
//        hikariConfig.setPassword(databaseProperty.getPassword());
//        hikariConfig.setAutoCommit(false);
//        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
//        return dataSource;
//    }
//
//    @Bean
//    public DataSource routingDataSource() {
//        ReplicationRoutingDataSource replicationRoutingDataSource = new ReplicationRoutingDataSource();
//
//        DataSource master = createDataSource(databaseProperty.getUrl());
//
//        Map<Object, Object> dataSourceMap = new LinkedHashMap<>();
//        dataSourceMap.put("master", master);
//
//        databaseProperty.getSlaveList().forEach(slave -> {
//            dataSourceMap.put(slave.getName(), createDataSource(slave.getUrl()));
//        });
//
//        replicationRoutingDataSource.setTargetDataSources(dataSourceMap);
//        replicationRoutingDataSource.setDefaultTargetDataSource(master);
//        return replicationRoutingDataSource;
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        return new LazyConnectionDataSourceProxy(routingDataSource());
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//        entityManagerFactoryBean.setDataSource(dataSource());
//        entityManagerFactoryBean.setPackagesToScan("com.onlyonegames");
//        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
//
//        return entityManagerFactoryBean;
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
//        JpaTransactionManager tm = new JpaTransactionManager();
//        tm.setEntityManagerFactory(entityManagerFactory);
//        return tm;
//    }
}
