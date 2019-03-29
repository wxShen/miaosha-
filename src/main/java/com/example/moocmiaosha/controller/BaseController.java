package com.example.moocmiaosha.controller;

import com.example.moocmiaosha.error.BusinessException;
import com.example.moocmiaosha.error.EmBusinessError;
import com.example.moocmiaosha.reeponse.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
    public static final String CONTENT_TYPE_FROMED = "application/x-www-form-urlencoded";

    //对于spring 在controller阶段无法捕获的异常的统一处理

    //定义exceptionhandler解决未被controller层吸收的exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request,Exception exception){
        Map<String,Object> responseData = new HashMap<>();
        if (exception instanceof BusinessException){
            BusinessException businessException = (BusinessException) exception;
            responseData.put("errCode",businessException.getErrCode());
            responseData.put("errMsg",businessException.getErrMsg());
        }else{
            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg",EmBusinessError.UNKNOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.create(responseData,"fail");
    }

}
