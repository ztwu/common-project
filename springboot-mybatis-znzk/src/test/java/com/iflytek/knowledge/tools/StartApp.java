package com.iflytek.knowledge.tools;

import com.iflytek.knowledge.tools.entity.Group;
import com.iflytek.knowledge.tools.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@ServletComponentScan
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.iflytek.knowledge.tools")
public class StartApp implements CommandLineRunner {

    @Autowired
    public TestService test2Service;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(StartApp.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("cehsi");
        List<Group> list = test2Service.getUserList();
        for(Group group:list){
            System.out.println(group);
        }

    }
}
