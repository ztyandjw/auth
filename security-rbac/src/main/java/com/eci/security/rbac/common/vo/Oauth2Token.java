package com.eci.security.rbac.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/29.
 */


@Data
@Accessors(chain = true)
public class Oauth2Token implements Serializable{
    private static final long serialVersionUID = 1L;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType = "bearer";
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonIgnore
    private LocalDateTime expireIn;
    private String scope = "all";
    private String jti;

    @JsonProperty("expires_in")
    public Long getExpiresIn() {
        ZoneId myZone = ZoneId.of("Asia/Shanghai");
        LocalDateTime now = LocalDateTime.now(myZone);
        return expireIn != null ?
        ChronoUnit.SECONDS.between(now, expireIn) : 0;
    }

//    protected void setExpiresIn(int delta) {
//        setExpiration(new Date(System.currentTimeMillis() + delta));
//    }
//
//
//    public Date getExpiration() {
//        return expiration;
//    }

    /**
     * The instant the token expires.
     *
     * @param expiration The instant the token expires.
     */
    public void setExpiration(LocalDateTime expiration) {
        this.expireIn = expiration;
}


    public static void main(String[] args) {
        System.out.println(new Date(System.currentTimeMillis() + (60 * 1000L)));
    }
}
