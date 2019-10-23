package com.eci.security.rbac.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/29.
 */


@Data
@Accessors(chain = true)
public class Oauth2Token implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType = "bearer";
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private Long expireIn;
    private String scope = "all";
    private String jti;
}


