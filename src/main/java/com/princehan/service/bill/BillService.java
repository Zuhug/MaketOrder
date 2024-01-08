package com.princehan.service.bill;

import com.princehan.pojo.Bill;

import java.util.List;


public interface BillService {
    //添加订单
    boolean add(Bill bill);

    //通过条件获取订单列表-模糊查询-billList
    List<Bill> getBillList(Bill bill);

    //删除订单
    boolean deleteBillById(String delId);

    //获取订单
    Bill getBillById(String id);

    //修改订单
    boolean modify(Bill bill);
}
