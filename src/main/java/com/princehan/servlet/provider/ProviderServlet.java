package com.princehan.servlet.provider;


import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.princehan.pojo.Provider;
import com.princehan.pojo.User;
import com.princehan.service.provider.ProviderService;
import com.princehan.service.provider.ProviderServiceImpl;
import com.princehan.servlet.util.Constants;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@WebServlet("/jsp/provider.do")
public class ProviderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        if (method != null && method.equals("query")) {
            this.query(request, response);
        } else if (method != null && method.equals("add")) {
            this.add(request, response);
        } else if (method != null && method.equals("view")) {
            this.getProviderById(request, response, "providerview.jsp");
        } else if (method != null && method.equals("modify")) {
            this.getProviderById(request, response, "providermodify.jsp");
        } else if (method != null && method.equals("modifysave")) {
            this.modify(request, response);
        } else if (method != null && method.equals("delprovider")) {
            this.delProvider(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    //查询供应商
    public void query(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String queryProCode = request.getParameter("queryProCode");
        String queryProName = request.getParameter("queryProName");
        if (StringUtils.isNullOrEmpty(queryProCode)) {
            queryProCode = "";
        }
        if (StringUtils.isNullOrEmpty(queryProCode)) {
            queryProCode = "";
        }
        List<Provider> providerList = new ArrayList<>();
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        providerList = providerService.getProviderList(queryProName, queryProCode);
        request.setAttribute("providerList", providerList);
        request.setAttribute("queryProName", queryProName);
        request.setAttribute("queryProCode", queryProCode);
        request.getRequestDispatcher("providerlist.jsp").forward(request, response);
    }

    //添加供应商
    public void add(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String proCode = request.getParameter("proCode");
        String proName = request.getParameter("proName");
        String proContact = request.getParameter("proContact");
        String proPhone = request.getParameter("proPhone");
        String proAddress = request.getParameter("proAddress");
        String proFax = request.getParameter("proFax");
        String proDesc = request.getParameter("proDesc");

        Provider provider = new Provider();

        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProFax(proFax);
        provider.setProAddress(proAddress);
        provider.setProDesc(proDesc);
        provider.setCreatedBy(((User) request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        provider.setCreationDate(new Date());

        ProviderServiceImpl providerService = new ProviderServiceImpl();
        boolean flag = providerService.add(provider);
        if (flag) {
            response.sendRedirect(request.getContextPath() + "/jsp/provider.do?method=query");
        } else {
            request.getRequestDispatcher("provideradd.jsp").forward(request, response);
        }
    }

    //获取供应商
    public void getProviderById(HttpServletRequest request, HttpServletResponse response, String url) throws ServletException, IOException {
        String id = request.getParameter("proid");
        if (!StringUtils.isNullOrEmpty(id)) {
            ProviderServiceImpl providerService = new ProviderServiceImpl();
            Provider provider = null;
            provider = providerService.getProviderById(id);
            request.setAttribute("provider", provider);
            request.getRequestDispatcher(url).forward(request, response);
        }
    }
    
    //修改供应商
    public void modify(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String proCode = request.getParameter("proCode");
        String proName = request.getParameter("proName");
        String proContact = request.getParameter("proContact");
        String proPhone = request.getParameter("proPhone");
        String proAddress = request.getParameter("proAddress");
        String proFax = request.getParameter("proFax");
        String proDesc = request.getParameter("proDesc");
        String id = request.getParameter("id");
        Provider provider = new Provider();
        provider.setId(Integer.valueOf(id));
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProFax(proFax);
        provider.setProAddress(proAddress);
        provider.setProDesc(proDesc);
        provider.setModifyBy(((User) request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        provider.setModifyDate(new Date());
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        boolean flag = providerService.modify(provider);
        if (flag) {
            response.sendRedirect(request.getContextPath() + "/jsp/provider.do?method=query");
        } else {
            request.getRequestDispatcher("providermodify.jsp").forward(request, response);
        }
    }

    //删除供应商
    public void delProvider(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("proid");
        HashMap<String, String> resultMap = new HashMap<>();
        if (!StringUtils.isNullOrEmpty(id)) {
            ProviderServiceImpl providerService = new ProviderServiceImpl();
            int flag = providerService.deleteProviderById(id);
            if (flag == 0) {
                //删除成功
                resultMap.put("delResult", "true");
            } else if (flag == -1) {
                //删除失败
                resultMap.put("delResult", "false");
            } else if (flag > 0) {
                //该供应商下有订单，不能删除，返回订单数
                resultMap.put("delResult", String.valueOf(flag));
            }
        } else {
            resultMap.put("delResult", "notexit");
        }
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }
}
