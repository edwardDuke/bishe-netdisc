package com.bishe.netdisc.common.exception;

import com.bishe.netdisc.common.entity.Result;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author third_e
 * @create 2020/4/14 0014-下午 6:07
 */
@RestControllerAdvice
public class OriginException {
    // 捕捉shiro的异常
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public Result handle401(ShiroException e) {
        System.out.println("捕捉shiro的异常");
        return new Result(401, e.getMessage(), false);
    }

    /**
     * 单独捕捉Shiro(UnauthenticatedException)异常
     * 该异常为以游客身份访问有权限管控的请求无法对匿名主体进行授权，而授权失败所抛出的异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthenticatedException.class)
    public Result handle401(UnauthenticatedException e) {
        System.out.println("单独捕捉Shiro(UnauthenticatedException)异常"+e);
        return new Result(HttpStatus.UNAUTHORIZED.value(), "无权访问(Unauthorized):当前Subject是匿名Subject，请先登录(This subject is anonymous.)", false);
    }


    // 捕捉CommonException
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(CommonException.class)
    public Result handle401(CommonException e) {
        System.out.println("捕捉CommonException,错误信息"+e.getMessage());
        return new Result(401, e.getMessage(), false);
    }

    // 捕捉其他所有异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public Result globalException(HttpServletRequest request, Throwable ex) {
        System.out.println("捕捉其他所有异常,错误信息"+ex.getMessage()+getStatus(request).value());
        return new Result(getStatus(request).value(), ex.getMessage(), false);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        System.out.println("全局异常类getStatus方法");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
