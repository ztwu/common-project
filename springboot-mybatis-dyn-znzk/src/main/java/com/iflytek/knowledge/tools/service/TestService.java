package com.iflytek.knowledge.tools.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iflytek.knowledge.tools.util.DataSourceType;
import com.iflytek.knowledge.tools.util.DataSource;
import com.iflytek.knowledge.tools.dto.Group;
import com.iflytek.knowledge.tools.dao.GroupMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class TestService {

    @Autowired
    private GroupMapper groupMapper;

    @DataSource(value = DataSourceType.db1)
    public PageInfo<Group> getUser1(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Group> list = groupMapper.selectGroup();
        log.info("=====>>>>> logger()");
        log.error("错误了11");
        log.warn("警告了11");
        for(Group item :list){
            System.out.println(item);
        }
        return new PageInfo<Group>(list);
    }

    @DataSource(value = DataSourceType.db2)
    public List<Group> getUser2() {
        log.error("错误了22");
        log.warn("警告了22");
        List<Group> list = groupMapper.selectGroup();
        for(Group item :list){
            System.out.println(item);
        }
        return list;
    }

    @DataSource(value = DataSourceType.db2)
    public List<Group> getUserList() {
        log.error("错误了22");
        log.warn("警告了22");
        List<Group> list = groupMapper.selectGroup();
        for(Group item :list){
            System.out.println(item);
        }
        return list;
    }

}
