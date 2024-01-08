package com.princehan.service.provider;

import com.princehan.dao.BaseDao;
import com.princehan.dao.bill.BillDao;
import com.princehan.dao.bill.BillDaoImpl;
import com.princehan.dao.provider.ProviderDao;
import com.princehan.dao.provider.ProviderIDaompl;
import com.princehan.pojo.Provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class ProviderServiceImpl implements ProviderService {
    //业务层调用Dao层
    private ProviderDao providerDao;
    private BillDao billDao;

    public ProviderServiceImpl() {
        providerDao = new ProviderIDaompl();
        billDao = new BillDaoImpl();
    }

    //增加供应商
    @Override
    public boolean add(Provider provider) {
        Connection con = null;
        boolean flag = false;
        try {
            con = BaseDao.getConnection();
            con.setAutoCommit(false);
            if (providerDao.add(con, provider) > 0) {
                flag = true;
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Provider-->rollback");
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return flag;
    }

    //通过供应商名称、编码获取供应商列表-模糊查询-providerList
    @Override
    public List<Provider> getProviderList(String proName, String proCode) {
        Connection con = null;
        List<Provider> providerList = null;
        try {
            con = BaseDao.getConnection();
            providerList = providerDao.getProviderList(con, proName, proCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return providerList;
    }

    //通过proId删除Provider 返回订单列表
    @Override
    public int deleteProviderById(String delId) {
        Connection con = null;
        int billCount = -1;
        try {
            con = BaseDao.getConnection();
            con.setAutoCommit(false);
            billCount = billDao.getBillCountByProviderId(con, delId);
            if (billCount == 0) {
                providerDao.deleteProviderById(con, delId);
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
            billCount = -1;
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return billCount;
    }

    //通过proId获取Provider
    @Override
    public Provider getProviderById(String id) {
        Connection con = null;
        Provider provider = null;
        try {
            con = BaseDao.getConnection();
            provider = providerDao.getProviderById(con, id);
        } catch (Exception e) {
            e.printStackTrace();
            provider = null;
        } finally {
            BaseDao.closeResource(con, null, null);
        }
        return provider;
    }

    //修改用户信息
    @Override
    public boolean modify(Provider provider) {
        Connection con = null;
        boolean flag = false;
        try {
            con = BaseDao.getConnection();
            if (providerDao.modify(con, provider) > 0) {
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
