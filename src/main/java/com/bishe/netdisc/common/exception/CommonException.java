package com.bishe.netdisc.common.exception;


import com.bishe.netdisc.common.entity.ResultCode;
import lombok.Getter;

/**
 * 自定义异常
 */

public class CommonException extends RuntimeException{

    public CommonException(String msg) {
        super(msg);
    }

    public CommonException(){
        super();
    }
}
