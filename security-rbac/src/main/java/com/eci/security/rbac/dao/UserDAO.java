package com.eci.security.rbac.dao;

import com.eci.security.rbac.common.dataobject.UserDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/20.
 */


@Repository
public interface UserDAO {

    UserDO selectByUsername(@Param("username") String username);


}

