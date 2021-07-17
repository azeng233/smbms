package cn.zengchen233.dao.user;

import cn.zengchen233.pojo.User;

import java.sql.Connection;

public interface UserDao {
    //得到要登陆的用户
    public User getLoginUser(Connection connection, String userCode) throws Exception;

}
