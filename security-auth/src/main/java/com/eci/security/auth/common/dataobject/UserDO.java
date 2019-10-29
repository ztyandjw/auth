package com.eci.security.auth.common.dataobject;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/20.
 */
@Data
@Accessors(chain = true)
public class UserDO extends BaseDO{
    private Long id;
    private String username;
    private String password;
    private Integer enable;
    private Integer noExpired;
    private Integer credentialNoExpired;
    private Integer noLock;
    private String enName;
    private String cnName;
    private String telPhone;
    private String emailAddress;

}
