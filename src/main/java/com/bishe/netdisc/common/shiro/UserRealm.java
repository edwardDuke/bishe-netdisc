package com.bishe.netdisc.common.shiro;

import com.bishe.netdisc.common.jwt.JwtToken;
import com.bishe.netdisc.common.utils.JwtUtil;
import com.bishe.netdisc.entity.Role;
import com.bishe.netdisc.entity.User;
import com.bishe.netdisc.service.RoleService;
import com.bishe.netdisc.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;

import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author third_e
 * @create 2020/4/11 0011-下午 3:16
 */
@Service
public class UserRealm extends AuthorizingRealm {
    protected Logger logger = LoggerFactory.getLogger(UserRealm.class);

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 大坑，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof JwtToken;
    }



    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取账号
        String username = JwtUtil.getUsername(principalCollection.toString());
        System.out.println("z走入doGetAuthorizationInfo方法======================");
        System.out.println(username);
        User user = userService.findByAccount(username);
        System.out.println(user);
        // 获取角色对应有的权限
        Role role = roleService.roleDao().queryById(user.getRoleid());
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRole(role.getName());
        Set<String> permission = roleService.listPermission(user.getRoleid());
        System.out.println(permission);
        simpleAuthorizationInfo.addStringPermissions(permission);
        System.out.println("===========================");
        return simpleAuthorizationInfo;
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

        System.out.println(username);
        // 帐号为空
        if (username==null || username =="") {
            throw new AuthenticationException("Token中帐号为空");
        }
        // 查询数据库
        User user = userService.findByAccount(username);
        if (user == null) {
            throw new AuthenticationException("该帐号不存在");
        }
        if (! JwtUtil.verify(token, username, user.getPassword())) {
            throw new AuthenticationException("Token无效");
        }
        return new SimpleAuthenticationInfo(token, token, "userRealm");
    }
}
