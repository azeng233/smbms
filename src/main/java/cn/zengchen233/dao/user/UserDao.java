package cn.zengchen233.dao.user;

import cn.zengchen233.pojo.Role;
import cn.zengchen233.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    //得到要登陆的用户
    public User getLoginUser(Connection connection, String userCode) throws Exception;

    //修改当前用户密码
    public int updatePwd(Connection connection, String userCode, String userPassword) throws Exception;

    //查询用户总数
    public int getUserCount(Connection connection, String userName, int userRole) throws Exception;

    //通过条件查询userList
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws Exception;

    //增加用户信息
    public int add(Connection connection, User user)throws Exception;

    //通过userId删除user
    public int deleteUserById(Connection connection, Integer delId)throws Exception;

    //修改用户信息
    public int modify(Connection connection, User user)throws Exception;

    public User getUserById(Connection connection, String id) throws Exception;

}
