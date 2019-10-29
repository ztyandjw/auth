package com.eci.security.auth.common.dataobject;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/30.
 */
@Data
@Accessors(chain = true)
public class ResourceDO extends BaseDO {
    private Long id;
    private String resourceName;
    private String url;
    private Integer type;
    private String permission;
    private String method;
    private Integer appId;
}

