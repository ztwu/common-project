package com.iflytek.knowledge.tools;

import com.iflytek.knowledge.tools.entity.Group;
import com.iflytek.knowledge.tools.mapper.GroupMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MapperTest {

    @Resource
    GroupMapper groupMapper;

    @Test
    public void testInsert(){
        List<Group> list = groupMapper.selectGroup();
        for(Group group:list){
            System.out.println(group);
        }
    }
}
