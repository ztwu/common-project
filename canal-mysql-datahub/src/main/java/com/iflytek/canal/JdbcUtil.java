package com.iflytek.canal;

import com.iflytek.canal.util.DataSource;
import com.iflytek.canal.util.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class JdbcUtil {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public JdbcUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @DataSource(value = DataSourceType.db1)
    public Map<String,List<String>> getExistsDestTable(String table, String field, String value, String isUpdate) throws Exception {
        String sql_exists1 = String.format("SELECT distinct coll_coll_table,coll_coll_column,coll_rpt_column " +
                "from coll_coll_rpt " +
                "where coll_rpt_table = '%s' and coll_rpt_column = '%s' and coll_weapon_type = '0' " +
                "and value_type = '0' and coll_coll_table <> ''",table,field);
        String sql_exists2 = String.format("SELECT distinct coll_coll_table,coll_coll_column,coll_rpt_info_column as coll_rpt_column " +
                "from coll_coll_rpt " +
                "where coll_rpt_info_table = '%s' and coll_rpt_info_column = '%s' and coll_weapon_type = '0' " +
                "and value_type = '1' and coll_coll_table <> ''",table,field);
        String sql_exists = sql_exists1 + " union all " +sql_exists2;
        System.out.println(sql_exists);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql_exists);
        Map<String,List<String>> result = new IdentityHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> rs = list.get(i);
            String destTable = rs.get("coll_coll_table").toString();
            String destField = rs.get("coll_coll_column").toString();
            String sourceField = rs.get("coll_rpt_column").toString();
            if(value.length()>0){
                List<String> new_value = new ArrayList<>();
                new_value.add(destField);
                new_value.add(value);
                new_value.add(sourceField);
                new_value.add(isUpdate);
                System.out.println("new_value:========");
                System.out.println(new_value);
                result.put(destTable,new_value);
            }
        }
        System.out.println(result);
        return result;
    }

    @DataSource(value = DataSourceType.db1)
    public Map<String,List<String>> getAppendDestTable(String table) throws Exception {
        String sql_append = String.format("SELECT distinct coll_coll_table,coll_coll_column,coll_default,coll_rpt_column " +
                "from coll_coll_rpt " +
                "where coll_rpt_table = '%s' and coll_weapon_type = '1'",table);
        System.out.println(sql_append);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql_append);
        Map<String, List<String>> result = new IdentityHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> rs = list.get(i);
            String destTable = rs.get("coll_coll_table").toString();
            String destField = rs.get("coll_coll_column").toString();
            String value = rs.get("coll_default").toString();
            List<String> new_value = new ArrayList<>();
            if(value.length()>0) {
                new_value.add(destField);
                new_value.add(value);
                new_value.add(destField);
                new_value.add("1");
                result.put(destTable, new_value);
            }
        }
        System.out.println(result);
        return result;
    }

    @DataSource(value = DataSourceType.db1)
    public List<String> getWqId(String oid) throws Exception {
        List<String> result = new ArrayList<>();
        String sql = String.format("select distinct wq_id from coll_wq_oid where oid = '%s' limit 1", oid);
        System.out.println(sql);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> rs = list.get(i);
            String wqid = rs.get("wq_id").toString();
            result.add(wqid);
        }
        System.out.println(result);
        return result;
    }

    @DataSource(value = DataSourceType.db1)
    public Map<String,List<String>> getExistsAppendDestTable(String table, String field, String sourcevalue, String isUpdate) throws Exception {
        String sql_append = String.format("SELECT distinct coll_coll_table,coll_coll_column,coll_rpt_column_name,coll_rpt_column " +
                "from coll_coll_rpt " +
                "where coll_rpt_table = '%s' and coll_weapon_type = '2' and coll_rpt_column = '%s'",table,field);
        System.out.println(sql_append);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql_append);
        Map<String, List<String>> result = new IdentityHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> rs = list.get(i);
            String destTable = rs.get("coll_coll_table").toString();
            String destField = rs.get("coll_coll_column").toString();
            String value = rs.get("coll_rpt_column_name").toString();
            String sourceField = rs.get("coll_rpt_column").toString();
            List<String> new_value = new ArrayList<>();
            if(sourcevalue.length()>0) {
                new_value.add(destField);
                new_value.add(value);
                new_value.add(sourceField);
                new_value.add("1");
                result.put(destTable, new_value);
            }
        }
        System.out.println(result);
        return result;
    }

    @DataSource(value = DataSourceType.db1)
    public void execUpdateSql(String sql){
        int result = jdbcTemplate.update(sql);
        if(result!=0){
            System.out.println("插入完成");
        }
    }

    @DataSource(value = DataSourceType.db1)
    public Boolean execCountSql(String sql){
        int count = jdbcTemplate.queryForObject(sql, Integer.class);
        if(count == 0){
            return true;
        }else{
            return false;
        }
    }

}
