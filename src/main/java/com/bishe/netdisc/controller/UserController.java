package com.bishe.netdisc.controller;

import com.bishe.netdisc.common.entity.Result;
import com.bishe.netdisc.common.entity.ResultCode;
import com.bishe.netdisc.common.exception.CommonException;
import com.bishe.netdisc.common.utils.JwtUtil;
import com.bishe.netdisc.common.utils.SaltUtil;
import com.bishe.netdisc.common.utils.UserUtil;
import com.bishe.netdisc.entity.Role;
import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.entity.UserFile;
import com.bishe.netdisc.entity.common.UserForm;
import com.bishe.netdisc.service.RoleService;
import com.bishe.netdisc.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author third_e
 * @create 2020/4/10 0010-下午 9:34
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserUtil userUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    //测试
    @PostMapping("/test")
    public String test(){
        User user = userService.findByAccount("admin");
        User user1 = userService.userDao().findByAccount("third_e@163.com");
        System.out.println(user1);
        return user.toString();
    }

    // 登录用户认证状态
    @RequestMapping("/article")
    @RequiresAuthentication
    public Result article() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return new Result(200,"认证有效",true);
        } else {
            return new Result(ResultCode.FAIL);
        }
    }
    // 用户注册
    @PostMapping("/register")
    public Result register (@RequestParam("username") String username, @RequestParam("password") String password) {
        if (username == null || username =="" || password == null || password == ""){
            throw new CommonException("注册失败");
        }
        User user = userService.findByAccount(username);
        if (user != null){
            throw new CommonException("账号已存在");
        }
        User newUser = new User();
        newUser.setAccount(username);
        // 生成新的密码盐
        String salt = SaltUtil.getRandomSalt();
        // 用户密码加密
        String saltPassword = SaltUtil.md5Encrypt(password,salt);
        newUser.setPassword(saltPassword);
        newUser.setSalt(salt);
        // 获取普通用户id
        Role role = roleService.findByname("普通用户");
        newUser.setRoleid(role.getId());
        newUser.setStatus("enable");
        newUser.setUsestoragesize((double) 0);
        newUser.setCreatetime(new Date());
        userService.save(newUser);
        // 创建
        return new Result(200,"注册成功",true);
    }

    // 用户登录
    @PostMapping(value = "/login")
    public Result login(@RequestParam("username") String username, @RequestParam("password") String password) {
       if (username == "" || password == ""){
           throw new CommonException("登录失败");
       }
        User userBean = userService.findByAccount(username);
        System.out.println(userBean);
        if (userBean == null){
            throw  new CommonException("账号不存在");
        }
        if ("disable".equals(userBean.getStatus())) {
            throw  new CommonException("账号已被冻结");
        }
        if (!userBean.getPassword().equals(SaltUtil.md5Encrypt(password,userBean.getSalt()))) {
            throw  new CommonException("账号或密码不正确");

        }
        // 填充用户信息
        Map<String,Object> userInfo = new HashMap();
        userInfo.put( "id", userBean.getId());
        userInfo.put("account",userBean.getAccount());
        userInfo.put("name",userBean.getName());
        userInfo.put("sex",userBean.getSex());
        //获取当前登录的token
        String token = JwtUtil.sign(username, userBean.getPassword());
        // 返回所需要的用户数据
        Map<String,Object> result = new HashMap<>();
        result.put("userInfo", userInfo);
        result.put("Token",token);
        return new Result(ResultCode.SUCCESS, result);
    }

    //判断用户类型
    @GetMapping("/roletype")
    public Result roleType (HttpServletRequest httpServletRequest){
        //获取token,前面已经过滤没有token的情况
         String token =  httpServletRequest.getHeader("Authorization");
        //获取用户账号
        String username = JwtUtil.getUsername(token);
        User user = userService.findByAccount(username);
        if ("5e79aeb6e246000046007562".equals(user.getRoleid())) {
            return new Result(ResultCode.SUCCESS);
        }else {
            return new Result(ResultCode.FAIL);
        }
    }



    /**
     * 查询用户
     */

    // 获取当前用户
    @GetMapping("/info")
    public Result info(){
        User user = userUtil.getUser();
        Map<String, Object> usermap = new HashMap<>();
        usermap.put("id",user.getId());
        usermap.put("account",user.getAccount());
        usermap.put("name",user.getName());
        usermap.put("sex",user.getSex());
        Map<String, Object> map = new HashMap<>();
        map.put("userInfo",usermap);
        map.put("Token",userUtil.getToken());
        return new Result(ResultCode.SUCCESS, map);
    }

    // 获取单个用户信息
    @GetMapping("/{ id }")
    public Result getUserById(@PathVariable(" id ") String id) {
        if (id == null || id == ""){
            throw new CommonException("查询失败");
        }
        // 获取用户信息
        User user = userService.userDao().queryById(id);
        if (user == null ) {
            throw new CommonException("用户不存在");
        }
        Map<String,Object> userinfo = new HashMap<>();
        userinfo.put("id", user.getId());
        userinfo.put("account", user.getAccount());
        userinfo.put("name", user.getName());
        userinfo.put("sex", user.getSex());

        return new Result(ResultCode.SUCCESS,userinfo);
    }

    // 用户修改用户信息
    @PostMapping("/edit")
    public Result editUser(UserForm userinfo) {
        // 判断信息不能为空
        if (userinfo.getName() == null || userinfo.getName() == "" || userinfo.getSex() == null || userinfo.getSex() == "") {
            throw new CommonException("姓名/性别不能为空");
        }
        if (!"男".equals(userinfo.getSex()) && !"女".equals(userinfo.getSex())) {
            throw new CommonException("性别有误");
        }
        // 获取当前用户
        User oldUser = userUtil.getUser();
        oldUser.setName(userinfo.getName());
        oldUser.setSex(userinfo.getSex());
        System.out.println(oldUser);
        this.userService.userDao().save(oldUser);
        Map<String,Object> map = new HashMap<>();
        map.put("id",oldUser.getId());
        map.put("account",oldUser.getAccount());
        map.put("name",oldUser.getName());
        map.put("sex",oldUser.getSex());
        return new Result(ResultCode.SUCCESS,map);
    }

    // 获取存储空间大小
    @GetMapping("/getstorage")
    public Result getStorage(){
        User user = userUtil.getUser();
        Role role = roleService.roleDao().queryById(user.getRoleid());
        Map<String,Object> map = new HashMap<>();
        map.put("useStorage",user.getUsestoragesize());
        map.put("allStorage",role.getStoragesize());
        return new Result(ResultCode.SUCCESS,map);
    }

    /**
     *
     * 管理员用户操作部分
     * @return
     */

    // 获取每个角色对应的用户列表
    @GetMapping("/all")
    @RequiresPermissions(logical = Logical.AND, value = {"/user/all"})
    public Result getAllUsers() {
        List all = new ArrayList();
        // 获取所有角色
        List<Role> roles = this.roleService.roleDao().queryList(new Role());
        Map<String,Object> map = new HashMap<>();
        map.put("id","");
        map.put("name","全部用户");
        List allRole = new ArrayList();
        for (Role role:roles){
           Map<String,Object> rolemap = new HashMap<>();
           rolemap.put("id",role.getId());
           rolemap.put("name",role.getName());
            // 获取用户信息
            List userByRole = this.userService.getAllUsers(role.getId());
            rolemap.put("children",userByRole);
            allRole.add(rolemap);
        }
        map.put("children",allRole);
        System.out.println(roles);
        all.add(map);
        return new Result(ResultCode.SUCCESS,all);
    }

    // 获取所有用户
    @GetMapping("/queryusers")
    @RequiresPermissions(logical = Logical.AND, value = {"/user/queryusers"})
    public Result queryUsers(@RequestParam(value = "query", required = false) String query,
                             @RequestParam(value = "pagenum", required = false) Integer pagenum,
                             @RequestParam(value = "pagesize", required = false) Integer pagesize) {
        if (query == null){
            query ="";
        }
        if (pagenum == null){
            pagenum =1;
        }
        if (pagesize == null){
            pagesize =10;
        }
        // 获取所有用户
        List allUsers = this.userService.queryUsers(query, pagenum, pagesize);
        // 获取所有的条数
        Long total = this.userService.userDao().getTotal(query);
        Map<String,Object> map = new HashMap<>();
        map.put("users",allUsers);
        map.put("total",total);
        return new Result(ResultCode.SUCCESS,map);
    }

    // 获取用户信息
    @GetMapping("/admin/{id}")
    @RequiresPermissions(logical = Logical.AND, value = {"/user/admin/id"})
    public Result adminGetUserById (@PathVariable("id") String id) {
        if (id == null || id == "") {
            throw new CommonException("获取失败，id不能为空");
        }

        // 获取用户信息
        Map<String,Object> userInfo = this.userService.adminGetUserById(id);

        return new Result(ResultCode.SUCCESS,userInfo);
    }

    // 编辑用户
    @PostMapping("/admin/edit")
    @RequiresPermissions(logical = Logical.AND, value = {"/user/admin/edit"})
    public Result adminEditUser (UserForm user) {

        if (user.getId() == null || user.getId() == "") {
            throw new CommonException("修改用户失败");
        }
        // 查询该用户
        User nowUser = this.userService.userDao().queryById(user.getId());
        if (user.getName() !=null && user.getName() != "") {
            nowUser.setName(user.getName());
        }
        if ("男".equals(user.getSex()) || "女".equals(user.getSex())) {
            nowUser.setSex(user.getSex());
        }
        if (user.getPassword() !=null && user.getPassword() !="") {
            String password = SaltUtil.md5Encrypt(user.getPassword(),nowUser.getSalt());
            nowUser.setPassword(password);
        }
        if (user.getRoleid() != null && user.getRoleid() != "") {
            Role role = this.roleService.roleDao().queryById(user.getRoleid());
            if (role == null) {
                throw new CommonException("分配的角色不存在");
            }
            nowUser.setRoleid(role.getId());
        }
        // 存储更新用户
        System.out.println(nowUser);
        this.userService.userDao().save(nowUser);

        return new Result(ResultCode.SUCCESS);
    }

    // 添加用户
    @PostMapping("/admin/add")
    @RequiresPermissions(logical = Logical.AND, value = {"/user/admin/add"})
    public Result adminAdd (UserForm user) {
        if (user.getAccount() == null || user.getAccount() == ""){
            throw new CommonException("用户账号不能为空");
        }
        if (user.getPassword() == null || user.getPassword() == ""){
            throw new CommonException("密码不能为空");
        }
        // 查询是否存在账号
        User getUser = this.userService.findByAccount(user.getAccount());
        if (getUser != null) {
            throw new CommonException("账号已存在");
        }

        User newUser = new User();
        newUser.setAccount(user.getAccount());
        newUser.setName(user.getName());
        newUser.setSex(user.getSex());
        // 密码加密加盐
        String salt = SaltUtil.getRandomSalt();
        String password = SaltUtil.md5Encrypt(user.getPassword(),salt);
        newUser.setSalt(salt);
        newUser.setPassword(password);
        // 判断角色是否存在
        if(user.getRoleid() != null && user.getRoleid() !=""){
            Role role = this.roleService.roleDao().queryById(user.getRoleid());
            if (role == null) {
                // 获取普通用户id
                Role commonRole = roleService.findByname("普通用户");
                newUser.setRoleid(commonRole.getId());
            }else {
                newUser.setRoleid(role.getId());
            }
        }
        newUser.setStatus("enable");
        newUser.setUsestoragesize((double) 0);
        newUser.setCreatetime(new Date());
        userService.save(newUser);
        return new Result(ResultCode.SUCCESS);
    }

    // 删除用户
    @GetMapping("/admin/delete")
    @RequiresPermissions(logical = Logical.AND, value = {"/user/admin/delete"})
    public Result deleteUser (@RequestParam("id") String id ) {
        if (id == null || id == "" ) {
            throw new CommonException("删除用户失败");
        }
        String[] strArr = id.split(",");
        for (int i = 0; i < strArr.length; ++i) {
            System.out.println(strArr[i]);
            User user = this.userService.userDao().queryById(strArr[i]);
            if (user == null){
                throw new CommonException("用户不存在，删除失败");
            }
        }
        // 删除用户
        for (int i = 0; i < strArr.length; ++i) {
            this.userService.delete(strArr[i]);
        }
        return new Result(ResultCode.SUCCESS);
    }

    // 修改用户状态
    @GetMapping("/admin/changstatus")
    @RequiresPermissions(logical = Logical.AND, value = {"/user/admin/changstatus"})
    public Result changStatus(@RequestParam("id") String id, @RequestParam("status") String status) {

        if (id ==null || id == "" || status == null || status == ""){
            throw new CommonException("用户id/状态不能为空");
        }
        if (!"enable".equals(status) && !"disable".equals(status)){
            throw new CommonException("提交状态异常");
        }
        // 查询用户id
        User user = this.userService.userDao().queryById(id);
        if (user == null) {
            throw new CommonException("用户不存在");
        }
        // 存在则修改用户状态
        user.setStatus(status);
        this.userService.userDao().save(user);
        return new Result(ResultCode.SUCCESS);
    }
}
