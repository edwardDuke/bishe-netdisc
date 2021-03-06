package com.bishe.netdisc.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bishe.netdisc.common.exception.CommonException;


import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * jwt校验token和生成token工具
 *
 * @author third_e
 * @create 2020/4/11 0011-下午 3:39
 */
public class JwtUtil {
    /**
     * 过期时间改为从配置文件获取
     *
     * 过期时间3个小时
     */
    private static final long EXPIRE_TIME = 180*60*1000;

    /**
     * 校验token是否正确
     * @param token 密钥
     * @param secret 用户的密码
     * @return 是否正确
     */
    public static boolean verify(String token, String username, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception exception) {
//            throw new CommonException("JWTToken认证解密出现UnsupportedEncodingException异常:" + exception.getMessage());
        }
        return false;
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            System.out.println("获得token中的信息无需secret解密也能获得抛出异常了，返回null");
            return null;
        }
    }

    /**
     * 生成签名,5min后过期
     * @param username 用户名
     * @param secret 用户的密码
     * @return 加密的token
     */
    public static String sign(String username, String secret) {
        try {
            Date date = new Date(System.currentTimeMillis()+EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带username信息
            return JWT.create()
                    .withClaim("username", username)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            throw new CommonException("JWTToken加密出现UnsupportedEncodingException异常:" + e.getMessage());
        }
    }
}
