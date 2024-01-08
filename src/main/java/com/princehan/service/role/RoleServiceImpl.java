package com.princehan.service.role;

import com.princehan.dao.BaseDao;
import com.princehan.dao.role.RoleDao;
import com.princehan.dao.role.RoleDaoImpl;
import com.princehan.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class RoleServiceImpl implements RoleService{

    //引入Dao
    private RoleDao roleDao;

    public RoleServiceImpl() {
        roleDao = new RoleDaoImpl();
    }

    @Override
    public List<Role> getRoleList() {
        Connection con = null;
        List<Role> roleList = null;
        try {
            con = BaseDao.getConnection();
            roleList = roleDao.getRoleList(con);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return roleList;
    }
}
