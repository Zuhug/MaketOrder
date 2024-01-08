package com.princehan.dao.role;

import com.princehan.dao.BaseDao;
import com.princehan.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class RoleDaoImpl implements RoleDao{

    //获取角色列表
    @Override
    public List<Role> getRoleList(Connection con) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        ArrayList<Role> roleList = new ArrayList<>();
        if (con != null) {
            String sql = "select * from smbms_role";
            Object[] params = {};
            rs = BaseDao.execute(con, pstm, rs, sql, params);
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setRoleCode(rs.getString("roleCode"));
                role.setRoleName(rs.getString("roleName"));
                roleList.add(role);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return roleList;
    }
}
