package com.princehan.dao.bill;

import com.princehan.pojo.Bill;

import java.sql.Connection;
import java.util.List;


public interface BillDao {
    //添加订单
    int add(Connection con, Bill bill);

    //模糊查询获取订单列表
    List<Bill> getBillList(Connection con, Bill bill) throws Exception;

    //删除订单
    int deleteBillById(Connection con, String delId) throws Exception;

    //获取订单
    Bill getBillById(Connection con, String id) throws Exception;

    //修改订单
    int modify(Connection con, Bill bill) throws Exception;

    //根据供应商ID查询订单数量
    int getBillCountByProviderId(Connection con, String providerId) throws Exception;
}
