package com.eci.security.rbac.service;

import com.eci.security.rbac.common.dataobject.RoleDO;
import com.eci.security.rbac.common.dataobject.UserDO;
import com.eci.security.rbac.common.vo.UserPrincipal;
import com.eci.security.rbac.constant.ServiceErrorCodeEnum;
import com.eci.security.rbac.dao.RoleDAO;
import com.eci.security.rbac.dao.UserDAO;
import com.eci.security.rbac.util.ServiceExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/20.
 */


@Configuration
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    public UserDetails loadUserByUsernameAndAppId(String username, Integer appId) throws UsernameNotFoundException {
        UserDO user = userDAO.selectByNameAndAppid(username, appId);
        if(user == null) {
            throw new UsernameNotFoundException(String.format("用户名: %s, 密码: %s， 未找到用户", username, appId));
        }
        List<RoleDO> roles = roleDAO.getRolesByUserid(user.getId());
        return UserPrincipal.createUser(user, roles);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

}




