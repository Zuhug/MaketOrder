package com.princehan.servlet.user;


import com.princehan.pojo.User;
import com.princehan.service.user.UserServiceImpl;
import com.princehan.servlet.util.Constants;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login.do")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("LoginServlet start...");
        //获取用户名和密码
        String userCode = request.getParameter("userCode");
        String userPassword = request.getParameter("userPassword");
        //和数据库中的密码进行对比
        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.login(userCode, userPassword);
        if (user != null) {
            //将用户信息存入session中
            request.getSession().setAttribute(Constants.USER_SESSION, user);
            //跳转到主页
            response.sendRedirect("jsp/frame.jsp");
        } else {
            /**
             查无此人，无法登录
             转发给登录页面，提示用户名或密码错误
             */
            request.setAttribute("error", "用户或密码不正确");

            //转发路径相对于webapp内第一层
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
