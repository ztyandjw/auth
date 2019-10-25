package com.eci.security.rbac.exception.handler;


import com.eci.security.rbac.common.vo.CommonResult;
import com.eci.security.rbac.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("【全局异常拦截】MethodArgumentNotValidException: 错误信息 {}", e.getMessage());
        String msg = StringUtils.join(e.getBindingResult().getAllErrors().stream().map((x) -> x.getDefaultMessage()).collect(Collectors.toList()), ",");
        return CommonResult.error(400, msg);
    }

    @ResponseBody
    @ExceptionHandler(value = ServiceException.class)
    public Object handlerException(ServiceException e) {

        logger.error("【全局异常拦截】: ServiceException: 异常信息 {} ", e.getMessage());
        logger.error("【错误堆栈信息】:", e);
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object handlerException(Exception e) {

        logger.error("【全局异常拦截】: 未捕获异常: 异常信息 {} ", e.getMessage());
        logger.error("【错误堆栈信息】:", e);
        return CommonResult.error(500, e.getMessage());
    }
}
