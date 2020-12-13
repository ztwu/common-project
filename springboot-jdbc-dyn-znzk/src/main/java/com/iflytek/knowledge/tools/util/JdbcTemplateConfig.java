package com.iflytek.knowledge.tools.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ztwu4
 * @description
 */
@Configuration
public class JdbcTemplateConfig {
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

    @Bean("jdbcTemplate")
    public JdbcTemplate getJdbcTemplate(DynamicDataSource dataSource){
        System.out.println("生成JdbcTemplate===");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }

}
