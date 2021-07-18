package cn.zengchen233.servlet.user;

import cn.zengchen233.pojo.User;
import cn.zengchen233.service.user.UserService;
import cn.zengchen233.service.user.UserServiceImpl;
import cn.zengchen233.util.Constant;
import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

//实现Servlet复用
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method.equals("savepwd") && method != null) {
            this.updatePwd(req, resp);
        } else if (method.equals("pwdmodify") && method != null) {
            this.pwdmodify(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    //修改密码
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) {
        //从Session取账号
        Object o = req.getSession().getAttribute(Constant.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        boolean flag = false;

        if (o != null && !StringUtils.isNullOrEmpty(newpassword)) { //如果这个人取到了，且新密码不为空
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) o).getUserCode(), newpassword);
            if (flag) {
                req.setAttribute(Constant.MESSAGE, "密码修改成功,请退出并且是用新密码登录!");
                //密码修改成功，移除当前Session
                req.getSession().removeAttribute(Constant.USER_SESSION);
                //因为有SysFilter,只要发现没有这个Session就立马跳转到错误页面
            } else {
                req.setAttribute(Constant.MESSAGE, "密码修改失败!");
            }
        } else {
            req.setAttribute(Constant.MESSAGE, "新密码有问题!");
        }
        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //验证旧密码,从Session中取得旧密码
    public void pwdmodify(HttpServletRequest req, HttpServletResponse resp) {
        Object o = req.getSession().getAttribute(Constant.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");

        //万能的map:结果集
        Map<String, String> resultMap = new HashMap<String, String>();
        if (o == null) { //Session过期
            resultMap.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldpassword)){ //输入的密码为空
            resultMap.put("result", "error");
        } else {
            String userPassword = ((User) o).getUserPassword();//Session中的老密码
            if (oldpassword.equals(userPassword)) {
                resultMap.put("result", "true");
            } else {
                resultMap.put("result", "false");
            }
        }
        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //阿里巴巴的JSON工具类,转换格式用
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
