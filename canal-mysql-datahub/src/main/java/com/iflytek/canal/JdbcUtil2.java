package com.iflytek.canal;

import com.iflytek.canal.util.DataSource;
import com.iflytek.canal.util.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class JdbcUtil2 {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public JdbcUtil2(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @DataSource(value = DataSourceType.db2)
    public List<String> getWorkName(String oid) throws Exception {
        List<String> result = new ArrayList<>();
        String sql = String.format("SELECT TASKNAME from wf_generworkflow where WORKID = '%s' limit 1", oid);
        System.out.println(sql);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> rs = list.get(i);
            String workname = rs.get("TASKNAME").toString();
            System.out.println(workname);
            result.add(workname);
        }
        System.out.println(result);
        return result;
    }

    @DataSource(value = DataSourceType.db2)
    public List<String> getControlName(String controlId) throws Exception {
        List<String> result = new ArrayList<>();
        String sql = String.format("SELECT name from wf_form_control where id = '%s' limit 1", controlId);
        System.out.println(sql);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> rs = list.get(i);
            String controlName = rs.get("name").toString();
            System.out.println(controlName);
            result.add(controlName);
        }
        System.out.println(result);
        return result;
    }

}
