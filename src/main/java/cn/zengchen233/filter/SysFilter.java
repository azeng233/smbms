package cn.zengchen233.filter;

import cn.zengchen233.pojo.User;
import cn.zengchen233.util.Constant;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        User user = (User) req.getSession().getAttribute(Constant.USER_SESSION);
        if (user == null) {
            //已经被移除，注销，未登录
            resp.sendRedirect( req.getContextPath()+ "/error.jsp");
        }

        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
