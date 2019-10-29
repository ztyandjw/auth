package com.eci.security.auth.dao;

import com.eci.security.auth.common.dataobject.UserDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/20.
 */


@Repository
public interface UserDAO {

    UserDO selectByUsername(@Param("username") String username);


}

