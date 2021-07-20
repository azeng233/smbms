package cn.zengchen233.service.user;

import cn.zengchen233.pojo.User;

import java.util.List;

public interface UserService {
    //用户登录
    public User login(String userCode, String userPassword);

    //根据usercode修改密码
    public boolean updatePwd(String userCode, String userPassword);

    //查询用户数量
    public int getUserCount(String userName, int userRole);

    //根据条件查询用户列表
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);

}
