package com.eci.security.rbac.util;

import com.eci.security.rbac.common.vo.CommonResult;
import com.eci.security.rbac.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ServiceExceptionUtil {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    private static ConcurrentMap<Integer, String> messages = new ConcurrentHashMap<>();

    public static void putAll(Map<Integer, String> messages) {
        ServiceExceptionUtil.messages.putAll(messages);
    }

    public static void put(Integer code, String message) {
        ServiceExceptionUtil.messages.put(code, message);
    }


    public static <T> CommonResult<T> error(Integer code) {
        return CommonResult.error(code, messages.get(code));
    }

    public static CommonResult error(Integer code, Object... params) {
        String message = doFormat(code, messages.get(code), params);
        return CommonResult.error(code, message);
    }


    public static ServiceException exception(Integer code) {
        return new ServiceException(code, messages.get(code));
    }

    /**
     * 创建指定编号的 ServiceException 的异常
     *
     * @param code 编号
     * @param params 消息提示的占位符对应的参数
     * @return 异常
     */
    public static ServiceException exception(Integer code, Object... params) {
        String message = doFormat(code, messages.get(code), params);
        return new ServiceException(code, message);
    }

    public static ServiceException exception(Integer code, String messagePattern, Object... params) {
        String message = doFormat(code, messagePattern, params);
        return new ServiceException(code, message);
    }

    /**
     * 将错误编号对应的消息使用 params 进行格式化。
     *
     * @param code           错误编号
     * @param messagePattern 消息模版
     * @param params         参数
     * @return 格式化后的提示
     */
    private static String doFormat(int code, String messagePattern, Object... params) {
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
        int i = 0;
        int j;
        int l;
        for (l = 0; l < params.length; l++) {
            j = messagePattern.indexOf("{}", i);
            if (j == -1) {
                log.error("[doFormat][参数过多：错误码({})|错误内容({})|参数({})", code, messagePattern, params);
                if (i == 0) {
                    return messagePattern;
                } else {
                    sbuf.append(messagePattern.substring(i, messagePattern.length()));
                    return sbuf.toString();
                }
            } else {
                sbuf.append(messagePattern.substring(i, j));
                sbuf.append(params[l]);
                i = j + 2;
            }
        }
        if (messagePattern.indexOf("{}", i) != -1) {
            log.error("[doFormat][参数过少：错误码({})|错误内容({})|参数({})", code, messagePattern, params);
        }
        sbuf.append(messagePattern.substring(i, messagePattern.length()));
        return sbuf.toString();
    }

}
