package com.eci.security.rbac.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/25.
 */



@ConfigurationProperties(prefix = "jwt.config")
@Data
public class JWTConfig {
    /**
     * jwt 加密 key，默认值：testkey.
     */
    private String key = "test123";

    /**
     * jwt 过期时间，默认值：600000，10分钟.
     */
    private Long ttl = 600000L;

    /**
     * 开启 记住我 之后 jwt 过期时间，默认值 604800000，7天
     */
    private Long remember = 604800000L;

    private String clientId = "test001";


}
