package com.bishe.netdisc.common.jwt;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author third_e
 * @create 2020/4/11 0011-下午 2:56
 */
public class JwtToken implements AuthenticationToken {
    /**
     * Token，密钥
     */
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
