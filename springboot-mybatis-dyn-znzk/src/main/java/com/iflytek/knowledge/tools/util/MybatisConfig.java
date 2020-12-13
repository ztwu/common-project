package com.iflytek.knowledge.tools.util;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ztwu4
 * @description
 */
@Configuration
public class MybatisConfig {
    @Bean("db1DataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSource db1DataSource() {
        DataSource build = DataSourceBuilder.create().build();
        return build;
    }

    @Bean("db2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    public DataSource db2DataSource(){
        DataSource build = DataSourceBuilder.create().build();
        return build;
    }

    @Bean
    public DynamicDataSource dataSource(@Qualifier("db1DataSource") DataSource db1DataSource,
                                        @Qualifier("db2DataSource") DataSource db2DataSource) {
        Map<Object, Object> map = new HashMap<>();
        map.put(DataSourceType.db1, db1DataSource);
        map.put(DataSourceType.db2, db2DataSource);

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(map);
        dynamicDataSource.setDefaultTargetDataSource(db1DataSource);

        return dynamicDataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DynamicDataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dynamicDataSource);
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/*.xml");
        factoryBean.setMapperLocations(resources);
        return factoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DynamicDataSource dynamicDataSource){
        return new DataSourceTransactionManager(dynamicDataSource);
    }
}
