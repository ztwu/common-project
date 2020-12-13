package com.iflytek.knowledge.tools;

import com.iflytek.knowledge.tools.jobs.SchedulerJob1;
import com.iflytek.knowledge.tools.jobs.SchedulerJob2;
import org.quartz.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

@EnableAutoConfiguration
@ComponentScan(basePackages = "com.iflytek.knowledge.tools")
public class StartApp implements CommandLineRunner {

    @Resource(name = "multitaskScheduler")
    private Scheduler scheduler;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(StartApp.class);
        app.run(args);
    }

    public void startTask1() throws SchedulerException {
        //配置定时任务对应的Job，这里执行的是ScheduledJob类中定时的方法
        JobDetail jobDetail = JobBuilder.newJob(SchedulerJob1.class)
                .withIdentity("job1", "group1").build();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0/3 * * * * ?");
        // 每3s执行一次
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

    public void startTask2() throws SchedulerException {
        //配置定时任务对应的Job，这里执行的是ScheduledJob类中定时的方法
        JobDetail jobDetail = JobBuilder.newJob(SchedulerJob2.class)
                .withIdentity("job2", "group1")
                .build();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0/6 * * * * ?");
        // 每3s执行一次
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger2", "group1")
                .withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

    @Override
    public void run(String... args) throws Exception {
        startTask1();
        startTask2();

    }
}
