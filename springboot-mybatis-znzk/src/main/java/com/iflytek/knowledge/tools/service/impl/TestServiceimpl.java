package com.iflytek.knowledge.tools.service.impl;

import com.iflytek.knowledge.tools.entity.Group;
import com.iflytek.knowledge.tools.mapper.GroupMapper;
import com.iflytek.knowledge.tools.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("test2Service")
public class TestServiceimpl implements TestService {

    @Autowired
    private GroupMapper groupMapper;

    public List<Group> getUser() {
        List<Group> list = groupMapper.selectGroup();
        return list;
    }

    public List<Group> getUserList() {
        List<Group> list = groupMapper.selectGroup();
        return list;
    }

}
