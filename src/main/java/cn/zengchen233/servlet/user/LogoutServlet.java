package cn.zengchen233.servlet.user;

import cn.zengchen233.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //移除用户的Session
        req.getSession().removeAttribute(Constant.USER_SESSION);
        // req.getSession().removeAttribute("userName");
        resp.sendRedirect( req.getContextPath()+ "/login.jsp");//返回登录界面
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
