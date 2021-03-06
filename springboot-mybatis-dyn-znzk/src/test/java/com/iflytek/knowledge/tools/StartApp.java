package com.iflytek.knowledge.tools;

import com.iflytek.knowledge.tools.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan(basePackages = "com.iflytek.knowledge.tools")
public class StartApp implements CommandLineRunner {

    @Autowired
    public TestService userService;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(StartApp.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("cehsi");
        userService.getUser2();
        userService.getUserList();
        userService.getUser1(1,10);

    }
}
