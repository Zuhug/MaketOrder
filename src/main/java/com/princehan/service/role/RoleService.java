package com.princehan.service.role;

import com.princehan.dao.role.RoleDao;
import com.princehan.dao.role.RoleDaoImpl;
import com.princehan.pojo.Role;

import java.sql.Connection;
import java.util.List;


public interface RoleService {

    List<Role> getRoleList();
}
