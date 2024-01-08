package com.princehan.dao.bill;

import com.mysql.cj.util.StringUtils;
import com.princehan.dao.BaseDao;
import com.princehan.pojo.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BillDaoImpl implements BillDao {
    //添加订单
    @Override
    public int add(Connection con, Bill bill) {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (con != null) {
            String sql = "insert into smbms_bill (billCode,productName,productDesc," +
                    "productUnit,productCount,totalPrice,isPayment,providerId,createdBy,creationDate) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {bill.getBillCode(), bill.getProductName(), bill.getProductDesc(),
                    bill.getProductUnit(), bill.getProductCount(), bill.getTotalPrice(), bill.getIsPayment(),
                    bill.getProviderId(), bill.getCreatedBy(), bill.getCreationDate()};
            updateRows = BaseDao.execute(con, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }

    //模糊查询获取订单列表
    @Override
    public List<Bill> getBillList(Connection con, Bill bill) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Bill> billList = new ArrayList<Bill>();
        if (con != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select b.*,p.proName as providerName from smbms_bill b, smbms_provider p where b.providerId = p.id");
            List<Object> list = new ArrayList<>();
            if (!StringUtils.isNullOrEmpty(bill.getProductName())) {
                sql.append(" and productName like ?");
                list.add("%" + bill.getProductName() + "%");
            }
            if (bill.getProviderId() > 0) {
                sql.append(" and providerId = ?");
                list.add(bill.getProviderId());
            }
            if (bill.getIsPayment() > 0) {
                sql.append(" and isPayment = ?");
                list.add(bill.getIsPayment());
            }
            Object[] params = list.toArray();
            rs = BaseDao.execute(con, pstm, rs, sql.toString(), params);
            while (rs.next()) {
                Bill _bill = new Bill();
                _bill.setId(rs.getInt("id"));
                _bill.setBillCode(rs.getString("billCode"));
                _bill.setProductName(rs.getString("productName"));
                _bill.setProductDesc(rs.getString("productDesc"));
                _bill.setProductUnit(rs.getString("productUnit"));
                _bill.setProductCount(rs.getBigDecimal("productCount"));
                _bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                _bill.setIsPayment(rs.getInt("isPayment"));
                _bill.setProviderId(rs.getInt("providerId"));
                _bill.setProviderName(rs.getString("providerName"));
                _bill.setCreationDate(rs.getTimestamp("creationDate"));
                _bill.setCreatedBy(rs.getInt("createdBy"));
                billList.add(_bill);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return billList;
    }

    //删除订单
    @Override
    public int deleteBillById(Connection con, String delId) throws Exception {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (con != null) {
            String sql = "delete from smbms_bill where id = ?";
            Object[] params = {delId};
            updateRows = BaseDao.execute(con, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }

    //获取订单
    @Override
    public Bill getBillById(Connection con, String id) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Bill bill = null;
        if (con != null) {
            String sql = "select b.*,p.proName as providerName from smbms_bill b, smbms_provider p " +
                    "where b.providerId = p.id and b.id=?";
            Object[] params = {id};
            rs = BaseDao.execute(con, pstm, rs, sql, params);
            if (rs.next()) {
                bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProductDesc(rs.getString("productDesc"));
                bill.setProductUnit(rs.getString("productUnit"));
                bill.setProductCount(rs.getBigDecimal("productCount"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setIsPayment(rs.getInt("isPayment"));
                bill.setProviderId(rs.getInt("providerId"));
                bill.setProviderName(rs.getString("providerName"));
                bill.setModifyBy(rs.getInt("modifyBy"));
                bill.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return bill;
    }

    //修改订单
    @Override
    public int modify(Connection con, Bill bill) throws Exception {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (con != null) {
            String sql = "update smbms_bill set productName=?," +
                    "productDesc=?,productUnit=?,productCount=?,totalPrice=?," +
                    "isPayment=?,providerId=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {bill.getProductName(), bill.getProductDesc(),
                    bill.getProductUnit(), bill.getProductCount(), bill.getTotalPrice(), bill.getIsPayment(),
                    bill.getProviderId(), bill.getModifyBy(), bill.getModifyDate(), bill.getId()};
            updateRows = BaseDao.execute(con, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }

    //根据供应商ID查询订单数量
    @Override
    public int getBillCountByProviderId(Connection con, String providerId) throws Exception {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int billCount = 0;
        if (con != null) {
            String sql = "select count(1) as billCount from smbms_bill where" +
                    "	providerId = ?";
            Object[] params = {providerId};
            rs = BaseDao.execute(con, pstm, rs, sql, params);
            if (rs.next()) {
                billCount = rs.getInt("billCount");
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return billCount;
    }
}
