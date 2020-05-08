package com.bishe.netdisc.common.jwt;

import com.bishe.netdisc.common.entity.Result;
import com.bishe.netdisc.common.exception.CommonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

/**
 * @author third_e
 * @create 2020/4/11 0011-下午 5:15
 */
public class JwtFilter extends BasicHttpAuthenticationFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());



    /**
     * 这里我们详细说明下为什么最终返回的都是true，即允许访问
     * 例如我们提供一个地址 GET /article
     * 登入用户和游客看到的内容是不同的
     * 如果在这里返回了false，请求会被直接拦截，用户看不到任何东西
     * 所以我们在这里返回true，Controller中可以通过 subject.isAuthenticated() 来判断用户是否登入
     * 如果有些资源只有登入用户才能访问，我们只需要在方法上面加上 @RequiresAuthentication 注解即可
     * 但是这样做有一个缺点，就是不能够对GET,POST等请求进行分别过滤鉴权(因为我们重写了官方的方法)，但实际上对应用影响不大
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        System.out.println("JwtFilel类中的isAccessAllowed方法");
        // 查看当前Header中是否携带Authorization属性(Token)，有的话就进行登录认证授权
//        System.out.println(this.isLoginAttempt(request, response));
        if (this.isLoginAttempt(request, response)) {
            try {
                // 进行Shiro的登录UserRealm
                executeLogin(request, response);
            } catch (Exception e) {
                // 认证出现异常，传递错误信息msg
                String msg = e.getMessage();
                // Token认证失败直接返回Response信息
                response401(response, msg);
//                return false;
            }
            return true;
        }
        else {
            // 没有携带Token
            HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
            // 获取当前请求类型
            String httpMethod = httpServletRequest.getMethod();
            // 获取当前请求URI
            String requestURI = httpServletRequest.getRequestURI();
            logger.info("当前请求 {} Authorization属性(Token)为空 请求类型 {}", requestURI, httpMethod);
            // mustLoginFlag = true 开启任何请求必须登录才可访问
            final Boolean mustLoginFlag = true;
            if (mustLoginFlag) {
//                throw new CommonException("请先登录");
                this.response401(response, "请先登录");
//                return false;
            }
            return true;
        }

    }


    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Authorization字段,有就进行Token登录认证授权
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;

        System.out.println("当前Authorization：" + " "+this.getAuthzHeader(request));
        // 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
        String token = this.getAuthzHeader(request);
        return token != null;
    }

    /**
     *进行AccessToken登录认证授权
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {

        // 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
        JwtToken token = new JwtToken(this.getAuthzHeader(request));
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(token);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }


    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpResponse.setHeader("Access-control-Allow-Origin", httpRequest.getHeader("Origin"));
            httpResponse.setHeader("Access-Control-Allow-Methods", httpRequest.getMethod());
            httpResponse.setHeader("Access-Control-Allow-Headers", httpRequest.getHeader("Access-Control-Request-Headers"));
            httpResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**
     * 将非法请求跳转到 /401
     */
    private void response401( ServletResponse resp, String msg) {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(resp);
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        OutputStream out = null;
        try {
            out = httpServletResponse.getOutputStream();
            String data = new ObjectMapper().writeValueAsString(new Result(201, "无权访问(Unauthorized):" + msg, false));
            out.write(data.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            logger.error("直接返回Response信息出现IOException异常:{}", e.getMessage());
            throw new CommonException("直接返回Response信息出现IOException异常:" + e.getMessage());
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 这里我们详细说明下为什么重写
     * 可以对比父类方法，只是将executeLogin方法调用去除了
     * 如果没有去除将会循环调用doGetAuthenticationInfo方法
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        this.sendChallenge(request, response);
        return false;
    }
    /**
     * 此处为AccessToken刷新，未过期就返回新的AccessToken且继续正常访问
     */
//    private boolean refreshToken(ServletRequest request, ServletResponse response) {
//        // 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
//        String token = this.getAuthzHeader(request);
//        // 获取当前Token的帐号信息
//        String account = JwtUtil.getUsername(token);
//        // 判断Redis中RefreshToken是否存在
//        if (JedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account)) {
//            // Redis中RefreshToken还存在，获取RefreshToken的时间戳
//            String currentTimeMillisRedis = JedisUtil.getObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account).toString();
//            // 获取当前AccessToken中的时间戳，与RefreshToken的时间戳对比，如果当前时间戳一致，进行AccessToken刷新
//            if (JwtUtil.getClaim(token, Constant.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
//                // 获取当前最新时间戳
//                String currentTimeMillis = String.valueOf(System.currentTimeMillis());
//                // 读取配置文件，获取refreshTokenExpireTime属性
//                PropertiesUtil.readProperties("config.properties");
//                String refreshTokenExpireTime = PropertiesUtil.getProperty("refreshTokenExpireTime");
//                // 设置RefreshToken中的时间戳为当前最新时间戳，且刷新过期时间重新为30分钟过期(配置文件可配置refreshTokenExpireTime属性)
//                JedisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account, currentTimeMillis, Integer.parseInt(refreshTokenExpireTime));
//                // 刷新AccessToken，设置时间戳为当前最新时间戳
//                token = JwtUtil.sign(account, currentTimeMillis);
//                // 将新刷新的AccessToken再次进行Shiro的登录
//                JwtToken jwtToken = new JwtToken(token);
//                // 提交给UserRealm进行认证，如果错误他会抛出异常并被捕获，如果没有抛出异常则代表登入成功，返回true
//                this.getSubject(request, response).login(jwtToken);
//                // 最后将刷新的AccessToken存放在Response的Header中的Authorization字段返回
//                HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
//                httpServletResponse.setHeader("Authorization", token);
//                httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
//                return true;
//            }
//        }
//        return false;
//    }
}
