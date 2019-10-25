package com.eci.security.rbac.core.provider;

import com.eci.security.rbac.common.dataobject.AppDO;
import com.eci.security.rbac.common.dataobject.ResourceDO;
import com.eci.security.rbac.common.dataobject.RoleDO;
import com.eci.security.rbac.common.dataobject.UserDO;
import com.eci.security.rbac.common.vo.UserPrincipal;
import com.eci.security.rbac.dao.AppDAO;
import com.eci.security.rbac.dao.ResourceDAO;
import com.eci.security.rbac.dao.RoleDAO;
import com.eci.security.rbac.dao.UserDAO;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/20.
 */


@Configuration
public class LocalUserDetailService implements UserDetailsService {
    
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private ResourceDAO resourceDAO;

    public UserDetails loadUserByUsernameAndAppId(String username, Long appId) throws UsernameNotFoundException {
        UserDO user = userDAO.selectByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(String.format("用户名: %s， 未找到用户", username));
        }
        List<RoleDO> roles = roleDAO.getRolesByUseridAndAppid(user.getId(), appId);
        List<ResourceDO> resources = Lists.newArrayList();
        if(roles.size() > 0) {
            resources =  resourceDAO.getResourceByRoleIds(roles.stream().map(RoleDO :: getId).collect(Collectors.toList()));
        }
        return UserPrincipal.createUser(user, roles, resources, appId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }


}




