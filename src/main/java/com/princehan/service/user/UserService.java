package com.princehan.service.user;

import com.mysql.cj.util.StringUtils;
import com.princehan.dao.BaseDao;
import com.princehan.pojo.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface UserService {
        //用户登录
        User login(String userCode, String password);

        //根据用户id修改密码
        boolean updatePwd(int id, String password);

        //查询记录数
        int getUserCount(String username, int userRole);

        //根据条件查询用户列表
        List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);

        //添加
        boolean add(User user);

        //删除用户
        boolean deleteUserById(Integer delId);

        //获取用户
        User getUserById(String id);

        //修改用户
        boolean modify(User user);

        //查询用户
        User selectUserCodeExist(String userCode);

}
