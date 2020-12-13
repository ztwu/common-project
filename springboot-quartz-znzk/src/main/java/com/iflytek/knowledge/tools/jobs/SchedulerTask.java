package com.iflytek.knowledge.tools.jobs;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import java.util.Date;

@Configuration
@Component
@EnableScheduling
public class SchedulerTask {

    public void start() throws InterruptedException {
        System.out.println("活动开始！！！"+new Date());
    }
}
