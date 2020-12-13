package com.iflytek.knowledge.tools.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(JpaProperties.class)
@EnableJpaRepositories(value = "com.iflytek.knowledge.tools.repository")
public class JpaEntityManager {

    @Autowired
    private JpaProperties jpaProperties;

    @Resource(name = "dynamicDataSource")
    private DynamicDataSource dynamicDataSource;

    @Bean(name = "entityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(EntityManagerFactoryBuilder builder) {
        Map<String, String> properties = jpaProperties.getProperties();
        return builder
                .dataSource(dynamicDataSource)//关键：注入routingDataSource
                .properties(properties)
                .packages("com.iflytek.knowledge.tools.domain")
                .persistenceUnit("myPersistenceUnit")
                .build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public EntityManagerFactory entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return this.entityManagerFactoryBean(builder).getObject();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactory(builder));
    }
}
