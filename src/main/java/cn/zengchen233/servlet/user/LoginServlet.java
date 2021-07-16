package cn.zengchen233.servlet.user;

import cn.zengchen233.pojo.User;
import cn.zengchen233.service.user.UserService;
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
        // System.out.println("进入LoginServlet");
        //获取用户名和密码
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");
        User user = null;

        //和数据库中的做对比,调用业务层
        UserService userService = new UserServiceImpl();
        user = userService.login(userCode, userPassword);//这里已经把登陆的人查出来

        if (user != null && userPassword.equals(user.getUserPassword())) {//有这个人 ，可以登录
            //将用户的信息放在Session
            req.getSession().setAttribute(Constant.USER_SESSION,user);
            // req.getSession().setAttribute("userName",user.getUserName()); //用来解决前端页面不显示名字
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
