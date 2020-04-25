package com.bishe.netdisc.controller;

import com.bishe.netdisc.common.entity.Result;
import com.bishe.netdisc.common.entity.ResultCode;
import com.bishe.netdisc.common.exception.CommonException;
import com.bishe.netdisc.common.utils.JwtUtil;
import com.bishe.netdisc.common.utils.SaltUtil;
import com.bishe.netdisc.common.utils.UserUtil;
import com.bishe.netdisc.entity.Role;
import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.entity.common.UserForm;
import com.bishe.netdisc.service.RoleService;
import com.bishe.netdisc.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    public Result editUser(User nowuser) {
        System.out.println(nowuser);
        if (nowuser == null || nowuser.getId() == null){
            throw new CommonException("修改失败");
        }
        User oldUser = userUtil.getUser();
        if (oldUser == null){
            throw new CommonException("修改失败");
        }
        System.out.println(oldUser);
        userService.editUser(oldUser,nowuser);
        return new Result(200,"修改用户成功!",true);
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
}
