package com.iflytek.knowledge.tools.mapper;

import com.iflytek.knowledge.tools.entity.Group;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMapper {
    List<Group> selectGroup();
}
