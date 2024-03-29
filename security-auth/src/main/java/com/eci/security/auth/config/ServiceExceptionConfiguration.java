package com.eci.security.auth.config;

import com.eci.security.auth.constant.ErrorCoceEnum;
import com.eci.security.auth.util.ServiceExceptionUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/25.
 */
@Configuration
public class ServiceExceptionConfiguration {

    @EventListener(ApplicationReadyEvent.class)
    public void initMessages() {
        for (ErrorCoceEnum item : ErrorCoceEnum.values()) {
            ServiceExceptionUtil.put(item.getCode(), item.getMessage());
        }
    }
}
