package cn.zengchen233.servlet.user;

import cn.zengchen233.pojo.Role;
import cn.zengchen233.pojo.User;
import cn.zengchen233.service.role.RoleService;
import cn.zengchen233.service.role.RoleServiceImpl;
import cn.zengchen233.service.user.UserService;
import cn.zengchen233.service.user.UserServiceImpl;
import cn.zengchen233.util.Constant;
import cn.zengchen233.util.PageSupport;
import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;

import javax.servlet.ServletException;
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
import java.util.Map;

//实现Servlet复用
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method.equals("savepwd") && method != null) {
            this.updatePwd(req, resp);
        } else if (method.equals("pwdmodify") && method != null) {
            this.verifyPwd(req, resp);
        } else if (method.equals("query") && method != null) {
            this.query(req, resp);
        } else if(method.equals("add") && method != null) {
            this.add(req, resp);
        } else if (method.equals("deluser") && method != null) {
            this.delUser(req, resp);
        } else if(method.equals("getrolelist")&& method != null) {
            this.getRoleList(req, resp);
        } else if(method.equals("modify") && method != null){
            this.getUserById(req, resp,"usermodify.jsp");
        } else if(method.equals("modifyexe") && method != null){
            this.modify(req, resp);
        } else if(method.equals("ucexist") && method != null){
            this.userCodeExist(req, resp);
        }   else if(method.equals("view") && method != null){
            this.getUserById(req, resp,"userview.jsp");
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
                req.setAttribute(Constant.MESSAGE, "密码修改成功,请退出并使用新密码登录!");
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
    public void verifyPwd(HttpServletRequest req, HttpServletResponse resp) {
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

    //重点和难点
    public void query(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //查询用户列表
        //从前端获取数据
        String queryUserName = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        //获取用户列表
        UserService userService = new UserServiceImpl();

        List<User> userList = null;
        //第一次走这个请求一定是第一页，页面大小固定
        int pageSize = Constant.pageSize;
        int currentPageNo = 1;

        if (queryUserName == null){
            queryUserName = "";
        }
        if (temp != null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp);//给查询赋值
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

        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount();

        //控制首页和尾页
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }

        //获取用户列表展示
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList",userList);

        RoleService roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();

        req.setAttribute("roleList",roleList);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole",queryUserRole);
        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //增加用户
    private void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("add()================");
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
        user.setCreatedBy(((User)req.getSession().getAttribute(Constant.USER_SESSION)).getId());

        UserService userService = new UserServiceImpl();
        if(userService.add(user)){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else{
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);
        }
    }

    //删除用户
    private void delUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("uid");
        Integer delId = 0;
        try{
            delId = Integer.parseInt(id);
        }catch (Exception e) {
            delId = 0;
        }
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(delId <= 0){
            resultMap.put("delResult", "notexist");
        }else{
            UserService userService = new UserServiceImpl();
            if(userService.deleteUserById(delId)){
                resultMap.put("delResult", "true");
            }else{
                resultMap.put("delResult", "false");
            }
        }

        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    private void modify(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("uid");
        String userName = request.getParameter("userName");
        String gender = request.getParameter("gender");
        String birthday = request.getParameter("birthday");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String userRole = request.getParameter("userRole");

        User user = new User();
        user.setId(Integer.valueOf(id));
        user.setUserName(userName);
        user.setGender(Integer.valueOf(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setModifyBy(((User)request.getSession().getAttribute(Constant.USER_SESSION)).getId());
        user.setModifyDate(new Date());

        UserService userService = new UserServiceImpl();
        if(userService.modify(user)){
            response.sendRedirect(request.getContextPath()+"/jsp/user.do?method=query");
        }else{
            request.getRequestDispatcher("usermodify.jsp").forward(request, response);
        }

    }

    private void userCodeExist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //判断用户账号是否可用
        String userCode = request.getParameter("userCode");

        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(StringUtils.isNullOrEmpty(userCode)){
            //userCode == null || userCode.equals("")
            resultMap.put("userCode", "exist");
        }else{
            UserService userService = new UserServiceImpl();
            User user = userService.selectUserCodeExist(userCode);
            if(null != user){
                resultMap.put("userCode","exist");
            }else{
                resultMap.put("userCode", "notexist");
            }
        }

        //把resultMap转为json字符串以json的形式输出
        //配置上下文的输出类型
        response.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter outPrintWriter = response.getWriter();
        //把resultMap转为json字符串 输出
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }

    private void getRoleList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        //把roleList转换成json对象输出
        response.setContentType("application/json");
        PrintWriter outPrintWriter = response.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(roleList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    private void getUserById(HttpServletRequest request, HttpServletResponse response,String url) throws ServletException, IOException {
        String id = request.getParameter("uid");
        if(!StringUtils.isNullOrEmpty(id)){
            //调用后台方法得到user对象
            UserService userService = new UserServiceImpl();
            User user = userService.getUserById(id);
            request.setAttribute("user", user);
            request.getRequestDispatcher(url).forward(request, response);
        }

    }
}
