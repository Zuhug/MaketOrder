package com.princehan.dao.role;

import com.princehan.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public interface RoleDao {
        //获取角色列表
        List<Role> getRoleList(Connection con) throws SQLException;
}
