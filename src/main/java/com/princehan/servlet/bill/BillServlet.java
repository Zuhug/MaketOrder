package com.princehan.servlet.bill;

import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.princehan.pojo.Bill;
import com.princehan.pojo.Provider;
import com.princehan.pojo.User;
import com.princehan.service.bill.BillServiceImpl;
import com.princehan.service.provider.ProviderServiceImpl;
import com.princehan.servlet.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@WebServlet("/jsp/bill.do")
public class BillServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method != null && method.equals("query")) {
            this.query(req, resp);
        } else if (method != null && method.equals("add")) {
            this.add(req, resp);
        } else if (method != null && method.equals("view")) {
            this.getBillById(req, resp, "billview.jsp");
        } else if (method != null && method.equals("modify")) {
            this.getBillById(req, resp, "billmodify.jsp");
        } else if (method != null && method.equals("modifysave")) {
            this.modify(req, resp);
        } else if (method != null && method.equals("delbill")) {
            this.delBill(req, resp);
        } else if (method != null && method.equals("getproviderlist")) {
            this.getProviderlist(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    //查询订单
    public void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<Provider> providerlist = new ArrayList<>();
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        providerlist = providerService.getProviderList("", "");
        req.setAttribute("providerList", providerlist);
        String queryProductName = req.getParameter("queryProductName");
        String queryProviderId = req.getParameter("queryProviderId");
        String queryIsPayment = req.getParameter("queryIsPayment");
        if (StringUtils.isNullOrEmpty(queryProductName)) {
            queryProductName = "";
        }
        List<Bill> billList = new ArrayList<>();
        BillServiceImpl billService = new BillServiceImpl();
        Bill bill = new Bill();
        if (StringUtils.isNullOrEmpty(queryIsPayment)) {
            bill.setIsPayment(0);
        } else {
            bill.setIsPayment(Integer.parseInt(queryIsPayment));
        }

        if (StringUtils.isNullOrEmpty(queryProviderId)) {
            bill.setProviderId(0);
        } else {
            bill.setProviderId(Integer.parseInt(queryProviderId));
        }
        bill.setProductName(queryProductName);
        billList = billService.getBillList(bill);
        req.setAttribute("billList", billList);
        req.setAttribute("queryProductName", queryProductName);
        req.setAttribute("queryProviderId", queryProviderId);
        req.setAttribute("queryIsPayment", queryIsPayment);
        req.getRequestDispatcher("billlist.jsp").forward(req, resp);
    }

    //添加订单
    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String billCode = req.getParameter("billCode");
        String productName = req.getParameter("productName");
        //String productDesc = req.getParameter("productDesc");
        String productUnit = req.getParameter("productUnit");

        String productCount = req.getParameter("productCount");
        String totalPrice = req.getParameter("totalPrice");
        String providerId = req.getParameter("providerId");
        String isPayment = req.getParameter("isPayment");

        Bill bill = new Bill();
        bill.setBillCode(billCode);
        bill.setProductName(productName);
        //bill.setProductDesc(productDesc);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));
        bill.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        bill.setCreationDate(new Date());
        boolean flag = false;
        BillServiceImpl billService = new BillServiceImpl();
        flag = billService.add(bill);
        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/jsp/bill.do?method=query");
        } else {
            req.getRequestDispatcher("billadd.jsp").forward(req, resp);
        }
    }

    //获取订单
    public void getBillById(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        String id = req.getParameter("billid");
        if (!StringUtils.isNullOrEmpty(id)) {
            BillServiceImpl billService = new BillServiceImpl();
            Bill bill = null;
            bill = billService.getBillById(id);
            req.setAttribute("bill", bill);
            req.getRequestDispatcher(url).forward(req, resp);
        }
    }

    //修改订单
    public void modify(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getParameter("id");
        String productName = req.getParameter("productName");
        String productUnit = req.getParameter("productUnit");
        String productCount = req.getParameter("productCount");
        String totalPrice = req.getParameter("totalPrice");
        String providerId = req.getParameter("providerId");
        String isPayment = req.getParameter("isPayment");

        Bill bill = new Bill();
        bill.setId(Integer.valueOf(id));
        bill.setProductName(productName);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));

        bill.setModifyBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        bill.setModifyDate(new Date());

        boolean flag = false;
        BillServiceImpl billService = new BillServiceImpl();
        flag = billService.modify(bill);
        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/jsp/bill.do?method=query");
        } else {
            req.getRequestDispatcher("billmodify.jsp").forward(req, resp);
        }
    }

    //删除订单
    public void delBill(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String billid = req.getParameter("billid");
        HashMap<String, String> resultMap = new HashMap<>();
        if (!StringUtils.isNullOrEmpty(billid)) {
            BillServiceImpl billService = new BillServiceImpl();
            boolean flag = billService.deleteBillById(billid);
            if (flag) {
                resultMap.put("delResult", "true");
            } else {
                resultMap.put("delResult", "false");
            }
        } else {
            resultMap.put("delResult", "notexist");
        }
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }

    //获取供应商列表
    public void getProviderlist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Provider> providerList = new ArrayList<>();
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        providerList = providerService.getProviderList("", "");
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(providerList));
        writer.flush();
        writer.close();
    }
}
