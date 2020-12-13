package com.iflytek.knowledge.tools.dao;

import com.iflytek.knowledge.tools.dto.Group;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface GroupMapper {
    List<Group> selectGroup();
}
