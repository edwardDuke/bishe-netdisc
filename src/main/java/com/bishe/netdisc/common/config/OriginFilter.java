package com.bishe.netdisc.common.config;

import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全局跨域放开
 * @author third_e
 * @create 2020/4/14 0014-下午 9:27
 */
@Component
public class OriginFilter implements Filter {

    //初始化调用的方法
    //当服务器 被启动的时候，调用
    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    //  拦截方法
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        System.out.println("----------");
        System.out.println("进入跨域filter");
        System.out.println("----------");
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        System.out.println("自己添加的请求头abc:"+httpServletRequest.getHeader("abc"));
        System.out.println(httpServletRequest.getMethod().equals("OPTIONS"));
        //测试添加
//        if (httpServletRequest.getMethod().equals("OPTIONS")) {
//            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
//        }
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        //不需要被拦截的方法，直接放行
        filterChain.doFilter(request, response);
    }

    //销毁时候调用的方法
    @Override
    public void destroy() { }

}
