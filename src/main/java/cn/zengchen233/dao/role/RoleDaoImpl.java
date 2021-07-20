package cn.zengchen233.dao.role;

import cn.zengchen233.dao.BaseDao;
import cn.zengchen233.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao {
    //获取角色列表
    @Override
    public List<Role> getRoleList(Connection connection) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Role> roleList = new ArrayList<Role>();

        if (connection != null) {
            String sql = "select * from smbms_role";
            Object[] params = {};
            rs = BaseDao.execute(connection, pstm, sql, params, rs);

            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setRoleCode(rs.getString("roleCode"));
                role.setRoleName(rs.getString("roleName"));
                roleList.add(role);
            }
            BaseDao.closeResources(null, pstm, rs);
        }
        return roleList;
    }
}
