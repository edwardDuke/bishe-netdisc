package com.bishe.netdisc.common.shiro;

import com.bishe.netdisc.common.shiro.jwt.JwtToken;
import com.bishe.netdisc.common.utils.JwtUtil;
import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;

import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author third_e
 * @create 2020/4/11 0011-下午 3:16
 */
@Service
public class UserRealm extends AuthorizingRealm {
    protected Logger logger = LoggerFactory.getLogger(UserRealm.class);

    @Autowired
    UserService userService;

    /**
     * 大坑，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof JwtToken;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获取token
        String token = (String) authenticationToken.getCredentials();
        //获取用户账号
        String username = JwtUtil.getUsername(token);
        // 帐号为空
        if (username.isEmpty()) {
            throw new AuthenticationException("Token中帐号为空(The account in Token is empty.)");
        }
        // 查询数据库
        User user = userService.findByAccount(username);
        if (user == null) {
            throw new AuthenticationException("该帐号不存在(The account does not exist.)");
        }
        if (! JwtUtil.verify(token, username, user.getPassword())) {
            throw new AuthenticationException("Username or password error");
        }
        return new SimpleAuthenticationInfo(token, token, "userRealm");
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        return null;
    }
}
