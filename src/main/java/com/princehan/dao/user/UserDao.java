package com.princehan.dao.user;

import com.princehan.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @Description
 * @Author:PrinceHan
 * @CreateTime:2022/3/6 08:49
 */
public interface UserDao {

        //查询登录用户
        User getLoginUser(Connection con, String userCode) throws SQLException;

        //修改当前用户密码
        int updatePwd(Connection con, int id, String password) throws SQLException;

        //查询用户整数
        int getUserCount(Connection con, String username, int userRole) throws SQLException;

        //获取用户列表
        List<User> getUserList(Connection con, String userName, int userRole, int currentPageNo, int pageSize) throws Exception;

        //添加用户
        int add(Connection con, User user) throws Exception;

        //删除用户
        int deleteUserById(Connection con, Integer delId) throws Exception;

        //获取用户
        User getUserById(Connection con, String id) throws Exception;

        //修改
        int modify(Connection con, User user) throws Exception;
        
}
