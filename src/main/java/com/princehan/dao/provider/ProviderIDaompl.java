package com.princehan.dao.provider;

import com.mysql.cj.util.StringUtils;
import com.princehan.dao.BaseDao;
import com.princehan.pojo.Provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class ProviderIDaompl implements ProviderDao {
    //添加用户
    @Override
    public int add(Connection con, Provider provider) throws Exception {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (con != null) {
            String sql = "insert into smbms_provider (proCode,proName,proDesc," +
                    "proContact,proPhone,proAddress,proFax,createdBy,creationDate) " +
                    "values(?,?,?,?,?,?,?,?,?)";
            Object[] params = {provider.getProCode(), provider.getProName(), provider.getProDesc(),
                    provider.getProContact(), provider.getProPhone(), provider.getProAddress(),
                    provider.getProFax(), provider.getCreatedBy(), provider.getCreationDate()};
            updateRows = BaseDao.execute(con, pstm, sql, params);
        }
        return updateRows;
    }

    //获取供应商列表
    @Override
    public List<Provider> getProviderList(Connection con, String proName, String proCode) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Provider> providerList = new ArrayList<>();
        if (con != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select * from smbms_provider where 1=1 ");
            List<Object> list = new ArrayList<>();
            if (!StringUtils.isNullOrEmpty(proName)) {
                sql.append(" and proName like ?");
                list.add("%" + proName + "%");
            }
            if (!StringUtils.isNullOrEmpty(proCode)) {
                sql.append(" and proCode like ?");
                list.add("%" + proCode + "%");
            }
            Object[] params = list.toArray();
            rs = BaseDao.execute(con, pstm, rs, sql.toString(), params);
            while (rs.next()) {
                Provider provider = new Provider();
                provider.setId(rs.getInt("id"));
                provider.setProCode(rs.getString("proCode"));
                provider.setProName(rs.getString("proName"));
                provider.setProDesc(rs.getString("proDesc"));
                provider.setProContact(rs.getString("proContact"));
                provider.setProPhone(rs.getString("proPhone"));
                provider.setProAddress(rs.getString("proAddress"));
                provider.setProFax(rs.getString("proFax"));
                provider.setCreationDate(rs.getTimestamp("creationDate"));
                providerList.add(provider);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return providerList;
    }

    //删除供应商
    @Override
    public int deleteProviderById(Connection con, String delId) throws Exception {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (con != null) {
            String sql = "delete from smbms_provider where id = ?";
            Object[] params = {delId};
            updateRows = BaseDao.execute(con, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }

    //获取供应商
    @Override
    public Provider getProviderById(Connection con, String id) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Provider provider = null;
        if (con != null) {
            String sql = "select * from smbms_provider where id = ?";
            Object[] params = {id};
            rs = BaseDao.execute(con, pstm, rs, sql, params);
            if (rs.next()) {
                provider = new Provider();
                provider.setId(rs.getInt("id"));
                provider.setProCode(rs.getString("proCode"));
                provider.setProName(rs.getString("proName"));
                provider.setProDesc(rs.getString("proDesc"));
                provider.setProContact(rs.getString("proContact"));
                provider.setProPhone(rs.getString("proPhone"));
                provider.setProAddress(rs.getString("proAddress"));
                provider.setProFax(rs.getString("proFax"));
                provider.setCreatedBy(rs.getInt("createdBy"));
                provider.setCreationDate(rs.getTimestamp("creationDate"));
                provider.setModifyBy(rs.getInt("modifyBy"));
                provider.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return provider;
    }

    //修改
    @Override
    public int modify(Connection con, Provider provider) throws Exception {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (con != null) {
            String sql = "update smbms_provider set proName=?,proDesc=?,proContact=?," +
                    "proPhone=?,proAddress=?,proFax=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {provider.getProName(), provider.getProDesc(), provider.getProContact(), provider.getProPhone(), provider.getProAddress(), provider.getProFax(), provider.getModifyBy(), provider.getModifyDate(), provider.getId()};
            updateRows = BaseDao.execute(con, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }
}
