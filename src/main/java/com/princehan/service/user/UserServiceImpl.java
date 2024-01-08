package com.princehan.service.user;

import com.princehan.dao.BaseDao;
import com.princehan.dao.user.UserDao;
import com.princehan.dao.user.UserDaoImpl;
import com.princehan.pojo.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {
    //业务层调Dao层
    private UserDao userDao;

    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    //登录用户
    public User login(String userCode, String password) {
        Connection con = null;
        User user = null;
        try {
            con = BaseDao.getConnection();
            //通过业务层调用对应的数据库操作
            user = userDao.getLoginUser(con, userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        if (null != user) {
            if (!user.getUserPassword().equals(password)) {
                user = null;
            }
        }
        return user;
    }

    //修改密码
    @Override
    public boolean updatePwd(int id, String password) {
        Connection con = null;
        PreparedStatement pstm = null;
        con = BaseDao.getConnection();
        boolean flag = false;
        try {
            if (userDao.updatePwd(con, id, password) > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(con, pstm, null);
        }
        return flag;
    }

    //查询记录数
    @Override
    public int getUserCount(String username, int userRole) {
        Connection con = null;
        int count = 0;
        try {
            con = BaseDao.getConnection();
            count = userDao.getUserCount(con, username, userRole);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return count;
    }

    //根据条件查询用户列表
    @Override
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {
        Connection con = null;
        List<User> userList = null;
//        System.out.println("queryUserName ----> " + queryUserName);
//        System.out.println("queryUserName ----> " + queryUserRole);
//        System.out.println("currentPageNo ----> " + currentPageNo);
//        System.out.println("pageSize ----> " + pageSize);
        try {
            con = BaseDao.getConnection();
            userList = userDao.getUserList(con, queryUserName, queryUserRole, currentPageNo, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return userList;
    }

    //添加用户
    @Override
    public boolean add(User user) {
        Connection con = null;
        boolean flag = false;
        try {
            con = BaseDao.getConnection();
            //false 可回滚
            con.setAutoCommit(false);
            int updateRows = userDao.add(con, user);
            con.commit();
            if (updateRows > 0) {
                flag = true;
                System.out.println("Add succeed!");
            } else {
                System.out.println("Add failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                System.out.println("rollback");
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            //在service层进行connection连接的关闭
            BaseDao.closeResource(con, null, null);
        }
        return flag;
    }

    //删除用户
    @Override
    public boolean deleteUserById(Integer delId) {
        Connection con = null;
        boolean flag = false;
        try {
            con = BaseDao.getConnection();
            if (userDao.deleteUserById(con,delId) > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return flag;
    }

    //获取用户id
    @Override
    public User getUserById(String id) {
        User user = null;
        Connection con = null;
        try {
            con = BaseDao.getConnection();
            user = userDao.getUserById(con, id);
        } catch (Exception e) {
            e.printStackTrace();
            user = null;
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return user;
    }

    //修改用户
    @Override
    public boolean modify(User user) {
        Connection con = null;
        boolean flag = false;
        try {
            con = BaseDao.getConnection();
            if (userDao.modify(con, user) > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return flag;
    }

    @Override
    public User selectUserCodeExist(String userCode) {
        Connection con = null;
        User user = null;
        try {
            con = BaseDao.getConnection();
            user = userDao.getLoginUser(con, userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return user;
    }
}
