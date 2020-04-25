package com.bishe.netdisc.common.utils;

import com.bishe.netdisc.common.exception.CommonException;
import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.mapper.UserDao;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author third_e
 * @create 2020/4/17 0017-下午 5:55
 */
@Component
public class UserUtil {
    @Autowired
    private UserDao userDao;

    /**
     * 获取当前用户信息
     * @return
     */
    public User getUser() {
        String token = SecurityUtils.getSubject().getPrincipal().toString();
        // 解密获得Account
        String account = JwtUtil.getUsername(token);
        //获取用户信息
        User user = userDao.findByAccount(account);
        // 用户是否存在
        if (user == null) {
            throw new CommonException("该帐号不存在(The account does not exist.)");
        }
        return user;
    }

    /**
     * 获取当前用户id
     * @return
     */
    public String getUserId() {
        return getUser().getId();
    }

    /**
     * 获取用户token
     * @return
     */
    public String getToken() {
        return SecurityUtils.getSubject().getPrincipal().toString();
    }
}
