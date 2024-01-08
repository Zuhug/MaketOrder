package com.princehan.service.bill;

import com.princehan.dao.BaseDao;
import com.princehan.dao.bill.BillDao;
import com.princehan.dao.bill.BillDaoImpl;
import com.princehan.pojo.Bill;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class BillServiceImpl implements BillService{
    private BillDao billDao;

    public BillServiceImpl() {
        billDao = new BillDaoImpl();
    }

    //添加订单
    @Override
    public boolean add(Bill bill) {
        Connection con = null;
        boolean flag = false;
        try {
            con = BaseDao.getConnection();
            con.setAutoCommit(false);
            if (billDao.add(con, bill) > 0) {
                flag = true;
            }
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                System.out.println("Billadd-->rollback");
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return flag;
    }

    //通过条件获取订单列表-模糊查询-billList
    @Override
    public List<Bill> getBillList(Bill bill) {
        Connection con = null;
        List<Bill> billList = null;
        System.out.println("query productName ---- > " + bill.getProductName());
        System.out.println("query providerId ---- > " + bill.getProviderId());
        System.out.println("query isPayment ---- > " + bill.getIsPayment());
        try {
            con = BaseDao.getConnection();
            billList = billDao.getBillList(con, bill);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return billList;
    }

    //删除订单
    @Override
    public boolean deleteBillById(String delId) {
        Connection con = null;
        boolean flag = false;
        try {
            con = BaseDao.getConnection();
            if (billDao.deleteBillById(con, delId) > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return flag;
    }

    //获取订单
    @Override
    public Bill getBillById(String id) {
        Connection con = null;
        Bill bill = null;
        try {
            con = BaseDao.getConnection();
            bill = billDao.getBillById(con, id);
        } catch (Exception e) {
            e.printStackTrace();
            bill = null;
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return bill;
    }

    //修改订单
    @Override
    public boolean modify(Bill bill) {
        Connection con = null;
        boolean flag = false;
        try {
            con = BaseDao.getConnection();
            if (billDao.modify(con, bill) > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return flag;
    }
}
