package com.eci.security.auth.dao;

import com.eci.security.auth.common.dataobject.AppDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/11.
 */
@Repository
public interface AppDAO {

    AppDO selectByAppid(@Param("appid") Long appId);
}
