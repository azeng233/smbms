package cn.zengchen233.servlet.user;

import cn.zengchen233.pojo.User;
import cn.zengchen233.service.user.UserServiceImpl;
import cn.zengchen233.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {

    //Servlet:控制层调用业务层

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("进入LoginServlet");
        //获取用户名和密码
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");
        User user = null;

        //和数据库中的做对比,调用业务层
        UserServiceImpl userService = new UserServiceImpl();
        try {
            user = userService.login(userCode, userPassword);//这里已经把登陆的人查出来
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (user != null) {//有这个人 ，可以登录
            //将用户的信息放在Session
            req.getSession().setAttribute(Constant.USER_SESSION,user);
            resp.sendRedirect("jsp/frame.jsp");
        } else {
            req.setAttribute("error","用户名或密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
