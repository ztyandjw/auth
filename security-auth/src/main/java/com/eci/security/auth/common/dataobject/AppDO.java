package com.eci.security.auth.common.dataobject;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/11.
 */
@Data
@Accessors(chain = true)
public class AppDO extends  BaseDO{

    private Long id;
    private String appName;
}
