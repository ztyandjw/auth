package com.eci.security.rbac.exception.handler;


import com.eci.security.rbac.common.vo.CommonResult;
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


    //    @ExceptionHandler(value = Exception.class)
//    @ResponseBody
//    public CommonResult handlerException(Exception e) {
//        if (e instanceof NoHandlerFoundException) {
//            log.error("【全局异常拦截】NoHandlerFoundException: 请求方法 {}, 请求路径 {}", ((NoHandlerFoundException) e).getRequestURL(), ((NoHandlerFoundException) e).getHttpMethod());
//            return ApiResponse.ofStatus(Status.REQUEST_NOT_FOUND);
//        } else if (e instanceof HttpRequestMethodNotSupportedException) {
//            log.error("【全局异常拦截】HttpRequestMethodNotSupportedException: 当前请求方式 {}, 支持请求方式 {}", ((HttpRequestMethodNotSupportedException) e).getMethod(), JSONUtil.toJsonStr(((HttpRequestMethodNotSupportedException) e).getSupportedHttpMethods()));
//            return ApiResponse.ofStatus(Status.HTTP_BAD_METHOD);
//        } else if (e instanceof MethodArgumentNotValidException) {
//            log.error("【全局异常拦截】MethodArgumentNotValidException", e);
//            return ApiResponse.of(Status.BAD_REQUEST.getCode(), ((MethodArgumentNotValidException) e).getBindingResult()
//                    .getAllErrors()
//                    .get(0)
//                    .getDefaultMessage(), null);
//        } else if (e instanceof ConstraintViolationException) {
//            log.error("【全局异常拦截】ConstraintViolationException", e);
//            return ApiResponse.of(Status.BAD_REQUEST.getCode(), CollUtil.getFirst(((ConstraintViolationException) e).getConstraintViolations())
//                    .getMessage(), null);
//        } else if (e instanceof MethodArgumentTypeMismatchException) {
//            log.error("【全局异常拦截】MethodArgumentTypeMismatchException: 参数名 {}, 异常信息 {}", ((MethodArgumentTypeMismatchException) e).getName(), ((MethodArgumentTypeMismatchException) e).getMessage());
//            return ApiResponse.ofStatus(Status.PARAM_NOT_MATCH);
//        } else if (e instanceof HttpMessageNotReadableException) {
//            log.error("【全局异常拦截】HttpMessageNotReadableException: 错误信息 {}", ((HttpMessageNotReadableException) e).getMessage());
//            return ApiResponse.ofStatus(Status.PARAM_NOT_NULL);
//        } else if (e instanceof BadCredentialsException) {
//            log.error("【全局异常拦截】BadCredentialsException: 错误信息 {}", e.getMessage());
//            return ApiResponse.ofStatus(Status.USERNAME_PASSWORD_ERROR);
//        } else if (e instanceof DisabledException) {
//            log.error("【全局异常拦截】BadCredentialsException: 错误信息 {}", e.getMessage());
//            return ApiResponse.ofStatus(Status.USER_DISABLED);
//        } else if (e instanceof BaseException) {
//            log.error("【全局异常拦截】DataManagerException: 状态码 {}, 异常信息 {}", ((BaseException) e).getCode(), e.getMessage());
//            return ApiResponse.ofException((BaseException) e);
//        }
//
//        log.error("【全局异常拦截】: 异常信息 {} ", e.getMessage());
//        e.printStackTrace();
//        return ApiResponse.ofStatus(Status.ERROR);
//    }
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("【全局异常拦截】MethodArgumentNotValidException: 错误信息 {}", e.getMessage());
        String msg = StringUtils.join(e.getBindingResult().getAllErrors().stream().map((x) -> x.getDefaultMessage()).collect(Collectors.toList()), ",");
        return CommonResult.error(400, msg);
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object handlerException(Exception e) {
        if(e instanceof BadCredentialsException) {
            logger.error("【全局异常拦截】BadCredentialsException: 错误信息 {}", e.getMessage());
            logger.error("【错误堆栈信息】:", e);
            return CommonResult.error(500, e.getMessage());
        }
        logger.error("【全局异常拦截】: 异常信息 {} ", e.getMessage());
        logger.error("【错误堆栈信息】:", e);
        return CommonResult.error(500, e.getMessage());
    }
}
