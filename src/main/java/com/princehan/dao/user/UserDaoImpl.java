package com.princehan.dao.user;

import com.mysql.cj.util.StringUtils;
import com.princehan.dao.BaseDao;
import com.princehan.pojo.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    //得到要登陆的用户
    @Override
    public User getLoginUser(Connection con, String userCode) {
        PreparedStatement pre = null;
        ResultSet rs = null;
        User user = null;
        try {
            if (con != null) {
                String sql = "select * from smbms_user where userCode=?";
                Object[] params = {userCode};

                rs = BaseDao.execute(con, pre, rs, sql, params);
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
                BaseDao.closeResource(null, pre, rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    //修改当前用户密码
    @Override
    public int updatePwd(Connection con, int id, String password) throws SQLException {
        PreparedStatement pstm = null;
        int execute = 0;
        if (con != null) {
            String sql = "update smbms_user set userPassword = ? where id = ?";
            Object[] params = {password, id};
            execute = BaseDao.execute(con, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return execute;
    }

    //根据用户名和用户角色查询用户总数
    @Override
    public int getUserCount(Connection con, String username, int userRole) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;
        if (con != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u, smbms_role r where u.userRole = r.id");
            //存放参数
            ArrayList<Object> list = new ArrayList<>();

            if (!StringUtils.isNullOrEmpty(username)) {
                sql.append(" and u.userName like ?");
                //%代表多个字符 _代表一个字符
                list.add("%" + username + "%");
            }

            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            Object[] params = list.toArray();
            System.out.println("UserDaoImpl->getUserCount:" + sql.toString());
            rs = BaseDao.execute(con, pstm, rs, sql.toString(), params);
            if (rs.next()) {
                count = rs.getInt("count");
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return count;
    }

    //获取用户列表
    @Override
    public List<User> getUserList(Connection con, String userName, int userRole, int currentPageNo, int pageSize) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<User>();
        if (con != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");
            List<Object> list = new ArrayList<Object>();
            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");
                list.add("%" + userName + "%");
            }
            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            //在mysql数据库中，分页使用 limit startIndex，pageSize ; 总数
            sql.append(" order by creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo - 1) * pageSize;
            list.add(currentPageNo);
            list.add(pageSize);

            Object[] params = list.toArray();
//            System.out.println("sql ----> " + sql.toString());
            rs = BaseDao.execute(con, pstm, rs, sql.toString(), params);
            while (rs.next()) {
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
            BaseDao.closeResource(null, pstm, rs);
        }
        return userList;
    }

    //添加用户
    @Override
    public int add(Connection con, User user) throws Exception {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (con != null) {
            String sql = "insert into smbms_user (userCode, userName, userPassword, userRole, " +
                    "gender, birthday, phone, address, creationDate, createdBy) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {user.getUserCode(), user.getUserName(), user.getUserPassword(),
                    user.getUserRole(), user.getGender(), user.getBirthday(),
                    user.getPhone(), user.getAddress(), user.getCreationDate(), user.getCreatedBy()};
            updateRows = BaseDao.execute(con, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }

    //删除用户
    @Override
    public int deleteUserById(Connection con, Integer delId) throws Exception {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (con != null) {
            String sql = "delete from smbms_user where id = ?";
            Object[] params = {delId};
            updateRows = BaseDao.execute(con, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }

    //获取用户
    @Override
    public User getUserById(Connection con, String id) throws Exception {
        User user = new User();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if (con != null) {
            String sql = "select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.id=? and u.userRole = r.id";
            Object[] params = {id};
            rs = BaseDao.execute(con, pstm, rs, sql, params);
            if (rs.next()) {
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
                user.setUserRoleName(rs.getString("userRoleName"));
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return user;
    }

    //修改
    @Override
    public int modify(Connection con, User user) throws Exception {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (con != null) {
            String sql = "update smbms_user set userName=?," +
                    "gender=?,birthday=?,phone=?,address=?,userRole=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {user.getUserName(), user.getGender(), user.getBirthday(),
                    user.getPhone(), user.getAddress(), user.getUserRole(), user.getModifyBy(),
                    user.getModifyDate(), user.getId()};
            updateRows = BaseDao.execute(con, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }
}

