package com.princehan.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.princehan.pojo.Role;
import com.princehan.pojo.User;
import com.princehan.service.role.RoleServiceImpl;
import com.princehan.service.user.UserService;
import com.princehan.service.user.UserServiceImpl;
import com.princehan.servlet.util.Constants;
import com.princehan.servlet.util.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@WebServlet("/jsp/user.do")
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method.equals("savepwd") && method != null) {
            this.updatePwd(req, resp);
        } else if (method.equals("pwdmodify") && method != null) {
            this.pwdModify(req, resp);
        } else if (method.equals("query") && method != null) {
            this.query(req, resp);
        } else if (method.equals("add") && method != null) {
            this.add(req, resp);
        } else if (method.equals("deluser") && method != null) {
            this.delUser(req, resp);
        } else if (method.equals("view") && method != null) {
            this.getUserById(req, resp, "userview.jsp");
        } else if (method.equals("modify") && method != null) {
            this.getUserById(req, resp, "usermodify.jsp");
        } else if (method.equals("modifyexe") && method != null) {
            try {
                this.modifyexe(req, resp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (method.equals("ucexist") && method != null) {
            this.userCodeExist(req, resp);
        } else if (method.equals("getrolelist") && method != null) {
            this.getRoleList(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    //修改密码
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) {
        //从session里拿id
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");

        System.out.println("UserServlet" + newpassword);
        boolean flag = false;
        if (o != null && !StringUtils.isNullOrEmpty(newpassword)) {
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) o).getId(), newpassword);
            if (flag) {
                req.setAttribute("message", "修改密码成功，请使用新密码登录！");
                req.getSession().removeAttribute(Constants.USER_SESSION);
            } else {
                req.setAttribute("message", "密码修改失败！");
            }
        } else {
            req.setAttribute("message", "密码修改失败！");
        }
        try {
            req.getRequestDispatcher("/jsp/pwdmodify.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //验证旧密码
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp) {
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");

        HashMap<String, String> resultMap = new HashMap<>();
        if (o == null) {
            // o = null session过期
            resultMap.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldpassword)) {
            resultMap.put("result", "error");
        } else {
            String userPassword = ((User) o).getUserPassword();
            if (oldpassword.equals(userPassword)) {
                resultMap.put("result", "true");
            } else {
                resultMap.put("result", "false");
            }
        }

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //转换格式
            // JSONArray: alibaba的JSON工具类----转换格式
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //查询 重点难点
    public void query(HttpServletRequest req, HttpServletResponse resp) {

        //查询用户列表

        //从前端获取数据：
        String queryUserName = req.getParameter("queryname");
        String tmp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        //获取用户列表
        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList = null;

        //第一次走这个请求，一定是第一个，页面大小固定的
        //int pageSize = 8;//可写配置文件里
        int pageSize = Integer.parseInt(getServletContext().getInitParameter("pageSize"));

        System.out.println("pageSize: "+req.getParameter("pageSize"));
        req.getParameter("pageSize");
        int currentPageNo = 1;

        if (queryUserName == null) {
            queryUserName = "";
        }
        if (tmp != null && !tmp.equals("")) {
            //给查询赋值
            queryUserRole = Integer.parseInt(tmp);
        }
        if (pageIndex != null) {
            currentPageNo = Integer.parseInt(pageIndex);
        }

        //获取用户总数
        int totalCount = userService.getUserCount(queryUserName, queryUserRole);
        //总页数支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalPageCount((int) Math.ceil(totalCount * 1.0 / pageSize));

        int totalPageCount = pageSupport.getTotalPageCount();

        //控制首页和尾页
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }

        //获取用户列表展示
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList", userList);

        //获取角色列表
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList", roleList);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalPageCount", totalPageCount);
        req.setAttribute("queryUserName", queryUserName);
        req.setAttribute("queryUserRole", queryUserRole);

        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //添加用户
    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //从前端获取数据
        System.out.println("add");
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        UserServiceImpl userService = new UserServiceImpl();
        if (userService.add(user)) {
            System.out.println("add" + user);
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);
        }

    }

    //删除用户
    public void delUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("uid");
        Integer delId = 0;
        try {
            delId = Integer.parseInt(id);
        } catch (Exception e) {
            e.printStackTrace();
            delId = 0;
        }
        HashMap<String, String> resultMap = new HashMap<>();
        if (delId <= 0) {
            resultMap.put("delResult", "notexist");
        } else {
            UserServiceImpl userService = new UserServiceImpl();
            if (userService.deleteUserById(delId)) {
                resultMap.put("delresult", "true");
            } else {
                resultMap.put("delresult", "false");
            }
        }
        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }

    //获取用户
    public void getUserById(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        if (!StringUtils.isNullOrEmpty(uid)) {
            UserServiceImpl userService = new UserServiceImpl();
            User user = userService.getUserById(uid);
            req.setAttribute("user", user);
            req.getRequestDispatcher(url).forward(req, resp);
        }
    }

    //修改用户
    public void modifyexe(HttpServletRequest req, HttpServletResponse resp) throws ParseException, IOException, ServletException {
        String uid = req.getParameter("uid");
        String userName = req.getParameter("userName");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");
        User user = new User();
        user.setId(Integer.parseInt(uid));
        user.setUserName(userName);
        user.setGender(Integer.parseInt(gender));
        user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());

        UserServiceImpl userService = new UserServiceImpl();
        if (userService.modify(user)) {
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
        }
    }

    public void userCodeExist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //判断用户是否可用
        String userCode = req.getParameter("userCode");
        HashMap<String, String> resultMap = new HashMap<>();

        if (StringUtils.isNullOrEmpty(userCode)) {
            resultMap.put("userCode", "exist");
        } else {
            UserServiceImpl userService = new UserServiceImpl();
            User user = userService.selectUserCodeExist(userCode);
            if (user != null) {
                resultMap.put("userCode", "exist");
            } else {
                resultMap.put("userCode", "notexist");
            }
        }
        //把resultMap转为json字符串以json的形式输出
        //配置上下文的输出类型
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }

    //获取用户角色列表
    public void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Role> roleList = null;
        RoleServiceImpl roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(roleList));
        writer.flush();
        writer.close();
    }
}
