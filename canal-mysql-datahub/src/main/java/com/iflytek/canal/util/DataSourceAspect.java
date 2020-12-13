package com.iflytek.canal.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.*;

@Slf4j
@Aspect
@Component
public class DataSourceAspect {
    @Before("@annotation(ds)")
    public void beforeDataSource(DataSource ds) {
        DataSourceType value = ds.value();
        System.out.println("===========设置数据源============："+value);
        DataSourceContextHolder.setDataSource(value);
    }

    @After("@annotation(ds)")
    public void afterDataSource(DataSource ds){
        DataSourceContextHolder.clearDataSource();
    }
}
