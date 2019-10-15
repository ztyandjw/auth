package com.eci.security.rbac.dao;

import com.eci.security.rbac.common.dataobject.AppDO;
import com.eci.security.rbac.common.dataobject.UserDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/11.
 */
@Repository
public interface AppDAO {

    AppDO selectByAppid(@Param("appid") Long appId);
}
