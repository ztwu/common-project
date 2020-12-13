package com.iflytek.knowledge.tools.service.impl;

import com.iflytek.knowledge.tools.domain.User;
import com.iflytek.knowledge.tools.repository.UserRepository;
import com.iflytek.knowledge.tools.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("testService")
public class TestServiceImpl implements TestService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getUserList() {
        List<User> list = userRepository.findTest();
        System.out.println("数据==");
        System.out.println(list);
        return list;
    }
}
