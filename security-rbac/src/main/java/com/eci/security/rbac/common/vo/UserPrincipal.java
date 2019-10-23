package com.eci.security.rbac.common.vo;

import com.eci.security.rbac.common.dataobject.ResourceDO;
import com.eci.security.rbac.constant.Consts;
import com.eci.security.rbac.common.dataobject.RoleDO;
import com.eci.security.rbac.common.dataobject.UserDO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    private String appName;

    private Integer enabled;

    private Integer noExpired;

    private Integer credentialNoExpired;

    private Integer noLock;

    private String cnName;

    private String enName;

    private String phoneNumber;

    private String emailAddress;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户角色列表
     */
    private List<String> roles;

    /**
     * 用户权限列表
     */
    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal createUser(UserDO user, List<RoleDO> roles, List<ResourceDO> resources, String appName) {
        List<String> roleNames = roles.stream().map(RoleDO::getRoleName).collect(Collectors.toList());
        List<GrantedAuthority> authorities = resources.stream()
                .filter(resource -> StringUtils.isNotBlank(resource.getPermission()))
                .map(resource -> new SimpleGrantedAuthority(resource.getPermission()))
                .collect(Collectors.toList());
        return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword(), appName, user.getEnable(), user.getNoExpired(), user.getCredentialNoExpired(), user.getNoLock(), user.getCnName()
                ,user.getEnName(), user.getTelPhone(), user.getEmailAddress(), user.getCreateTime(), user.getUpdateTime(), roleNames, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return Objects.equals(noExpired, Consts.User.NO_EXPIRED);
    }

    @Override
    public boolean isAccountNonLocked() {
        return Objects.equals(noLock, Consts.User.NO_LOCK);

    }

    @Override
    public boolean isCredentialsNonExpired() {
        return Objects.equals(credentialNoExpired, Consts.User.CREDENTIAL_NO_EXPIRED);
    }

    @Override
    public boolean isEnabled() {
        return Objects.equals(enabled, Consts.User.ENABLED);
    }
}