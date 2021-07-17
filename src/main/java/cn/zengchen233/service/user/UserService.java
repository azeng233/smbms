package cn.zengchen233.service.user;

import cn.zengchen233.pojo.User;

public interface UserService {
    //用户登录
    public User login(String userCode, String userPassword);
}
