package com.eci.security.rbac.common.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/14.
 */
@Data
@Accessors(chain = true)
public class AuthRefreshTokenBO implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotBlank(message = "refreshToken不能为空")
    private String refreshToken;

}
