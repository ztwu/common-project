package com.iflytek.db.proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Test {
    public static void main(String[] args){
        try {
            String name = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:43306/test?useSSL=FALSE&serverTimezone=UTC";
            String user = "root";
            String password = "admin123";
            Class.forName(name);//指定连接类型
            Connection conn = DriverManager.getConnection(url, user, password);//url为代理服务器的地址
            PreparedStatement pst = conn.prepareStatement("select * from user;");//准备执行语句
            PreparedStatement pst2 = conn.prepareStatement("select count(1) from user;");
            ResultSet resultSet = pst.executeQuery();
            pst2.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getLong(1) + ": " + resultSet.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
