package cn.zengchen233.dao.user;

import cn.zengchen233.dao.BaseDao;
import cn.zengchen233.pojo.Role;
import cn.zengchen233.pojo.User;
import com.mysql.cj.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    //得到要登陆的用户
    @Override
    public User getLoginUser(Connection connection, String userCode) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;

        if (connection != null) {
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};
            rs = BaseDao.execute(connection, pstm, sql, params, rs);

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResources(null, pstm, rs);
            //connection不用关,连接可能存在业务
        }
        return user;
    }

    @Override
    public int updatePwd(Connection connection, String userCode, String userPassword) throws Exception {
        PreparedStatement pstm = null;
        int execute = 0;

        if (connection != null) {
            String sql = "update smbms_user set userPassword = ? where userCode = ?";
            Object[] params = {userPassword, userCode};
            execute = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResources(null, pstm, null);
        }
        return execute;
    }

    //根据用户名或者角色查询用户总数(最难理解的sql)
    @Override
    public int getUserCount(Connection connection, String userName, int userRole) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;

        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u,smbms_role r where u.userRole = r.id");
            ArrayList<Object> list = new ArrayList<Object>();//存放我们的参数

            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");
                list.add("%" + userName + "%");//index = 0
            }

            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                list.add(userRole);//index = 1
            }

            //把list转化为数组
            Object[] params = list.toArray();
            System.out.println("sql ----> " + sql.toString());

            rs = BaseDao.execute(connection, pstm, sql.toString(), params, rs);
            if (rs.next()) {
                count = rs.getInt("count");//从结果集获取最终的数量
            }
            BaseDao.closeResources(null, pstm, rs);
        }
        return count;
    }

    //获取用户列表
    @Override
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<User>();

        if(connection != null){
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");
            List<Object> list = new ArrayList<Object>();
            if(!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and u.userName like ?");
                list.add("%"+userName+"%");
            }
            if(userRole > 0){
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            sql.append(" order by creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo-1)*pageSize;
            list.add(currentPageNo);
            list.add(pageSize);

            Object[] params = list.toArray();
            System.out.println("sql ----> " + sql.toString());
            rs = BaseDao.execute(connection, pstm, sql.toString(), params, rs);
            while(rs.next()){
                User _user = new User();
                _user.setId(rs.getInt("id"));
                _user.setUserCode(rs.getString("userCode"));
                _user.setUserName(rs.getString("userName"));
                _user.setGender(rs.getInt("gender"));
                _user.setBirthday(rs.getDate("birthday"));
                _user.setPhone(rs.getString("phone"));
                _user.setUserRole(rs.getInt("userRole"));
                _user.setUserRoleName(rs.getString("userRoleName"));
                userList.add(_user);
            }
            BaseDao.closeResources(null, pstm, rs);
        }
        return userList;
    }

}
