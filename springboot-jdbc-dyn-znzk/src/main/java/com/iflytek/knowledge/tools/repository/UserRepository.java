package com.iflytek.knowledge.tools.repository;

import com.iflytek.knowledge.tools.domain.User;
import com.iflytek.knowledge.tools.util.DataSource;
import com.iflytek.knowledge.tools.util.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findTest(){
        List<Object> param = new ArrayList();
        String sql = "select * from User";
        List<User> list = jdbcTemplate.query(sql, param.toArray(), new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setAge(resultSet.getInt("age"));
                user.setName(resultSet.getString("name"));
                return user;
            }
        });
        return list;
    }

    public List<User> findTest2(){
        List<Object> param = new ArrayList();
        String sql = "select * from User";
        List<User> list = jdbcTemplate.query(sql, param.toArray(), new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setAge(resultSet.getInt("age"));
                user.setName(resultSet.getString("name"));
                return user;
            }
        });
        return list;
    }

}
