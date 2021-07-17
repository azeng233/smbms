package cn.zengchen233.service.user;

import cn.zengchen233.dao.BaseDao;
import cn.zengchen233.dao.user.UserDao;
import cn.zengchen233.dao.user.UserDaoImpl;
import cn.zengchen233.pojo.User;
import org.junit.Test;

import java.sql.Connection;

public class UserServiceImpl implements UserService {

    //业务层都会调用Dao层,所以我们要引用Dao层
    private UserDao userDao;

    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    @Override
    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;

        try {
            connection = BaseDao.getConnection();
            //通过业务层调用对应的具体的数据库
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection,null,null);
        }
        return user;
    }

    // @Test
    // public void test() {
    //     UserServiceImpl userService = new UserServiceImpl();
    //     User admin = userService.login("admin", "1234567");
    //     System.out.println(admin.getUserPassword());
    // }
}
