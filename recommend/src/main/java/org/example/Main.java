package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

// 独立测试类：用于连接 Hive 并验证基础查询能力
public class Main {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    public static void main(String[] args) throws Exception {
        Class.forName(driverName);

        // 连接 HiveServer2
        Connection con = DriverManager.getConnection(
                "jdbc:hive2://202.207.12.217:10000/default");

        Statement stmt = con.createStatement();
        String sql = "show tables";
        ResultSet res = stmt.executeQuery(sql);

        while (res.next()) {
            System.out.println(res.getString(1));
        }
        con.close();
    }
}