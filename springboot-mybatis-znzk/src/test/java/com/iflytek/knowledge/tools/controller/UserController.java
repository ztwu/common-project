package com.iflytek.knowledge.tools.controller;

import com.iflytek.knowledge.tools.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    public TestService test2Service;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home() {
        test2Service.getUserList();
        return "你好，Spring Boot";
    }

}