package cn.zengchen233.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

//操作数据库的公共类
public class BaseDao {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    //静态代码块，类加载的时候就初始化
    static {
        Properties properties = new Properties();
        //通过类加载器读取对应的资源
        //云服务器
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("database.properties");
        //本地服务器
        // InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("local.properties");

        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    //获取数据库链接
    public static Connection getConnection() {
        Connection connection = null;
        try {
            System.out.println("数据库初始化中...");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("数据库连接成功!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    //编写查询公共类
    public static ResultSet execute(Connection connection, String sql, Object[] params, ResultSet resultSet, PreparedStatement preparedStatement) throws SQLException {
        //用到了预编译，不需要传参,直接执行
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            //占位符从1开始，但是数组从0开始 可能有很多问号 问号从1开始
            preparedStatement.setObject(i + 1,params[i]);
        }
        return preparedStatement.executeQuery();
    }

    //编写增删改公共方法
    public static int execute(Connection connection, String sql, Object[] params, PreparedStatement preparedStatement) throws SQLException {
        //用到了预编译，不需要传参,直接执行
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            //占位符从1开始，但是数组从0开始 可能有很多问号 问号从1开始
            preparedStatement.setObject(i + 1,params[i]);
        }
        return preparedStatement.executeUpdate();
    }

    //释放资源
    public static boolean closeResources(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        boolean flag = true;
        if(resultSet != null){
            try {
                resultSet.close();
                resultSet = null;//GC回收
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        if(preparedStatement != null){
            try {
                preparedStatement.close();
                preparedStatement = null;//GC回收
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        if(connection != null){
            try {
                connection.close();
                connection = null;//GC回收
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        return true;
    }
}

