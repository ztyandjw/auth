package com.eci.security.auth.common.dataobject;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/20.
 */
@Data
@Accessors(chain = true)
public class RoleDO extends BaseDO {

    private Long id;
    private String roleName;
    private Long appId;
}
