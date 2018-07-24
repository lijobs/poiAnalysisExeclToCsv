package com.poianalysisexecltocsv.demo.util.mybatisutil;

import org.apache.ibatis.jdbc.SqlRunner;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author lizehua 2018/02/22
 * @desc 执行创建orcale数据库外部表的DDl sql语句
 * 先要获得数据库连接注入到该类的构造方法中，
 * 然后调用SqlRunner中相应的方法执行sql语句
 */
@Configuration
public class MybatisExecuteSqlUtil {


    /**
     * 获得数据库连接
     *
     * @return Connection
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.99:1521:orcl", "qarTest", "root");
        return conn;
    }

    /**
     * 获得数据库链接信息
     *
     * @param drive    数据库驱动
     * @param userName 数据库用户名
     * @param password 数据库密码
     * @return 返回数据库链接信息
     * @throws SQLException
     */
    public static Connection getConnection(String drive, String userName, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(drive, userName, password);
        return connection;
    }

    /**
     * 创建SqlRunner的对象
     *
     * @param connection 数据库链接对象信息
     * @return 返回SqlRunner对象
     */
    public static SqlRunner getSQLRunner(Connection connection) {
        SqlRunner sqlRunner = new SqlRunner(connection);
        return sqlRunner;
    }


}
