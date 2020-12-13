package com.iflytek.canal.util;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author ztwu4
 * @description 动态数据源，每执行一次数据库，动态获取数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSource();
    }
}
