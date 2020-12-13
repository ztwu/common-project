package com.iflytek.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
@Slf4j
public class CanalUtil {

    @Value("${canal.service.host}")
    private String host;
    @Value("${canal.service.port}")
    private int port;

    @Autowired
    JdbcUtil jdbcUtil;
    @Autowired
    JdbcUtil2 jdbcUtil2;

    public CanalConnector getCanalConnector(){
        // 创建链接
        log.info(host);
        log.info(Integer.toString(port));
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(host,port), "example", "", "");
        return connector;
    }

    public void printEntry(List<CanalEntry.Entry> entrys) {
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry
                    .EntryType
                    .TRANSACTIONEND) {
                continue;
            }
 
            CanalEntry.RowChange rowChage = null;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }
 
            CanalEntry.EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));

            String table = entry.getHeader().getTableName();
 
            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == CanalEntry.EventType.DELETE) {
//                    printColumn(rowData.getBeforeColumnsList());
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());

                    String op_type_del = "delete";
                    sysData(rowData.getAfterColumnsList(),table,op_type_del);

                    String op_type_ins = "insert";
                    sysData(rowData.getAfterColumnsList(),table,op_type_ins);
//                    createInsertSql(rowData.getAfterColumnsList(),table);
                } else {
                    System.out.println("-------> before");
                    printColumn(rowData.getBeforeColumnsList());
                    String op_type_del = "delete";
                    sysData(rowData.getBeforeColumnsList(),table,op_type_del);
                    System.out.println("-------> after");
                    printColumn(rowData.getAfterColumnsList());
                    String op_type_ins = "insert";
                    sysData(rowData.getAfterColumnsList(),table,op_type_ins);
//                    createUpdateSql(rowData.getAfterColumnsList(),table);
                }
            }
        }
    }

    private void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            log.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

    private Boolean isSetWqId(Map<String,List<String>> datas, String table){
        Iterator iter = datas.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            if(table.equalsIgnoreCase(key)){
                List<String> value = (List<String>) entry.getValue();
                String field = value.get(0);
                String ve = value.get(1);
                if ("wq_id".equals(field)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sysData(List<CanalEntry.Column> columns, String table,String op_type) {
        Map<String,List<String>> datas = new IdentityHashMap<>();
        try {
            datas.putAll(jdbcUtil.getAppendDestTable(table));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String oid = "";
        for(int i =0; i<columns.size(); i++){
            CanalEntry.Column column = columns.get(i);
            String field = column.getName();
            String value = column.getValue();
            if("oid".equalsIgnoreCase(field)){
                oid += value;
            }
            try {
                String temp = "1";
                datas.putAll(jdbcUtil.getExistsDestTable(table,field, value,temp));
                datas.putAll(jdbcUtil.getExistsAppendDestTable(table,field,value,temp));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
        Set<String> keys = new HashSet();
        for(String key : datas.keySet()) {
            keys.add(key);
        }
        if(keys.size() >0) {
            for (String keyitem : keys) {
                Map<String,List<String>> rs = new IdentityHashMap<>();
                log.info("======================================================");
                log.info(keyitem);
                System.out.println(datas);
                boolean isWqId = false;
                if (true) {
                    if (isSetWqId(datas,keyitem)){
                        log.info("======================================================已经配置wq_id");
                    }else{
                        log.info(String.format("======================================================没有配置wq_id==oid%s",oid));
                        try {
                            List<String> wqids = jdbcUtil.getWqId(oid);
                            if(wqids.size()>0){
                                String destfield = "wq_id";
                                String destvalue = wqids.get(0);
                                String sourcefield = "wq_id";
                                log.info(String.format("wq_id_1==================%s",destvalue));
                                List<String> temp = new ArrayList<>();
                                temp.add(destfield);
                                temp.add(destvalue);
                                rs.put(sourcefield,temp);
                            }else{
                                String destfield = "wq_id";
                                String destvalue = "";
                                String sourcefield = "wq_id";
                                log.info(String.format("wq_id_1==================%s",destvalue));
                                List<String> temp = new ArrayList<>();
                                temp.add(destfield);
                                temp.add(destvalue);
                                rs.put(sourcefield,temp);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Iterator iter = datas.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        String key = (String) entry.getKey();
                        List<String> value = (List<String>) entry.getValue();
                        if (keyitem.equals(key)) {
                            //每张表的数据
                            if("wq_id".equalsIgnoreCase(value.get(0))){
                                try {
                                    String wqid ;
                                    if(value.get(1).length()>0){
                                        List<String> workname = jdbcUtil2.getWorkName(value.get(1));
                                        MessageDigest md = MessageDigest.getInstance("MD5");
                                        md.update(workname.get(0).getBytes());
                                        wqid = new BigInteger(1, md.digest()).toString(16);
                                        isWqId = true;

                                        String isSql = String.format("select count(1) from coll_wq_oid where wq_id = '%s' and oid = '%s'", wqid, oid);
                                        if(jdbcUtil.execCountSql(isSql)){
                                            String sql = String.format("insert into coll_wq_oid(wq_id,oid) values('%s','%s')", wqid, oid);
                                            log.info(sql);
                                            jdbcUtil.execUpdateSql(sql);
                                        }
                                    }else{
                                        wqid = "";
                                    }
                                    String destfield = value.get(0);
                                    log.info(String.format("wq_id_2==================%s",value.get(1)));
                                    log.info(String.format("wq_id_2==================%s",wqid));
                                    String destvalue = wqid;
                                    String sourcefield = value.get(2);
                                    List<String> temp = new ArrayList<>();
                                    temp.add(destfield);
                                    temp.add(destvalue);
                                    rs.put(sourcefield,temp);

                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else {
                                String destfield = value.get(0);
                                String destvalue = value.get(1);
                                String sourcefield = value.get(2);
                                if ("controlId".equalsIgnoreCase(sourcefield) && "fight_type".equalsIgnoreCase(destfield)){
                                    try {
                                        List<String> workname = jdbcUtil2.getControlName(destvalue);
                                        List<String> temp = new ArrayList<>();
                                        temp.add(destfield);
                                        temp.add(workname.get(0));
                                        rs.put(sourcefield,temp);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    List<String> temp = new ArrayList<>();
                                    temp.add(destfield);
                                    temp.add(destvalue);
                                    rs.put(sourcefield,temp);
                                }
                            }
                        }
                    }
                }
                log.info("表的数据：========");
                System.out.println(rs);

                Set<String> ks = new HashSet();
                for(String key : rs.keySet()) {
                    ks.add(key);
                }
                if(ks.size()>0) {
                    List<List<List<String>>> batchs = new ArrayList<>();
                    List<List<String>> comes = new ArrayList<>();
                    for (String kitem : ks) {
                        List<List<String>> lies = new ArrayList<>();
                        log.info(kitem);
                        System.out.println(rs);
                        Iterator iter = rs.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next();
                            String key = (String) entry.getKey();
                            List<String> value = (List<String>) entry.getValue();
                            if (kitem.equals(key)) {
                                lies.add(value);
                            }
                        }
                        if(lies.size()>1){
                            batchs.add(lies);
                        }else{
                            comes.add(lies.get(0));
                        }
                    }
                    log.info("batchs数据：===");
                    System.out.println(batchs);
                    if(batchs.size()>0){
                        for(List<List<String>> it : batchs){
                            it.addAll(comes);
                            List<String> where = new ArrayList<>();
                            log.info("最新数据：===");
                            System.out.println(it);
                            List<String> fields = new ArrayList<>();
                            List<String> vales = new ArrayList<>();
                            for(List<String> iitem : it){
                                fields.add(iitem.get(0));
                                vales.add(iitem.get(1));
                                where.add(String.format(" `%s` = '%s' ", iitem.get(0), iitem.get(1).replace("'","\\'")));
                            }
                            log.info("fields:=======");
                            System.out.println(fields);
                            log.info("vales:=======");
                            System.out.println(vales);
                            System.out.println(vales.size());

                            String tempsql = String.format("insert into %s", keyitem);
                            StringBuffer fieldbuff = new StringBuffer(tempsql);
                            fieldbuff.append("(");
                            for (int i = 0; i < fields.size(); i++) {
                                String field = fields.get(i);
                                if (i == fields.size() - 1) {
                                    fieldbuff.append("`"+field+"`");
                                } else {
                                    fieldbuff.append("`"+field+"`" + ",");
                                }
                            }
                            fieldbuff.append(")");
                            fieldbuff.append(" ");

                            StringBuffer valuesbuff = new StringBuffer("values(");
                            for (int i = 0; i < vales.size(); i++) {
                                String vale = vales.get(i);
                                if (i == vales.size() - 1) {
                                    valuesbuff.append(String.format("'%s'",vale.replace("'","\\'")));
                                } else {
                                    valuesbuff.append(String.format("'%s',",vale.replace("'","\\'")));
                                }
                            }
                            valuesbuff.append(")");

                            System.out.println("hhh====================================================================================hhh");
                            System.out.println(keyitem);
                            System.out.println(isWqId);
                            System.out.println(fields.contains("wq_name"));
                            if("coll_weapon_main".equalsIgnoreCase(keyitem) &&  isWqId == false){
                                continue;
                            }
                            if("coll_weapon_main".equalsIgnoreCase(keyitem) &&  fields.contains("wq_name") == false){
                                continue;
                            }

                            if("insert".equalsIgnoreCase(op_type)) {
                                String sql = fieldbuff.toString() + valuesbuff.toString();
                                log.info("sql:=======");
                                log.info(sql);
                                jdbcUtil.execUpdateSql(sql);
                            }

                            if("delete".equalsIgnoreCase(op_type)){
                                String deltemp = String.format("delete from %s",keyitem);
                                StringBuffer deletebuff = new StringBuffer(deltemp);
                                if(where.size()>0){
                                    deletebuff.append(" where ");
                                    for(int i=0;i<where.size();i++){
                                        String value = where.get(i);
                                        if(i==where.size()-1){
                                            deletebuff.append(value);
                                        }else {
                                            deletebuff.append(value+" and ");
                                        }
                                    }
                                    String deletesql = deletebuff.toString();
                                    log.info("sql:=======");
                                    log.info(deletesql);
                                    jdbcUtil.execUpdateSql(deletesql);
                                }
                            }
                        }
                    }else {
                        List<String> where = new ArrayList<>();
                        log.info("最新数据：===");
                        System.out.println(comes);
                        List<String> fields = new ArrayList<>();
                        List<String> vales = new ArrayList<>();
                        for(List<String> iitem : comes){
                            fields.add(iitem.get(0));
                            vales.add(iitem.get(1));
                            where.add(String.format(" `%s` = '%s' ", iitem.get(0), iitem.get(1).replace("'","\\'")));
                        }
                        log.info("fields:=======");
                        System.out.println(fields);
                        log.info("vales:=======");
                        System.out.println(vales);
                        System.out.println(vales.size());

                        String tempsql = String.format("insert into %s", keyitem);
                        StringBuffer fieldbuff = new StringBuffer(tempsql);
                        fieldbuff.append("(");
                        for (int i = 0; i < fields.size(); i++) {
                            String field = fields.get(i);
                            if (i == fields.size() - 1) {
                                fieldbuff.append("`"+field+"`");
                            } else {
                                fieldbuff.append("`"+field+"`" + ",");
                            }
                        }
                        fieldbuff.append(")");
                        fieldbuff.append(" ");

                        StringBuffer valuesbuff = new StringBuffer("values(");
                        for (int i = 0; i < vales.size(); i++) {
                            String vale = vales.get(i);
                            if (i == vales.size() - 1) {
                                valuesbuff.append(String.format("'%s'",vale.replace("'","\\'")));
                            } else {
                                valuesbuff.append(String.format("'%s',",vale.replace("'","\\'")));
                            }
                        }
                        valuesbuff.append(")");

                        System.out.println("hhh====================================================================================hhh");
                        System.out.println(keyitem);
                        System.out.println(isWqId);
                        if("coll_weapon_main".equalsIgnoreCase(keyitem) &&  isWqId == false){
                            continue;
                        }
                        if("coll_weapon_state".equalsIgnoreCase(keyitem) &&  isWqId == false){
                            continue;
                        }
                        if("coll_weapon_main".equalsIgnoreCase(keyitem) &&  fields.contains("wq_name") == false){
                            continue;
                        }
                        if("coll_weapon_state".equalsIgnoreCase(keyitem) &&  fields.size() == 1){
                            continue;
                        }

                        if("insert".equalsIgnoreCase(op_type)) {
                            String sql = fieldbuff.toString() + valuesbuff.toString();
                            log.info("sql:=======");
                            log.info(sql);
                            jdbcUtil.execUpdateSql(sql);
                        }

                        if("delete".equalsIgnoreCase(op_type)){
                            String deltemp = String.format("delete from %s",keyitem);
                            StringBuffer deletebuff = new StringBuffer(deltemp);
                            if(where.size()>0){
                                deletebuff.append(" where ");
                                for(int i=0;i<where.size();i++){
                                    String value = where.get(i);
                                    if(i==where.size()-1){
                                        deletebuff.append(value);
                                    }else {
                                        deletebuff.append(value+" and ");
                                    }
                                }
                                String deletesql = deletebuff.toString();
                                log.info("sql:=======");
                                log.info(deletesql);
                                jdbcUtil.execUpdateSql(deletesql);
                            }
                        }
                    }
                }
            }
        }
    }

}