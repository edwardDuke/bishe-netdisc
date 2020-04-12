package com.bishe.netdisc.controller;

import com.bishe.netdisc.common.entity.Result;
import com.bishe.netdisc.common.entity.ResultCode;
import com.bishe.netdisc.common.utils.JwtUtil;
import com.bishe.netdisc.common.utils.SaltUtil;
import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.mapper.userDao;
import com.bishe.netdisc.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author third_e
 * @create 2020/4/10 0010-下午 9:34
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private userDao userDao;

    //测试
    @RequestMapping("/test")
    public String test(){
        User user = userService.findByAccount("admin");
        return user.toString();
    }

    // 登录用户认证状态
    @RequestMapping("/article")
    public Result article() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return new Result(ResultCode.SUCCESS);
        } else {
            return new Result(ResultCode.FAIL);
        }
    }

    // 用户登录
    @RequestMapping(value = "/login")
    public Result login(@RequestParam String username, @RequestParam String password) {
        System.out.println(SaltUtil.getRandomSalt());
        System.out.println(SaltUtil.md5Encrypt("123456","abcde"));
        User userBean = userService.findByAccount(username);
        if (userBean.getPassword().equals(password)) {
//            JwtUtil.sign(username, password);
            return new Result(ResultCode.SUCCESS, JwtUtil.sign(username, password));
        } else {
            return new Result(ResultCode.FAIL);
        }
//        try {
//            //密码加密 shiro md5加密。参数一密码，参数二盐，参数三加密次数
//
//            password = new Md2Hash(password,"abcde",3).toString();
//
//            //构造登录令牌
//            UsernamePasswordToken upToken = new UsernamePasswordToken(username, password);
//            // 获取subject
//            Subject subject = SecurityUtils.getSubject();
//            subject.login(upToken);
//            return "登录成功";
//
//        }catch (Exception e){
//            return "登录失败";
//        }
    }
}
