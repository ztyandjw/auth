package com.eci.security.auth.common.bo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/19.
 */


@Data
@Accessors(chain = true)
public class AuthLoginBO implements Serializable{
    private static final long serialVersionUID = 1L;
    @NotNull(message = "应用id不能为空")
    private Long appId;
    @NotBlank(message = "用户名不能为空")
    @Length(min = 3, message = "用户名不能小于3个字符")
    @Length(max = 10, message = "用户名不能超过10个字符")
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "用户昵称限制：最多20字符，包含文字、字母和数字")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Length(min = 3, message = "密码不能小于3个字符")
    @Length(max = 10, message = "密码不能超过10个字符")
    private String password;
    @NotBlank(message = "providerType不能为空")
    @Pattern(regexp = "local", message = "providerType必须为local")
    private String providerType;
}
